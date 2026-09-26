# ComposeParallaxToolbar - Compose Multiplatform

[![Maven Central](https://img.shields.io/maven-central/v/am.highapps.parallaxtoolbar/compose-parallax-toolbar-kmp)](https://search.maven.org/artifact/am.highapps.parallaxtoolbar/compose-parallax-toolbar-kmp)
[![Kotlin](https://img.shields.io/badge/kotlin-v2.4.20-blue.svg?logo=kotlin)](http://kotlinlang.org)
[![Compose Multiplatform](https://img.shields.io/badge/Compose%20Multiplatform-v1.12.1-blue)](https://github.com/JetBrains/compose-multiplatform)
[![Platform](https://img.shields.io/badge/platform-Android%20|%20iOS-green.svg)](https://github.com/haykarustamyan/ComposeParallaxToolbar)
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
- `iosApp` is the Xcode host project; it builds the shared framework through Gradle.

Android:

```bash
./gradlew :sample-android:installDebug
```

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

### Version 2.0.0

- **NEW**: `ParallaxToolbarState` and `rememberParallaxToolbarState()`: observe `collapseFraction` and `isCollapsed`, and call `collapse()` / `expand()` (animated or not). The `scrollState` parameter is replaced by `state`; the deprecated overload still accepts a `ScrollState`.
- **NEW**: `parallaxMultiplier` on the header config, `height` on the toolbar config, and `collapsedScale` on the title config.
- **REMOVED**: the Material 3 dependency. The library now depends only on Compose UI and Foundation, so it works with any design system
- **CHANGED**: the header fades out over the collapse range instead of its full height, so it is fully hidden once the toolbar covers it
- **CHANGED**: the toolbar and title are measured in one pass by a custom layout instead of position callbacks, removing the one-frame jump on first display and rotation. The toolbar no longer uses Material's `TopAppBar` internally; its look is unchanged.

- **UPDATED**: Kotlin 2.4.20, Compose Multiplatform 1.12.1, Material 3 1.9.0
- **UPGRADED**: Gradle 9.7.0, Android Gradle Plugin 9.3.3, Maven Publish Plugin 0.37.0
- **CHANGED**: Migrated to the `com.android.kotlin.multiplatform.library` plugin required by AGP 9
- **REMOVED**: `iosX64` target, since Compose Multiplatform no longer publishes artifacts for it
- **REMOVED**: unused `components-resources` and `components-ui-tooling-preview` dependencies
- **REMOVED**: sample screens, previews and iOS sample view controllers from the published artifact; they now live in the `sample` module. The library no longer depends on `material-icons-extended`.
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

### Version 1.3.0

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

### Version 1.2.0

- **NEW**: Introduced unified `ParallaxContent` sealed class system for content types:
  - `ParallaxContent.Regular` - Regular scrollable content using Column with vertical scroll
  - `ParallaxContent.Lazy` - LazyColumn content for better performance with large lists
- **API Enhancement**: New unified `ComposeParallaxToolbarLayout` with single `content: ParallaxContent` parameter
- **Backward Compatibility**: Legacy API maintained but marked as deprecated
- **Developer Experience**: Clearer API with explicit content type declarations

### Version 1.1.0

- Updated to Kotlin 2.1.20
- Updated to Compose Multiplatform 1.8.1
- Ios integration details update

### Version 1.0.0

- Initial release
- Basic parallax toolbar functionality
- Material 3 support

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