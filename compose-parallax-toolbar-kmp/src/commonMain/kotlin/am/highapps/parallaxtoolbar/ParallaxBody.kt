package am.highapps.parallaxtoolbar

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.gestures.ScrollableDefaults
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Regular body: a header-sized gap, the content, then a filler so short content can still scroll
 * far enough to collapse the toolbar. Measured in a single pass: the filler is derived from the
 * content's measured height inside the same layout.
 *
 * @param viewportHeight Height available to the scrolling column, i.e. the layout height minus
 *   the status bar inset it is padded by.
 */
@Composable
internal fun ParallaxBody(
    scroll: ScrollState,
    viewportHeight: Dp,
    headerHeight: Dp,
    toolbarHeight: Dp,
    minBottomSpacerHeight: Dp,
    contentPadding: PaddingValues,
    modifier: Modifier,
    content: @Composable () -> Unit
) {
    val layoutDirection = LocalLayoutDirection.current

    Layout(
        modifier = modifier.fillMaxSize().verticalScroll(scroll),
        content = {
            Box(
                Modifier
                    .fillMaxWidth()
                    .padding(
                        start = contentPadding.calculateStartPadding(layoutDirection),
                        end = contentPadding.calculateEndPadding(layoutDirection)
                    )
            ) {
                content()
            }
        }
    ) { measurables, constraints ->
        val width = constraints.maxWidth
        val contentPlaceable = measurables.first().measure(
            Constraints(minWidth = width, maxWidth = width)
        )
        val headerPx = headerHeight.roundToPx()
        val bottomPaddingPx = contentPadding.calculateBottomPadding().roundToPx()

        // The column must be able to scroll by (header - toolbar) for the toolbar to collapse.
        // Its scroll range is (header + content + filler + bottom padding) - viewport.
        val filler = (viewportHeight.roundToPx() - toolbarHeight.roundToPx() -
                contentPlaceable.height - bottomPaddingPx)
            .coerceAtLeast(minBottomSpacerHeight.roundToPx())
            .coerceAtLeast(0)

        layout(width, headerPx + contentPlaceable.height + filler + bottomPaddingPx) {
            contentPlaceable.placeRelative(0, headerPx)
        }
    }
}

/** Lazy body: a header-sized spacer item, the user's items, then a bottom spacer item. */
@Composable
internal fun ParallaxLazyBody(
    lazyListState: LazyListState,
    headerHeight: Dp,
    minBottomSpacerHeight: Dp,
    config: LazyColumnConfig,
    contentPadding: PaddingValues,
    modifier: Modifier,
    lazyContent: LazyListScope.() -> Unit
) {
    // Merge external contentPadding with LazyColumn's own contentPadding
    val layoutDirection = LocalLayoutDirection.current
    val mergedContentPadding = PaddingValues(
        start = config.contentPadding.calculateStartPadding(layoutDirection) +
                contentPadding.calculateStartPadding(layoutDirection),
        top = config.contentPadding.calculateTopPadding() +
                contentPadding.calculateTopPadding(),
        end = config.contentPadding.calculateEndPadding(layoutDirection) +
                contentPadding.calculateEndPadding(layoutDirection),
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
            Spacer(Modifier.height(minBottomSpacerHeight.coerceAtLeast(0.dp)))
        }
    }
}
