package am.highapps.parallaxtoolbar

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.swipeUp
import androidx.compose.ui.test.v2.runComposeUiTest
import androidx.compose.ui.unit.dp
import kotlin.test.Test

@OptIn(ExperimentalTestApi::class)
class ComposeParallaxToolbarLayoutTest : UiTestBase() {

    @Composable
    private fun Title(collapsed: Boolean) {
        Text(if (collapsed) "collapsed" else "expanded", modifier = Modifier.testTag("title"))
    }

    @Composable
    private fun Header() {
        Box(Modifier.fillMaxSize().background(Color.Red).testTag("header"))
    }

    @Test
    fun regularContent_startsExpanded_andCollapsesAfterScroll() = runComposeUiTest {
        setContent {
            ComposeParallaxToolbarLayout(
                titleContent = { Title(it) },
                headerContent = { Header() },
                navigationIcon = {
                    IconButton(onClick = {}, modifier = Modifier.testTag("nav")) { Text("<") }
                },
                content = ParallaxContent.Regular { _ ->
                    Column(Modifier.testTag("body")) {
                        repeat(60) { i ->
                            Text("Row $i", modifier = Modifier.fillMaxWidth().height(48.dp))
                        }
                    }
                },
                scrollState = rememberScrollState()
            )
        }

        onNodeWithTag("title").assertIsDisplayed()
        onNodeWithText("expanded").assertIsDisplayed()
        onNodeWithTag("header").assertIsDisplayed()
        onNodeWithTag("nav").assertIsDisplayed()
        onNodeWithText("Row 0").assertIsDisplayed()

        // Scroll well past the collapse range (default header 450dp, toolbar 64dp).
        repeat(6) { onNodeWithTag("body").performTouchInput { swipeUp() } }
        waitForIdle()

        onNodeWithText("collapsed").assertIsDisplayed()
        onNodeWithTag("nav").assertIsDisplayed()
    }

    @Test
    fun lazyContent_startsExpanded_andCollapsesAfterScroll() = runComposeUiTest {
        setContent {
            val listState = rememberLazyListState()
            ComposeParallaxToolbarLayout(
                titleContent = { Title(it) },
                headerContent = { Header() },
                subtitleContent = { Text("subtitle") },
                content = ParallaxContent.Lazy(
                    content = { _ ->
                        items(200) { i ->
                            Text("Row $i", modifier = Modifier.fillMaxWidth().height(48.dp))
                        }
                    },
                    lazyListState = listState
                ),
                modifier = Modifier.testTag("layout")
            )
        }

        onNodeWithText("expanded").assertIsDisplayed()
        onNodeWithText("subtitle").assertIsDisplayed()
        onNodeWithText("Row 0").assertIsDisplayed()

        // Six swipes reach the end of the list, where the first visible item offset is exactly 0.
        repeat(6) { onNodeWithTag("layout").performTouchInput { swipeUp() } }
        waitForIdle()

        onNodeWithText("collapsed").assertIsDisplayed()
    }

    @Test
    fun startsCollapsed_whenIsExpandedWhenFirstDisplayedIsFalse() = runComposeUiTest {
        setContent {
            ComposeParallaxToolbarLayout(
                titleContent = { Title(it) },
                headerContent = { Header() },
                headerConfig = ParallaxToolbarDefaults.headerConfig(isExpandedWhenFirstDisplayed = false),
                content = ParallaxContent.Regular { _ ->
                    Column {
                        repeat(60) { i ->
                            Text("Row $i", modifier = Modifier.fillMaxWidth().height(48.dp))
                        }
                    }
                }
            )
        }
        waitForIdle()
        onNodeWithText("collapsed").assertIsDisplayed()
    }

    @Test
    fun headerHeightVariants_render() = runComposeUiTest {
        setContent {
            Column(Modifier.fillMaxSize()) {
                ComposeParallaxToolbarLayout(
                    titleContent = { Text("aspect") },
                    headerContent = { Header() },
                    headerConfig = ParallaxToolbarDefaults.headerConfigWithAspectRatio(16f / 9f),
                    content = ParallaxContent.Regular { Text("a-body") },
                    modifier = Modifier.height(300.dp)
                )
                ComposeParallaxToolbarLayout(
                    titleContent = { Text("percent") },
                    headerContent = { Header() },
                    headerConfig = ParallaxToolbarDefaults.headerConfigWithPercentage(0.4f),
                    content = ParallaxContent.Regular { Text("p-body") },
                    modifier = Modifier.height(300.dp)
                )
            }
        }
        onNodeWithText("aspect").assertIsDisplayed()
        onNodeWithText("percent").assertIsDisplayed()
    }
}

@OptIn(ExperimentalTestApi::class)
class ComposeParallaxToolbarConfigTest : UiTestBase() {

    @Composable
    private fun Title(collapsed: Boolean) {
        Text(if (collapsed) "collapsed" else "expanded")
    }

    @Composable
    private fun Rows(count: Int) {
        Column { repeat(count) { i -> Text("Row $i", modifier = Modifier.fillMaxWidth().height(48.dp)) } }
    }

    @Test
    fun keepSubtitleAfterCollapse_withNavigationIconActionsGradientAndToolbarConfig() = runComposeUiTest {
        setContent {
            ComposeParallaxToolbarLayout(
                titleContent = { Title(it) },
                headerContent = { Box(Modifier.fillMaxSize().background(Color.Blue)) },
                subtitleContent = { Text(if (it) "sub-collapsed" else "sub-expanded") },
                navigationIcon = {
                    IconButton(onClick = {}) { Text("<") }
                },
                actions = { Text("action") },
                headerConfig = ParallaxToolbarDefaults.headerConfig(
                    height = HeaderHeight.Fixed(300.dp),
                    gradient = androidx.compose.ui.graphics.Brush.verticalGradient(listOf(Color.Transparent, Color.Black))
                ),
                toolbarConfig = ParallaxToolbarDefaults.toolbarConfig(
                    initialColor = Color.Transparent,
                    targetColor = Color.Green,
                    elevation = 4.dp
                ),
                titleConfig = ParallaxToolbarDefaults.titleConfig(keepSubtitleAfterCollapse = true),
                bodyConfig = ParallaxToolbarDefaults.bodyConfig(minBottomSpacerHeight = 24.dp),
                content = ParallaxContent.Regular { Rows(60) },
                modifier = Modifier.testTag("layout")
            )
        }
        onNodeWithText("expanded").assertIsDisplayed()
        onNodeWithText("sub-expanded").assertIsDisplayed()
        onNodeWithText("action").assertIsDisplayed()

        repeat(4) { onNodeWithTag("layout").performTouchInput { swipeUp() } }
        waitForIdle()

        onNodeWithText("collapsed").assertIsDisplayed()
        onNodeWithText("sub-collapsed").assertIsDisplayed()
        onNodeWithText("action").assertIsDisplayed()
    }

    @Test
    fun subtitleHidesWithoutAnimation_whenAnimateSubTitleHidingIsFalse() = runComposeUiTest {
        setContent {
            ComposeParallaxToolbarLayout(
                titleContent = { Title(it) },
                headerContent = { Box(Modifier.fillMaxSize().background(Color.Blue)) },
                subtitleContent = { Text("subtitle") },
                titleConfig = ParallaxToolbarDefaults.titleConfig(animateSubTitleHiding = false),
                content = ParallaxContent.Regular { Rows(60) },
                modifier = Modifier.testTag("layout")
            )
        }
        onNodeWithText("subtitle").assertIsDisplayed()
        repeat(4) { onNodeWithTag("layout").performTouchInput { swipeUp() } }
        waitForIdle()
        onNodeWithText("collapsed").assertIsDisplayed()
    }

    @Test
    fun regularContent_respectsContentPadding_andShortContentGetsBottomSpacer() = runComposeUiTest {
        setContent {
            ComposeParallaxToolbarLayout(
                titleContent = { Title(it) },
                headerContent = { Box(Modifier.fillMaxSize().background(Color.Blue)) },
                headerConfig = ParallaxToolbarDefaults.headerConfig(height = HeaderHeight.Fixed(120.dp)),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(bottom = 80.dp),
                content = ParallaxContent.Regular { Rows(2) }
            )
        }
        onNodeWithText("Row 0").assertIsDisplayed()
        onNodeWithText("Row 1").assertIsDisplayed()
    }

    @Test
    fun lazyContent_withLazyColumnConfigAndContentPadding_composesTrailingSpacer() = runComposeUiTest {
        setContent {
            ComposeParallaxToolbarLayout(
                titleContent = { Title(it) },
                headerContent = { Box(Modifier.fillMaxSize().background(Color.Blue)) },
                headerConfig = ParallaxToolbarDefaults.headerConfig(height = HeaderHeight.Fixed(120.dp)),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(
                    start = 4.dp, top = 4.dp, end = 4.dp, bottom = 40.dp
                ),
                bodyConfig = ParallaxToolbarDefaults.bodyConfig(minBottomSpacerHeight = 16.dp),
                content = ParallaxContent.Lazy(
                    content = { _ -> items(3) { i -> Text("Row $i") } },
                    config = ParallaxToolbarDefaults.lazyColumnConfig(
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(8.dp),
                        verticalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(4.dp),
                        horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally,
                        userScrollEnabled = true
                    )
                )
            )
        }
        onNodeWithText("Row 0").assertIsDisplayed()
        onNodeWithText("Row 2").assertIsDisplayed()
    }

    @Test
    fun legacyOverload_withLazyContent_stillWorks() = runComposeUiTest {
        setContent {
            @Suppress("DEPRECATION")
            ComposeParallaxToolbarLayout(
                titleContent = { Title(it) },
                headerContent = { Box(Modifier.fillMaxSize().background(Color.Blue)) },
                content = { _ -> },
                lazyContent = { _ -> items(50) { i -> Text("Row $i", modifier = Modifier.fillMaxWidth().height(48.dp)) } },
                modifier = Modifier.testTag("layout")
            )
        }
        onNodeWithText("Row 0").assertIsDisplayed()
        repeat(4) { onNodeWithTag("layout").performTouchInput { swipeUp() } }
        waitForIdle()
        onNodeWithText("collapsed").assertIsDisplayed()
    }

    @Test
    fun legacyOverload_withRegularContent_usesAllDefaults() = runComposeUiTest {
        setContent {
            @Suppress("DEPRECATION")
            ComposeParallaxToolbarLayout(
                titleContent = { Title(it) },
                headerContent = { Box(Modifier.fillMaxSize().background(Color.Blue)) },
                content = { _ -> Rows(5) }
            )
        }
        onNodeWithText("expanded").assertIsDisplayed()
        onNodeWithText("Row 0").assertIsDisplayed()
    }
}

@OptIn(ExperimentalTestApi::class)
class ComposeParallaxToolbarTitleTest : UiTestBase() {

    @Test
    fun keepSubtitleAfterCollapse_withoutNavigationIcon_keepsStartPadding() = runComposeUiTest {
        setContent {
            ComposeParallaxToolbarLayout(
                titleContent = { Text(if (it) "collapsed" else "expanded") },
                headerContent = { Box(Modifier.fillMaxSize().background(Color.Blue)) },
                subtitleContent = { Text("subtitle") },
                headerConfig = ParallaxToolbarDefaults.headerConfig(height = HeaderHeight.Fixed(200.dp)),
                titleConfig = ParallaxToolbarDefaults.titleConfig(keepSubtitleAfterCollapse = true),
                content = ParallaxContent.Regular {
                    Column { repeat(60) { i -> Text("Row $i", modifier = Modifier.fillMaxWidth().height(48.dp)) } }
                },
                modifier = Modifier.testTag("layout")
            )
        }
        onNodeWithText("subtitle").assertIsDisplayed()
        repeat(4) { onNodeWithTag("layout").performTouchInput { swipeUp() } }
        waitForIdle()
        onNodeWithText("collapsed").assertIsDisplayed()
        onNodeWithText("subtitle").assertIsDisplayed()
    }
}

@OptIn(ExperimentalTestApi::class)
class ComposeParallaxToolbarRecompositionTest : UiTestBase() {

    /** A parent recomposition with unchanged parameters must let both overloads skip cleanly. */
    @Test
    fun parentRecomposition_withUnchangedParameters_isSkipped() = runComposeUiTest {
        var tick by androidx.compose.runtime.mutableStateOf(0)
        setContent {
            Column(Modifier.fillMaxSize()) {
                Text("tick $tick")
                ComposeParallaxToolbarLayout(
                    titleContent = { Text("unified") },
                    headerContent = { Box(Modifier.fillMaxSize().background(Color.Blue)) },
                    headerConfig = ParallaxToolbarDefaults.headerConfig(height = HeaderHeight.Fixed(100.dp)),
                    content = ParallaxContent.Regular { Text("u-body") },
                    modifier = Modifier.height(200.dp)
                )
                @Suppress("DEPRECATION")
                ComposeParallaxToolbarLayout(
                    titleContent = { Text("legacy") },
                    headerContent = { Box(Modifier.fillMaxSize().background(Color.Blue)) },
                    headerConfig = ParallaxToolbarDefaults.headerConfig(height = HeaderHeight.Fixed(100.dp)),
                    content = { Text("l-body") },
                    modifier = Modifier.height(200.dp)
                )
            }
        }
        onNodeWithText("tick 0").assertIsDisplayed()
        tick = 1
        waitForIdle()
        tick = 2
        waitForIdle()
        onNodeWithText("tick 2").assertIsDisplayed()
        onNodeWithText("unified").assertIsDisplayed()
        onNodeWithText("legacy").assertIsDisplayed()
    }
}
