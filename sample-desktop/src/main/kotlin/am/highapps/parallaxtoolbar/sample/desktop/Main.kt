package am.highapps.parallaxtoolbar.sample.desktop

import am.highapps.parallaxtoolbar.sample.SampleApp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.application

/** Desktop host for the shared playground. Arguments: [screen] [preset]. */
fun main(args: Array<String>) = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "Compose Parallax Toolbar",
        state = WindowState(size = DpSize(480.dp, 900.dp)),
    ) {
        SampleApp(args.getOrNull(0) ?: "playground", args.getOrNull(1) ?: "default")
    }
}
