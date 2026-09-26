package am.highapps.parallaxtoolbar.sample

import androidx.compose.ui.window.ComposeUIViewController
import platform.UIKit.UIViewController

/** Called from Swift. Pass a sample name to open a fixed screen instead of the playground. */
fun MainViewController(screen: String = "playground"): UIViewController =
    ComposeUIViewController { SampleApp(screen) }
