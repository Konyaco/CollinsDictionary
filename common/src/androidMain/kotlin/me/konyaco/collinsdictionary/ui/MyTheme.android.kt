package me.konyaco.collinsdictionary.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import me.konyaco.collinsdictionary.common.R


actual object MyRes {
    actual val Sound: Painter
        @Composable
        get() = painterResource(R.drawable.ic_sound)

    actual val WordNotFoundLRes: Painter
        @Composable
        get() = painterResource(R.drawable.wordnotfound_l)

    actual val WordNotFoundSRes: Painter
        @Composable
        get() = painterResource(R.drawable.wordnotfound_s)

    actual val WordNotFoundLRes_Dark: Painter
        @Composable
        get() = painterResource(R.drawable.wordnotfound_l_dark)

    actual val WordNotFoundSRes_Dark: Painter
        @Composable
        get() = painterResource(R.drawable.wordnotfound_s_dark)
}

actual val RobotoFontFamily: FontFamily = FontFamily.Default
actual val SourceSerifProFontFamily: FontFamily = FontFamily.Serif