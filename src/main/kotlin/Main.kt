import androidx.compose.runtime.remember
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application

fun main(args: Array<String>) {
    if (args.getOrNull(0) == "cli") cli()
    else gui()
}

fun gui() = application {
    Window(onCloseRequest = ::exitApplication, title = "Collins Dictionary", icon = painterResource("icon.png")) {
        App(remember { AppComponent() })
    }
}