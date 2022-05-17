import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.produce
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import me.konyaco.collinsdictionary.service.CollinsOnlineDictionary
import me.konyaco.collinsdictionary.service.SearchResult
import java.io.File
import kotlin.test.Test
import kotlin.test.assertIs

internal class CollinsDictionaryTest {
    @Test
    fun testOne(): Unit = runBlocking {
        CollinsOnlineDictionary().getDefinition("hello")
    }

    @Test
    fun `Test a bundle of word in word list, and count failures`() = runBlocking {
        val dict = CollinsOnlineDictionary()
        val errChannel = Channel<Pair<String, Exception>>()
        val channel = produce {
            getTestWordList().forEach { send(it) }
        }
        repeat(12) {
            launch {
                for(word in channel) {
                    try {
                        println("Testing: $word")
                        dict.search(word)
                        dict.getDefinition(word)
                    } catch (e: Exception) {
                        errChannel.send(word to e)
                        e.printStackTrace()
                    }
                }
            }
        }
        val errors = errChannel.consumeAsFlow().toList()
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
    fun `Test three result type of search()`(): Unit = runBlocking {
        val dict = CollinsOnlineDictionary()
        assertIs<SearchResult.NotFound>(dict.search("interchangebly"))
        assertIs<SearchResult.Redirect>(dict.search("interchangeably"))
        assertIs<SearchResult.PreciseWord>(dict.search("interchangeable"))
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