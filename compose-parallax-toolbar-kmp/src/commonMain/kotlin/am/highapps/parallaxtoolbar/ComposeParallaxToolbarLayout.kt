package am.highapps.parallaxtoolbar

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
                "toolbarConfig, titleConfig, bodyConfig, rememberParallaxToolbarState(scroll, lazyListState))"
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
        state = rememberParallaxToolbarState(scrollState = scroll, lazyListState = lazyListState)
    )
}

/** Resolved body: which scroll source drives the collapse and how to render the content. */
private sealed class ParallaxBodySpec {
    class Regular(
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
    state: ParallaxToolbarState,
    body: ParallaxBodySpec
) {
    val density = LocalDensity.current
    val topInset = with(density) { WindowInsets.statusBars.getTop(this).toDp() }
    val toolbarHeight = toolbarConfig.height

    BoxWithConstraints(modifier = modifier) {
        val headerHeight = headerConfig.height.resolve(
            availableWidth = maxWidth,
            availableHeight = maxHeight
        )
        val headerHeightPx = with(density) { headerHeight.toPx() }
        val toolbarHeightPx = with(density) { toolbarHeight.toPx() }
        val collapseRangePx = headerHeightPx - toolbarHeightPx

        val collapseState = rememberCollapseState(
            scrollState = state.scrollState,
            lazyListState = (body as? ParallaxBodySpec.Lazy)?.lazyListState,
            collapseRangePx = collapseRangePx
        )
        val isCollapsed by remember(collapseState) { derivedStateOf { collapseState.isCollapsed } }

        SideEffect { state.collapseState = collapseState }

        LaunchedEffect(collapseState, headerConfig.isExpandedWhenFirstDisplayed) {
            if (!headerConfig.isExpandedWhenFirstDisplayed) collapseState.collapse(animated = false)
        }

        // The body and title are pushed down by the status bar inset so the collapsed toolbar
        // clears it. The body is padded rather than offset so its scroll range shrinks with it
        // and the end of the content stays reachable. The header covers the inset too, or a gap
        // would appear under it.
        ParallaxHeader(
            collapseState = collapseState,
            headerHeightPx = headerHeightPx,
            parallaxMultiplier = headerConfig.parallaxMultiplier,
            gradientBrush = headerConfig.gradient,
            initialColor = toolbarConfig.initialColor,
            targetColor = toolbarConfig.targetColor,
            modifier = Modifier.fillMaxWidth().height(headerHeight + topInset),
            content = headerContent
        )

        when (body) {
            is ParallaxBodySpec.Regular -> ParallaxBody(
                scroll = state.scrollState,
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

        ParallaxTopBar(
            collapseState = collapseState,
            isCollapsed = isCollapsed,
            topInset = topInset,
            headerHeight = headerHeight,
            toolbarConfig = toolbarConfig,
            titleConfig = titleConfig,
            navigationIcon = navigationIcon,
            actions = actions,
            titleContent = titleContent,
            subtitleContent = subtitleContent
        )
    }
}
