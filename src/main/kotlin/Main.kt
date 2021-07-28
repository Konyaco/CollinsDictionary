fun main(args: Array<String>) {
    val collinsDictionary = CollinsDictionary()

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
        word /pronunciation/
        [Forms] a: pla. b: plase. c: ae
        -------------
        1. [Type] Verb
        Explanation

        [Examples]
          -
          -
        -------------
        2. [Type] Noun
        Explanation

        [Examples]
        =============
        */

        println(
            """
            |=============
            |${definition.word} /${definition.pronunciation.ipa}/
            |${
                definition.forms.takeIf { it.isNotEmpty() }?.let {
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
        )
    }
}