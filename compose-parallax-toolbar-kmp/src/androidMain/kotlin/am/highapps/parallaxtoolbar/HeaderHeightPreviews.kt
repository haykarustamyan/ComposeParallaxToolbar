package am.highapps.parallaxtoolbar

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * This file contains preview composables demonstrating different header height configurations
 * for the ComposeParallaxToolbarLayout.
 */

@Preview(
    name = "Fixed Height Header",
    showBackground = true,
    device = "spec:width=411dp,height=891dp",
    showSystemUi = true
)
@Composable
fun FixedHeightHeaderPreview() {
    MaterialTheme {
        ComposeParallaxToolbarLayout(
            titleContent = { isCollapsed ->
                Text(
                    text = "Fixed Height (450dp)",
                    fontSize = if (isCollapsed) 18.sp else 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isCollapsed) MaterialTheme.colorScheme.onSurface else Color.White
                )
            },
            subtitleContent = { isCollapsed ->
                if (!isCollapsed) {
                    Text(
                        text = "Traditional fixed height approach",
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
                                    Color(0xFF2196F3),
                                    Color(0xFF1976D2)
                                )
                            )
                        )
                        .fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "450dp Fixed Height",
                        color = Color.White,
                        style = MaterialTheme.typography.headlineMedium
                    )
                }
            },
            headerConfig = ParallaxToolbarDefaults.headerConfig(
                height = HeaderHeight.Fixed(450.dp)
            ),
            content = ParallaxContent.Regular {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Card(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Fixed height works well for consistent layouts but may not adapt well to different screen sizes.",
                            modifier = Modifier.padding(16.dp),
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }

                    repeat(8) { index ->
                        Spacer(modifier = Modifier.height(16.dp))
                        Card(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "Content item ${index + 1}",
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
            }
        )
    }
}

@Preview(
    name = "Aspect Ratio Header (16:9)",
    showBackground = true,
    device = "spec:width=411dp,height=891dp",
    showSystemUi = true
)
@Composable
fun AspectRatioHeaderPreview() {
    MaterialTheme {
        ComposeParallaxToolbarLayout(
            titleContent = { isCollapsed ->
                Text(
                    text = "Aspect Ratio Header",
                    fontSize = if (isCollapsed) 18.sp else 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isCollapsed) MaterialTheme.colorScheme.onSurface else Color.White
                )
            },
            subtitleContent = { isCollapsed ->
                if (!isCollapsed) {
                    Text(
                        text = "16:9 aspect ratio - adapts to screen width",
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
                            text = "16:9 Aspect Ratio",
                            color = Color.White,
                            style = MaterialTheme.typography.headlineMedium
                        )
                        Text(
                            text = "Adapts to screen width",
                            color = Color.White.copy(alpha = 0.8f),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            },
            headerConfig = ParallaxToolbarDefaults.headerConfigWithAspectRatio(
                aspectRatio = 16f / 9f
            ),
            content = ParallaxContent.Regular {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Card(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Aspect ratio headers maintain consistent proportions across different screen sizes, making them ideal for video-like layouts.",
                            modifier = Modifier.padding(16.dp),
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }

                    repeat(8) { index ->
                        Spacer(modifier = Modifier.height(16.dp))
                        Card(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "Content item ${index + 1}",
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
            }
        )
    }
}

@Preview(
    name = "Percentage Height Header (40%)",
    showBackground = true,
    device = "spec:width=411dp,height=891dp",
    showSystemUi = true
)
@Composable
fun PercentageHeightHeaderPreview() {
    MaterialTheme {
        ComposeParallaxToolbarLayout(
            titleContent = { isCollapsed ->
                Text(
                    text = "Percentage Header",
                    fontSize = if (isCollapsed) 18.sp else 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isCollapsed) MaterialTheme.colorScheme.onSurface else Color.White
                )
            },
            subtitleContent = { isCollapsed ->
                if (!isCollapsed) {
                    Text(
                        text = "40% of screen height - responsive design",
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
                        Text(
                            text = "40% Screen Height",
                            color = Color.White,
                            style = MaterialTheme.typography.headlineMedium
                        )
                        Text(
                            text = "Responsive to device",
                            color = Color.White.copy(alpha = 0.8f),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            },
            headerConfig = ParallaxToolbarDefaults.headerConfigWithPercentage(
                heightPercentage = 0.4f
            ),
            content = ParallaxContent.Regular {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Card(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Percentage-based headers ensure consistent relative sizing across all devices, perfect for responsive designs.",
                            modifier = Modifier.padding(16.dp),
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }

                    repeat(8) { index ->
                        Spacer(modifier = Modifier.height(16.dp))
                        Card(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "Content item ${index + 1}",
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
            }
        )
    }
}

@Preview(
    name = "Square Aspect Ratio Header (1:1)",
    showBackground = true,
    device = "spec:width=411dp,height=891dp",
    showSystemUi = true
)
@Composable
fun SquareAspectRatioHeaderPreview() {
    MaterialTheme {
        ComposeParallaxToolbarLayout(
            titleContent = { isCollapsed ->
                Text(
                    text = "Square Header",
                    fontSize = if (isCollapsed) 18.sp else 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isCollapsed) MaterialTheme.colorScheme.onSurface else Color.White
                )
            },
            subtitleContent = { isCollapsed ->
                if (!isCollapsed) {
                    Text(
                        text = "1:1 square aspect ratio",
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
                                    Color(0xFFFF9800),
                                    Color(0xFFF57C00)
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
                            text = "Square",
                            color = Color.White,
                            style = MaterialTheme.typography.headlineLarge
                        )
                        Text(
                            text = "1:1 Ratio",
                            color = Color.White.copy(alpha = 0.8f),
                            style = MaterialTheme.typography.headlineSmall
                        )
                    }
                }
            },
            headerConfig = ParallaxToolbarDefaults.headerConfigWithAspectRatio(
                aspectRatio = 1f
            ),
            content = ParallaxContent.Lazy(
                content = { isCollapsed ->
                    items(15) { index ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                        ) {
                            Text(
                                text = "Lazy item ${index + 1}",
                                modifier = Modifier.padding(16.dp),
                                style = MaterialTheme.typography.bodyLarge
                            )
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
}

@Preview(
    name = "Ultrawide Aspect Ratio Header (21:9)",
    showBackground = true,
    device = "spec:width=411dp,height=891dp",
    showSystemUi = true
)
@Composable
fun UltrawideAspectRatioHeaderPreview() {
    MaterialTheme {
        ComposeParallaxToolbarLayout(
            titleContent = { isCollapsed ->
                Text(
                    text = "Ultrawide Header",
                    fontSize = if (isCollapsed) 18.sp else 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isCollapsed) MaterialTheme.colorScheme.onSurface else Color.White
                )
            },
            subtitleContent = { isCollapsed ->
                if (!isCollapsed) {
                    Text(
                        text = "21:9 ultrawide aspect ratio",
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
                                    Color(0xFF607D8B),
                                    Color(0xFF455A64)
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
                            text = "21:9 Ultrawide",
                            color = Color.White,
                            style = MaterialTheme.typography.headlineMedium
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
            content = ParallaxContent.Regular {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Card(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Ultrawide aspect ratios like 21:9 create cinematic headers perfect for immersive content.",
                            modifier = Modifier.padding(16.dp),
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }

                    repeat(8) { index ->
                        Spacer(modifier = Modifier.height(16.dp))
                        Card(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "Content item ${index + 1}",
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
            }
        )
    }
}

@Preview(
    name = "Small Percentage Header (25%)",
    showBackground = true,
    device = "spec:width=411dp,height=891dp",
    showSystemUi = true
)
@Composable
fun SmallPercentageHeaderPreview() {
    MaterialTheme {
        ComposeParallaxToolbarLayout(
            titleContent = { isCollapsed ->
                Text(
                    text = "Compact Header",
                    fontSize = if (isCollapsed) 18.sp else 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isCollapsed) MaterialTheme.colorScheme.onSurface else Color.White
                )
            },
            subtitleContent = { isCollapsed ->
                if (!isCollapsed) {
                    Text(
                        text = "25% of screen height - compact design",
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
                                    Color(0xFFE91E63),
                                    Color(0xFFC2185B)
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
                            text = "Compact",
                            color = Color.White,
                            style = MaterialTheme.typography.headlineMedium
                        )
                        Text(
                            text = "25% Height",
                            color = Color.White.copy(alpha = 0.8f),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            },
            headerConfig = ParallaxToolbarDefaults.headerConfigWithPercentage(
                heightPercentage = 0.25f
            ),
            content = ParallaxContent.Lazy(
                content = { isCollapsed ->
                    items(20) { index ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                        ) {
                            Text(
                                text = "Compact design item ${index + 1}",
                                modifier = Modifier.padding(16.dp),
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    }
                },
                config = ParallaxToolbarDefaults.lazyColumnConfig(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
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
}

