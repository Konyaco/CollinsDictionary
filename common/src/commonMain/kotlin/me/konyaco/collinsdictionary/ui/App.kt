package me.konyaco.collinsdictionary.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import me.konyaco.collinsdictionary.ui.component.CobuildDictionarySection
import me.konyaco.collinsdictionary.ui.component.SearchBox

val LocalScreenSize = compositionLocalOf { ScreenSize.PHONE }

enum class ScreenSize {
    PHONE, TABLET, LAPTOP, DESKTOP
}

@Composable
fun ProvideLocalScreenSize(content: @Composable () -> Unit) {
    var screenSize by remember { mutableStateOf(ScreenSize.PHONE) }
    BoxWithConstraints {
        val width = with(LocalDensity.current) { constraints.maxWidth.toDp() }
        screenSize = when {
            width < 600.dp -> ScreenSize.PHONE
            width >= 600.dp && width < 1240.dp -> ScreenSize.TABLET
            width >= 1240.dp && width < 1440.dp -> ScreenSize.LAPTOP
            width >= 1440.dp -> ScreenSize.DESKTOP
            else -> error("error")
        }
        CompositionLocalProvider(LocalScreenSize provides screenSize, content = content)
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun App(viewModel: AppViewModel) {
    ProvideLocalScreenSize {
        Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colors.background) {
            val queryState by viewModel.queryState.collectAsState()

            val padding = when (LocalScreenSize.current) {
                ScreenSize.PHONE -> 16.dp
                ScreenSize.TABLET -> 24.dp
                ScreenSize.LAPTOP -> 48.dp
                ScreenSize.DESKTOP -> 48.dp
            }

            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.Top
            ) {
                AnimatedVisibility(queryState is AppViewModel.State.None) {
                    Box(Modifier.fillMaxHeight(0.5f), contentAlignment = Alignment.BottomStart) {
                        Column(Modifier.padding(vertical = 16.dp, horizontal = padding)) {
                            Title()
                            Spacer(Modifier.height(16.dp))
                            CollinsDivider()
                        }
                    }
                }

                Spacer(Modifier.height(32.dp))

                var input by remember { mutableStateOf("") }
                SearchBox(
                    modifier = Modifier.padding(horizontal = padding).fillMaxWidth(),
                    value = input,
                    onValueChange = { input = it },
                    onSearchClick = { viewModel.search(input) }
                )

                AnimatedContent(targetState = queryState) { state ->
                    when (state) {
                        AppViewModel.State.None -> {
                        }
                        is AppViewModel.State.Succeed -> {
                            ColumnWithScrollBar(Modifier.fillMaxWidth()) {
                                Spacer(Modifier.height(4.dp))
                                state.data.cobuildDictionary.sections.forEach { section ->
                                    CobuildDictionarySection(
                                        section = section,
                                        modifier = Modifier.fillMaxWidth().padding(horizontal = padding)
                                    )
                                }
                            }
                        }
                        is AppViewModel.State.Failed -> {
                            ErrorPage(state.message, Modifier.fillMaxWidth())
                        }
                        AppViewModel.State.Searching -> {
                            Box(Modifier.fillMaxWidth().padding(horizontal = padding)) {
                                LinearProgressIndicator(Modifier.fillMaxWidth())
                            }
                        }
                        AppViewModel.State.WordNotFound -> {
                            Spacer(Modifier.height(16.dp))
                            WordNotFound(Modifier.fillMaxSize())
                        }
                    }
                }
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