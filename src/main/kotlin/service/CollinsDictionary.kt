package service

import org.jsoup.Jsoup
import java.net.URL

interface CollinsDictionary {
    fun getDefinition(word: String): Word?
}

data class Word(
    val word: String,
    val forms: List<WordForm>,
    val pronunciation: Pronunciation,
    val definitionEntries: List<DefinitionEntry>
)

data class WordForm(
    val description: String,
    val spell: String
)

data class Pronunciation(
    val ipa: String,
    val soundUrl: String?
)

data class DefinitionEntry(
    val index: Int,
    val type: String,
    val definition: Definition,
    val extraDefinitions: List<Definition>
)

data class Definition(
    val def: String,
    val examples: List<ExampleSentence>
)

data class ExampleSentence(
    val sentence: String,
    val grammarPattern: String?,
    val soundUrl: String?,
    val synonyms: List<String>
)

class CollinsOnlineDictionary : CollinsDictionary {
    override fun getDefinition(word: String): Word? {
        return CollinsDictionaryHTMLParser.parse(getHtml(word))
    }

    private fun getHtml(word: String): String {
        return URL("https://www.collinsdictionary.com/dictionary/english/$word").readText()
    }
}

private object CollinsDictionaryHTMLParser {
    fun parse(html: String): Word? {
        val jsoup = Jsoup.parse(html)
        val mainContent = jsoup.getElementById("main_content")
        val dictionaryElement = mainContent.getElementsByClass("dictionary Cob_Adv_Brit dictentry").first()

        val wordName = run {
            dictionaryElement.getElementsByClass("title_container")
                .firstOrNull()
                ?.getElementsByTag("h2")
                ?.lastOrNull()
                ?.getElementsByTag("span")
                ?.firstOrNull()
                ?.text()
                ?: return null
        }

        val forms = emptyList<WordForm>()

        val pronunciation = run {
            val pron = dictionaryElement.getElementsByClass("pron type-").first().text()
            Pronunciation(pron, null)
        }

        val definitionEntries = mutableListOf<DefinitionEntry>()

        run {
            val definitionElement = dictionaryElement.getElementsByClass("content definitions cobuild br ").first()
            definitionElement.getElementsByClass("hom").forEachIndexed { index, element ->
                val grammarGroup: String = element.getElementsByClass("gramGrp pos").firstOrNull()?.text()
                    ?: element.getElementsByClass("gramGrp").firstOrNull()?.getElementsByClass("pos")?.text()
//                ?: error("Cannot found grammar group")
                    ?: return@forEachIndexed // TODO: 2021/7/28 Consider how to handle error
                val senseElement = element.getElementsByClass("sense").first()

                val defElement = senseElement.getElementsByClass("def").first()
                val def = defElement.text()

                val examples = senseElement.getElementsByClass("cit type-example").map {
                    val sentence = it.getElementsByClass("quote").first().text()
                    ExampleSentence(sentence, null, null, emptyList()) // TODO: 2021/7/28
                }

                definitionEntries.add(
                    DefinitionEntry(
                        index = index + 1,
                        type = grammarGroup,
                        definition = Definition(
                            def = def,
                            examples = examples
                        ),
                        extraDefinitions = emptyList() // TODO: 2021/7/28
                    )
                )
            }
        }

        return Word(
            word = wordName,
            forms = forms,
            pronunciation = pronunciation,
            definitionEntries = definitionEntries
        )
    }
}