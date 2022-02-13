import androidx.compose.ui.res.painterResource
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import me.konyaco.collinsdictionary.repository.Repository
import me.konyaco.collinsdictionary.service.CollinsOnlineDictionary
import me.konyaco.collinsdictionary.service.LocalCacheDictionary
import me.konyaco.collinsdictionary.store.FileBasedLocalStorage
import me.konyaco.collinsdictionary.ui.App
import me.konyaco.collinsdictionary.ui.MyTheme
import me.konyaco.collinsdictionary.viewmodel.AppViewModel
import java.io.File

suspend fun main(args: Array<String>) {
    if (args.getOrNull(0) == "cli") cli()
    else gui()
}

fun Repository(): Repository {
    return Repository(
        CollinsOnlineDictionary(),
        LocalCacheDictionary(
            FileBasedLocalStorage(
                File("cache").also {
                    if (!it.exists()) it.mkdir()
                }
            )
        )
    )
}

fun gui() {
    val viewModel = AppViewModel(Repository())
    application {
        Window(
            onCloseRequest = ::exitApplication,
            title = "Collins Dictionary",
            icon = painterResource("icon.png")
        ) {
            MyTheme {
                App(viewModel)
            }
        }
    }
}