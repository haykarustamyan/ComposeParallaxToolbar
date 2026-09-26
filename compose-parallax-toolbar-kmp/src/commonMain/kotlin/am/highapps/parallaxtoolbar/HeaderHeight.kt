package am.highapps.parallaxtoolbar

import androidx.compose.runtime.Immutable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.isSpecified

/**
 * How the expanded header height is determined.
 */
@Immutable
public sealed class HeaderHeight {
    /** Fixed height in Dp. */
    public data class Fixed(val height: Dp) : HeaderHeight() {
        init {
            require(height.isSpecified && height >= 0.dp) { "HeaderHeight.Fixed height must be 0.dp or more, was $height" }
        }
    }

    /**
     * Height derived from the available width and a width/height ratio, e.g. `16f / 9f`, capped
     * at [maxHeight] when specified so wide layouts do not produce a towering header.
     */
    public data class AspectRatio(val ratio: Float, val maxHeight: Dp = Dp.Unspecified) : HeaderHeight() {
        init {
            require(ratio > 0f && ratio.isFinite()) { "HeaderHeight.AspectRatio ratio must be a positive width/height ratio such as 16f / 9f, was $ratio" }
            require(!maxHeight.isSpecified || maxHeight >= 0.dp) { "HeaderHeight.AspectRatio maxHeight must be 0.dp or more, was $maxHeight" }
        }
    }

    /**
     * Height as a fraction of the available height, between 0f and 1f (e.g. 0.4f for 40%),
     * capped at [maxHeight] when specified so tablets and landscape do not produce a towering header.
     */
    public data class Percentage(val percentage: Float, val maxHeight: Dp = Dp.Unspecified) : HeaderHeight() {
        init {
            require(percentage > 0f && percentage <= 1f) { "HeaderHeight.Percentage must be in 0..1 (0.4f is 40% of the height), was $percentage" }
            require(!maxHeight.isSpecified || maxHeight >= 0.dp) { "HeaderHeight.Percentage maxHeight must be 0.dp or more, was $maxHeight" }
        }
    }

    /** Resolves this specification against the space the layout was given. */
    internal fun resolve(availableWidth: Dp, availableHeight: Dp): Dp = when (this) {
        is Fixed -> height
        is AspectRatio -> (availableWidth / ratio).capped(maxHeight)
        is Percentage -> (availableHeight * percentage).capped(maxHeight)
    }

    private fun Dp.capped(max: Dp): Dp = if (max.isSpecified && this > max) max else this
}
