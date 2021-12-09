package me.konyaco.collinsdictionary.viewmodel

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.launch
import me.konyaco.collinsdictionary.repository.Repository
import me.konyaco.collinsdictionary.service.SearchResult
import me.konyaco.collinsdictionary.service.Word

class AppViewModel(private val repository: Repository) {
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
                repository.search(word).take(1).collect { result ->
                    when (result.data) {
                        is SearchResult.PreciseWord -> {
                            getDef(result.data.word)
                        }
                        is SearchResult.Redirect -> {
                            getDef(result.data.redirectTo)
                        }
                        is SearchResult.NotFound -> {
                            queryResult.emit(Result.WordNotFound)
                            // TODO: Use user-friendly UI to display alternatives.
                        }
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

    private suspend fun getDef(word: String) {
        repository.getDefinition(word).take(1).collect { result ->
            if (result.data != null) {
                queryResult.emit(Result.Succeed(result.data))
            } else {
                queryResult.emit(Result.WordNotFound)
            }
        }
    }

}