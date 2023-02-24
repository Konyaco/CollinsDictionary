package me.konyaco.collinsdictionary.service

import androidx.compose.runtime.Composable

@Composable
actual fun getSoundPlayer(): SoundPlayer = JavaFxSoundPlayer