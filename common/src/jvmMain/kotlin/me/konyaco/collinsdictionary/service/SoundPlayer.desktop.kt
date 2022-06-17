package me.konyaco.collinsdictionary.service

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.sun.javafx.application.PlatformImpl
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import javafx.scene.media.Media
import javafx.scene.media.MediaPlayer
import kotlinx.coroutines.*
import java.io.File
import kotlin.coroutines.resume

class SoundPlayerImpl : SoundPlayer {

    init {
        PlatformImpl.startup { } // In order to use media player.
    }

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
                    System.err.println("Failed to download sound: " + e.stackTraceToString())
                    onError(e)
                    return@async
                }
                it?.cancelAndJoin()
                try {
                    onStart()
                    playMedia(file)
                    onStop()
                } catch (e: Throwable) {
                    System.err.println("Failed to play sound: " + e.stackTraceToString())
                    onError(e)
                }
            }
        }
    }

    private val scope = CoroutineScope(Dispatchers.Default)
    private var job: Job? = null

    private suspend fun playMedia(file: File) {
        suspendCancellableCoroutine<Unit> { continuation ->
            val media = Media(file.toURI().toString())
            val mediaPlayer = MediaPlayer(media)
            continuation.invokeOnCancellation {
                mediaPlayer.stop()
            }
            with(mediaPlayer) {
                setOnEndOfMedia {
                    continuation.resume(Unit)
                }
                play()
            }
        }
    }

    private fun directory(): File = File("./cache/music/").also { it.mkdirs() }

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
actual fun getSoundPlayer(): SoundPlayer = remember {
    SoundPlayerImpl()
}