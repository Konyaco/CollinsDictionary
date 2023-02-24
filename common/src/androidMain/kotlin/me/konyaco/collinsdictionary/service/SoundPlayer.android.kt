package me.konyaco.collinsdictionary.service

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext

@Composable
actual fun getSoundPlayer(): SoundPlayer {
    val context = LocalContext.current.applicationContext
    val soundPlayer = remember { AndroidSoundPlayer() }
    DisposableEffect(Unit) {
        soundPlayer.context = context
        onDispose { soundPlayer.context = null }
    }
    return soundPlayer
}