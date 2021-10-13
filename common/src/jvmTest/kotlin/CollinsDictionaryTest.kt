import me.konyaco.collinsdictionary.service.CollinsOnlineDictionary
import me.konyaco.collinsdictionary.service.SearchResult
import kotlin.test.Test

internal class CollinsDictionaryTest {
    @Test
    fun test() {
        println(CollinsOnlineDictionary().getDefinition("take"))
    }

    @Test
    fun testWordNotFound() {
        assert(CollinsOnlineDictionary().getDefinition("wfnigjei") == null)
    }

    @Test
    fun testSearch() {
        assert(CollinsOnlineDictionary().search("interchangebly").also(::println) is SearchResult.NotFound)
        assert(CollinsOnlineDictionary().search("interchangeably").also(::println) is SearchResult.Redirect)
        assert(CollinsOnlineDictionary().search("interchangeable").also(::println) is SearchResult.PreciseWord)
    }
}