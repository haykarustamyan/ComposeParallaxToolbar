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
    headerState: HeaderScrollState,
    headerHeightPx: Float,
    parallaxMultiplier: Float,
    gradientBrush: Brush?,
    initialColor: Color,
    targetColor: Color,
    modifier: Modifier,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier.graphicsLayer {
            translationY = -headerState.offsetPx * parallaxMultiplier
            // Fully faded exactly when the toolbar covers it, so nothing shows through the body.
            alpha = 1f - headerState.fraction
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
