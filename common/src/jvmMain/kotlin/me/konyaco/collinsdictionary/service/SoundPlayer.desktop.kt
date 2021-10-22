package me.konyaco.collinsdictionary.service

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember

class SoundPlayerImpl : SoundPlayer {
    override fun play(url: String, onStart: () -> Unit, onStop: () -> Unit, onError: (e: Throwable) -> Unit) {
        // TODO: 2021/8/13
        println(this.toString() + url)
    }
}

@Composable
actual fun getSoundPlayer(): SoundPlayer = remember { SoundPlayerImpl() }