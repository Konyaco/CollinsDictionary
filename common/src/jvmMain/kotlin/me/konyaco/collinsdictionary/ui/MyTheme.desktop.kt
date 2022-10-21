package me.konyaco.collinsdictionary.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.platform.Font

actual object MyRes {
    actual val Sound: Painter
        @Composable
        get() = painterResource("ic_sound.xml")

    actual val WordNotFoundLRes: Painter
        @Composable
        get() = painterResource("wordnotfound_l.xml")

    actual val WordNotFoundSRes: Painter
        @Composable
        get() = painterResource("wordnotfound_s.xml")

    actual val WordNotFoundLRes_Dark: Painter
        @Composable
        get() = painterResource("wordnotfound_l_dark.xml")

    actual val WordNotFoundSRes_Dark: Painter
        @Composable
        get() = painterResource("wordnotfound_s_dark.xml")
}

actual val RobotoFontFamily: FontFamily =
    FontFamily(
        Font("font/roboto/Roboto-Black.ttf", FontWeight.Black, FontStyle.Normal),
        Font("font/roboto/Roboto-BlackItalic.ttf", FontWeight.Black, FontStyle.Italic),
        Font("font/roboto/Roboto-Bold.ttf", FontWeight.Bold, FontStyle.Normal),
        Font("font/roboto/Roboto-BoldItalic.ttf", FontWeight.Bold, FontStyle.Italic),
        Font("font/roboto/Roboto-Medium.ttf", FontWeight.Medium, FontStyle.Normal),
        Font("font/roboto/Roboto-MediumItalic.ttf", FontWeight.Medium, FontStyle.Italic),
        Font("font/roboto/Roboto-Regular.ttf", FontWeight.Normal, FontStyle.Normal),
        Font("font/roboto/Roboto-Italic.ttf", FontWeight.Normal, FontStyle.Italic),
        Font("font/roboto/Roboto-Light.ttf", FontWeight.Light, FontStyle.Normal),
        Font("font/roboto/Roboto-LightItalic.ttf", FontWeight.Light, FontStyle.Italic),
        Font("font/roboto/Roboto-Thin.ttf", FontWeight.Thin, FontStyle.Normal),
        Font("font/roboto/Roboto-ThinItalic.ttf", FontWeight.Thin, FontStyle.Italic),
    )

actual val SourceSerifProFontFamily: FontFamily =
    FontFamily(
        Font("font/sourceserifpro/SourceSerifPro-Black.ttf", FontWeight.Black, FontStyle.Normal),
        Font(
            "font/sourceserifpro/SourceSerifPro-BlackItalic.ttf",
            FontWeight.Black,
            FontStyle.Italic
        ),
        Font("font/sourceserifpro/SourceSerifPro-Bold.ttf", FontWeight.Bold, FontStyle.Normal),
        Font(
            "font/sourceserifpro/SourceSerifPro-BoldItalic.ttf",
            FontWeight.Bold,
            FontStyle.Italic
        ),
        Font(
            "font/sourceserifpro/SourceSerifPro-SemiBold.ttf",
            FontWeight.SemiBold,
            FontStyle.Normal
        ),
        Font(
            "font/sourceserifpro/SourceSerifPro-SemiBoldItalic.ttf",
            FontWeight.SemiBold,
            FontStyle.Italic
        ),
        Font("font/sourceserifpro/SourceSerifPro-Regular.ttf", FontWeight.Normal, FontStyle.Normal),
        Font("font/sourceserifpro/SourceSerifPro-Italic.ttf", FontWeight.Normal, FontStyle.Italic),
        Font("font/sourceserifpro/SourceSerifPro-Light.ttf", FontWeight.Light, FontStyle.Normal),
        Font(
            "font/sourceserifpro/SourceSerifPro-LightItalic.ttf",
            FontWeight.Light,
            FontStyle.Italic
        ),
        Font(
            "font/sourceserifpro/SourceSerifPro-ExtraLight.ttf",
            FontWeight.ExtraLight,
            FontStyle.Normal
        ),
        Font(
            "font/sourceserifpro/SourceSerifPro-ExtraLightItalic.ttf",
            FontWeight.ExtraLight,
            FontStyle.Italic
        ),
    )