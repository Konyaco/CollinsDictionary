package me.konyaco.collinsdictionary

import android.app.Application
import me.konyaco.collinsdictionary.store.FileBasedLocalStorage
import me.konyaco.collinsdictionary.store.LocalStorage
import org.koin.core.context.startKoin
import org.koin.dsl.module

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        initKoin()
    }

    private fun initKoin() {
        startKoin {
            modules(
                commonModule,
                module {
                    single<LocalStorage> {
                        FileBasedLocalStorage(filesDir.resolve("cache").also {
                            if (!it.exists()) it.mkdir()
                        })
                    }
                }
            )
        }
    }
}