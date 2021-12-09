package me.konyaco.collinsdictionary.viewmodel

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import me.konyaco.collinsdictionary.service.CollinsOnlineDictionary
import me.konyaco.collinsdictionary.service.SearchResult
import me.konyaco.collinsdictionary.service.Word

class AppViewModel {
    private val collinsDictionary = CollinsOnlineDictionary()
    private val scope = CoroutineScope(Dispatchers.Default)

    sealed class Result {
        data class Succeed(val data: Word) : Result()
        object WordNotFound : Result()
        data class Failed(val message: String) : Result()
    }

    val isSearching = MutableStateFlow(false)
    val queryResult = MutableStateFlow<Result?>(null)

    fun search(word: String) {
        scope.launch {
            isSearching.emit(true)
            try {
                when (val result = collinsDictionary.search(word)) {
                    is SearchResult.PreciseWord -> {
                        val word = collinsDictionary.getDefinition(result.word)
                        if (word != null) {
                            queryResult.emit(Result.Succeed(word))
                        } else {
                            queryResult.emit(Result.WordNotFound)
                        }
                    }
                    is SearchResult.Redirect -> {
                        val word = collinsDictionary.getDefinition(result.redirectTo)
                        if (word != null) {
                            queryResult.emit(Result.Succeed(word))
                        } else {
                            queryResult.emit(Result.WordNotFound)
                        }
                    }
                    is SearchResult.NotFound -> {
                        queryResult.emit(Result.WordNotFound)
                        // TODO: Use user-friendly UI to display alternatives.
                    }
                }
            } catch (e: Exception) {
                queryResult.emit(Result.Failed(e.message ?: "Unknown error"))
                return@launch
            } finally {
                isSearching.emit(false)
            }
        }
    }
}