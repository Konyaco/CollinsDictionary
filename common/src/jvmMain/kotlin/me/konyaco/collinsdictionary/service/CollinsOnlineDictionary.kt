package me.konyaco.collinsdictionary.service

import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import java.net.URL
import javax.net.ssl.HttpsURLConnection

actual class CollinsOnlineDictionary : CollinsDictionary {
    override fun getDefinition(word: String): Word? {
        return CollinsDictionaryHTMLParser.parse(getHtml(word))
    }

    private fun getHtml(word: String): String {
        val url = URL("https://www.collinsdictionary.com/dictionary/english/$word")

        val conn = (url.openConnection() as HttpsURLConnection).apply {
            instanceFollowRedirects = false
            connect()
        }
        return conn.inputStream.bufferedReader().readText()
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
        val cobuildElement =
            mainContentElement.getElementsByClass("dictionary Cob_Adv_Brit dictentry").firstOrNull()

        return if (cobuildElement != null) {
            CobuildDictionary(listOf(parseSection(cobuildElement)))
        } else {
            val dictionaries =
                mainContentElement.getElementsByClass("dictionary Cob_Adv_Brit").firstOrNull()
                    ?: error("Cannot find COBUILD dictionary element")
            // Some words (like "take") have multiple section for different usage.
            val sectionElements = dictionaries.getElementsByClass("dictentry dictlink")
            val sections = sectionElements.map { parseSection(it) }
            return CobuildDictionary(sections)
        }
    }

    fun parseSection(dictionaryElement: Element): CobuildDictionarySection {
        val wordName = WordNameParser().parse(dictionaryElement)
        val wordForms: List<WordForm>? = WordFormParser().parse(dictionaryElement)
        val pronunciation = PronunciationParser().parse(dictionaryElement)
        val definitionEntries = DefinitionParser().parse(dictionaryElement)

        return CobuildDictionarySection(
            word = wordName,
            forms = wordForms,
            pronunciation = pronunciation,
            definitionEntries = definitionEntries
        )
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

        val descriptions = formElement.getElementsByClass("lbl type-gram").map { it.text() }
        val spells = formElement.getElementsByClass("orth").map { it.text() }

        return descriptions.zip(spells) { desc, spell ->
            WordForm(desc, spell)
        }
    }
}

private class PronunciationParser {
    fun parse(dictionaryElement: Element): Pronunciation {
        val pron = dictionaryElement.getElementsByClass("pron type-").firstOrNull()?.text()
            ?: error("Cannot find word pronunciation")
        // TODO: 2021/7/30 Parse sound url
        return Pronunciation(pron, null)
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

            val senseElement = element.getElementsByClass("sense").first()
            val defElement = senseElement.getElementsByClass("def").first()
            val def = defElement.text()

            val examples = senseElement.getElementsByClass("cit type-example").map {
                val sentence = it.getElementsByClass("quote").first().text()
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