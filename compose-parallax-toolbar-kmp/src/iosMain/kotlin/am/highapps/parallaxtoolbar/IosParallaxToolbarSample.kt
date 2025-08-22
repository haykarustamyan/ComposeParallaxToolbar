package am.highapps.parallaxtoolbar

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.ComposeUIViewController

/**
 * Creates a UIViewController with the SimpleParallaxToolbarScreen content.
 * This function is called from Swift to display the parallax toolbar.
 */
fun SimpleParallaxToolbarViewController() = ComposeUIViewController {
    MaterialTheme {
        SimpleParallaxToolbarScreen()
    }
}

/**
 * Creates a UIViewController with the LazyParallaxToolbarScreen content.
 * This provides an example with lazy content for better performance with large lists.
 */
fun LazyParallaxToolbarViewController() = ComposeUIViewController {
    MaterialTheme {
        LazyParallaxToolbarScreen()
    }
}

/**
 * Creates a UIViewController for iOS with LazyColumn custom padding.
 * Shows usage of LazyColumnConfig with custom padding values.
 */
fun LazyWithPaddingViewController() = ComposeUIViewController {
    MaterialTheme {
        LazyParallaxToolbarWithPaddingScreen()
    }
}

/**
 * Creates a UIViewController for iOS with LazyColumn custom spacing.
 * Shows usage of LazyColumnConfig with custom vertical arrangement.
 */
fun LazyWithSpacingViewController() = ComposeUIViewController {
    MaterialTheme {
        LazyParallaxToolbarWithSpacingScreen()
    }
}

/**
 * Creates a UIViewController for iOS with LazyColumn custom scroll behavior.
 * Shows usage of LazyColumnConfig with custom fling and user scroll control.
 */
fun LazyWithScrollControlViewController() = ComposeUIViewController {
    MaterialTheme {
        LazyParallaxToolbarWithScrollControlScreen()
    }
}

/**
 * iOS-specific example showing a settings-style list with the parallax toolbar.
 */
fun IOSSettingsStyleViewController() = ComposeUIViewController {
    MaterialTheme {
        IOSSettingsStyleScreen()
    }
}

/**
 * iOS-specific example showing a photo gallery-style list with the parallax toolbar.
 */
fun IOSPhotoGalleryViewController() = ComposeUIViewController {
    MaterialTheme {
        IOSPhotoGalleryScreen()
    }
}

/**
 * Creates a UIViewController with all sample screens organized in a container.
 * This is the main entry point for viewing all samples in iOS.
 */
fun AllSamplesViewController() = ComposeUIViewController {
    MaterialTheme {
        // Sample contains all examples to easily switch between them
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            // By default, show the simple sample
            SimpleParallaxToolbarScreen()

            // Other samples can be switched by updating the code:
            // LazyParallaxToolbarScreen()
            // LazyParallaxToolbarWithPaddingScreen()
            // LazyParallaxToolbarWithSpacingScreen()
            // LazyParallaxToolbarWithScrollControlScreen()
            // IOSSettingsStyleScreen()
            // IOSPhotoGalleryScreen()
        }
    }
}

/**
 * iOS-specific settings-style screen
 */
@Composable
fun IOSSettingsStyleScreen() {
    ComposeParallaxToolbarLayout(
        titleContent = { isCollapsed ->
            Text(
                text = "Settings",
                fontSize = if (isCollapsed) 18.sp else 28.sp,
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
                                Color(0xFF007AFF), // iOS Blue
                                Color(0xFF0056CC)
                            )
                        )
                    )
                    .fillMaxSize()
            )
        },
        content = ParallaxContent.Lazy(
            content = { isCollapsed ->
                val settingsSections = listOf(
                    "General" to listOf("About", "Software Update", "AirDrop", "AirPlay & Handoff"),
                    "Display & Brightness" to listOf(
                        "Brightness",
                        "Text Size",
                        "Bold Text",
                        "Dark Mode"
                    ),
                    "Privacy & Security" to listOf(
                        "Privacy Report",
                        "Analytics",
                        "Safety Check",
                        "Lockdown Mode"
                    ),
                    "Battery" to listOf("Battery Health", "Low Power Mode", "Battery Usage by App")
                )

                settingsSections.forEach { (sectionTitle, items) ->
                    // Section header
                    item {
                        Text(
                            text = sectionTitle,
                            style = MaterialTheme.typography.titleSmall,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                        )
                    }

                    // Section items
                    items(items) { item ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 2.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant
                            )
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = item,
                                    style = MaterialTheme.typography.bodyLarge
                                )
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "Navigate",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            },
            config = ParallaxToolbarDefaults.lazyColumnConfig(
                contentPadding = PaddingValues(vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            )
        ),
        navigationIcon = { isCollapsed ->
            IconButton(onClick = {}) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = if (isCollapsed) MaterialTheme.colorScheme.onSurface else Color.White
                )
            }
        }
    )
}

/**
 * iOS-specific photo gallery-style screen
 */
@Composable
fun IOSPhotoGalleryScreen() {
    ComposeParallaxToolbarLayout(
        titleContent = { isCollapsed ->
            Text(
                text = "Photos",
                fontSize = if (isCollapsed) 18.sp else 28.sp,
                fontWeight = FontWeight.Bold,
                color = if (isCollapsed) MaterialTheme.colorScheme.onSurface else Color.White
            )
        },
        subtitleContent = { isCollapsed ->
            if (!isCollapsed) {
                Text(
                    text = "All Photos • 1,234 items",
                    fontSize = 14.sp,
                    color = Color.White.copy(alpha = 0.9f)
                )
            }
        },
        headerContent = {
            Box(
                modifier = Modifier
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color(0xFF6C5CE7), // Purple
                                Color(0xFF5A4FCF)
                            )
                        )
                    )
                    .fillMaxSize()
            )
        },
        content = ParallaxContent.Lazy(
            content = { isCollapsed ->
                val photoGridData = (1..100).chunked(3) // Groups of 3 for grid layout

                items(photoGridData) { photoRow ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        photoRow.forEach { photoIndex ->
                            Card(
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(2.dp),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(40.dp) // Simulate square aspect ratio
                                        .background(
                                            brush = Brush.linearGradient(
                                                colors = listOf(
                                                    Color(0xFFF093FB),
                                                    Color(0xFFF5576C),
                                                    Color(0xFF4FACFE),
                                                    Color(0xFF00F2FE)
                                                )
                                            )
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "$photoIndex",
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }

                        // Fill remaining space if last row has fewer than 3 items
                        repeat(3 - photoRow.size) {
                            Box(modifier = Modifier.weight(1f))
                        }
                    }
                }
            },
            config = ParallaxToolbarDefaults.lazyColumnConfig(
                contentPadding = PaddingValues(4.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            )
        ),
        navigationIcon = { isCollapsed ->
            IconButton(onClick = {}) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = if (isCollapsed) MaterialTheme.colorScheme.onSurface else Color.White
                )
            }
        },
        actions = { isCollapsed ->
            IconButton(onClick = {}) {
                Icon(
                    imageVector = Icons.Default.Share,
                    contentDescription = "Share",
                    tint = if (isCollapsed) MaterialTheme.colorScheme.onSurface else Color.White
                )
            }
        }
    )
}