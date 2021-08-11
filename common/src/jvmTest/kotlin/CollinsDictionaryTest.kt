import me.konyaco.collinsdictionary.service.CollinsOnlineDictionary
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
}