# iOS Sample Implementation Guide

This guide provides detailed examples for implementing the Compose Parallax Toolbar in iOS apps.

## Quick Reference

Sample view controllers available in the library:
- `IosParallaxToolbarSampleKt.SimpleParallaxToolbarViewController()`
- `IosParallaxToolbarSampleKt.CustomParallaxToolbarViewController()`
- `IosParallaxToolbarSampleKt.MinimalParallaxToolbarViewController()`
- `IosParallaxToolbarSampleKt.InitiallyCollapsedToolbarViewController()`
- `IosParallaxToolbarSampleKt.AllSamplesViewController()`

## UIKit Implementation Examples

### Basic Integration

```swift
import UIKit
import compose_parallax_toolbar_kmp

class ParallaxViewController: UIViewController {
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        // Create the compose view controller
        let composeVC = IosParallaxToolbarSampleKt.CustomParallaxToolbarViewController()
        
        // Add it to your view hierarchy
        addChild(composeVC)
        view.addSubview(composeVC.view)
        composeVC.view.frame = view.bounds
        composeVC.view.autoresizingMask = [.flexibleWidth, .flexibleHeight]
        composeVC.didMove(toParent: self)
    }
}
```

### Sample Gallery App

```swift
import UIKit
import compose_parallax_toolbar_kmp

class SampleListViewController: UITableViewController {
    
    private enum Sample: String, CaseIterable {
        case simple = "Simple Toolbar"
        case custom = "Custom Toolbar"
        case minimal = "Minimal Toolbar"
        case initiallyCollapsed = "Initially Collapsed"
        case all = "All Samples"
    }
    
    override func viewDidLoad() {
        super.viewDidLoad()
        title = "Parallax Toolbar Samples"
        tableView.register(UITableViewCell.self, forCellReuseIdentifier: "Cell")
    }
    
    override func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return Sample.allCases.count
    }
    
    override func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCell(withIdentifier: "Cell", for: indexPath)
        let sample = Sample.allCases[indexPath.row]
        
        cell.textLabel?.text = sample.rawValue
        cell.accessoryType = .disclosureIndicator
        
        return cell
    }
    
    override func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        let sample = Sample.allCases[indexPath.row]
        let sampleVC = createViewController(for: sample)
        navigationController?.pushViewController(sampleVC, animated: true)
    }
    
    private func createViewController(for sample: Sample) -> UIViewController {
        let viewController: UIViewController
        
        switch sample {
        case .simple:
            viewController = IosParallaxToolbarSampleKt.SimpleParallaxToolbarViewController()
        case .custom:
            viewController = IosParallaxToolbarSampleKt.CustomParallaxToolbarViewController()
        case .minimal:
            viewController = IosParallaxToolbarSampleKt.MinimalParallaxToolbarViewController()
        case .initiallyCollapsed:
            viewController = IosParallaxToolbarSampleKt.InitiallyCollapsedToolbarViewController()
        case .all:
            viewController = IosParallaxToolbarSampleKt.AllSamplesViewController()
        }
        
        return viewController
    }
}
```

### App Entry Point with Navigation

```swift
import UIKit

class SceneDelegate: UIResponder, UIWindowSceneDelegate {
    var window: UIWindow?
    
    func scene(_ scene: UIScene, willConnectTo session: UISceneSession, options connectionOptions: UIScene.ConnectionOptions) {
        guard let windowScene = (scene as? UIWindowScene) else { return }
        
        let window = UIWindow(windowScene: windowScene)
        window.rootViewController = UINavigationController(rootViewController: SampleListViewController())
        self.window = window
        window.makeKeyAndVisible()
    }
}
```

## SwiftUI Implementation Examples

### Basic Integration

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

struct ContentView: View {
    var body: some View {
        ComposeParallaxToolbarView()
            .ignoresSafeArea(edges: .top) // Makes the toolbar use the full height
    }
}

@main
struct ParallaxApp: App {
    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }
}
```

### Navigation with Samples Gallery

```swift
import SwiftUI
import compose_parallax_toolbar_kmp

struct ParallaxToolbarNavigation: View {
    enum Sample: String, CaseIterable, Identifiable {
        case simple = "Simple Toolbar"
        case custom = "Custom Toolbar"
        case minimal = "Minimal Toolbar"
        case initiallyCollapsed = "Initially Collapsed"
        case all = "All Samples"
        
        var id: String { self.rawValue }
    }
    
    var body: some View {
        NavigationStack {
            List(Sample.allCases) { sample in
                NavigationLink(sample.rawValue, value: sample)
            }
            .navigationTitle("Parallax Toolbar Samples")
            .navigationDestination(for: Sample.self) { sample in
                sampleView(for: sample)
            }
        }
    }
    
    @ViewBuilder
    private func sampleView(for sample: Sample) -> some View {
        switch sample {
        case .simple:
            SimpleToolbarWrapper()
        case .custom:
            CustomToolbarWrapper()
        case .minimal:
            MinimalToolbarWrapper()
        case .initiallyCollapsed:
            InitiallyCollapsedToolbarWrapper()
        case .all:
            AllSamplesWrapper()
        }
    }
}

// Wrapper components for each sample
struct SimpleToolbarWrapper: UIViewControllerRepresentable {
    func makeUIViewController(context: Context) -> UIViewController {
        return IosParallaxToolbarSampleKt.SimpleParallaxToolbarViewController()
    }
    
    func updateUIViewController(_ uiViewController: UIViewController, context: Context) {}
}

struct CustomToolbarWrapper: UIViewControllerRepresentable {
    func makeUIViewController(context: Context) -> UIViewController {
        return IosParallaxToolbarSampleKt.CustomParallaxToolbarViewController()
    }
    
    func updateUIViewController(_ uiViewController: UIViewController, context: Context) {}
}

struct MinimalToolbarWrapper: UIViewControllerRepresentable {
    func makeUIViewController(context: Context) -> UIViewController {
        return IosParallaxToolbarSampleKt.MinimalParallaxToolbarViewController()
    }
    
    func updateUIViewController(_ uiViewController: UIViewController, context: Context) {}
}

struct InitiallyCollapsedToolbarWrapper: UIViewControllerRepresentable {
    func makeUIViewController(context: Context) -> UIViewController {
        return IosParallaxToolbarSampleKt.InitiallyCollapsedToolbarViewController()
    }
    
    func updateUIViewController(_ uiViewController: UIViewController, context: Context) {}
}

struct AllSamplesWrapper: UIViewControllerRepresentable {
    func makeUIViewController(context: Context) -> UIViewController {
        return IosParallaxToolbarSampleKt.AllSamplesViewController()
    }
    
    func updateUIViewController(_ uiViewController: UIViewController, context: Context) {}
}

@main
struct ParallaxNavigationApp: App {
    var body: some Scene {
        WindowGroup {
            ParallaxToolbarNavigation()
        }
    }
}
```

## Creating Custom Implementations

### Basic Custom Implementation

To create a custom implementation, add this to your iOS multiplatform code (src/iosMain/kotlin):

```kotlin
fun MyBasicToolbarViewController() = ComposeUIViewController {
    MaterialTheme {
        ComposeParallaxToolbarLayout(
            titleContent = { isCollapsed ->
                Text(
                    text = "My Custom App",
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
                                colors = listOf(Color(0xFF6200EE), Color(0xFF3700B3))
                            )
                        )
                        .fillMaxSize()
                )
            },
            content = ParallaxContent.Regular { isCollapsed ->
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    repeat(15) { index ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                        ) {
                            Text(
                                text = "Custom Item ${index + 1}",
                                modifier = Modifier.padding(16.dp)
                            )
                        }
                    }
                }
            }
        )
    }
}
```

### Advanced Custom Implementation with LazyColumn

```kotlin
fun MyAdvancedToolbarViewController() = ComposeUIViewController {
    MaterialTheme {
        ComposeParallaxToolbarLayout(
            titleContent = { isCollapsed ->
                Text(
                    text = "Advanced Example",
                    fontSize = if (isCollapsed) 18.sp else 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isCollapsed) 
                        MaterialTheme.colorScheme.onSurface 
                    else 
                        Color.White
                )
            },
            subtitleContent = { isCollapsed ->
                Text(
                    text = "With LazyColumn and styling",
                    fontSize = if (isCollapsed) 14.sp else 16.sp,
                    color = if (isCollapsed) 
                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f) 
                    else 
                        Color.White.copy(alpha = 0.9f)
                )
            },
            headerContent = {
                Box(
                    modifier = Modifier
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    Color(0xFF4CAF50), 
                                    Color(0xFF2E7D32)
                                )
                            )
                        )
                        .fillMaxSize()
                )
            },
            content = ParallaxContent.Lazy { isCollapsed ->
                items(100) { index ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(
                                text = "Advanced Item $index",
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text(
                                text = "This is a detailed description for item $index",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                            )
                        }
                    }
                }
            },
            navigationIcon = { isCollapsed ->
                IconButton(onClick = { /* Handle navigation */ }) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = if (isCollapsed) 
                            MaterialTheme.colorScheme.onSurface 
                        else 
                            Color.White
                    )
                }
            },
            actions = { isCollapsed ->
                IconButton(onClick = { /* Handle action */ }) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = "Share",
                        tint = if (isCollapsed) 
                            MaterialTheme.colorScheme.onSurface 
                        else 
                            Color.White
                    )
                }
            }
        )
    }
}
```

### Custom Configuration Example

```kotlin
fun MyConfiguredToolbarViewController() = ComposeUIViewController {
    MaterialTheme {
        val headerConfig = ParallaxToolbarDefaults.headerConfig(
            height = 400.dp,
            gradient = Brush.verticalGradient(
                colors = listOf(
                    Color.Transparent,
                    Color.Black.copy(alpha = 0.3f),
                    Color.Black.copy(alpha = 0.7f)
                )
            )
        )
        
        val titleConfig = ParallaxToolbarDefaults.titleConfig(
            paddingStart = 24.dp,
            paddingEnd = 24.dp,
            paddingTop = 50.dp,
            paddingBottom = 20.dp
        )
        
        ComposeParallaxToolbarLayout(
            titleContent = { isCollapsed ->
                Text(
                    text = "Configured Toolbar",
                    fontSize = if (isCollapsed) 18.sp else 32.sp,
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
                                colors = listOf(Color(0xFFFF5722), Color(0xFFD84315))
                            )
                        )
                        .fillMaxSize()
                )
            },
            content = ParallaxContent.Regular { isCollapsed ->
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    repeat(20) { index ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp)
                        ) {
                            Text(
                                text = "Configured Item ${index + 1}",
                                modifier = Modifier.padding(16.dp)
                            )
                        }
                    }
                }
            },
            headerConfig = headerConfig,
            titleConfig = titleConfig
        )
    }
}
```

## Using Custom Implementations in Swift

After adding your custom implementations to the Kotlin code, use them in Swift:

```swift
// UIKit
let myCustomVC = IosParallaxToolbarSampleKt.MyBasicToolbarViewController()
let myAdvancedVC = IosParallaxToolbarSampleKt.MyAdvancedToolbarViewController()
let myConfiguredVC = IosParallaxToolbarSampleKt.MyConfiguredToolbarViewController()

// SwiftUI
struct MyCustomWrapper: UIViewControllerRepresentable {
    func makeUIViewController(context: Context) -> UIViewController {
        return IosParallaxToolbarSampleKt.MyBasicToolbarViewController()
    }
    
    func updateUIViewController(_ uiViewController: UIViewController, context: Context) {}
}
```

## Performance Considerations

1. **Use LazyColumn for large lists**: For lists with many items, use `ParallaxContent.Lazy` for better performance
2. **Optimize header content**: Keep header content lightweight to maintain smooth scrolling
3. **Minimize state changes**: Use stable data structures and avoid unnecessary recompositions

## Common Patterns

### Content Type Selection
```kotlin
// Choose based on your data size
val content = if (itemCount > 50) {
    ParallaxContent.Lazy { isCollapsed ->
        items(itemCount) { index ->
            // Lazy item content
        }
    }
} else {
    ParallaxContent.Regular { isCollapsed ->
        Column {
            repeat(itemCount) { index ->
                // Regular item content
            }
        }
    }
}
```

### Dynamic Header
```kotlin
headerContent = {
    Box(modifier = Modifier.fillMaxSize()) {
        // Background image or gradient
        Box(
            modifier = Modifier
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primary,
                            MaterialTheme.colorScheme.secondary
                        )
                    )
                )
                .fillMaxSize()
        )
        
        // Optional overlay for better text visibility
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color.Black.copy(alpha = 0.3f)
                        )
                    )
                )
        )
    }
}
```

This guide provides comprehensive examples for integrating and customizing the Compose Parallax Toolbar in iOS applications. For more information, refer to the main [API documentation](../../../docs/API.md). 