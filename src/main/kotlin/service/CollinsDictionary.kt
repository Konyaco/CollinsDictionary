package service

import org.jsoup.Jsoup
import java.net.URL

interface CollinsDictionary {
    fun getDefinition(word: String): Word?
}

data class Word(
    val word: String,
    val forms: List<WordForm>?,
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
    val extraDefinitions: List<Definition>?
)

data class Definition(
    val def: String,
    val examples: List<ExampleSentence>
)

data class ExampleSentence(
    val sentence: String,
    val grammarPattern: String?,
    val soundUrl: String?,
    val synonyms: List<String>?
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
        val mainContent = jsoup.getElementById("main_content") ?: return null // Word not found

        val dictionaryElement =
            mainContent.getElementsByClass("dictionary Cob_Adv_Brit dictentry").firstOrNull()
                ?: error("Cannot find dictionary content")

        val wordName = run {
            dictionaryElement.getElementsByClass("title_container")
                .firstOrNull()
                ?.getElementsByTag("h2")
                ?.lastOrNull()
                ?.getElementsByTag("span")
                ?.firstOrNull()
                ?.text()
                ?: error("Cannot find word name")
        }

        val wordForms: List<WordForm>? = run {
            val formElement = dictionaryElement.getElementsByClass("form inflected_forms type-infl")
                .firstOrNull() ?: return@run null

            val descriptions = formElement.getElementsByClass("lbl type-gram").map { it.text() }
            val spells = formElement.getElementsByClass("orth").map { it.text() }

            descriptions.zip(spells) { desc, spell ->
                WordForm(desc, spell)
            }
        }

        val pronunciation = run {
            val pron = dictionaryElement.getElementsByClass("pron type-").firstOrNull()?.text()
                ?: error("Cannot find word pronunciation")
            Pronunciation(pron, null)
        }

        val definitionEntries = mutableListOf<DefinitionEntry>()

        run {
            val definitionElement =
                dictionaryElement.getElementsByClass("content definitions cobuild br ").firstOrNull()
                    ?: error("Cannot find word definitions")

            definitionElement.getElementsByClass("hom").forEachIndexed { index, element ->
                val grammarGroup: String = element.getElementsByClass("gramGrp pos").firstOrNull()?.text()
                    ?: element.getElementsByClass("gramGrp").firstOrNull()?.getElementsByClass("pos")?.text()
                    ?: return@forEachIndexed // Maybe it's not a definition, just skip
//                    ?: error("Cannot find grammar group in entry $index")

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
            forms = wordForms,
            pronunciation = pronunciation,
            definitionEntries = definitionEntries
        )
    }
}