package me.konyaco.collinsdictionary

import android.app.Application
import me.konyaco.collinsdictionary.repository.Repository
import me.konyaco.collinsdictionary.service.CollinsOnlineDictionary
import me.konyaco.collinsdictionary.service.LocalCacheDictionary
import me.konyaco.collinsdictionary.store.FileBasedLocalStorage

class MyApplication : Application() {
    lateinit var repository: Repository

    override fun onCreate() {
        super.onCreate()
        repository = Repository(
            CollinsOnlineDictionary(),
            LocalCacheDictionary(FileBasedLocalStorage(filesDir.resolve("cache").also {
                if (!it.exists()) it.mkdir()
            }))
        )
    }
}