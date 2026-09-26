package am.highapps.parallaxtoolbar

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
class ComposeParallaxToolbarLayoutTest {

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
                    IconButton(onClick = {}, modifier = Modifier.testTag("nav")) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "back")
                    }
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
