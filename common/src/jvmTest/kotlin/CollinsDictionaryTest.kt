import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import kotlinx.coroutines.runBlocking
import me.konyaco.collinsdictionary.service.CollinsOnlineDictionary
import me.konyaco.collinsdictionary.service.SearchResult
import java.io.File
import kotlin.test.Test

internal class CollinsDictionaryTest {
    @Test
    fun ktorTest() = runBlocking {
        val client = HttpClient(CIO) { followRedirects = false }
        println(client.get<String>("https://www.collinsdictionary.com/dictionary/english/their"))
    }

    @Test
    fun testOne(): Unit = runBlocking {
        CollinsOnlineDictionary().getDefinition("hello")
    }

    @Test
    fun testBundle() = runBlocking {
        val file = ClassLoader.getSystemResource("word_list.txt").file.let {
            File(it)
        }
        val dict = CollinsOnlineDictionary()
        val errors = mutableListOf<Pair<String, Exception>>()
        file.useLines {
            for (word in it) {
                try {
                    println("Testing: $word")
                    dict.search(word)
                    dict.getDefinition(word)
                } catch (e: Exception) {
                    errors.add(word to e)
                    e.printStackTrace()
                }
            }
        }
        if (errors.isNotEmpty()) {
            error("${errors.size} Error(s)")
        }
    }

    @Test
    fun testWordNotFound() = runBlocking {
        assert(CollinsOnlineDictionary().getDefinition("wfnigjei") == null)
    }

    @Test
    fun testSearch() = runBlocking {
        assert(
            CollinsOnlineDictionary().search("interchangebly")
                .also(::println) is SearchResult.NotFound
        )
        assert(
            CollinsOnlineDictionary().search("interchangeably")
                .also(::println) is SearchResult.Redirect
        )
        assert(
            CollinsOnlineDictionary().search("interchangeable")
                .also(::println) is SearchResult.PreciseWord
        )
    }
}