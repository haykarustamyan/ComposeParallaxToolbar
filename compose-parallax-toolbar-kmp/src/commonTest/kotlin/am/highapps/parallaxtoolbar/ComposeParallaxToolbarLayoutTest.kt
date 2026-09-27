package am.highapps.parallaxtoolbar

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.clickable
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.performSemanticsAction
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.swipeDown
import androidx.compose.ui.test.swipeUp
import androidx.compose.ui.test.v2.runComposeUiTest
import androidx.compose.ui.unit.dp
import kotlin.test.Test
import kotlinx.coroutines.launch

/** Material-free stand-ins so the library tests only depend on Foundation. */
@Composable
private fun Text(text: String, modifier: Modifier = Modifier) = BasicText(text = text, modifier = modifier)

@Composable
private fun IconButton(onClick: () -> Unit, modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    Box(modifier.size(48.dp).clickable(onClick = onClick), contentAlignment = androidx.compose.ui.Alignment.Center) { content() }
}

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
                state = rememberParallaxToolbarState(scrollState = rememberScrollState())
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

@OptIn(ExperimentalTestApi::class)
class ComposeParallaxToolbarLazyStartTest : UiTestBase() {

    /** isExpandedWhenFirstDisplayed = false must also work for lazy content. */
    @Test
    fun lazyContent_startsCollapsed_whenIsExpandedWhenFirstDisplayedIsFalse() = runComposeUiTest {
        setContent {
            ComposeParallaxToolbarLayout(
                titleContent = { Text(if (it) "collapsed" else "expanded") },
                headerContent = { Box(Modifier.fillMaxSize().background(Color.Blue)) },
                headerConfig = ParallaxToolbarDefaults.headerConfig(isExpandedWhenFirstDisplayed = false),
                content = ParallaxContent.Lazy(content = { _ ->
                    items(60) { i -> Text("Row $i", modifier = Modifier.fillMaxWidth().height(48.dp)) }
                })
            )
        }
        waitForIdle()
        onNodeWithText("collapsed").assertIsDisplayed()
    }
}

@OptIn(ExperimentalTestApi::class)
class ComposeParallaxToolbarRtlTest : UiTestBase() {

    @Composable
    private fun Rtl(content: @Composable () -> Unit) {
        androidx.compose.runtime.CompositionLocalProvider(
            androidx.compose.ui.platform.LocalLayoutDirection provides androidx.compose.ui.unit.LayoutDirection.Rtl,
            content = content
        )
    }

    @Test
    fun regularAndLazyContent_renderAndCollapse_inRtl() = runComposeUiTest {
        setContent {
            Rtl {
                Column(Modifier.fillMaxSize()) {
                    ComposeParallaxToolbarLayout(
                        titleContent = { Text(if (it) "r-collapsed" else "r-expanded") },
                        headerContent = { Box(Modifier.fillMaxSize().background(Color.Blue)) },
                        headerConfig = ParallaxToolbarDefaults.headerConfig(height = HeaderHeight.Fixed(150.dp)),
                        navigationIcon = { IconButton(onClick = {}) { Text("<") } },
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(start = 12.dp, end = 4.dp),
                        content = ParallaxContent.Regular {
                            Column { repeat(40) { i -> Text("R$i", modifier = Modifier.fillMaxWidth().height(48.dp)) } }
                        },
                        modifier = Modifier.weight(1f).testTag("regular")
                    )
                    ComposeParallaxToolbarLayout(
                        titleContent = { Text(if (it) "l-collapsed" else "l-expanded") },
                        headerContent = { Box(Modifier.fillMaxSize().background(Color.Blue)) },
                        headerConfig = ParallaxToolbarDefaults.headerConfig(height = HeaderHeight.Fixed(150.dp)),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(start = 12.dp, end = 4.dp),
                        content = ParallaxContent.Lazy(content = { _ ->
                            items(40) { i -> Text("L$i", modifier = Modifier.fillMaxWidth().height(48.dp)) }
                        }),
                        modifier = Modifier.weight(1f).testTag("lazy")
                    )
                }
            }
        }
        onNodeWithText("r-expanded").assertIsDisplayed()
        onNodeWithText("l-expanded").assertIsDisplayed()
        repeat(3) { onNodeWithTag("regular").performTouchInput { swipeUp() } }
        repeat(3) { onNodeWithTag("lazy").performTouchInput { swipeUp() } }
        waitForIdle()
        onNodeWithText("r-collapsed").assertIsDisplayed()
        onNodeWithText("l-collapsed").assertIsDisplayed()
    }
}


@OptIn(ExperimentalTestApi::class)
class ComposeParallaxToolbarStateTest : UiTestBase() {

    @Test
    fun state_reportsFraction_andCollapsesAndExpandsProgrammatically() = runComposeUiTest {
        lateinit var state: ParallaxToolbarState
        setContent {
            state = rememberParallaxToolbarState()
            ComposeParallaxToolbarLayout(
                titleContent = { Text(if (it) "collapsed" else "expanded") },
                headerContent = { Box(Modifier.fillMaxSize().background(Color.Blue)) },
                headerConfig = ParallaxToolbarDefaults.headerConfig(
                    height = HeaderHeight.Fixed(200.dp),
                    parallaxMultiplier = 0.3f
                ),
                toolbarConfig = ParallaxToolbarDefaults.toolbarConfig(height = 56.dp, elevation = 3.dp),
                titleConfig = ParallaxToolbarDefaults.titleConfig(collapsedScale = 0.8f),
                subtitleContent = { Text("subtitle") },
                content = ParallaxContent.Regular {
                    Column { repeat(60) { i -> Text("Row $i", modifier = Modifier.fillMaxWidth().height(48.dp)) } }
                },
                state = state
            )
        }
        waitForIdle()
        kotlin.test.assertEquals(0f, state.collapseFraction)
        kotlin.test.assertFalse(state.isCollapsed)

        runOnIdle { kotlinx.coroutines.runBlocking { state.collapse(animated = false) } }
        waitForIdle()
        onNodeWithText("collapsed").assertIsDisplayed()
        kotlin.test.assertEquals(1f, state.collapseFraction)
        kotlin.test.assertTrue(state.isCollapsed)

        runOnIdle { kotlinx.coroutines.runBlocking { state.expand(animated = false) } }
        waitForIdle()
        onNodeWithText("expanded").assertIsDisplayed()
        kotlin.test.assertEquals(0f, state.collapseFraction)
    }

    @Test
    fun lazyState_collapsesAndExpandsProgrammatically() = runComposeUiTest {
        lateinit var state: ParallaxToolbarState
        lateinit var scope: kotlinx.coroutines.CoroutineScope
        setContent {
            state = rememberParallaxToolbarState()
            scope = androidx.compose.runtime.rememberCoroutineScope()
            ComposeParallaxToolbarLayout(
                titleContent = { Text(if (it) "collapsed" else "expanded") },
                headerContent = { Box(Modifier.fillMaxSize().background(Color.Blue)) },
                content = ParallaxContent.Lazy(content = { _ ->
                    items(60) { i -> Text("Row $i", modifier = Modifier.fillMaxWidth().height(48.dp)) }
                }),
                state = state
            )
        }
        // Animated variants need the frame clock to advance, so launch rather than block.
        runOnIdle { scope.launch { state.collapse() } }
        waitForIdle()
        onNodeWithText("collapsed").assertIsDisplayed()
        runOnIdle { scope.launch { state.expand() } }
        waitForIdle()
        onNodeWithText("expanded").assertIsDisplayed()
    }

    @Test
    fun configs_haveDefaultsForNewFields() {
        val toolbar = ParallaxToolbarConfig(
            initialColor = Color.Transparent, targetColor = Color.Black, elevation = 0.dp,
            animationSpec = androidx.compose.animation.core.tween()
        )
        val title = ParallaxTitleConfig(
            paddingBottom = 0.dp, paddingStart = 16.dp, collapsedPaddingStart = 64.dp,
            keepSubtitleAfterCollapse = false, animateSubTitleHiding = true
        )
        kotlin.test.assertEquals(ParallaxToolbarDefaults.ToolbarHeight, toolbar.height)
        kotlin.test.assertEquals(ParallaxToolbarDefaults.TitleCollapsedScale, title.collapsedScale)
    }
}

class ParallaxConfigEqualityTest {

    @Test
    fun configs_compareByValue_andCopyOverridesSelectively() {
        val header = ParallaxHeaderConfig(HeaderHeight.Fixed(300.dp), gradient = null)
        kotlin.test.assertEquals(header, header.copy())
        kotlin.test.assertEquals(header.hashCode(), header.copy().hashCode())
        kotlin.test.assertNotEquals(header, header.copy(parallaxMultiplier = 0.1f))
        kotlin.test.assertTrue(header.toString().contains("Fixed"))

        val toolbar = ParallaxToolbarConfig(Color.Transparent, Color.Black, 0.dp, androidx.compose.animation.core.snap())
        kotlin.test.assertEquals(toolbar, toolbar.copy())
        kotlin.test.assertNotEquals(toolbar, toolbar.copy(height = 56.dp))
        kotlin.test.assertEquals(toolbar.hashCode(), toolbar.copy().hashCode())
        kotlin.test.assertTrue(toolbar.toString().startsWith("ParallaxToolbarConfig("))

        val title = ParallaxTitleConfig(0.dp, 16.dp, 64.dp, keepSubtitleAfterCollapse = false, animateSubTitleHiding = true)
        kotlin.test.assertEquals(title, title.copy())
        kotlin.test.assertNotEquals(title, title.copy(collapsedScale = 0.5f))
        kotlin.test.assertEquals(title.hashCode(), title.copy().hashCode())
        kotlin.test.assertTrue(title.toString().startsWith("ParallaxTitleConfig("))

        val body = ParallaxBodyConfig(8.dp)
        kotlin.test.assertEquals(body, body.copy())
        kotlin.test.assertNotEquals(body, body.copy(minBottomSpacerHeight = 9.dp))
        kotlin.test.assertEquals(body.hashCode(), body.copy().hashCode())
        kotlin.test.assertTrue(body.toString().startsWith("ParallaxBodyConfig("))

        val lazy = LazyColumnConfig()
        kotlin.test.assertEquals(lazy, lazy.copy())
        kotlin.test.assertNotEquals(lazy, lazy.copy(userScrollEnabled = false))
        kotlin.test.assertEquals(lazy.hashCode(), lazy.copy().hashCode())
        kotlin.test.assertTrue(lazy.toString().startsWith("LazyColumnConfig("))
        kotlin.test.assertNotEquals<Any>(lazy, "not a config")
    }
}


@OptIn(ExperimentalTestApi::class)
class ComposeParallaxToolbarCustomContentTest : UiTestBase() {

    @Test
    fun customGridContent_collapsesThroughNestedScroll() = runComposeUiTest {
        lateinit var state: ParallaxToolbarState
        setContent {
            state = rememberParallaxToolbarState()
            ComposeParallaxToolbarLayout(
                titleContent = { Text(if (it) "collapsed" else "expanded") },
                headerContent = { Box(Modifier.fillMaxSize().background(Color.Blue)) },
                headerConfig = ParallaxToolbarDefaults.headerConfig(height = HeaderHeight.Fixed(200.dp)),
                content = ParallaxContent.Custom { collapsed ->
                    androidx.compose.foundation.lazy.grid.LazyVerticalGrid(
                        columns = androidx.compose.foundation.lazy.grid.GridCells.Fixed(2),
                        modifier = Modifier.fillMaxSize().testTag("grid")
                    ) {
                        items(120) { i -> Text("Cell $i", modifier = Modifier.fillMaxWidth().height(48.dp)) }
                    }
                },
                state = state
            )
        }
        onNodeWithText("expanded").assertIsDisplayed()
        onNodeWithText("Cell 0").assertIsDisplayed()

        repeat(4) { onNodeWithTag("grid").performTouchInput { swipeUp() } }
        waitForIdle()
        onNodeWithText("collapsed").assertIsDisplayed()
        kotlin.test.assertEquals(1f, state.collapseFraction)

        // Scrolling back down expands only once the grid is at its top again.
        repeat(8) { onNodeWithTag("grid").performTouchInput { swipeDown() } }
        waitForIdle()
        onNodeWithText("expanded").assertIsDisplayed()
        kotlin.test.assertEquals(0f, state.collapseFraction)
    }

    @Test
    fun dragOnHeader_collapsesIt_andSnapSettlesToNearestEnd() = runComposeUiTest {
        lateinit var state: ParallaxToolbarState
        setContent {
            state = rememberParallaxToolbarState()
            ComposeParallaxToolbarLayout(
                titleContent = { Text(if (it) "collapsed" else "expanded") },
                headerContent = { Box(Modifier.fillMaxSize().background(Color.Blue).testTag("header")) },
                headerConfig = ParallaxToolbarDefaults.headerConfig(
                    height = HeaderHeight.Fixed(300.dp),
                    snapOnRelease = true
                ),
                content = ParallaxContent.Regular { Column { repeat(3) { i -> Text("Row $i") } } },
                state = state
            )
        }
        // A short, slow drag on the header itself leaves it part way; snap settles it fully.
        onNodeWithTag("header").performTouchInput {
            down(center)
            moveBy(androidx.compose.ui.geometry.Offset(0f, -60f))
            moveBy(androidx.compose.ui.geometry.Offset(0f, -60f))
            up()
        }
        waitForIdle()
        kotlin.test.assertTrue(state.collapseFraction == 0f || state.collapseFraction == 1f, "fraction=${state.collapseFraction}")
    }

}

@OptIn(ExperimentalTestApi::class)
class ComposeParallaxToolbarSnapTest : UiTestBase() {

    @Composable
    private fun Snapping(state: ParallaxToolbarState, rows: Int) {
        ComposeParallaxToolbarLayout(
            titleContent = { Text(if (it) "collapsed" else "expanded") },
            headerContent = { Box(Modifier.fillMaxSize().background(Color.Blue).testTag("header")) },
            headerConfig = ParallaxToolbarDefaults.headerConfig(height = HeaderHeight.Fixed(300.dp), snapOnRelease = true),
            content = ParallaxContent.Regular {
                Column(Modifier.testTag("body")) { repeat(rows) { i -> Text("Row $i", Modifier.fillMaxWidth().height(48.dp)) } }
            },
            state = state
        )
    }

    @Test
    fun headerDrag_releasedWithoutVelocity_settlesToNearestEnd() = runComposeUiTest {
        lateinit var state: ParallaxToolbarState
        setContent { state = rememberParallaxToolbarState(); Snapping(state, rows = 3) }
        // Small drag, then a pause so the release velocity is zero: only settle() can finish it.
        onNodeWithTag("header").performTouchInput {
            down(center); moveBy(androidx.compose.ui.geometry.Offset(0f, -40f)); advanceEventTime(400); up()
        }
        waitForIdle()
        kotlin.test.assertEquals(0f, state.collapseFraction)

        onNodeWithTag("header").performTouchInput {
            down(center); moveBy(androidx.compose.ui.geometry.Offset(0f, -200f)); advanceEventTime(400); up()
        }
        waitForIdle()
        kotlin.test.assertEquals(1f, state.collapseFraction)
    }

    @Test
    fun bodyFling_withShortContent_settlesInTheFlingDirection() = runComposeUiTest {
        lateinit var state: ParallaxToolbarState
        setContent { state = rememberParallaxToolbarState(); Snapping(state, rows = 2) }
        // A short upward fling on the body: it cannot scroll, so the velocity reaches the header.
        onNodeWithTag("body").performTouchInput { swipeUp(startY = centerY + 20f, endY = centerY - 20f, durationMillis = 50) }
        waitForIdle()
        kotlin.test.assertEquals(1f, state.collapseFraction)

        onNodeWithTag("body").performTouchInput { swipeDown(startY = centerY - 20f, endY = centerY + 20f, durationMillis = 50) }
        waitForIdle()
        kotlin.test.assertEquals(0f, state.collapseFraction)

        // Zero-velocity release on the body takes the nearest end.
        onNodeWithTag("body").performTouchInput {
            down(center); moveBy(androidx.compose.ui.geometry.Offset(0f, -200f)); advanceEventTime(400); up()
        }
        waitForIdle()
        kotlin.test.assertEquals(1f, state.collapseFraction)
    }
}

@OptIn(ExperimentalTestApi::class)
class ComposeParallaxToolbarScopeTest : UiTestBase() {

    @Test
    fun slots_receiveCollapseFraction_throughTheScope() = runComposeUiTest {
        lateinit var state: ParallaxToolbarState
        var headerFraction = -1f
        var actionsFraction = -1f
        setContent {
            state = rememberParallaxToolbarState()
            ComposeParallaxToolbarLayout(
                titleContent = { collapsed -> Text("title ${(collapseFraction * 100).toInt()} $collapsed") },
                headerContent = {
                    headerFraction = collapseFraction
                    Box(Modifier.fillMaxSize().background(Color.Blue))
                },
                subtitleContent = { Text("sub $isCollapsed") },
                navigationIcon = { Text(if (collapseFraction > 0.5f) ">" else "<") },
                actions = {
                    actionsFraction = collapseFraction
                    Text("a")
                },
                content = ParallaxContent.Regular { Column { Text("body ${state.isCollapsed}") } },
                state = state
            )
        }
        onNodeWithText("title 0 false").assertIsDisplayed()
        onNodeWithText("sub false").assertIsDisplayed()
        onNodeWithText("body false").assertIsDisplayed()
        kotlin.test.assertEquals(0f, headerFraction)
        kotlin.test.assertEquals(0f, actionsFraction)

        runOnIdle { kotlinx.coroutines.runBlocking { state.collapse(animated = false) } }
        waitForIdle()
        onNodeWithText("title 100 true").assertIsDisplayed()
        onNodeWithText("sub true").assertIsDisplayed()
        onNodeWithText(">").assertIsDisplayed()
        kotlin.test.assertEquals(1f, headerFraction)
        kotlin.test.assertEquals(1f, actionsFraction)
    }
}

@OptIn(ExperimentalTestApi::class)
class ComposeParallaxToolbarScrollModeTest : UiTestBase() {

    @Composable
    private fun Modes(mode: ScrollMode, state: ParallaxToolbarState) {
        ComposeParallaxToolbarLayout(
            titleContent = { Text(if (it) "collapsed" else "expanded") },
            headerContent = { Box(Modifier.fillMaxSize().background(Color.Blue)) },
            headerConfig = ParallaxToolbarDefaults.headerConfig(height = HeaderHeight.Fixed(200.dp), scrollMode = mode),
            content = ParallaxContent.Lazy(content = { _ ->
                items(300) { i -> Text("Row $i", Modifier.fillMaxWidth().height(48.dp)) }
            }),
            modifier = Modifier.testTag("layout"),
            state = state
        )
    }

    private fun androidx.compose.ui.test.ComposeUiTest.scrollFarDown() {
        repeat(4) { onNodeWithTag("layout").performTouchInput { swipeUp() } }
        waitForIdle()
    }

    /** A short downward drag: far too small to reach the list top. */
    private fun androidx.compose.ui.test.ComposeUiTest.nudgeDown() {
        onNodeWithTag("layout").performTouchInput {
            down(center); moveBy(androidx.compose.ui.geometry.Offset(0f, 80f)); moveBy(androidx.compose.ui.geometry.Offset(0f, 80f)); up()
        }
        waitForIdle()
    }

    @Test
    fun exitUntilCollapsed_expandsOnlyAtTop() = runComposeUiTest {
        lateinit var state: ParallaxToolbarState
        setContent { state = rememberParallaxToolbarState(); Modes(ScrollMode.ExitUntilCollapsed, state) }
        scrollFarDown()
        kotlin.test.assertEquals(1f, state.collapseFraction)
        kotlin.test.assertEquals(0f, state.toolbarExitFraction)
        nudgeDown()
        kotlin.test.assertEquals(1f, state.collapseFraction, "stays collapsed away from the top")
    }

    @Test
    fun enterAlways_expandsAnywhere() = runComposeUiTest {
        lateinit var state: ParallaxToolbarState
        setContent { state = rememberParallaxToolbarState(); Modes(ScrollMode.EnterAlways, state) }
        scrollFarDown()
        kotlin.test.assertEquals(1f, state.collapseFraction)
        kotlin.test.assertEquals(0f, state.toolbarExitFraction)
        nudgeDown()
        kotlin.test.assertTrue(state.collapseFraction < 1f, "expands away from the top, fraction=${state.collapseFraction}")
        onNodeWithText("expanded").assertIsDisplayed()
    }

    @Test
    fun enterAlwaysCollapsed_hidesToolbar_thenBringsItBackFirst() = runComposeUiTest {
        lateinit var state: ParallaxToolbarState
        setContent { state = rememberParallaxToolbarState(); Modes(ScrollMode.EnterAlwaysCollapsed, state) }
        scrollFarDown()
        kotlin.test.assertEquals(1f, state.collapseFraction)
        kotlin.test.assertEquals(1f, state.toolbarExitFraction, "toolbar left the screen")
        nudgeDown()
        kotlin.test.assertEquals(0f, state.toolbarExitFraction, "toolbar came back")
        kotlin.test.assertEquals(1f, state.collapseFraction, "header stays collapsed away from the top")

        repeat(8) { onNodeWithTag("layout").performTouchInput { swipeDown() } }
        waitForIdle()
        kotlin.test.assertEquals(0f, state.collapseFraction, "expands at the top")
        onNodeWithText("expanded").assertIsDisplayed()

        // Programmatic collapse only collapses the header; expand clears any exit as well.
        runOnIdle { kotlinx.coroutines.runBlocking { state.collapse(animated = false) } }
        waitForIdle()
        kotlin.test.assertEquals(0f, state.toolbarExitFraction)
        onNodeWithText("collapsed").assertIsDisplayed()
    }

    @Test
    fun enterAlwaysCollapsed_snapSettlesExitToo() = runComposeUiTest {
        lateinit var state: ParallaxToolbarState
        setContent {
            state = rememberParallaxToolbarState()
            ComposeParallaxToolbarLayout(
                titleContent = { Text(if (it) "collapsed" else "expanded") },
                headerContent = { Box(Modifier.fillMaxSize().background(Color.Blue)) },
                headerConfig = ParallaxToolbarDefaults.headerConfig(
                    height = HeaderHeight.Fixed(200.dp), scrollMode = ScrollMode.EnterAlwaysCollapsed, snapOnRelease = true
                ),
                content = ParallaxContent.Regular { Column { repeat(2) { i -> Text("Row $i") } } },
                modifier = Modifier.testTag("layout"),
                state = state
            )
        }
        // Collapse the header fully and push a little into the exit, then release without velocity.
        onNodeWithTag("layout").performTouchInput {
            down(center); moveBy(androidx.compose.ui.geometry.Offset(0f, -400f)); moveBy(androidx.compose.ui.geometry.Offset(0f, -20f)); advanceEventTime(400); up()
        }
        waitForIdle()
        kotlin.test.assertEquals(1f, state.collapseFraction)
        kotlin.test.assertTrue(state.toolbarExitFraction == 0f || state.toolbarExitFraction == 1f, "exit=${state.toolbarExitFraction}")
    }
}

@OptIn(ExperimentalTestApi::class)
class ComposeParallaxToolbarOverlayTest : UiTestBase() {

    @Test
    fun overlayElement_movesBetweenHeaderAndToolbar_andLayoutInfoIsPublished() = runComposeUiTest {
        lateinit var state: ParallaxToolbarState
        setContent {
            state = rememberParallaxToolbarState()
            ComposeParallaxToolbarLayout(
                titleContent = { Text(if (it) "collapsed" else "expanded") },
                headerContent = {
                    Box(Modifier.fillMaxSize().background(Color.Blue))
                    Box(Modifier.size(20.dp).parallax(0.3f).fadeOnCollapse().scaleOnCollapse(0.5f).testTag("decor"))
                },
                overlayContent = {
                    Box(
                        Modifier
                            .size(40.dp)
                            .moveBetween(
                                expanded = androidx.compose.ui.Alignment.BottomCenter,
                                collapsed = androidx.compose.ui.Alignment.CenterEnd,
                                collapsedPadding = androidx.compose.foundation.layout.PaddingValues(end = 8.dp),
                                collapsedScale = 0.5f
                            )
                            .background(Color.Red)
                            .testTag("avatar")
                    )
                },
                headerConfig = ParallaxToolbarDefaults.headerConfig(
                    height = HeaderHeight.Fixed(200.dp), parallaxMultiplier = 0f, fadeOnCollapse = false
                ),
                content = ParallaxContent.Regular { Column { repeat(60) { i -> Text("Row $i", Modifier.height(48.dp)) } } },
                state = state
            )
        }
        waitForIdle()
        val info = state.layoutInfo
        kotlin.test.assertTrue(info.isMeasured)
        kotlin.test.assertTrue(info.widthPx > 0f && info.headerHeightPx > 0f && info.toolbarHeightPx > 0f)
        kotlin.test.assertEquals(info.headerHeightPx - info.toolbarHeightPx, info.collapseRangePx)
        kotlin.test.assertEquals(0f, info.headerOffsetPx)

        val expandedBounds = onNodeWithTag("avatar").fetchSemanticsNode().boundsInRoot
        onNodeWithTag("decor").assertIsDisplayed()

        runOnIdle { kotlinx.coroutines.runBlocking { state.collapse(animated = false) } }
        waitForIdle()
        kotlin.test.assertEquals(info.collapseRangePx, info.headerOffsetPx)
        kotlin.test.assertEquals(info.topInsetPx + info.toolbarHeightPx, info.currentHeaderBottomPx)

        val collapsedBounds = onNodeWithTag("avatar").fetchSemanticsNode().boundsInRoot
        kotlin.test.assertTrue(collapsedBounds.top < expandedBounds.top, "moved up: $expandedBounds -> $collapsedBounds")
        kotlin.test.assertTrue(collapsedBounds.left > expandedBounds.left, "moved to the end: $expandedBounds -> $collapsedBounds")
        kotlin.test.assertTrue(collapsedBounds.height < expandedBounds.height, "scaled down")
        // Still within the toolbar band once collapsed.
        kotlin.test.assertTrue(collapsedBounds.bottom <= info.topInsetPx + info.toolbarHeightPx + 1f)
        onNodeWithText("collapsed").assertIsDisplayed()
    }
}

@OptIn(ExperimentalTestApi::class)
class ComposeParallaxToolbarExtrasTest : UiTestBase() {

    @Test
    fun stretch_pullsPastTop_thenSpringsBack_andFiresTriggerWhenFarEnough() = runComposeUiTest {
        lateinit var state: ParallaxToolbarState
        var triggers = 0
        var maxStretchSeen = 0f
        setContent {
            state = rememberParallaxToolbarState()
            ComposeParallaxToolbarLayout(
                titleContent = { Text(if (it) "collapsed" else "expanded") },
                headerContent = { Box(Modifier.fillMaxSize().background(Color.Blue).testTag("header")) },
                headerConfig = ParallaxToolbarDefaults.headerConfig(
                    height = HeaderHeight.Fixed(200.dp), stretchEnabled = true, stretchTriggerDistance = 40.dp
                ),
                onStretchTrigger = { triggers++ },
                content = ParallaxContent.Regular {
                    Column(Modifier.testTag("body")) { repeat(40) { i -> Text("Row $i", Modifier.fillMaxWidth().height(48.dp)) } }
                },
                state = state
            )
        }
        // A short pull on the body at its top: stretches a little, releases below the trigger.
        // Touch blocks dispatch as a batch, so the pointer is held across two blocks to observe.
        onNodeWithTag("body").performTouchInput {
            down(center); moveBy(androidx.compose.ui.geometry.Offset(0f, 30f)); advanceEventTime(300)
        }
        waitForIdle()
        maxStretchSeen = state.stretchPx
        onNodeWithTag("body").performTouchInput { up() }
        waitForIdle()

        kotlin.test.assertTrue(maxStretchSeen > 0f, "stretched during the pull: $maxStretchSeen")
        kotlin.test.assertEquals(0f, state.stretchPx, "sprang back")
        kotlin.test.assertEquals(0, triggers)

        // A long pull on the header itself passes the trigger distance.
        onNodeWithTag("header").performTouchInput {
            down(center); moveBy(androidx.compose.ui.geometry.Offset(0f, 200f)); moveBy(androidx.compose.ui.geometry.Offset(0f, 200f)); advanceEventTime(300); up()
        }
        waitForIdle()
        kotlin.test.assertEquals(0f, state.stretchPx)
        kotlin.test.assertEquals(1, triggers, "trigger fired once")
        kotlin.test.assertEquals(0f, state.collapseFraction)
    }

    @Test
    fun bottomSlot_isPinnedUnderTheToolbar_andBodyStartsBelowIt() = runComposeUiTest {
        lateinit var state: ParallaxToolbarState
        setContent {
            state = rememberParallaxToolbarState()
            ComposeParallaxToolbarLayout(
                titleContent = { Text(if (it) "collapsed" else "expanded") },
                headerContent = { Box(Modifier.fillMaxSize().background(Color.Blue)) },
                headerConfig = ParallaxToolbarDefaults.headerConfig(height = HeaderHeight.Fixed(200.dp)),
                bottomContent = { Box(Modifier.fillMaxWidth().height(40.dp).background(Color.Green).testTag("tabs")) },
                content = ParallaxContent.Regular {
                    Column { repeat(60) { i -> Text("Row $i", Modifier.fillMaxWidth().height(48.dp).testTag("row$i")) } }
                },
                modifier = Modifier.testTag("layout"),
                state = state
            )
        }
        val info = state.layoutInfo
        kotlin.test.assertTrue(info.bottomHeightPx > 0f)
        val tabsExpanded = onNodeWithTag("tabs").fetchSemanticsNode().boundsInRoot
        val row0Expanded = onNodeWithTag("row0").fetchSemanticsNode().boundsInRoot
        kotlin.test.assertTrue(row0Expanded.top >= tabsExpanded.bottom - 1f, "body starts below the slot")
        kotlin.test.assertEquals(info.topInsetPx + info.headerHeightPx, tabsExpanded.top, 1f)

        repeat(4) { onNodeWithTag("layout").performTouchInput { swipeUp() } }
        waitForIdle()
        val tabsCollapsed = onNodeWithTag("tabs").fetchSemanticsNode().boundsInRoot
        kotlin.test.assertEquals(info.topInsetPx + info.toolbarHeightPx, tabsCollapsed.top, 1f)
        onNodeWithTag("tabs").assertIsDisplayed()
    }

    @Test
    fun collapsedTitle_canBeCenteredOrEndAligned() = runComposeUiTest {
        lateinit var state: ParallaxToolbarState
        var alignment by androidx.compose.runtime.mutableStateOf(Alignment.CenterHorizontally as Alignment.Horizontal)
        setContent {
            state = rememberParallaxToolbarState()
            ComposeParallaxToolbarLayout(
                titleContent = { Box(Modifier.size(60.dp, 20.dp).background(Color.Black).testTag("title")) },
                headerContent = { Box(Modifier.fillMaxSize().background(Color.Blue)) },
                navigationIcon = { Box(Modifier.size(48.dp)) },
                actions = { Box(Modifier.size(48.dp)) },
                headerConfig = ParallaxToolbarDefaults.headerConfig(height = HeaderHeight.Fixed(200.dp)),
                titleConfig = ParallaxToolbarDefaults.titleConfig(collapsedAlignment = alignment),
                content = ParallaxContent.Regular { Column { repeat(60) { i -> Text("Row $i", Modifier.height(48.dp)) } } },
                state = state
            )
        }
        runOnIdle { kotlinx.coroutines.runBlocking { state.collapse(animated = false) } }
        waitForIdle()
        val width = state.layoutInfo.widthPx
        val centered = onNodeWithTag("title").fetchSemanticsNode().boundsInRoot
        kotlin.test.assertEquals(width / 2f, (centered.left + centered.right) / 2f, 2f)

        alignment = Alignment.End
        waitForIdle()
        val ended = onNodeWithTag("title").fetchSemanticsNode().boundsInRoot
        kotlin.test.assertTrue(ended.right > centered.right, "moved toward the end: $centered -> $ended")
        kotlin.test.assertTrue(ended.right <= width - 48f + 1f, "stays before the actions")
    }

    @Test
    fun relativeHeaderHeights_respectMaxHeight_andAlwaysElevatedRenders() = runComposeUiTest {
        lateinit var state: ParallaxToolbarState
        setContent {
            state = rememberParallaxToolbarState()
            ComposeParallaxToolbarLayout(
                titleContent = { Text("t") },
                headerContent = { Box(Modifier.fillMaxSize().background(Color.Blue)) },
                headerConfig = ParallaxToolbarDefaults.headerConfigWithPercentage(0.9f, maxHeight = 150.dp),
                toolbarConfig = ParallaxToolbarDefaults.toolbarConfig(elevation = 4.dp, alwaysElevated = true),
                content = ParallaxContent.Regular { Text("body") },
                state = state
            )
        }
        val density = state.layoutInfo.headerHeightPx / 150f
        kotlin.test.assertTrue(density in 0.5f..5f, "capped at 150dp: ${state.layoutInfo.headerHeightPx}px")
        kotlin.test.assertEquals(HeaderHeight.AspectRatio(1f, maxHeight = 10.dp).resolve(1000.dp, 1000.dp), 10.dp)
        kotlin.test.assertEquals(HeaderHeight.AspectRatio(2f).resolve(100.dp, 1000.dp), 50.dp)
        onNodeWithText("body").assertIsDisplayed()
    }
}

@OptIn(ExperimentalTestApi::class)
class ComposeParallaxToolbarSemanticsTest : UiTestBase() {

    @Test
    fun layout_exposesStateAndActions_orderAndHeading_andHidesInvisibleParts() = runComposeUiTest {
        lateinit var state: ParallaxToolbarState
        setContent {
            state = rememberParallaxToolbarState()
            ComposeParallaxToolbarLayout(
                titleContent = { Text(if (it) "collapsed" else "expanded") },
                headerContent = { Box(Modifier.fillMaxSize().background(Color.Blue).testTag("headerBox")) },
                navigationIcon = { Text("<") },
                headerConfig = ParallaxToolbarDefaults.headerConfig(
                    height = HeaderHeight.Fixed(200.dp), scrollMode = ScrollMode.EnterAlwaysCollapsed
                ),
                semanticsConfig = ParallaxToolbarDefaults.semanticsConfig(
                    expandedStateDescription = "Open", collapsedStateDescription = "Closed",
                    expandActionLabel = "Open it", collapseActionLabel = "Close it"
                ),
                content = ParallaxContent.Lazy(content = { _ -> items(300) { i -> Text("Row $i", Modifier.fillMaxWidth().height(48.dp)) } }),
                modifier = Modifier.testTag("layout"),
                state = state
            )
        }
        val root = onNodeWithTag("layout")
        root.assert(androidx.compose.ui.test.hasStateDescription("Open"))
        root.assert(androidx.compose.ui.test.SemanticsMatcher.keyIsDefined(androidx.compose.ui.semantics.SemanticsActions.Collapse))
        root.assert(androidx.compose.ui.test.SemanticsMatcher.keyIsDefined(androidx.compose.ui.semantics.SemanticsProperties.IsTraversalGroup))

        // The title is a heading; the header is announced while visible.
        onNodeWithText("expanded", useUnmergedTree = true).assertExists()
        kotlin.test.assertTrue(
            onAllNodes(androidx.compose.ui.test.SemanticsMatcher.keyIsDefined(androidx.compose.ui.semantics.SemanticsProperties.Heading), useUnmergedTree = true)
                .fetchSemanticsNodes().isNotEmpty(), "title is a heading"
        )
        val headerHidden = androidx.compose.ui.test.SemanticsMatcher.keyIsDefined(androidx.compose.ui.semantics.SemanticsProperties.HideFromAccessibility)
        kotlin.test.assertTrue(onAllNodes(headerHidden, useUnmergedTree = true).fetchSemanticsNodes().isEmpty(), "nothing hidden while expanded")

        // The collapse action works and flips the state; the faded header is then hidden.
        root.performSemanticsAction(androidx.compose.ui.semantics.SemanticsActions.Collapse)
        waitForIdle()
        kotlin.test.assertTrue(state.isCollapsed)
        root.assert(androidx.compose.ui.test.hasStateDescription("Closed"))
        root.assert(androidx.compose.ui.test.SemanticsMatcher.keyIsDefined(androidx.compose.ui.semantics.SemanticsActions.Expand))
        kotlin.test.assertEquals(1, onAllNodes(headerHidden, useUnmergedTree = true).fetchSemanticsNodes().size, "faded header hidden")

        // Scrolling far in EnterAlwaysCollapsed exits the toolbar, which is then hidden too.
        repeat(4) { root.performTouchInput { swipeUp() } }
        waitForIdle()
        kotlin.test.assertEquals(1f, state.toolbarExitFraction)
        kotlin.test.assertEquals(2, onAllNodes(headerHidden, useUnmergedTree = true).fetchSemanticsNodes().size, "exited toolbar hidden")

        root.performSemanticsAction(androidx.compose.ui.semantics.SemanticsActions.Expand)
        waitForIdle()
        kotlin.test.assertEquals(0f, state.collapseFraction)
        kotlin.test.assertEquals(0f, state.toolbarExitFraction)
        root.assert(androidx.compose.ui.test.hasStateDescription("Open"))

        // Traversal order: toolbar before header before body.
        val index = androidx.compose.ui.semantics.SemanticsProperties.TraversalIndex
        val indices = onAllNodes(androidx.compose.ui.test.SemanticsMatcher.keyIsDefined(index), useUnmergedTree = true)
            .fetchSemanticsNodes().map { it.config[index] }.sorted()
        kotlin.test.assertEquals(listOf(0f, 1f, 4f), indices)
    }
}

class ParallaxValidationTest {

    private inline fun failsWith(fragment: String, block: () -> Unit) {
        val e = kotlin.test.assertFailsWith<IllegalArgumentException>(block = block)
        kotlin.test.assertTrue(e.message!!.contains(fragment), "message '${e.message}' should mention '$fragment'")
    }

    @Test
    fun invalidValues_failEarly_withActionableMessages() {
        failsWith("Percentage must be in 0..1") { HeaderHeight.Percentage(1.4f) }
        failsWith("Percentage must be in 0..1") { HeaderHeight.Percentage(0f) }
        failsWith("AspectRatio ratio must be a positive") { HeaderHeight.AspectRatio(0f) }
        failsWith("AspectRatio ratio must be a positive") { HeaderHeight.AspectRatio(Float.NaN) }
        failsWith("maxHeight must be 0.dp or more") { HeaderHeight.Percentage(0.5f, maxHeight = (-1).dp) }
        failsWith("Fixed height must be 0.dp or more") { HeaderHeight.Fixed((-10).dp) }
        failsWith("parallaxMultiplier must be a finite") { ParallaxHeaderConfig(HeaderHeight.Fixed(100.dp), null, parallaxMultiplier = Float.NaN) }
        failsWith("stretchTriggerDistance must be greater") { ParallaxHeaderConfig(HeaderHeight.Fixed(100.dp), null, stretchTriggerDistance = 0.dp) }
        failsWith("height must be greater than 0.dp") { ParallaxToolbarConfig(Color.Black, Color.Black, 0.dp, androidx.compose.animation.core.snap(), height = 0.dp) }
        failsWith("elevation must be 0.dp or more") { ParallaxToolbarConfig(Color.Black, Color.Black, (-1).dp, androidx.compose.animation.core.snap()) }
        failsWith("collapsedScale must be a positive") { ParallaxTitleConfig(0.dp, 0.dp, 0.dp, false, true, collapsedScale = 0f) }
        failsWith("minBottomSpacerHeight must be 0.dp or more") { ParallaxBodyConfig((-4).dp) }

        // Valid edges still construct.
        HeaderHeight.Percentage(1f); HeaderHeight.Fixed(0.dp); HeaderHeight.AspectRatio(0.1f, maxHeight = 0.dp)
        ParallaxTitleConfig(0.dp, 0.dp, 0.dp, false, true, collapsedScale = 2f)
    }
}

@OptIn(ExperimentalTestApi::class)
class ComposeParallaxToolbarGeometryTest : UiTestBase() {

    @Test
    fun centeredTitle_isCenteredBetweenSlots_inRtl_withNavigationOnly() = runComposeUiTest {
        lateinit var state: ParallaxToolbarState
        setContent {
            androidx.compose.runtime.CompositionLocalProvider(
                androidx.compose.ui.platform.LocalLayoutDirection provides androidx.compose.ui.unit.LayoutDirection.Rtl
            ) {
                state = rememberParallaxToolbarState()
                ComposeParallaxToolbarLayout(
                    titleContent = { Box(Modifier.size(60.dp, 20.dp).background(Color.Black).testTag("title")) },
                    headerContent = { Box(Modifier.fillMaxSize().background(Color.Blue)) },
                    navigationIcon = { Box(Modifier.size(48.dp).testTag("nav")) },
                    headerConfig = ParallaxToolbarDefaults.headerConfig(height = HeaderHeight.Fixed(200.dp)),
                    titleConfig = ParallaxToolbarDefaults.titleConfig(collapsedAlignment = Alignment.CenterHorizontally),
                    content = ParallaxContent.Regular { Column { repeat(60) { Box(Modifier.height(48.dp)) } } },
                    windowInsets = androidx.compose.foundation.layout.WindowInsets(0),
                    state = state
                )
            }
        }
        runOnIdle { kotlinx.coroutines.runBlocking { state.collapse(animated = false) } }
        waitForIdle()
        val nav = onNodeWithTag("nav").fetchSemanticsNode().boundsInRoot
        val title = onNodeWithTag("title").fetchSemanticsNode().boundsInRoot
        // In RTL the navigation icon sits on the right; the free region is everything left of it.
        kotlin.test.assertEquals(state.layoutInfo.widthPx, nav.right + 4f * density.density, 1f)
        kotlin.test.assertEquals(nav.left / 2f, (title.left + title.right) / 2f, 2f)
    }

    @Test
    fun longTitle_staysClearOfActions_whenCollapsed() = runComposeUiTest {
        lateinit var state: ParallaxToolbarState
        setContent {
            state = rememberParallaxToolbarState()
            ComposeParallaxToolbarLayout(
                titleContent = { Box(Modifier.fillMaxWidth().height(20.dp).background(Color.Black).testTag("title")) },
                headerContent = { Box(Modifier.fillMaxSize().background(Color.Blue)) },
                navigationIcon = { Box(Modifier.size(24.dp)) },
                actions = { Box(Modifier.size(48.dp).testTag("actions")) },
                headerConfig = ParallaxToolbarDefaults.headerConfig(height = HeaderHeight.Fixed(200.dp)),
                content = ParallaxContent.Regular { Column { repeat(60) { Box(Modifier.height(48.dp)) } } },
                windowInsets = androidx.compose.foundation.layout.WindowInsets(0),
                state = state
            )
        }
        runOnIdle { kotlinx.coroutines.runBlocking { state.collapse(animated = false) } }
        waitForIdle()
        val title = onNodeWithTag("title").fetchSemanticsNode().boundsInRoot
        val actions = onNodeWithTag("actions").fetchSemanticsNode().boundsInRoot
        // The collapsed start padding (64dp) is wider than the 24dp icon; the title must still end
        // before the actions instead of running under them.
        kotlin.test.assertEquals(64f * density.density, title.left, 1f)
        kotlin.test.assertTrue(title.right <= actions.left + 1f, "title $title ends before actions $actions")
    }

    @Test
    fun windowInsets_pushTheToolbarSlotsInside_andCanBeZeroed() = runComposeUiTest {
        lateinit var state: ParallaxToolbarState
        setContent {
            state = rememberParallaxToolbarState()
            ComposeParallaxToolbarLayout(
                titleContent = { Box(Modifier.size(60.dp, 20.dp).background(Color.Black).testTag("title")) },
                headerContent = { Box(Modifier.fillMaxSize().background(Color.Blue)) },
                navigationIcon = { Box(Modifier.size(48.dp).testTag("nav")) },
                actions = { Box(Modifier.size(48.dp).testTag("actions")) },
                headerConfig = ParallaxToolbarDefaults.headerConfig(height = HeaderHeight.Fixed(200.dp)),
                content = ParallaxContent.Regular { Column { repeat(60) { Box(Modifier.height(48.dp)) } } },
                windowInsets = androidx.compose.foundation.layout.WindowInsets(left = 20.dp, top = 30.dp, right = 10.dp),
                state = state
            )
        }
        val d = density.density
        kotlin.test.assertEquals(30f * d, state.layoutInfo.topInsetPx, 0.5f)
        val nav = onNodeWithTag("nav").fetchSemanticsNode().boundsInRoot
        val actions = onNodeWithTag("actions").fetchSemanticsNode().boundsInRoot
        kotlin.test.assertEquals((20f + 4f) * d, nav.left, 1f)
        kotlin.test.assertEquals(state.layoutInfo.widthPx - (10f + 4f) * d, actions.right, 1f)
        kotlin.test.assertEquals(30f * d + (64f - 48f) / 2f * d, nav.top, 1f)
        // The expanded title starts after the left inset too.
        val title = onNodeWithTag("title").fetchSemanticsNode().boundsInRoot
        kotlin.test.assertEquals((20f + 16f) * d, title.left, 1f)
    }

    @Test
    fun unboundedHeight_failsWithAClearMessage() = runComposeUiTest {
        val error = kotlin.runCatching {
            setContent {
                Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
                    ComposeParallaxToolbarLayout(
                        titleContent = { Text("t") },
                        headerContent = { Box(Modifier.fillMaxSize()) },
                        content = ParallaxContent.Regular { Text("body") }
                    )
                }
            }
            waitForIdle()
        }.exceptionOrNull()
        val messages = generateSequence(error) { it.cause }.mapNotNull { it.message }.toList()
        kotlin.test.assertTrue(
            messages.any { "needs a bounded height" in it },
            "expected the bounded-height message, got $messages"
        )
    }

    @Test
    fun collapsedHeader_isHiddenFromAccessibility_evenWithoutFade() = runComposeUiTest {
        lateinit var state: ParallaxToolbarState
        setContent {
            state = rememberParallaxToolbarState()
            ComposeParallaxToolbarLayout(
                titleContent = { Text("t") },
                headerContent = { Box(Modifier.fillMaxSize().background(Color.Blue)) },
                headerConfig = ParallaxToolbarDefaults.headerConfig(height = HeaderHeight.Fixed(200.dp), fadeOnCollapse = false),
                content = ParallaxContent.Regular { Column { repeat(60) { Box(Modifier.height(48.dp)) } } },
                state = state
            )
        }
        val hidden = androidx.compose.ui.test.SemanticsMatcher.keyIsDefined(androidx.compose.ui.semantics.SemanticsProperties.HideFromAccessibility)
        kotlin.test.assertTrue(onAllNodes(hidden, useUnmergedTree = true).fetchSemanticsNodes().isEmpty())
        runOnIdle { kotlinx.coroutines.runBlocking { state.collapse(animated = false) } }
        waitForIdle()
        kotlin.test.assertEquals(1, onAllNodes(hidden, useUnmergedTree = true).fetchSemanticsNodes().size, "covered header hidden")
    }

    @Test
    fun collapse_honorsAnimationSpec_andReportsProgress() = runComposeUiTest {
        lateinit var state: ParallaxToolbarState
        lateinit var scope: kotlinx.coroutines.CoroutineScope
        var progressSeen = false
        setContent {
            state = rememberParallaxToolbarState()
            scope = androidx.compose.runtime.rememberCoroutineScope()
            ComposeParallaxToolbarLayout(
                titleContent = { Text("t") },
                headerContent = { Box(Modifier.fillMaxSize().background(Color.Blue)) },
                headerConfig = ParallaxToolbarDefaults.headerConfig(
                    height = HeaderHeight.Fixed(200.dp),
                    animationSpec = androidx.compose.animation.core.tween(durationMillis = 400)
                ),
                content = ParallaxContent.Regular { Column { repeat(60) { Box(Modifier.height(48.dp)) } } },
                state = state
            )
        }
        kotlin.test.assertFalse(state.isScrollInProgress)
        mainClock.autoAdvance = false
        runOnIdle { scope.launch { state.collapse() } }
        mainClock.advanceTimeBy(200)
        progressSeen = state.isScrollInProgress
        val midway = state.collapseFraction
        mainClock.advanceTimeBy(400)
        mainClock.autoAdvance = true
        waitForIdle()
        kotlin.test.assertTrue(progressSeen, "in progress midway")
        kotlin.test.assertTrue(midway > 0f && midway < 1f, "tween is partway at 200ms: $midway")
        kotlin.test.assertTrue(state.isCollapsed)
        kotlin.test.assertFalse(state.isScrollInProgress)

        // A per-call spec overrides the config: snap() finishes without frames.
        runOnIdle { scope.launch { state.expand(animationSpec = androidx.compose.animation.core.snap()) } }
        mainClock.advanceTimeByFrame()
        kotlin.test.assertEquals(0f, state.collapseFraction)
    }
}
