package me.konyaco.collinsdictionary

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
expect fun ColumnWithScrollBar(modifier: Modifier, content: @Composable () -> Unit)