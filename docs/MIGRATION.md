# Migrating from 1.x to 2.0

2.0.0 is a breaking release. Most screens need one or two edits; many need none.

## What changed

| 1.x | 2.0 |
|---|---|
| `scrollState` parameter | `state = rememberParallaxToolbarState(scrollState = ...)` |
| `ParallaxContent.Lazy(lazyListState = ...)` | still accepted; `state.lazyListState` is the default |
| Sample view controllers such as `SimpleParallaxToolbarViewController` in the framework | moved to the `sample` module; write your own `ComposeUIViewController` in your shared module |
| `ParallaxToolbarConfig.iconSize`, `iconSpacing`; `ParallaxToolbarDefaults.ToolbarMinWidth`, `ToolbarIconSize`, `ToolbarIconSpacing` | removed; nothing read them. Size icons inside the slots |
| Material 3 dependency | removed; bring your own design system |
| `iosX64` target | removed; Compose Multiplatform no longer publishes it |
| Config data classes | plain classes with `copy`, `equals`, `hashCode`, `toString`; `componentN` destructuring no longer compiles |
| Slot lambdas `(Boolean) -> Unit` | `ParallaxToolbarScope.(Boolean) -> Unit`; existing `{ collapsed -> }` lambdas compile unchanged |
| Toolbar shadow drawn while expanded | drawn once collapsed, or always with `toolbarConfig(alwaysElevated = true)` |
| Header faded over its full height | faded over the collapse range, so it is fully hidden once covered |

## Behavior changes you may notice

- The header now collapses through nested scrolling. Scrolling feels the same, and any scrollable
  can be the body. Dragging on the header itself now collapses it too.
- `collapse()` and `expand()` move only the header; the body keeps its scroll position. To also
  scroll to the top, use `state.scrollState` or `state.lazyListState`.
- The collapse fraction is saved across configuration changes and process death.
- Short content no longer leaves extra blank space below it, and the last part of long content
  is reachable under the status bar inset on every platform.

## Step by step

1. Bump the dependency to `2.0.0`.
2. If you passed `scrollState`, wrap it: `state = rememberParallaxToolbarState(scrollState = yourState)`.
3. If a Swift file called a sample view controller from the framework, create the equivalent
   `ComposeUIViewController` in your shared module. The [platform guide](PLATFORMS.md#ios) shows the pattern.
4. Delete any `iconSize` or `iconSpacing` arguments.
5. If you relied on the Material 3 dependency transitively, add it to your own module.
6. Build. The deprecated 1.x overload still compiles with a warning and a quick-fix; it is removed in 3.0.

## New in 2.0 worth adopting

`ParallaxContent.Custom` for grids, `ScrollMode`, `snapOnRelease`, per-element modifiers with the
`overlayContent` slot, `bottomContent` for tabs, `stretchEnabled` with `onStretchTrigger`,
`collapsedAlignment`, `maxHeight` on relative header heights, and `semanticsConfig` for
localized accessibility strings. All are described in the [API reference](API.md).
