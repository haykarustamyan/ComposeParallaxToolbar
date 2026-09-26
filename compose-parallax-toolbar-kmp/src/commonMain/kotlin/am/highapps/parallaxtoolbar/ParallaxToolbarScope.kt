package am.highapps.parallaxtoolbar

import androidx.compose.foundation.layout.RowScope
import androidx.compose.runtime.Stable

/**
 * Receiver of every content slot of [ComposeParallaxToolbarLayout]. Slots still receive the
 * collapsed flag as their parameter; this scope adds the continuous [collapseFraction] for
 * effects that should follow the scroll, such as fading a header image or cross-fading titles.
 *
 * Reading [collapseFraction] in composition recomposes the slot on every scroll frame. For
 * per-frame visuals prefer reading it inside `Modifier.graphicsLayer { }` or `drawBehind { }`,
 * which only re-run that block.
 */
@Stable
public interface ParallaxToolbarScope {
    /** The layout's state, for [ParallaxToolbarState.collapse] and friends. */
    public val state: ParallaxToolbarState

    /** 0f while fully expanded, 1f once fully collapsed. */
    public val collapseFraction: Float
        get() = state.collapseFraction

    public val isCollapsed: Boolean
        get() = state.isCollapsed
}

/** Receiver of the `actions` slot: a [RowScope] that also exposes the toolbar scope. */
@Stable
public interface ParallaxActionsScope : ParallaxToolbarScope, RowScope

internal class ParallaxToolbarScopeImpl(override val state: ParallaxToolbarState) : ParallaxToolbarScope

internal class ParallaxActionsScopeImpl(
    scope: ParallaxToolbarScope,
    row: RowScope
) : ParallaxActionsScope, ParallaxToolbarScope by scope, RowScope by row
