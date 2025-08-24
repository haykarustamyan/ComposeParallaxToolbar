# ComposeParallaxToolbar API Documentation

This document provides detailed API documentation for the ComposeParallaxToolbar Compose
Multiplatform library. It covers the main composable, configuration classes, and utilities.

## Table of Contents

- [Main Composable](#main-composable)
    - [ComposeParallaxToolbarLayout](#composeparallaxtoolbarlayout)
    - [ParallaxContent Types](#parallaxcontent-types)
    - [Parameters](#parameters)
    - [Quick Reference Examples](#quick-reference-examples)
- [Configuration Classes](#configuration-classes)
    - [ParallaxHeaderConfig](#parallaxheaderconfig)
    - [ParallaxToolbarConfig](#parallaxtoolbarconfig)
    - [ParallaxTitleConfig](#parallaxtitleconfig)
    - [ParallaxBodyConfig](#parallaxbodyconfig)
    - [LazyColumnConfig](#lazycolumnconfig)
- [Default Values](#default-values)
    - [ParallaxToolbarDefaults](#parallaxtoolbardefaults)
- [iOS Integration](#ios-integration)
- [Internal Components](#internal-components)

## Main Composable

### ComposeParallaxToolbarLayout

The main composable function that implements a parallax toolbar with collapsing header.

```kotlin
@Composable
fun ComposeParallaxToolbarLayout(
    titleContent: @Composable (Boolean) -> Unit,
    headerContent: @Composable () -> Unit,
    content: ParallaxContent,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp),
    subtitleContent: (@Composable (Boolean) -> Unit)? = null,
    navigationIcon: (@Composable (Boolean) -> Unit)? = null,
    actions: (@Composable RowScope.(Boolean) -> Unit)? = null,
    headerConfig: ParallaxHeaderConfig = ParallaxToolbarDefaults.headerConfig(),
    toolbarConfig: ParallaxToolbarConfig = ParallaxToolbarDefaults.toolbarConfig(),
    titleConfig: ParallaxTitleConfig = ParallaxToolbarDefaults.titleConfig(),
    bodyConfig: ParallaxBodyConfig = ParallaxToolbarDefaults.bodyConfig(),
    scrollState: ScrollState = rememberScrollState()
)
```

### ParallaxContent Types

The `content` parameter accepts a `ParallaxContent` sealed class that represents different types of
scrollable content:

#### Regular Content

For traditional scrollable content using Column with vertical scroll:

```kotlin
content = ParallaxContent.Regular { isCollapsed ->
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        repeat(10) { index ->
            Card(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Item ${index + 1}",
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
    }
}
```

#### LazyColumn Content

For high-performance lazy loading with large lists:

```kotlin
content = ParallaxContent.Lazy(
    content = { isCollapsed ->
        items(1000) { index ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text(
                    text = "Lazy Item ${index + 1}",
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
```

**Note**: For `ParallaxContent.Lazy`, the `lazyListState` parameter controls the LazyColumn's scroll
behavior and can be used for external scroll control. The `scrollState` parameter in the main
function only applies to `ParallaxContent.Regular` content.

### Simple Usage Examples

#### Basic Example with Regular Content

```kotlin
ComposeParallaxToolbarLayout(
    titleContent = { isCollapsed ->
        Text(
            text = "My App",
            fontSize = if (isCollapsed) 18.sp else 24.sp,
            fontWeight = FontWeight.Bold
        )
    },
    headerContent = {
        Box(
            modifier = Modifier
                .background(Color.Blue)
                .fillMaxSize()
        )
    },
    content = ParallaxContent.Regular { isCollapsed ->
        // Your regular content here
    }
)
```

#### LazyColumn Example

```kotlin
ComposeParallaxToolbarLayout(
    titleContent = { isCollapsed ->
        Text("Lazy Content")
    },
    headerContent = {
        // Your header content
    },
    content = ParallaxContent.Lazy(
        content = { isCollapsed ->
            items(100) { index ->
                // Your lazy items here
            }
        },
        config = ParallaxToolbarDefaults.lazyColumnConfig(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        )
    )
)
```

#### Parameters

| Parameter         | Type                                        | Description                                                                                                                              | Default Value                             |
|-------------------|---------------------------------------------|------------------------------------------------------------------------------------------------------------------------------------------|-------------------------------------------|
| `titleContent`    | `@Composable (Boolean) -> Unit`             | Composable lambda for the title that is provided with a boolean parameter indicating whether the toolbar is collapsed                    | (Required)                                |
| `headerContent`   | `@Composable () -> Unit`                    | Composable lambda for the header area (typically an image)                                                                               | (Required)                                |
| `content`         | `ParallaxContent`                           | The main content area - either ParallaxContent.Regular or ParallaxContent.Lazy                                                           | (Required)                                |
| `modifier`        | `Modifier`                                  | Modifier to be applied to the layout                                                                                                     | `Modifier`                                |
| `contentPadding`  | `PaddingValues`                             | Padding applied to the content area, typically passed from Scaffold to prevent drawing behind bottom bars                                | `PaddingValues(0.dp)`                     |
| `subtitleContent` | `(@Composable (Boolean) -> Unit)?`          | Optional composable lambda for the subtitle that is provided with a boolean parameter indicating whether the toolbar is collapsed        | `null`                                    |
| `navigationIcon`  | `(@Composable (Boolean) -> Unit)?`          | Optional composable lambda for the navigation icon that is provided with a boolean parameter indicating whether the toolbar is collapsed | `null`                                    |
| `actions`         | `(@Composable RowScope.(Boolean) -> Unit)?` | Optional composable lambda for toolbar actions that is provided with a boolean parameter indicating whether the toolbar is collapsed     | `null`                                    |
| `headerConfig`    | `ParallaxHeaderConfig`                      | Configuration for the header area behavior                                                                                               | `ParallaxToolbarDefaults.headerConfig()`  |
| `toolbarConfig`   | `ParallaxToolbarConfig`                     | Configuration for the toolbar appearance and behavior                                                                                    | `ParallaxToolbarDefaults.toolbarConfig()` |
| `titleConfig`     | `ParallaxTitleConfig`                       | Configuration for the title and subtitle animations and behavior                                                                         | `ParallaxToolbarDefaults.titleConfig()`   |
| `bodyConfig`      | `ParallaxBodyConfig`                        | Configuration for the content body layout                                                                                                | `ParallaxToolbarDefaults.bodyConfig()`    |
| `scroll`          | `ScrollState`                               | ScrollState to control the scrolling behavior (can be externally controlled)                                                             | `rememberScrollState()`                   |
| `lazyListState`   | `LazyListState`                             | LazyListState to control the LazyColumn scrolling behavior (can be externally controlled)                                                | `rememberLazyListState()`                 |

#### Behavior

- The `isCollapsed` boolean parameter passed to several content lambdas indicates whether the
  toolbar is in collapsed state
- The parallax effect automatically adjusts as the user scrolls
- Title animation occurs along a curved path during collapse/expansion
- The toolbar background color animates between `initialColor` and `targetColor` based on scroll
  position

## Scaffold Integration

When using `ComposeParallaxToolbarLayout` within a Scaffold (especially with bottom bars), you must
pass the Scaffold's `paddingValues` to the `contentPadding` parameter to prevent content from
drawing behind bottom navigation or other UI elements.

### Basic Scaffold Integration

```kotlin
@Composable
fun MyScreen() {
    Scaffold(
        bottomBar = {
            BottomAppBar {
                // Your bottom navigation items
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    IconButton(onClick = {}) {
                        Icon(Icons.Default.Home, "Home")
                    }
                    IconButton(onClick = {}) {
                        Icon(Icons.Default.Search, "Search")
                    }
                }
            }
        }
    ) { paddingValues ->
        ComposeParallaxToolbarLayout(
            contentPadding = paddingValues, // Essential for proper spacing
            titleContent = { isCollapsed ->
                Text(
                    text = "My App",
                    fontSize = if (isCollapsed) 18.sp else 24.sp,
                    fontWeight = FontWeight.Bold
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
                // Your content here - will respect bottom bar padding
                Column {
                    repeat(20) { index ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
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
}
```

### What happens without contentPadding?

```kotlin
// ❌ BROKEN: Content draws behind bottom bar
Scaffold(
    bottomBar = { /* Bottom navigation */ }
) { paddingValues ->
    ComposeParallaxToolbarLayout(
        // Missing contentPadding = paddingValues
        // Last items will be hidden behind bottom bar
    )
}
```

### What happens with contentPadding?

```kotlin
// ✅ FIXED: Content respects Scaffold padding
Scaffold(
    bottomBar = { /* Bottom navigation */ }
) { paddingValues ->
    ComposeParallaxToolbarLayout(
        contentPadding = paddingValues, // This fixes the issue
        // Last items are properly visible above bottom bar
    )
}
```

### Works with Both Content Types

The `contentPadding` parameter works with both regular and lazy content:

```kotlin
// Regular content
ComposeParallaxToolbarLayout(
    contentPadding = paddingValues,
    content = ParallaxContent.Regular { /* ... */ }
)

// Lazy content  
ComposeParallaxToolbarLayout(
    contentPadding = paddingValues,
    content = ParallaxContent.Lazy(
        content = { /* ... */ },
        config = ParallaxToolbarDefaults.lazyColumnConfig(
            // LazyColumn's own contentPadding is merged with external contentPadding
            contentPadding = PaddingValues(16.dp)
        )
    )
)
```

For more detailed information and examples, see
the [Scaffold Integration Guide](SCAFFOLD_INTEGRATION.md).

### Quick Reference Examples

#### Minimal Setup

```kotlin
ComposeParallaxToolbarLayout(
    titleContent = { isCollapsed -> Text("My App") },
    headerContent = { Box(Modifier.background(Color.Blue).fillMaxSize()) },
    content = ParallaxContent.Regular {
        Column { /* Your content */ }
    }
)
```

#### With Scaffold (Recommended)

```kotlin
Scaffold(bottomBar = { BottomAppBar { /* ... */ } }) { paddingValues ->
    ComposeParallaxToolbarLayout(
        contentPadding = paddingValues, // Essential!
        titleContent = { isCollapsed -> Text("My App") },
        headerContent = { /* ... */ },
        content = ParallaxContent.Regular { /* ... */ }
    )
}
```

#### Lazy Content with Custom Configuration

```kotlin
ComposeParallaxToolbarLayout(
    contentPadding = paddingValues,
    titleContent = { isCollapsed -> Text("My App") },
    headerContent = { /* ... */ },
    content = ParallaxContent.Lazy(
        content = { isCollapsed ->
            items(100) { index ->
                Text("Item $index")
            }
        },
        config = ParallaxToolbarDefaults.lazyColumnConfig(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        )
    )
)
```

## Configuration Classes

### HeaderHeight

Sealed class representing different ways to specify header height for responsive design.

```kotlin
sealed class HeaderHeight {
    /**
     * Fixed height in Dp
     */
    data class Fixed(val height: Dp) : HeaderHeight()

    /**
     * Height based on aspect ratio (width/height)
     * For example: 16f/9f for a 16:9 aspect ratio
     */
    data class AspectRatio(val ratio: Float) : HeaderHeight()

    /**
     * Height as percentage of screen height
     * Value should be between 0f and 1f (e.g., 0.4f for 40% of screen height)
     */
    data class Percentage(val percentage: Float) : HeaderHeight()
}
```

#### Types

| Type                        | Description                                                                    | Example Usage                                |
|-----------------------------|--------------------------------------------------------------------------------|----------------------------------------------|
| `HeaderHeight.Fixed`        | Traditional fixed height in Dp                                                | `HeaderHeight.Fixed(450.dp)`                |
| `HeaderHeight.AspectRatio`  | Height calculated from screen width and aspect ratio (width/height)           | `HeaderHeight.AspectRatio(16f/9f)`           |
| `HeaderHeight.Percentage`   | Height as percentage of screen height (0.0 to 1.0)                           | `HeaderHeight.Percentage(0.4f)` (40%)       |

#### Benefits

- **Responsive Design**: Adapts to different screen sizes automatically
- **Consistent Visual Hierarchy**: Maintains proportions across devices
- **No Overflow Issues**: Prevents headers from taking too much space on small screens
- **Future-Proof**: Easy to adjust for new device form factors

### ParallaxHeaderConfig

Configuration for the header area behavior.

```kotlin
data class ParallaxHeaderConfig(
    val height: HeaderHeight,
    val gradient: Brush?,
    val isExpandedWhenFirstDisplayed: Boolean = true
)
```

#### Properties

| Property                       | Type           | Description                                                                  | Default Value                              |
|--------------------------------|----------------|------------------------------------------------------------------------------|--------------------------------------------|
| `height`                       | `HeaderHeight` | The height specification for the header area (Fixed, AspectRatio, or Percentage) | `HeaderHeight.Fixed(450.dp)`               |
| `gradient`                     | `Brush?`       | Optional gradient brush to overlay on the header for better title visibility | `null`                                     |
| `isExpandedWhenFirstDisplayed` | `Boolean`      | Whether the header should start in expanded state                            | `true`                                     |

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

| Property        | Type                   | Description                                             | Default Value       |
|-----------------|------------------------|---------------------------------------------------------|---------------------|
| `initialColor`  | `Color`                | The toolbar background color when expanded              | `Color.Transparent` |
| `targetColor`   | `Color`                | The toolbar background color when collapsed             | `Color.Black`       |
| `elevation`     | `Dp`                   | The elevation of the toolbar when visible               | `0.dp`              |
| `iconSize`      | `Dp`                   | The size of toolbar icons                               | `24.dp`             |
| `iconSpacing`   | `Dp`                   | The spacing between toolbar actions                     | `8.dp`              |
| `animationSpec` | `AnimationSpec<Color>` | Animation specification for background color transition | `tween(300)`        |

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

| Property                    | Type      | Description                                          | Default Value |
|-----------------------------|-----------|------------------------------------------------------|---------------|
| `paddingBottom`             | `Dp`      | Padding from bottom of header to title when expanded | `-16.dp`      |
| `paddingStart`              | `Dp`      | Horizontal padding for title when expanded           | `16.dp`       |
| `collapsedPaddingStart`     | `Dp`      | Horizontal padding for title when collapsed          | `64.dp`       |
| `keepSubtitleAfterCollapse` | `Boolean` | Whether to keep subtitle visible after collapse      | `false`       |
| `animateSubTitleHiding`     | `Boolean` | Whether to animate subtitle hiding during collapse   | `true`        |

### ParallaxBodyConfig

Configuration for the content body layout.

```kotlin
data class ParallaxBodyConfig(
    val minBottomSpacerHeight: Dp
)
```

#### Properties

| Property                | Type | Description                   | Default Value |
|-------------------------|------|-------------------------------|---------------|
| `minBottomSpacerHeight` | `Dp` | Minimum spacing after content | `0.dp`        |

### LazyColumnConfig

Configuration for LazyColumn behavior when using `ParallaxContent.Lazy`.

```kotlin
data class LazyColumnConfig(
    val contentPadding: PaddingValues,
    val verticalArrangement: Arrangement.Vertical,
    val horizontalAlignment: Alignment.Horizontal,
    val flingBehavior: FlingBehavior?,
    val userScrollEnabled: Boolean,
    val overscrollEffect: OverscrollEffect?
)
```

#### Properties

| Property              | Type                   | Description                                                           | Default Value         |
|-----------------------|------------------------|-----------------------------------------------------------------------|-----------------------|
| `contentPadding`      | `PaddingValues`        | Padding around the entire LazyColumn content                          | `PaddingValues(0.dp)` |
| `verticalArrangement` | `Arrangement.Vertical` | Vertical arrangement strategy for items (e.g., spacing between items) | `Arrangement.Top`     |
| `horizontalAlignment` | `Alignment.Horizontal` | Horizontal alignment of items within the LazyColumn                   | `Alignment.Start`     |
| `flingBehavior`       | `FlingBehavior?`       | Custom fling behavior for scrolling animations, null uses default     | `null`                |
| `userScrollEnabled`   | `Boolean`              | Whether user can scroll the LazyColumn                                | `true`                |
| `overscrollEffect`    | `OverscrollEffect?`    | Custom overscroll effect, null uses default                           | `null`                |

#### Usage Examples

**Basic Configuration:**

```kotlin
val config = ParallaxToolbarDefaults.lazyColumnConfig(
    contentPadding = PaddingValues(16.dp),
    verticalArrangement = Arrangement.spacedBy(8.dp)
)

content = ParallaxContent.Lazy(
    content = { /* your items */ },
    config = config,
    lazyListState = rememberLazyListState()
)
```

**Advanced Configuration:**

```kotlin
val config = ParallaxToolbarDefaults.lazyColumnConfig(
    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
    verticalArrangement = Arrangement.spacedBy(12.dp),
    horizontalAlignment = Alignment.CenterHorizontally,
    userScrollEnabled = true
)
```

**External LazyListState Control:**

```kotlin
// Control LazyColumn scrolling from outside the component
val lazyListState = rememberLazyListState()

// Programmatically scroll to specific items
LaunchedEffect(someCondition) {
    lazyListState.animateScrollToItem(index = 10)
    // or
    lazyListState.scrollToItem(index = 5)
}

// Monitor scroll position
val isAtTop by remember {
    derivedStateOf {
        lazyListState.firstVisibleItemIndex == 0 &&
                lazyListState.firstVisibleItemScrollOffset == 0
    }
}

content = ParallaxContent.Lazy(
    content = { /* your items */ },
    config = config,
    lazyListState = lazyListState // Use your controlled state
)
```

**Custom Spacing Examples:**

```kotlin
// Evenly spaced items
verticalArrangement = Arrangement.spacedBy(16.dp)

// Space around items
verticalArrangement = Arrangement.SpaceAround

// Items at top with spacing
verticalArrangement = Arrangement.Top
```

## iOS Integration

The library provides several ready-to-use UIViewController implementations for iOS integration.

### View Controller Factories

```kotlin
// In the IosParallaxToolbarSample.kt file

// Basic Examples
fun SimpleParallaxToolbarViewController(): UIViewController
fun LazyParallaxToolbarViewController(): UIViewController
fun AllSamplesViewController(): UIViewController

// LazyColumn Configuration Examples
fun LazyWithPaddingViewController(): UIViewController
fun LazyWithSpacingViewController(): UIViewController
fun LazyWithScrollControlViewController(): UIViewController

// iOS-Style Examples
fun IOSSettingsStyleViewController(): UIViewController
fun IOSPhotoGalleryViewController(): UIViewController
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
        let composeVC = IosParallaxToolbarSampleKt.SimpleParallaxToolbarViewController()

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
        return IosParallaxToolbarSampleKt.SimpleParallaxToolbarViewController()
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

    @Composable
    fun lazyColumnConfig(...): LazyColumnConfig
}
```

### Factory Methods

#### headerConfig

```kotlin
@Composable
fun headerConfig(
    height: HeaderHeight = HeaderHeight.Fixed(HeaderHeightDp),
    gradient: Brush? = null,
    isExpandedWhenFirstDisplayed: Boolean = true
): ParallaxHeaderConfig
```

Creates a configuration for the header area with default or custom values. Uses `HeaderHeight.Fixed` by default for backward compatibility.

#### headerConfigWithAspectRatio

```kotlin
@Composable
fun headerConfigWithAspectRatio(
    aspectRatio: Float = 16f / 9f,
    gradient: Brush? = null,
    isExpandedWhenFirstDisplayed: Boolean = true
): ParallaxHeaderConfig
```

Creates a header configuration with aspect ratio-based height. The header height will be calculated as `screenWidth / aspectRatio`.

**Parameters:**
- `aspectRatio` - The width/height ratio (e.g., 16f/9f for widescreen, 4f/3f for standard, 1f for square)
- `gradient` - Optional gradient overlay
- `isExpandedWhenFirstDisplayed` - Whether to start expanded

**Common Aspect Ratios:**
- `16f/9f` - Widescreen (default)
- `4f/3f` - Standard/classic
- `21f/9f` - Ultrawide
- `1f/1f` - Square
- `3f/2f` - Photography standard

#### headerConfigWithPercentage

```kotlin
@Composable
fun headerConfigWithPercentage(
    heightPercentage: Float = 0.4f,
    gradient: Brush? = null,
    isExpandedWhenFirstDisplayed: Boolean = true
): ParallaxHeaderConfig
```

Creates a header configuration with percentage-based height. The header height will be calculated as `screenHeight * heightPercentage`.

**Parameters:**
- `heightPercentage` - Percentage of screen height (0.0 to 1.0, e.g., 0.4f for 40%)
- `gradient` - Optional gradient overlay
- `isExpandedWhenFirstDisplayed` - Whether to start expanded

**Recommended Percentages:**
- `0.25f` (25%) - Minimal header
- `0.4f` (40%) - Standard header (default)
- `0.5f` (50%) - Large header
- `0.6f` (60%) - Extra large header

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

#### lazyColumnConfig

```kotlin
@Composable
fun lazyColumnConfig(
    contentPadding: PaddingValues = PaddingValues(0.dp),
    verticalArrangement: Arrangement.Vertical = Arrangement.Top,
    horizontalAlignment: Alignment.Horizontal = Alignment.Start,
    flingBehavior: FlingBehavior? = null,
    userScrollEnabled: Boolean = true,
    overscrollEffect: OverscrollEffect? = null
): LazyColumnConfig
```

Creates a configuration for LazyColumn behavior with default or custom values. This method provides
defaults for `@Composable` properties like `flingBehavior` and `overscrollEffect`.

**Parameters:**

- `contentPadding` - Padding around the entire LazyColumn content
- `verticalArrangement` - Vertical arrangement strategy for items
- `horizontalAlignment` - Horizontal alignment of items within the LazyColumn
- `flingBehavior` - Custom fling behavior, null uses `ScrollableDefaults.flingBehavior()`
- `userScrollEnabled` - Whether user can scroll the LazyColumn
- `overscrollEffect` - Custom overscroll effect, null uses `rememberOverscrollEffect()`

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

The library includes several sample usage examples to demonstrate different configurations and use
cases:

### Fully Customized Example

The `SampleParallaxToolbarScreen` shows how to use all customization options:

```kotlin
@Composable
fun SampleParallaxToolbarScreen() {
    // Create custom configurations using the defaults object
    // Example with aspect ratio for responsive design
    val headerConfig = ParallaxToolbarDefaults.headerConfigWithAspectRatio(
        aspectRatio = 16f/9f,  // Widescreen ratio
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
        content = ParallaxContent.Regular {
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
        content = ParallaxContent.Regular {
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
    // Create a header config that starts in collapsed state using percentage
    val collapsedHeaderConfig = ParallaxToolbarDefaults.headerConfigWithPercentage(
        heightPercentage = 0.35f,  // 35% of screen height
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

### Responsive Header Height Examples

#### Fixed Height (Traditional)

```kotlin
@Composable
fun FixedHeaderExample() {
    val headerConfig = ParallaxToolbarDefaults.headerConfig(
        height = HeaderHeight.Fixed(400.dp)  // Traditional fixed height
    )
    
    ComposeParallaxToolbarLayout(
        titleContent = { isCollapsed ->
            Text("Fixed Height", fontSize = if (isCollapsed) 18.sp else 24.sp)
        },
        headerContent = { /* Your header */ },
        content = ParallaxContent.Regular { /* Your content */ },
        headerConfig = headerConfig
    )
}
```

#### Aspect Ratio Height (Responsive)

```kotlin
@Composable
fun AspectRatioHeaderExample() {
    // Widescreen header - great for hero images
    val widescreenConfig = ParallaxToolbarDefaults.headerConfigWithAspectRatio(
        aspectRatio = 16f/9f
    )
    
    // Square header - perfect for profile screens
    val squareConfig = ParallaxToolbarDefaults.headerConfigWithAspectRatio(
        aspectRatio = 1f/1f
    )
    
    // Ultrawide header - for cinematic content
    val ultrawideConfig = ParallaxToolbarDefaults.headerConfigWithAspectRatio(
        aspectRatio = 21f/9f
    )
    
    ComposeParallaxToolbarLayout(
        titleContent = { isCollapsed ->
            Text("Aspect Ratio Header", fontSize = if (isCollapsed) 18.sp else 24.sp)
        },
        headerContent = {
            Image(
                painter = painterResource(id = R.drawable.hero_image),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        },
        content = ParallaxContent.Regular { /* Your content */ },
        headerConfig = widescreenConfig  // Choose based on your content
    )
}
```

#### Percentage Height (Screen-Based)

```kotlin
@Composable
fun PercentageHeaderExample() {
    // Large header taking 50% of screen
    val largeHeaderConfig = ParallaxToolbarDefaults.headerConfigWithPercentage(
        heightPercentage = 0.5f  // 50% of screen height
    )
    
    // Compact header taking 30% of screen - great for mobile
    val compactHeaderConfig = ParallaxToolbarDefaults.headerConfigWithPercentage(
        heightPercentage = 0.3f  // 30% of screen height
    )
    
    ComposeParallaxToolbarLayout(
        titleContent = { isCollapsed ->
            Text("Percentage Header", fontSize = if (isCollapsed) 18.sp else 24.sp)
        },
        headerContent = { /* Your header */ },
        content = ParallaxContent.Regular { /* Your content */ },
        headerConfig = largeHeaderConfig
    )
}
```

#### Responsive Design Best Practices

```kotlin
@Composable
fun ResponsiveHeaderExample() {
    // Different configurations for different screen types
    val configuration = LocalConfiguration.current
    val screenWidthDp = configuration.screenWidthDp.dp
    
    val headerConfig = when {
        screenWidthDp < 600.dp -> {
            // Phone: Use percentage to avoid taking too much space
            ParallaxToolbarDefaults.headerConfigWithPercentage(0.35f)
        }
        screenWidthDp < 840.dp -> {
            // Tablet portrait: Use moderate aspect ratio
            ParallaxToolbarDefaults.headerConfigWithAspectRatio(4f/3f)
        }
        else -> {
            // Tablet landscape/Desktop: Use widescreen ratio
            ParallaxToolbarDefaults.headerConfigWithAspectRatio(16f/9f)
        }
    }
    
    ComposeParallaxToolbarLayout(
        titleContent = { isCollapsed ->
            Text("Responsive Design", fontSize = if (isCollapsed) 18.sp else 24.sp)
        },
        headerContent = { /* Your header */ },
        content = ParallaxContent.Regular { /* Your content */ },
        headerConfig = headerConfig
    )
}
```

### Migration from Fixed Height

If you're upgrading from a previous version that used fixed heights:

```kotlin
// Old way (still works)
val oldConfig = ParallaxToolbarDefaults.headerConfig(
    height = 400.dp  // This automatically becomes HeaderHeight.Fixed(400.dp)
)

// New way - more responsive options
val newConfigAspectRatio = ParallaxToolbarDefaults.headerConfigWithAspectRatio(
    aspectRatio = 16f/9f  // Adapts to screen width
)

val newConfigPercentage = ParallaxToolbarDefaults.headerConfigWithPercentage(
    heightPercentage = 0.4f  // 40% of screen height
)

// Direct usage with HeaderHeight sealed class
val advancedConfig = ParallaxToolbarDefaults.headerConfig(
    height = HeaderHeight.AspectRatio(21f/9f)  // Direct usage
)
```

For more complete examples, see the full sample implementations
in [SampleUsage.kt](https://github.com/haykarustamyan/ComposeParallaxToolbar/blob/main/compose-parallax-toolbar-kmp/src/commonMain/kotlin/am/highapps/parallaxtoolbar/SampleUsage.kt). 