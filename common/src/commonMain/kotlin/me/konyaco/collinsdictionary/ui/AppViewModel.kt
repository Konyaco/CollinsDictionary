package me.konyaco.collinsdictionary.ui

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

    var queryState: MutableStateFlow<State> = MutableStateFlow(State.None)

    sealed class State {
        object None : State()
        object Searching : State()
        data class Succeed(val data: Word) : State()
        object WordNotFound : State()
        data class Failed(val message: String) : State()
    }

    fun search(word: String) {
        scope.launch {
            queryState.emit(State.Searching)
            try {
                val result = collinsDictionary.search(word)

                when (result) {
                    is SearchResult.PreciseWord -> {
                        val word = collinsDictionary.getDefinition(result.word)
                        if (word != null) {
                            queryState.emit(State.Succeed(word))
                        } else {
                            queryState.emit(State.WordNotFound)
                        }
                    }
                    is SearchResult.Redirect -> {
                        val word = collinsDictionary.getDefinition(result.redirectTo)
                        if (word != null) {
                            queryState.emit(State.Succeed(word))
                        } else {
                            queryState.emit(State.WordNotFound)
                        }
                    }
                    is SearchResult.NotFound -> {
                        queryState.emit(State.WordNotFound)
                        // TODO: Use user-friendly UI to display alternatives.
                    }
                }
            } catch (e: Exception) {
                queryState.emit(State.Failed(e.message ?: "Unknown error"))
                return@launch
            }
        }
    }
}