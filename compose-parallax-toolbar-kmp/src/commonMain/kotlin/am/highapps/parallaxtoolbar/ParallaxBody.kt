package am.highapps.parallaxtoolbar

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.gestures.ScrollableDefaults
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp

/**
 * Regular body: a header-sized spacer, the content, then a filler so short content can still
 * scroll far enough to collapse the toolbar.
 */
@Composable
internal fun ParallaxBody(
    scroll: ScrollState,
    screenHeight: Dp,
    headerHeight: Dp,
    toolbarHeight: Dp,
    minBottomSpacerHeight: Dp,
    contentPadding: PaddingValues,
    modifier: Modifier,
    content: @Composable () -> Unit
) {
    var contentHeight by remember { mutableStateOf(0) }

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
            content()
        }

        val contentHeightDp = with(LocalDensity.current) { contentHeight.toDp() }
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
    val mergedContentPadding = PaddingValues(
        start = config.contentPadding.calculateStartPadding(LayoutDirection.Ltr) +
                contentPadding.calculateStartPadding(LayoutDirection.Ltr),
        top = config.contentPadding.calculateTopPadding() +
                contentPadding.calculateTopPadding(),
        end = config.contentPadding.calculateEndPadding(LayoutDirection.Ltr) +
                contentPadding.calculateEndPadding(LayoutDirection.Ltr),
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
