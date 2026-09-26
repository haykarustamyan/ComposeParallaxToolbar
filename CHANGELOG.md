# Changelog

All notable changes to this project are documented here. The format follows
[Keep a Changelog](https://keepachangelog.com/en/1.1.0/) and the project uses
[Semantic Versioning](https://semver.org/): breaking changes only ship in a major version.

## 2.0.0

- **NEW**: the header collapses through nested scrolling. Any vertically scrollable composable can be the body via `ParallaxContent.Custom`, including `LazyVerticalGrid`, `LazyVerticalStaggeredGrid` and pagers. Dragging on the header itself collapses it.
- **NEW**: every slot runs in a `ParallaxToolbarScope` exposing `collapseFraction`, `isCollapsed` and `state`; the `actions` slot receives a `ParallaxActionsScope` that is also a `RowScope`. Existing `{ collapsed -> }` lambdas compile unchanged.
- **NEW**: accessibility semantics: state description with expand and collapse actions, explicit reading order with the toolbar first, the title as a heading, and the faded header or exited toolbar hidden from screen readers. Strings are configurable through `semanticsConfig`.
- **NEW**: `bottomContent` slot pinned under the toolbar for tabs or search; overscroll stretch with `stretchEnabled`, `stretchTriggerDistance` and an `onStretchTrigger` callback for pull-to-refresh; `collapsedAlignment` for a centered or end-aligned collapsed title; `maxHeight` on `HeaderHeight.Percentage` and `AspectRatio`; `alwaysElevated` on the toolbar config.
- **NEW**: per-element behaviors on the slot scope: `Modifier.parallax()`, `fadeOnCollapse()`, `scaleOnCollapse()` and `moveBetween()`; an `overlayContent` slot above the body and toolbar for elements that travel into the toolbar; `fadeOnCollapse` on the header config; and `state.layoutInfo` with the measured geometry.
- **NEW**: `ScrollMode` on the header config: `ExitUntilCollapsed` (default), `EnterAlways`, and `EnterAlwaysCollapsed`, which also slides the toolbar off screen. `state.toolbarExitFraction` reports the exit.
- **NEW**: `snapOnRelease` on the header config settles a half-collapsed header to the nearer end.
- **NEW**: desktop (JVM) and web (Kotlin/Wasm) targets, plus desktop and web sample apps.
- **NEW**: the collapse fraction is saved and restored across configuration changes and process death.
- **CHANGED**: `collapse()` and `expand()` move only the header; the body keeps its scroll position. `rememberParallaxToolbarState(initiallyCollapsed = true)` is the state-side equivalent of `isExpandedWhenFirstDisplayed = false`.
- **NEW**: explicit API mode, a checked-in ABI dump verified on every pull request, and generated API docs in the javadoc jar
- **NEW**: `ParallaxToolbarState` and `rememberParallaxToolbarState()`: observe `collapseFraction` and `isCollapsed`, and call `collapse()` / `expand()` (animated or not). The `scrollState` parameter is replaced by `state`; the deprecated overload still accepts a `ScrollState`.
- **NEW**: `parallaxMultiplier` on the header config, `height` on the toolbar config, and `collapsedScale` on the title config.
- **REMOVED**: `iconSize` and `iconSpacing` from `ParallaxToolbarConfig` and `ToolbarMinWidth`, `ToolbarIconSize`, `ToolbarIconSpacing` from `ParallaxToolbarDefaults`. Nothing ever read them; size icons inside the slots.
- **REMOVED**: the Material 3 dependency. The library now depends only on Compose UI and Foundation, so it works with any design system
- **CHANGED**: the header fades out over the collapse range instead of its full height, so it is fully hidden once the toolbar covers it
- **CHANGED**: the toolbar and title are measured in one pass by a custom layout instead of position callbacks, removing the one-frame jump on first display and rotation. The toolbar no longer uses Material's `TopAppBar` internally; its look is unchanged.
- **UPDATED**: Kotlin 2.4.20, Compose Multiplatform 1.12.1
- **UPGRADED**: Gradle 9.7.0, Android Gradle Plugin 9.3.3, Maven Publish Plugin 0.37.0
- **CHANGED**: Migrated to the `com.android.kotlin.multiplatform.library` plugin required by AGP 9
- **REMOVED**: `iosX64` target, since Compose Multiplatform no longer publishes artifacts for it
- **REMOVED**: unused `components-resources` and `components-ui-tooling-preview` dependencies
- **REMOVED**: sample screens, previews and iOS sample view controllers from the published artifact; they now live in the `sample` module. The library no longer depends on `material-icons-extended`.
- **CHANGED**: configuration classes are plain classes with `copy`, `equals`, `hashCode` and `toString` instead of data classes, so fields can be added later without breaking compiled consumers
- **CHANGED**: configuration classes are annotated `@Immutable` so the layout skips recomposition when its inputs are unchanged
- **CHANGED**: the legacy overload taking `lazyContent` is now marked `@Deprecated` with a replacement; it delegates to the `ParallaxContent` overload
- **FIXED**: `isExpandedWhenFirstDisplayed = false` now works for `ParallaxContent.Lazy`
- **FIXED**: the last status-bar-inset height of the body could never be scrolled into view; the body is now padded by the inset instead of offset
- **FIXED**: right-to-left layouts: content padding and the title's horizontal motion now follow the layout direction
- **FIXED**: horizontal `contentPadding` is applied to `ParallaxContent.Regular`
- **CHANGED**: the toolbar shadow (`elevation`) now appears only once the toolbar is collapsed, so it no longer draws a band across the expanded header
- **CHANGED**: the bottom filler for short regular content is computed exactly, removing extra blank space
- **FIXED**: Lazy content reported the toolbar as expanded when the first visible item offset was exactly 0
- **FIXED**: A gap the height of the status bar inset appeared under the header in edge-to-edge apps and on iOS, hiding the title
- **NEW**: Compose Multiplatform sample playground with Android and iOS host apps
- **TESTS**: Compose UI tests now run on Android (Robolectric) and the iOS simulator, with a Kover line-coverage gate

## 1.3.0

- **NEW**: Dynamic header height options with `HeaderHeight` sealed class:
  - `HeaderHeight.Fixed`: Fixed height in Dp (previous default behavior)
  - `HeaderHeight.AspectRatio`: Responsive height based on screen width and aspect ratio
  - `HeaderHeight.Percentage`: Adaptive height as percentage of screen height
- **NEW**: `contentPadding` parameter for seamless Scaffold integration
  - Prevents content from drawing behind bottom navigation bars
  - Automatically merges with LazyColumn's internal contentPadding
  - Works with both Regular and Lazy content types
- **ENHANCED**: Extended `ParallaxContent.Lazy` with comprehensive `LazyColumnConfig` customization
  - Factory method `ParallaxToolbarDefaults.lazyColumnConfig()` for easy configuration
- **UPDATED**: Kotlin 2.2.10, Compose Multiplatform 1.8.2, Compose UI 1.9.0
- **UPGRADED**: Gradle 9.0.0, Android Gradle Plugin 8.12.1, Maven Publish Plugin 0.34.0

## 1.2.0

- **NEW**: Introduced unified `ParallaxContent` sealed class system for content types:
  - `ParallaxContent.Regular` - Regular scrollable content using Column with vertical scroll
  - `ParallaxContent.Lazy` - LazyColumn content for better performance with large lists
- **API Enhancement**: New unified `ComposeParallaxToolbarLayout` with single `content: ParallaxContent` parameter
- **Backward Compatibility**: Legacy API maintained but marked as deprecated
- **Developer Experience**: Clearer API with explicit content type declarations

## 1.1.0

- Updated to Kotlin 2.1.20
- Updated to Compose Multiplatform 1.8.1
- Ios integration details update

## 1.0.0

- Initial release
- Basic parallax toolbar functionality
- Material 3 support
