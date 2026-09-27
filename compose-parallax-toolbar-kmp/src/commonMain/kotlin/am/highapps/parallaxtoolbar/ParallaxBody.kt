package am.highapps.parallaxtoolbar

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.gestures.ScrollableDefaults
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection

/**
 * Regular body: a scrolling column. It is measured to the space below the collapsed toolbar, so
 * it needs no header spacer or filler; the header collapses through nested scroll before the
 * column consumes any scroll itself.
 */
@Composable
internal fun ParallaxBody(
    scroll: ScrollState,
    contentPadding: PaddingValues,
    modifier: Modifier,
    content: @Composable () -> Unit,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scroll)
            .padding(contentPadding),
    ) {
        content()
    }
}

/** Lazy body: a `LazyColumn` with the layout's content padding merged into its own. */
@Composable
internal fun ParallaxLazyBody(
    lazyListState: LazyListState,
    config: LazyColumnConfig,
    contentPadding: PaddingValues,
    modifier: Modifier,
    lazyContent: LazyListScope.() -> Unit,
) {
    val layoutDirection = LocalLayoutDirection.current
    val mergedContentPadding = PaddingValues(
        start = config.contentPadding.calculateStartPadding(layoutDirection) +
            contentPadding.calculateStartPadding(layoutDirection),
        top = config.contentPadding.calculateTopPadding() +
            contentPadding.calculateTopPadding(),
        end = config.contentPadding.calculateEndPadding(layoutDirection) +
            contentPadding.calculateEndPadding(layoutDirection),
        bottom = config.contentPadding.calculateBottomPadding() +
            contentPadding.calculateBottomPadding(),
    )

    LazyColumn(
        state = lazyListState,
        modifier = modifier.fillMaxSize(),
        contentPadding = mergedContentPadding,
        verticalArrangement = config.verticalArrangement,
        horizontalAlignment = config.horizontalAlignment,
        flingBehavior = config.flingBehavior ?: ScrollableDefaults.flingBehavior(),
        userScrollEnabled = config.userScrollEnabled,
        overscrollEffect = config.overscrollEffect,
        content = lazyContent,
    )
}
