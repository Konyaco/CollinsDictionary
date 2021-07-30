package service

interface CollinsDictionary {
    fun getDefinition(word: String): Word?
}

data class Word(
    val cobuildDictionary: CobuildDictionary,
    // TODO: 2021/7/30 Collins British English Dictionary, Collins American English Dictionary
)

data class CobuildDictionary(
    val sections: List<CobuildDictionarySection>
)

data class CobuildDictionarySection(
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