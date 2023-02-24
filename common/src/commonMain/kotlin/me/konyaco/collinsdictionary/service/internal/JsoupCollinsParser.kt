package me.konyaco.collinsdictionary.service.internal

import me.konyaco.collinsdictionary.service.*
import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import java.util.*

internal class JsoupCollinsParser {
    fun parseAlternatives(alternativeHtml: String): List<String> {
        val jsoup = Jsoup.parse(alternativeHtml)
        val mainContentElement = jsoup.getElementById("main_content")
            ?: error("Could not parse word list: main_content not found.")
        val column = mainContentElement.getElementsByClass("columns2")
            .firstOrNull() ?: error("Could not parse word list: columns2 not found.")
        val result = column.children().map {
            it.getElementsByTag("a").firstOrNull()?.text() ?: error("Could not get entry")
        }
        return result
    }

    fun parseContent(htmlContent: String): Word? {
        val jsoup = Jsoup.parse(htmlContent)
        val mainContentElement =
            jsoup.getElementById("main_content") ?: return null // Word not found

        return Word(parseCobuildDictionary(mainContentElement))
    }

    private fun parseCobuildDictionary(mainContentElement: Element): CobuildDictionary {
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

    private fun parseSection(dictionaryElement: Element): CobuildDictionarySection {
        return CobuildDictionarySection(
            word = parseWordName(dictionaryElement),
            frequency = parseWordFrequency(dictionaryElement),
            forms = parseWordForms(dictionaryElement),
            pronunciation = parsePronunciation(dictionaryElement),
            definitionEntries = parseDefinition(dictionaryElement)
        )
    }

    private fun parseWordForms(dictionaryElement: Element): List<WordForm>? {
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

    private fun parseWordName(dictionaryElement: Element): String {
        return dictionaryElement.getElementsByClass("title_container")
            .firstOrNull()
            ?.getElementsByTag("h2")
            ?.lastOrNull()
            ?.getElementsByTag("span")
            ?.firstOrNull()
            ?.text()
            ?: error("Cannot find word name")
    }

    private fun parseWordFrequency(dictionaryElement: Element): Int? {
        return dictionaryElement.getElementsByClass("word-frequency-img")
            .firstOrNull()
            ?.attributes()
            ?.get("data-band")
            ?.toInt()
    }

    private fun parsePronunciation(dictionaryElement: Element): Pronunciation {
        val pronElement = dictionaryElement.getElementsByClass("mini_h2").firstOrNull()
            ?: error("Cannot find word pronunciation")
        val pronItem = dictionaryElement.getElementsByClass("pron").firstOrNull()
        val pronStr = pronItem?.text()
        val soundElement = pronElement.getElementsByAttribute("data-src-mp3").firstOrNull()
        val sound = soundElement?.let {
            it.attributes()["data-src-mp3"] ?: error("Cannot find sound url")
        }
        if (pronStr == null && soundElement == null) {
            error("Cannot find word pronunciation")
        }
        return Pronunciation(pronStr ?: "[err]", sound)
    }

    private fun parseDefinition(dictionaryElement: Element): List<DefinitionEntry> {
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
                    null
                ) // TODO: 2021/7/28 Grammar pattern and sound url.
            }

            definitionEntries.add(
                DefinitionEntry(
                    index = index + 1,
                    type = grammarGroup,
                    definition = Definition(
                        def = def,
                        examples = examples,
                        synonyms = parseSynonyms(senseElement)
                    ),
                    extraDefinitions = emptyList() // TODO: 2021/7/28 Extra definitions.
                )
            )
        }

        return definitionEntries
    }

    private fun parseSynonyms(synonymElement: Element): List<String>? {
        val result = mutableListOf<String>()
        val thesElement = synonymElement.getElementsByClass("thes").first()
            ?: return null

        thesElement.getElementsByClass("form ref").forEach { //
            result.add(it.text())
        }
        return result
    }
}