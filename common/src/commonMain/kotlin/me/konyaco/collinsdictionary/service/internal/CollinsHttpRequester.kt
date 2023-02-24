package me.konyaco.collinsdictionary.service.internal

import io.ktor.client.*
import io.ktor.client.engine.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import java.net.ProxySelector
import java.net.URI

internal class CollinsHttpRequester {
    private val client = HttpClient(CIO) {
        setupClient()
        followRedirects = false
    }
    private val clientFollowRedirect = HttpClient(CIO) {
        setupClient()
    }

    private fun HttpClientConfig<*>.setupClient() {
        engine {
            val proxy =
                ProxySelector.getDefault().select(URI("https://www.collinsdictionary.com"))
                    .firstOrNull()
            if (proxy != null && proxy.type != ProxyType.UNKNOWN) {
                this.proxy = proxy
            }
        }
    }

    companion object {
        private const val SEARCH_URL = "https://www.collinsdictionary.com/search"
        private const val SPELL_CHECK_URL = "https://www.collinsdictionary.com/spellcheck/english"
        private const val DICTIONARY_URL = "https://www.collinsdictionary.com/dictionary/english"
        private fun buildSearchURL(word: String) = "$SEARCH_URL/?dictCode=english&q=$word"
        private fun buildDictionaryURL(word: String) = "$DICTIONARY_URL/$word"
    }

    suspend fun getDefinition(word: String): String {
        return clientFollowRedirect.get(buildDictionaryURL(word)).bodyAsText()
    }

    sealed interface SearchResult {
        data class PreciseWord(val contentHtml: String): SearchResult
        data class Redirect(val redirectWord: String): SearchResult
        data class NotFound(val wordListHtml: String): SearchResult
    }

    suspend fun search(word: String): SearchResult {
        val response = try {
            client.get(buildSearchURL(word))
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
                val html = client.get(redirectedUrl).bodyAsText() // Get response in [spellcheck]
                SearchResult.NotFound(html)
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

}