package am.highapps.parallaxtoolbar

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.lerp

/**
 * Title and optional subtitle. They sit at the bottom of the header when expanded and glide into
 * the toolbar as the body scrolls; all motion happens in the draw phase.
 */
@Composable
internal fun ParallaxTitle(
    collapseState: CollapseState,
    headerHeightPx: Float,
    toolbarHeightPx: Float,
    hasNavigationIcon: Boolean,
    config: ParallaxTitleConfig,
    titleFontScaleStart: Float,
    titleFontScaleEnd: Float,
    modifier: Modifier,
    titleContent: @Composable (Boolean) -> Unit,
    subtitleContent: (@Composable (Boolean) -> Unit)?
) {
    var combinedHeightPx by remember { mutableStateOf(0f) }
    var combinedWidthPx by remember { mutableStateOf(0f) }
    var subtitleHeightPx by remember { mutableStateOf(0f) }

    val isCollapsed by remember(collapseState) { derivedStateOf { collapseState.isCollapsed } }

    Column(
        modifier = modifier
            .graphicsLayer {
                val collapseFraction = collapseState.fraction
                if (config.keepSubtitleAfterCollapse) {
                    val scaleXY = lerp(
                        titleFontScaleStart.dp,
                        titleFontScaleEnd.dp,
                        collapseFraction
                    ).value
                    val extraStartPadding = combinedWidthPx.toDp() * (1 - scaleXY) / 2f

                    translationY = lerp(
                        headerHeightPx.toDp() - combinedHeightPx.toDp() - subtitleHeightPx.toDp() - config.paddingBottom,
                        toolbarHeightPx.toDp() / 2 - combinedHeightPx.toDp() / 2,
                        collapseFraction
                    ).toPx()

                    translationX = if (hasNavigationIcon) {
                        lerp(
                            config.paddingStart,
                            config.collapsedPaddingStart - extraStartPadding,
                            collapseFraction
                        ).toPx()
                    } else {
                        config.paddingStart.toPx()
                    }

                    scaleX = scaleXY
                    scaleY = scaleXY
                } else {
                    translationY = lerp(
                        headerHeightPx.toDp() - combinedHeightPx.toDp() - subtitleHeightPx.toDp() - config.paddingBottom,
                        toolbarHeightPx.toDp() / 2 - (combinedHeightPx - subtitleHeightPx).toDp() / 2,
                        collapseFraction
                    ).toPx()

                    translationX = if (hasNavigationIcon) {
                        lerp(
                            config.paddingStart,
                            config.collapsedPaddingStart,
                            collapseFraction
                        ).toPx()
                    } else {
                        config.paddingStart.toPx()
                    }
                }
            }
            .onGloballyPositioned {
                combinedHeightPx = it.size.height.toFloat()
                combinedWidthPx = it.size.width.toFloat()
            }
    ) {
        titleContent(isCollapsed)

        subtitleContent?.let { content ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .onGloballyPositioned {
                        subtitleHeightPx = it.size.height.toFloat()
                    }
                    .graphicsLayer {
                        if (!config.keepSubtitleAfterCollapse) {
                            alpha = if (config.animateSubTitleHiding) {
                                lerp(1f, 0f, collapseState.fraction)
                            } else {
                                if (collapseState.isCollapsed) 0f else 1f
                            }
                        }
                    }
            ) {
                content(isCollapsed)
            }
        }
    }
}

private fun lerp(start: Float, stop: Float, fraction: Float): Float =
    start + (stop - start) * fraction

private val Float.dp: Dp get() = Dp(this)
