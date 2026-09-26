package am.highapps.parallaxtoolbar

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer

/** The expanded header: user content with a parallax translation and fade, under a gradient. */
@Composable
internal fun ParallaxHeader(
    headerState: HeaderScrollState,
    headerHeightPx: Float,
    parallaxMultiplier: Float,
    fadeOnCollapse: Boolean,
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
            if (fadeOnCollapse) alpha = 1f - headerState.fraction
            // A stretch zooms the content about the top edge so it fills the extra height.
            val stretch = headerState.stretchPx
            if (stretch > 0f && headerHeightPx > 0f) {
                val zoom = 1f + stretch / headerHeightPx
                transformOrigin = TransformOrigin(0.5f, 0f)
                scaleX = zoom
                scaleY = zoom
            }
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
