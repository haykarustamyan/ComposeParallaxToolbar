package am.highapps.parallaxtoolbar

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable

/**
 * Hoisted state of a [ComposeParallaxToolbarLayout]: observe how far the toolbar has collapsed
 * and drive it programmatically.
 *
 * Create it with [rememberParallaxToolbarState]. The collapse fraction survives configuration
 * changes and process death. [scrollState] backs [ParallaxContent.Regular] and [lazyListState]
 * backs [ParallaxContent.Lazy] unless the content carries its own list state; custom content
 * manages its own scroll state.
 */
@Stable
public class ParallaxToolbarState internal constructor(
    public val scrollState: ScrollState,
    public val lazyListState: LazyListState,
    internal val headerState: HeaderScrollState
) {
    /** Measured geometry, updated on every layout pass. */
    public val layoutInfo: ParallaxToolbarLayoutInfo = ParallaxToolbarLayoutInfo(headerState)

    /** 0f while fully expanded, 1f once fully collapsed. */
    public val collapseFraction: Float
        get() = headerState.fraction

    public val isCollapsed: Boolean
        get() = headerState.isCollapsed

    /**
     * 0f while the toolbar is on screen, 1f once it has slid away. Only moves in
     * [ScrollMode.EnterAlwaysCollapsed]; other modes keep the toolbar pinned at 0f.
     */
    public val toolbarExitFraction: Float
        get() = headerState.exitFraction

    /** Current stretch past the expanded height, in px, while `stretchEnabled` and pulled down. */
    public val stretchPx: Float
        get() = headerState.stretchPx

    /**
     * True while the header is being dragged, flung, snapped or animated by [collapse] and
     * [expand]. False once it has come to rest.
     */
    public val isScrollInProgress: Boolean
        get() = headerState.isScrollInProgress

    /**
     * Collapses the header. The body keeps its own scroll position.
     *
     * @param animationSpec Spec for the move; `null` uses the header config's `animationSpec`.
     */
    public suspend fun collapse(animated: Boolean = true, animationSpec: AnimationSpec<Float>? = null) {
        if (animated) headerState.animateFractionTo(1f, animationSpec ?: headerState.animationSpec)
        else headerState.snapFractionTo(1f)
    }

    /**
     * Expands the header and brings an exited toolbar back. The body keeps its own scroll position.
     *
     * @param animationSpec Spec for the move; `null` uses the header config's `animationSpec`.
     */
    public suspend fun expand(animated: Boolean = true, animationSpec: AnimationSpec<Float>? = null) {
        if (animated) headerState.animateFractionTo(0f, animationSpec ?: headerState.animationSpec)
        else headerState.snapFractionTo(0f)
    }
}

/**
 * Creates and remembers a [ParallaxToolbarState]. Pass your own [scrollState] or [lazyListState]
 * to share scroll position with other components; otherwise fresh ones are remembered.
 *
 * @param initiallyCollapsed Whether the header starts collapsed the first time it is shown.
 *   Later restorations keep whatever fraction was saved.
 */
@Composable
public fun rememberParallaxToolbarState(
    scrollState: ScrollState = rememberScrollState(),
    lazyListState: LazyListState = rememberLazyListState(),
    initiallyCollapsed: Boolean = false
): ParallaxToolbarState {
    val headerState = rememberSaveable(saver = HeaderScrollStateSaver) {
        HeaderScrollState(if (initiallyCollapsed) 1f else 0f)
    }
    return remember(scrollState, lazyListState, headerState) {
        ParallaxToolbarState(scrollState, lazyListState, headerState)
    }
}

private val HeaderScrollStateSaver = androidx.compose.runtime.saveable.listSaver<HeaderScrollState, Float>(
    save = { listOf(it.fraction, it.exitFraction) },
    restore = { HeaderScrollState(it[0], it.getOrElse(1) { 0f }) }
)
