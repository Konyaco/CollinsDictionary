package me.konyaco.collinsdictionary.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
expect fun ColumnWithScrollBar(modifier: Modifier, content: @Composable () -> Unit)