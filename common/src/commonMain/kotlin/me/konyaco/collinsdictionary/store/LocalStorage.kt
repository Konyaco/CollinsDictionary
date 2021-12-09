package me.konyaco.collinsdictionary.store

interface LocalStorage {
    fun getSearch(word: String): String?
    fun getDefinition(word: String): String?
    fun saveSearch(word: String, value: String)
    fun saveDefinition(word: String, value: String)
    fun deleteSearch(word: String)
    fun deleteDefinition(word: String)
}