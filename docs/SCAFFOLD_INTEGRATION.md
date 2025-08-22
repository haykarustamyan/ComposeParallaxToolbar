# Scaffold Integration Guide

This guide shows how to properly use `ComposeParallaxToolbarLayout` within a `Scaffold` to ensure content doesn't draw behind bottom navigation bars or other UI elements.

## The Problem

Before the fix, when using `ComposeParallaxToolbarLayout` inside a `Scaffold` with a bottom bar, the content would draw behind the bottom navigation, making the last items unreachable:

```kotlin
// ❌ BROKEN: Content draws behind bottom bar
Scaffold(
    bottomBar = { /* Bottom navigation */ }
) { paddingValues ->
    ComposeParallaxToolbarLayout(
        // Missing contentPadding parameter
        titleContent = { /* ... */ },
        content = ParallaxContent.Regular { /* ... */ }
    )
}
```

## The Solution

The fix adds a `contentPadding` parameter to `ComposeParallaxToolbarLayout`. Simply pass the `paddingValues` from the Scaffold:

```kotlin
// ✅ FIXED: Content respects Scaffold padding
Scaffold(
    bottomBar = { /* Bottom navigation */ }
) { paddingValues ->
    ComposeParallaxToolbarLayout(
        contentPadding = paddingValues, // This is the key fix!
        titleContent = { /* ... */ },
        content = ParallaxContent.Regular { /* ... */ }
    )
}
```

## Complete Example

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
                    IconButton(onClick = {}) {
                        Icon(Icons.Default.Settings, "Settings")
                    }
                }
            }
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {}) {
                Icon(Icons.Default.Add, "Add")
            }
        }
    ) { paddingValues ->
        ComposeParallaxToolbarLayout(
            contentPadding = paddingValues, // Essential for proper spacing
            titleContent = { isCollapsed ->
                Text(
                    text = "My App",
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
                                colors = listOf(
                                    MaterialTheme.colorScheme.primary,
                                    MaterialTheme.colorScheme.tertiary
                                )
                            )
                        )
                        .fillMaxSize()
                )
            },
            content = ParallaxContent.Regular { isCollapsed ->
                // Your content here
                LazyColumn {
                    items(100) { index ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                        ) {
                            Text(
                                text = "Item ${index + 1}",
                                modifier = Modifier.padding(16.dp)
                            )
                        }
                    }
                }
            },
            navigationIcon = { isCollapsed ->
                IconButton(onClick = {}) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
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

## Works with Both Content Types

The fix works with both regular content and lazy content:

### Regular Content
```kotlin
ComposeParallaxToolbarLayout(
    contentPadding = paddingValues,
    content = ParallaxContent.Regular { isCollapsed ->
        Column {
            // Your regular content
        }
    }
)
```

### Lazy Content
```kotlin
ComposeParallaxToolbarLayout(
    contentPadding = paddingValues,
    content = ParallaxContent.Lazy(
        content = { isCollapsed ->
            items(100) { index ->
                // Your lazy items
            }
        }
    )
)
```

## Key Benefits

- ✅ Content no longer draws behind bottom navigation bars
- ✅ Proper spacing is maintained when used in Scaffold
- ✅ Works with both regular and lazy content
- ✅ Maintains backward compatibility (default padding is 0.dp)
- ✅ Respects all Scaffold padding constraints (top, bottom, start, end)

## Migration

If you're upgrading from a previous version:

1. Add the `contentPadding = paddingValues` parameter to your `ComposeParallaxToolbarLayout` calls within Scaffold
2. No other changes are required - the fix is backward compatible

## Preview Examples

Check out the preview examples in `ScaffoldIntegrationPreviews.kt` to see the difference between the fixed and broken implementations side by side.
