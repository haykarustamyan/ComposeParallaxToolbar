package am.highapps.parallaxtoolbar

import androidx.compose.runtime.Immutable
import androidx.compose.ui.unit.Dp

/**
 * How the expanded header height is determined.
 */
@Immutable
sealed class HeaderHeight {
    /** Fixed height in Dp. */
    data class Fixed(val height: Dp) : HeaderHeight()

    /** Height derived from the available width and a width/height ratio, e.g. `16f / 9f`. */
    data class AspectRatio(val ratio: Float) : HeaderHeight()

    /** Height as a fraction of the available height, between 0f and 1f (e.g. 0.4f for 40%). */
    data class Percentage(val percentage: Float) : HeaderHeight()

    /** Resolves this specification against the space the layout was given. */
    internal fun resolve(availableWidth: Dp, availableHeight: Dp): Dp = when (this) {
        is Fixed -> height
        is AspectRatio -> availableWidth / ratio
        is Percentage -> availableHeight * percentage
    }
}
