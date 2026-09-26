package am.highapps.parallaxtoolbar

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.animation.core.AnimationState
import androidx.compose.animation.core.animateDecay
import androidx.compose.animation.core.exponentialDecay
import androidx.compose.foundation.gestures.FlingBehavior
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.ScrollScope
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp

/**
 * A collapsing toolbar with a parallax header for Compose Multiplatform.
 *
 * The header collapses as [content] scrolls; the title glides into the toolbar and the toolbar
 * background animates to its target color. Every slot receives `true` once collapsed so it can
 * adapt colors or content.
 *
 * @param titleContent Title, positioned by the layout.
 * @param headerContent Content shown in the expanded header, e.g. an image.
 * @param content The scrollable body: [ParallaxContent.Regular] or [ParallaxContent.Lazy].
 * @param contentPadding Padding applied to the body, typically the `Scaffold` padding.
 * @param subtitleContent Optional subtitle under the title.
 * @param navigationIcon Optional leading toolbar slot.
 * @param actions Optional trailing toolbar slot.
 * @param state Hoisted state to observe the collapse fraction or expand and collapse
 *   programmatically. It also owns the scroll states of both content kinds, unless
 *   [ParallaxContent.Lazy] carries its own list state.
 *
 * Example with a customized LazyColumn:
 * ```
 * ComposeParallaxToolbarLayout(
 *     titleContent = { collapsed -> Text("Title") },
 *     headerContent = { Image(...) },
 *     content = ParallaxContent.Lazy(
 *         content = { collapsed -> items(100) { Text("Row $it") } },
 *         config = ParallaxToolbarDefaults.lazyColumnConfig(
 *             contentPadding = PaddingValues(16.dp),
 *             verticalArrangement = Arrangement.spacedBy(8.dp)
 *         )
 *     )
 * )
 * ```
 */
@Composable
public fun ComposeParallaxToolbarLayout(
    titleContent: @Composable ParallaxToolbarScope.(collapsed: Boolean) -> Unit,
    headerContent: @Composable ParallaxToolbarScope.() -> Unit,
    content: ParallaxContent,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp),
    subtitleContent: (@Composable ParallaxToolbarScope.(collapsed: Boolean) -> Unit)? = null,
    navigationIcon: (@Composable ParallaxToolbarScope.(collapsed: Boolean) -> Unit)? = null,
    actions: (@Composable ParallaxActionsScope.(collapsed: Boolean) -> Unit)? = null,
    headerConfig: ParallaxHeaderConfig = ParallaxToolbarDefaults.headerConfig(),
    toolbarConfig: ParallaxToolbarConfig = ParallaxToolbarDefaults.toolbarConfig(),
    titleConfig: ParallaxTitleConfig = ParallaxToolbarDefaults.titleConfig(),
    bodyConfig: ParallaxBodyConfig = ParallaxToolbarDefaults.bodyConfig(),
    state: ParallaxToolbarState = rememberParallaxToolbarState()
) {
    when (content) {
        is ParallaxContent.Regular -> ParallaxToolbarLayoutImpl(
            titleContent = titleContent,
            headerContent = headerContent,
            modifier = modifier,
            contentPadding = contentPadding,
            subtitleContent = subtitleContent,
            navigationIcon = navigationIcon,
            actions = actions,
            headerConfig = headerConfig,
            toolbarConfig = toolbarConfig,
            titleConfig = titleConfig,
            bodyConfig = bodyConfig,
            state = state,
            body = ParallaxBodySpec.Regular(content.content)
        )

        is ParallaxContent.Lazy -> ParallaxToolbarLayoutImpl(
            titleContent = titleContent,
            headerContent = headerContent,
            modifier = modifier,
            contentPadding = contentPadding,
            subtitleContent = subtitleContent,
            navigationIcon = navigationIcon,
            actions = actions,
            headerConfig = headerConfig,
            toolbarConfig = toolbarConfig,
            titleConfig = titleConfig,
            bodyConfig = bodyConfig,
            state = state,
            body = ParallaxBodySpec.Lazy(
                lazyListState = content.lazyListState ?: state.lazyListState,
                config = content.config,
                content = content.content
            )
        )

        is ParallaxContent.Custom -> ParallaxToolbarLayoutImpl(
            titleContent = titleContent,
            headerContent = headerContent,
            modifier = modifier,
            contentPadding = contentPadding,
            subtitleContent = subtitleContent,
            navigationIcon = navigationIcon,
            actions = actions,
            headerConfig = headerConfig,
            toolbarConfig = toolbarConfig,
            titleConfig = titleConfig,
            bodyConfig = bodyConfig,
            state = state,
            body = ParallaxBodySpec.Custom(content.content)
        )
    }
}

/**
 * The 1.x entry point, kept so existing call sites compile. It wraps [content] or [lazyContent]
 * into a [ParallaxContent] and [scroll] into a [ParallaxToolbarState], then delegates to the
 * main overload. It will be removed in 3.0.
 */
@Deprecated(
    message = "Use the overload that takes ParallaxContent.",
    replaceWith = ReplaceWith(
        "ComposeParallaxToolbarLayout(titleContent, headerContent, ParallaxContent.Regular(content), " +
                "modifier, contentPadding, subtitleContent, navigationIcon, actions, headerConfig, " +
                "toolbarConfig, titleConfig, bodyConfig, rememberParallaxToolbarState(scroll, lazyListState))"
    )
)
@Composable
public fun ComposeParallaxToolbarLayout(
    titleContent: @Composable ParallaxToolbarScope.(collapsed: Boolean) -> Unit,
    headerContent: @Composable ParallaxToolbarScope.() -> Unit,
    content: @Composable ParallaxToolbarScope.(collapsed: Boolean) -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp),
    subtitleContent: (@Composable ParallaxToolbarScope.(collapsed: Boolean) -> Unit)? = null,
    navigationIcon: (@Composable ParallaxToolbarScope.(collapsed: Boolean) -> Unit)? = null,
    actions: (@Composable ParallaxActionsScope.(collapsed: Boolean) -> Unit)? = null,
    headerConfig: ParallaxHeaderConfig = ParallaxToolbarDefaults.headerConfig(),
    toolbarConfig: ParallaxToolbarConfig = ParallaxToolbarDefaults.toolbarConfig(),
    titleConfig: ParallaxTitleConfig = ParallaxToolbarDefaults.titleConfig(),
    bodyConfig: ParallaxBodyConfig = ParallaxToolbarDefaults.bodyConfig(),
    scroll: ScrollState = rememberScrollState(),
    lazyContent: (LazyListScope.(Boolean) -> Unit)? = null,
    lazyListState: LazyListState = rememberLazyListState(),
    lazyColumnConfig: LazyColumnConfig = LazyColumnConfig()
) {
    ComposeParallaxToolbarLayout(
        titleContent = titleContent,
        headerContent = headerContent,
        content = if (lazyContent != null) {
            ParallaxContent.Lazy(lazyContent, lazyColumnConfig, lazyListState)
        } else {
            ParallaxContent.Regular(content)
        },
        modifier = modifier,
        contentPadding = contentPadding,
        subtitleContent = subtitleContent,
        navigationIcon = navigationIcon,
        actions = actions,
        headerConfig = headerConfig,
        toolbarConfig = toolbarConfig,
        titleConfig = titleConfig,
        bodyConfig = bodyConfig,
        state = rememberParallaxToolbarState(scrollState = scroll, lazyListState = lazyListState)
    )
}

/** Resolved body: which scroll source drives the collapse and how to render the content. */
private sealed class ParallaxBodySpec {
    class Regular(
        val content: @Composable ParallaxToolbarScope.(Boolean) -> Unit
    ) : ParallaxBodySpec()

    class Lazy(
        val lazyListState: LazyListState,
        val config: LazyColumnConfig,
        val content: LazyListScope.(Boolean) -> Unit
    ) : ParallaxBodySpec()

    class Custom(
        val content: @Composable ParallaxToolbarScope.(Boolean) -> Unit
    ) : ParallaxBodySpec()
}

@Composable
private fun ParallaxToolbarLayoutImpl(
    titleContent: @Composable ParallaxToolbarScope.(collapsed: Boolean) -> Unit,
    headerContent: @Composable ParallaxToolbarScope.() -> Unit,
    modifier: Modifier,
    contentPadding: PaddingValues,
    subtitleContent: (@Composable ParallaxToolbarScope.(Boolean) -> Unit)?,
    navigationIcon: (@Composable ParallaxToolbarScope.(Boolean) -> Unit)?,
    actions: (@Composable ParallaxActionsScope.(Boolean) -> Unit)?,
    headerConfig: ParallaxHeaderConfig,
    toolbarConfig: ParallaxToolbarConfig,
    titleConfig: ParallaxTitleConfig,
    bodyConfig: ParallaxBodyConfig,
    state: ParallaxToolbarState,
    body: ParallaxBodySpec
) {
    val density = LocalDensity.current
    val topInset = with(density) { WindowInsets.statusBars.getTop(this).toDp() }
    val toolbarHeight = toolbarConfig.height
    val headerState = state.headerState
    val isCollapsed by remember(headerState) { derivedStateOf { headerState.isCollapsed } }
    val scope = remember(state) { ParallaxToolbarScopeImpl(state) }

    // The first composition honors the config; later ones keep the saved fraction.
    var appliedInitialState by rememberSaveable { mutableStateOf(false) }
    SideEffect {
        if (!appliedInitialState) {
            appliedInitialState = true
            if (!headerConfig.isExpandedWhenFirstDisplayed) headerState.snapFractionTo(1f)
        }
    }

    val connection = remember(headerState, headerConfig.snapOnRelease) {
        headerState.connection(snapOnRelease = headerConfig.snapOnRelease)
    }
    val headerFling = remember(headerState, headerConfig.snapOnRelease) {
        HeaderFlingBehavior(headerState, headerConfig.snapOnRelease)
    }

    BoxWithConstraints(modifier = modifier) {
        val headerHeight = headerConfig.height.resolve(
            availableWidth = maxWidth,
            availableHeight = maxHeight
        )
        val headerHeightPx = with(density) { headerHeight.toPx() }
        val toolbarHeightPx = with(density) { toolbarHeight.toPx() }
        val collapseRangePx = (headerHeightPx - toolbarHeightPx).coerceAtLeast(0f)
        SideEffect { headerState.collapseRangePx = collapseRangePx }

        Layout(
            modifier = Modifier
                .fillMaxSize()
                // Drags that start on the header area collapse it directly.
                .scrollable(
                    state = headerState,
                    orientation = Orientation.Vertical,
                    reverseDirection = true,
                    flingBehavior = headerFling
                )
                // Scrolls that start in the body reach the header through nested scroll.
                .nestedScroll(connection),
            content = {
                // The body and title are pushed down by the status bar inset so the collapsed
                // toolbar clears it; the header covers the inset too.
                ParallaxHeader(
                    headerState = headerState,
                    headerHeightPx = headerHeightPx,
                    parallaxMultiplier = headerConfig.parallaxMultiplier,
                    gradientBrush = headerConfig.gradient,
                    initialColor = toolbarConfig.initialColor,
                    targetColor = toolbarConfig.targetColor,
                    modifier = Modifier.layoutId(HeaderSlot).fillMaxWidth().height(headerHeight + topInset),
                    content = { scope.headerContent() }
                )

                val bodyModifier = Modifier.layoutId(BodySlot)
                when (body) {
                    is ParallaxBodySpec.Regular -> ParallaxBody(
                        scroll = state.scrollState,
                        contentPadding = contentPadding.withExtraBottom(bodyConfig.minBottomSpacerHeight),
                        modifier = bodyModifier,
                        content = { body.content(scope, isCollapsed) }
                    )

                    is ParallaxBodySpec.Lazy -> ParallaxLazyBody(
                        lazyListState = body.lazyListState,
                        config = body.config,
                        contentPadding = contentPadding.withExtraBottom(bodyConfig.minBottomSpacerHeight),
                        modifier = bodyModifier,
                        lazyContent = { body.content(this, isCollapsed) }
                    )

                    is ParallaxBodySpec.Custom -> Box(bodyModifier) { body.content(scope, isCollapsed) }
                }

                ParallaxTopBar(
                    headerState = headerState,
                    isCollapsed = isCollapsed,
                    topInset = topInset,
                    headerHeight = headerHeight,
                    toolbarConfig = toolbarConfig,
                    titleConfig = titleConfig,
                    scope = scope,
                    navigationIcon = navigationIcon,
                    actions = actions,
                    titleContent = titleContent,
                    subtitleContent = subtitleContent,
                    modifier = Modifier.layoutId(TopBarSlot)
                )
            }
        ) { measurables, constraints ->
            val width = constraints.maxWidth
            val height = constraints.maxHeight
            val insetPx = topInset.roundToPx()
            val toolbarPx = toolbarHeight.roundToPx()
            val headerPx = headerHeight.roundToPx()

            val header = measurables.first { it.layoutId == HeaderSlot }
                .measure(Constraints.fixedWidth(width))
            val topBar = measurables.first { it.layoutId == TopBarSlot }
                .measure(Constraints.fixedWidth(width))
            // The body gets the space below the collapsed toolbar. While the header is expanded it
            // is pushed down and its tail is off screen; it slides up as the header collapses.
            val bodyHeight = (height - insetPx - toolbarPx).coerceAtLeast(0)
            val bodyPlaceable = measurables.first { it.layoutId == BodySlot }
                .measure(Constraints.fixed(width, bodyHeight))

            layout(width, height) {
                header.placeRelative(0, 0)
                bodyPlaceable.placeRelativeWithLayer(0, insetPx + headerPx) {
                    translationY = -headerState.offsetPx
                }
                topBar.placeRelative(0, 0)
            }
        }
    }
}

private const val HeaderSlot = "header"
private const val BodySlot = "body"
private const val TopBarSlot = "topBar"

private fun PaddingValues.withExtraBottom(extra: Dp): PaddingValues =
    if (extra <= 0.dp) this else PaddingValues(
        start = calculateLeftPadding(LayoutDirection.Ltr),
        top = calculateTopPadding(),
        end = calculateRightPadding(LayoutDirection.Ltr),
        bottom = calculateBottomPadding() + extra
    )

/** Flings on the header itself decelerate the header frame by frame, then optionally settle it. */
private class HeaderFlingBehavior(
    private val headerState: HeaderScrollState,
    private val snapOnRelease: Boolean
) : FlingBehavior {
    private val decay = exponentialDecay<Float>(frictionMultiplier = 2f)

    override suspend fun ScrollScope.performFling(initialVelocity: Float): Float {
        var lastValue = 0f
        var remaining = initialVelocity
        AnimationState(initialValue = 0f, initialVelocity = initialVelocity).animateDecay(decay) {
            val delta = value - lastValue
            lastValue = value
            val consumed = scrollBy(delta)
            remaining = velocity
            // Stop once the header hits an end or stops moving.
            if (kotlin.math.abs(delta - consumed) > 0.5f || !headerState.canScrollForward && delta > 0f ||
                !headerState.canScrollBackward && delta < 0f
            ) {
                remaining = 0f
                cancelAnimation()
            }
        }
        if (snapOnRelease) headerState.settleIn(this, velocityPx = initialVelocity)
        return remaining
    }
}
