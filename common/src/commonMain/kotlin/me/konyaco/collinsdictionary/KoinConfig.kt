package me.konyaco.collinsdictionary

import me.konyaco.collinsdictionary.repository.Repository
import me.konyaco.collinsdictionary.service.CollinsOnlineDictionary
import me.konyaco.collinsdictionary.service.LocalCacheDictionary
import me.konyaco.collinsdictionary.viewmodel.AppViewModel
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val commonModule = module {
    singleOf(::LocalCacheDictionary)
    singleOf(::CollinsOnlineDictionary)
    singleOf(::Repository)
    singleOf(::AppViewModel)
}