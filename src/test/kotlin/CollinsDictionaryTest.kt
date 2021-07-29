import org.junit.jupiter.api.Test
import service.CollinsOnlineDictionary

internal class CollinsDictionaryTest {
    @Test
    fun test() {
        println(CollinsOnlineDictionary().getDefinition("welcome"))
    }
}