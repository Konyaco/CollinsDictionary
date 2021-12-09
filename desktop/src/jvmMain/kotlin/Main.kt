import androidx.compose.ui.res.painterResource
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import me.konyaco.collinsdictionary.ui.App
import me.konyaco.collinsdictionary.ui.MyTheme
import me.konyaco.collinsdictionary.viewmodel.AppViewModel

fun main(args: Array<String>) {
    if (args.getOrNull(0) == "cli") cli()
    else gui()
}

fun gui() {
    val viewModel = AppViewModel()
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