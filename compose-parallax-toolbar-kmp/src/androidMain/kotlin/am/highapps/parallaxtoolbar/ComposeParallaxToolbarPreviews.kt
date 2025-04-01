package am.highapps.parallaxtoolbar

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * This file contains preview composables for the ComposeParallaxToolbarLayout.
 * Use these previews to quickly visualize different configurations of the component.
 */

@Preview(
    name = "Default Configuration",
    showBackground = true,
    device = "spec:width=411dp,height=891dp",
    showSystemUi = true
)
@Composable
fun DefaultParallaxToolbarPreview() {
    MaterialTheme {
        SimpleParallaxToolbarScreen()
    }
}

@Preview(
    name = "Custom Configuration",
    showBackground = true,
    device = "spec:width=411dp,height=891dp",
    showSystemUi = true
)
@Composable
fun CustomParallaxToolbarPreview() {
    MaterialTheme {
        SampleParallaxToolbarScreen()
    }
}

@Preview(
    name = "Minimal Configuration",
    showBackground = true,
    device = "spec:width=411dp,height=891dp",
    showSystemUi = true
)
@Composable
fun MinimalParallaxToolbarPreview() {
    MaterialTheme {
        // Using only required parameters - explicitly omitting optional ones
        ComposeParallaxToolbarLayout(
            titleContent = { _ ->
                Text(
                    text = "Minimal Example",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            },
            headerContent = {
                Box(
                    modifier = Modifier
                        .background(Color(0xFF1976D2))
                        .fillMaxSize()
                )
            },
            content = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    repeat(5) {
                        Spacer(modifier = Modifier.height(16.dp))
                        Card(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "Content item ${it + 1}",
                                modifier = Modifier.padding(16.dp),
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    }
                }
            }
            // navigationIcon = null (default)
            // actions = null (default)
        )
    }
}

@Preview(
    name = "With Subtitle",
    showBackground = true,
    device = "spec:width=411dp,height=891dp",
    showSystemUi = true
)
@Composable
fun WithSubtitleParallaxToolbarPreview() {
    MaterialTheme {
        // Create header config with custom height
        val headerConfig = ParallaxToolbarDefaults.headerConfig(
            height = 350.dp
        )

        // Create title config to keep subtitle after collapse
        val titleConfig = ParallaxToolbarDefaults.titleConfig(
            keepSubtitleAfterCollapse = true
        )

        ComposeParallaxToolbarLayout(
            titleContent = { isCollapsed ->
                Text(
                    text = "Location Title",
                    fontSize = if (isCollapsed) 18.sp else 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isCollapsed) MaterialTheme.colorScheme.onSurface else Color.White
                )
            },
            subtitleContent = { isCollapsed ->
                Text(
                    text = "Subtitle description",
                    fontSize = if (isCollapsed) 14.sp else 16.sp,
                    color = if (isCollapsed) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f) else Color.White.copy(
                        alpha = 0.9f
                    )
                )
            },
            headerContent = {
                Image(
                    imageVector = Icons.Default.ThumbUp,
                    contentDescription = "Header image",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            },
            content = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    repeat(3) {
                        Spacer(modifier = Modifier.height(16.dp))
                        Card(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "Content section ${it + 1}",
                                modifier = Modifier.padding(16.dp),
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    }
                }
            },
            navigationIcon = { isCollapsed ->
                IconButton(onClick = {}) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back"
                    )
                }
            },
            actions = { isCollapsed ->
                IconButton(onClick = {}) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = "Share"
                    )
                }
            }, // actions parameter is optional and can be omitted if no actions are needed
            headerConfig = headerConfig,
            titleConfig = titleConfig
        )
    }
}

@Preview(
    name = "Dark Theme",
    showBackground = true,
    device = "spec:width=411dp,height=891dp",
    backgroundColor = 0xFF121212,
    showSystemUi = true
)
@Composable
fun DarkThemeParallaxToolbarPreview() {
    MaterialTheme {
        // Create custom toolbar configuration for dark theme
        val toolbarConfig = ParallaxToolbarDefaults.toolbarConfig(
            initialColor = Color.Transparent,
            targetColor = Color(0xFF121212)
        )

        ComposeParallaxToolbarLayout(
            titleContent = { isCollapsed ->
                Text(
                    text = "Dark Theme Example",
                    fontSize = if (isCollapsed) 18.sp else 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            },
            headerContent = {
                Box(
                    modifier = Modifier
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    Color(0xFF121212),
                                    Color(0xFF6200EA)
                                )
                            )
                        )
                        .fillMaxSize()
                )
            },
            content = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    repeat(4) {
                        Spacer(modifier = Modifier.height(16.dp))
                        Card(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "Card content ${it + 1}",
                                modifier = Modifier.padding(16.dp),
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    }
                }
            },
            navigationIcon = { isCollapsed ->
                IconButton(onClick = {}) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White
                    )
                }
            },
            toolbarConfig = toolbarConfig
        )
    }
}

@Preview(
    name = "Padding Demo",
    showBackground = true,
    device = "spec:width=411dp,height=891dp",
    showSystemUi = true
)
@Composable
fun PaddingDemoPreview() {
    MaterialTheme {
        // Using custom configs for header and title
        val headerConfig = ParallaxToolbarDefaults.headerConfig(
            height = 400.dp,
            gradient = Brush.verticalGradient(
                colors = listOf(
                    Color(0xFF0D47A1),
                    Color(0xFF1976D2),
                ),
                startY = 300f
            )
        )

        val titleConfig = ParallaxToolbarDefaults.titleConfig(
            paddingStart = 60.dp,  // Very large padding to make it obvious
            collapsedPaddingStart = 100.dp,
            keepSubtitleAfterCollapse = true
        )

        ComposeParallaxToolbarLayout(
            titleContent = { isCollapsed ->
                Text(
                    text = "⬅️ Padding Test",  // Arrow emoji to show the padding
                    fontSize = if (isCollapsed) 18.sp else 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isCollapsed) MaterialTheme.colorScheme.onSurface else Color.White
                )
            },
            subtitleContent = { isCollapsed ->
                Text(
                    text = "⬅️ 60dp start padding",  // Arrow emoji to show the padding
                    fontSize = if (isCollapsed) 14.sp else 16.sp,
                    color = if (isCollapsed) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f) else Color.White.copy(
                        alpha = 0.9f
                    )
                )
            },
            headerContent = {
                // Add a vertical line at the expected padding position
                Box(
                    modifier = Modifier
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color(0xFF1976D2),
                                    Color(0xFF0D47A1)
                                )
                            )
                        )
                        .fillMaxSize()
                ) {
                    // Visual guides at padding positions
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .padding(start = 60.dp)
                            .width(2.dp)
                            .background(Color.White.copy(alpha = 0.5f))
                    )

                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .padding(start = 100.dp)
                            .width(2.dp)
                            .background(Color.Yellow.copy(alpha = 0.5f))
                    )
                }
            },
            content = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = "The title should align with the white vertical guide (60dp) at the start, and animate toward the yellow guide (100dp) when collapsed.",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    repeat(3) {
                        Spacer(modifier = Modifier.height(16.dp))
                        Card(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "Content section ${it + 1}",
                                modifier = Modifier.padding(16.dp),
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    }
                }
            },
            navigationIcon = { isCollapsed ->
                IconButton(onClick = {}) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back"
                    )
                }
            },
            headerConfig = headerConfig,
            titleConfig = titleConfig
        )
    }
}

@Preview(
    name = "No Actions Preview",
    showBackground = true,
    device = "spec:width=411dp,height=891dp",
    showSystemUi = true
)
@Composable
fun NoActionsPreview() {
    MaterialTheme {
        ComposeParallaxToolbarLayout(
            titleContent = { isCollapsed ->
                Text(
                    text = "No Actions Example",
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
            content = { _ ->
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    repeat(5) {
                        Spacer(modifier = Modifier.height(16.dp))
                        Card(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "This example shows a toolbar with navigation but no actions",
                                modifier = Modifier.padding(16.dp),
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    }
                }
            },
            navigationIcon = { _ ->
                IconButton(onClick = {}) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back"
                    )
                }
            }
            // actions explicitly omitted (null by default)
        )
    }
}

@Preview(
    name = "Initially Collapsed",
    showBackground = true,
    device = "spec:width=411dp,height=891dp",
    showSystemUi = true
)
@Composable
fun InitiallyCollapsedPreview() {
    MaterialTheme {
        // Create header config that starts in collapsed state
        val collapsedHeaderConfig = ParallaxToolbarDefaults.headerConfig(
            height = 350.dp,
            gradient = Brush.verticalGradient(
                colors = listOf(
                    Color(0xFF0D47A1),
                    Color(0xFF1976D2)
                )
            ),
            isExpandedWhenFirstDisplayed = false // Start collapsed instead of expanded
        )

        ComposeParallaxToolbarLayout(
            titleContent = { isCollapsed ->
                Text(
                    text = "Initially Collapsed Example",
                    fontSize = if (isCollapsed) 18.sp else 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isCollapsed) MaterialTheme.colorScheme.onSurface else Color.White
                )
            },
            headerContent = {
                Box(
                    modifier = Modifier
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    Color(0xFF1976D2),
                                    Color(0xFF0D47A1)
                                )
                            )
                        )
                        .fillMaxSize()
                )
            },
            content = { isCollapsed ->
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = "This toolbar starts in the collapsed state thanks to isExpandedWhenFirstDisplayed = false in the headerConfig",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    repeat(3) {
                        Spacer(modifier = Modifier.height(16.dp))
                        Card(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "Content section ${it + 1}",
                                modifier = Modifier.padding(16.dp),
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    }
                }
            },
            navigationIcon = { isCollapsed ->
                IconButton(onClick = {}) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back"
                    )
                }
            },
            // Use the custom header config that starts collapsed
            headerConfig = collapsedHeaderConfig
        )
    }
} 