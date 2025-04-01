# iOS Integration Guide

This guide explains how to integrate the Compose Parallax Toolbar library in your iOS application.

## Installation Options

### Published Artifact (Recommended)

The library is published to Maven Central. Here are the recommended ways to integrate the published artifact:

#### Option 1: Swift Package Manager

Swift Package Manager is the recommended way to integrate the published artifact:

1. In Xcode: File → Add Package Dependencies...
2. Enter the repository URL: `https://github.com/haykarustamyan/ComposeParallaxToolbar`
3. Select the desired version rules
4. Click "Add Package"

After installation, import the library in your Swift files:

```swift
import compose_parallax_toolbar_kmp
```

#### Option 2: CocoaPods

For CocoaPods integration with the published artifact:

1. Add to your Podfile:

```ruby
pod 'compose_parallax_toolbar_kmp', 'version'
```

2. Run:

```bash
pod install
```

3. Import in your Swift files:

```swift
import compose_parallax_toolbar_kmp
```

### Manual Setup

If you prefer not to use package managers or need a custom configuration:

#### Option 3: Direct XCFramework Integration

For direct integration:

1. Download the XCFramework from the [releases page](https://github.com/haykarustamyan/ComposeParallaxToolbar/releases)
2. Drag the XCFramework into your Xcode project
3. Ensure it's added to "Frameworks, Libraries, and Embedded Content" with "Embed & Sign" option
4. Import in your Swift files:

```swift
import compose_parallax_toolbar_kmp
```

#### Option 4: Build from Source

If you need to customize the framework or work with the latest unreleased code:

1. Clone the repository:
```bash
git clone https://github.com/haykarustamyan/ComposeParallaxToolbar.git
```

2. Build the framework:
```bash
./gradlew buildIosFramework
```

This generates frameworks in:
```
compose-parallax-toolbar-kmp/build/bin/iosArm64/releaseFramework/compose_parallax_toolbar_kmp.framework
compose-parallax-toolbar-kmp/build/bin/iosSimulatorArm64/releaseFramework/compose_parallax_toolbar_kmp.framework
compose-parallax-toolbar-kmp/build/bin/iosX64/releaseFramework/compose_parallax_toolbar_kmp.framework
```

> **Note:** The framework name uses underscores: `compose_parallax_toolbar_kmp.framework`

3. Create XCFramework (recommended for universal support):
```bash
xcodebuild -create-xcframework \
  -framework compose-parallax-toolbar-kmp/build/bin/iosArm64/releaseFramework/compose_parallax_toolbar_kmp.framework \
  -framework compose-parallax-toolbar-kmp/build/bin/iosSimulatorArm64/releaseFramework/compose_parallax_toolbar_kmp.framework \
  -output compose-parallax-toolbar-kmp.xcframework
```

4. Add to Xcode Project:
   - In Xcode, select your project
   - Go to General → Frameworks, Libraries, and Embedded Content
   - Click "+" → Add Other → Select the `.xcframework` file
   - Set to "Embed & Sign"
   - If needed, update Framework Search Paths in Build Settings

## Using the Library

### UIKit Integration

```swift
import UIKit
import compose_parallax_toolbar_kmp

class MyViewController: UIViewController {
    override func viewDidLoad() {
        super.viewDidLoad()
        
        // Create a Compose view controller with the toolbar
        let composeVC = IosParallaxToolbarSampleKt.CustomParallaxToolbarViewController()
        
        // Add it to your view hierarchy
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

struct ComposeParallaxToolbarView: UIViewControllerRepresentable {
    func makeUIViewController(context: Context) -> UIViewController {
        return IosParallaxToolbarSampleKt.CustomParallaxToolbarViewController()
    }
    
    func updateUIViewController(_ uiViewController: UIViewController, context: Context) {
        // Updates happen here if needed
    }
}

// Use in your SwiftUI app
@main
struct MyApp: App {
    var body: some Scene {
        WindowGroup {
            ComposeParallaxToolbarView()
        }
    }
}
```

## Available Sample Implementations

The library includes ready-to-use view controllers:

- `SimpleParallaxToolbarViewController()` - Basic example with default settings
- `CustomParallaxToolbarViewController()` - Customized implementation
- `MinimalParallaxToolbarViewController()` - Minimal configuration
- `InitiallyCollapsedToolbarViewController()` - Starts collapsed
- `AllSamplesViewController()` - Container with all samples

## Custom Implementations

You can create your own implementations:

```kotlin
fun MyCustomToolbarViewController() = ComposeUIViewController {
    MaterialTheme {
        ComposeParallaxToolbarLayout(
            titleContent = { isCollapsed ->
                Text(
                    text = "My Custom Title",
                    fontSize = if (isCollapsed) 18.sp else 24.sp,
                    fontWeight = FontWeight.Bold
                )
            },
            headerContent = {
                // Your header content
            },
            content = {
                // Your main content
            }
        )
    }
}
```

## Troubleshooting

If you encounter issues:

1. **"No such module" errors:**
   - Make sure the framework is properly embedded with "Embed & Sign"
   - Verify Framework Search Paths in Build Settings
   - Clean build folder (Cmd+Shift+K) and rebuild
   - For M1/M2 Macs, ensure you're using the arm64 simulator version

2. **Other common issues:**
   - Check you're using the correct import: `import compose_parallax_toolbar_kmp`
   - Make sure Kotlin functions are called with the `Kt` suffix
   - For UI issues, check that you've provided appropriate sizes

## Detailed Examples

For more detailed examples and sample app implementations, see [iOS-Samples.md](src/iosMain/kotlin/am/highapps/parallaxtoolbar/iOS-Samples.md). 