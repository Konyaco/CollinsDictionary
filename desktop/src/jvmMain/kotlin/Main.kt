import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.*
import me.konyaco.collinsdictionary.commonModule
import me.konyaco.collinsdictionary.ui.App
import me.konyaco.collinsdictionary.ui.MyTheme
import me.konyaco.collinsdictionary.viewmodel.AppViewModel
import org.koin.core.context.startKoin
import org.koin.java.KoinJavaComponent

suspend fun main(args: Array<String>) {
    startKoin {
        modules(commonModule)
    }
    System.setProperty("java.net.useSystemProxies", "true")
    if (args.getOrNull(0) == "cli") cli()
    else gui()
}

fun gui() {
    val viewModel by KoinJavaComponent.getKoin().inject<AppViewModel>()
    application {
        val icon = painterResource("icon.png")
        var display by remember { mutableStateOf(true) }
        var isPinned by remember { mutableStateOf(false) }

        Tray(icon,
            onAction = { display = true },
            tooltip = "Collins Dictionary",
            menu = {
                CheckboxItem("Always on Top", isPinned, onCheckedChange = {
                    isPinned = it
                })
                Item("Close", onClick = {
                    exitApplication()
                })
            }
        )

        if (display) {
            Window(
                onCloseRequest = {
                    display = false
                    System.gc()
                },
                title = "Collins Dictionary",
                icon = icon,
                state = rememberWindowState(
                    width = 420.dp, height = 800.dp,
                    position = WindowPosition(Alignment.CenterEnd)
                ),
                alwaysOnTop = isPinned
            ) {
                LaunchedEffect(Unit) {
                    System.gc()
                }

                MyTheme {
                    App(viewModel)
                }
            }
        }
    }
}