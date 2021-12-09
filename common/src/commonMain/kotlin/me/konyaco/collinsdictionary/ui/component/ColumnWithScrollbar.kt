package me.konyaco.collinsdictionary.ui.component

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
expect fun ColumnWithScrollBar(modifier: Modifier, content: @Composable () -> Unit)