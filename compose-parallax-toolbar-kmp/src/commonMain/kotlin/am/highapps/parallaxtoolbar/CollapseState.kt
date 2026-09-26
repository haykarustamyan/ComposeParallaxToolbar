package am.highapps.parallaxtoolbar

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember

/**
 * Single source of truth for how far the header has collapsed, regardless of whether the body
 * scrolls through a [ScrollState] or a [LazyListState]. Every property reads the underlying
 * snapshot state directly, so it can be read from composition, from `derivedStateOf`, or from a
 * layer block and will observe scroll changes in each.
 */
@Stable
internal class CollapseState private constructor(
    private val scrollState: ScrollState?,
    private val lazyListState: LazyListState?,
    /** Scroll distance, in px, between fully expanded and fully collapsed. */
    val collapseRangePx: Float
) {
    /** Scroll distance consumed toward collapsing, in px. Not clamped. */
    val offsetPx: Float
        get() = when {
            lazyListState != null -> {
                // Item 0 is the header spacer. Once it is fully scrolled away, the layout is past
                // the collapse range whatever the offset inside the next item is.
                if (lazyListState.firstVisibleItemIndex == 0) {
                    lazyListState.firstVisibleItemScrollOffset.toFloat()
                } else {
                    collapseRangePx + lazyListState.firstVisibleItemScrollOffset
                }
            }
            else -> scrollState!!.value.toFloat()
        }

    /** 0f when expanded, 1f when collapsed. */
    val fraction: Float
        get() = if (collapseRangePx <= 0f) 1f else (offsetPx / collapseRangePx).coerceIn(0f, 1f)

    val isCollapsed: Boolean
        get() = offsetPx >= collapseRangePx

    suspend fun collapse(animated: Boolean) {
        if (lazyListState != null) {
            if (animated) lazyListState.animateScrollToItem(1) else lazyListState.scrollToItem(1)
        } else {
            val target = collapseRangePx.toInt() + 1
            if (animated) scrollState!!.animateScrollTo(target) else scrollState!!.scrollTo(target)
        }
    }

    suspend fun expand(animated: Boolean) {
        if (lazyListState != null) {
            if (animated) lazyListState.animateScrollToItem(0) else lazyListState.scrollToItem(0)
        } else {
            if (animated) scrollState!!.animateScrollTo(0) else scrollState!!.scrollTo(0)
        }
    }

    companion object {
        fun regular(scrollState: ScrollState, collapseRangePx: Float) =
            CollapseState(scrollState, null, collapseRangePx)

        fun lazy(lazyListState: LazyListState, collapseRangePx: Float) =
            CollapseState(null, lazyListState, collapseRangePx)
    }
}

@Composable
internal fun rememberCollapseState(
    scrollState: ScrollState,
    lazyListState: LazyListState?,
    collapseRangePx: Float
): CollapseState = remember(scrollState, lazyListState, collapseRangePx) {
    if (lazyListState != null) {
        CollapseState.lazy(lazyListState, collapseRangePx)
    } else {
        CollapseState.regular(scrollState, collapseRangePx)
    }
}
