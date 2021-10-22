package me.konyaco.collinsdictionary.service

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.core.net.toUri
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.net.URL

private const val TAG = "SoundPlayer.android"

class SoundPlayerImpl : SoundPlayer {
    var context: Context? = null

    private val scope = CoroutineScope(Dispatchers.Default)

    override fun play(
        url: String,
        onStart: () -> Unit,
        onStop: () -> Unit,
        onError: (e: Throwable) -> Unit
    ) {
        scope.launch(Dispatchers.IO) {
            try {
                val fileName =
                    url.substringAfterLast("/") // Get filename part of url (xx://xx/xxx.mp3)
                val file = context!!.externalCacheDir!!.resolve("sound")
                    .also { it.mkdir() }
                    .resolve(fileName)
                if (!file.exists()) {
                    val bytes = URL(url).readBytes()
                    file.outputStream().use {
                        it.write(bytes)
                    }
                }
                MediaPlayer().apply {
                    setDataSource(context!!, file.toUri())
                    setAudioAttributes(
                        AudioAttributes.Builder()
                            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                            .setUsage(AudioAttributes.USAGE_MEDIA)
                            .build()
                    )
                    prepare()
                    onStart()
                    start()
                    setOnCompletionListener {
                        onStop()
                        release()
                    }
                }
            } catch (e: Throwable) {
                Log.w(TAG, "Failed to play sound", e)
                onError(e)
            }
        }
    }
}

@Composable
actual fun getSoundPlayer(): SoundPlayer {
    val context = LocalContext.current.applicationContext
    val soundPlayer = remember { SoundPlayerImpl() }
    DisposableEffect(Unit) {
        soundPlayer.context = context
        onDispose { soundPlayer.context = null }
    }
    return soundPlayer
}