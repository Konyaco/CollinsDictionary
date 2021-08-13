package me.konyaco.collinsdictionary.service

import androidx.compose.runtime.Composable

interface SoundPlayer {
    fun play(url: String, onStart: () -> Unit, onStop: () -> Unit, onError: (e: Throwable) -> Unit)
}

expect val soundPlayer: SoundPlayer
    @Composable
    get