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
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import kotlinx.coroutines.*
import java.io.File
import kotlin.coroutines.resume

private const val TAG = "SoundPlayer.android"

class SoundPlayerImpl : SoundPlayer {
    var context: Context? = null

    override fun play(
        url: String,
        onStart: () -> Unit,
        onStop: () -> Unit,
        onError: (e: Throwable) -> Unit
    ) {
        job.let {
            job = scope.async(Dispatchers.IO) {
                val file = try {
                    getFile(url)
                } catch (e: Throwable) {
                    Log.w(TAG, "Failed to download sound", e)
                    onError(e)
                    return@async
                }
                it?.cancelAndJoin()
                try {
                    onStart()
                    playMedia(file)
                    onStop()
                } catch (e: Throwable) {
                    Log.w("Failed to play sound", e)
                    onError(e)
                }
            }
        }
    }

    private val scope = CoroutineScope(Dispatchers.Default)
    private var job: Job? = null

    private suspend fun playMedia(file: File) {
        suspendCancellableCoroutine<Unit> { continuation ->
            val mediaPlayer = MediaPlayer()
            continuation.invokeOnCancellation {
                mediaPlayer.pause()
                mediaPlayer.release()
            }
            with(mediaPlayer) {
                setOnCompletionListener {
                    release()
                    continuation.resume(Unit)
                }
                setAudioAttributes(
                    AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .build()
                )
                setDataSource(context!!, file.toUri())
                prepare()
                start()
            }
        }
    }

    private fun directory(): File =
        context!!.externalCacheDir!!.resolve("sound").also { it.mkdir() }

    // Get filename part of url (xx://xx/xxx.mp3)
    private fun fileNameFromUrl(url: String) = url.substringAfterLast("/")

    private suspend fun getFile(url: String): File {
        val fileName = fileNameFromUrl(url)
        val file = directory().resolve(fileName)
        if (!file.exists()) {
            val bytes = HttpClient().get(url).readBytes()
            withContext(Dispatchers.IO) {
                file.outputStream().use {
                    it.write(bytes)
                }
            }
        }
        return file
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