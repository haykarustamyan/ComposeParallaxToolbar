package am.highapps.parallaxtoolbar

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer

/** The expanded header: user content with a parallax translation and fade, under a gradient. */
@Composable
internal fun ParallaxHeader(
    collapseState: CollapseState,
    headerHeightPx: Float,
    gradientBrush: Brush?,
    initialColor: Color,
    targetColor: Color,
    modifier: Modifier,
    content: @Composable () -> Unit
) {
    val parallaxMultiplier = ParallaxToolbarDefaults.HeaderParallaxMultiplier
    val alphaHeightFraction = ParallaxToolbarDefaults.HeaderAlphaHeightFraction

    Box(
        modifier = modifier.graphicsLayer {
            val scrollOffset = collapseState.offsetPx
            translationY = -scrollOffset * parallaxMultiplier
            alpha = (1f - scrollOffset / (headerHeightPx * alphaHeightFraction)).coerceIn(0f, 1f)
        }
    ) {
        content()

        Box(
            Modifier
                .fillMaxSize()
                .background(
                    brush = gradientBrush ?: Brush.verticalGradient(
                        colors = listOf(initialColor, targetColor),
                        startY = 3 * headerHeightPx / 4
                    )
                )
        )
    }
}
