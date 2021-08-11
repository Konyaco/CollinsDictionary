package me.konyaco.collinsdictionary.ui

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import me.konyaco.collinsdictionary.service.CollinsOnlineDictionary
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

            val result = try {
                collinsDictionary.getDefinition(word)
            } catch (e: Exception) {
                queryState.emit(State.Failed(e.message ?: "Unknown error"))
                return@launch
            }

            if (result == null) {
                queryState.emit(State.WordNotFound)
            } else {
                queryState.emit(State.Succeed(result))
            }
        }
    }
}