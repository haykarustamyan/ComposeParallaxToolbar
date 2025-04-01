# ComposeParallaxToolbar API Documentation

This document provides detailed API documentation for the ComposeParallaxToolbar Compose Multiplatform library. It covers the main composable, configuration classes, and utilities.

## Table of Contents

- [Main Composable](#main-composable)
- [Configuration Classes](#configuration-classes)
  - [ParallaxHeaderConfig](#parallaxheaderconfig)
  - [ParallaxToolbarConfig](#parallaxtoolbarconfig)
  - [ParallaxTitleConfig](#parallaxtitleconfig)
  - [ParallaxBodyConfig](#parallaxbodyconfig)
- [iOS Integration](#ios-integration)
- [Default Values](#default-values)
- [Internal Components](#internal-components)

## Main Composable

### ComposeParallaxToolbarLayout

The main composable function that implements a parallax toolbar with collapsing header.

```kotlin
@Composable
fun ComposeParallaxToolbarLayout(
    titleContent: @Composable (Boolean) -> Unit,
    headerContent: @Composable () -> Unit,
    content: @Composable (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    subtitleContent: (@Composable (Boolean) -> Unit)? = null,
    navigationIcon: (@Composable (Boolean) -> Unit)? = null,
    actions: (@Composable RowScope.(Boolean) -> Unit)? = null,
    headerConfig: ParallaxHeaderConfig = ParallaxToolbarDefaults.headerConfig(),
    toolbarConfig: ParallaxToolbarConfig = ParallaxToolbarDefaults.toolbarConfig(),
    titleConfig: ParallaxTitleConfig = ParallaxToolbarDefaults.titleConfig(),
    bodyConfig: ParallaxBodyConfig = ParallaxToolbarDefaults.bodyConfig(),
    scroll: ScrollState = rememberScrollState()
){
}
```

#### Parameters

| Parameter | Type | Description | Default Value |
|-----------|------|-------------|---------------|
| `titleContent` | `@Composable (Boolean) -> Unit` | Composable lambda for the title that is provided with a boolean parameter indicating whether the toolbar is collapsed | (Required) |
| `headerContent` | `@Composable () -> Unit` | Composable lambda for the header area (typically an image) | (Required) |
| `content` | `@Composable (Boolean) -> Unit` | Composable lambda for the main content area that is provided with a boolean parameter indicating whether the toolbar is collapsed | (Required) |
| `modifier` | `Modifier` | Modifier to be applied to the layout | `Modifier` |
| `subtitleContent` | `(@Composable (Boolean) -> Unit)?` | Optional composable lambda for the subtitle that is provided with a boolean parameter indicating whether the toolbar is collapsed | `null` |
| `navigationIcon` | `(@Composable (Boolean) -> Unit)?` | Optional composable lambda for the navigation icon that is provided with a boolean parameter indicating whether the toolbar is collapsed | `null` |
| `actions` | `(@Composable RowScope.(Boolean) -> Unit)?` | Optional composable lambda for toolbar actions that is provided with a boolean parameter indicating whether the toolbar is collapsed | `null` |
| `headerConfig` | `ParallaxHeaderConfig` | Configuration for the header area behavior | `ParallaxToolbarDefaults.headerConfig()` |
| `toolbarConfig` | `ParallaxToolbarConfig` | Configuration for the toolbar appearance and behavior | `ParallaxToolbarDefaults.toolbarConfig()` |
| `titleConfig` | `ParallaxTitleConfig` | Configuration for the title and subtitle animations and behavior | `ParallaxToolbarDefaults.titleConfig()` |
| `bodyConfig` | `ParallaxBodyConfig` | Configuration for the content body layout | `ParallaxToolbarDefaults.bodyConfig()` |
| `scroll` | `ScrollState` | ScrollState to control the scrolling behavior (can be externally controlled) | `rememberScrollState()` |

#### Behavior

- The `isCollapsed` boolean parameter passed to several content lambdas indicates whether the toolbar is in collapsed state
- The parallax effect automatically adjusts as the user scrolls
- Title animation occurs along a curved path during collapse/expansion
- The toolbar background color animates between `initialColor` and `targetColor` based on scroll position

## Configuration Classes

### ParallaxHeaderConfig

Configuration for the header area behavior.

```kotlin
data class ParallaxHeaderConfig(
    val height: Dp,
    val gradient: Brush?,
    val isExpandedWhenFirstDisplayed: Boolean = true
)
```

#### Properties

| Property | Type | Description | Default Value |
|----------|------|-------------|---------------|
| `height` | `Dp` | The height of the header area | 450.dp |
| `gradient` | `Brush?` | Optional gradient brush to overlay on the header for better title visibility | `null` |
| `isExpandedWhenFirstDisplayed` | `Boolean` | Whether the header should start in expanded state | `true` |

### ParallaxToolbarConfig

Configuration for the toolbar appearance and behavior.

```kotlin
data class ParallaxToolbarConfig(
    val initialColor: Color,
    val targetColor: Color,
    val elevation: Dp,
    val iconSize: Dp,
    val iconSpacing: Dp,
    val animationSpec: AnimationSpec<Color>
)
```

#### Properties

| Property | Type | Description | Default Value |
|----------|------|-------------|---------------|
| `initialColor` | `Color` | The toolbar background color when expanded | `Color.Transparent` |
| `targetColor` | `Color` | The toolbar background color when collapsed | `Color.Black` |
| `elevation` | `Dp` | The elevation of the toolbar when visible | `0.dp` |
| `iconSize` | `Dp` | The size of toolbar icons | `24.dp` |
| `iconSpacing` | `Dp` | The spacing between toolbar actions | `8.dp` |
| `animationSpec` | `AnimationSpec<Color>` | Animation specification for background color transition | `tween(300)` |

### ParallaxTitleConfig

Configuration for the title and subtitle animations and behavior.

```kotlin
data class ParallaxTitleConfig(
    val paddingBottom: Dp,
    val paddingStart: Dp,
    val collapsedPaddingStart: Dp,
    val keepSubtitleAfterCollapse: Boolean,
    val animateSubTitleHiding: Boolean
)
```

#### Properties

| Property | Type | Description | Default Value |
|----------|------|-------------|---------------|
| `paddingBottom` | `Dp` | Padding from bottom of header to title when expanded | `-16.dp` |
| `paddingStart` | `Dp` | Horizontal padding for title when expanded | `16.dp` |
| `collapsedPaddingStart` | `Dp` | Horizontal padding for title when collapsed | `64.dp` |
| `keepSubtitleAfterCollapse` | `Boolean` | Whether to keep subtitle visible after collapse | `false` |
| `animateSubTitleHiding` | `Boolean` | Whether to animate subtitle hiding during collapse | `true` |

### ParallaxBodyConfig

Configuration for the content body layout.

```kotlin
data class ParallaxBodyConfig(
    val minBottomSpacerHeight: Dp
)
```

#### Properties

| Property | Type | Description | Default Value |
|----------|------|-------------|---------------|
| `minBottomSpacerHeight` | `Dp` | Minimum spacing after content | `0.dp` |

## iOS Integration

The library provides several ready-to-use UIViewController implementations for iOS integration.

### View Controller Factories

```kotlin
// In the IosParallaxToolbarSample.kt file

fun SimpleParallaxToolbarViewController(): UIViewController
fun CustomParallaxToolbarViewController(): UIViewController
fun MinimalParallaxToolbarViewController(): UIViewController
fun InitiallyCollapsedToolbarViewController(): UIViewController
fun AllSamplesViewController(): UIViewController
```

These factory functions return UIViewControllers that can be directly used in iOS applications.

### Usage from Swift

#### UIKit Integration

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

#### SwiftUI Integration

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

// Use in SwiftUI
struct ContentView: View {
    var body: some View {
        ComposeParallaxToolbarView()
    }
}
```

### Creating Custom View Controllers

You can create your own UIViewController implementations by defining Kotlin factory functions:

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

Then use them from Swift just like the built-in view controllers:

```swift
let myVC = MyModuleKt.MyCustomToolbarViewController()
```

## Default Values

### ParallaxToolbarDefaults

Object containing default values and factory methods for the configuration classes.

```kotlin
object ParallaxToolbarDefaults {
    // Header defaults
    val HeaderHeight: Dp = 450.dp
    internal const val HeaderParallaxMultiplier: Float = 0.5f
    internal const val HeaderAlphaHeightFraction: Float = 1f

    // Toolbar defaults
    internal val ToolbarHeight: Dp = 64.dp
    val ToolbarMinWidth: Dp = 56.dp
    val ToolbarIconSize: Dp = 24.dp
    val ToolbarIconSpacing: Dp = 8.dp

    // Title and subtitle defaults
    val TitlePaddingBottom: Dp = (-16).dp
    val TitlePaddingStart: Dp = 16.dp
    val TitleCollapsedPaddingStart: Dp = 64.dp
    internal const val TitleFontScaleStart: Float = 1f
    internal const val TitleFontScaleEnd: Float = 1f

    // Body defaults
    val BodyMinBottomSpacing: Dp = 0.dp

    @Composable
    fun headerConfig(...): ParallaxHeaderConfig

    @Composable
    fun toolbarConfig(...): ParallaxToolbarConfig

    @Composable
    fun titleConfig(...): ParallaxTitleConfig

    @Composable
    fun bodyConfig(...): ParallaxBodyConfig
}
```

### Factory Methods

#### headerConfig

```kotlin
@Composable
fun headerConfig(
    height: Dp = HeaderHeight,
    gradient: Brush? = null,
    isExpandedWhenFirstDisplayed: Boolean = true
): ParallaxHeaderConfig
```

Creates a configuration for the header area with default or custom values.

#### toolbarConfig

```kotlin
@Composable
fun toolbarConfig(
    initialColor: Color = Color.Transparent,
    targetColor: Color = Color.Black,
    elevation: Dp = 0.dp,
    iconSize: Dp = ToolbarIconSize,
    iconSpacing: Dp = ToolbarIconSpacing,
    animationSpec: AnimationSpec<Color> = tween(durationMillis = 300)
): ParallaxToolbarConfig
```

Creates a configuration for the toolbar with default or custom values.

#### titleConfig

```kotlin
@Composable
fun titleConfig(
    paddingBottom: Dp = TitlePaddingBottom,
    paddingStart: Dp = TitlePaddingStart,
    collapsedPaddingStart: Dp = TitleCollapsedPaddingStart,
    keepSubtitleAfterCollapse: Boolean = false,
    animateSubTitleHiding: Boolean = true
): ParallaxTitleConfig
```

Creates a configuration for the title and subtitle with default or custom values.

#### bodyConfig

```kotlin
@Composable
fun bodyConfig(
    minBottomSpacerHeight: Dp = BodyMinBottomSpacing
): ParallaxBodyConfig
```

Creates a configuration for the content body with default or custom values.

## Internal Components

The library also includes several internal components that implement specific parts of the behavior:

### Header

Responsible for rendering the header content with parallax effect.

### Body

Manages the main content area and its scrolling behavior.

### Toolbar

Implements the toolbar with animated background color based on scroll position.

### TitleWithSubtitle

Handles the animation and positioning of title and subtitle during collapse/expansion.

## Examples

The library includes several sample usage examples to demonstrate different configurations and use cases:

### Fully Customized Example

The `SampleParallaxToolbarScreen` shows how to use all customization options:

```kotlin
@Composable
fun SampleParallaxToolbarScreen() {
    // Create custom configurations using the defaults object
    val headerConfig = ParallaxToolbarDefaults.headerConfig(
        height = 400.dp,
        gradient = Brush.verticalGradient(
            colors = listOf(
                Color.Transparent,
                Color(0x80000000),
                Color(0xCC000000)
            ),
            startY = 300f  // Start gradient lower in the header
        )
    )

    val titleConfig = ParallaxToolbarDefaults.titleConfig(
        paddingStart = 20.dp,
        collapsedPaddingStart = 60.dp,
        keepSubtitleAfterCollapse = true,
        animateSubTitleHiding = true
    )

    // Custom toolbar config
    val toolbarConfig = ParallaxToolbarDefaults.toolbarConfig(
        initialColor = Color.Transparent,
        targetColor = MaterialTheme.colorScheme.surface,
        elevation = 2.dp,
        animationSpec = tween(durationMillis = 400)
    )

    // Custom body config
    val bodyConfig = ParallaxToolbarDefaults.bodyConfig(
        minBottomSpacerHeight = 32.dp
    )

    // Use the ComposeParallaxToolbarLayout with custom configuration
    ComposeParallaxToolbarLayout(
        titleContent = { isCollapsed ->
            Text(
                text = "Mountain View",
                fontSize = if (isCollapsed) 18.sp else 28.sp,
                fontWeight = FontWeight.Bold,
                color = if (isCollapsed) MaterialTheme.colorScheme.onSurface else Color.White
            )
        },
        subtitleContent = { isCollapsed ->
            Text(
                text = "California, USA",
                fontSize = if (isCollapsed) 14.sp else 18.sp,
                color = if (isCollapsed) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f) else Color.White.copy(
                    alpha = 0.9f
                )
            )
        },
        headerContent = {
            // Header image 
            Image(
                // Replace with your actual image resource 
                imageVector = Icons.Default.ThumbUp, // Use your actual image painter here
                contentDescription = "Mountain view header",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        },
        // ...content and other parameters
        headerConfig = headerConfig,
        toolbarConfig = toolbarConfig,
        titleConfig = titleConfig,
        bodyConfig = bodyConfig
    )
}
```

### Simple Default Configuration

The `SimpleParallaxToolbarScreen` demonstrates usage with minimal customization:

```kotlin
@Composable
fun SimpleParallaxToolbarScreen() {
    // Using all defaults directly from the ParallaxToolbarDefaults object
    ComposeParallaxToolbarLayout(
        titleContent = { isCollapsed ->
            Text(
                text = "Simple Example",
                fontSize = if (isCollapsed) 18.sp else 24.sp,
                fontWeight = FontWeight.Bold,
                color = if (isCollapsed) MaterialTheme.colorScheme.onSurface else Color.White
            )
        },
        headerContent = {
            // Just a colored box for the header
            Box(
                modifier = Modifier.background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primary,
                            MaterialTheme.colorScheme.tertiary
                        )
                    )
                ).fillMaxSize()
            )
        },
        content = {
            // Simple content
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                repeat(10) { index ->
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Content item $index",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        },
        navigationIcon = { isCollapsed ->
            IconButton(onClick = {}) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back"
                )
            }
        }
        // All other parameters use default values from ParallaxToolbarDefaults
    )
}
```

### Minimal Configuration

The `MinimalParallaxToolbarScreen` shows the minimal required setup:

```kotlin
@Composable
fun MinimalParallaxToolbarScreen() {
    ComposeParallaxToolbarLayout(
        titleContent = { isCollapsed ->
            Text(
                text = "Minimal Example",
                fontSize = if (isCollapsed) 18.sp else 24.sp,
                fontWeight = FontWeight.Bold,
                color = if (isCollapsed) MaterialTheme.colorScheme.onSurface else Color.White
            )
        },
        headerContent = {
            Box(
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.primary)
                    .fillMaxSize()
            )
        },
        content = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                // Your content here
            }
        }
        // Both navigationIcon and actions are omitted (null by default)
    )
}
```

### Initially Collapsed Toolbar

The `InitiallyCollapsedToolbarScreen` demonstrates how to start with a collapsed toolbar:

```kotlin
@Composable
fun InitiallyCollapsedToolbarScreen() {
    // Create a header config that starts in collapsed state
    val collapsedHeaderConfig = ParallaxToolbarDefaults.headerConfig(
        height = 350.dp,
        gradient = Brush.verticalGradient(
            colors = listOf(
                Color.Transparent,
                Color(0x80000000),
                Color(0xCC000000)
            )
        ),
        isExpandedWhenFirstDisplayed = false // Start in collapsed state
    )
    
    ComposeParallaxToolbarLayout(
        titleContent = { isCollapsed ->
            Text(
                text = "Initially Collapsed",
                fontSize = if (isCollapsed) 18.sp else 24.sp,
                fontWeight = FontWeight.Bold,
                color = if (isCollapsed) MaterialTheme.colorScheme.onSurface else Color.White
            )
        },
        // ...other parameters
        // Apply the collapsed header config
        headerConfig = collapsedHeaderConfig
    )
}
```

For more complete examples, see the full sample implementations in [SampleUsage.kt](https://github.com/haykarustamyan/ComposeParallaxToolbar/blob/main/compose-parallax-toolbar-kmp/src/commonMain/kotlin/am/highapps/parallaxtoolbar/SampleUsage.kt). 