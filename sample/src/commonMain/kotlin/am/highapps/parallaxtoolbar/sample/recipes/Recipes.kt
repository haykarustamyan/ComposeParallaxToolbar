// Complete, compiling examples. docs/RECIPES.md is generated from this file by
// scripts/generate-docs.py; each `// recipe:` block becomes one section.
package am.highapps.parallaxtoolbar.sample.recipes

import am.highapps.parallaxtoolbar.ComposeParallaxToolbarLayout
import am.highapps.parallaxtoolbar.HeaderHeight
import am.highapps.parallaxtoolbar.ParallaxContent
import am.highapps.parallaxtoolbar.ParallaxToolbarDefaults
import am.highapps.parallaxtoolbar.ScrollMode
import am.highapps.parallaxtoolbar.rememberParallaxToolbarState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

// recipe: Basic list screen
// A title that changes style once collapsed, a subtitle, navigation and actions, and a lazy list.
@Composable
fun BasicListScreen(items: List<String>, onBack: () -> Unit) {
    ComposeParallaxToolbarLayout(
        titleContent = { collapsed ->
            Text(
                text = "Playlist",
                color = if (collapsed) MaterialTheme.colorScheme.onSurface else Color.White,
                style = if (collapsed) MaterialTheme.typography.titleMedium else MaterialTheme.typography.headlineMedium,
            )
        },
        subtitleContent = { collapsed -> if (!collapsed) Text("${items.size} tracks", color = Color.White) },
        headerContent = { HeaderArtwork() },
        navigationIcon = { collapsed ->
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = tint(collapsed))
            }
        },
        actions = { collapsed ->
            IconButton(onClick = {}) { Icon(Icons.Default.Share, contentDescription = "Share", tint = tint(collapsed)) }
        },
        toolbarConfig = ParallaxToolbarDefaults.toolbarConfig(targetColor = MaterialTheme.colorScheme.surface, elevation = 3.dp),
        content = ParallaxContent.Lazy(
            content = { _ -> items(items.size) { i -> ListItem(headlineContent = { Text(items[i]) }) } },
            config = ParallaxToolbarDefaults.lazyColumnConfig(contentPadding = PaddingValues(vertical = 8.dp)),
        ),
    )
}
// end recipe

// recipe: Photo grid
// Any scrollable can be the body. A two-column grid collapses the header through nested scroll.
@Composable
fun PhotoGridScreen(photos: List<Int>) {
    ComposeParallaxToolbarLayout(
        titleContent = { Text("Gallery", color = Color.White, style = MaterialTheme.typography.headlineMedium) },
        headerContent = { HeaderArtwork() },
        headerConfig = ParallaxToolbarDefaults.headerConfigWithAspectRatio(aspectRatio = 16f / 9f, maxHeight = 320.dp),
        content = ParallaxContent.Custom { _ ->
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                items(photos) { seed -> Box(Modifier.aspectRatio(1f).background(swatch(seed))) }
            }
        },
    )
}
// end recipe

// recipe: Profile with avatar into the toolbar
// The avatar lives in overlayContent, above the body, and glides into the toolbar. Header-wide
// effects are turned off so the cover image keeps its own parallax and fade.
@Composable
fun ProfileScreen(name: String, posts: List<String>) {
    ComposeParallaxToolbarLayout(
        titleContent = { collapsed ->
            Text(
                name,
                color = if (collapsed) MaterialTheme.colorScheme.onSurface else Color.White,
                style = MaterialTheme.typography.headlineSmall,
            )
        },
        headerContent = {
            Box(Modifier.fillMaxSize().parallax(0.5f).fadeOnCollapse().background(swatch(7)))
        },
        overlayContent = {
            Box(
                Modifier
                    .size(80.dp)
                    .moveBetween(
                        expanded = Alignment.BottomStart,
                        collapsed = Alignment.CenterEnd,
                        expandedPadding = PaddingValues(start = 16.dp, bottom = 56.dp),
                        collapsedPadding = PaddingValues(end = 16.dp),
                        collapsedScale = 0.5f,
                    )
                    .background(Color(0xFFFFC107), CircleShape),
            )
        },
        headerConfig = ParallaxToolbarDefaults.headerConfig(
            height = HeaderHeight.Fixed(280.dp),
            parallaxMultiplier = 0f,
            fadeOnCollapse = false,
        ),
        toolbarConfig = ParallaxToolbarDefaults.toolbarConfig(targetColor = MaterialTheme.colorScheme.surface),
        content = ParallaxContent.Lazy(content = { _ -> items(posts.size) { i -> ListItem(headlineContent = { Text(posts[i]) }) } }),
    )
}
// end recipe

// recipe: Tabs under the toolbar
// bottomContent stays pinned below the toolbar; the body starts beneath it.
@Composable
fun TabbedScreen(sections: List<String>) {
    var selected by remember { mutableIntStateOf(0) }
    ComposeParallaxToolbarLayout(
        titleContent = { Text("Store", color = Color.White, style = MaterialTheme.typography.headlineMedium) },
        headerContent = { HeaderArtwork() },
        bottomContent = {
            TabRow(selectedTabIndex = selected, modifier = Modifier.fillMaxWidth()) {
                sections.forEachIndexed { i, s -> Tab(selected = i == selected, onClick = { selected = i }, text = { Text(s) }) }
            }
        },
        headerConfig = ParallaxToolbarDefaults.headerConfig(height = HeaderHeight.Fixed(220.dp)),
        toolbarConfig = ParallaxToolbarDefaults.toolbarConfig(targetColor = MaterialTheme.colorScheme.surface),
        content = ParallaxContent.Lazy(content = { _ ->
            items(40) { i -> ListItem(headlineContent = { Text("${sections[selected]} item ${i + 1}") }) }
        }),
    )
}
// end recipe

// recipe: Pull to refresh
// stretchEnabled lets a pull past the top stretch the header; onStretchTrigger fires on release.
@Composable
fun RefreshableFeedScreen(feed: List<String>, onRefresh: () -> Unit) {
    ComposeParallaxToolbarLayout(
        titleContent = { Text("Feed", color = Color.White, style = MaterialTheme.typography.headlineMedium) },
        headerContent = { HeaderArtwork() },
        headerConfig = ParallaxToolbarDefaults.headerConfig(
            height = HeaderHeight.Percentage(0.35f, maxHeight = 300.dp),
            stretchEnabled = true,
            stretchTriggerDistance = 96.dp,
        ),
        onStretchTrigger = onRefresh,
        content = ParallaxContent.Lazy(content = { _ -> items(feed.size) { i -> ListItem(headlineContent = { Text(feed[i]) }) } }),
    )
}
// end recipe

// recipe: Scaffold with a bottom bar
// Pass the Scaffold padding as contentPadding so the last row clears the navigation bar.
@Composable
fun ScaffoldScreen(rows: List<String>) {
    var tab by remember { mutableIntStateOf(0) }
    Scaffold(
        bottomBar = {
            NavigationBar {
                listOf("Home", "Search").forEachIndexed { i, label ->
                    NavigationBarItem(selected = tab == i, onClick = { tab = i }, icon = {}, label = { Text(label) })
                }
            }
        },
    ) { padding ->
        ComposeParallaxToolbarLayout(
            titleContent = { Text("Home", color = Color.White, style = MaterialTheme.typography.headlineMedium) },
            headerContent = { HeaderArtwork() },
            contentPadding = padding,
            content = ParallaxContent.Regular { _ ->
                rows.forEach {
                    Card(Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 6.dp)) { Text(it, Modifier.padding(16.dp)) }
                }
            },
        )
    }
}
// end recipe

// recipe: iOS-style centered title with enter-always header
// The collapsed title is centered and the header returns on any downward scroll.
@Composable
fun SettingsStyleScreen(rows: List<String>) {
    ComposeParallaxToolbarLayout(
        titleContent = { collapsed ->
            Text(
                "Settings",
                color = if (collapsed) MaterialTheme.colorScheme.onSurface else Color.White,
                style = if (collapsed) MaterialTheme.typography.titleMedium else MaterialTheme.typography.headlineLarge,
            )
        },
        headerContent = { Box(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.primary)) },
        headerConfig = ParallaxToolbarDefaults.headerConfig(
            height = HeaderHeight.Fixed(160.dp),
            scrollMode = ScrollMode.EnterAlways,
            snapOnRelease = true,
        ),
        toolbarConfig = ParallaxToolbarDefaults.toolbarConfig(targetColor = MaterialTheme.colorScheme.surface, elevation = 2.dp),
        titleConfig = ParallaxToolbarDefaults.titleConfig(collapsedAlignment = Alignment.CenterHorizontally, collapsedScale = 0.85f),
        content = ParallaxContent.Lazy(content = { _ -> items(rows.size) { i -> ListItem(headlineContent = { Text(rows[i]) }) } }),
    )
}
// end recipe

// recipe: Programmatic control and reading the fraction
// Hoist the state to drive the header from outside and to react to the collapse fraction.
@Composable
fun ControlledScreen(rows: List<String>) {
    val state = rememberParallaxToolbarState()
    val scope = rememberCoroutineScope()
    ComposeParallaxToolbarLayout(
        titleContent = { Text("Controlled", color = Color.White, style = MaterialTheme.typography.headlineMedium) },
        headerContent = {
            // Read the fraction on the draw path: this never recomposes while scrolling.
            Box(Modifier.fillMaxSize().graphicsLayer { alpha = 1f - collapseFraction / 2f }.background(swatch(3)))
        },
        actions = { collapsed ->
            Button(onClick = { scope.launch { if (collapsed) state.expand() else state.collapse() } }) {
                Text(if (collapsed) "Expand" else "Collapse")
            }
        },
        state = state,
        content = ParallaxContent.Lazy(content = { _ -> items(rows.size) { i -> ListItem(headlineContent = { Text(rows[i]) }) } }),
    )
}
// end recipe

@Composable
internal fun HeaderArtwork() {
    Box(Modifier.fillMaxSize().background(Brush.linearGradient(listOf(Color(0xFF6A11CB), Color(0xFF2575FC)))))
}

@Composable
private fun tint(collapsed: Boolean): Color = if (collapsed) MaterialTheme.colorScheme.onSurface else Color.White

private fun swatch(seed: Int): Color {
    val palette = listOf(0xFF7E57C2, 0xFF26A69A, 0xFFEF5350, 0xFF42A5F5, 0xFFFFA726, 0xFF66BB6A, 0xFFEC407A, 0xFF5C6BC0)
    return Color(palette[seed.mod(palette.size)])
}
