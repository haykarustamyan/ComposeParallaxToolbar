package am.highapps.parallaxtoolbar

import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.layout.LayoutCoordinates

/**
 * Measured geometry of a [ComposeParallaxToolbarLayout], in pixels, updated on every layout pass.
 * All values are 0 until the layout has been measured once; check [isMeasured].
 *
 * Every property is snapshot state, so reading one inside a `graphicsLayer` or placement block
 * follows the scroll without recomposing.
 */
@Stable
public class ParallaxToolbarLayoutInfo internal constructor(private val headerState: HeaderScrollState) {
    public var isMeasured: Boolean by mutableStateOf(false)
        internal set

    /** Width of the layout. */
    public var widthPx: Float by mutableFloatStateOf(0f)
        internal set

    /** Height of the layout. */
    public var heightPx: Float by mutableFloatStateOf(0f)
        internal set

    /** Status bar inset the toolbar and body are pushed down by. */
    public var topInsetPx: Float by mutableFloatStateOf(0f)
        internal set

    /** Expanded header height, excluding [topInsetPx]. */
    public var headerHeightPx: Float by mutableFloatStateOf(0f)
        internal set

    /** Pinned toolbar height, excluding [topInsetPx]. */
    public var toolbarHeightPx: Float by mutableFloatStateOf(0f)
        internal set

    /** Scroll distance between fully expanded and fully collapsed. */
    public val collapseRangePx: Float
        get() = (headerHeightPx - toolbarHeightPx).coerceAtLeast(0f)

    /** How far the header has collapsed right now, in px. */
    public val headerOffsetPx: Float
        get() = headerState.offsetPx

    /** How far the toolbar has slid off screen right now, in px. */
    public val toolbarExitOffsetPx: Float
        get() = headerState.exitOffsetPx

    /** Current stretch past the expanded height, in px. */
    public val stretchPx: Float
        get() = headerState.stretchPx

    /** Coordinates of the header slot, inside its parallax layer, for [ParallaxToolbarScope.pin]. */
    internal var headerCoordinates: LayoutCoordinates? by mutableStateOf(null)

    /** Height of the pinned `bottomContent` slot, 0 when absent. */
    public var bottomHeightPx: Float by mutableFloatStateOf(0f)
        internal set

    /** Current bottom edge of the header, measured from the top of the layout. */
    public val currentHeaderBottomPx: Float
        get() = topInsetPx + headerHeightPx - headerOffsetPx - toolbarExitOffsetPx + stretchPx
}
