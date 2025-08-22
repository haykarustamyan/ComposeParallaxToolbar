package am.highapps.parallaxtoolbar

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * This file contains preview composables for the ComposeParallaxToolbarLayout.
 * Use these previews to quickly visualize different configurations of the component.
 */

@Preview(
    name = "Regular Content",
    showBackground = true,
    device = "spec:width=411dp,height=891dp",
    showSystemUi = true
)
@Composable
fun RegularContentPreview() {
    MaterialTheme {
        ComposeParallaxToolbarLayout(
            titleContent = { isCollapsed ->
                Text(
                    text = "Regular Content Example",
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
                                    MaterialTheme.colorScheme.primary,
                                    MaterialTheme.colorScheme.tertiary
                                )
                            )
                        )
                        .fillMaxSize()
                )
            },
            content = ParallaxContent.Regular {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    repeat(10) { index ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp)
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
                        contentDescription = "Back"
                    )
                }
            }
        )
    }
}

@Preview(
    name = "Lazy Content",
    showBackground = true,
    device = "spec:width=411dp,height=891dp",
    showSystemUi = true
)
@Composable
fun LazyContentPreview() {
    MaterialTheme {
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
}

@Preview(
    name = "Lazy Content with Custom Padding",
    showBackground = true,
    device = "spec:width=411dp,height=891dp",
    showSystemUi = true
)
@Composable
fun LazyContentWithPaddingPreview() {
    MaterialTheme {
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
                    items(20) { index ->
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
                ),
                lazyListState = rememberLazyListState()
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
}

@Preview(
    name = "Lazy Content Center Aligned",
    showBackground = true,
    device = "spec:width=411dp,height=891dp",
    showSystemUi = true
)
@Composable
fun LazyContentCenterAlignedPreview() {
    MaterialTheme {
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
                    items(12) { index ->
                        Card(
                            modifier = Modifier.padding(horizontal = 32.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(
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
                ),
                lazyListState = rememberLazyListState()
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
} 