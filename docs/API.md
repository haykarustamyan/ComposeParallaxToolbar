# API reference

Package `am.highapps.parallaxtoolbar`. Everything below is public API and covered by the
compatibility policy in the README.

## ComposeParallaxToolbarLayout

```kotlin
@Composable
fun ComposeParallaxToolbarLayout(
    titleContent: @Composable ParallaxToolbarScope.(collapsed: Boolean) -> Unit,
    headerContent: @Composable ParallaxToolbarScope.() -> Unit,
    content: ParallaxContent,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp),
    subtitleContent: (@Composable ParallaxToolbarScope.(collapsed: Boolean) -> Unit)? = null,
    navigationIcon: (@Composable ParallaxToolbarScope.(collapsed: Boolean) -> Unit)? = null,
    actions: (@Composable ParallaxActionsScope.(collapsed: Boolean) -> Unit)? = null,
    overlayContent: (@Composable ParallaxToolbarScope.() -> Unit)? = null,
    bottomContent: (@Composable ParallaxToolbarScope.() -> Unit)? = null,
    onStretchTrigger: (() -> Unit)? = null,
    headerConfig: ParallaxHeaderConfig = ParallaxToolbarDefaults.headerConfig(),
    toolbarConfig: ParallaxToolbarConfig = ParallaxToolbarDefaults.toolbarConfig(),
    titleConfig: ParallaxTitleConfig = ParallaxToolbarDefaults.titleConfig(),
    bodyConfig: ParallaxBodyConfig = ParallaxToolbarDefaults.bodyConfig(),
    semanticsConfig: ParallaxSemanticsConfig = ParallaxToolbarDefaults.semanticsConfig(),
    windowInsets: WindowInsets = ParallaxToolbarDefaults.windowInsets,
    collapseEnabled: Boolean = true,
    state: ParallaxToolbarState = rememberParallaxToolbarState()
)
```

| Parameter | Description |
|---|---|
| `titleContent` | Title. Placed at the bottom of the header while expanded and glides into the toolbar. |
| `headerContent` | Fills the expanded header, under the body. Typically an image. |
| `content` | The scrolling body; see [ParallaxContent](#parallaxcontent). |
| `modifier` | Applied to the whole layout. |
| `contentPadding` | Padding for the body, typically the `Scaffold` padding. Applied to `Regular` and merged into the `LazyColumn` of `Lazy`; not applied to `Custom`. |
| `subtitleContent` | Optional subtitle under the title. Fades out on collapse unless kept. |
| `navigationIcon` | Optional leading toolbar slot. |
| `actions` | Optional trailing toolbar slot. Its scope is also a `RowScope`. |
| `overlayContent` | Optional layer the size of the layout, drawn above the body and toolbar. Hosts elements that use `moveBetween`. |
| `bottomContent` | Optional row pinned under the toolbar: tabs, a search field. Rides the header's bottom edge while expanded; the body starts beneath it. Give it a background. |
| `onStretchTrigger` | Called when a stretch is released past `stretchTriggerDistance`. Needs `stretchEnabled`. |
| `headerConfig`, `toolbarConfig`, `titleConfig`, `bodyConfig`, `semanticsConfig` | See [Configuration](#configuration). |
| `windowInsets` | Insets the toolbar stays inside of. The top inset sits above the toolbar, under the header; the horizontal insets keep the navigation icon, actions and title clear of a display cutout. Default: system bars plus cutout, top and sides, as Material's top app bar. Pass `WindowInsets(0)` when the layout does not touch the window edge, such as in a dialog, a bottom sheet or a split pane. |
| `collapseEnabled` | `false` locks the header: scrolling and dragging no longer move it, the body still scrolls, and `collapse()`/`expand()` still work. For loading, empty or editing states. |
| `state` | See [ParallaxToolbarState](#parallaxtoolbarstate). |

Every slot lambda runs with a [ParallaxToolbarScope](#parallaxtoolbarscope) receiver and receives
`collapsed`, which is `true` once the header is fully collapsed.

A deprecated overload with the 1.x signature (`content` as a lambda, `scroll`, `lazyContent`,
`lazyListState`, `lazyColumnConfig`) still compiles and delegates here. It is removed in 3.0; see
[MIGRATION.md](MIGRATION.md).

## ParallaxContent

```kotlin
sealed class ParallaxContent {
    data class Regular(val content: @Composable ParallaxToolbarScope.(collapsed: Boolean) -> Unit)
    data class Lazy(
        val content: LazyListScope.(collapsed: Boolean) -> Unit,
        val config: LazyColumnConfig = LazyColumnConfig(),
        val lazyListState: LazyListState? = null
    )
    data class Custom(val content: @Composable ParallaxToolbarScope.(collapsed: Boolean) -> Unit)
}
```

- `Regular` lays the content out in a `Column` with vertical scroll backed by `state.scrollState`.
- `Lazy` uses a `LazyColumn` backed by `lazyListState`, or `state.lazyListState` when null.
- `Custom` places your composable in the space below the collapsed toolbar. It must fill that size
  and scroll vertically so nested scroll events reach the header. Grids, staggered grids and
  pagers all work.

The header collapses through nested scrolling: scrolling up collapses it before the body scrolls,
and a drag that starts on the header collapses it directly. When it expands depends on the
[ScrollMode](#scrollmode).

## ParallaxToolbarState

```kotlin
@Composable
fun rememberParallaxToolbarState(
    scrollState: ScrollState = rememberScrollState(),
    lazyListState: LazyListState = rememberLazyListState(),
    initiallyCollapsed: Boolean = false
): ParallaxToolbarState
```

| Member | Description |
|---|---|
| `collapseFraction: Float` | 0f while expanded, 1f once collapsed. |
| `isCollapsed: Boolean` | `collapseFraction >= 1f`. |
| `toolbarExitFraction: Float` | 0f on screen, 1f slid away. Moves only in `ScrollMode.EnterAlwaysCollapsed`. |
| `stretchPx: Float` | Current stretch past the expanded height while pulled down, in px. |
| `isScrollInProgress: Boolean` | True while the header is dragged, flung, snapping or animating. |
| `suspend fun collapse(animated = true, animationSpec = null)` | Collapses the header. The body keeps its scroll position. A null spec uses the header config's `animationSpec`. |
| `suspend fun expand(animated = true, animationSpec = null)` | Expands the header and brings an exited toolbar back. |
| `scrollState`, `lazyListState` | The scroll states backing `Regular` and `Lazy` content. |
| `layoutInfo` | See [ParallaxToolbarLayoutInfo](#parallaxtoolbarlayoutinfo). |

The collapse fraction is saved with `rememberSaveable`, so it survives configuration changes and
process death. `initiallyCollapsed` applies the first time only; `headerConfig.isExpandedWhenFirstDisplayed`
is the equivalent on the config side.

## ParallaxToolbarScope

Receiver of every slot.

| Member | Description |
|---|---|
| `state` | The layout's state. |
| `collapseFraction`, `isCollapsed` | Shortcuts to the state. |
| `layoutInfo` | Measured geometry. |
| `Modifier.parallax(ratio = 0.5f)` | Moves the element up by `ratio` of the collapse distance. |
| `Modifier.fadeOnCollapse(expandedAlpha = 1f, collapsedAlpha = 0f)` | Interpolates alpha with the collapse. |
| `Modifier.scaleOnCollapse(collapsedScale, origin = TransformOrigin.Center)` | Scales toward `collapsedScale`. |
| `Modifier.pin(stopAtTop = false)` | Keeps a header element still until the header's bottom edge reaches it, then rides that edge up. With `stopAtTop` it stops at the toolbar's top edge. Ignores `parallaxMultiplier`; pair with `fadeOnCollapse = false`. Header content ends under the toolbar, so use `moveBetween` in `overlayContent` for elements that must stay visible once collapsed. |
| `Modifier.moveBetween(expanded, collapsed, expandedPadding, collapsedPadding, collapsedScale = 1f)` | Glides the element from an alignment in the header area to an alignment in the toolbar area, scaling on the way. For `overlayContent`. Put it outside any `size` modifier. |

Reading `collapseFraction` in composition recomposes that slot on every scroll frame. The modifiers,
and reads inside `graphicsLayer { }` or `drawBehind { }`, stay on the draw path.

`ParallaxActionsScope` is the receiver of `actions`: a `ParallaxToolbarScope` that is also a `RowScope`.

## ParallaxToolbarLayoutInfo

`state.layoutInfo`, in pixels, updated on every layout pass. All zero until `isMeasured`.

| Property | Description |
|---|---|
| `widthPx`, `heightPx` | Size of the layout. |
| `topInsetPx` | Top window inset the toolbar and body are pushed down by. |
| `headerHeightPx` | Expanded header height, excluding the inset. |
| `toolbarHeightPx` | Toolbar height, excluding the inset. |
| `bottomHeightPx` | Height of `bottomContent`, 0 when absent. |
| `collapseRangePx` | `headerHeightPx - toolbarHeightPx`. |
| `headerOffsetPx` | Current collapse distance. |
| `toolbarExitOffsetPx` | Current toolbar exit distance. |
| `stretchPx` | Current stretch. |
| `currentHeaderBottomPx` | Current bottom edge of the header from the top of the layout. |

## ScrollMode

| Value | Scrolling up | Scrolling down |
|---|---|---|
| `ExitUntilCollapsed` (default) | collapses the header; toolbar stays | expands only once the body is at its top |
| `EnterAlways` | collapses the header; toolbar stays | expands immediately, wherever the body is |
| `EnterAlwaysCollapsed` | collapses the header, then the toolbar slides away | the toolbar returns immediately; the header expands at the top |

In `EnterAlwaysCollapsed` the body is measured to the viewport with the toolbar gone, so its last
stretch is reachable once the toolbar has exited. Pair with `snapOnRelease` if a half-exited
toolbar should never rest on screen.

## HeaderHeight

```kotlin
sealed class HeaderHeight {
    data class Fixed(val height: Dp)
    data class AspectRatio(val ratio: Float, val maxHeight: Dp = Dp.Unspecified)
    data class Percentage(val percentage: Float, val maxHeight: Dp = Dp.Unspecified)
}
```

`AspectRatio` divides the available width by `ratio` (`16f / 9f` for widescreen). `Percentage`
takes a fraction of the available height. Both are capped at `maxHeight` when it is specified.

## Configuration

All config types are immutable classes with `copy`, `equals`, `hashCode` and `toString`. Build
them with the `ParallaxToolbarDefaults` factories, which supply every default.

### ParallaxHeaderConfig

`headerConfig(...)`, `headerConfigWithAspectRatio(...)`, `headerConfigWithPercentage(...)`

| Field | Default | Description |
|---|---|---|
| `height` | `Fixed(450.dp)` | See [HeaderHeight](#headerheight). |
| `gradient` | `null` | Brush drawn over the header. When null, a vertical gradient from the toolbar's initial color to its target color covers the lower quarter. |
| `isExpandedWhenFirstDisplayed` | `true` | Start collapsed when false. |
| `parallaxMultiplier` | `0.5f` | How much of the collapse distance the header content moves by. 0f pins it. |
| `snapOnRelease` | `false` | Settle a partly collapsed header to a resting position when a drag or fling ends, or once wheel and trackpad input has been quiet for a moment. A fling settles in its direction; a plain release settles by `snapThreshold`. |
| `snapThreshold` | `0.5f` | Collapse progress at or past which a plain release settles collapsed. `0.5f` is the nearer position; `0.75f` favors expanded. Applies to the toolbar exit in `EnterAlwaysCollapsed` too. |
| `scrollMode` | `ExitUntilCollapsed` | See [ScrollMode](#scrollmode). |
| `fadeOnCollapse` | `true` | Fade the whole header out as it collapses. Turn off, with `parallaxMultiplier = 0f`, when elements use the scope modifiers. |
| `stretchEnabled` | `false` | Let a pull past the top stretch the header. Its content zooms and the body moves down; release springs back. |
| `stretchTriggerDistance` | `100.dp` | Stretch required at release for `onStretchTrigger` to fire. |
| `animationSpec` | `spring()` | Used when the header settles on its own: snaps, stretch releases and `collapse()`/`expand()` without a spec. Use `snap()` or a short `tween` to honor a reduced-motion setting. |

### ParallaxToolbarConfig

`toolbarConfig(...)`

| Field | Default | Description |
|---|---|---|
| `initialColor` | `Color.Transparent` | Toolbar background while expanded. |
| `targetColor` | `Color.Black` | Toolbar background once collapsed. |
| `elevation` | `0.dp` | Shadow once collapsed. |
| `animationSpec` | `tween(300)` | Animation between the two colors. |
| `height` | `64.dp` | Toolbar height, excluding the status bar inset. |
| `alwaysElevated` | `false` | Draw the shadow while expanded too. |

### ParallaxTitleConfig

`titleConfig(...)`

| Field | Default | Description |
|---|---|---|
| `paddingBottom` | `16.dp` | Distance from the header's bottom edge to the bottom of the title block, subtitle included, while expanded. Negative lets it straddle the edge. |
| `paddingStart` | `16.dp` | Start padding while expanded. |
| `collapsedPaddingStart` | `64.dp` | Start padding once collapsed, when a navigation icon is present and `collapsedAlignment` is `Start`. |
| `keepSubtitleAfterCollapse` | `false` | Keep the subtitle in the toolbar instead of hiding it. |
| `animateSubTitleHiding` | `true` | Fade the subtitle with the collapse; when false it disappears at the end. |
| `collapsedScale` | `1f` | Scale of the title block once collapsed, about its start edge. |
| `collapsedAlignment` | `Alignment.Start` | `Start` next to the navigation icon, `CenterHorizontally` centered between the slots, `End` before the actions. |

### ParallaxBodyConfig

`bodyConfig(minBottomSpacerHeight = 0.dp, backgroundColor = Color.Unspecified)`

| Field | Default | Description |
|---|---|---|
| `minBottomSpacerHeight` | `0.dp` | Extra space after the content of `Regular` and `Lazy` bodies. |
| `backgroundColor` | `Color.Unspecified` | Drawn behind the body. The body slides over the header, so gaps between items would show it through; pass the screen background unless every item paints its own. |

### LazyColumnConfig

`lazyColumnConfig(...)`, used by `ParallaxContent.Lazy`.

| Field | Default |
|---|---|
| `contentPadding` | `PaddingValues(0.dp)`, merged with the layout's `contentPadding` |
| `verticalArrangement` | `Arrangement.Top` |
| `horizontalAlignment` | `Alignment.Start` |
| `flingBehavior` | platform default |
| `userScrollEnabled` | `true` |
| `overscrollEffect` | platform default |

### ParallaxSemanticsConfig

`semanticsConfig(...)`: the strings announced to accessibility services. Defaults are English:
`"Expanded"`, `"Collapsed"`, `"Expand header"`, `"Collapse header"`.

## Accessibility

The root node announces the state description and offers the standard expand and collapse
semantic actions. Reading order is toolbar, header, bottom slot, overlay, body. The title is a
heading. The collapsed header, which the body and toolbar cover, and an exited toolbar are hidden
from accessibility.

## ParallaxToolbarDefaults

Constants: `HeaderHeightDp = 450.dp`, `HeaderParallaxMultiplier = 0.5f`, `ToolbarHeight = 64.dp`,
`TitlePaddingBottom = 16.dp`, `TitlePaddingStart = 16.dp`, `TitleCollapsedPaddingStart = 64.dp`,
`TitleCollapsedScale = 1f`, `BodyMinBottomSpacing = 0.dp`, `StretchTriggerDistance = 100.dp`,
`AnimationSpec = spring()`, `SnapThreshold = 0.5f`. `windowInsets` is the default inset set: system bars plus display
cutout, top and sides.
