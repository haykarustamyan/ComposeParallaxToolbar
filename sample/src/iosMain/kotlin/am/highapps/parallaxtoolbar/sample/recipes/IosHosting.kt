package am.highapps.parallaxtoolbar.sample.recipes

import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.window.ComposeUIViewController
import platform.UIKit.UIViewController

// recipe: iOS hosting
// Expose a screen to Swift from your shared module's iosMain. Swift wraps it in
// UIViewControllerRepresentable and applies ignoresSafeArea() so the header reaches the top edge.
fun PlaylistViewController(items: List<String>): UIViewController = ComposeUIViewController {
    MaterialTheme { BasicListScreen(items = items, onBack = {}) }
}
// end recipe
