package me.konyaco.collinsdictionary.ui

import androidx.compose.animation.*
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import me.konyaco.collinsdictionary.service.ProvideSoundPlayer
import me.konyaco.collinsdictionary.service.Word
import me.konyaco.collinsdictionary.ui.component.CobuildDictionarySection
import me.konyaco.collinsdictionary.ui.component.ColumnWithScrollBar
import me.konyaco.collinsdictionary.ui.component.SearchBox
import me.konyaco.collinsdictionary.viewmodel.AppViewModel
import me.konyaco.collinsdictionary.viewmodel.AppUiState
import org.koin.java.KoinJavaComponent

val LocalScreenSize = compositionLocalOf { ScreenSize.PHONE }

enum class ScreenSize {
    PHONE, TABLET, LAPTOP, DESKTOP
}

@Composable
fun ProvideLocalScreenSize(content: @Composable () -> Unit) {
    var screenSize by remember { mutableStateOf(ScreenSize.PHONE) }
    BoxWithConstraints {
        val width = with(LocalDensity.current) { constraints.maxWidth.toDp() }
        LaunchedEffect(width) {
            screenSize = when {
                width < 600.dp -> ScreenSize.PHONE
                width >= 600.dp && width < 1240.dp -> ScreenSize.TABLET
                width >= 1240.dp && width < 1440.dp -> ScreenSize.LAPTOP
                width >= 1440.dp -> ScreenSize.DESKTOP
                else -> error("error")
            }
        }
        CompositionLocalProvider(LocalScreenSize provides screenSize, content = content)
    }
}

@Composable
fun App(viewModel: AppViewModel = remember { KoinJavaComponent.getKoin().get() }) {
    val uiState by viewModel.uiState.collectAsState()
    App(
        uiState.queryResult,
        uiState.isSearching,
        uiState.queryResult?.word ?: ""
    ) {
        viewModel.search(it)
    }
}

@Composable
fun App(
    data: AppUiState.Result?,
    isSearching: Boolean,
    searchInput: String,
    onSearch: (text: String) -> Unit
) {
    ProvideSoundPlayer {
        ProvideLocalScreenSize {
            Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colors.background) {
                val padding by animateDpAsState(when (LocalScreenSize.current) {
                    ScreenSize.PHONE -> 16.dp
                    ScreenSize.TABLET -> 24.dp
                    ScreenSize.LAPTOP -> 48.dp
                    ScreenSize.DESKTOP -> 48.dp
                })

                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.Start,
                    verticalArrangement = Arrangement.Top
                ) {
                    Banner(display = data == null, padding)
                    Spacer(Modifier.height(32.dp))
                    var input by remember(searchInput) { mutableStateOf(searchInput) }
                    SearchBox(
                        modifier = Modifier.zIndex(2f).padding(horizontal = padding).fillMaxWidth(),
                        value = input,
                        onValueChange = { input = it },
                        onSearchClick = { onSearch(input) },
                        isSearching = isSearching
                    )
                    Result(Modifier.zIndex(1f).weight(1f).fillMaxWidth(), data, padding)
                }
            }
        }
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
private fun Result(modifier: Modifier, data: AppUiState.Result?, contentPadding: Dp) {
    AnimatedContent(modifier = modifier, targetState = data) { data ->
        when (data) {
            is AppUiState.Result.Succeed -> Content(data.data, contentPadding)
            is AppUiState.Result.WordNotFound -> WordNotFound(
                Modifier.fillMaxSize().padding(vertical = 32.dp)
            )
            is AppUiState.Result.Failed -> ErrorPage(
                data.message,
                Modifier.fillMaxWidth()
            )
            null -> {}
        }
    }
}

@Composable
private fun Banner(display: Boolean, contentPadding: Dp) {
    AnimatedVisibility(
        display,
        enter = fadeIn() + expandVertically(),
        exit = fadeOut() + shrinkVertically()
    ) {
        Box(Modifier.fillMaxHeight(0.5f), contentAlignment = Alignment.BottomStart) {
            Column(Modifier.padding(vertical = 16.dp, horizontal = contentPadding)) {
                Title()
                Spacer(Modifier.height(16.dp))
                CollinsDivider()
            }
        }
    }
}

@Composable
private fun Title(modifier: Modifier = Modifier) {
    Text(
        modifier = modifier,
        text = "Collins\nOnline\nDictionary",
        fontSize = 48.sp,
        fontFamily = SourceSerifProFontFamily,
        textAlign = TextAlign.Start,
        fontWeight = FontWeight.Bold,
        lineHeight = 68.sp
    )
}

@Composable
private fun CollinsDivider() {
    Divider(Modifier.width(88.dp), color = MaterialTheme.colors.primary, thickness = 4.dp)
}

@Composable
private fun Content(word: Word, contentPadding: Dp) {
    ColumnWithScrollBar(Modifier.fillMaxSize()) {
        Spacer(Modifier.height(4.dp))
        word.cobuildDictionary.sections.forEach { section ->
            CobuildDictionarySection(
                section = section,
                modifier = Modifier.fillMaxWidth().padding(horizontal = contentPadding)
            )
        }
    }
}

@Composable
private fun ErrorPage(message: String, modifier: Modifier = Modifier) {
    Box(modifier.padding(24.dp)) {
        Text("Failed: $message", color = Color.Red)
    }
}

@Composable
private fun WordNotFound(modifier: Modifier) {
    when (LocalScreenSize.current) {
        ScreenSize.PHONE -> {
            Image(
                modifier = modifier.fillMaxWidth().aspectRatio(1f).padding(end = 16.dp),
                painter = if (isSystemInDarkTheme()) MyRes.WordNotFoundSRes_Dark else MyRes.WordNotFoundSRes,
                alignment = Alignment.CenterEnd,
                contentDescription = "Word Not Found",
                colorFilter = null,
                contentScale = ContentScale.FillHeight
            )
        }
        ScreenSize.TABLET, ScreenSize.LAPTOP, ScreenSize.DESKTOP -> {
            Image(
                modifier = modifier.padding(vertical = 32.dp, horizontal = 48.dp),
                painter = if (isSystemInDarkTheme()) MyRes.WordNotFoundLRes_Dark else MyRes.WordNotFoundLRes,
                contentDescription = "Word Not Found",
                colorFilter = null
            )
        }
    }
}