import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import service.CollinsOnlineDictionary
import service.Word

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun App(component: AppComponent) {
    MyTheme {
        Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colors.background) {
            val queryState by component.queryState.collectAsState()

            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                AnimatedVisibility(
                    queryState is AppComponent.State.None
                ) {
                    Text(
                        text = "Collins Online\nDictionary",
                        fontSize = 72.sp,
                        fontFamily = FontFamily.Serif,
                        textAlign = TextAlign.Center
                    )
                }
                Spacer(Modifier.height(32.dp))
                var input by remember { mutableStateOf("") }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    TextField(
                        value = input,
                        onValueChange = { input = it },
                        singleLine = true,
                        keyboardActions = KeyboardActions(onDone = { component.search(input) }),
                        keyboardOptions = KeyboardOptions(
                            KeyboardCapitalization.Words,
                            keyboardType = KeyboardType.Ascii,
                            imeAction = ImeAction.Search
                        )
                    )
                    Spacer(Modifier.width(16.dp))
                    IconButton(onClick = {
                        component.search(input)
                    }) {
                        Icon(Icons.Filled.Search, "Search")
                    }
                }
                Spacer(Modifier.height(32.dp))

                Column(Modifier.fillMaxWidth().animateContentSize()) {
                    when (val state = queryState) {
                        is AppComponent.State.Succeed -> {
                            Box(Modifier.fillMaxWidth()) {
                                val scrollState = rememberScrollState()
                                Column(modifier = Modifier.fillMaxWidth().verticalScroll(scrollState)) {
                                    Definition(state.data, Modifier.fillMaxWidth().padding(horizontal = 48.dp))
                                }
                                VerticalScrollbar(
                                    adapter = rememberScrollbarAdapter(scrollState),
                                    modifier = Modifier.align(Alignment.CenterEnd)
                                )
                            }
                        }
                        is AppComponent.State.Failed -> {
                            Text("Failed: ${state.message}", color = Color.Red)
                        }
                        AppComponent.State.Searching -> {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                                CircularProgressIndicator()
                            }
                        }
                        AppComponent.State.WordNotFound -> {
                            Text("service.Word not found")
                        }
                    }
                }
            }
        }
    }
}


class AppComponent {
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