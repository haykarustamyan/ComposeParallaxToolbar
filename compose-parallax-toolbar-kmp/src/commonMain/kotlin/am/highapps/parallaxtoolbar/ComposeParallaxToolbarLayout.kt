package am.highapps.parallaxtoolbar

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
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
 * @param scrollState Scroll state used by [ParallaxContent.Regular]. Lazy content uses the
 *   state carried by [ParallaxContent.Lazy].
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
            body = ParallaxBodySpec.Regular(scrollState, content.content)
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
            body = ParallaxBodySpec.Lazy(
                lazyListState = content.lazyListState ?: rememberLazyListState(),
                config = content.config,
                content = content.content
            )
        )
    }
}

/**
 * Previous entry point. Prefer the overload taking [ParallaxContent].
 */
@Deprecated(
    message = "Use the overload that takes ParallaxContent.",
    replaceWith = ReplaceWith(
        "ComposeParallaxToolbarLayout(titleContent, headerContent, ParallaxContent.Regular(content), " +
                "modifier, contentPadding, subtitleContent, navigationIcon, actions, headerConfig, " +
                "toolbarConfig, titleConfig, bodyConfig, scroll)"
    )
)
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
        scrollState = scroll
    )
}

/** Resolved body: which scroll source drives the collapse and how to render the content. */
private sealed class ParallaxBodySpec {
    class Regular(
        val scrollState: ScrollState,
        val content: @Composable (Boolean) -> Unit
    ) : ParallaxBodySpec()

    class Lazy(
        val lazyListState: LazyListState,
        val config: LazyColumnConfig,
        val content: LazyListScope.(Boolean) -> Unit
    ) : ParallaxBodySpec()
}

@Composable
private fun ParallaxToolbarLayoutImpl(
    titleContent: @Composable (Boolean) -> Unit,
    headerContent: @Composable () -> Unit,
    modifier: Modifier,
    contentPadding: PaddingValues,
    subtitleContent: (@Composable (Boolean) -> Unit)?,
    navigationIcon: (@Composable (Boolean) -> Unit)?,
    actions: (@Composable RowScope.(Boolean) -> Unit)?,
    headerConfig: ParallaxHeaderConfig,
    toolbarConfig: ParallaxToolbarConfig,
    titleConfig: ParallaxTitleConfig,
    bodyConfig: ParallaxBodyConfig,
    body: ParallaxBodySpec
) {
    val density = LocalDensity.current
    val topInset = with(density) { WindowInsets.statusBars.getTop(this).toDp() }
    val toolbarHeight = ParallaxToolbarDefaults.ToolbarHeight

    BoxWithConstraints(modifier = modifier) {
        val headerHeight = headerConfig.height.resolve(
            availableWidth = maxWidth,
            availableHeight = maxHeight
        )
        val headerHeightPx = with(density) { headerHeight.toPx() }
        val toolbarHeightPx = with(density) { toolbarHeight.toPx() }
        val maxWidthPx = with(density) { maxWidth.toPx() }
        val collapseRangePx = headerHeightPx - toolbarHeightPx

        val collapseState = rememberCollapseState(
            scrollState = (body as? ParallaxBodySpec.Regular)?.scrollState ?: rememberScrollState(),
            lazyListState = (body as? ParallaxBodySpec.Lazy)?.lazyListState,
            collapseRangePx = collapseRangePx
        )
        val isCollapsed by remember(collapseState) { derivedStateOf { collapseState.isCollapsed } }

        LaunchedEffect(collapseState, headerConfig.isExpandedWhenFirstDisplayed) {
            if (!headerConfig.isExpandedWhenFirstDisplayed) collapseState.collapse()
        }

        var navIconWidthPx by remember { mutableStateOf(0f) }
        var actionsWidthPx by remember { mutableStateOf(0f) }

        // The body and title are pushed down by the status bar inset so the collapsed toolbar
        // clears it. The body is padded rather than offset so its scroll range shrinks with it
        // and the end of the content stays reachable. The header covers the inset too, or a gap
        // would appear under it.
        ParallaxHeader(
            collapseState = collapseState,
            headerHeightPx = headerHeightPx,
            gradientBrush = headerConfig.gradient,
            initialColor = toolbarConfig.initialColor,
            targetColor = toolbarConfig.targetColor,
            modifier = Modifier.fillMaxWidth().height(headerHeight + topInset),
            content = headerContent
        )

        when (body) {
            is ParallaxBodySpec.Regular -> ParallaxBody(
                scroll = body.scrollState,
                viewportHeight = maxHeight - topInset,
                headerHeight = headerHeight,
                toolbarHeight = toolbarHeight,
                minBottomSpacerHeight = bodyConfig.minBottomSpacerHeight,
                contentPadding = contentPadding,
                modifier = Modifier.padding(top = topInset),
                content = { body.content(isCollapsed) }
            )

            is ParallaxBodySpec.Lazy -> ParallaxLazyBody(
                lazyListState = body.lazyListState,
                headerHeight = headerHeight,
                minBottomSpacerHeight = bodyConfig.minBottomSpacerHeight,
                config = body.config,
                contentPadding = contentPadding,
                modifier = Modifier.padding(top = topInset),
                lazyContent = { body.content(this, isCollapsed) }
            )
        }

        ParallaxToolbar(
            collapseState = collapseState,
            initialColor = toolbarConfig.initialColor,
            targetColor = toolbarConfig.targetColor,
            colorAnimationSpec = toolbarConfig.animationSpec,
            elevation = toolbarConfig.elevation,
            navigationIcon = {
                Box(
                    modifier = Modifier
                        .onGloballyPositioned { navIconWidthPx = it.size.width.toFloat() }
                        .size(if (navigationIcon != null) Dp.Unspecified else 0.dp)
                ) {
                    navigationIcon?.invoke(isCollapsed)
                }
            },
            actions = {
                Row(
                    modifier = Modifier.onGloballyPositioned { actionsWidthPx = it.size.width.toFloat() }
                ) {
                    actions?.invoke(this, isCollapsed)
                }
            }
        )

        ParallaxTitle(
            collapseState = collapseState,
            headerHeightPx = headerHeightPx,
            toolbarHeightPx = toolbarHeightPx,
            hasNavigationIcon = navigationIcon != null,
            config = titleConfig,
            titleFontScaleStart = ParallaxToolbarDefaults.TitleFontScaleStart,
            titleFontScaleEnd = ParallaxToolbarDefaults.TitleFontScaleEnd,
            modifier = Modifier
                .offset(y = topInset)
                .widthIn(
                    min = 0.dp,
                    max = with(density) { (maxWidthPx - navIconWidthPx - actionsWidthPx).toDp() }
                ),
            titleContent = titleContent,
            subtitleContent = subtitleContent
        )
    }
}
