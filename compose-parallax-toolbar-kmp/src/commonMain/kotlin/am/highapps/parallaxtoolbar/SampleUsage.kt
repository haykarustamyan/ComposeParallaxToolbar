package am.highapps.parallaxtoolbar

import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Sample usage of ComposeParallaxToolbarLayout with fully customized configuration
 */
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
        content = {
            // Main content
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                // Content cards
                repeat(5) { index ->
                    Spacer(modifier = Modifier.height(16.dp))
                    Card(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "Section ${index + 1}",
                                style = MaterialTheme.typography.titleMedium
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "This is a sample content for section ${index + 1}. " +
                                        "The ComposeParallaxToolbarLayout provides a customizable " +
                                        "parallax effect when scrolling through content.",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
            }
        },
        navigationIcon = { isCollapsed ->
            IconButton(onClick = {}) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
        },
        actions = { isCollapsed ->
            IconButton(onClick = {}) {
                Icon(
                    imageVector = Icons.Default.Share,
                    contentDescription = "Share",
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
        },
        headerConfig = headerConfig,
        toolbarConfig = toolbarConfig,
        titleConfig = titleConfig,
        bodyConfig = bodyConfig
    )
}

/**
 * Sample usage with default configuration
 */
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

/**
 * Sample usage with no toolbar icons
 */
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
                repeat(5) { index ->
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Content item $index",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        }
        // Both navigationIcon and actions are omitted (null by default)
    )
}

/**
 * Example demonstrating a toolbar that starts in collapsed state
 */
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
        headerContent = {
            // Colored box for the header
            Box(
                modifier = Modifier.background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primary,
                            MaterialTheme.colorScheme.secondary
                        )
                    )
                ).fillMaxSize()
            )
        },
        content = { isCollapsed ->
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = "This toolbar starts in the collapsed state and can be expanded by scrolling down.",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                
                repeat(5) { index ->
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
        },
        // Apply the collapsed header config
        headerConfig = collapsedHeaderConfig
    )
} 