package am.highapps.parallaxtoolbar

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
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

/**
 * Sample usage of ComposeParallaxToolbarLayout with regular content
 */
@Composable
fun SimpleParallaxToolbarScreen() {
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
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                repeat(10) { index ->
                    Spacer(modifier = Modifier.height(16.dp))
                    Card(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Regular Content Item ${index + 1}",
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
                    contentDescription = "Back"
                )
            }
        }
    )
}

/**
 * Sample usage of ComposeParallaxToolbarLayout with lazy content
 */
@Composable
fun LazyParallaxToolbarScreen() {
    ComposeParallaxToolbarLayout(
        titleContent = { isCollapsed ->
            Text(
                text = "Lazy Content Example",
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
                                Color(0xFF4CAF50),
                                Color(0xFF388E3C)
                            )
                        )
                    )
                    .fillMaxSize()
            )
        },
        content = ParallaxContent.Lazy(
            content = { isCollapsed ->
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
                                text = "Lazy Item ${index + 1}",
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text(
                                text = "This is a lazy-loaded item that performs well with large lists",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                            )
                        }
                    }
                }
            },
            lazyListState = rememberLazyListState()
        ),
        navigationIcon = { isCollapsed ->
            IconButton(onClick = {}) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
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
        }
    )
}

/**
 * Sample usage with custom LazyColumn configuration - Content Padding & Spacing
 */
@Composable
fun LazyParallaxToolbarWithPaddingScreen() {
    ComposeParallaxToolbarLayout(
        titleContent = { isCollapsed ->
            Text(
                text = "Custom Padding",
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
                                Color(0xFF673AB7),
                                Color(0xFF512DA8)
                            )
                        )
                    )
                    .fillMaxSize()
            )
        },
        content = ParallaxContent.Lazy(
            content = { isCollapsed ->
                items(50) { index ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = "Padded Item ${index + 1}",
                            modifier = Modifier.padding(20.dp),
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            },
            config = ParallaxToolbarDefaults.lazyColumnConfig(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            )
        ),
        navigationIcon = { isCollapsed ->
            IconButton(onClick = {}) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back"
                )
            }
        }
    )
}

/**
 * Sample usage with reverse layout LazyColumn
 */
@Composable
fun LazyParallaxToolbarReversedScreen() {
    ComposeParallaxToolbarLayout(
        titleContent = { isCollapsed ->
            Text(
                text = "Reversed Layout",
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
                                Color(0xFFFF5722),
                                Color(0xFFD84315)
                            )
                        )
                    )
                    .fillMaxSize()
            )
        },
        content = ParallaxContent.Lazy(
            content = { isCollapsed ->
                items(30) { index ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        colors = androidx.compose.material3.CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(
                                text = "Reversed Item ${index + 1}",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                            Text(
                                text = "Items appear from bottom to top",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.7f)
                            )
                        }
                    }
                }
            },
            config = ParallaxToolbarDefaults.lazyColumnConfig(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.Bottom
            )
        ),
        navigationIcon = { isCollapsed ->
            IconButton(onClick = {}) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
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
        }
    )
}

/**
 * Sample usage with center-aligned LazyColumn content
 */
@Composable
fun LazyParallaxToolbarCenteredScreen() {
    ComposeParallaxToolbarLayout(
        titleContent = { isCollapsed ->
            Text(
                text = "Center Aligned",
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
                                Color(0xFF00BCD4),
                                Color(0xFF0097A7)
                            )
                        )
                    )
                    .fillMaxSize()
            )
        },
        content = ParallaxContent.Lazy(
            content = { isCollapsed ->
                items(25) { index ->
                    Card(
                        modifier = Modifier.padding(horizontal = 32.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = androidx.compose.material3.CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f)
                        )
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Centered ${index + 1}",
                                style = MaterialTheme.typography.headlineSmall,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            },
            config = ParallaxToolbarDefaults.lazyColumnConfig(
                contentPadding = PaddingValues(vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            )
        ),
        navigationIcon = { isCollapsed ->
            IconButton(onClick = {}) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back"
                )
            }
        }
    )
}

/**
 * Sample usage with custom LazyColumn configuration - Spacing & Arrangement
 */
@Composable
fun LazyParallaxToolbarWithSpacingScreen() {
    ComposeParallaxToolbarLayout(
        titleContent = { isCollapsed ->
            Text(
                text = if (isCollapsed) "Spaced List" else "Custom Spacing Example",
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
                                Color(0xFF4CAF50),
                                Color(0xFF2E7D32)
                            )
                        )
                    )
                    .fillMaxSize()
            )
        },
        content = ParallaxContent.Lazy(
            content = { isCollapsed ->
                items(25) { index ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(20.dp)
                        ) {
                            Text(
                                text = "Spaced Item ${index + 1}",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Text(
                                text = "Items have custom spacing using Arrangement.spacedBy()",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                            )
                        }
                    }
                }
            },
            config = ParallaxToolbarDefaults.lazyColumnConfig(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
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

/**
 * Sample usage with custom LazyColumn configuration - Scroll Control
 */
@Composable
fun LazyParallaxToolbarWithScrollControlScreen() {
    ComposeParallaxToolbarLayout(
        titleContent = { isCollapsed ->
            Text(
                text = if (isCollapsed) "Scroll Control" else "Custom Scroll Behavior",
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
                                Color(0xFF9C27B0),
                                Color(0xFF6A1B9A)
                            )
                        )
                    )
                    .fillMaxSize()
            )
        },
        content = ParallaxContent.Lazy(
            content = { isCollapsed ->
                items(30) { index ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.tertiaryContainer
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "Scroll Item ${index + 1}",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.onTertiaryContainer
                                )
                                Text(
                                    text = "Custom fling behavior and scroll control",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onTertiaryContainer.copy(alpha = 0.7f)
                                )
                            }
                            Icon(
                                imageVector = Icons.Default.Share,
                                contentDescription = "Action",
                                tint = MaterialTheme.colorScheme.onTertiaryContainer.copy(alpha = 0.6f)
                            )
                        }
                    }
                }
            },
            config = ParallaxToolbarDefaults.lazyColumnConfig(
                contentPadding = PaddingValues(vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                userScrollEnabled = true
            ),
            lazyListState = rememberLazyListState()
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