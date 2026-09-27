package am.highapps.parallaxtoolbar

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
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
                state = state
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
