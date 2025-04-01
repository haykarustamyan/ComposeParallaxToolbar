package am.highapps.parallaxtoolbar

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.ComposeUIViewController

/**
 * Creates a UIViewController with the SimpleParallaxToolbarScreen content.
 * This function is called from Swift to display the parallax toolbar.
 */
fun SimpleParallaxToolbarViewController() = ComposeUIViewController {
    MaterialTheme {
        SimpleParallaxToolbarScreen()
    }
}

/**
 * Creates a UIViewController with the SampleParallaxToolbarScreen content.
 * This provides a more customized example of the parallax toolbar for iOS.
 */
fun CustomParallaxToolbarViewController() = ComposeUIViewController {
    MaterialTheme {
        SampleParallaxToolbarScreen()
    }
}

/**
 * Creates a UIViewController with the MinimalParallaxToolbarScreen content.
 * This provides a minimal example with only required parameters.
 */
fun MinimalParallaxToolbarViewController() = ComposeUIViewController {
    MaterialTheme {
        MinimalParallaxToolbarScreen()
    }
}

/**
 * Creates a UIViewController with the InitiallyCollapsedToolbarScreen content.
 * This example shows a toolbar that starts in a collapsed state.
 */
fun InitiallyCollapsedToolbarViewController() = ComposeUIViewController {
    MaterialTheme {
        InitiallyCollapsedToolbarScreen()
    }
}

/**
 * Creates a UIViewController with all sample screens organized in a container.
 * This is the main entry point for viewing all samples in iOS.
 */
fun AllSamplesViewController() = ComposeUIViewController {
    MaterialTheme {
        // Sample contains all examples to easily switch between them
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            // By default, show the custom sample
            SampleParallaxToolbarScreen()
            
            // Other samples can be switched by updating the code:
            // SimpleParallaxToolbarScreen()
            // MinimalParallaxToolbarScreen()
            // InitiallyCollapsedToolbarScreen()
        }
    }
} 