package me.konyaco.collinsdictionary.viewmodel

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.launch
import me.konyaco.collinsdictionary.repository.Repository
import me.konyaco.collinsdictionary.service.SearchResult
import me.konyaco.collinsdictionary.service.Word
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

data class AppUiState(
    val isSearching: Boolean,
    val queryResult: Result?
) {
    sealed class Result(open val word: String) {
        data class Succeed(override val word: String, val data: Word) : Result(word)
        data class WordNotFound(override val word: String) : Result(word)
        data class Failed(override val word: String, val message: String) : Result(word)
    }
}

class AppViewModel: KoinComponent {
    private val repository: Repository by inject()
    private val _uiState = MutableStateFlow<AppUiState>(AppUiState(false, null))
    val uiState = _uiState.asStateFlow()

    private val scope = CoroutineScope(Dispatchers.Default)

    private var searchJob: Job? = null

    fun search(word: String) {
        searchJob?.cancel()
        searchJob = scope.launch {
            _uiState.let {
                it.emit(it.value.copy(isSearching = true))
            }

            try {
                repository.search(word).take(1).collect { result ->
                    val queryResult = when (result.data) {
                        is SearchResult.PreciseWord -> {
                            getDef(result.data.word)
                        }
                        is SearchResult.Redirect -> {
                            getDef(result.data.redirectTo)
                        }
                        is SearchResult.NotFound -> {
                            AppUiState.Result.WordNotFound(word)
                            // TODO: Use user-friendly UI to display alternatives.
                        }
                    }
                    _uiState.let {
                        it.emit(it.value.copy(queryResult = queryResult))
                    }
                }

            } catch (e: Exception) {
                _uiState.let {
                    it.emit(it.value.copy(queryResult = AppUiState.Result.Failed(word, e.message ?: "Unknown error")))
                }
                return@launch
            } finally {
                _uiState.let {
                    it.emit(it.value.copy(isSearching = false))
                }
            }
        }
    }

    fun clearResult() {
        scope.launch {
            _uiState.let {
                it.emit(it.value.copy(isSearching = false, queryResult = null))
            }
        }
    }

    private suspend fun getDef(word: String): AppUiState.Result {
        lateinit var r: AppUiState.Result
        repository.getDefinition(word).take(1).collect { result ->
            r = if (result.data != null) {
                AppUiState.Result.Succeed(word, result.data)
            } else {
                AppUiState.Result.WordNotFound(word)
            }
        }
        return r
    }
}