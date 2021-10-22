package me.konyaco.collinsdictionary.service

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf

interface SoundPlayer {
    fun play(url: String, onStart: () -> Unit, onStop: () -> Unit, onError: (e: Throwable) -> Unit)
}

@Composable
expect fun getSoundPlayer(): SoundPlayer

val LocalSoundPlayer = compositionLocalOf<SoundPlayer> { error("No default impl") }

@Composable
fun ProvideSoundPlayer(content: @Composable () -> Unit) {
    CompositionLocalProvider(LocalSoundPlayer provides getSoundPlayer(), content = content)
}