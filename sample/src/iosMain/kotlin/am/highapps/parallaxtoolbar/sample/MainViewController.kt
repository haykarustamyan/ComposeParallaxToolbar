package am.highapps.parallaxtoolbar.sample

import androidx.compose.ui.window.ComposeUIViewController
import platform.UIKit.UIViewController

/** Called from Swift. Pass a sample name to open a fixed screen, or a playground preset name. */
fun MainViewController(screen: String = "playground", preset: String = "default"): UIViewController =
    ComposeUIViewController { SampleApp(screen, preset) }
