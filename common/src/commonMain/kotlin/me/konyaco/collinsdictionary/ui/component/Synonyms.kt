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
        color = Color.Transparent,
        contentColor = MaterialTheme.colors.onSurface,
        border = BorderStroke(
            1.dp,
            MaterialTheme.colors.onBackground.copy(0.18f)
        )
    ) {
        Box(Modifier.wrapContentHeight()) {
            Column(Modifier.padding(14.dp, 10.dp, 14.dp, 14.dp)) {
                Text(
                    text = "SYNONYMS",
                    fontSize = 14.sp,
                    color = MaterialTheme.colors.onBackground.copy(0.54f)
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