package am.highapps.parallaxtoolbar

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.tween
import androidx.compose.foundation.OverscrollEffect
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.FlingBehavior
import androidx.compose.foundation.gestures.ScrollableDefaults
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberOverscrollEffect
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.lerp
import androidx.compose.ui.util.lerp

/**
 * Default values and factory methods for ComposeParallaxToolbar
 */
object ParallaxToolbarDefaults {
    // Header defaults
    val HeaderHeight: Dp = 450.dp
    internal const val HeaderParallaxMultiplier: Float = 0.5f
    internal const val HeaderAlphaHeightFraction: Float = 1f

    // Toolbar defaults
    internal val ToolbarHeight: Dp = 64.dp
    val ToolbarMinWidth: Dp = 56.dp
    val ToolbarIconSize: Dp = 24.dp
    val ToolbarIconSpacing: Dp = 8.dp

    // Title and subtitle defaults
    val TitlePaddingBottom: Dp = (-16).dp
    val TitlePaddingStart: Dp = 16.dp
    val TitleCollapsedPaddingStart: Dp = 64.dp
    internal const val TitleFontScaleStart: Float = 1f
    internal const val TitleFontScaleEnd: Float = 1f

    // Body defaults
    val BodyMinBottomSpacing: Dp = 0.dp

    @Composable
    fun headerConfig(
        height: Dp = HeaderHeight,
        gradient: Brush? = null,
        isExpandedWhenFirstDisplayed: Boolean = true
    ): ParallaxHeaderConfig = ParallaxHeaderConfig(
        height = height,
        gradient = gradient,
        isExpandedWhenFirstDisplayed = isExpandedWhenFirstDisplayed
    )

    @Composable
    fun toolbarConfig(
        initialColor: Color = Color.Transparent,
        targetColor: Color = Color.Black,
        elevation: Dp = 0.dp,
        iconSize: Dp = ToolbarIconSize,
        iconSpacing: Dp = ToolbarIconSpacing,
        animationSpec: AnimationSpec<Color> = tween(durationMillis = 300)
    ): ParallaxToolbarConfig = ParallaxToolbarConfig(
        initialColor = initialColor,
        targetColor = targetColor,
        elevation = elevation,
        iconSize = iconSize,
        iconSpacing = iconSpacing,
        animationSpec = animationSpec
    )

    @Composable
    fun titleConfig(
        paddingBottom: Dp = TitlePaddingBottom,
        paddingStart: Dp = TitlePaddingStart,
        collapsedPaddingStart: Dp = TitleCollapsedPaddingStart,
        keepSubtitleAfterCollapse: Boolean = false,
        animateSubTitleHiding: Boolean = true
    ): ParallaxTitleConfig = ParallaxTitleConfig(
        paddingBottom = paddingBottom,
        paddingStart = paddingStart,
        collapsedPaddingStart = collapsedPaddingStart,
        keepSubtitleAfterCollapse = keepSubtitleAfterCollapse,
        animateSubTitleHiding = animateSubTitleHiding
    )

    @Composable
    fun bodyConfig(
        minBottomSpacerHeight: Dp = BodyMinBottomSpacing
    ): ParallaxBodyConfig = ParallaxBodyConfig(
        minBottomSpacerHeight = minBottomSpacerHeight
    )

    @Composable
    fun lazyColumnConfig(
        contentPadding: PaddingValues = PaddingValues(0.dp),
        verticalArrangement: Arrangement.Vertical = Arrangement.Top,
        horizontalAlignment: Alignment.Horizontal = Alignment.Start,
        flingBehavior: FlingBehavior? = null,
        userScrollEnabled: Boolean = true,
        overscrollEffect: OverscrollEffect? = null
    ): LazyColumnConfig = LazyColumnConfig(
        contentPadding = contentPadding,
        verticalArrangement = verticalArrangement,
        horizontalAlignment = horizontalAlignment,
        flingBehavior = flingBehavior ?: ScrollableDefaults.flingBehavior(),
        userScrollEnabled = userScrollEnabled,
        overscrollEffect = overscrollEffect ?: rememberOverscrollEffect()
    )
}

/**
 * Data classes for type-safe parameters
 */
data class ParallaxHeaderConfig(
    val height: Dp,
    val gradient: Brush?,
    val isExpandedWhenFirstDisplayed: Boolean = true
)

data class ParallaxToolbarConfig(
    val initialColor: Color,
    val targetColor: Color,
    val elevation: Dp,
    val iconSize: Dp,
    val iconSpacing: Dp,
    val animationSpec: AnimationSpec<Color>
)

data class ParallaxTitleConfig(
    val paddingBottom: Dp,
    val paddingStart: Dp,
    val collapsedPaddingStart: Dp,
    val keepSubtitleAfterCollapse: Boolean,
    val animateSubTitleHiding: Boolean
)

data class ParallaxBodyConfig(
    val minBottomSpacerHeight: Dp
)

data class LazyColumnConfig(
    val contentPadding: PaddingValues = PaddingValues(0.dp),
    val verticalArrangement: Arrangement.Vertical = Arrangement.Top,
    val horizontalAlignment: Alignment.Horizontal = Alignment.Start,
    val flingBehavior: FlingBehavior? = null,
    val userScrollEnabled: Boolean = true,
    val overscrollEffect: OverscrollEffect? = null
)

/**
 * Represents different types of content that can be used with the parallax toolbar
 */
sealed class ParallaxContent {
    /**
     * Regular scrollable content using Column with vertical scroll
     */
    data class Regular(val content: @Composable (Boolean) -> Unit) : ParallaxContent()

    /**
     * LazyColumn content for better performance with large lists
     *
     * @param content The content to display in the LazyColumn
     * @param config Configuration for LazyColumn behavior (padding, arrangement, etc.)
     * @param lazyListState State object to control and observe LazyColumn scrolling
     */
    data class Lazy(
        val content: LazyListScope.(Boolean) -> Unit,
        val config: LazyColumnConfig = LazyColumnConfig(),
        val lazyListState: LazyListState? = null
    ) : ParallaxContent()
}

/**
 * Unified ComposeParallaxToolbarLayout with a single content parameter
 *
 * @param content The content to display - can be either Regular (Column + verticalScroll) or Lazy (LazyColumn)
 *                For LazyColumn content, use ParallaxContent.Lazy with LazyColumnConfig for customization
 *
 * Example usage with LazyColumn customization:
 * ```
 * ComposeParallaxToolbarLayout(
 *     content = ParallaxContent.Lazy(
 *         content = { isCollapsed ->
 *             // Your lazy items here
 *         },
 *         config = ParallaxToolbarDefaults.lazyColumnConfig(
 *             contentPadding = PaddingValues(16.dp),
 *             reverseLayout = true,
 *             verticalArrangement = Arrangement.spacedBy(8.dp)
 *         )
 *     )
 * )
 * ```
 */
@Composable
fun ComposeParallaxToolbarLayout(
    titleContent: @Composable (Boolean) -> Unit,
    headerContent: @Composable () -> Unit,
    content: ParallaxContent,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp),
    subtitleContent: (@Composable (Boolean) -> Unit)? = null,
    navigationIcon: (@Composable (Boolean) -> Unit)? = null,
    actions: (@Composable RowScope.(Boolean) -> Unit)? = null,
    headerConfig: ParallaxHeaderConfig = ParallaxToolbarDefaults.headerConfig(),
    toolbarConfig: ParallaxToolbarConfig = ParallaxToolbarDefaults.toolbarConfig(),
    titleConfig: ParallaxTitleConfig = ParallaxToolbarDefaults.titleConfig(),
    bodyConfig: ParallaxBodyConfig = ParallaxToolbarDefaults.bodyConfig(),
    scrollState: ScrollState = rememberScrollState()
) {
    // Delegate to the existing implementation based on content type
    when (content) {
        is ParallaxContent.Regular -> {
            ComposeParallaxToolbarLayout(
                titleContent = titleContent,
                headerContent = headerContent,
                content = content.content,
                modifier = modifier,
                contentPadding = contentPadding,
                subtitleContent = subtitleContent,
                navigationIcon = navigationIcon,
                actions = actions,
                headerConfig = headerConfig,
                toolbarConfig = toolbarConfig,
                titleConfig = titleConfig,
                bodyConfig = bodyConfig,
                scroll = scrollState,
                lazyContent = null,
                lazyListState = rememberLazyListState()
            )
        }

        is ParallaxContent.Lazy -> {
            ComposeParallaxToolbarLayout(
                titleContent = titleContent,
                headerContent = headerContent,
                content = { _ -> /* Empty since we're using lazy content */ },
                modifier = modifier,
                contentPadding = contentPadding,
                subtitleContent = subtitleContent,
                navigationIcon = navigationIcon,
                actions = actions,
                headerConfig = headerConfig,
                toolbarConfig = toolbarConfig,
                titleConfig = titleConfig,
                bodyConfig = bodyConfig,
                scroll = scrollState,
                lazyContent = content.content,
                lazyListState = content.lazyListState ?: rememberLazyListState(),
                lazyColumnConfig = content.config
            )
        }
    }
}

/**
 * Legacy ComposeParallaxToolbarLayout - kept for backward compatibility
 * @deprecated Use the new unified API with ParallaxContent instead
 */
@Composable
fun ComposeParallaxToolbarLayout(
    titleContent: @Composable (Boolean) -> Unit,
    headerContent: @Composable () -> Unit,
    content: @Composable (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp),
    subtitleContent: (@Composable (Boolean) -> Unit)? = null,
    navigationIcon: (@Composable (Boolean) -> Unit)? = null,
    actions: (@Composable RowScope.(Boolean) -> Unit)? = null,
    headerConfig: ParallaxHeaderConfig = ParallaxToolbarDefaults.headerConfig(),
    toolbarConfig: ParallaxToolbarConfig = ParallaxToolbarDefaults.toolbarConfig(),
    titleConfig: ParallaxTitleConfig = ParallaxToolbarDefaults.titleConfig(),
    bodyConfig: ParallaxBodyConfig = ParallaxToolbarDefaults.bodyConfig(),
    scroll: ScrollState = rememberScrollState(),
    lazyContent: (LazyListScope.(Boolean) -> Unit)? = null,
    lazyListState: LazyListState = rememberLazyListState(),
    lazyColumnConfig: LazyColumnConfig = LazyColumnConfig()
) {
    val topInset = with(LocalDensity.current) {
        WindowInsets.statusBars.getTop(this).toDp()
    }

    val toolbarHeight = ParallaxToolbarDefaults.ToolbarHeight
    val titleFontScaleStart = ParallaxToolbarDefaults.TitleFontScaleStart
    val titleFontScaleEnd = ParallaxToolbarDefaults.TitleFontScaleEnd

    BoxWithConstraints {
        val screenHeight = this@BoxWithConstraints.maxHeight

        val headerHeightPx = with(LocalDensity.current) { headerConfig.height.toPx() }
        val toolbarHeightPx = with(LocalDensity.current) { toolbarHeight.toPx() }

        val maxWidthPx = with(LocalDensity.current) { maxWidth.toPx() }

        var navIconWidthPx by remember { mutableStateOf(0f) }
        var actionsWidthPx by remember { mutableStateOf(0f) }

        val collapseRange =
            remember(headerHeightPx, toolbarHeightPx) { headerHeightPx - toolbarHeightPx }

        LaunchedEffect(collapseRange, headerConfig.isExpandedWhenFirstDisplayed) {
            if (!headerConfig.isExpandedWhenFirstDisplayed) {
                scroll.scrollTo(collapseRange.toInt() + 1)
            }
        }

        val isCollapsed =
            remember(scroll.value, lazyListState.firstVisibleItemScrollOffset, collapseRange) {
                derivedStateOf {
                    if (lazyContent != null) {
                        val firstVisibleItemIndex = lazyListState.firstVisibleItemIndex
                        val firstVisibleItemScrollOffset =
                            lazyListState.firstVisibleItemScrollOffset
                        val totalScrollOffset = if (firstVisibleItemIndex == 0) {
                            firstVisibleItemScrollOffset
                        } else {
                            collapseRange.toInt() + firstVisibleItemScrollOffset
                        }
                        totalScrollOffset > collapseRange
                    } else {
                        scroll.value > collapseRange
                    }
                }
            }

        Box(modifier = modifier) {
            Header(
                scroll = scroll,
                lazyListState = if (lazyContent != null) lazyListState else null,
                headerHeightPx = headerHeightPx,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(headerConfig.height),
                content = headerContent,
                gradientBrush = headerConfig.gradient,
                initialColor = toolbarConfig.initialColor,
                targetColor = toolbarConfig.targetColor
            )
            if (lazyContent != null) {
                LazyBody(
                    lazyListState = lazyListState,
                    screenHeight = screenHeight,
                    headerHeight = headerConfig.height,
                    toolbarHeight = toolbarHeight,
                    lazyContent = { lazyContent(isCollapsed.value) },
                    modifier = Modifier.offset(y = topInset),
                    minBottomSpacerHeight = bodyConfig.minBottomSpacerHeight,
                    config = lazyColumnConfig,
                    contentPadding = contentPadding
                )
            } else {
                Body(
                    scroll = scroll,
                    screenHeight = screenHeight,
                    headerHeight = headerConfig.height,
                    toolbarHeight = toolbarHeight,
                    content = { content(isCollapsed.value) },
                    modifier = Modifier.offset(y = topInset),
                    minBottomSpacerHeight = bodyConfig.minBottomSpacerHeight,
                    contentPadding = contentPadding
                )
            }
            Toolbar(
                scroll = scroll,
                lazyListState = if (lazyContent != null) lazyListState else null,
                headerHeightPx = headerHeightPx,
                toolbarHeightPx = toolbarHeightPx,
                navigationIcon = {
                    Box(
                        modifier = Modifier
                            .onGloballyPositioned {
                                navIconWidthPx = it.size.width.toFloat()
                            }
                            .size(if (navigationIcon != null) Dp.Unspecified else 0.dp)
                    ) {
                        navigationIcon?.invoke(isCollapsed.value)
                    }
                },
                actions = {
                    Row(
                        modifier = Modifier.onGloballyPositioned {
                            actionsWidthPx = it.size.width.toFloat()
                        }
                    ) {
                        actions?.invoke(this, isCollapsed.value)
                    }
                },
                initialColor = toolbarConfig.initialColor,
                targetColor = toolbarConfig.targetColor,
                colorAnimationSpec = toolbarConfig.animationSpec,
                elevation = toolbarConfig.elevation
            )
            TitleWithSubtitle(
                headerHeight = headerConfig.height,
                toolbarHeight = toolbarHeight,
                modifier = Modifier.offset(y = topInset)
                    .widthIn(
                        min = 0.dp,
                        max = with(LocalDensity.current) {
                            (maxWidthPx - navIconWidthPx - actionsWidthPx).toDp()
                        }
                    ),
                titleContent = titleContent,
                subtitleContent = subtitleContent,
                scroll = scroll,
                lazyListState = if (lazyContent != null) lazyListState else null,
                hasNavigationIcon = navigationIcon != null,
                keepSubtitleAfterCollapse = titleConfig.keepSubtitleAfterCollapse,
                titleWithSubTitlePaddingBottom = titleConfig.paddingBottom,
                titleWithSubTitlePaddingStart = titleConfig.paddingStart,
                titleWithSubTitleCollapsedPaddingStart = titleConfig.collapsedPaddingStart,
                titleFontScaleStart = titleFontScaleStart,
                titleFontScaleEnd = titleFontScaleEnd,
                animateSubTitleHiding = titleConfig.animateSubTitleHiding
            )
        }
    }
}

@Composable
private fun Header(
    scroll: ScrollState,
    lazyListState: LazyListState?,
    headerHeightPx: Float,
    modifier: Modifier = Modifier,
    gradientBrush: Brush? = null,
    content: @Composable () -> Unit,
    initialColor: Color,
    targetColor: Color
) {
    val parallaxMultiplier = ParallaxToolbarDefaults.HeaderParallaxMultiplier
    val alphaHeightFraction = ParallaxToolbarDefaults.HeaderAlphaHeightFraction

    Box(
        modifier = modifier
            .graphicsLayer {
                val scrollOffset = if (lazyListState != null) {
                    val firstVisibleItemIndex = lazyListState.firstVisibleItemIndex
                    val firstVisibleItemScrollOffset = lazyListState.firstVisibleItemScrollOffset
                    if (firstVisibleItemIndex == 0) {
                        firstVisibleItemScrollOffset.toFloat()
                    } else {
                        headerHeightPx + firstVisibleItemScrollOffset.toFloat()
                    }
                } else {
                    scroll.value.toFloat()
                }

                translationY = -scrollOffset * parallaxMultiplier
                alpha = (-1f / (headerHeightPx * alphaHeightFraction)) * scrollOffset + 1
            }
    ) {
        content()

        Box(
            Modifier
                .fillMaxSize()
                .background(
                    brush = gradientBrush ?: Brush.verticalGradient(
                        colors = listOf(initialColor, targetColor),
                        startY = 3 * headerHeightPx / 4
                    )
                )
        )
    }
}

@Composable
private fun Body(
    scroll: ScrollState,
    screenHeight: Dp,
    headerHeight: Dp,
    toolbarHeight: Dp,
    content: @Composable (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    minBottomSpacerHeight: Dp = 0.dp,
    contentPadding: PaddingValues = PaddingValues(0.dp)
) {
    var contentHeight by remember { mutableStateOf(0) }

    val density = LocalDensity.current
    val headerHeightPx = with(density) { headerHeight.toPx() }
    val toolbarHeightPx = with(density) { toolbarHeight.toPx() }

    val collapseRange = headerHeightPx - toolbarHeightPx
    val isCollapsed = scroll.value > collapseRange

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.fillMaxSize().verticalScroll(scroll)
    ) {
        Spacer(Modifier.height(headerHeight))

        Box(
            modifier = Modifier.onGloballyPositioned { layoutCoordinates ->
                contentHeight = layoutCoordinates.size.height
            }
        ) {
            content(isCollapsed)
        }

        val contentHeightDp = with(LocalDensity.current) { contentHeight.toDp() }
        val availableHeight = screenHeight - headerHeight + toolbarHeight
        val bottomPadding = contentPadding.calculateBottomPadding()
        val minimumSpacerHeight = (screenHeight - contentHeightDp + toolbarHeight - bottomPadding)
            .coerceAtLeast(minBottomSpacerHeight)

        Spacer(Modifier.height(minimumSpacerHeight.coerceAtLeast(0.dp)))

        // Add bottom padding as additional spacer to respect Scaffold padding
        if (bottomPadding > 0.dp) {
            Spacer(Modifier.height(bottomPadding))
        }
    }
}

@Composable
private fun LazyBody(
    lazyListState: LazyListState,
    screenHeight: Dp,
    headerHeight: Dp,
    toolbarHeight: Dp,
    lazyContent: LazyListScope.() -> Unit,
    modifier: Modifier = Modifier,
    minBottomSpacerHeight: Dp = 0.dp,
    config: LazyColumnConfig = LazyColumnConfig(),
    contentPadding: PaddingValues = PaddingValues(0.dp)
) {
    val density = LocalDensity.current
    val headerHeightPx = with(density) { headerHeight.toPx() }
    val toolbarHeightPx = with(density) { toolbarHeight.toPx() }

    val collapseRange = headerHeightPx - toolbarHeightPx
    val isCollapsed = remember(
        lazyListState.firstVisibleItemIndex,
        lazyListState.firstVisibleItemScrollOffset,
        collapseRange
    ) {
        derivedStateOf {
            val firstVisibleItemIndex = lazyListState.firstVisibleItemIndex
            val firstVisibleItemScrollOffset = lazyListState.firstVisibleItemScrollOffset
            val totalScrollOffset = if (firstVisibleItemIndex == 0) {
                firstVisibleItemScrollOffset
            } else {
                collapseRange.toInt() + firstVisibleItemScrollOffset
            }
            totalScrollOffset > collapseRange
        }
    }

    // Merge external contentPadding with LazyColumn's own contentPadding
    val mergedContentPadding = PaddingValues(
        start = config.contentPadding.calculateStartPadding(androidx.compose.ui.unit.LayoutDirection.Ltr) +
                contentPadding.calculateStartPadding(androidx.compose.ui.unit.LayoutDirection.Ltr),
        top = config.contentPadding.calculateTopPadding() +
                contentPadding.calculateTopPadding(),
        end = config.contentPadding.calculateEndPadding(androidx.compose.ui.unit.LayoutDirection.Ltr) +
                contentPadding.calculateEndPadding(androidx.compose.ui.unit.LayoutDirection.Ltr),
        bottom = config.contentPadding.calculateBottomPadding() +
                contentPadding.calculateBottomPadding()
    )

    LazyColumn(
        state = lazyListState,
        modifier = modifier.fillMaxSize(),
        contentPadding = mergedContentPadding,
        verticalArrangement = config.verticalArrangement,
        horizontalAlignment = config.horizontalAlignment,
        flingBehavior = config.flingBehavior ?: ScrollableDefaults.flingBehavior(),
        userScrollEnabled = config.userScrollEnabled,
        overscrollEffect = config.overscrollEffect
    ) {
        item {
            Spacer(Modifier.height(headerHeight))
        }

        lazyContent()

        item {
            val availableHeight = screenHeight - headerHeight + toolbarHeight
            val minimumSpacerHeight = minBottomSpacerHeight.coerceAtLeast(0.dp)
            Spacer(Modifier.height(minimumSpacerHeight))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun Toolbar(
    scroll: ScrollState,
    lazyListState: LazyListState?,
    headerHeightPx: Float,
    toolbarHeightPx: Float,
    navigationIcon: @Composable () -> Unit,
    actions: @Composable RowScope.() -> Unit,
    modifier: Modifier = Modifier,
    initialColor: Color,
    targetColor: Color,
    colorAnimationSpec: AnimationSpec<Color> = tween(durationMillis = 300),
    elevation: Dp = 0.dp
) {
    val toolbarBottom = remember(headerHeightPx, toolbarHeightPx) {
        headerHeightPx - toolbarHeightPx
    }

    val isToolbarVisible by remember(
        scroll.value,
        lazyListState?.firstVisibleItemScrollOffset,
        toolbarBottom
    ) {
        derivedStateOf {
            if (lazyListState != null) {
                val firstVisibleItemIndex = lazyListState.firstVisibleItemIndex
                val firstVisibleItemScrollOffset = lazyListState.firstVisibleItemScrollOffset
                val totalScrollOffset = if (firstVisibleItemIndex == 0) {
                    firstVisibleItemScrollOffset
                } else {
                    toolbarBottom.toInt() + firstVisibleItemScrollOffset
                }
                totalScrollOffset >= toolbarBottom
            } else {
                scroll.value >= toolbarBottom
            }
        }
    }

    val backgroundColor by animateColorAsState(
        targetValue = if (isToolbarVisible) targetColor else initialColor,
        animationSpec = colorAnimationSpec
    )

    TopAppBar(
        modifier = modifier.shadow(elevation = elevation),
        title = {},
        navigationIcon = navigationIcon,
        actions = actions,
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = backgroundColor
        )
    )
}

@Composable
private fun TitleWithSubtitle(
    scroll: ScrollState,
    lazyListState: LazyListState?,
    headerHeight: Dp,
    toolbarHeight: Dp,
    hasNavigationIcon: Boolean,
    titleContent: @Composable (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    subtitleContent: (@Composable (Boolean) -> Unit)? = null,
    keepSubtitleAfterCollapse: Boolean = false,
    titleWithSubTitlePaddingBottom: Dp = ParallaxToolbarDefaults.TitlePaddingBottom,
    titleWithSubTitlePaddingStart: Dp = ParallaxToolbarDefaults.TitlePaddingStart,
    titleWithSubTitleCollapsedPaddingStart: Dp = ParallaxToolbarDefaults.TitleCollapsedPaddingStart,
    titleFontScaleStart: Float,
    titleFontScaleEnd: Float,
    animateSubTitleHiding: Boolean = true
) {
    var combinedHeightPx by remember { mutableStateOf(0f) }
    var combinedWidthPx by remember { mutableStateOf(0f) }
    var subtitleHeightPx by remember { mutableStateOf(0f) }

    val density = LocalDensity.current
    val headerHeightPx = remember(density, headerHeight) { with(density) { headerHeight.toPx() } }
    val toolbarHeightPx = remember(density) { with(density) { toolbarHeight.toPx() } }

    val collapseRange =
        remember(headerHeightPx, toolbarHeightPx) { headerHeightPx - toolbarHeightPx }

    val collapseFraction =
        remember(scroll.value, lazyListState?.firstVisibleItemScrollOffset, collapseRange) {
            if (lazyListState != null) {
                val firstVisibleItemIndex = lazyListState.firstVisibleItemIndex
                val firstVisibleItemScrollOffset = lazyListState.firstVisibleItemScrollOffset
                val totalScrollOffset = if (firstVisibleItemIndex == 0) {
                    firstVisibleItemScrollOffset
                } else {
                    collapseRange.toInt() + firstVisibleItemScrollOffset
                }
                (totalScrollOffset / collapseRange).coerceIn(0f, 1f)
            } else {
                (scroll.value / collapseRange).coerceIn(0f, 1f)
            }
        }

    val isCollapsed =
        remember(scroll.value, lazyListState?.firstVisibleItemScrollOffset, collapseRange) {
            if (lazyListState != null) {
                val firstVisibleItemIndex = lazyListState.firstVisibleItemIndex
                val firstVisibleItemScrollOffset = lazyListState.firstVisibleItemScrollOffset
                val totalScrollOffset = if (firstVisibleItemIndex == 0) {
                    firstVisibleItemScrollOffset
                } else {
                    collapseRange.toInt() + firstVisibleItemScrollOffset
                }
                totalScrollOffset > collapseRange
            } else {
                scroll.value > collapseRange
            }
        }

    Column(
        modifier = modifier
            .graphicsLayer {
                if (keepSubtitleAfterCollapse) {
                    val scaleXY = lerp(
                        titleFontScaleStart.dp,
                        titleFontScaleEnd.dp,
                        collapseFraction
                    ).value
                    val extraStartPadding = combinedWidthPx.toDp() * (1 - scaleXY) / 2f

                    translationY = lerp(
                        headerHeightPx.toDp() - combinedHeightPx.toDp() - subtitleHeightPx.toDp() - titleWithSubTitlePaddingBottom,
                        toolbarHeightPx.toDp() / 2 - combinedHeightPx.toDp() / 2,
                        collapseFraction
                    ).toPx()

                    translationX = if (hasNavigationIcon) {
                        lerp(
                            titleWithSubTitlePaddingStart,
                            titleWithSubTitleCollapsedPaddingStart - extraStartPadding,
                            collapseFraction
                        ).toPx()
                    } else {
                        titleWithSubTitlePaddingStart.toPx()
                    }

                    scaleX = scaleXY
                    scaleY = scaleXY
                } else {
                    translationY = lerp(
                        headerHeightPx.toDp() - combinedHeightPx.toDp() - subtitleHeightPx.toDp() - titleWithSubTitlePaddingBottom,
                        toolbarHeightPx.toDp() / 2 - (combinedHeightPx - subtitleHeightPx).toDp() / 2,
                        collapseFraction
                    ).toPx()

                    translationX = if (hasNavigationIcon) {
                        lerp(
                            titleWithSubTitlePaddingStart,
                            titleWithSubTitleCollapsedPaddingStart,
                            collapseFraction
                        ).toPx()
                    } else {
                        titleWithSubTitlePaddingStart.toPx()
                    }
                }
            }
            .onGloballyPositioned {
                combinedHeightPx = it.size.height.toFloat()
                combinedWidthPx = it.size.width.toFloat()
            }
    ) {
        titleContent(isCollapsed)

        subtitleContent?.let { content ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .onGloballyPositioned {
                        subtitleHeightPx = it.size.height.toFloat()
                    }
                    .graphicsLayer {
                        if (!keepSubtitleAfterCollapse && animateSubTitleHiding) {
                            alpha = lerp(1f, 0f, collapseFraction)
                        } else if (!keepSubtitleAfterCollapse && !animateSubTitleHiding) {
                            alpha = if (isCollapsed) 0f else 1f
                        }
                    }
            ) {
                content(isCollapsed)
            }
        }
    }
}