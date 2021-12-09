package me.konyaco.collinsdictionary.store

import java.io.File

class FileBasedLocalStorage(
    private val dir: File
) : LocalStorage {
    private val searchDir = dir.resolve("search").also { it.mkdirIfNotExists() }
    private val definitionDir = dir.resolve("definition").also { it.mkdirIfNotExists() }

    override fun getSearch(word: String): String? {
        return searchDir.resolve(word).takeIf { it.exists() }?.readText()
    }

    override fun getDefinition(word: String): String? {
        return definitionDir.resolve(word).takeIf { it.exists() }?.readText()
    }

    override fun saveSearch(word: String, value: String) {
        searchDir.resolve(word).writeText(value)
    }

    override fun saveDefinition(word: String, value: String) {
        definitionDir.resolve(word).writeText(value)
    }

    override fun deleteSearch(word: String) {
        searchDir.resolve(word).deleteIfExists()
    }

    override fun deleteDefinition(word: String) {
        definitionDir.resolve(word).deleteIfExists()
    }
}

private fun File.deleteIfExists() {
    if (exists()) delete()
}

private fun File.mkdirIfNotExists() {
    if (!exists()) mkdir()
}