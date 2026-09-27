package am.highapps.parallaxtoolbar.sample

import am.highapps.parallaxtoolbar.ComposeParallaxToolbarLayout
import am.highapps.parallaxtoolbar.HeaderHeight
import am.highapps.parallaxtoolbar.ParallaxContent
import am.highapps.parallaxtoolbar.ParallaxToolbarDefaults
import am.highapps.parallaxtoolbar.ParallaxToolbarState
import am.highapps.parallaxtoolbar.ScrollMode
import am.highapps.parallaxtoolbar.rememberParallaxToolbarState
import am.highapps.parallaxtoolbar.sample.resources.Res
import am.highapps.parallaxtoolbar.sample.resources.header_forest
import am.highapps.parallaxtoolbar.sample.resources.header_mountains
import am.highapps.parallaxtoolbar.sample.resources.header_river
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Slider
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch

enum class ContentKind { Regular, Lazy, Grid }
enum class TitleAlign(val alignment: Alignment.Horizontal) {
    Start(Alignment.Start),
    Center(Alignment.CenterHorizontally),
    End(Alignment.End),
}
enum class HeaderKind { Fixed, AspectRatio, Percentage }
enum class ToolbarColor(val color: Color) {
    Black(Color.Black),
    Indigo(Color(0xFF3F51B5)),
    White(Color.White),

    /** Resolved to the theme's surface color at draw time. */
    Surface(Color.Unspecified),
}
enum class HeaderImage(val res: org.jetbrains.compose.resources.DrawableResource?, val title: String, val caption: String) {
    None(null, "Parallax Toolbar", "Gradient"),
    Mountains(Res.drawable.header_mountains, "Isle of Skye", "Quiraing, Scotland"),
    River(Res.drawable.header_river, "Alpine Valley", "Spring meltwater"),
    Forest(Res.drawable.header_forest, "Yosemite Valley", "Merced River trail"),
}

/** Every knob the library exposes, so each can be flipped while the layout is on screen. */
data class PlaygroundConfig(
    val content: ContentKind = ContentKind.Regular,
    val headerKind: HeaderKind = HeaderKind.Fixed,
    val fixedHeightDp: Float = 350f,
    val aspectRatio: Float = 16f / 9f,
    val percentage: Float = 0.4f,
    val gradient: Boolean = true,
    val startExpanded: Boolean = true,
    val snapOnRelease: Boolean = false,
    val scrollMode: ScrollMode = ScrollMode.ExitUntilCollapsed,
    val fadeHeader: Boolean = true,
    val overlayAvatar: Boolean = false,
    val stretch: Boolean = false,
    val bottomTabs: Boolean = false,
    val titleAlignment: TitleAlign = TitleAlign.Start,
    val alwaysElevated: Boolean = false,
    val toolbarColor: ToolbarColor = ToolbarColor.Black,
    val elevation: Float = 0f,
    val subtitle: Boolean = true,
    val keepSubtitleAfterCollapse: Boolean = false,
    val animateSubtitleHiding: Boolean = true,
    val navigationIcon: Boolean = true,
    val actions: Boolean = true,
    val collapsedTitlePaddingStart: Float = 64f,
    val collapsedTitleScale: Float = 1f,
    val parallaxMultiplier: Float = 0.5f,
    val toolbarHeight: Float = 64f,
    val bottomContentPadding: Float = 0f,
    val minBottomSpacer: Float = 0f,
    val itemCount: Int = 30,
    val darkTheme: Boolean = false,
    val rightToLeft: Boolean = false,
    val headerImage: HeaderImage = HeaderImage.None,
    /** Presentation mode for recordings: captions instead of debug text, no floating controls. */
    val showcase: Boolean = false,
)

/**
 * Hosts either the live playground or a fixed sample screen, with a settings button that opens
 * the configuration sheet. The sheet opens by itself on first launch so the controls are found.
 */
/** Named starting configurations, handy for demos and recordings. */
val playgroundPresets: Map<String, PlaygroundConfig> = mapOf(
    "default" to PlaygroundConfig(),
    "grid-avatar" to PlaygroundConfig(content = ContentKind.Grid, overlayAvatar = true),
    "enter-always-collapsed" to PlaygroundConfig(scrollMode = ScrollMode.EnterAlwaysCollapsed),
    "tabs-stretch" to PlaygroundConfig(bottomTabs = true, stretch = true),
    "rtl" to PlaygroundConfig(rightToLeft = true, titleAlignment = TitleAlign.Center, collapsedTitleScale = 0.85f),
    "centered-title" to PlaygroundConfig(
        scrollMode = ScrollMode.EnterAlways,
        snapOnRelease = true,
        titleAlignment = TitleAlign.Center,
        collapsedTitleScale = 0.85f,
        toolbarColor = ToolbarColor.White,
    ),
    // Dark theme with photo headers, used for the README recordings.
    "showcase-basic" to PlaygroundConfig(
        showcase = true,
        darkTheme = true,
        headerImage = HeaderImage.Mountains,
        toolbarColor = ToolbarColor.Surface,
        elevation = 3f,
        itemCount = 20,
    ),
    "showcase-grid-avatar" to PlaygroundConfig(
        showcase = true,
        darkTheme = true,
        headerImage = HeaderImage.Forest,
        toolbarColor = ToolbarColor.Surface,
        content = ContentKind.Grid,
        overlayAvatar = true,
        itemCount = 24,
    ),
    "showcase-exit" to PlaygroundConfig(
        showcase = true,
        darkTheme = true,
        headerImage = HeaderImage.River,
        toolbarColor = ToolbarColor.Surface,
        scrollMode = ScrollMode.EnterAlwaysCollapsed,
        content = ContentKind.Lazy,
        itemCount = 40,
    ),
    "showcase-tabs" to PlaygroundConfig(
        showcase = true,
        darkTheme = true,
        headerImage = HeaderImage.Mountains,
        toolbarColor = ToolbarColor.Surface,
        bottomTabs = true,
        stretch = true,
        itemCount = 20,
    ),
    "showcase-exit-until" to PlaygroundConfig(
        showcase = true,
        darkTheme = true,
        headerImage = HeaderImage.Forest,
        toolbarColor = ToolbarColor.Surface,
        content = ContentKind.Lazy,
        itemCount = 40,
    ),
    "showcase-enter-always" to PlaygroundConfig(
        showcase = true,
        darkTheme = true,
        headerImage = HeaderImage.Mountains,
        toolbarColor = ToolbarColor.Surface,
        scrollMode = ScrollMode.EnterAlways,
        content = ContentKind.Lazy,
        itemCount = 40,
    ),
    "showcase-snap" to PlaygroundConfig(
        showcase = true,
        darkTheme = true,
        headerImage = HeaderImage.River,
        toolbarColor = ToolbarColor.Surface,
        snapOnRelease = true,
        itemCount = 20,
    ),
    "showcase-centered" to PlaygroundConfig(
        showcase = true,
        darkTheme = true,
        headerImage = HeaderImage.Forest,
        toolbarColor = ToolbarColor.Surface,
        scrollMode = ScrollMode.EnterAlways,
        titleAlignment = TitleAlign.Center,
        collapsedTitleScale = 0.85f,
        itemCount = 30,
    ),
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlaygroundHost(initialScreen: String = "playground", preset: String = "default") {
    var screen by remember { mutableStateOf(initialScreen) }
    var config by remember { mutableStateOf(playgroundPresets[preset] ?: PlaygroundConfig()) }
    var showSheet by remember { mutableStateOf(initialScreen == "playground" && preset == "default") }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()

    MaterialTheme(colorScheme = if (config.darkTheme) darkColorScheme() else lightColorScheme()) {
        androidx.compose.runtime.CompositionLocalProvider(
            androidx.compose.ui.platform.LocalLayoutDirection provides
                if (config.rightToLeft) androidx.compose.ui.unit.LayoutDirection.Rtl else androidx.compose.ui.unit.LayoutDirection.Ltr,
        ) {
            Box(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
                // Scroll position lives inside the state, so recreate it when the initial state changes.
                key(screen, config.content, config.startExpanded) {
                    val toolbarState = rememberParallaxToolbarState()
                    if (screen == "playground") Playground(config, toolbarState) else FixedSampleScreen(screen)

                    if (screen == "playground" && !config.showcase) {
                        Row(
                            modifier = Modifier.align(Alignment.BottomStart).navigationBarsPadding().padding(16.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            SmallFloatingActionButton(onClick = { scope.launch { toolbarState.expand() } }) {
                                Icon(Icons.Default.KeyboardArrowDown, contentDescription = "Expand")
                            }
                            SmallFloatingActionButton(onClick = { scope.launch { toolbarState.collapse() } }) {
                                Icon(Icons.Default.KeyboardArrowUp, contentDescription = "Collapse")
                            }
                            Text(
                                "${(toolbarState.collapseFraction * 100).toInt()}%",
                                modifier = Modifier.align(Alignment.CenterVertically),
                            )
                        }
                    }
                }

                if (!config.showcase) {
                    ExtendedFloatingActionButton(
                        onClick = { showSheet = true },
                        icon = { Icon(Icons.Default.Settings, contentDescription = null) },
                        text = { Text("Configure") },
                        modifier = Modifier.align(Alignment.BottomEnd).navigationBarsPadding().padding(16.dp),
                    )
                }

                if (showSheet) {
                    ModalBottomSheet(onDismissRequest = { showSheet = false }, sheetState = sheetState) {
                        ConfigSheet(
                            screen = screen,
                            onScreenChange = { screen = it },
                            config = config,
                            onChange = { config = it },
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun PlaygroundScreen() = PlaygroundHost("playground")

@Composable
private fun Playground(config: PlaygroundConfig, state: ParallaxToolbarState) {
    var refreshCount by remember { mutableStateOf(0) }
    val headerHeight = when (config.headerKind) {
        HeaderKind.Fixed -> HeaderHeight.Fixed(config.fixedHeightDp.dp)
        HeaderKind.AspectRatio -> HeaderHeight.AspectRatio(config.aspectRatio)
        HeaderKind.Percentage -> HeaderHeight.Percentage(config.percentage)
    }
    val toolbarTarget = if (config.toolbarColor == ToolbarColor.Surface) MaterialTheme.colorScheme.surface else config.toolbarColor.color
    val onToolbar = when (config.toolbarColor) {
        ToolbarColor.White -> Color.Black
        ToolbarColor.Surface -> MaterialTheme.colorScheme.onSurface
        else -> Color.White
    }

    ComposeParallaxToolbarLayout(
        titleContent = { collapsed ->
            Text(
                text = if (config.showcase) {
                    config.headerImage.title
                } else if (collapsed) {
                    "Collapsed"
                } else {
                    "Parallax Toolbar"
                },
                color = if (collapsed) onToolbar else Color.White,
                fontSize = if (collapsed) 20.sp else 28.sp,
                fontWeight = FontWeight.Bold,
            )
        },
        subtitleContent = if (config.subtitle) {
            { collapsed ->
                Text(
                    text = (if (config.showcase) config.headerImage.caption else "${config.content} · ${config.headerKind}") +
                        if (refreshCount > 0) " · refreshed $refreshCount×" else "",
                    color = if (collapsed) onToolbar.copy(alpha = 0.7f) else Color.White.copy(alpha = 0.85f),
                    fontSize = 14.sp,
                )
            }
        } else {
            null
        },
        headerContent = {
            // The slot scope exposes collapseFraction; reading it in graphicsLayer keeps the
            // effect on the draw path so scrolling never recomposes the header.
            val zoomModifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    val zoom = 1f + 0.15f * collapseFraction
                    scaleX = zoom
                    scaleY = zoom
                }
            val image = config.headerImage.res
            if (image != null) {
                androidx.compose.foundation.Image(
                    painter = org.jetbrains.compose.resources.painterResource(image),
                    contentDescription = null,
                    modifier = zoomModifier,
                    contentScale = androidx.compose.ui.layout.ContentScale.Crop,
                )
            } else {
                Box(zoomModifier.background(Brush.linearGradient(listOf(Color(0xFF6A11CB), Color(0xFF2575FC)))))
            }
        },
        navigationIcon = if (config.navigationIcon) {
            { collapsed ->
                IconButton(onClick = {}) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = if (collapsed) onToolbar else Color.White,
                    )
                }
            }
        } else {
            null
        },
        actions = if (config.actions) {
            { collapsed ->
                val tint = if (collapsed) onToolbar else Color.White
                IconButton(onClick = {}) { Icon(Icons.Default.Favorite, "Like", tint = tint) }
                IconButton(onClick = {}) { Icon(Icons.Default.Share, "Share", tint = tint) }
            }
        } else {
            null
        },
        headerConfig = ParallaxToolbarDefaults.headerConfig(
            height = headerHeight,
            gradient = if (config.gradient) {
                Brush.verticalGradient(
                    listOf(
                        Color.Transparent,
                        Color.Black.copy(
                            alpha = if (config.headerImage ==
                                HeaderImage.None
                            ) {
                                0.6f
                            } else {
                                0.75f
                            },
                        ),
                    ),
                )
            } else {
                null
            },
            isExpandedWhenFirstDisplayed = config.startExpanded,
            parallaxMultiplier = config.parallaxMultiplier,
            snapOnRelease = config.snapOnRelease,
            scrollMode = config.scrollMode,
            fadeOnCollapse = config.fadeHeader,
            stretchEnabled = config.stretch,
        ),
        onStretchTrigger = { refreshCount++ },
        bottomContent = if (config.bottomTabs) {
            {
                Row(
                    Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.surface).padding(vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                ) {
                    listOf("Posts", "Photos", "About").forEachIndexed { i, tab ->
                        Text(tab, fontWeight = if (i == 0) FontWeight.Bold else FontWeight.Normal)
                    }
                }
            }
        } else {
            null
        },
        overlayContent = if (config.overlayAvatar) {
            {
                // Travels from the header's bottom-start into the toolbar's end, shrinking on the way.
                Box(
                    Modifier
                        .size(72.dp)
                        .moveBetween(
                            expanded = Alignment.BottomEnd,
                            collapsed = Alignment.CenterEnd,
                            expandedPadding = PaddingValues(end = 16.dp, bottom = 24.dp),
                            collapsedPadding = PaddingValues(end = 104.dp),
                            collapsedScale = 0.5f,
                        )
                        .background(Color(0xFFFFC107), androidx.compose.foundation.shape.CircleShape),
                )
            }
        } else {
            null
        },
        toolbarConfig = ParallaxToolbarDefaults.toolbarConfig(
            targetColor = toolbarTarget,
            elevation = config.elevation.dp,
            height = config.toolbarHeight.dp,
            alwaysElevated = config.alwaysElevated,
        ),
        titleConfig = ParallaxToolbarDefaults.titleConfig(
            collapsedPaddingStart = config.collapsedTitlePaddingStart.dp,
            keepSubtitleAfterCollapse = config.keepSubtitleAfterCollapse,
            animateSubTitleHiding = config.animateSubtitleHiding,
            collapsedScale = config.collapsedTitleScale,
            collapsedAlignment = config.titleAlignment.alignment,
        ),
        state = state,
        bodyConfig = ParallaxToolbarDefaults.bodyConfig(minBottomSpacerHeight = config.minBottomSpacer.dp),
        contentPadding = PaddingValues(bottom = config.bottomContentPadding.dp),
        content = when (config.content) {
            ContentKind.Regular -> ParallaxContent.Regular { collapsed ->
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    repeat(config.itemCount) { i -> SampleCard(i, collapsed, config.showcase) }
                }
            }
            ContentKind.Lazy -> ParallaxContent.Lazy(
                content = { collapsed ->
                    items(config.itemCount) { i -> SampleCard(i, collapsed, config.showcase) }
                },
                config = ParallaxToolbarDefaults.lazyColumnConfig(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ),
            )
            // Any scrollable works as the body; here a two-column grid.
            ContentKind.Grid -> ParallaxContent.Custom { collapsed ->
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    items(config.itemCount) { i -> SampleCard(i, collapsed, config.showcase) }
                }
            }
        },
    )
}

private val showcaseTitles = listOf("Morning at the ridge", "Sea stacks", "The old path", "Weather turns", "Lochside camp", "Down the glen")
private val showcaseLines =
    listOf(
        "4.2 km · 310 m ascent",
        "Best light after 6 pm",
        "Boggy after rain",
        "Wind picks up by noon",
        "Two hours from the road",
        "Return the same way",
    )

@Composable
private fun SampleCard(index: Int, collapsed: Boolean, showcase: Boolean = false) {
    Card(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp)) {
            if (showcase) {
                Text(showcaseTitles[index % showcaseTitles.size], fontWeight = FontWeight.SemiBold)
                Text(showcaseLines[index % showcaseLines.size], style = MaterialTheme.typography.bodySmall)
            } else {
                Text("Item ${index + 1}", fontWeight = FontWeight.SemiBold)
                Text(if (collapsed) "Toolbar is collapsed" else "Toolbar is expanded", style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
private fun ConfigSheet(
    screen: String,
    onScreenChange: (String) -> Unit,
    config: PlaygroundConfig,
    onChange: (PlaygroundConfig) -> Unit,
) {
    Column(
        Modifier.verticalScroll(rememberScrollState()).padding(horizontal = 20.dp).padding(bottom = 32.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text("Configuration", style = MaterialTheme.typography.titleLarge)
        Text(
            "Pick a screen. \"playground\" applies the settings below live; the others are the fixed samples from the library.",
            style = MaterialTheme.typography.bodySmall,
        )

        Section("Screen")
        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            sampleScreenNames.forEach { name ->
                FilterChip(selected = name == screen, onClick = { onScreenChange(name) }, label = { Text(name) })
            }
        }

        if (screen != "playground") return@Column

        Section("Appearance")
        SwitchRow("Dark theme", config.darkTheme) { onChange(config.copy(darkTheme = it)) }
        SwitchRow("Right-to-left", config.rightToLeft) { onChange(config.copy(rightToLeft = it)) }
        Choice(HeaderImage.entries, config.headerImage) { onChange(config.copy(headerImage = it)) }

        Section("Content")
        Choice(ContentKind.entries, config.content) { onChange(config.copy(content = it)) }
        SliderRow("Items: ${config.itemCount}", config.itemCount.toFloat(), 2f..60f) {
            onChange(config.copy(itemCount = it.toInt()))
        }

        Section("Header height")
        Choice(HeaderKind.entries, config.headerKind) { onChange(config.copy(headerKind = it)) }
        when (config.headerKind) {
            HeaderKind.Fixed -> SliderRow("Fixed: ${config.fixedHeightDp.toInt()} dp", config.fixedHeightDp, 150f..500f) {
                onChange(config.copy(fixedHeightDp = it))
            }
            HeaderKind.AspectRatio -> SliderRow("Ratio: ${(config.aspectRatio * 100).toInt() / 100f}", config.aspectRatio, 1f..3f) {
                onChange(config.copy(aspectRatio = it))
            }
            HeaderKind.Percentage -> SliderRow("Height: ${(config.percentage * 100).toInt()} %", config.percentage, 0.2f..0.7f) {
                onChange(config.copy(percentage = it))
            }
        }
        SliderRow("Parallax multiplier: ${(config.parallaxMultiplier * 100).toInt() / 100f}", config.parallaxMultiplier, 0f..1f) {
            onChange(config.copy(parallaxMultiplier = it))
        }
        SwitchRow("Gradient overlay", config.gradient) { onChange(config.copy(gradient = it)) }
        SwitchRow("Fade header on collapse", config.fadeHeader) { onChange(config.copy(fadeHeader = it)) }
        SwitchRow("Overlay avatar (moveBetween)", config.overlayAvatar) { onChange(config.copy(overlayAvatar = it)) }
        SwitchRow("Stretch on overscroll (pull to refresh)", config.stretch) { onChange(config.copy(stretch = it)) }
        SwitchRow("Bottom tabs slot", config.bottomTabs) { onChange(config.copy(bottomTabs = it)) }
        SwitchRow("Start expanded", config.startExpanded) { onChange(config.copy(startExpanded = it)) }
        SwitchRow("Snap on release", config.snapOnRelease) { onChange(config.copy(snapOnRelease = it)) }

        Section("Scroll mode")
        Choice(ScrollMode.entries, config.scrollMode, label = { mode ->
            when (mode) {
                ScrollMode.ExitUntilCollapsed -> "Exit until\ncollapsed"
                ScrollMode.EnterAlways -> "Enter\nalways"
                ScrollMode.EnterAlwaysCollapsed -> "Enter always\ncollapsed"
            }
        }) { onChange(config.copy(scrollMode = it)) }

        Section("Toolbar")
        Choice(ToolbarColor.entries, config.toolbarColor) { onChange(config.copy(toolbarColor = it)) }
        SliderRow("Elevation: ${config.elevation.toInt()} dp", config.elevation, 0f..12f) {
            onChange(config.copy(elevation = it))
        }
        SliderRow("Toolbar height: ${config.toolbarHeight.toInt()} dp", config.toolbarHeight, 48f..96f) {
            onChange(config.copy(toolbarHeight = it))
        }
        SwitchRow("Always elevated", config.alwaysElevated) { onChange(config.copy(alwaysElevated = it)) }
        SwitchRow("Navigation icon", config.navigationIcon) { onChange(config.copy(navigationIcon = it)) }
        SwitchRow("Actions", config.actions) { onChange(config.copy(actions = it)) }

        Section("Title")
        Choice(TitleAlign.entries, config.titleAlignment) { onChange(config.copy(titleAlignment = it)) }
        SliderRow("Collapsed start padding: ${config.collapsedTitlePaddingStart.toInt()} dp", config.collapsedTitlePaddingStart, 16f..96f) {
            onChange(config.copy(collapsedTitlePaddingStart = it))
        }
        SliderRow("Collapsed scale: ${(config.collapsedTitleScale * 100).toInt()} %", config.collapsedTitleScale, 0.5f..1f) {
            onChange(config.copy(collapsedTitleScale = it))
        }
        SwitchRow("Subtitle", config.subtitle) { onChange(config.copy(subtitle = it)) }
        SwitchRow("Keep subtitle after collapse", config.keepSubtitleAfterCollapse) {
            onChange(config.copy(keepSubtitleAfterCollapse = it))
        }
        SwitchRow("Animate subtitle hiding", config.animateSubtitleHiding) {
            onChange(config.copy(animateSubtitleHiding = it))
        }

        Section("Body")
        SliderRow("Bottom content padding: ${config.bottomContentPadding.toInt()} dp", config.bottomContentPadding, 0f..120f) {
            onChange(config.copy(bottomContentPadding = it))
        }
        SliderRow("Min bottom spacer: ${config.minBottomSpacer.toInt()} dp", config.minBottomSpacer, 0f..200f) {
            onChange(config.copy(minBottomSpacer = it))
        }
    }
}

@Composable
private fun Section(title: String) {
    Spacer(Modifier.height(8.dp))
    Text(title, style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.primary)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun <T : Enum<T>> Choice(
    options: List<T>,
    selected: T,
    label: (T) -> String = { it.name },
    onSelect: (T) -> Unit,
) {
    SingleChoiceSegmentedButtonRow(Modifier.fillMaxWidth()) {
        options.forEachIndexed { index, option ->
            SegmentedButton(
                selected = option == selected,
                onClick = { onSelect(option) },
                shape = SegmentedButtonDefaults.itemShape(index = index, count = options.size),
            ) { Text(label(option), textAlign = androidx.compose.ui.text.style.TextAlign.Center, fontSize = 12.sp) }
        }
    }
}

@Composable
private fun SwitchRow(label: String, checked: Boolean, onChange: (Boolean) -> Unit) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
        Text(label)
        Switch(checked = checked, onCheckedChange = onChange)
    }
}

@Composable
private fun SliderRow(label: String, value: Float, range: ClosedFloatingPointRange<Float>, onChange: (Float) -> Unit) {
    Column {
        Text(label, style = MaterialTheme.typography.bodyMedium)
        Slider(value = value, onValueChange = onChange, valueRange = range)
    }
}
