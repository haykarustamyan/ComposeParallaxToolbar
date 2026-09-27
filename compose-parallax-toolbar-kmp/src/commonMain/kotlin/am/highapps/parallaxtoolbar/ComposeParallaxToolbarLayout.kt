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
import kotlin.math.roundToInt
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.semantics.collapse
import androidx.compose.ui.semantics.expand
import androidx.compose.ui.semantics.hideFromAccessibility
import androidx.compose.ui.semantics.isTraversalGroup
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.semantics.traversalIndex
import kotlinx.coroutines.launch
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
 * @param overlayContent Optional layer drawn above the body and the toolbar, the size of the
 *   whole layout. Use it with [ParallaxToolbarScope.moveBetween] for elements that travel from
 *   the header into the toolbar, such as an avatar.
 * @param bottomContent Optional row pinned under the toolbar, such as tabs or a search field. It
 *   rides the header's bottom edge while expanded and stays below the toolbar once collapsed. Give
 *   it a background; the body scrolls underneath it.
 * @param onStretchTrigger Called when a stretch is released past
 *   [ParallaxHeaderConfig.stretchTriggerDistance]; requires `stretchEnabled`. Typical use is
 *   pull-to-refresh.
 * @param semanticsConfig Strings announced to accessibility services; see
 *   [ParallaxSemanticsConfig]. The layout exposes its state and expand/collapse actions, reads
 *   the toolbar before the header and body, marks the title as a heading, and hides the covered
 *   header or an exited toolbar from screen readers.
 * @param windowInsets Insets the toolbar and body are pushed inside of. The top inset sits above
 *   the toolbar and under the header; the horizontal insets keep the navigation icon, actions and
 *   title clear of display cutouts. Defaults to the status bar plus the cutout, the same as
 *   Material's top app bar. Pass `WindowInsets(0)` when the layout does not touch the window edge,
 *   for example inside a dialog, a bottom sheet or a split pane.
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
    overlayContent: (@Composable ParallaxToolbarScope.() -> Unit)? = null,
    bottomContent: (@Composable ParallaxToolbarScope.() -> Unit)? = null,
    onStretchTrigger: (() -> Unit)? = null,
    headerConfig: ParallaxHeaderConfig = ParallaxToolbarDefaults.headerConfig(),
    toolbarConfig: ParallaxToolbarConfig = ParallaxToolbarDefaults.toolbarConfig(),
    titleConfig: ParallaxTitleConfig = ParallaxToolbarDefaults.titleConfig(),
    bodyConfig: ParallaxBodyConfig = ParallaxToolbarDefaults.bodyConfig(),
    semanticsConfig: ParallaxSemanticsConfig = ParallaxToolbarDefaults.semanticsConfig(),
    windowInsets: WindowInsets = ParallaxToolbarDefaults.windowInsets,
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
            overlayContent = overlayContent,
            bottomContent = bottomContent,
            onStretchTrigger = onStretchTrigger,
            headerConfig = headerConfig,
            toolbarConfig = toolbarConfig,
            titleConfig = titleConfig,
            bodyConfig = bodyConfig,
            semanticsConfig = semanticsConfig,
            windowInsets = windowInsets,
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
            overlayContent = overlayContent,
            bottomContent = bottomContent,
            onStretchTrigger = onStretchTrigger,
            headerConfig = headerConfig,
            toolbarConfig = toolbarConfig,
            titleConfig = titleConfig,
            bodyConfig = bodyConfig,
            semanticsConfig = semanticsConfig,
            windowInsets = windowInsets,
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
            overlayContent = overlayContent,
            bottomContent = bottomContent,
            onStretchTrigger = onStretchTrigger,
            headerConfig = headerConfig,
            toolbarConfig = toolbarConfig,
            titleConfig = titleConfig,
            bodyConfig = bodyConfig,
            semanticsConfig = semanticsConfig,
            windowInsets = windowInsets,
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
    overlayContent: (@Composable ParallaxToolbarScope.() -> Unit)? = null,
    bottomContent: (@Composable ParallaxToolbarScope.() -> Unit)? = null,
    onStretchTrigger: (() -> Unit)? = null,
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
        overlayContent = null,
        bottomContent = null,
        onStretchTrigger = null,
        headerConfig = headerConfig,
        toolbarConfig = toolbarConfig,
        titleConfig = titleConfig,
        bodyConfig = bodyConfig,
        semanticsConfig = ParallaxToolbarDefaults.semanticsConfig(),
        windowInsets = ParallaxToolbarDefaults.windowInsets,
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
    overlayContent: (@Composable ParallaxToolbarScope.() -> Unit)?,
    bottomContent: (@Composable ParallaxToolbarScope.() -> Unit)?,
    onStretchTrigger: (() -> Unit)?,
    headerConfig: ParallaxHeaderConfig,
    toolbarConfig: ParallaxToolbarConfig,
    titleConfig: ParallaxTitleConfig,
    bodyConfig: ParallaxBodyConfig,
    semanticsConfig: ParallaxSemanticsConfig,
    windowInsets: WindowInsets,
    state: ParallaxToolbarState,
    body: ParallaxBodySpec
) {
    val semanticsScope = rememberCoroutineScope()
    val density = LocalDensity.current
    val topInset = with(density) { windowInsets.getTop(this).toDp() }
    val leftInset = with(density) { windowInsets.getLeft(this, LocalLayoutDirection.current).toDp() }
    val rightInset = with(density) { windowInsets.getRight(this, LocalLayoutDirection.current).toDp() }
    val toolbarHeight = toolbarConfig.height
    val headerState = state.headerState
    val isCollapsed by remember(headerState) { derivedStateOf { headerState.isCollapsed } }
    val isToolbarExited by remember(headerState) { derivedStateOf { headerState.exitFraction >= 1f } }
    val scope = remember(state) { ParallaxToolbarScopeImpl(state) }

    // The first composition honors the config; later ones keep the saved fraction.
    var appliedInitialState by rememberSaveable { mutableStateOf(false) }
    SideEffect {
        if (!appliedInitialState) {
            appliedInitialState = true
            if (!headerConfig.isExpandedWhenFirstDisplayed) headerState.snapFractionTo(1f)
        }
    }

    val connection = remember(headerState, headerConfig.scrollMode, headerConfig.snapOnRelease) {
        headerState.connection(mode = headerConfig.scrollMode, snapOnRelease = headerConfig.snapOnRelease)
    }
    val headerFling = remember(headerState, headerConfig.snapOnRelease) {
        HeaderFlingBehavior(headerState, headerConfig.snapOnRelease)
    }

    BoxWithConstraints(
        modifier = modifier
            // Stretch follows a held pointer only. Watched in the initial pass and never consumed,
            // so it sees every press and release under the layout before the slots do.
            .pointerInput(headerState) {
                awaitPointerEventScope {
                    while (true) {
                        val event = awaitPointerEvent(PointerEventPass.Initial)
                        headerState.isPointerPressed = event.changes.any { it.pressed }
                    }
                }
            }
            // Screen readers get the state and a way to toggle it on the root node; the slots
            // declare their reading order so the toolbar comes first.
            .semantics {
                isTraversalGroup = true
                stateDescription = if (isCollapsed) semanticsConfig.collapsedStateDescription
                else semanticsConfig.expandedStateDescription
                if (isCollapsed) {
                    expand(semanticsConfig.expandActionLabel) {
                        semanticsScope.launch { state.expand() }
                        true
                    }
                } else {
                    collapse(semanticsConfig.collapseActionLabel) {
                        semanticsScope.launch { state.collapse() }
                        true
                    }
                }
            }
    ) {
        require(constraints.hasBoundedHeight) {
            "ComposeParallaxToolbarLayout needs a bounded height: it fills the space it is given " +
                    "and scrolls its body inside it. Give it a fixed height or a weight, or take it out " +
                    "of the vertically scrolling parent."
        }
        val headerHeight = headerConfig.height.resolve(
            availableWidth = maxWidth,
            availableHeight = maxHeight
        )
        val headerHeightPx = with(density) { headerHeight.toPx() }
        val toolbarHeightPx = with(density) { toolbarHeight.toPx() }
        val collapseRangePx = (headerHeightPx - toolbarHeightPx).coerceAtLeast(0f)
        val insetPxF = with(density) { topInset.toPx() }
        // Only EnterAlwaysCollapsed lets the toolbar leave; it travels its own height plus the inset.
        val exitRangePx = if (headerConfig.scrollMode == ScrollMode.EnterAlwaysCollapsed) toolbarHeightPx + insetPxF else 0f
        val stretchTriggerPx = with(density) { headerConfig.stretchTriggerDistance.toPx() }
        SideEffect {
            headerState.collapseRangePx = collapseRangePx
            headerState.exitRangePx = exitRangePx
            headerState.stretchMaxPx = if (headerConfig.stretchEnabled) stretchTriggerPx * ParallaxToolbarDefaults.StretchMaxFactor else 0f
            headerState.stretchTriggerPx = stretchTriggerPx
            headerState.onStretchTrigger = onStretchTrigger
            headerState.animationSpec = headerConfig.animationSpec
        }

        Layout(
            modifier = Modifier
                .fillMaxSize()
                // Scrolls that start in the body reach the header through nested scroll. The
                // scrollable for drags on the header sits on the header itself, not here: a
                // scrollable ancestor would also swallow whatever the body leaves unconsumed and
                // bypass the mode rules in the connection.
                .nestedScroll(connection),
            content = {
                // The body and title are pushed down by the status bar inset so the collapsed
                // toolbar clears it; the header covers the inset too.
                ParallaxHeader(
                    headerState = headerState,
                    headerHeightPx = headerHeightPx,
                    parallaxMultiplier = headerConfig.parallaxMultiplier,
                    fadeOnCollapse = headerConfig.fadeOnCollapse,
                    gradientBrush = headerConfig.gradient,
                    initialColor = toolbarConfig.initialColor,
                    targetColor = toolbarConfig.targetColor,
                    modifier = Modifier
                        .layoutId(HeaderSlot)
                        .fillMaxWidth()
                        .height(headerHeight + topInset)
                        .semantics {
                            traversalIndex = 1f
                            // Covered by the body and the toolbar: nothing there should be announced.
                            if (isCollapsed) hideFromAccessibility()
                        }
                        // Drags that start on the header collapse it directly.
                        .scrollable(
                            state = headerState,
                            orientation = Orientation.Vertical,
                            reverseDirection = true,
                            flingBehavior = headerFling
                        ),
                    content = { scope.headerContent() }
                )

                val bodyModifier = Modifier.layoutId(BodySlot).semantics { traversalIndex = 4f }
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
                    leftInset = leftInset,
                    rightInset = rightInset,
                    headerHeight = headerHeight,
                    toolbarConfig = toolbarConfig,
                    titleConfig = titleConfig,
                    scope = scope,
                    navigationIcon = navigationIcon,
                    actions = actions,
                    titleContent = titleContent,
                    subtitleContent = subtitleContent,
                    modifier = Modifier
                        .layoutId(TopBarSlot)
                        .semantics {
                            traversalIndex = 0f
                            if (isToolbarExited) hideFromAccessibility()
                        }
                )

                if (bottomContent != null) {
                    Box(Modifier.layoutId(BottomSlot).semantics { traversalIndex = 2f }) { scope.bottomContent() }
                }
                if (overlayContent != null) {
                    Box(Modifier.layoutId(OverlaySlot).semantics { traversalIndex = 3f }) { scope.overlayContent() }
                }
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
            val overlay = measurables.firstOrNull { it.layoutId == OverlaySlot }
                ?.measure(Constraints.fixed(width, height))
            val bottom = measurables.firstOrNull { it.layoutId == BottomSlot }
                ?.measure(Constraints.fixedWidth(width))
            val bottomPx = bottom?.height ?: 0

            // Publish the geometry before placement so overlay elements can position themselves.
            val info = state.layoutInfo
            info.widthPx = width.toFloat()
            info.heightPx = height.toFloat()
            info.topInsetPx = insetPx.toFloat()
            info.headerHeightPx = headerPx.toFloat()
            info.toolbarHeightPx = toolbarPx.toFloat()
            info.bottomHeightPx = bottomPx.toFloat()
            info.isMeasured = true
            // The body gets the space below the collapsed toolbar, plus whatever the toolbar can
            // vacate by exiting. While the header is expanded it is pushed down and its tail is off
            // screen; it slides up as the header collapses and the toolbar exits.
            val exitPx = exitRangePx.roundToInt()
            val bodyHeight = (height - insetPx - toolbarPx - bottomPx + exitPx).coerceAtLeast(0)
            val bodyPlaceable = measurables.first { it.layoutId == BodySlot }
                .measure(Constraints.fixed(width, bodyHeight))

            layout(width, height) {
                header.placeRelativeWithLayer(0, 0) {
                    translationY = -headerState.exitOffsetPx
                }
                // The body and the bottom slot ride the header's bottom edge, including a stretch.
                bodyPlaceable.placeRelativeWithLayer(0, insetPx + headerPx + bottomPx) {
                    translationY = -headerState.offsetPx - headerState.exitOffsetPx + headerState.stretchPx
                }
                bottom?.placeRelativeWithLayer(0, insetPx + headerPx) {
                    translationY = -headerState.offsetPx - headerState.exitOffsetPx + headerState.stretchPx
                }
                topBar.placeRelativeWithLayer(0, 0) {
                    translationY = -headerState.exitOffsetPx
                }
                overlay?.placeRelative(0, 0)
            }
        }
    }
}

private const val HeaderSlot = "header"
private const val BodySlot = "body"
private const val TopBarSlot = "topBar"
private const val OverlaySlot = "overlay"
private const val BottomSlot = "bottom"

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
            // Stop before applying a delta the header cannot use: a fling never stretches, so
            // once fully expanded a downward fling has nothing left to do.
            val expandedFully = headerState.fraction <= 0f && headerState.exitFraction <= 0f
            if (delta < 0f && expandedFully || delta > 0f && !headerState.canScrollForward) {
                remaining = 0f
                cancelAnimation()
                return@animateDecay
            }
            val consumed = scrollBy(delta)
            remaining = velocity
            // Stop once the header hits an end or stops moving.
            if (kotlin.math.abs(delta - consumed) > 0.5f || delta > 0f && !headerState.canScrollForward) {
                remaining = 0f
                cancelAnimation()
            }
        }
        headerState.releaseStretchIn()
        if (snapOnRelease) headerState.settleIn(this, velocityPx = initialVelocity)
        return remaining
    }
}
