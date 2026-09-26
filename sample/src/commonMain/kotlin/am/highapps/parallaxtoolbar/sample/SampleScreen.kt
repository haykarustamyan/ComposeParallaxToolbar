package am.highapps.parallaxtoolbar.sample

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable

/** Names accepted by [SampleApp] and listed in the playground's screen picker. */
val sampleScreenNames = listOf(
    "playground", "simple", "lazy", "lazyPadding", "lazyReversed", "lazyCentered", "lazySpacing",
    "lazyScrollControl", "scaffold", "aspectRatio", "percentage", "square", "compact", "ultrawide"
)

/** Shared entry point: the interactive playground, or one of the fixed sample screens by name. */
@Composable
fun SampleApp(screen: String = "playground") {
    MaterialTheme {
        PlaygroundHost(initialScreen = screen)
    }
}

/** Renders one fixed sample screen by name. */
@Composable
fun FixedSampleScreen(screen: String) {
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
            else -> SimpleParallaxToolbarScreen()
        }
}
