package am.highapps.parallaxtoolbar.sample

import am.highapps.parallaxtoolbar.ComposeParallaxToolbarLayout
import am.highapps.parallaxtoolbar.HeaderHeight
import am.highapps.parallaxtoolbar.ParallaxContent
import am.highapps.parallaxtoolbar.ParallaxToolbarDefaults
import am.highapps.parallaxtoolbar.ParallaxToolbarState
import am.highapps.parallaxtoolbar.rememberParallaxToolbarState
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
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch

enum class ContentKind { Regular, Lazy }
enum class HeaderKind { Fixed, AspectRatio, Percentage }
enum class ToolbarColor(val color: Color) {
    Black(Color.Black), Indigo(Color(0xFF3F51B5)), White(Color.White)
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
    val itemCount: Int = 30
)

/**
 * Hosts either the live playground or a fixed sample screen, with a settings button that opens
 * the configuration sheet. The sheet opens by itself on first launch so the controls are found.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlaygroundHost(initialScreen: String = "playground") {
    var screen by remember { mutableStateOf(initialScreen) }
    var config by remember { mutableStateOf(PlaygroundConfig()) }
    var showSheet by remember { mutableStateOf(initialScreen == "playground") }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()

    Box(Modifier.fillMaxSize()) {
        // Scroll position lives inside the state, so recreate it when the initial state changes.
        key(screen, config.content, config.startExpanded) {
            val toolbarState = rememberParallaxToolbarState()
            if (screen == "playground") Playground(config, toolbarState) else FixedSampleScreen(screen)

            if (screen == "playground") {
                Row(
                    modifier = Modifier.align(Alignment.BottomStart).navigationBarsPadding().padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    SmallFloatingActionButton(onClick = { scope.launch { toolbarState.expand() } }) {
                        Icon(Icons.Default.KeyboardArrowDown, contentDescription = "Expand")
                    }
                    SmallFloatingActionButton(onClick = { scope.launch { toolbarState.collapse() } }) {
                        Icon(Icons.Default.KeyboardArrowUp, contentDescription = "Collapse")
                    }
                    Text(
                        "${(toolbarState.collapseFraction * 100).toInt()}%",
                        modifier = Modifier.align(Alignment.CenterVertically)
                    )
                }
            }
        }

        ExtendedFloatingActionButton(
            onClick = { showSheet = true },
            icon = { Icon(Icons.Default.Settings, contentDescription = null) },
            text = { Text("Configure") },
            modifier = Modifier.align(Alignment.BottomEnd).navigationBarsPadding().padding(16.dp)
        )

        if (showSheet) {
            ModalBottomSheet(onDismissRequest = { showSheet = false }, sheetState = sheetState) {
                ConfigSheet(
                    screen = screen,
                    onScreenChange = { screen = it },
                    config = config,
                    onChange = { config = it }
                )
            }
        }
    }
}

@Composable
fun PlaygroundScreen() = PlaygroundHost("playground")

@Composable
private fun Playground(config: PlaygroundConfig, state: ParallaxToolbarState) {
    val headerHeight = when (config.headerKind) {
        HeaderKind.Fixed -> HeaderHeight.Fixed(config.fixedHeightDp.dp)
        HeaderKind.AspectRatio -> HeaderHeight.AspectRatio(config.aspectRatio)
        HeaderKind.Percentage -> HeaderHeight.Percentage(config.percentage)
    }
    val onToolbar = if (config.toolbarColor == ToolbarColor.White) Color.Black else Color.White

    ComposeParallaxToolbarLayout(
        titleContent = { collapsed ->
            Text(
                text = if (collapsed) "Collapsed" else "Parallax Toolbar",
                color = if (collapsed) onToolbar else Color.White,
                fontSize = if (collapsed) 20.sp else 28.sp,
                fontWeight = FontWeight.Bold
            )
        },
        subtitleContent = if (config.subtitle) {
            { collapsed ->
                Text(
                    text = "${config.content} · ${config.headerKind}",
                    color = if (collapsed) onToolbar.copy(alpha = 0.7f) else Color.White.copy(alpha = 0.85f),
                    fontSize = 14.sp
                )
            }
        } else null,
        headerContent = {
            Box(
                Modifier.fillMaxSize().background(
                    Brush.linearGradient(listOf(Color(0xFF6A11CB), Color(0xFF2575FC)))
                )
            )
        },
        navigationIcon = if (config.navigationIcon) {
            { collapsed ->
                IconButton(onClick = {}) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = if (collapsed) onToolbar else Color.White
                    )
                }
            }
        } else null,
        actions = if (config.actions) {
            { collapsed ->
                val tint = if (collapsed) onToolbar else Color.White
                IconButton(onClick = {}) { Icon(Icons.Default.Favorite, "Like", tint = tint) }
                IconButton(onClick = {}) { Icon(Icons.Default.Share, "Share", tint = tint) }
            }
        } else null,
        headerConfig = ParallaxToolbarDefaults.headerConfig(
            height = headerHeight,
            gradient = if (config.gradient) {
                Brush.verticalGradient(listOf(Color.Transparent, Color.Black.copy(alpha = 0.6f)))
            } else null,
            isExpandedWhenFirstDisplayed = config.startExpanded,
            parallaxMultiplier = config.parallaxMultiplier
        ),
        toolbarConfig = ParallaxToolbarDefaults.toolbarConfig(
            targetColor = config.toolbarColor.color,
            elevation = config.elevation.dp,
            height = config.toolbarHeight.dp
        ),
        titleConfig = ParallaxToolbarDefaults.titleConfig(
            collapsedPaddingStart = config.collapsedTitlePaddingStart.dp,
            keepSubtitleAfterCollapse = config.keepSubtitleAfterCollapse,
            animateSubTitleHiding = config.animateSubtitleHiding,
            collapsedScale = config.collapsedTitleScale
        ),
        state = state,
        bodyConfig = ParallaxToolbarDefaults.bodyConfig(minBottomSpacerHeight = config.minBottomSpacer.dp),
        contentPadding = PaddingValues(bottom = config.bottomContentPadding.dp),
        content = when (config.content) {
            ContentKind.Regular -> ParallaxContent.Regular { collapsed ->
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    repeat(config.itemCount) { i -> SampleCard(i, collapsed) }
                }
            }
            ContentKind.Lazy -> ParallaxContent.Lazy(
                content = { collapsed ->
                    items(config.itemCount) { i -> SampleCard(i, collapsed) }
                },
                config = ParallaxToolbarDefaults.lazyColumnConfig(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                )
            )
        }
    )
}

@Composable
private fun SampleCard(index: Int, collapsed: Boolean) {
    Card(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp)) {
            Text("Item ${index + 1}", fontWeight = FontWeight.SemiBold)
            Text(
                if (collapsed) "Toolbar is collapsed" else "Toolbar is expanded",
                style = MaterialTheme.typography.bodySmall
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
    onChange: (PlaygroundConfig) -> Unit
) {
    Column(
        Modifier.verticalScroll(rememberScrollState()).padding(horizontal = 20.dp).padding(bottom = 32.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text("Configuration", style = MaterialTheme.typography.titleLarge)
        Text(
            "Pick a screen. \"playground\" applies the settings below live; the others are the fixed samples from the library.",
            style = MaterialTheme.typography.bodySmall
        )

        Section("Screen")
        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            sampleScreenNames.forEach { name ->
                FilterChip(selected = name == screen, onClick = { onScreenChange(name) }, label = { Text(name) })
            }
        }

        if (screen != "playground") return@Column

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
        SwitchRow("Start expanded", config.startExpanded) { onChange(config.copy(startExpanded = it)) }

        Section("Toolbar")
        Choice(ToolbarColor.entries, config.toolbarColor) { onChange(config.copy(toolbarColor = it)) }
        SliderRow("Elevation: ${config.elevation.toInt()} dp", config.elevation, 0f..12f) {
            onChange(config.copy(elevation = it))
        }
        SliderRow("Toolbar height: ${config.toolbarHeight.toInt()} dp", config.toolbarHeight, 48f..96f) {
            onChange(config.copy(toolbarHeight = it))
        }
        SwitchRow("Navigation icon", config.navigationIcon) { onChange(config.copy(navigationIcon = it)) }
        SwitchRow("Actions", config.actions) { onChange(config.copy(actions = it)) }

        Section("Title")
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
private fun <T : Enum<T>> Choice(options: List<T>, selected: T, onSelect: (T) -> Unit) {
    SingleChoiceSegmentedButtonRow(Modifier.fillMaxWidth()) {
        options.forEachIndexed { index, option ->
            SegmentedButton(
                selected = option == selected,
                onClick = { onSelect(option) },
                shape = SegmentedButtonDefaults.itemShape(index = index, count = options.size)
            ) { Text(option.name) }
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
