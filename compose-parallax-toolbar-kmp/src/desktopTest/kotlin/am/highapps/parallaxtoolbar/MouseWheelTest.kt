package am.highapps.parallaxtoolbar

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performMouseInput
import androidx.compose.ui.test.v2.runComposeUiTest
import androidx.compose.ui.unit.dp
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/** Wheel and trackpad scrolling never flings, so it must not leave the header stretched. */
@OptIn(ExperimentalTestApi::class)
class MouseWheelTest : UiTestBase() {

    @Test
    fun wheel_collapsesAndExpands_butNeverStretches() = runComposeUiTest {
        lateinit var state: ParallaxToolbarState
        setContent {
            state = rememberParallaxToolbarState()
            ComposeParallaxToolbarLayout(
                titleContent = { Box(Modifier.height(20.dp)) },
                headerContent = { Box(Modifier.fillMaxSize().background(Color.Blue).testTag("header")) },
                headerConfig = ParallaxToolbarDefaults.headerConfig(height = HeaderHeight.Fixed(200.dp), stretchEnabled = true),
                content = ParallaxContent.Regular {
                    Column(Modifier.testTag("body")) { repeat(60) { Box(Modifier.height(48.dp)) } }
                },
                state = state,
            )
        }
        // Wheel over the body: collapse, then back up past the top.
        onNodeWithTag("body").performMouseInput { scroll(300f) }
        waitForIdle()
        assertTrue(state.collapseFraction > 0f, "wheel collapsed: ${state.collapseFraction}")
        onNodeWithTag("body").performMouseInput { repeat(5) { scroll(-300f) } }
        waitForIdle()
        assertEquals(0f, state.collapseFraction, "wheel expanded")
        assertEquals(0f, state.stretchPx, "wheel left no stretch on the body path")

        // Wheel over the header itself goes through its own scrollable; same rule.
        onNodeWithTag("header").performMouseInput { repeat(3) { scroll(-300f) } }
        waitForIdle()
        assertEquals(0f, state.stretchPx, "wheel left no stretch on the header path")
    }
}

@OptIn(ExperimentalTestApi::class)
class MouseWheelSnapTest : UiTestBase() {

    private fun androidx.compose.ui.test.ComposeUiTest.setUp(snap: Boolean): ParallaxToolbarState {
        lateinit var state: ParallaxToolbarState
        setContent {
            state = rememberParallaxToolbarState()
            ComposeParallaxToolbarLayout(
                titleContent = { Box(Modifier.height(20.dp)) },
                headerContent = { Box(Modifier.fillMaxSize().background(Color.Blue).testTag("header")) },
                headerConfig = ParallaxToolbarDefaults.headerConfig(height = HeaderHeight.Fixed(200.dp), snapOnRelease = snap),
                content = ParallaxContent.Regular {
                    Column(Modifier.testTag("body")) { repeat(60) { Box(Modifier.height(48.dp)) } }
                },
                state = state,
            )
        }
        return state
    }

    @Test
    fun oneWheelTick_leavesTheHeaderPartWay_withoutSnap() = runComposeUiTest {
        val state = setUp(snap = false)
        onNodeWithTag("body").performMouseInput { scroll(1f) }
        waitForIdle()
        val f = state.collapseFraction
        assertTrue(f > 0f && f < 1f, "a wheel tick has no fling to carry the header through: $f")
    }

    @Test
    fun oneWheelTick_settles_withSnap() = runComposeUiTest {
        val state = setUp(snap = true)
        onNodeWithTag("body").performMouseInput { scroll(1f) }
        waitForIdle()
        assertTrue(state.collapseFraction == 0f || state.collapseFraction == 1f, "settled: ${state.collapseFraction}")

        // Also over the header's own scrollable, in the other direction.
        onNodeWithTag("body").performMouseInput { scroll(3f) }
        waitForIdle()
        assertEquals(1f, state.collapseFraction, "three ticks settle collapsed")
        onNodeWithTag("header").performMouseInput { scroll(-1f) }
        waitForIdle()
        assertTrue(state.collapseFraction == 0f || state.collapseFraction == 1f, "settled after the header tick: ${state.collapseFraction}")
    }
}

@OptIn(ExperimentalTestApi::class)
class MouseWheelAtBoundsTest : UiTestBase() {

    @Test
    fun wheelOverAListAtItsTop_expandsACollapsedHeader() = runComposeUiTest {
        lateinit var state: ParallaxToolbarState
        setContent {
            state = rememberParallaxToolbarState()
            ComposeParallaxToolbarLayout(
                titleContent = { Box(Modifier.height(20.dp)) },
                headerContent = { Box(Modifier.fillMaxSize().background(Color.Blue)) },
                headerConfig = ParallaxToolbarDefaults.headerConfig(height = HeaderHeight.Fixed(200.dp)),
                content = ParallaxContent.Lazy(content = { _ ->
                    items(60) { Box(Modifier.fillMaxWidth().height(48.dp).testTag("row$it")) }
                }),
                state = state,
            )
        }
        // Collapse without moving the list: the list is at its top and refuses an upward wheel.
        runOnIdle { kotlinx.coroutines.runBlocking { state.collapse(animated = false) } }
        waitForIdle()
        assertEquals(0, state.lazyListState.firstVisibleItemIndex)
        onNodeWithTag("row0").performMouseInput { repeat(3) { scroll(-1f) } }
        waitForIdle()
        val afterThree = state.collapseFraction
        assertTrue(afterThree < 1f, "wheel over the list at its top moved the header: $afterThree")
        onNodeWithTag("row0").performMouseInput { repeat(9) { scroll(-1f) } }
        waitForIdle()
        assertEquals(0f, state.collapseFraction, "enough ticks expand it fully")

        // And the other way: short content cannot scroll down, yet the wheel still collapses.
        onNodeWithTag("row0").performMouseInput { repeat(6) { scroll(1f) } }
        waitForIdle()
        assertTrue(state.collapseFraction > 0f, "wheel over the list collapsed the header")
    }
}
