# Platform guide

The library is pure common Compose code. This page covers what differs per host.

## Android

**Edge-to-edge.** The layout reads the window insets itself: the header covers the status bar,
the toolbar and body are pushed below it, and in landscape the navigation icon, actions and title
stay clear of a display cutout. Call `enableEdgeToEdge()` in your activity and do not add a
status bar padding of your own around the layout. Status bar icon color is your app's concern;
switch it when `collapsed` flips if the toolbar colors need it.

**Scaffold.** Pass the padding `Scaffold` gives you as `contentPadding` so the body clears a bottom
bar or a floating action button. The horizontal and bottom padding is applied to `Regular` and
merged into the `LazyColumn` for `Lazy`. For `Custom` content apply it inside your scrollable.

```kotlin
Scaffold(bottomBar = { NavigationBar { /* ... */ } }) { padding ->
    ComposeParallaxToolbarLayout(
        titleContent = { Text("Feed") },
        headerContent = { HeaderImage() },
        contentPadding = padding,
        content = ParallaxContent.Lazy(content = { items(posts) { PostRow(it) } })
    )
}
```

**Previews.** The layout renders in `@Preview`. The sample module keeps a set of previews under
`sample/src/androidMain`.

## iOS

**Swift-only apps.** Compose UI has no Swift API, so the library, like every Compose
Multiplatform library, is used from Kotlin. An existing Swift app adds one Kotlin Multiplatform
module, which can be a single file holding the screen, and keeps everything else in Swift. The
1.x XCFramework only exposed the sample screens; it could not be used to build your own.

Add the dependency to your shared module's `commonMain` and build screens there. Expose them to
Swift through a `ComposeUIViewController`, as with any Compose Multiplatform screen:

```kotlin
// shared module, iosMain
fun AlbumViewController(album: Album): UIViewController = ComposeUIViewController { AlbumScreen(album) }
```

```swift
struct AlbumView: UIViewControllerRepresentable {
    func makeUIViewController(context: Context) -> UIViewController { AlbumViewControllerKt.AlbumViewController(album: album) }
    func updateUIViewController(_ vc: UIViewController, context: Context) {}
}

AlbumView().ignoresSafeArea()
```

Use `ignoresSafeArea()` so the header can extend under the status bar; the layout places the
toolbar and body below the inset on its own.

**Info.plist.** Compose for iOS refuses to start on high refresh rate iPhones without this key,
and the failure is a crash on launch:

```xml
<key>CADisableMinimumFrameDurationOnPhone</key>
<true/>
```

**Minimum version.** Kotlin 2.4 targets iOS 15.0. Only arm64 devices and Apple Silicon simulators
are supported; Compose Multiplatform no longer publishes the Intel simulator target.

## Desktop

Add the dependency to the `jvm` source set or `commonMain`. Mouse wheel scrolling drives the
header through nested scroll, including expansion when scrolling back up. Dragging the header
with the mouse works as on touch. A stretch follows a held pointer only, so the wheel never
leaves the header stretched.

**Scrollbar.** Put the list and a `VerticalScrollbar` in one `Box` as `ParallaxContent.Custom`;
the state's `lazyListState` or `scrollState` feeds the adapter. See the
[Desktop scrollbar](RECIPES.md#desktop-scrollbar) recipe.

## Web

Add the dependency to the `wasmJs` source set or `commonMain` and mount your screen with
`ComposeViewport`. Wheel and touch input behave as on desktop and mobile. Resizing the browser
window restarts the Compose viewport, which is standard Compose for Web behavior.

## Insets and the toolbar height

The `windowInsets` parameter defaults to the system bars plus the display cutout, top and sides,
the same set Material's top app bar uses. It is zero on desktop and web. The top inset sits
under the header and above the toolbar; the horizontal insets inset the toolbar slots. Pass
`WindowInsets(0)` when the layout does not touch the window edge, for example in a dialog, a
bottom sheet or a split pane, or your own set to pick different bars. `toolbarConfig(height = ...)`
sets the toolbar height excluding the top inset; `state.layoutInfo` exposes the resolved values
in pixels if you need them.

The layout needs a bounded height. It fills the space it is given and scrolls its body inside
it, so it cannot sit in a vertically scrolling parent; give it a fixed height or a weight there.
