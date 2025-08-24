package am.highapps.parallaxtoolbar

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Card
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

/**
 * Sample usage demonstrating different header height configurations
 * for the ComposeParallaxToolbarLayout.
 */

/**
 * Sample showcasing aspect ratio based header height
 * Perfect for video-like content or maintaining consistent proportions
 */
@Composable
fun AspectRatioHeaderSample() {
    ComposeParallaxToolbarLayout(
        titleContent = { isCollapsed ->
            Text(
                text = "Video Gallery",
                fontSize = if (isCollapsed) 18.sp else 24.sp,
                fontWeight = FontWeight.Bold,
                color = if (isCollapsed) MaterialTheme.colorScheme.onSurface else Color.White
            )
        },
        subtitleContent = { isCollapsed ->
            if (!isCollapsed) {
                Text(
                    text = "16:9 aspect ratio header",
                    fontSize = 14.sp,
                    color = Color.White.copy(alpha = 0.8f)
                )
            }
        },
        headerContent = {
            Box(
                modifier = Modifier
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color(0xFF1976D2),
                                Color(0xFF1565C0)
                            )
                        )
                    )
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "🎬",
                        style = MaterialTheme.typography.displayLarge
                    )
                    Text(
                        text = "16:9 Video Header",
                        color = Color.White,
                        style = MaterialTheme.typography.headlineSmall
                    )
                }
            }
        },
        headerConfig = ParallaxToolbarDefaults.headerConfigWithAspectRatio(
            aspectRatio = 16f / 9f
        ),
        content = ParallaxContent.Lazy(
            content = { isCollapsed ->
                items(30) { index ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(60.dp)
                                    .background(
                                        color = MaterialTheme.colorScheme.primary,
                                        shape = RoundedCornerShape(8.dp)
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "▶",
                                    color = Color.White,
                                    style = MaterialTheme.typography.titleLarge
                                )
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                            Column {
                                Text(
                                    text = "Video ${index + 1}",
                                    style = MaterialTheme.typography.titleMedium
                                )
                                Text(
                                    text = "Aspect ratio header adapts to screen width",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                                )
                            }
                        }
                    }
                }
            }
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

/**
 * Sample showcasing percentage based header height
 * Great for responsive design across different screen sizes
 */
@Composable
fun PercentageHeaderSample() {
    ComposeParallaxToolbarLayout(
        titleContent = { isCollapsed ->
            Text(
                text = "Responsive Design",
                fontSize = if (isCollapsed) 18.sp else 24.sp,
                fontWeight = FontWeight.Bold,
                color = if (isCollapsed) MaterialTheme.colorScheme.onSurface else Color.White
            )
        },
        subtitleContent = { isCollapsed ->
            if (!isCollapsed) {
                Text(
                    text = "40% of screen height - adapts to any device",
                    fontSize = 14.sp,
                    color = Color.White.copy(alpha = 0.8f)
                )
            }
        },
        headerContent = {
            Box(
                modifier = Modifier
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color(0xFF4CAF50),
                                Color(0xFF388E3C)
                            )
                        )
                    )
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "📱",
                        style = MaterialTheme.typography.displayLarge
                    )
                    Text(
                        text = "40% Screen Height",
                        color = Color.White,
                        style = MaterialTheme.typography.headlineSmall
                    )
                    Text(
                        text = "Works on all devices",
                        color = Color.White.copy(alpha = 0.8f),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        },
        headerConfig = ParallaxToolbarDefaults.headerConfigWithPercentage(
            heightPercentage = 0.4f
        ),
        content = ParallaxContent.Regular { isCollapsed ->
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "📊 Benefits of percentage-based headers:",
                        modifier = Modifier.padding(16.dp),
                        style = MaterialTheme.typography.titleMedium
                    )
                }

                val benefits = listOf(
                    "Consistent relative sizing",
                    "Adapts to screen size",
                    "Perfect for responsive design",
                    "Works on phones and tablets",
                    "Maintains visual hierarchy"
                )

                benefits.forEachIndexed { index, benefit ->
                    Spacer(modifier = Modifier.height(12.dp))
                    Card(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "✓",
                                color = MaterialTheme.colorScheme.primary,
                                style = MaterialTheme.typography.titleLarge
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = benefit,
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    }
                }

                repeat(5) { index ->
                    Spacer(modifier = Modifier.height(16.dp))
                    Card(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Additional content ${index + 1}",
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

/**
 * Sample showcasing square aspect ratio header (1:1)
 * Perfect for profile screens or square content
 */
@Composable
fun SquareHeaderSample() {
    ComposeParallaxToolbarLayout(
        titleContent = { isCollapsed ->
            Text(
                text = "Profile",
                fontSize = if (isCollapsed) 18.sp else 24.sp,
                fontWeight = FontWeight.Bold,
                color = if (isCollapsed) MaterialTheme.colorScheme.onSurface else Color.White
            )
        },
        subtitleContent = { isCollapsed ->
            if (!isCollapsed) {
                Text(
                    text = "Square header perfect for profiles",
                    fontSize = 14.sp,
                    color = Color.White.copy(alpha = 0.8f)
                )
            }
        },
        headerContent = {
            Box(
                modifier = Modifier
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color(0xFF9C27B0),
                                Color(0xFF7B1FA2)
                            )
                        )
                    )
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(120.dp)
                            .background(
                                color = Color.White.copy(alpha = 0.2f),
                                shape = RoundedCornerShape(60.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "👤",
                            style = MaterialTheme.typography.displayLarge
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "John Doe",
                        color = Color.White,
                        style = MaterialTheme.typography.headlineSmall
                    )
                    Text(
                        text = "1:1 Square Header",
                        color = Color.White.copy(alpha = 0.8f),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        },
        headerConfig = ParallaxToolbarDefaults.headerConfigWithAspectRatio(
            aspectRatio = 1f
        ),
        content = ParallaxContent.Lazy(
            content = { isCollapsed ->
                items(20) { index ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(
                                text = when (index) {
                                    0 -> "📧 Email: john.doe@example.com"
                                    1 -> "📱 Phone: +1 (555) 123-4567"
                                    2 -> "🏢 Company: Tech Corp"
                                    3 -> "📍 Location: San Francisco, CA"
                                    else -> "📋 Profile item ${index - 3}"
                                },
                                style = MaterialTheme.typography.bodyLarge
                            )
                            if (index < 4) {
                                Text(
                                    text = "Square headers work great for profile content",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                                )
                            }
                        }
                    }
                }
            }
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

/**
 * Sample showcasing compact design with small percentage header
 * Perfect for apps that need more content space
 */
@Composable
fun CompactHeaderSample() {
    ComposeParallaxToolbarLayout(
        titleContent = { isCollapsed ->
            Text(
                text = "Compact Design",
                fontSize = if (isCollapsed) 18.sp else 22.sp,
                fontWeight = FontWeight.Bold,
                color = if (isCollapsed) MaterialTheme.colorScheme.onSurface else Color.White
            )
        },
        subtitleContent = { isCollapsed ->
            if (!isCollapsed) {
                Text(
                    text = "25% header - more space for content",
                    fontSize = 13.sp,
                    color = Color.White.copy(alpha = 0.8f)
                )
            }
        },
        headerContent = {
            Box(
                modifier = Modifier
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color(0xFFFF5722),
                                Color(0xFFE64A19)
                            )
                        )
                    )
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Compact Header (25%)",
                    color = Color.White,
                    style = MaterialTheme.typography.headlineSmall
                )
            }
        },
        headerConfig = ParallaxToolbarDefaults.headerConfigWithPercentage(
            heightPercentage = 0.25f
        ),
        content = ParallaxContent.Lazy(
            content = { isCollapsed ->
                items(50) { index ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "Compact list item ${index + 1} - More content fits on screen with smaller headers",
                            modifier = Modifier.padding(12.dp),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            },
            config = ParallaxToolbarDefaults.lazyColumnConfig(
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
 * Sample showcasing ultrawide aspect ratio header (21:9)
 * Perfect for cinematic content or modern widescreen layouts
 */
@Composable
fun UltrawideHeaderSample() {
    ComposeParallaxToolbarLayout(
        titleContent = { isCollapsed ->
            Text(
                text = "Cinema Mode",
                fontSize = if (isCollapsed) 18.sp else 24.sp,
                fontWeight = FontWeight.Bold,
                color = if (isCollapsed) MaterialTheme.colorScheme.onSurface else Color.White
            )
        },
        subtitleContent = { isCollapsed ->
            if (!isCollapsed) {
                Text(
                    text = "21:9 ultrawide cinematic experience",
                    fontSize = 14.sp,
                    color = Color.White.copy(alpha = 0.8f)
                )
            }
        },
        headerContent = {
            Box(
                modifier = Modifier
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color(0xFF37474F),
                                Color(0xFF263238)
                            )
                        )
                    )
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "🎭",
                        style = MaterialTheme.typography.displayLarge
                    )
                    Text(
                        text = "21:9 Ultrawide",
                        color = Color.White,
                        style = MaterialTheme.typography.headlineSmall
                    )
                    Text(
                        text = "Cinematic proportions",
                        color = Color.White.copy(alpha = 0.8f),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        },
        headerConfig = ParallaxToolbarDefaults.headerConfigWithAspectRatio(
            aspectRatio = 21f / 9f
        ),
        content = ParallaxContent.Regular { isCollapsed ->
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "🎬 Movie Collection",
                        modifier = Modifier.padding(16.dp),
                        style = MaterialTheme.typography.titleLarge
                    )
                }

                val movies = listOf(
                    "Blade Runner 2049",
                    "The Dark Knight",
                    "Interstellar",
                    "Mad Max: Fury Road",
                    "Dune"
                )

                movies.forEachIndexed { index, movie ->
                    Spacer(modifier = Modifier.height(12.dp))
                    Card(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(50.dp)
                                    .background(
                                        color = MaterialTheme.colorScheme.primary,
                                        shape = RoundedCornerShape(8.dp)
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "🎥",
                                    style = MaterialTheme.typography.titleMedium
                                )
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                            Column {
                                Text(
                                    text = movie,
                                    style = MaterialTheme.typography.titleMedium
                                )
                                Text(
                                    text = "Ultrawide aspect ratio perfect for cinematic content",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                                )
                            }
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