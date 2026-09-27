package am.highapps.parallaxtoolbar.sample.web

import am.highapps.parallaxtoolbar.sample.SampleApp
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import kotlinx.browser.window

/** Web host for the shared playground. Query: `?screen=lazyPadding` or `?preset=grid-avatar`. */
@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    val params = window.location.search.removePrefix("?").split("&")
        .mapNotNull { it.split("=", limit = 2).takeIf { p -> p.size == 2 } }
        .associate { it[0] to it[1] }
    ComposeViewport { SampleApp(params["screen"] ?: "playground", params["preset"] ?: "default") }
}
