package am.highapps.parallaxtoolbar.sample

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
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable

/** Shared entry point: the interactive playground, or one of the fixed sample screens by name. */
@Composable
fun SampleApp(screen: String = "playground") {
    MaterialTheme {
        when (screen) {
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
}
