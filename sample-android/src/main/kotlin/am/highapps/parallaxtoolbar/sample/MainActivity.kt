package am.highapps.parallaxtoolbar.sample

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import am.highapps.parallaxtoolbar.AspectRatioHeaderSample
import am.highapps.parallaxtoolbar.CompactHeaderSample
import am.highapps.parallaxtoolbar.LazyParallaxToolbarCenteredScreen
import am.highapps.parallaxtoolbar.LazyParallaxToolbarReversedScreen
import am.highapps.parallaxtoolbar.LazyParallaxToolbarScreen
import am.highapps.parallaxtoolbar.LazyParallaxToolbarWithPaddingScreen
import am.highapps.parallaxtoolbar.LazyParallaxToolbarWithScrollControlScreen
import am.highapps.parallaxtoolbar.LazyParallaxToolbarWithSpacingScreen
import am.highapps.parallaxtoolbar.ParallaxToolbarInScaffoldScreen
import am.highapps.parallaxtoolbar.PercentageHeaderSample
import am.highapps.parallaxtoolbar.SimpleParallaxToolbarScreen
import am.highapps.parallaxtoolbar.SquareHeaderSample
import am.highapps.parallaxtoolbar.UltrawideHeaderSample

/**
 * Opens the interactive playground by default. A fixed sample screen can be launched with:
 *   adb shell am start -n am.highapps.parallaxtoolbar.sample/.MainActivity --es screen <name>
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val screen = intent.getStringExtra("screen") ?: "playground"
        setContent {
            MaterialTheme {
                SampleScreen(screen)
            }
        }
    }
}

@Composable
fun SampleScreen(name: String) {
    when (name) {
        "simple" -> SimpleParallaxToolbarScreen()
        "lazy" -> LazyParallaxToolbarScreen()
        "lazyPadding" -> LazyParallaxToolbarWithPaddingScreen()
        "lazyReversed" -> LazyParallaxToolbarReversedScreen()
        "lazyCentered" -> LazyParallaxToolbarCenteredScreen()
        "lazySpacing" -> LazyParallaxToolbarWithSpacingScreen()
        "lazyScrollControl" -> LazyParallaxToolbarWithScrollControlScreen()
        "scaffold" -> ParallaxToolbarInScaffoldScreen()
        "aspectRatio" -> AspectRatioHeaderSample()
        "percentage" -> PercentageHeaderSample()
        "square" -> SquareHeaderSample()
        "compact" -> CompactHeaderSample()
        "ultrawide" -> UltrawideHeaderSample()
        else -> PlaygroundScreen()
    }
}
