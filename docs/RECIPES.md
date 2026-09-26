# Recipes

Complete screens for common tasks, version 2.0.0. Every Kotlin block is compiled as part of the
`sample` module on each CI run, so it is known to build against the current API. Imports are in
[Recipes.kt](../sample/src/commonMain/kotlin/am/highapps/parallaxtoolbar/sample/recipes/Recipes.kt);
the examples use Material 3 for their own widgets, which the library does not require.

## Basic list screen

A title that changes style once collapsed, a subtitle, navigation and actions, and a lazy list.

```kotlin
@Composable
fun BasicListScreen(items: List<String>, onBack: () -> Unit) {
    ComposeParallaxToolbarLayout(
        titleContent = { collapsed ->
            Text(
                text = "Playlist",
                color = if (collapsed) MaterialTheme.colorScheme.onSurface else Color.White,
                style = if (collapsed) MaterialTheme.typography.titleMedium else MaterialTheme.typography.headlineMedium
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
            config = ParallaxToolbarDefaults.lazyColumnConfig(contentPadding = PaddingValues(vertical = 8.dp))
        )
    )
}
```

## Photo grid

Any scrollable can be the body. A two-column grid collapses the header through nested scroll.

```kotlin
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
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(photos) { seed -> Box(Modifier.aspectRatio(1f).background(swatch(seed))) }
            }
        }
    )
}
```

## Profile with avatar into the toolbar

The avatar lives in overlayContent, above the body, and glides into the toolbar. Header-wide effects are turned off so the cover image keeps its own parallax and fade.

```kotlin
@Composable
fun ProfileScreen(name: String, posts: List<String>) {
    ComposeParallaxToolbarLayout(
        titleContent = { collapsed ->
            Text(name, color = if (collapsed) MaterialTheme.colorScheme.onSurface else Color.White,
                style = MaterialTheme.typography.headlineSmall)
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
                        collapsedScale = 0.5f
                    )
                    .background(Color(0xFFFFC107), CircleShape)
            )
        },
        headerConfig = ParallaxToolbarDefaults.headerConfig(
            height = HeaderHeight.Fixed(280.dp), parallaxMultiplier = 0f, fadeOnCollapse = false
        ),
        toolbarConfig = ParallaxToolbarDefaults.toolbarConfig(targetColor = MaterialTheme.colorScheme.surface),
        content = ParallaxContent.Lazy(content = { _ -> items(posts.size) { i -> ListItem(headlineContent = { Text(posts[i]) }) } })
    )
}
```

## Tabs under the toolbar

bottomContent stays pinned below the toolbar; the body starts beneath it.

```kotlin
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
        })
    )
}
```

## Pull to refresh

stretchEnabled lets a pull past the top stretch the header; onStretchTrigger fires on release.

```kotlin
@Composable
fun RefreshableFeedScreen(feed: List<String>, onRefresh: () -> Unit) {
    ComposeParallaxToolbarLayout(
        titleContent = { Text("Feed", color = Color.White, style = MaterialTheme.typography.headlineMedium) },
        headerContent = { HeaderArtwork() },
        headerConfig = ParallaxToolbarDefaults.headerConfig(
            height = HeaderHeight.Percentage(0.35f, maxHeight = 300.dp),
            stretchEnabled = true,
            stretchTriggerDistance = 96.dp
        ),
        onStretchTrigger = onRefresh,
        content = ParallaxContent.Lazy(content = { _ -> items(feed.size) { i -> ListItem(headlineContent = { Text(feed[i]) }) } })
    )
}
```

## Scaffold with a bottom bar

Pass the Scaffold padding as contentPadding so the last row clears the navigation bar.

```kotlin
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
        }
    ) { padding ->
        ComposeParallaxToolbarLayout(
            titleContent = { Text("Home", color = Color.White, style = MaterialTheme.typography.headlineMedium) },
            headerContent = { HeaderArtwork() },
            contentPadding = padding,
            content = ParallaxContent.Regular { _ ->
                rows.forEach { Card(Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 6.dp)) { Text(it, Modifier.padding(16.dp)) } }
            }
        )
    }
}
```

## iOS-style centered title with enter-always header

The collapsed title is centered and the header returns on any downward scroll.

```kotlin
@Composable
fun SettingsStyleScreen(rows: List<String>) {
    ComposeParallaxToolbarLayout(
        titleContent = { collapsed ->
            Text("Settings", color = if (collapsed) MaterialTheme.colorScheme.onSurface else Color.White,
                style = if (collapsed) MaterialTheme.typography.titleMedium else MaterialTheme.typography.headlineLarge)
        },
        headerContent = { Box(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.primary)) },
        headerConfig = ParallaxToolbarDefaults.headerConfig(
            height = HeaderHeight.Fixed(160.dp), scrollMode = ScrollMode.EnterAlways, snapOnRelease = true
        ),
        toolbarConfig = ParallaxToolbarDefaults.toolbarConfig(targetColor = MaterialTheme.colorScheme.surface, elevation = 2.dp),
        titleConfig = ParallaxToolbarDefaults.titleConfig(collapsedAlignment = Alignment.CenterHorizontally, collapsedScale = 0.85f),
        content = ParallaxContent.Lazy(content = { _ -> items(rows.size) { i -> ListItem(headlineContent = { Text(rows[i]) }) } })
    )
}
```

## Programmatic control and reading the fraction

Hoist the state to drive the header from outside and to react to the collapse fraction.

```kotlin
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
        content = ParallaxContent.Lazy(content = { _ -> items(rows.size) { i -> ListItem(headlineContent = { Text(rows[i]) }) } })
    )
}
```

## iOS hosting

Expose a screen to Swift from your shared module's iosMain. Swift wraps it in UIViewControllerRepresentable and applies ignoresSafeArea() so the header reaches the top edge.

```kotlin
fun PlaylistViewController(items: List<String>): UIViewController = ComposeUIViewController {
    MaterialTheme { BasicListScreen(items = items, onBack = {}) }
}
```

Swift side:

```swift
struct PlaylistView: UIViewControllerRepresentable {
    let items: [String]
    func makeUIViewController(context: Context) -> UIViewController {
        IosHostingKt.PlaylistViewController(items: items)
    }
    func updateUIViewController(_ vc: UIViewController, context: Context) {}
}

// Somewhere in your SwiftUI hierarchy:
PlaylistView(items: tracks).ignoresSafeArea()
```

Add `CADisableMinimumFrameDurationOnPhone = true` to the app's `Info.plist`; Compose refuses to
start on high refresh rate iPhones without it.
