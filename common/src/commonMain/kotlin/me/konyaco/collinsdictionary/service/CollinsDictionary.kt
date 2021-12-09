package me.konyaco.collinsdictionary.service

import kotlinx.serialization.Serializable

interface CollinsDictionary {
    fun search(word: String): SearchResult

    fun getDefinition(word: String): Word?
}

@Serializable
sealed class SearchResult {
    data class PreciseWord(val word: String) : SearchResult()
    data class Redirect(val redirectTo: String): SearchResult()
    data class NotFound(val alternatives: List<String>) : SearchResult()
}

@Serializable
data class Word(
    val cobuildDictionary: CobuildDictionary,
    // TODO: 2021/7/30 Collins British English Dictionary, Collins American English Dictionary
)

@Serializable
data class CobuildDictionary(
    val sections: List<CobuildDictionarySection>
)

@Serializable
data class CobuildDictionarySection(
    val word: String,
    val frequency: Int?,
    val forms: List<WordForm>?,
    val pronunciation: Pronunciation,
    val definitionEntries: List<DefinitionEntry>
)

@Serializable
data class WordForm(
    val description: String,
    val spell: String
)

@Serializable
data class Pronunciation(
    val ipa: String,
    val soundUrl: String?
)

@Serializable
data class DefinitionEntry(
    val index: Int,
    val type: String,
    val definition: Definition,
    val extraDefinitions: List<Definition>?
)

@Serializable
data class Definition(
    val def: String,
    val examples: List<ExampleSentence>
)

@Serializable
data class ExampleSentence(
    val sentence: String,
    val grammarPattern: String?,
    val soundUrl: String?,
    val synonyms: List<String>?
)