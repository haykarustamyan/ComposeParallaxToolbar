package am.highapps.parallaxtoolbar

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.BasicText
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.StateRestorationTester
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.unit.dp
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

/** Saved-state restoration is only emulated by the Android test rule, so this lives here. */
@RunWith(AndroidJUnit4::class)
@Config(sdk = [35])
@GraphicsMode(GraphicsMode.Mode.NATIVE)
class StateRestorationTest {

    @get:Rule
    val rule = createComposeRule()

    @Test
    fun collapseFraction_isRestored_fromSavedState() {
        val restorationTester = StateRestorationTester(rule)
        lateinit var state: ParallaxToolbarState
        restorationTester.setContent {
            state = rememberParallaxToolbarState()
            ComposeParallaxToolbarLayout(
                titleContent = { BasicText(if (it) "collapsed" else "expanded") },
                headerContent = { Box(Modifier.fillMaxSize().background(Color.Blue)) },
                content = ParallaxContent.Regular {
                    Column { repeat(60) { i -> BasicText("Row $i", Modifier.height(48.dp)) } }
                },
                state = state
            )
        }
        rule.runOnIdle { runBlocking { state.collapse(animated = false) } }
        rule.waitForIdle()

        restorationTester.emulateSavedInstanceStateRestore()
        rule.waitForIdle()

        rule.onNodeWithText("collapsed").assertIsDisplayed()
        assertEquals(1f, state.collapseFraction)
    }
}
