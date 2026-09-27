# Changelog

All notable changes to this project are documented here. The format follows
[Keep a Changelog](https://keepachangelog.com/en/1.1.0/) and the project uses
[Semantic Versioning](https://semver.org/): breaking changes only ship in a major version.

## 2.0.0

Breaking release. See [docs/MIGRATION.md](docs/MIGRATION.md) for the upgrade steps; most screens
need one edit or none.

### Added

- Nested-scroll collapse: any vertically scrollable composable can be the body through `ParallaxContent.Custom`, including grids, staggered grids and pagers. Dragging on the header itself collapses it.
- `ScrollMode` on the header config: `ExitUntilCollapsed` (default), `EnterAlways`, and `EnterAlwaysCollapsed`, which also slides the toolbar off screen. `state.toolbarExitFraction` reports the exit.
- `snapOnRelease` on the header config settles a partly collapsed header to the nearest resting position.
- `ParallaxToolbarState` from `rememberParallaxToolbarState()`: `collapseFraction`, `isCollapsed`, `collapse()`, `expand()`, `layoutInfo`, and the scroll states for both content kinds. The collapse fraction is saved across configuration changes and process death.
- `ParallaxToolbarScope` as the receiver of every slot, with `collapseFraction`, `isCollapsed`, `state`, `layoutInfo` and the per-element modifiers `parallax()`, `fadeOnCollapse()`, `scaleOnCollapse()` and `moveBetween()`. The `actions` slot receives a `ParallaxActionsScope` that is also a `RowScope`. Existing `{ collapsed -> }` lambdas compile unchanged.
- `overlayContent` slot above the body and toolbar, for elements that travel into the toolbar.
- `Modifier.pin()` for header elements that stay in view until the header's bottom edge reaches them, such as a chip row.
- `backgroundColor` on the body config, drawn behind the body so gaps between items do not show the header sliding underneath.
- `snapThreshold` on the header config decides where a plain release settles; `collapseEnabled` on the layout locks the header while the body keeps scrolling.
- `bottomContent` slot pinned under the toolbar, for tabs or a search field.
- Overscroll stretch: `stretchEnabled` and `stretchTriggerDistance` on the header config, with an `onStretchTrigger` callback for pull-to-refresh.
- Header config: `parallaxMultiplier` and `fadeOnCollapse`. Toolbar config: `height` and `alwaysElevated`. Title config: `collapsedScale` and `collapsedAlignment`. `maxHeight` on `HeaderHeight.Percentage` and `HeaderHeight.AspectRatio`.
- Accessibility: state description with expand and collapse actions, reading order with the toolbar first, the title as a heading, and the faded header or exited toolbar hidden from screen readers. Strings come from `semanticsConfig`.
- Invalid configuration values throw at construction with a message naming the field and the valid range. A layout given unbounded height fails with a message that says so.
- `windowInsets` parameter, defaulting to the system bars plus the display cutout on the top and sides. The horizontal insets keep the toolbar slots clear of a cutout in landscape; pass `WindowInsets(0)` for a layout that does not touch the window edge.
- `animationSpec` on the header config for snaps, stretch releases and programmatic moves; `collapse()` and `expand()` accept a per-call spec. `state.isScrollInProgress` reports header motion.
- Desktop (JVM) and web (Kotlin/Wasm) targets.
- Sample playground shared by Android, iOS, desktop and web hosts; compiled recipes in `docs/RECIPES.md`; `llms.txt` and an agent skill file; a docs site on GitHub Pages.

### Changed

- The `scrollState` parameter is replaced by `state`. The deprecated 1.x overload still accepts a `ScrollState` and now carries a real `@Deprecated` annotation with a replacement.
- `collapse()` and `expand()` move only the header; the body keeps its scroll position.
- The toolbar and title are measured in one pass by a custom layout instead of position callbacks, removing the one-frame jump on first display and rotation. The toolbar no longer uses Material's `TopAppBar` internally; its look is unchanged.
- The toolbar shadow appears once collapsed, or always with `alwaysElevated`, instead of drawing a band across the expanded header.
- The header fades over the collapse range rather than its full height, so it is fully hidden once covered.
- Configuration types are plain `@Immutable` classes with `copy`, `equals`, `hashCode` and `toString` instead of data classes, so fields can be added without breaking compiled consumers.
- Explicit API mode with a checked-in ABI dump verified in CI; API docs generated into the javadoc jar.
- ktlint in CI with the IntelliJ code style, and a root `AGENTS.md` for coding agents working on the library.
- Toolchain: Kotlin 2.4.20, Compose Multiplatform 1.12.1, Gradle 9.7.0, Android Gradle Plugin 9.3.3 with the `com.android.kotlin.multiplatform.library` plugin, Maven Publish Plugin 0.37.0. Minimum iOS is 15.0.

### Removed

- The Material 3 dependency. The library depends only on Compose UI and Foundation.
- Sample screens, previews and iOS sample view controllers from the published artifact; they live in the `sample` module. The library no longer depends on `material-icons-extended`.
- `iconSize` and `iconSpacing` from `ParallaxToolbarConfig`, and `ToolbarMinWidth`, `ToolbarIconSize`, `ToolbarIconSpacing` from `ParallaxToolbarDefaults`. Nothing read them.
- The `iosX64` target, which Compose Multiplatform no longer publishes.
- Unused `components-resources` and `components-ui-tooling-preview` dependencies.

### Fixed

- A gap the height of the status bar inset appeared under the header in edge-to-edge apps and on iOS, hiding the title.
- The last status-bar-inset height of the body could never be scrolled into view.
- Lazy content reported the toolbar as expanded when the first visible item offset was exactly 0.
- `isExpandedWhenFirstDisplayed = false` had no effect with lazy content.
- Right-to-left layouts: content padding and the title's horizontal motion follow the layout direction.
- Horizontal `contentPadding` was dropped for regular content.
- Short regular content left extra blank space below it.
- Right-to-left layouts placed the navigation icon and actions on the wrong sides; centered and end-aligned collapsed titles were offset by the navigation icon width.
- A collapsed title could run under the actions when the collapsed start padding was wider than the navigation icon.
- Mouse wheel and trackpad scrolling on desktop and web could leave the header stretched, since they never fling; a stretch now follows a held pointer only.
- With the header collapsed and the list at its top, a wheel tick over the list did nothing on desktop and web: Compose drops wheel events the list cannot use before nested scroll runs. The layout now catches them, and drags on the toolbar move the header as well.
- `snapOnRelease` never fired for wheel and trackpad scrolling, which has no release; the header now settles once such input has been quiet for a moment.
- A downward fling on an expanded header stretched it for one frame.
- The collapsed header is hidden from screen readers whether or not it fades.
- Without a subtitle the expanded title hung 16dp below the header's bottom edge and was clipped by the body. `paddingBottom` now measures from the header's bottom edge to the bottom of the title block in both cases, and its default is `16.dp`; screens that pass their own value sit 16dp higher than in 1.x when they have a subtitle.

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
