package am.highapps.parallaxtoolbar

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.runtime.Stable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.layout
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import kotlin.math.roundToInt

/**
 * Receiver of every content slot of [ComposeParallaxToolbarLayout]. Slots still receive the
 * collapsed flag as their parameter; this scope adds the continuous [collapseFraction] for
 * effects that should follow the scroll, and modifiers that give individual elements their own
 * collapse behavior.
 *
 * Reading [collapseFraction] in composition recomposes the slot on every scroll frame. For
 * per-frame visuals prefer the modifiers below, or read it inside `Modifier.graphicsLayer { }`
 * or `drawBehind { }`, which only re-run that block.
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

    /** Measured geometry of the layout; see [ParallaxToolbarLayoutInfo]. */
    public val layoutInfo: ParallaxToolbarLayoutInfo
        get() = state.layoutInfo

    /**
     * Moves the element up by [ratio] of the collapse distance as the header collapses. 0f keeps
     * it still, 1f moves it with the body. Pair with `headerConfig(parallaxMultiplier = 0f)` when
     * elements should each set their own ratio.
     */
    public fun Modifier.parallax(ratio: Float = ParallaxToolbarDefaults.HeaderParallaxMultiplier): Modifier =
        graphicsLayer { translationY = -layoutInfo.headerOffsetPx * ratio }

    /** Interpolates the element's alpha from [expandedAlpha] to [collapsedAlpha] with the collapse. */
    public fun Modifier.fadeOnCollapse(expandedAlpha: Float = 1f, collapsedAlpha: Float = 0f): Modifier =
        graphicsLayer { alpha = expandedAlpha + (collapsedAlpha - expandedAlpha) * collapseFraction }

    /** Scales the element toward [collapsedScale] about [origin] as the header collapses. */
    public fun Modifier.scaleOnCollapse(
        collapsedScale: Float,
        origin: TransformOrigin = TransformOrigin.Center
    ): Modifier = graphicsLayer {
        val scale = 1f + (collapsedScale - 1f) * collapseFraction
        transformOrigin = origin
        scaleX = scale
        scaleY = scale
    }

    /**
     * Glides the element from its place in the expanded header to its place in the toolbar,
     * scaling toward [collapsedScale] on the way. Meant for the `overlayContent` slot, whose
     * elements sit above the body and the toolbar, so the element stays visible once collapsed.
     *
     * [expanded] aligns the element within the header area (the status bar inset plus the header
     * height) inset by [expandedPadding]; [collapsed] aligns it within the toolbar area inset by
     * [collapsedPadding]. Both alignments honor the layout direction.
     */
    public fun Modifier.moveBetween(
        expanded: Alignment,
        collapsed: Alignment,
        expandedPadding: PaddingValues = PaddingValues(0.dp),
        collapsedPadding: PaddingValues = PaddingValues(0.dp),
        collapsedScale: Float = 1f
    ): Modifier = layout { measurable, constraints ->
        // Measure with the constraints as given so an outer size modifier is honored.
        val placeable = measurable.measure(constraints)
        val info = layoutInfo
        val direction = layoutDirection
        val size = IntSize(placeable.width, placeable.height)
        val scaledSize = IntSize((placeable.width * collapsedScale).roundToInt(), (placeable.height * collapsedScale).roundToInt())

        // Header area: from the top of the layout to the header's bottom edge.
        val ePadStart = expandedPadding.calculateStartPadding(direction).roundToPx()
        val ePadEnd = expandedPadding.calculateEndPadding(direction).roundToPx()
        val ePadTop = expandedPadding.calculateTopPadding().roundToPx()
        val ePadBottom = expandedPadding.calculateBottomPadding().roundToPx()
        val headerArea = IntSize(
            (info.widthPx.roundToInt() - ePadStart - ePadEnd).coerceAtLeast(0),
            ((info.topInsetPx + info.headerHeightPx).roundToInt() - ePadTop - ePadBottom).coerceAtLeast(0)
        )
        val eOffset = expanded.align(size, headerArea, direction)
        val expandedX = (if (direction == androidx.compose.ui.unit.LayoutDirection.Rtl) ePadEnd else ePadStart) + eOffset.x
        val expandedY = ePadTop + eOffset.y

        // Toolbar area: below the inset, the toolbar height.
        val cPadStart = collapsedPadding.calculateStartPadding(direction).roundToPx()
        val cPadEnd = collapsedPadding.calculateEndPadding(direction).roundToPx()
        val cPadTop = collapsedPadding.calculateTopPadding().roundToPx()
        val cPadBottom = collapsedPadding.calculateBottomPadding().roundToPx()
        val toolbarArea = IntSize(
            (info.widthPx.roundToInt() - cPadStart - cPadEnd).coerceAtLeast(0),
            (info.toolbarHeightPx.roundToInt() - cPadTop - cPadBottom).coerceAtLeast(0)
        )
        val cOffset = collapsed.align(scaledSize, toolbarArea, direction)
        val collapsedX = (if (direction == androidx.compose.ui.unit.LayoutDirection.Rtl) cPadEnd else cPadStart) + cOffset.x
        val collapsedY = info.topInsetPx.roundToInt() + cPadTop + cOffset.y

        layout(placeable.width, placeable.height) {
            placeable.placeWithLayer(expandedX, expandedY) {
                val f = collapseFraction
                val scale = 1f + (collapsedScale - 1f) * f
                transformOrigin = TransformOrigin(0f, 0f)
                scaleX = scale
                scaleY = scale
                translationX = (collapsedX - expandedX) * f
                translationY = (collapsedY - expandedY) * f - layoutInfo.toolbarExitOffsetPx
            }
        }
    }
}

/** Receiver of the `actions` slot: a [RowScope] that also exposes the toolbar scope. */
@Stable
public interface ParallaxActionsScope : ParallaxToolbarScope, RowScope

internal class ParallaxToolbarScopeImpl(override val state: ParallaxToolbarState) : ParallaxToolbarScope

internal class ParallaxActionsScopeImpl(
    scope: ParallaxToolbarScope,
    row: RowScope
) : ParallaxActionsScope, ParallaxToolbarScope by scope, RowScope by row
