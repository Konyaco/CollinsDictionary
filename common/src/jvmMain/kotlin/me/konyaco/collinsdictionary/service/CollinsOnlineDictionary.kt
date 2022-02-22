package me.konyaco.collinsdictionary.service

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.features.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import java.util.*

actual class CollinsOnlineDictionary : CollinsDictionary {
    private val client = HttpClient(CIO) { followRedirects = false }
    private val clientFollowRedirect = HttpClient(CIO)

    companion object {
        private const val SEARCH_URL = "https://www.collinsdictionary.com/search"
        private const val SPELL_CHECK_URL = "https://www.collinsdictionary.com/spellcheck/english"
        private const val DICTIONARY_URL = "https://www.collinsdictionary.com/dictionary/english"
        private fun buildSearchURL(word: String) = "$SEARCH_URL/?dictCode=english&q=$word"
        private fun buildDictionaryURL(word: String) = "$DICTIONARY_URL/$word"
    }

    override suspend fun getDefinition(word: String): Word? {
        return CollinsDictionaryHTMLParser.parse(getHtml(word))
    }

    override suspend fun search(word: String): SearchResult {
        val response = try {
            client.get<HttpResponse>(buildSearchURL(word))
        } catch (e: RedirectResponseException) {
            e.response
        }

        if (response.status == HttpStatusCode.Found) {
            val redirectedUrl =
                response.headers[HttpHeaders.Location] ?: error("Redirect to header was not found.")

            return if (isPrecise(redirectedUrl)) {
                val redirectWord = getRedirectedWord(redirectedUrl)
                if (redirectWord == word) {
                    SearchResult.PreciseWord(redirectWord)
                } else {
                    SearchResult.Redirect(redirectWord)
                }
            } else {
                val html = client.get<String>(redirectedUrl) // Get response in [spellcheck]
                val list = CollinsSpellCheckParser.parseWordList(html)  // Parse result list
                SearchResult.NotFound(list)
            }
        } else {
            error("Response code is: ${response.status}")
        }
    }

    /**
     * If the search result redirects to a precise page. Or to a spellcheck page.
     */
    private fun isPrecise(redirectedUrl: String): Boolean {
        return when {
            redirectedUrl.startsWith(DICTIONARY_URL) -> true
            redirectedUrl.startsWith(SPELL_CHECK_URL) -> false
            else -> error("Could not search word.")
        }
    }

    private fun getRedirectedWord(redirectedUrl: String): String =
        redirectedUrl.substringAfterLast("/")

    private suspend fun getHtml(word: String): String {
        // Some words may be redirected to another (like "out" -> "out_1")
        return clientFollowRedirect.get<String>(buildDictionaryURL(word))
    }
}

private object CollinsSpellCheckParser {
    fun parseWordList(html: String): List<String> {
        val jsoup = Jsoup.parse(html)
        val mainContentElement = jsoup.getElementById("main_content")
            ?: error("Could not parse word list: main_content not found.")
        val column = mainContentElement.getElementsByClass("columns2")
            .firstOrNull() ?: error("Could not parse word list: columns2 not found.")
        val result = column.children().map {
            it.getElementsByTag("a").firstOrNull()?.text() ?: error("Could not get entry")
        }
        return result
    }
}

private object CollinsDictionaryHTMLParser {
    fun parse(html: String): Word? {
        val jsoup = Jsoup.parse(html)
        val mainContentElement =
            jsoup.getElementById("main_content") ?: return null // Word not found

        return Word(parseCobuildDictionary(mainContentElement))
    }

    fun parseCobuildDictionary(mainContentElement: Element): CobuildDictionary {
        return run {
            val cobuildElement =
                mainContentElement.getElementsByClass("dictionary Cob_Adv_Brit dictentry")
                    .firstOrNull()
            cobuildElement?.let {
                CobuildDictionary(listOf(parseSection(it)))
            }
        } ?: run {
            val dictionaries =
                mainContentElement.getElementsByClass("dictionary Cob_Adv_Brit").firstOrNull()
                    ?: error("Cannot find COBUILD dictionary element")
            // Some words (like "take") have multiple section for different usage.
            val sectionElements = dictionaries.getElementsByClass("dictentry dictlink")
            val sections = sectionElements.map { parseSection(it) }
            CobuildDictionary(sections)
        }
    }

    fun parseSection(dictionaryElement: Element): CobuildDictionarySection {
        val wordName = WordNameParser().parse(dictionaryElement)
        val wordFrequency = WordFrequencyParser().parse(dictionaryElement)
        val wordForms: List<WordForm>? = WordFormParser().parse(dictionaryElement)
        val pronunciation = PronunciationParser().parse(dictionaryElement)
        val definitionEntries = DefinitionParser().parse(dictionaryElement)

        return CobuildDictionarySection(
            word = wordName,
            frequency = wordFrequency,
            forms = wordForms,
            pronunciation = pronunciation,
            definitionEntries = definitionEntries
        )
    }
}

private class WordFrequencyParser {
    fun parse(dictionaryElement: Element): Int? {
        return dictionaryElement.getElementsByClass("word-frequency-img")
            .firstOrNull()
            ?.attributes()
            ?.get("data-band")
            ?.toInt()
    }
}

private class WordNameParser {
    fun parse(dictionaryElement: Element): String {
        return dictionaryElement.getElementsByClass("title_container")
            .firstOrNull()
            ?.getElementsByTag("h2")
            ?.lastOrNull()
            ?.getElementsByTag("span")
            ?.firstOrNull()
            ?.text()
            ?: error("Cannot find word name")
    }
}

private class WordFormParser {
    fun parse(dictionaryElement: Element): List<WordForm>? {
        val formElement = dictionaryElement.getElementsByClass("form inflected_forms type-infl")
            .firstOrNull() ?: return null
        val result = mutableListOf<WordForm>()
        val types = LinkedList<String>()
        for (e in formElement.children()) {
            if (e.classNames().contains("type-gram")) {
                val type = e.ownText()
                // Grammar type
                types.push(type)
            } else if (e.classNames().contains("orth")) {
                // Spell
                val spell = e.ownText()
                types.forEach { type ->
                    result.add(WordForm(type, spell))
                }
                types.clear()
            }
        }
        return result
    }
}

private class PronunciationParser {
    fun parse(dictionaryElement: Element): Pronunciation {
        val pronElement = dictionaryElement.getElementsByClass("pron type-").firstOrNull()
            ?: error("Cannot find word pronunciation")
        val pronStr = pronElement.text()
        val soundElement = pronElement.getElementsByAttribute("data-src-mp3").firstOrNull()
            ?: error("Cannot find sound element")
        val sound = soundElement.attributes()["data-src-mp3"] ?: error("Cannot find sound url")
        return Pronunciation(pronStr, sound)
    }
}

private class DefinitionParser {
    fun parse(dictionaryElement: Element): List<DefinitionEntry> {
        val definitionEntries = mutableListOf<DefinitionEntry>()

        val definitionElement =
            dictionaryElement.getElementsByClass("content definitions cobuild br ").firstOrNull()
                ?: error("Cannot find word definitions")

        definitionElement.getElementsByClass("hom").forEachIndexed { index, element ->
            val grammarGroup: String =
                element.getElementsByClass("gramGrp pos").firstOrNull()?.text()
                    ?: element.getElementsByClass("gramGrp").firstOrNull()
                        ?.getElementsByClass("pos")?.text()
                    ?: return@forEachIndexed // Maybe it's not a definition, just skip
//                    ?: error("Cannot find grammar group in entry $index")

            val senseElement = element.getElementsByClass("sense").first()!!
            val defElement = senseElement.getElementsByClass("def").first()!!
            val def = defElement.text()

            val examples = senseElement.getElementsByClass("cit type-example").map {
                val sentence = it.getElementsByClass("quote").first()!!.text()
                ExampleSentence(
                    sentence,
                    null,
                    null,
                    emptyList()
                ) // TODO: 2021/7/28 Grammar pattern and sound url.
            }

            definitionEntries.add(
                DefinitionEntry(
                    index = index + 1,
                    type = grammarGroup,
                    definition = Definition(
                        def = def,
                        examples = examples
                    ),
                    extraDefinitions = emptyList() // TODO: 2021/7/28 Extra definitions.
                )
            )
        }

        return definitionEntries
    }
}