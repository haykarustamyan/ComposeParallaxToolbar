# iOS Integration Guide

This guide explains how to integrate the Compose Parallax Toolbar library in your iOS application.

## Installation

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

To create custom implementations, you need to add your custom composable functions in the **common
code** (specifically in the iOS part of the multiplatform module), then use them from your iOS
application.

**Step 1:** Add your custom implementation in the common code (iOS part):

```kotlin
// Add this in src/iosMain/kotlin (common code - iOS part)
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