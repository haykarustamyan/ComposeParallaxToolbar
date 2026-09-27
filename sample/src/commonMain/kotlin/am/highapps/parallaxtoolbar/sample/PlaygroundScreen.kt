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
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.derivedStateOf
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource

enum class ContentKind { Regular, Lazy, Grid }
enum class TitleAlign(val alignment: Alignment.Horizontal) {
    Start(Alignment.Start),
    Center(Alignment.CenterHorizontally),
    End(Alignment.End),
}
enum class HeaderKind { Fixed, AspectRatio, Percentage }
enum class ToolbarColor(val color: Color) {
    /** Resolved to the theme's surface color at draw time. */
    Surface(Color.Unspecified),
    Black(Color.Black),
    Indigo(Color(0xFF3F51B5)),
    White(Color.White),
}
enum class HeaderImage(val res: DrawableResource?, val title: String, val caption: String) {
    Mountains(Res.drawable.header_mountains, "Isle of Skye", "Quiraing, Scotland"),
    River(Res.drawable.header_river, "Alpine Valley", "Spring meltwater"),
    Forest(Res.drawable.header_forest, "Yosemite Valley", "Merced River trail"),
    None(null, "Parallax Toolbar", "Gradient header"),
}

/** Every knob the library exposes, so each can be flipped while the layout is on screen. */
data class PlaygroundConfig(
    val content: ContentKind = ContentKind.Regular,
    val headerKind: HeaderKind = HeaderKind.Fixed,
    val fixedHeightDp: Float = 320f,
    val aspectRatio: Float = 16f / 9f,
    val percentage: Float = 0.4f,
    val gradient: Boolean = true,
    val startExpanded: Boolean = true,
    val snapOnRelease: Boolean = false,
    val snapThreshold: Float = 0.5f,
    val scrollMode: ScrollMode = ScrollMode.ExitUntilCollapsed,
    val collapseEnabled: Boolean = true,
    val fadeHeader: Boolean = true,
    val overlayAvatar: Boolean = false,
    /** A chip row at the bottom of the header that stays in view with `Modifier.pin`. */
    val pinnedChips: Boolean = false,
    val stretch: Boolean = false,
    val bottomTabs: Boolean = false,
    val titleAlignment: TitleAlign = TitleAlign.Start,
    val alwaysElevated: Boolean = false,
    val toolbarColor: ToolbarColor = ToolbarColor.Surface,
    val elevation: Float = 3f,
    val subtitle: Boolean = true,
    val keepSubtitleAfterCollapse: Boolean = false,
    val animateSubtitleHiding: Boolean = true,
    val navigationIcon: Boolean = true,
    val actions: Boolean = true,
    val collapsedTitlePaddingStart: Float = 64f,
    val collapsedTitleScale: Float = 0.8f,
    val parallaxMultiplier: Float = 0.5f,
    val toolbarHeight: Float = 64f,
    val bottomContentPadding: Float = 0f,
    val minBottomSpacer: Float = 0f,
    val itemCount: Int = 24,
    val darkTheme: Boolean = false,
    val rightToLeft: Boolean = false,
    val headerImage: HeaderImage = HeaderImage.Mountains,
    /** Presentation mode for recordings: no control bar. */
    val showcase: Boolean = false,
)

/** Starting configurations that show one feature each; the sheet lists the first group. */
val playgroundPresets: Map<String, PlaygroundConfig> = mapOf(
    "default" to PlaygroundConfig(),
    "grid-avatar" to PlaygroundConfig(content = ContentKind.Grid, overlayAvatar = true, headerImage = HeaderImage.Forest),
    "pin" to PlaygroundConfig(pinnedChips = true, fadeHeader = false),
    "enter-always-collapsed" to PlaygroundConfig(scrollMode = ScrollMode.EnterAlwaysCollapsed, content = ContentKind.Lazy, itemCount = 40),
    "tabs-stretch" to PlaygroundConfig(bottomTabs = true, stretch = true, headerImage = HeaderImage.River),
    "rtl" to PlaygroundConfig(rightToLeft = true, titleAlignment = TitleAlign.Center),
    "centered-title" to PlaygroundConfig(
        scrollMode = ScrollMode.EnterAlways,
        snapOnRelease = true,
        titleAlignment = TitleAlign.Center,
        headerImage = HeaderImage.Forest,
    ),
    // Dark theme with photo headers, used for the README recordings.
    "showcase-basic" to PlaygroundConfig(showcase = true, darkTheme = true, headerImage = HeaderImage.Mountains, itemCount = 20),
    "showcase-grid-avatar" to PlaygroundConfig(
        showcase = true,
        darkTheme = true,
        headerImage = HeaderImage.Forest,
        content = ContentKind.Grid,
        overlayAvatar = true,
    ),
    "showcase-exit" to PlaygroundConfig(
        showcase = true,
        darkTheme = true,
        headerImage = HeaderImage.River,
        scrollMode = ScrollMode.EnterAlwaysCollapsed,
        content = ContentKind.Lazy,
        itemCount = 40,
    ),
    "showcase-tabs" to
        PlaygroundConfig(
            showcase = true,
            darkTheme = true,
            headerImage = HeaderImage.Mountains,
            bottomTabs = true,
            stretch = true,
            itemCount = 20,
        ),
    "showcase-exit-until" to
        PlaygroundConfig(showcase = true, darkTheme = true, headerImage = HeaderImage.Forest, content = ContentKind.Lazy, itemCount = 40),
    "showcase-enter-always" to PlaygroundConfig(
        showcase = true,
        darkTheme = true,
        headerImage = HeaderImage.Mountains,
        scrollMode = ScrollMode.EnterAlways,
        content = ContentKind.Lazy,
        itemCount = 40,
    ),
    "showcase-snap" to
        PlaygroundConfig(showcase = true, darkTheme = true, headerImage = HeaderImage.River, snapOnRelease = true, itemCount = 20),
    "showcase-centered" to PlaygroundConfig(
        showcase = true,
        darkTheme = true,
        headerImage = HeaderImage.Forest,
        scrollMode = ScrollMode.EnterAlways,
        titleAlignment = TitleAlign.Center,
        collapsedTitleScale = 0.85f,
    ),
)

/** Presets offered in the sheet, with a label; the showcase ones are for recordings only. */
private val presetLabels = listOf(
    "default" to "Default",
    "grid-avatar" to "Grid + avatar",
    "pin" to "Pinned chips",
    "enter-always-collapsed" to "Toolbar exits",
    "tabs-stretch" to "Tabs + stretch",
    "centered-title" to "Centered title",
    "rtl" to "Right-to-left",
)

private fun ScrollMode.label(): String = when (this) {
    ScrollMode.ExitUntilCollapsed -> "Exit until collapsed"
    ScrollMode.EnterAlways -> "Enter always"
    ScrollMode.EnterAlwaysCollapsed -> "Enter always collapsed"
}

private fun ScrollMode.hint(): String = when (this) {
    ScrollMode.ExitUntilCollapsed -> "Scrolling up collapses the header. It expands again only once the list is back at its top."
    ScrollMode.EnterAlways -> "Scrolling up collapses the header. Any scroll down expands it, wherever the list is."
    ScrollMode.EnterAlwaysCollapsed ->
        "Scrolling up collapses the header and then slides the toolbar away. " +
            "Scrolling down brings the toolbar back at once; the header expands at the top."
}

private fun ScrollMode.next(): ScrollMode = ScrollMode.entries[(ordinal + 1) % ScrollMode.entries.size]

/**
 * Hosts the live playground or a fixed sample screen. A control bar at the bottom moves the
 * header, shows the collapse progress and the scroll mode, and opens the configuration sheet.
 * Showcase presets hide the bar for recordings.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlaygroundHost(initialScreen: String = "playground", preset: String = "default") {
    var screen by remember { mutableStateOf(initialScreen) }
    var config by remember { mutableStateOf(playgroundPresets[preset] ?: PlaygroundConfig()) }
    var showSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    MaterialTheme(colorScheme = if (config.darkTheme) darkColorScheme() else lightColorScheme()) {
        CompositionLocalProvider(
            LocalLayoutDirection provides if (config.rightToLeft) LayoutDirection.Rtl else LayoutDirection.Ltr,
        ) {
            // Scroll position lives inside the state, so recreate it when the initial state changes.
            key(screen, config.content, config.startExpanded) {
                val toolbarState = rememberParallaxToolbarState()
                if (config.showcase) {
                    Playground(config, toolbarState, PaddingValues(0.dp))
                } else {
                    // The bar sits in a Scaffold whose padding goes to the layout as contentPadding,
                    // the same way an app passes its bottom navigation bar's padding.
                    Scaffold(
                        contentWindowInsets = WindowInsets(0),
                        bottomBar = {
                            ControlBar(
                                screen = screen,
                                config = config,
                                state = toolbarState,
                                onModeChange = { config = config.copy(scrollMode = it) },
                                onBackToPlayground = { screen = "playground" },
                                onOpenSettings = { showSheet = true },
                            )
                        },
                    ) { padding ->
                        if (screen == "playground") {
                            Playground(config, toolbarState, padding)
                        } else {
                            Box(Modifier.fillMaxSize().padding(padding)) { FixedSampleScreen(screen) }
                        }
                    }
                }
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

@Composable
fun PlaygroundScreen() = PlaygroundHost("playground")

@Composable
private fun ControlBar(
    screen: String,
    config: PlaygroundConfig,
    state: ParallaxToolbarState,
    onModeChange: (ScrollMode) -> Unit,
    onBackToPlayground: () -> Unit,
    onOpenSettings: () -> Unit,
) {
    val scope = rememberCoroutineScope()
    val percent by remember(state) { derivedStateOf { (state.collapseFraction * 100).toInt() } }
    Surface(tonalElevation = 3.dp, shadowElevation = 3.dp) {
        Column(Modifier.navigationBarsPadding()) {
            if (screen == "playground") {
                LinearProgressIndicator(
                    progress = { state.collapseFraction },
                    modifier = Modifier.fillMaxWidth().height(3.dp),
                    drawStopIndicator = {},
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                if (screen == "playground") {
                    IconButton(onClick = { scope.launch { state.expand() } }) {
                        Icon(Icons.Default.KeyboardArrowDown, contentDescription = "Expand header")
                    }
                    IconButton(onClick = { scope.launch { state.collapse() } }) {
                        Icon(Icons.Default.KeyboardArrowUp, contentDescription = "Collapse header")
                    }
                    Text(
                        "$percent%",
                        modifier = Modifier.width(44.dp),
                        style = MaterialTheme.typography.labelLarge,
                        textAlign = TextAlign.Center,
                    )
                    Spacer(Modifier.weight(1f))
                    AssistChip(onClick = { onModeChange(config.scrollMode.next()) }, label = { Text(config.scrollMode.label()) })
                } else {
                    TextButton(onClick = onBackToPlayground) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text("Playground")
                    }
                    Spacer(Modifier.weight(1f))
                    Text(sampleScreens.first { it.name == screen }.title, style = MaterialTheme.typography.labelLarge)
                }
                IconButton(onClick = onOpenSettings) { Icon(Icons.Default.Settings, contentDescription = "Configure") }
            }
        }
    }
}

@Composable
private fun Playground(config: PlaygroundConfig, state: ParallaxToolbarState, contentPadding: PaddingValues) {
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
                text = config.headerImage.title,
                color = if (collapsed) onToolbar else Color.White,
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
            )
        },
        subtitleContent = if (config.subtitle) {
            { collapsed ->
                Text(
                    text = config.headerImage.caption + if (refreshCount > 0) " · refreshed $refreshCount×" else "",
                    color = if (collapsed) onToolbar.copy(alpha = 0.7f) else Color.White.copy(alpha = 0.85f),
                    fontSize = 14.sp,
                    maxLines = 1,
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
                Image(
                    painter = painterResource(image),
                    contentDescription = null,
                    modifier = zoomModifier,
                    contentScale = ContentScale.Crop,
                )
            } else {
                Box(zoomModifier.background(Brush.linearGradient(listOf(Color(0xFF6A11CB), Color(0xFF2575FC)))))
            }
            if (config.pinnedChips) {
                // Sits at the end so the title can glide past it. pin keeps it still until the
                // header's bottom edge reaches it, then it rides up and stops at the toolbar's top.
                Box(Modifier.fillMaxSize()) {
                    Row(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(end = 16.dp, bottom = 88.dp)
                            .pin(stopAtTop = true),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        listOf("Trails", "Summits").forEachIndexed { i, label ->
                            FilterChip(selected = i == 0, onClick = {}, label = { Text(label) })
                        }
                    }
                }
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
                val bottomAlpha = if (config.headerImage == HeaderImage.None) 0.6f else 0.75f
                Brush.verticalGradient(listOf(Color.Transparent, Color.Black.copy(alpha = bottomAlpha)))
            } else {
                null
            },
            isExpandedWhenFirstDisplayed = config.startExpanded,
            parallaxMultiplier = config.parallaxMultiplier,
            snapOnRelease = config.snapOnRelease,
            snapThreshold = config.snapThreshold,
            scrollMode = config.scrollMode,
            fadeOnCollapse = config.fadeHeader,
            stretchEnabled = config.stretch,
        ),
        onStretchTrigger = { refreshCount++ },
        collapseEnabled = config.collapseEnabled,
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
                // Travels from the header's bottom-end into the toolbar, shrinking on the way.
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
                        .background(Color(0xFFFFC107), CircleShape),
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
        bodyConfig = ParallaxToolbarDefaults.bodyConfig(
            minBottomSpacerHeight = config.minBottomSpacer.dp,
            backgroundColor = MaterialTheme.colorScheme.background,
        ),
        contentPadding = PaddingValues(
            start = contentPadding.calculateStartPadding(LocalLayoutDirection.current),
            end = contentPadding.calculateEndPadding(LocalLayoutDirection.current),
            bottom = contentPadding.calculateBottomPadding() + config.bottomContentPadding.dp,
        ),
        content = when (config.content) {
            ContentKind.Regular -> ParallaxContent.Regular { collapsed ->
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    repeat(config.itemCount) { i -> SampleCard(i, collapsed) }
                }
            }
            ContentKind.Lazy -> ParallaxContent.Lazy(
                content = { collapsed -> items(config.itemCount) { i -> SampleCard(i, collapsed) } },
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
                    items(config.itemCount) { i -> SampleCard(i, collapsed) }
                }
            }
        },
    )
}

private val cardTitles = listOf("Morning at the ridge", "Sea stacks", "The old path", "Weather turns", "Lochside camp", "Down the glen")
private val cardLines = listOf(
    "4.2 km · 310 m ascent",
    "Best light after 6 pm",
    "Boggy after rain",
    "Wind picks up by noon",
    "Two hours from the road",
    "Return the same way",
)

/** The `collapsed` flag every slot receives is shown as a small dot so its timing is visible. */
@Composable
private fun SampleCard(index: Int, collapsed: Boolean) {
    Card(Modifier.fillMaxWidth()) {
        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text(cardTitles[index % cardTitles.size], fontWeight = FontWeight.SemiBold, maxLines = 1)
                Text(cardLines[index % cardLines.size], style = MaterialTheme.typography.bodySmall, maxLines = 1)
            }
            Box(
                Modifier.size(8.dp).background(
                    if (collapsed) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant,
                    CircleShape,
                ),
            )
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
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text("Configure", style = MaterialTheme.typography.titleLarge, modifier = Modifier.weight(1f))
            TextButton(onClick = {
                onChange(PlaygroundConfig())
                onScreenChange("playground")
            }) { Text("Reset") }
        }

        Section("Presets")
        Text("Each one starts the playground on a feature; everything below stays editable.", style = MaterialTheme.typography.bodySmall)
        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            presetLabels.forEach { (name, label) ->
                FilterChip(
                    selected = screen == "playground" && config == playgroundPresets[name],
                    onClick = {
                        onChange(playgroundPresets.getValue(name))
                        onScreenChange("playground")
                    },
                    label = { Text(label) },
                )
            }
        }

        Section("Examples")
        Text("Fixed screens from the sample sources, as an app would write them.", style = MaterialTheme.typography.bodySmall)
        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            sampleScreens.forEach { info ->
                FilterChip(selected = info.name == screen, onClick = { onScreenChange(info.name) }, label = { Text(info.title) })
            }
        }
        sampleScreens.firstOrNull { it.name == screen }?.let {
            Text(it.description, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
            return@Column
        }

        Section("Behavior")
        ScrollMode.entries.forEach { mode ->
            Row(
                Modifier.fillMaxWidth().clickable { onChange(config.copy(scrollMode = mode)) }.padding(vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                RadioButton(selected = mode == config.scrollMode, onClick = { onChange(config.copy(scrollMode = mode)) })
                Column(Modifier.padding(start = 4.dp)) {
                    Text(mode.label(), style = MaterialTheme.typography.bodyLarge)
                    Text(mode.hint(), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
        SwitchRow("Snap on release", config.snapOnRelease) { onChange(config.copy(snapOnRelease = it)) }
        if (config.snapOnRelease) {
            SliderRow("Snap threshold: ${(config.snapThreshold * 100).toInt()} %", config.snapThreshold, 0f..1f) {
                onChange(config.copy(snapThreshold = it))
            }
        }
        SwitchRow("Stretch on overscroll (pull to refresh)", config.stretch) { onChange(config.copy(stretch = it)) }
        SwitchRow("Start expanded", config.startExpanded) { onChange(config.copy(startExpanded = it)) }
        SwitchRow("Collapse enabled", config.collapseEnabled) { onChange(config.copy(collapseEnabled = it)) }

        Section("Content")
        Choice(ContentKind.entries, config.content) { onChange(config.copy(content = it)) }
        SliderRow("Items: ${config.itemCount}", config.itemCount.toFloat(), 2f..60f) { onChange(config.copy(itemCount = it.toInt())) }

        Section("Header")
        Choice(HeaderImage.entries, config.headerImage) { onChange(config.copy(headerImage = it)) }
        Choice(HeaderKind.entries, config.headerKind) { onChange(config.copy(headerKind = it)) }
        when (config.headerKind) {
            HeaderKind.Fixed -> SliderRow("Height: ${config.fixedHeightDp.toInt()} dp", config.fixedHeightDp, 150f..500f) {
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
        SwitchRow("Pinned chip row (pin)", config.pinnedChips) { onChange(config.copy(pinnedChips = it)) }
        SwitchRow("Overlay avatar (moveBetween)", config.overlayAvatar) { onChange(config.copy(overlayAvatar = it)) }
        SwitchRow("Bottom tabs slot", config.bottomTabs) { onChange(config.copy(bottomTabs = it)) }

        Section("Toolbar")
        Choice(ToolbarColor.entries, config.toolbarColor) { onChange(config.copy(toolbarColor = it)) }
        SliderRow("Height: ${config.toolbarHeight.toInt()} dp", config.toolbarHeight, 48f..96f) {
            onChange(config.copy(toolbarHeight = it))
        }
        SliderRow("Elevation: ${config.elevation.toInt()} dp", config.elevation, 0f..12f) { onChange(config.copy(elevation = it)) }
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
        SwitchRow("Animate subtitle hiding", config.animateSubtitleHiding) { onChange(config.copy(animateSubtitleHiding = it)) }

        Section("Body")
        SliderRow("Extra bottom padding: ${config.bottomContentPadding.toInt()} dp", config.bottomContentPadding, 0f..120f) {
            onChange(config.copy(bottomContentPadding = it))
        }
        SliderRow("Min bottom spacer: ${config.minBottomSpacer.toInt()} dp", config.minBottomSpacer, 0f..200f) {
            onChange(config.copy(minBottomSpacer = it))
        }

        Section("Appearance")
        SwitchRow("Dark theme", config.darkTheme) { onChange(config.copy(darkTheme = it)) }
        SwitchRow("Right-to-left", config.rightToLeft) { onChange(config.copy(rightToLeft = it)) }
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
            ) { Text(label(option), textAlign = TextAlign.Center, fontSize = 12.sp) }
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
