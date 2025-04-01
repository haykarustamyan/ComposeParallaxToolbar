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

## Troubleshooting Tips

### Framework Integration Issues

If you encounter "No such module" errors:

1. Verify that the framework is properly added:
   ```
   Project Settings → General → Frameworks, Libraries, and Embedded Content
   ```

2. Check Framework Search Paths:
   ```
   Project Settings → Build Settings → Framework Search Paths
   ```
   
   Add the correct path, such as:
   ```
   $(SRCROOT)/../ComposeParallaxToolbar
   ```

3. Clean and rebuild:
   - Clean Project (⇧⌘K)
   - Build (⌘B)

### Memory Management

For memory management issues:
- Maintain strong references to Compose view controllers
- Use proper child view controller containment APIs 