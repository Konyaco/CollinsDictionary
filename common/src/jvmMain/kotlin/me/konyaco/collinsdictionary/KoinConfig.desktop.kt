package me.konyaco.collinsdictionary

import me.konyaco.collinsdictionary.store.FileBasedLocalStorage
import me.konyaco.collinsdictionary.store.LocalStorage
import org.koin.core.module.Module
import java.io.File

actual fun Module.platformModule() {
    single<LocalStorage> {
        FileBasedLocalStorage(File("cache").also {
            if (!it.exists()) it.mkdir()
        })
    }
}