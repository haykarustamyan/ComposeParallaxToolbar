package am.highapps.parallaxtoolbar

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

/**
 * Hoisted state of a [ComposeParallaxToolbarLayout]: observe how far the toolbar has collapsed
 * and drive it programmatically.
 *
 * Create it with [rememberParallaxToolbarState]. [scrollState] backs [ParallaxContent.Regular]
 * and [lazyListState] backs [ParallaxContent.Lazy] unless the content carries its own list state.
 */
@Stable
public class ParallaxToolbarState internal constructor(
    public val scrollState: ScrollState,
    public val lazyListState: LazyListState
) {
    internal var collapseState: CollapseState? by mutableStateOf(null)

    /** 0f while fully expanded, 1f once fully collapsed. */
    public val collapseFraction: Float
        get() = collapseState?.fraction ?: 0f

    public val isCollapsed: Boolean
        get() = collapseState?.isCollapsed ?: false

    /** Scrolls the body until the toolbar is collapsed. No-op before the layout is first measured. */
    public suspend fun collapse(animated: Boolean = true) {
        collapseState?.collapse(animated)
    }

    /** Scrolls the body back to the top so the header is fully expanded. */
    public suspend fun expand(animated: Boolean = true) {
        collapseState?.expand(animated)
    }
}

/**
 * Creates and remembers a [ParallaxToolbarState]. Pass your own [scrollState] or [lazyListState]
 * to share scroll position with other components; otherwise fresh ones are remembered.
 */
@Composable
public fun rememberParallaxToolbarState(
    scrollState: ScrollState = rememberScrollState(),
    lazyListState: LazyListState = rememberLazyListState()
): ParallaxToolbarState = remember(scrollState, lazyListState) {
    ParallaxToolbarState(scrollState, lazyListState)
}
