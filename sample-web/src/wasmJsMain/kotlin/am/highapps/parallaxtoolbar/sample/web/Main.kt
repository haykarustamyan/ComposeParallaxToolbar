package am.highapps.parallaxtoolbar.sample.web

import am.highapps.parallaxtoolbar.sample.SampleApp
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import kotlinx.browser.window

/** Web host for the shared playground. Add `?screen=lazyPadding` to open a fixed sample. */
@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    val screen = window.location.search
        .removePrefix("?")
        .split("&")
        .firstOrNull { it.startsWith("screen=") }
        ?.substringAfter("=")
        ?: "playground"
    ComposeViewport { SampleApp(screen) }
}
