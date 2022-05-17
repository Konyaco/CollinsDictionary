import kotlinx.coroutines.runBlocking
import me.konyaco.collinsdictionary.service.CollinsOnlineDictionary
import me.konyaco.collinsdictionary.service.SearchResult
import java.io.File
import kotlin.test.Test

internal class CollinsDictionaryTest {
    @Test
    fun testOne(): Unit = runBlocking {
        CollinsOnlineDictionary().getDefinition("hello")
    }

    @Test
    fun `Test a bundle of word in word list, and count failures`() = runBlocking {
        val dict = CollinsOnlineDictionary()
        val errors = mutableListOf<Pair<String, Exception>>()
        for(word in getTestWordList()) {
            try {
                println("Testing: $word")
                dict.search(word)
                dict.getDefinition(word)
            } catch (e: Exception) {
                errors.add(word to e)
                e.printStackTrace()
            }
        }
        if (errors.isNotEmpty()) {
            error("${errors.size} Error(s)")
        }
    }

    private fun getTestWordList(): List<String> {
        val file = File(ClassLoader.getSystemResource("word_list.txt").file)
        return file.readLines()
    }

    @Test
    fun `Test if getDefinition() returns null when word does not exists`() = runBlocking {
        assert(CollinsOnlineDictionary().getDefinition("wfnigjei") == null)
    }

    @Test
    fun `Test three result type of search()`() = runBlocking {
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

    @Test
    fun `Count error pronunciations`() = runBlocking<Unit> {
        val dict = CollinsOnlineDictionary()
        val errors = mutableListOf<String>()
        val list = getTestWordList()
        list.forEachIndexed { index, word ->
            try {
                print("Testing[$index/${list.size}]: $word")
                val def = dict.getDefinition(word)
                if (def?.cobuildDictionary?.sections?.any { it.pronunciation.ipa != "[err]" } == true) {
                    println(" [ok]")
                } else {
                    println(" [err]")
                    errors.add(word)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        if (errors.isNotEmpty()) {
            error("${errors.size} Error(s)")
        }
    }
}