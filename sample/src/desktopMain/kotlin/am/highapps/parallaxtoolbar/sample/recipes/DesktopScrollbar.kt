package am.highapps.parallaxtoolbar.sample.recipes

import am.highapps.parallaxtoolbar.ComposeParallaxToolbarLayout
import am.highapps.parallaxtoolbar.ParallaxContent
import am.highapps.parallaxtoolbar.rememberParallaxToolbarState
import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

// recipe: Desktop scrollbar
// Desktop users expect a scrollbar beside a long list. Use Custom content so the list and its
// scrollbar share one Box; the collapse still runs through nested scroll and the wheel.
@Composable
fun DesktopListScreen(items: List<String>) {
    val state = rememberParallaxToolbarState()
    ComposeParallaxToolbarLayout(
        titleContent = { Text("Library") },
        headerContent = { HeaderArtwork() },
        state = state,
        content = ParallaxContent.Custom {
            Box(Modifier.fillMaxSize()) {
                LazyColumn(state = state.lazyListState, modifier = Modifier.fillMaxSize()) {
                    items(items) { ListItem(headlineContent = { Text(it) }) }
                }
                VerticalScrollbar(
                    adapter = rememberScrollbarAdapter(state.lazyListState),
                    modifier = Modifier.align(Alignment.CenterEnd).fillMaxHeight()
                )
            }
        }
    )
}
// end recipe
