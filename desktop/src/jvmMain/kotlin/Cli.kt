import me.konyaco.collinsdictionary.service.CobuildDictionarySection
import me.konyaco.collinsdictionary.service.CollinsOnlineDictionary

suspend fun cli() {
    val collinsDictionary = CollinsOnlineDictionary()

    while (true) {
        print("Enter word: ")
        val word = readLine() ?: break
        val definition = collinsDictionary.getDefinition(word)

        if (definition == null) {
            println("Word not found")
            continue
        }

        /*
        =============
        demonstrate /ˈdɛmənˌstreɪt/
        [Forms] 3rd person singular present tense: demonstrates. present participle demonstrating: past tense. past participle: demonstrated
        -------------
        1. [Type] Verb
        Explanation

        [Examples]
          -
          -
        -------------
        2. [Type] Verb
        Explanation

        [Examples]
        =============
        */

        println(wordDataToString(definition.cobuildDictionary.sections.first()))
    }
}

fun wordDataToString(definition: CobuildDictionarySection): String {
    return """
    |=============
    |${definition.word} /${definition.pronunciation.ipa}/
    |${
        definition.forms?.takeIf { it.isNotEmpty() }?.let {
            "[Forms] " + it.joinToString(".") {
                "${it.description}: ${it.spell}"
            }
        } ?: ""
    }
    |-------------
    |${ // Definitions
        definition.definitionEntries.joinToString("\n-------------\n") {
            """
            |${it.index}: [Type] ${it.type}
            |    [Definition] ${it.definition.def}
            |    ${ // Examples
                it.definition.examples.takeIf { it.isNotEmpty() }?.let {
                    "[Examples]\n" + it.joinToString("\n") {
                        "        - ${it.sentence}${it.grammarPattern?.let { " [$it]" } ?: ""}"
                    }
                } ?: ""
            }
            """.trimMargin()
        }
    }
    |============
    """.trimMargin()
}