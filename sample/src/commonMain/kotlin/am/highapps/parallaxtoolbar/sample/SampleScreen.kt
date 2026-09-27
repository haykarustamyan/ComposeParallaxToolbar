package am.highapps.parallaxtoolbar.sample

import androidx.compose.runtime.Composable

/** A fixed sample screen: its launch name, a short title and what it demonstrates. */
data class SampleScreenInfo(val name: String, val title: String, val description: String)

/** The fixed sample screens, in the order the playground lists them. */
val sampleScreens = listOf(
    SampleScreenInfo("simple", "Simple", "A regular column body with every default."),
    SampleScreenInfo("lazy", "Lazy list", "A LazyColumn body."),
    SampleScreenInfo("lazyPadding", "Lazy with padding", "Content padding merged into the list's own."),
    SampleScreenInfo("lazyReversed", "Reversed list", "reverseLayout through LazyColumnConfig."),
    SampleScreenInfo("lazyCentered", "Centered items", "horizontalAlignment through LazyColumnConfig."),
    SampleScreenInfo("lazySpacing", "Item spacing", "verticalArrangement.spacedBy through LazyColumnConfig."),
    SampleScreenInfo("lazyScrollControl", "Scroll control", "Buttons that scroll the list and move the header."),
    SampleScreenInfo("scaffold", "Inside Scaffold", "Scaffold padding passed as contentPadding under a bottom bar."),
    SampleScreenInfo("aspectRatio", "Aspect ratio header", "HeaderHeight.AspectRatio follows the width."),
    SampleScreenInfo("percentage", "Percentage header", "HeaderHeight.Percentage follows the height."),
    SampleScreenInfo("square", "Square header", "A 1:1 header."),
    SampleScreenInfo("compact", "Compact header", "A short fixed header."),
    SampleScreenInfo("ultrawide", "Ultrawide header", "A 21:9 header."),
)

/** Names accepted by [SampleApp]: the playground plus every fixed screen. */
val sampleScreenNames: List<String> = listOf("playground") + sampleScreens.map { it.name }

/** Shared entry point: the interactive playground, or one of the fixed sample screens by name. */
@Composable
fun SampleApp(screen: String = "playground", preset: String = "default") {
    PlaygroundHost(initialScreen = screen, preset = preset)
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
