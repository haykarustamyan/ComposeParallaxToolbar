package am.highapps.parallaxtoolbar

import androidx.compose.runtime.Immutable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.isSpecified

/**
 * How the expanded header height is determined.
 */
@Immutable
public sealed class HeaderHeight {
    /** Fixed height in Dp. */
    public data class Fixed(val height: Dp) : HeaderHeight()

    /**
     * Height derived from the available width and a width/height ratio, e.g. `16f / 9f`, capped
     * at [maxHeight] when specified so wide layouts do not produce a towering header.
     */
    public data class AspectRatio(val ratio: Float, val maxHeight: Dp = Dp.Unspecified) : HeaderHeight()

    /**
     * Height as a fraction of the available height, between 0f and 1f (e.g. 0.4f for 40%),
     * capped at [maxHeight] when specified so tablets and landscape do not produce a towering header.
     */
    public data class Percentage(val percentage: Float, val maxHeight: Dp = Dp.Unspecified) : HeaderHeight()

    /** Resolves this specification against the space the layout was given. */
    internal fun resolve(availableWidth: Dp, availableHeight: Dp): Dp = when (this) {
        is Fixed -> height
        is AspectRatio -> (availableWidth / ratio).capped(maxHeight)
        is Percentage -> (availableHeight * percentage).capped(maxHeight)
    }

    private fun Dp.capped(max: Dp): Dp = if (max.isSpecified && this > max) max else this
}
