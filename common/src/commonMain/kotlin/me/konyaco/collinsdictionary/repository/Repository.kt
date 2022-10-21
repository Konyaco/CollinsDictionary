package me.konyaco.collinsdictionary.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import me.konyaco.collinsdictionary.service.CollinsOnlineDictionary
import me.konyaco.collinsdictionary.service.LocalCacheDictionary
import me.konyaco.collinsdictionary.service.SearchResult
import me.konyaco.collinsdictionary.service.Word

class Repository(
    private val onlineDictionary: CollinsOnlineDictionary,
    private val localCacheDictionary: LocalCacheDictionary
) {
    data class Result<out T>(
        val source: Source,
        val data: T
    ) {
        enum class Source {
            REMOTE, LOCAL
        }
    }

    fun search(word: String): Flow<Result<SearchResult>> = flow {
        val localResult = localCacheDictionary.search(word)
        when (localResult) {
            is SearchResult.PreciseWord -> emit(Result(Result.Source.LOCAL, localResult))
            is SearchResult.Redirect -> emit(Result(Result.Source.LOCAL, localResult))
            else -> {
                // TODO:
            }
        }
        val remoteResult = onlineDictionary.search(word)
        localCacheDictionary.cacheSearchResult(word, remoteResult)
        emit(Result(Result.Source.REMOTE, remoteResult))
    }

    fun getDefinition(word: String): Flow<Result<Word?>> = flow {
        val localDefinition = localCacheDictionary.getDefinition(word)
        if (localDefinition != null) {
            emit(Result(Result.Source.LOCAL, localDefinition))
        }
        val remoteDefinition = onlineDictionary.getDefinition(word)
        remoteDefinition?.let {
            localCacheDictionary.cacheDefinition(word, remoteDefinition)
        }
        emit(Result(Result.Source.REMOTE, remoteDefinition))
    }
}