package me.konyaco.collinsdictionary.ui.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import me.konyaco.collinsdictionary.ui.SourceSerifProFontFamily

@Composable
fun Synonyms(
    modifier: Modifier,
    words: List<String>
) {
    Surface(
        modifier = modifier.wrapContentHeight(),
        color = if (MaterialTheme.colors.isLight) Color.Transparent
        else Color(0xFF424242),
        contentColor = MaterialTheme.colors.onSurface,
        border = if (MaterialTheme.colors.isLight) BorderStroke(
            1.dp,
            MaterialTheme.colors.onSurface.copy(0.12f)
        )
        else null
    ) {
        Box(Modifier.wrapContentHeight()) {
            Column(Modifier.padding(14.dp)) {
                Text(
                    text = "SYNONYMS",
                    fontSize = 14.sp
                )
                Spacer(Modifier.height(4.dp))

                Row {
                    words.forEachIndexed { i, s ->
                        Text(
                            text = s,
                            textDecoration = TextDecoration.Underline,
                            fontSize = 16.sp,
                            fontFamily = SourceSerifProFontFamily,
                            lineHeight = 22.sp
                        )
                        if (i < words.size - 1) {
                            Text(
                                text = ", ",
                                fontSize = 16.sp,
                                fontFamily = SourceSerifProFontFamily,
                                lineHeight = 22.sp
                            )
                        }
                    }
                    // TODO(2022/7/5): FlowLayout
                }
            }
        }
    }
}