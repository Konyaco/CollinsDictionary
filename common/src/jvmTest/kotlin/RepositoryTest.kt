import kotlinx.coroutines.runBlocking
import me.konyaco.collinsdictionary.repository.Repository
import me.konyaco.collinsdictionary.service.CollinsOnlineDictionary
import me.konyaco.collinsdictionary.service.LocalCacheDictionary
import me.konyaco.collinsdictionary.store.FileBasedLocalStorage
import java.io.File
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test

class RepositoryTest {
    lateinit var repository: Repository

    @BeforeTest
    fun setup() {
        repository = Repository(
            CollinsOnlineDictionary(),
            LocalCacheDictionary(FileBasedLocalStorage(File("test").also { it.mkdir() }))
        )
    }

    @AfterTest
    fun cleanup() {
        File("test").deleteRecursively()
    }

    @Test
    fun testCache() = runBlocking {
        repository.search("")
    }
}