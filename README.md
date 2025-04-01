# ComposeParallaxToolbar - Compose Multiplatform

[![Maven Central](https://img.shields.io/maven-central/v/am.highapps.parallaxtoolbar/compose-parallax-toolbar-kmp)](https://search.maven.org/artifact/am.highapps.parallaxtoolbar/compose-parallax-toolbar-kmp)
[![Kotlin](https://img.shields.io/badge/kotlin-v1.9.0-blue.svg?logo=kotlin)](http://kotlinlang.org)
[![Compose Multiplatform](https://img.shields.io/badge/Compose%20Multiplatform-Compatible-blue)](https://github.com/JetBrains/compose-multiplatform)
[![Platform](https://img.shields.io/badge/platform-Android%20|%20iOS-green.svg)](https://github.com/haykarustamyan/ComposeParallaxToolbar)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)

A fully customizable Material 3 parallax toolbar layout built with **Compose Multiplatform**. This cross-platform library provides a modern, material design parallax effect for app bars that animate smoothly as users scroll through content, working seamlessly on Android, iOS.

![Parallax Toolbar Animation](https://github.com/haykarustamyan/ComposeParallaxToolbar/raw/main/raw/main/images/parallax_gif.gif)

## Features

- **Material 3 Integration**: Built with Material 3 components, theming, and color system
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
                implementation("am.highapps.parallaxtoolbar:compose-parallax-toolbar-kmp:version")
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
    implementation("am.highapps.parallaxtoolbar:compose-parallax-toolbar-kmp:version")
}
```

#### Gradle (Groovy)

```groovy
dependencies {
    implementation 'am.highapps.parallaxtoolbar:compose-parallax-toolbar-kmp:version'
}
```
</details>

<details>
<summary><b>iOS Installation Options</b></summary>

There are several ways to integrate the library in your iOS project:

#### Option 1: Swift Package Manager (Recommended)

Integrate the published Maven Central artifact via SPM:

1. In Xcode: File → Add Package Dependencies...
2. Enter the repository URL: `https://github.com/haykarustamyan/ComposeParallaxToolbar`
3. Select the version rules you want to follow
4. Click "Add Package"

#### Option 2: CocoaPods

Use the published Maven Central artifact via CocoaPods:

```ruby
pod 'compose_parallax_toolbar_kmp', 'version'
```

#### Option 3: Manual Integration

For direct integration without package managers:

1. Download the XCFramework from the [releases page](https://github.com/haykarustamyan/ComposeParallaxToolbar/releases)
2. Drag the XCFramework into your Xcode project
3. Ensure it's added to "Frameworks, Libraries, and Embedded Content" with "Embed & Sign" option

#### Option 4: Build from Source

For custom modifications or using the latest code, follow the instructions in the [iOS Integration Guide](compose-parallax-toolbar-kmp/iOS-README.md) to build the framework from source.
</details>

## Basic Usage

Here's a simple example of how to implement the parallax toolbar in your Compose code:

```kotlin
import am.highapps.parallaxtoolbar.ComposeParallaxToolbarLayout

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
        content = { _ ->
            // Your main content
            Column(modifier = Modifier.padding(16.dp)) {
                repeat(10) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                    ) {
                        Text(
                            text = "Item $it",
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
@Composable
fun AndroidScreen() {
    // Use Material Theme from your Android app
    MaterialTheme {
        ComposeParallaxToolbarLayout(
            // Component parameters as shown in Basic Usage
            // ...
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
        
        let composeVC = IosParallaxToolbarSampleKt.CustomParallaxToolbarViewController()
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
        return IosParallaxToolbarSampleKt.CustomParallaxToolbarViewController()
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
- `SimpleParallaxToolbarViewController()` - Basic implementation
- `CustomParallaxToolbarViewController()` - Implementation with custom styling
- `MinimalParallaxToolbarViewController()` - Minimalist implementation
- `InitiallyCollapsedToolbarViewController()` - Starts with collapsed toolbar

For more details:
- **[iOS Integration Guide](compose-parallax-toolbar-kmp/iOS-README.md)** - Setup and basic usage
- **[iOS Sample Implementation Guide](compose-parallax-toolbar-kmp/src/iosMain/kotlin/am/highapps/parallaxtoolbar/iOS-Samples.md)** - Detailed examples
</details>

<details open>
<summary><b>Advanced Customization</b></summary>

For more control, use the `ParallaxToolbarDefaults` object to customize various aspects:

```kotlin
// Create customized configurations using factory methods
val headerConfig = ParallaxToolbarDefaults.headerConfig(
    height = 400.dp,
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
    content = { /* ... */ },
    
    // Optional customizations
    headerConfig = headerConfig,
    toolbarConfig = toolbarConfig,
    titleConfig = titleConfig,
    bodyConfig = bodyConfig
)
```
</details>

## Documentation

For detailed information on all components, parameters, and configuration options, see the [API Documentation](docs/API.md).

<details>
<summary><b>Compatibility</b></summary>

- **Kotlin**: 1.9.0+
- **Compose Multiplatform**: 1.5.0+
- **Android**: API 21+ (Android 5.0+)
- **iOS**: iOS 14+

</details>

<details>
<summary><b>Best Practices</b></summary>

### Material 3 Integration

- Use Material 3 typography and color schemes
- Adapt your UI using the `isCollapsed` parameter
- Leverage Material 3 components like `TopAppBar`

### Performance Optimization

- Avoid heavy computations in recomposing content
- Use `remember` and `derivedStateOf` for scroll-based calculations
- Optimize images for mobile rendering

### Multiplatform Considerations

- Use platform-agnostic libraries for image loading
- Handle differences in status bar behavior
- Test across screen sizes for responsive layouts

</details>

<details>
<summary><b>Troubleshooting</b></summary>

### Common Issues

#### Android

- Ensure you're using a compatible Material 3 theme
- Use proper insets handling to avoid system UI overlaps

#### iOS

- "No such module" errors: check framework linkage
- Memory issues: maintain strong references to view controllers

</details>

<details>
<summary><b>Changelog</b></summary>

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