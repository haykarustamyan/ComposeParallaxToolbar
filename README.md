# ComposeParallaxToolbar - Compose Multiplatform

[![Maven Central](https://img.shields.io/maven-central/v/am.highapps.parallaxtoolbar/compose-parallax-toolbar-kmp)](https://search.maven.org/artifact/am.highapps.parallaxtoolbar/compose-parallax-toolbar-kmp)
[![Kotlin](https://img.shields.io/badge/kotlin-v2.1.20-blue.svg?logo=kotlin)](http://kotlinlang.org)
[![Compose Multiplatform](https://img.shields.io/badge/Compose%20Multiplatform-Compatible-blue)](https://github.com/JetBrains/compose-multiplatform)
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
                implementation("am.highapps.parallaxtoolbar:compose-parallax-toolbar-kmp:1.2.0")
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
    implementation("am.highapps.parallaxtoolbar:compose-parallax-toolbar-kmp:1.2.0")
}
```

#### Gradle (Groovy)

```groovy
dependencies {
    implementation 'am.highapps.parallaxtoolbar:compose-parallax-toolbar-kmp:1.2.0'
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
                style = if (isCollapsed)
                    MaterialTheme.typography.titleMedium
                else
                    MaterialTheme.typography.headlineMedium
            )
        },
        headerContent = {
            // Your header image or content
            Image(
                painter = painterResource(id = R.drawable.header),
                contentDescription = null,
                contentScale = ContentScale.Crop,
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
        
        let composeVC = IosParallaxToolbarSampleKt.SimpleParallaxToolbarViewController()
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
        return IosParallaxToolbarSampleKt.SimpleParallaxToolbarViewController()
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

iOS Sample View Controllers:
- `SimpleParallaxToolbarViewController()` - Basic implementation with regular content
- `LazyParallaxToolbarViewController()` - LazyColumn content example
- `LazyWithPaddingViewController()` - Custom padding configuration
- `LazyWithSpacingViewController()` - Custom spacing and arrangement
- `LazyWithScrollControlViewController()` - Custom scroll behavior
- `IOSSettingsStyleViewController()` - iOS Settings app style with grouped sections
- `IOSPhotoGalleryViewController()` - iOS Photos app style with grid layout
- `AllSamplesViewController()` - Container with all samples

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
let customVC = IosParallaxToolbarSampleKt.MyCustomToolbarViewController()
```

> **Note:** Custom implementations cannot be created directly in the iOS application code. They must
> be added to the common multiplatform code (iOS part) and then accessed from the iOS app.

For more details:
- **[iOS Integration Guide](compose-parallax-toolbar-kmp/iOS-README.md)** - Setup and basic usage
- **[iOS Sample Implementation Guide](compose-parallax-toolbar-kmp/src/iosMain/kotlin/am/highapps/parallaxtoolbar/iOS-Samples.md)** - Detailed examples
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
<summary><b>Compatibility</b></summary>

- **Kotlin**: 2.1.21
- **Compose Multiplatform**: 1.8.2
- **Android**: API 24+ (Android 7.0+)
- **iOS**: iOS 14+

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