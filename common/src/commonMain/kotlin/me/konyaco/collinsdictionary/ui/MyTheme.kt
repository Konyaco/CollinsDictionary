package me.konyaco.collinsdictionary.ui

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.text.font.FontFamily

val myColors: MyColors
    @Composable
    get() = if (isSystemInDarkTheme()) MyDarkColors else MyLightColors

class MyColors(
    val background: Color,
    val collinsRed: Color,
    val searchBoxBackground: Color,
    val onSearchBox: Color,
)

val MyLightColors = MyColors(
    background = Color(0xFFFAFAFA),
    collinsRed = Color(0xFFD32F2F),
    searchBoxBackground = Color(0xFFEEEEEE),
    onSearchBox = Color.Black,
)

val MyDarkColors = MyColors(
    background = Color(0xFF212121),
    collinsRed = Color(0xFFE57373),
    searchBoxBackground = Color(0xFF424242),
    onSearchBox = Color.White
)

internal val lightColors = lightColors(
    primary = MyLightColors.collinsRed,
    onPrimary = Color.White,
    background = MyLightColors.background
)

internal val darkColors = darkColors(
    primary = MyDarkColors.collinsRed,
    onPrimary = Color.White,
    background = MyDarkColors.background
)

@Composable
expect fun MyTheme(content: @Composable () -> Unit)

expect object MyRes {
    val Sound: Painter
        @Composable get

    val WordNotFoundLRes: Painter
        @Composable get

    val WordNotFoundLRes_Dark: Painter
        @Composable get

    val WordNotFoundSRes: Painter
        @Composable get

    val WordNotFoundSRes_Dark: Painter
        @Composable get
}

expect val RobotoFontFamily: FontFamily
expect val SourceSerifProFontFamily: FontFamily
