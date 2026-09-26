# ComposeParallaxToolbar - Compose Multiplatform

[![Maven Central](https://img.shields.io/maven-central/v/am.highapps.parallaxtoolbar/compose-parallax-toolbar-kmp)](https://search.maven.org/artifact/am.highapps.parallaxtoolbar/compose-parallax-toolbar-kmp)
[![Kotlin](https://img.shields.io/badge/kotlin-v2.4.20-blue.svg?logo=kotlin)](http://kotlinlang.org)
[![Compose Multiplatform](https://img.shields.io/badge/Compose%20Multiplatform-v1.12.1-blue)](https://github.com/JetBrains/compose-multiplatform)
[![Platform](https://img.shields.io/badge/platform-Android%20|%20iOS%20|%20Desktop%20|%20Web-green.svg)](https://github.com/haykarustamyan/ComposeParallaxToolbar)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)

A fully customizable Material 3 parallax toolbar layout built with **Compose Multiplatform**. This cross-platform library provides a modern, material design parallax effect for app bars that animate smoothly as users scroll through content, working seamlessly on Android, iOS.

![Parallax Toolbar Animation](https://github.com/haykarustamyan/ComposeParallaxToolbar/raw/main/raw/main/images/parallax_gif.gif)

## Features

- **Material 3 Integration**: Built with Material 3 components, theming, and color system
- **Responsive Header Heights**: Configure headers using fixed heights, aspect ratios, or screen percentages for perfect scaling across all devices
- **Highly Customizable**: Full control over colors, dimensions, animations, and behaviors
- **Parallax Effect**: Smooth transitions and animations while scrolling
- **Title & Subtitle**: Animated title and subtitle with customizable transitions
- **Curved Motion**: Beautiful quadratic Bézier curve animations for title transitions
- **Cross-Platform**: Full support for Android, iOS with native integration

## Installation

<details open>
<summary><b>Compose Multiplatform Projects</b></summary>

For Compose Multiplatform projects, add the dependency to your shared module's `build.gradle.kts`:

```kotlin
kotlin {
    sourceSets {
        commonMain {
            dependencies {
                implementation("am.highapps.parallaxtoolbar:compose-parallax-toolbar-kmp:2.0.0")
            }
        }
    }
}
```

This will make the library available in all your platform-specific source sets (androidMain, iosMain, etc.).
</details>

<details>
<summary><b>Android Only Projects</b></summary>

#### Gradle (Kotlin DSL)

Add the dependency to your module's build.gradle.kts file:

```kotlin
dependencies {
    implementation("am.highapps.parallaxtoolbar:compose-parallax-toolbar-kmp:2.0.0")
}
```

#### Gradle (Groovy)

```groovy
dependencies {
    implementation 'am.highapps.parallaxtoolbar:compose-parallax-toolbar-kmp:2.0.0'
}
```
</details>

<details>
<summary><b>iOS Installation</b></summary>

### Direct XCFramework Integration

For direct integration:

1. Download the project
   from [GitHub repository](https://github.com/haykarustamyan/ComposeParallaxToolbar)

2. After downloading, run the following command to build the iOS framework:
```bash
./gradlew buildIosFramework
```

3. Create XCFramework by running:
```bash
xcodebuild -create-xcframework \
-framework compose-parallax-toolbar-kmp/build/bin/iosArm64/releaseFramework/compose_parallax_toolbar_kmp.framework \
-framework compose-parallax-toolbar-kmp/build/bin/iosSimulatorArm64/releaseFramework/compose_parallax_toolbar_kmp.framework \
-output compose-parallax-toolbar-kmp.xcframework
```

4. **Integrate XCFramework with Xcode:**
   - Open ios folder in Xcode
   - Add the XCFramework to ios project:
     - Go to Targets → Project → General → Frameworks, Libraries, and Embedded Content
     - Click + → Add Other → Add Files
     - Navigate to generated `compose-parallax-toolbar-kmp.xcframework` and add it

5. Import in your Swift files:

```swift
import compose_parallax_toolbar_kmp
```
</details>

## Basic Usage

Here's a simple example of how to implement the parallax toolbar in your Compose code:

```kotlin
import am.highapps.parallaxtoolbar.ComposeParallaxToolbarLayout
import am.highapps.parallaxtoolbar.ParallaxContent

@Composable
fun MyScreen() {
    ComposeParallaxToolbarLayout(
        titleContent = { isCollapsed ->
            Text(
                text = "My App",
                color = if (isCollapsed) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
                style = if (isCollapsed)
                    MaterialTheme.typography.titleMedium
                else
                    MaterialTheme.typography.headlineMedium
            )
        },
        headerContent = {
            // Your header image or content
            Icon(
                imageVector = Icons.Default.Share,
                contentDescription = null,
                modifier = Modifier.fillMaxSize()
            )
        },
        content = ParallaxContent.Regular { isCollapsed ->
            // Your main content
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                repeat(10) { index ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                    ) {
                        Text(
                            text = "Item ${index + 1}",
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }
            }
        }
    )
}
```

## Platform Integration

<details>
<summary><b>Android Integration</b></summary>

For Android, you can use the component directly in your Compose UI:

```kotlin
import am.highapps.parallaxtoolbar.ParallaxContent

@Composable
fun AndroidScreen() {
    // Use Material Theme from your Android app
    MaterialTheme {
        ComposeParallaxToolbarLayout(
            titleContent = { isCollapsed ->
                Text(
                    text = "Android App",
                    fontSize = if (isCollapsed) 18.sp else 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isCollapsed) 
                        MaterialTheme.colorScheme.onSurface 
                    else 
                        Color.White
                )
            },
            headerContent = {
                Box(
                    modifier = Modifier
                        .background(MaterialTheme.colorScheme.primary)
                        .fillMaxSize()
                )
            },
            content = ParallaxContent.Regular { isCollapsed ->
                LazyColumn(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(20) { index ->
                        ListItem(
                            headlineContent = { Text("Item $index") },
                            supportingContent = { Text("Supporting text") }
                        )
                    }
                }
            }
        )
    }
}
```
</details>

<details>
<summary><b>iOS Integration</b></summary>

### UIKit Integration

```swift
import UIKit
import compose_parallax_toolbar_kmp

class MyViewController: UIViewController {
    override func viewDidLoad() {
        super.viewDidLoad()
        
        let composeVC = MyToolbarViewControllerKt.MyToolbarViewController()
        addChild(composeVC)
        view.addSubview(composeVC.view)
        composeVC.view.frame = view.bounds
        composeVC.didMove(toParent: self)
    }
}
```

### SwiftUI Integration

```swift
import SwiftUI
import compose_parallax_toolbar_kmp

struct ComposeToolbarView: UIViewControllerRepresentable {
    func makeUIViewController(context: Context) -> UIViewController {
        return MyToolbarViewControllerKt.MyToolbarViewController()
    }
    func updateUIViewController(_ uivc: UIViewController, context: Context) {}
}

struct ContentView: View {
    var body: some View {
        ComposeToolbarView()
            .ignoresSafeArea(edges: .top)  // Optional: makes the toolbar use full height
    }
}
```

`MyToolbarViewController` is a function you write in your own shared module, as shown below.
Ready-made examples such as `SimpleParallaxToolbarViewController` and `IOSPhotoGalleryViewController`
live in the `sample` module of this repository, not in the published library.

### Custom Implementations

To create custom implementations, you need to add your custom composable functions in the **common
code** (specifically in the iOS part of the multiplatform module), then use them from your iOS
application.

**Step 1:** Add your custom implementation in the common code (iOS part):

```kotlin
// Add this in src/iosMain/kotlin (common code - iOS part)
import am.highapps.parallaxtoolbar.ParallaxContent

fun MyCustomToolbarViewController() = ComposeUIViewController {
    MaterialTheme {
        ComposeParallaxToolbarLayout(
            titleContent = { isCollapsed ->
                Text(
                    text = "My Custom Title",
                    fontSize = if (isCollapsed) 18.sp else 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isCollapsed) 
                        MaterialTheme.colorScheme.onSurface 
                    else 
                        Color.White
                )
            },
            headerContent = {
                Box(
                    modifier = Modifier
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(Color(0xFF4CAF50), Color(0xFF2E7D32))
                            )
                        )
                        .fillMaxSize()
                )
            },
            content = ParallaxContent.Lazy(
                content = { isCollapsed ->
                    items(50) { index ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                        ) {
                            Text(
                                text = "Custom Item ${index + 1}",
                                modifier = Modifier.padding(16.dp)
                            )
                        }
                    }
                },
                config = ParallaxToolbarDefaults.lazyColumnConfig(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ),
                lazyListState = rememberLazyListState()
            )
        )
    }
}
```

**Step 2:** After adding your custom implementation, rebuild the framework:
```bash
./gradlew buildIosFramework
```

**Step 3:** Use it in your iOS application:
```swift
// In your iOS app
let customVC = MyCustomToolbarViewControllerKt.MyCustomToolbarViewController()
```

> **Note:** Custom implementations cannot be created directly in the iOS application code. They must
> be added to the common multiplatform code (iOS part) and then accessed from the iOS app.

For more details:
- **[iOS Integration Guide](sample/docs/iOS-README.md)** - Setup and basic usage
- **[iOS Sample Implementation Guide](sample/docs/iOS-Samples.md)** - Detailed examples
</details>

<details open>
<summary><b>Advanced Customization</b></summary>

For more control, use the `ParallaxToolbarDefaults` object to customize various aspects:

```kotlin
import am.highapps.parallaxtoolbar.ParallaxContent
import am.highapps.parallaxtoolbar.ParallaxToolbarDefaults

// Create customized configurations using factory methods
// NEW: Responsive header height with aspect ratio
val headerConfig = ParallaxToolbarDefaults.headerConfigWithAspectRatio(
    aspectRatio = 16f/9f,  // Responsive widescreen header
    gradient = Brush.verticalGradient(
        colors = listOf(
            Color.Transparent,
            Color(0x80000000),
            Color(0xCC000000)
        ),
        startY = 300f
    )
)

val titleConfig = ParallaxToolbarDefaults.titleConfig(
    paddingStart = 20.dp,
    collapsedPaddingStart = 60.dp,
    keepSubtitleAfterCollapse = true,
    animateSubTitleHiding = true
)

val toolbarConfig = ParallaxToolbarDefaults.toolbarConfig(
    initialColor = Color.Transparent,
    targetColor = MaterialTheme.colorScheme.surface,
    elevation = 2.dp,
    animationSpec = tween(durationMillis = 400)
)

val bodyConfig = ParallaxToolbarDefaults.bodyConfig(
    minBottomSpacerHeight = 32.dp
)

ComposeParallaxToolbarLayout(
    // Required parameters
    titleContent = { /* ... */ },
    headerContent = { /* ... */ },
    content = ParallaxContent.Regular { /* ... */ },
    
    // Scaffold integration (important for bottom navigation)
    contentPadding = paddingValues, // Pass from Scaffold for proper spacing
    
    // Optional customizations
    headerConfig = headerConfig,
    toolbarConfig = toolbarConfig,
    titleConfig = titleConfig,
    bodyConfig = bodyConfig
)
```

### LazyColumn Customization

For `ParallaxContent.Lazy`, you can customize the LazyColumn behavior using `LazyColumnConfig`:

```kotlin
import am.highapps.parallaxtoolbar.ParallaxContent
import am.highapps.parallaxtoolbar.ParallaxToolbarDefaults

// Create a custom LazyColumn configuration
val lazyConfig = ParallaxToolbarDefaults.lazyColumnConfig(
    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
    verticalArrangement = Arrangement.spacedBy(12.dp),
    horizontalAlignment = Alignment.CenterHorizontally,
    userScrollEnabled = true,
    flingBehavior = null, // Uses default
    overscrollEffect = null // Uses default
)

// Example with external LazyListState control
val lazyListState = rememberLazyListState()

// You can programmatically control scrolling
LaunchedEffect(someCondition) {
    lazyListState.animateScrollToItem(index = 10)
}

ComposeParallaxToolbarLayout(
    titleContent = { isCollapsed ->
        Text(
            text = "Custom LazyList",
            fontSize = if (isCollapsed) 18.sp else 24.sp,
            fontWeight = FontWeight.Bold
        )
    },
    headerContent = { /* ... */ },
    
    // For Scaffold integration (merges with LazyColumn's own contentPadding)
    contentPadding = paddingValues, // External padding (e.g., from Scaffold)
    
    content = ParallaxContent.Lazy(
        content = { isCollapsed ->
            items(100) { index ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp)
                ) {
                    Text(
                        text = "Item ${index + 1}",
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        },
        config = lazyConfig, // Internal LazyColumn padding (16dp) + External padding = Total padding
        lazyListState = lazyListState // Pass your controlled state
    )
)
```
</details>

## Documentation

For detailed information on all components, parameters, and configuration options, see the [API Documentation](docs/API.md).

<details>
<summary><b>Any scrollable as the body</b></summary>

The header collapses through nested scrolling, so the body can be any vertically scrollable
composable. `ParallaxContent.Regular` and `ParallaxContent.Lazy` are conveniences;
`ParallaxContent.Custom` takes a grid, a staggered grid, a pager whose pages scroll, or your own
scrollable. It receives the full width and the height below the collapsed toolbar; fill that size.

```kotlin
ComposeParallaxToolbarLayout(
    titleContent = { Text("Gallery") },
    headerContent = { /* ... */ },
    content = ParallaxContent.Custom { collapsed ->
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp)
        ) {
            items(photos) { PhotoCell(it) }
        }
    }
)
```

Dragging on the header itself collapses it too. Set `headerConfig(snapOnRelease = true)` to settle
a half-collapsed header to the nearer resting position when a drag or fling ends.

`headerConfig(scrollMode = ...)` picks how the header and toolbar react to the body:

| `ScrollMode` | Scrolling up | Scrolling down |
|---|---|---|
| `ExitUntilCollapsed` (default) | collapses the header, toolbar stays | expands only once the body is at its top |
| `EnterAlways` | collapses the header, toolbar stays | expands immediately, wherever the body is |
| `EnterAlwaysCollapsed` | collapses the header, then the toolbar slides away | the toolbar returns immediately; the header expands at the top |

`state.toolbarExitFraction` reports the toolbar's exit in the last mode.

</details>

<details>
<summary><b>Programmatic control</b></summary>

```kotlin
val state = rememberParallaxToolbarState()
val scope = rememberCoroutineScope()

ComposeParallaxToolbarLayout(
    titleContent = { Text("Title") },
    headerContent = { /* ... */ },
    content = ParallaxContent.Regular { /* ... */ },
    state = state
)

// Anywhere with access to `state`:
Text("Collapsed ${(state.collapseFraction * 100).toInt()}%")
Button(onClick = { scope.launch { state.collapse() } }) { Text("Collapse") }
Button(onClick = { scope.launch { state.expand() } }) { Text("Expand") }
```

`state.scrollState` backs regular content and `state.lazyListState` backs lazy content, so
they can be passed to other scroll-aware components.

Every slot also runs in a `ParallaxToolbarScope`, so it can read `collapseFraction`, `isCollapsed`
and `state` without capturing anything. Read the fraction in a `graphicsLayer` or `drawBehind`
block for per-frame effects; that keeps scrolling off the recomposition path:

```kotlin
headerContent = {
    Image(
        painter = painterResource(Res.drawable.cover),
        contentDescription = null,
        modifier = Modifier.fillMaxSize().graphicsLayer { alpha = 1f - collapseFraction / 2 }
    )
}
```

</details>

<details>
<summary><b>Sample app</b></summary>

The sample is a Compose Multiplatform playground shared by Android and iOS. The configuration
sheet opens on launch and can be reopened any time with the **Configure** button at the bottom
right. Pick a screen at the top of the sheet: `playground` applies every setting below it live
(content type, header height mode, toolbar colors, elevation, subtitle behavior, navigation icon,
actions, padding); the other names show the fixed sample screens from the library.

- `sample` holds the shared playground UI and the fixed sample screens.
- `sample-android` is the Android host app.
- `sample-desktop` is the desktop host app.
- `sample-web` is the browser host (Kotlin/Wasm).
- `iosApp` is the Xcode host project; it builds the shared framework through Gradle.

Android:

```bash
./gradlew :sample-android:installDebug
```

Desktop:

```bash
./gradlew :sample-desktop:run
```

Web: build the bundle and serve it from any static server:

```bash
./gradlew :sample-web:wasmJsBrowserDistribution
python3 -m http.server 8080 --directory sample-web/build/dist/wasmJs/productionExecutable
```

Open `http://localhost:8080`; add `?screen=lazyPadding` to open a fixed sample.

iOS: open `iosApp/iosApp.xcodeproj` in Xcode and run, or from the terminal:

```bash
xcodebuild -project iosApp/iosApp.xcodeproj -scheme iosApp -sdk iphonesimulator -destination 'platform=iOS Simulator,name=iPhone 17' build
```

A fixed sample screen can be opened instead of the playground. On Android pass an intent extra,
on iOS set the `SAMPLE_SCREEN` environment variable in the run scheme:

```bash
adb shell am start -n am.highapps.parallaxtoolbar.sample/.MainActivity --es screen lazyPadding
```

Available names: `simple`, `lazy`, `lazyPadding`, `lazyReversed`, `lazyCentered`, `lazySpacing`,
`lazyScrollControl`, `scaffold`, `aspectRatio`, `percentage`, `square`, `compact`, `ultrawide`.

</details>

<details>
<summary><b>Compatibility</b></summary>

- **Kotlin**: 2.4.20
- **Compose Multiplatform**: 1.12.1 (the library depends only on Compose UI and Foundation; use it with Material 2, Material 3 or a custom design system)
- **Android**: API 24+ (Android 7.0+)
- **iOS**: 15.0+ on arm64 devices and Apple Silicon simulators (the Intel `iosX64` simulator target was dropped by Compose Multiplatform 1.11 and is no longer published)
- **Desktop**: JVM 17+ through Compose for Desktop
- **Web**: Kotlin/Wasm in the browser

</details>

<details>
<summary><b>API stability and versioning</b></summary>

- **Semantic versioning.** Breaking changes ship only in a major version. Minor versions add
  API, patch versions fix bugs.
- **Explicit API.** Everything not marked `public` is internal implementation and may change at
  any time. The public surface is dumped to `compose-parallax-toolbar-kmp/api/` and checked on
  every pull request; an unintended change fails CI.
- **Deprecation.** A deprecated API keeps working for at least one minor release and carries a
  `ReplaceWith`. It is removed in the next major version.
- **Supported versions.** Each release states the Kotlin and Compose Multiplatform versions it is
  built and tested against in the Compatibility section. Older Compose versions may work but are
  not tested.
- **Reporting.** Bugs and requests go through GitHub issues; security concerns through
  [SECURITY.md](SECURITY.md).
- **Releasing.** Bump the version in `compose-parallax-toolbar-kmp/build.gradle.kts`, add the
  matching `## x.y.z` section to `CHANGELOG.md`, and push a `vx.y.z` tag. CI verifies, publishes
  to Maven Central, then creates the GitHub release with that changelog section as its notes.

</details>

<details>
<summary><b>Best Practices</b></summary>

### Material 3 Integration

- Use Material 3 typography and color schemes
- Adapt your UI using the `isCollapsed` parameter
- Leverage Material 3 components like `TopAppBar`

### Performance Optimization

- Use `ParallaxContent.Lazy` for large lists to ensure optimal performance
- Avoid heavy computations in recomposing content
- Use `remember` and `derivedStateOf` for scroll-based calculations
- Optimize images for mobile rendering
- Keep header content lightweight to maintain smooth scrolling

### Multiplatform Considerations

- Use platform-agnostic libraries for image loading
- Handle differences in status bar behavior
- Test across screen sizes for responsive layouts
- Choose appropriate content type based on your data size

</details>

<details>
<summary><b>Troubleshooting</b></summary>

### Common Issues

#### Android

- Ensure you're using a compatible Material 3 theme
- Use proper insets handling to avoid system UI overlaps
- Make sure you're using the correct content type (`ParallaxContent.Regular` for regular scrollable content, `ParallaxContent.Lazy` for LazyColumn)

#### iOS

- "No such module" errors: check framework linkage and make sure the framework is properly embedded
- Memory issues: maintain strong references to view controllers
- Custom implementations must be added to the common multiplatform code (iOS part), not directly in iOS app code

</details>

<details>
<summary><b>Changelog</b></summary>

See [CHANGELOG.md](CHANGELOG.md).

</details>

## Contribution

Contributions are welcome! Check out the [Contributing Guidelines](CONTRIBUTING.md) for more information.

## Acknowledgments

This library was inspired by and based mainly on the excellent article [Collapsing toolbar with parallax effect and curve motion in Jetpack Compose](https://proandroiddev.com/collapsing-toolbar-with-parallax-effect-and-curve-motion-in-jetpack-compose-9ed1c3c0393f) by Morad Azzouzi.

## Author & Support

This project was created by [Hayk Arustamyan](https://github.com/haykarustamyan).

⭐ If you find this library helpful, consider giving it a star on GitHub!

If this project helps you reduce time to develop, you can give me a cup of coffee :)

[![Ko-Fi](https://ko-fi.com/img/githubbutton_sm.svg)](https://ko-fi.com/haykarustamyan)

## License

```
MIT License

Copyright (c) 2025 Hayk Arustamyan

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
```