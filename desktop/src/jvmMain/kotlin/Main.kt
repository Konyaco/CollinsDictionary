import androidx.compose.desktop.DesktopTheme
import androidx.compose.runtime.remember
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import me.konyaco.collinsdictionary.App
import me.konyaco.collinsdictionary.AppComponent
import me.konyaco.collinsdictionary.MyTheme

fun main(args: Array<String>) {
    if (args.getOrNull(0) == "cli") cli()
    else gui()
}

fun gui() = application {
    Window(onCloseRequest = ::exitApplication, title = "Collins Dictionary", icon = painterResource("icon.png")) {
        MyTheme {
            DesktopTheme {
                App(remember { AppComponent() })
            }
        }
    }
}