package me.konyaco.collinsdictionary.ui.util

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent

@OptIn(ExperimentalComposeUiApi::class)
actual fun Modifier.onEnterPress(onPress: () -> Unit) =
    this.onKeyEvent {
        return@onKeyEvent if (it.key == Key.Enter) {
            onPress()
            true
        } else false
    }