package am.highapps.parallaxtoolbar

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import kotlin.math.roundToInt

private const val NavigationSlot = "navigation"
private const val ActionsSlot = "actions"
private const val TitleSlot = "title"
private const val SubtitleSlot = "subtitle"

/** Horizontal inset of the navigation and action slots, matching Material's top app bar. */
private val SlotHorizontalPadding = 4.dp

/**
 * The pinned bar and the title block, measured and placed in a single pass.
 *
 * The navigation icon and actions are laid out inside the bar. The title and subtitle are placed at
 * their collapsed position, constrained to the width left between the two slots, and a layer block
 * translates and scales them toward their expanded position in the header as the body scrolls.
 * Reading [collapseState] inside the layer block means scrolling only re-runs that block, never
 * measurement.
 */
@Composable
internal fun ParallaxTopBar(
    collapseState: CollapseState,
    isCollapsed: Boolean,
    topInset: Dp,
    headerHeight: Dp,
    toolbarConfig: ParallaxToolbarConfig,
    titleConfig: ParallaxTitleConfig,
    navigationIcon: (@Composable (Boolean) -> Unit)?,
    actions: (@Composable RowScope.(Boolean) -> Unit)?,
    titleContent: @Composable (Boolean) -> Unit,
    subtitleContent: (@Composable (Boolean) -> Unit)?,
    modifier: Modifier = Modifier
) {
    val backgroundColor by animateColorAsState(
        targetValue = if (isCollapsed) toolbarConfig.targetColor else toolbarConfig.initialColor,
        animationSpec = toolbarConfig.animationSpec
    )
    // A shadow under a transparent bar would draw a band across the header, so it follows collapse.
    val elevation by animateDpAsState(targetValue = if (isCollapsed) toolbarConfig.elevation else 0.dp)

    Layout(
        modifier = modifier
            .fillMaxWidth()
            .shadow(elevation = elevation)
            .drawBehind { drawRect(backgroundColor) },
        content = {
            if (navigationIcon != null) {
                Box(Modifier.layoutId(NavigationSlot)) { navigationIcon(isCollapsed) }
            }
            if (actions != null) {
                Row(Modifier.layoutId(ActionsSlot)) { actions(isCollapsed) }
            }
            Box(Modifier.layoutId(TitleSlot)) { titleContent(isCollapsed) }
            if (subtitleContent != null) {
                Box(Modifier.layoutId(SubtitleSlot)) { subtitleContent(isCollapsed) }
            }
        }
    ) { measurables, constraints ->
        val width = constraints.maxWidth
        val insetPx = topInset.roundToPx()
        val toolbarPx = toolbarConfig.height.roundToPx()
        val barHeight = insetPx + toolbarPx
        val slotPadding = SlotHorizontalPadding.roundToPx()
        val loose = Constraints(maxWidth = width, maxHeight = toolbarPx)

        val navigation = measurables.firstOrNull { it.layoutId == NavigationSlot }?.measure(loose)
        val actionsPlaceable = measurables.firstOrNull { it.layoutId == ActionsSlot }?.measure(loose)
        val navigationWidth = navigation?.width ?: 0
        val actionsWidth = actionsPlaceable?.width ?: 0

        // Title geometry in dp-derived px. Expanded values describe the block's top-left in the
        // header; collapsed values describe it in the bar. The block is placed collapsed.
        val paddingStart = titleConfig.paddingStart.toPx()
        val collapsedPaddingStart =
            if (navigation != null) titleConfig.collapsedPaddingStart.toPx() else paddingStart
        val titleMaxWidth = (width - navigationWidth - actionsWidth).coerceAtLeast(0)
        val titleConstraints = Constraints(maxWidth = titleMaxWidth)
        val title = measurables.first { it.layoutId == TitleSlot }.measure(titleConstraints)
        val subtitle = measurables.firstOrNull { it.layoutId == SubtitleSlot }?.measure(titleConstraints)
        val subtitleHeight = subtitle?.height ?: 0
        val blockHeight = title.height + subtitleHeight
        val collapsedScale = titleConfig.collapsedScale
        val keepSubtitle = titleConfig.keepSubtitleAfterCollapse

        // Expanded: the block sits above the header's bottom edge, offset by paddingBottom. The
        // subtitle height counts twice here on purpose; it preserves the placement of 1.x.
        val expandedTop = insetPx + headerHeight.toPx() - blockHeight - subtitleHeight -
                titleConfig.paddingBottom.toPx()
        // Collapsed: the title, or the whole block when the subtitle is kept, is centered in the
        // toolbar at its collapsed scale. The scale origin is the block's top-start corner.
        val centeredHeight = (if (keepSubtitle) blockHeight else title.height) * collapsedScale
        val collapsedTop = insetPx + (toolbarPx - centeredHeight) / 2f
        val isRtl = layoutDirection == LayoutDirection.Rtl
        val direction = if (isRtl) -1f else 1f

        layout(width, barHeight) {
            navigation?.let {
                val x = if (isRtl) width - slotPadding - it.width else slotPadding
                it.placeRelative(x, insetPx + (toolbarPx - it.height) / 2)
            }
            actionsPlaceable?.let {
                val x = if (isRtl) slotPadding else width - slotPadding - it.width
                it.placeRelative(x, insetPx + (toolbarPx - it.height) / 2)
            }

            val collapsedX = if (isRtl) width - collapsedPaddingStart - title.width else collapsedPaddingStart
            val titleX = collapsedX.roundToInt()
            val titleY = collapsedTop.roundToInt()

            title.placeWithLayer(titleX, titleY) {
                val fraction = collapseState.fraction
                val scale = 1f + (collapsedScale - 1f) * fraction
                transformOrigin = TransformOrigin(if (isRtl) 1f else 0f, 0f)
                scaleX = scale
                scaleY = scale
                translationX = direction * (paddingStart - collapsedPaddingStart) * (1f - fraction)
                translationY = (expandedTop - collapsedTop) * (1f - fraction)
            }

            subtitle?.placeWithLayer(titleX, titleY + title.height) {
                val fraction = collapseState.fraction
                val scale = 1f + (collapsedScale - 1f) * fraction
                transformOrigin = TransformOrigin(if (isRtl) 1f else 0f, 0f)
                scaleX = scale
                scaleY = scale
                translationX = direction * (paddingStart - collapsedPaddingStart) * (1f - fraction)
                // Follow the title's bottom edge as it scales about its top.
                translationY = (expandedTop - collapsedTop) * (1f - fraction) + title.height * (scale - 1f)
                alpha = when {
                    keepSubtitle -> 1f
                    titleConfig.animateSubTitleHiding -> 1f - fraction
                    collapseState.isCollapsed -> 0f
                    else -> 1f
                }
            }
        }
    }
}
