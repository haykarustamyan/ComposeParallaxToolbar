# ComposeParallaxToolbar

[![Maven Central](https://img.shields.io/maven-central/v/am.highapps.parallaxtoolbar/compose-parallax-toolbar-kmp)](https://central.sonatype.com/artifact/am.highapps.parallaxtoolbar/compose-parallax-toolbar-kmp)
[![Kotlin](https://img.shields.io/badge/kotlin-2.4.20-blue.svg?logo=kotlin)](https://kotlinlang.org)
[![Compose Multiplatform](https://img.shields.io/badge/Compose%20Multiplatform-1.12.1-blue)](https://github.com/JetBrains/compose-multiplatform)
[![Platforms](https://img.shields.io/badge/platforms-Android%20|%20iOS%20|%20Desktop%20|%20Web-green.svg)](#compatibility)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)

A collapsing toolbar with a parallax header for Compose Multiplatform. The header collapses as
the body scrolls, the title glides into the toolbar, and every slot knows how far along it is.
It depends only on Compose UI and Foundation, so it works with Material 2, Material 3 or your own
design system, on Android, iOS, desktop and web.

![Parallax toolbar animation](https://github.com/haykarustamyan/ComposeParallaxToolbar/raw/main/raw/main/images/parallax_gif.gif)

## Features

- **Any scrollable body.** A column, a `LazyColumn`, or anything else that scrolls: grids,
  staggered grids, pagers. The header collapses through nested scrolling.
- **Scroll modes.** Exit until collapsed, enter always, or enter always collapsed with the
  toolbar sliding away. Optional snap on release.
- **Header height** as a fixed size, an aspect ratio or a fraction of the screen, with a cap.
- **Per-element behaviors.** Give any header element its own parallax, fade or scale, and glide
  elements such as an avatar from the header into the toolbar.
- **Bottom slot** pinned under the toolbar for tabs or a search field.
- **Overscroll stretch** with a trigger callback for pull-to-refresh.
- **Hoisted state** with the collapse fraction, `collapse()` and `expand()`, saved across
  configuration changes and process death.
- **Accessibility** built in: state announcements, expand and collapse actions, reading order,
  and a heading for the title.
- **Right-to-left** layouts, edge-to-edge insets and the iPhone status bar handled for you.

## Installation

Add the dependency to the source set that holds your screens. In a Compose Multiplatform project
that is `commonMain`; in an Android-only project it is the app module.

```kotlin
dependencies {
    implementation("am.highapps.parallaxtoolbar:compose-parallax-toolbar-kmp:2.0.0")
}
```

iOS, desktop and web apps consume it through their shared Kotlin module; nothing is imported on
the Swift or JavaScript side. See the [platform guide](docs/PLATFORMS.md) for the one iOS
`Info.plist` key Compose needs.

## Quick start

```kotlin
import am.highapps.parallaxtoolbar.ComposeParallaxToolbarLayout
import am.highapps.parallaxtoolbar.ParallaxContent

@Composable
fun AlbumScreen(album: Album) {
    ComposeParallaxToolbarLayout(
        titleContent = { collapsed ->
            Text(
                album.title,
                style = if (collapsed) MaterialTheme.typography.titleMedium else MaterialTheme.typography.headlineMedium
            )
        },
        subtitleContent = { Text(album.artist) },
        headerContent = {
            Image(album.cover, contentDescription = null, Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
        },
        navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, null) } },
        actions = { IconButton(onClick = onShare) { Icon(Icons.Default.Share, null) } },
        content = ParallaxContent.Lazy(
            content = { collapsed -> items(album.tracks) { TrackRow(it) } },
            config = ParallaxToolbarDefaults.lazyColumnConfig(contentPadding = PaddingValues(16.dp))
        )
    )
}
```

The Material calls are the app's choice; the library itself has no Material dependency. Every
slot receives `collapsed` and runs in a `ParallaxToolbarScope`, which also exposes the continuous
`collapseFraction`. Use `ParallaxContent.Regular` for a scrolling column, or
`ParallaxContent.Custom` to bring your own scrollable:

```kotlin
content = ParallaxContent.Custom {
    LazyVerticalGrid(GridCells.Fixed(2), Modifier.fillMaxSize()) { items(photos) { PhotoCell(it) } }
}
```

## Configuration

Everything is set through small immutable configs built by `ParallaxToolbarDefaults`:

```kotlin
ComposeParallaxToolbarLayout(
    // ...slots...
    headerConfig = ParallaxToolbarDefaults.headerConfigWithPercentage(
        heightPercentage = 0.4f,
        maxHeight = 320.dp,
        scrollMode = ScrollMode.EnterAlways,
        snapOnRelease = true,
        stretchEnabled = true
    ),
    onStretchTrigger = { viewModel.refresh() },
    toolbarConfig = ParallaxToolbarDefaults.toolbarConfig(targetColor = colorScheme.surface, elevation = 3.dp),
    titleConfig = ParallaxToolbarDefaults.titleConfig(collapsedScale = 0.8f, collapsedAlignment = Alignment.CenterHorizontally),
    bottomContent = { TabRow(/* ... */) },
    contentPadding = scaffoldPadding
)
```

| Config | What it controls |
|---|---|
| `headerConfig` | Height, gradient, initial state, parallax factor, scroll mode, snap, fade, stretch |
| `toolbarConfig` | Colors and their animation, elevation, height |
| `titleConfig` | Title and subtitle padding, collapsed scale and alignment, subtitle behavior |
| `bodyConfig` | Extra space after the content |
| `semanticsConfig` | Strings announced to screen readers |

The [API reference](docs/API.md) lists every parameter and default.

## State and effects

```kotlin
val state = rememberParallaxToolbarState()
val scope = rememberCoroutineScope()

ComposeParallaxToolbarLayout(/* ... */, state = state)

Text("${(state.collapseFraction * 100).toInt()} %")
Button(onClick = { scope.launch { state.collapse() } }) { Text("Collapse") }
```

Inside any slot the scope offers modifiers for per-element effects, and an `overlayContent` slot
above everything hosts elements that travel into the toolbar:

```kotlin
headerConfig = ParallaxToolbarDefaults.headerConfig(parallaxMultiplier = 0f, fadeOnCollapse = false),
headerContent = {
    Image(cover, null, Modifier.fillMaxSize().parallax(0.5f).fadeOnCollapse())
},
overlayContent = {
    Avatar(Modifier.size(72.dp).moveBetween(
        expanded = Alignment.BottomStart, collapsed = Alignment.CenterEnd,
        expandedPadding = PaddingValues(start = 16.dp, bottom = 72.dp),
        collapsedPadding = PaddingValues(end = 104.dp), collapsedScale = 0.5f
    ))
}
```

## Documentation

- [API reference](docs/API.md): every parameter, config, state member and modifier.
- [Recipes](docs/RECIPES.md): complete screens for common tasks, compiled on every CI run.
- [Platform guide](docs/PLATFORMS.md): Android edge-to-edge and `Scaffold`, iOS hosting, desktop, web.
- [Migrating from 1.x](docs/MIGRATION.md).
- [Changelog](CHANGELOG.md).

**Using an AI coding assistant?** Point it at [llms.txt](llms.txt), or drop
[docs/agents/SKILL.md](docs/agents/SKILL.md) into your project's agent instructions. It holds the
current signatures, the rules that matter, and the 1.x habits to avoid. [llms-full.txt](llms-full.txt)
is every guide in one file.

## Sample app

The `sample` module is an interactive playground shared by Android, iOS, desktop and web. Every
option above is a switch or slider in its configuration sheet, and the library's fixed sample
screens are one tap away. See [sample/README.md](sample/README.md) for how to run each host.

## Compatibility

| | Tested with |
|---|---|
| Kotlin | 2.4.20 |
| Compose Multiplatform | 1.12.1 |
| Android | API 24+ |
| iOS | 15.0+, arm64 devices and Apple Silicon simulators |
| Desktop | JVM 17+ |
| Web | Kotlin/Wasm |

The library depends only on `org.jetbrains.compose.ui:ui` and `org.jetbrains.compose.foundation:foundation`.

## Versioning

Semantic versioning: breaking changes ship only in a major version. The public API is explicit
and its dump under `compose-parallax-toolbar-kmp/api/` is checked on every pull request. A
deprecated API keeps working for at least one minor release, carries a `ReplaceWith`, and is
removed in the next major. Report bugs through [issues](https://github.com/haykarustamyan/ComposeParallaxToolbar/issues)
and security concerns as described in [SECURITY.md](SECURITY.md).

## Contributing

See [CONTRIBUTING.md](CONTRIBUTING.md). The project follows the [code of conduct](CODE_OF_CONDUCT.md).

## Acknowledgments

The original motion was inspired by Morad Azzouzi's article
[Collapsing toolbar with parallax effect and curve motion in Jetpack Compose](https://proandroiddev.com/collapsing-toolbar-with-parallax-effect-and-curve-motion-in-jetpack-compose-9ed1c3c0393f).

## Author

Created by [Hayk Arustamyan](https://github.com/haykarustamyan). If the library saves you time,
a star on GitHub or a [coffee](https://ko-fi.com/haykarustamyan) is appreciated.

## License

[MIT](LICENSE)
