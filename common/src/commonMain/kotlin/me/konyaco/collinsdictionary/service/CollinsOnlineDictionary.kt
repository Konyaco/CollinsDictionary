package me.konyaco.collinsdictionary.service

import me.konyaco.collinsdictionary.service.internal.JsoupCollinsParser
import me.konyaco.collinsdictionary.service.internal.CollinsHttpRequester

class CollinsOnlineDictionary : CollinsDictionary {
    private val parser = JsoupCollinsParser()
    private val requester = CollinsHttpRequester()

    override suspend fun search(word: String): SearchResult {
        return when (val result = requester.search(word)) {
            is CollinsHttpRequester.SearchResult.PreciseWord -> SearchResult.PreciseWord(word)
            is CollinsHttpRequester.SearchResult.Redirect -> SearchResult.Redirect(result.redirectWord)
            is CollinsHttpRequester.SearchResult.NotFound -> SearchResult.NotFound(parser.parseAlternatives(result.wordListHtml))
        }
    }

    override suspend fun getDefinition(word: String): Word? {
        val html = requester.getDefinition(word)
        return parser.parseContent(html)
    }
}