---
name: compose-parallax-toolbar
description: Use when adding or changing a collapsing toolbar with a parallax header in a Compose Multiplatform or Jetpack Compose screen using the ComposeParallaxToolbar library (am.highapps.parallaxtoolbar).
---

# ComposeParallaxToolbar for coding agents

Library: `am.highapps.parallaxtoolbar:compose-parallax-toolbar-kmp`. Package `am.highapps.parallaxtoolbar`.
Current major: 2.x. Check the project's dependency line for the exact version; 1.x has a different API.

## The one entry point

```kotlin
ComposeParallaxToolbarLayout(
    titleContent = { collapsed -> Text("Title") },          // required
    headerContent = { Image(...) },                           // required, fills the expanded header
    content = ParallaxContent.Lazy(content = { _ -> items(n) { ... } }),  // required
    // optional slots, all receive `collapsed`:
    subtitleContent = { Text("Subtitle") },
    navigationIcon = { IconButton(...) { ... } },
    actions = { IconButton(...) { ... } },                  // RowScope
    overlayContent = { /* elements that travel into the toolbar, see moveBetween */ },
    bottomContent = { TabRow(...) },                        // pinned under the toolbar
    onStretchTrigger = { refresh() },                       // with headerConfig(stretchEnabled = true)
    contentPadding = scaffoldPadding,
    headerConfig = ParallaxToolbarDefaults.headerConfig(...),
    toolbarConfig = ParallaxToolbarDefaults.toolbarConfig(...),
    titleConfig = ParallaxToolbarDefaults.titleConfig(...),
    bodyConfig = ParallaxToolbarDefaults.bodyConfig(...),
    semanticsConfig = ParallaxToolbarDefaults.semanticsConfig(...),
    state = rememberParallaxToolbarState()
)
```

## Rules

1. Choose the body: `ParallaxContent.Regular { }` for a column, `ParallaxContent.Lazy(content = { _ -> items(...) })` for a list, `ParallaxContent.Custom { }` for a grid, staggered grid, pager or any other vertical scrollable that fills its size.
2. Configure through `ParallaxToolbarDefaults.headerConfig / toolbarConfig / titleConfig / bodyConfig / lazyColumnConfig / semanticsConfig`. Do not construct config classes by hand unless you need to.
3. Header height: `HeaderHeight.Fixed(dp)`, `HeaderHeight.AspectRatio(ratio, maxHeight)`, `HeaderHeight.Percentage(fraction 0..1, maxHeight)`; or the `headerConfigWithAspectRatio` / `headerConfigWithPercentage` factories.
4. Scroll behavior: `headerConfig(scrollMode = ScrollMode.ExitUntilCollapsed | EnterAlways | EnterAlwaysCollapsed, snapOnRelease = true)`.
5. Programmatic control: `val state = rememberParallaxToolbarState()`; `state.collapseFraction`, `state.isCollapsed`, `scope.launch { state.collapse() }`, `state.expand()`. The body keeps its own scroll position; use `state.scrollState` or `state.lazyListState` to scroll it.
6. Inside any slot, `collapseFraction`, `isCollapsed`, `state` and `layoutInfo` are available from the scope. Read `collapseFraction` inside `graphicsLayer { }` for per-frame effects, not in composition.
7. Per-element effects in the header: `Modifier.parallax(ratio)`, `.fadeOnCollapse()`, `.scaleOnCollapse(scale)`. Pair with `headerConfig(parallaxMultiplier = 0f, fadeOnCollapse = false)`. Elements that must stay visible when collapsed go in `overlayContent` with `Modifier.moveBetween(expandedAlignment, collapsedAlignment, ...)`, placed outside any `size` modifier.
8. Scaffold: pass the Scaffold padding as `contentPadding`. Do not add status bar padding; the layout handles the inset.
9. iOS: expose the screen with `ComposeUIViewController` from the shared module and add `CADisableMinimumFrameDurationOnPhone = true` to Info.plist, or the app crashes at launch.
10. Validation: invalid values throw `IllegalArgumentException` at construction with a message that names the field and the valid range.

## Mistakes to avoid (1.x habits)

- There is no `scrollState` parameter; use `state = rememberParallaxToolbarState(scrollState = ...)`.
- There are no `iconSize` / `iconSpacing` options; size icons inside the slots.
- Do not call `SimpleParallaxToolbarViewController` or other sample view controllers from Swift; they are not in the library.
- The library has no Material dependency; import Material widgets from your own design system dependency.
- `lazyContent` / `lazyColumnConfig` parameters belong to the deprecated overload; use `ParallaxContent.Lazy(content, config, lazyListState)`.

## Where to look

- `docs/API.md` for every parameter and default.
- `docs/RECIPES.md` for complete screens: list, grid, profile with avatar, tabs, pull-to-refresh, Scaffold, centered title, programmatic control, iOS hosting.
- `docs/PLATFORMS.md` for platform specifics. `docs/MIGRATION.md` for 1.x upgrades.
