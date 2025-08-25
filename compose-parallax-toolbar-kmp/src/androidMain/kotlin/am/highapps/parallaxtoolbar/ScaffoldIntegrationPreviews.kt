package am.highapps.parallaxtoolbar

import android.annotation.SuppressLint
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
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Preview demonstrations for the Scaffold padding fix in ComposeParallaxToolbarLayout.
 * These previews showcase the difference between using and not using the contentPadding parameter.
 */

@Preview(
    name = "✅ Scaffold Integration - With Padding Fix",
    showBackground = true,
    device = "spec:width=411dp,height=891dp",
    showSystemUi = true
)
@Composable
fun ScaffoldIntegrationWithPaddingPreview() {
    MaterialTheme {
        Scaffold(
            bottomBar = {
                BottomAppBar {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        repeat(4) { index ->
                            IconButton(onClick = {}) {
                                Icon(
                                    imageVector = Icons.Default.Share,
                                    contentDescription = "Tab ${index + 1}"
                                )
                            }
                        }
                    }
                }
            },
            floatingActionButton = {
                FloatingActionButton(onClick = {}) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "FAB"
                    )
                }
            }
        ) { paddingValues ->
            // ✅ FIXED: Using contentPadding to respect Scaffold padding
            ComposeParallaxToolbarLayout(
                contentPadding = paddingValues, // This prevents content from drawing behind bottom bar
                titleContent = { isCollapsed ->
                    Text(
                        text = "Scaffold Fixed",
                        fontSize = if (isCollapsed) 18.sp else 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isCollapsed) MaterialTheme.colorScheme.onSurface else Color.White
                    )
                },
                subtitleContent = { isCollapsed ->
                    if (!isCollapsed) {
                        Text(
                            text = "Content respects bottom bar padding",
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
                            .fillMaxSize()
                    )
                },
                content = ParallaxContent.Regular { isCollapsed ->
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        repeat(15) { index ->
                            Spacer(modifier = Modifier.height(16.dp))
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (index >= 12) Color(0xFF4CAF50) else MaterialTheme.colorScheme.surface
                                )
                            ) {
                                Text(
                                    text = if (index >= 12) "✅ Last items don't hide behind bottom bar" else "Content item ${index + 1}",
                                    modifier = Modifier.padding(16.dp),
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = if (index >= 12) Color.White else MaterialTheme.colorScheme.onSurface
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
    }
}

@Preview(
    name = "❌ Scaffold Integration - WITHOUT Padding Fix",
    showBackground = true,
    device = "spec:width=411dp,height=891dp",
    showSystemUi = true
)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun ScaffoldIntegrationWithoutPaddingPreview() {
    MaterialTheme {
        Scaffold(
            bottomBar = {
                BottomAppBar {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        repeat(4) { index ->
                            IconButton(onClick = {}) {
                                Icon(
                                    imageVector = Icons.Default.Share,
                                    contentDescription = "Tab ${index + 1}"
                                )
                            }
                        }
                    }
                }
            },
            floatingActionButton = {
                FloatingActionButton(onClick = {}) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "FAB"
                    )
                }
            }
        ) { paddingValues ->
            // ❌ BROKEN: NOT using contentPadding (old behavior)
            ComposeParallaxToolbarLayout(
                // contentPadding = paddingValues, // Missing this line causes the issue
                titleContent = { isCollapsed ->
                    Text(
                        text = "Scaffold Broken",
                        fontSize = if (isCollapsed) 18.sp else 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isCollapsed) MaterialTheme.colorScheme.onSurface else Color.White
                    )
                },
                subtitleContent = { isCollapsed ->
                    if (!isCollapsed) {
                        Text(
                            text = "Content draws behind bottom bar",
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
                                        Color(0xFFFF5722),
                                        Color(0xFFD84315)
                                    )
                                )
                            )
                            .fillMaxSize()
                    )
                },
                content = ParallaxContent.Regular { isCollapsed ->
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        repeat(15) { index ->
                            Spacer(modifier = Modifier.height(16.dp))
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (index >= 12) Color(0xFFFF5722) else MaterialTheme.colorScheme.surface
                                )
                            ) {
                                Text(
                                    text = if (index >= 12) "❌ Last items hide behind bottom bar" else "Content item ${index + 1}",
                                    modifier = Modifier.padding(16.dp),
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = if (index >= 12) Color.White else MaterialTheme.colorScheme.onSurface
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
    }
}

@Preview(
    name = "✅ Lazy Content with Scaffold",
    showBackground = true,
    device = "spec:width=411dp,height=891dp",
    showSystemUi = true
)
@Composable
fun LazyContentScaffoldPreview() {
    MaterialTheme {
        Scaffold(
            bottomBar = {
                BottomAppBar {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        repeat(3) { index ->
                            IconButton(onClick = {}) {
                                Icon(
                                    imageVector = Icons.Default.Share,
                                    contentDescription = "Tab ${index + 1}"
                                )
                            }
                        }
                    }
                }
            }
        ) { paddingValues ->
            ComposeParallaxToolbarLayout(
                contentPadding = paddingValues,
                titleContent = { isCollapsed ->
                    Text(
                        text = "Lazy + Scaffold",
                        fontSize = if (isCollapsed) 18.sp else 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isCollapsed) MaterialTheme.colorScheme.onSurface else Color.White
                    )
                },
                subtitleContent = { isCollapsed ->
                    if (!isCollapsed) {
                        Text(
                            text = "LazyColumn respects Scaffold padding",
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
                                        Color(0xFF6A1B9A)
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
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 8.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (index >= 47) Color(0xFF4CAF50) else MaterialTheme.colorScheme.surface
                                )
                            ) {
                                Column(
                                    modifier = Modifier.padding(16.dp)
                                ) {
                                    Text(
                                        text = if (index >= 47) "✅ Bottom items visible" else "Lazy Item ${index + 1}",
                                        style = MaterialTheme.typography.titleMedium,
                                        color = if (index >= 47) Color.White else MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = if (index >= 47) 
                                            "Thanks to contentPadding fix!" 
                                        else 
                                            "High-performance lazy loading with proper padding",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = if (index >= 47) 
                                            Color.White.copy(alpha = 0.8f)
                                        else 
                                            MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
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
}

@Preview(
    name = "🌙 Dark Theme - Scaffold Integration",
    uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES,
    showBackground = true,
    device = "spec:width=411dp,height=891dp",
    showSystemUi = true
)
@Composable
fun DarkThemeScaffoldPreview() {
    MaterialTheme {
        Scaffold(
            bottomBar = {
                BottomAppBar {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        repeat(4) { index ->
                            IconButton(onClick = {}) {
                                Icon(
                                    imageVector = Icons.Default.Share,
                                    contentDescription = "Tab ${index + 1}"
                                )
                            }
                        }
                    }
                }
            },
            floatingActionButton = {
                FloatingActionButton(onClick = {}) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "FAB"
                    )
                }
            }
        ) { paddingValues ->
            ComposeParallaxToolbarLayout(
                contentPadding = paddingValues,
                titleContent = { isCollapsed ->
                    Text(
                        text = "Dark Theme",
                        fontSize = if (isCollapsed) 18.sp else 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isCollapsed) MaterialTheme.colorScheme.onSurface else Color.White
                    )
                },
                subtitleContent = { isCollapsed ->
                    if (!isCollapsed) {
                        Text(
                            text = "Works perfectly in dark mode",
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
                                        Color(0xFF0D47A1)
                                    )
                                )
                            )
                            .fillMaxSize()
                    )
                },
                content = ParallaxContent.Regular { isCollapsed ->
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        repeat(12) { index ->
                            Spacer(modifier = Modifier.height(16.dp))
                            Card(
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = "Dark theme content ${index + 1}",
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
    }
}

@Preview(
    name = "📱 Interactive Demo Preview",
    showBackground = true,
    device = "spec:width=411dp,height=891dp",
    showSystemUi = true
)
@Composable
fun InteractiveDemoPreview() {
    // This preview can be used for interactive testing in Android Studio
    ParallaxToolbarInScaffoldScreen()
}
