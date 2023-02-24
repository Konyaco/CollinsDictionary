package me.konyaco.collinsdictionary.ui.util

import androidx.compose.ui.Modifier

expect fun Modifier.onEnterPress(onPress: () -> Unit): Modifier
