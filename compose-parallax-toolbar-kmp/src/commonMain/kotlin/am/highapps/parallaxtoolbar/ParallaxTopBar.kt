package am.highapps.parallaxtoolbar

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
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
 * Reading [headerState] inside the layer block means scrolling only re-runs that block, never
 * measurement.
 */
@Composable
internal fun ParallaxTopBar(
    headerState: HeaderScrollState,
    isCollapsed: Boolean,
    topInset: Dp,
    leftInset: Dp,
    rightInset: Dp,
    headerHeight: Dp,
    toolbarConfig: ParallaxToolbarConfig,
    titleConfig: ParallaxTitleConfig,
    scope: ParallaxToolbarScope,
    navigationIcon: (@Composable ParallaxToolbarScope.(Boolean) -> Unit)?,
    actions: (@Composable ParallaxActionsScope.(Boolean) -> Unit)?,
    titleContent: @Composable ParallaxToolbarScope.(Boolean) -> Unit,
    subtitleContent: (@Composable ParallaxToolbarScope.(Boolean) -> Unit)?,
    modifier: Modifier = Modifier,
) {
    val backgroundColor by animateColorAsState(
        targetValue = if (isCollapsed) toolbarConfig.targetColor else toolbarConfig.initialColor,
        animationSpec = toolbarConfig.animationSpec,
    )
    // A shadow under a transparent bar would draw a band across the header, so it follows
    // collapse unless the caller asks for it always.
    val elevation by animateDpAsState(
        targetValue = if (isCollapsed || toolbarConfig.alwaysElevated) toolbarConfig.elevation else 0.dp,
    )

    Layout(
        modifier = modifier
            .fillMaxWidth()
            .shadow(elevation = elevation)
            .drawBehind { drawRect(backgroundColor) },
        content = {
            if (navigationIcon != null) {
                Box(Modifier.layoutId(NavigationSlot)) { scope.navigationIcon(isCollapsed) }
            }
            if (actions != null) {
                Row(Modifier.layoutId(ActionsSlot)) {
                    ParallaxActionsScopeImpl(scope, this).actions(isCollapsed)
                }
            }
            Box(Modifier.layoutId(TitleSlot).semantics { heading() }) { scope.titleContent(isCollapsed) }
            if (subtitleContent != null) {
                Box(Modifier.layoutId(SubtitleSlot)) { scope.subtitleContent(isCollapsed) }
            }
        },
    ) { measurables, constraints ->
        val width = constraints.maxWidth
        val insetPx = topInset.roundToPx()
        val toolbarPx = toolbarConfig.height.roundToPx()
        val barHeight = insetPx + toolbarPx
        val slotPadding = SlotHorizontalPadding.roundToPx()
        val isRtl = layoutDirection == LayoutDirection.Rtl
        // Horizontal insets are physical; the start inset is the one on the navigation side.
        val leftInsetPx = leftInset.roundToPx()
        val rightInsetPx = rightInset.roundToPx()
        val startInset = if (isRtl) rightInsetPx else leftInsetPx
        val endInset = if (isRtl) leftInsetPx else rightInsetPx
        val loose = Constraints(maxWidth = (width - leftInsetPx - rightInsetPx).coerceAtLeast(0), maxHeight = toolbarPx)

        val navigation = measurables.firstOrNull { it.layoutId == NavigationSlot }?.measure(loose)
        val actionsPlaceable = measurables.firstOrNull { it.layoutId == ActionsSlot }?.measure(loose)
        // Space the slots take at the start and end of the bar, insets and slot padding included.
        val startSlot = startInset + (navigation?.let { it.width + slotPadding } ?: 0)
        val endSlot = endInset + (actionsPlaceable?.let { it.width + slotPadding } ?: 0)
        val freeWidth = (width - startSlot - endSlot).coerceAtLeast(0)

        // Title geometry in dp-derived px, all measured from the start edge; placement mirrors
        // them for RTL. Expanded values describe the block's top-start corner in the header;
        // collapsed values describe it in the bar. The block is placed collapsed.
        val paddingStart = startInset + titleConfig.paddingStart.toPx()
        val collapsedScale = titleConfig.collapsedScale
        val collapsedStart = if (navigation != null) startInset + titleConfig.collapsedPaddingStart.toPx() else paddingStart
        // The title must fit between the slots once collapsed and scaled, or it would run under
        // the actions; measure it to whichever bound is tighter.
        val collapsedFit = when (titleConfig.collapsedAlignment) {
            Alignment.CenterHorizontally -> freeWidth / collapsedScale
            Alignment.End -> (freeWidth - titleConfig.paddingStart.toPx()) / collapsedScale
            else -> (width - endSlot - collapsedStart) / collapsedScale
        }
        val titleMaxWidth = minOf(freeWidth.toFloat(), collapsedFit).roundToInt().coerceAtLeast(0)
        val titleConstraints = Constraints(maxWidth = titleMaxWidth)
        val title = measurables.first { it.layoutId == TitleSlot }.measure(titleConstraints)
        val subtitle = measurables.firstOrNull { it.layoutId == SubtitleSlot }?.measure(titleConstraints)
        val subtitleHeight = subtitle?.height ?: 0
        val blockHeight = title.height + subtitleHeight
        val keepSubtitle = titleConfig.keepSubtitleAfterCollapse
        // Collapsed start edge: next to the navigation icon, centered between the slots, or
        // before the actions, per the configured alignment.
        val scaledTitleWidth = title.width * collapsedScale
        val collapsedPaddingStart = when (titleConfig.collapsedAlignment) {
            Alignment.CenterHorizontally -> startSlot + (freeWidth - scaledTitleWidth) / 2f
            Alignment.End -> width - endSlot - titleConfig.paddingStart.toPx() - scaledTitleWidth
            else -> collapsedStart
        }

        // Expanded: the whole block, subtitle included, ends paddingBottom above the header's
        // bottom edge, with or without a subtitle.
        val expandedTop = insetPx + headerHeight.toPx() - blockHeight - titleConfig.paddingBottom.toPx()
        // Collapsed: the title, or the whole block when the subtitle is kept, is centered in the
        // toolbar at its collapsed scale. The scale origin is the block's top-start corner.
        val centeredHeight = (if (keepSubtitle) blockHeight else title.height) * collapsedScale
        val collapsedTop = insetPx + (toolbarPx - centeredHeight) / 2f

        layout(width, barHeight) {
            // Coordinates below are already mirrored for RTL, so place them as is.
            navigation?.let {
                val x = if (isRtl) width - rightInsetPx - slotPadding - it.width else leftInsetPx + slotPadding
                it.place(x, insetPx + (toolbarPx - it.height) / 2)
            }
            actionsPlaceable?.let {
                val x = if (isRtl) leftInsetPx + slotPadding else width - rightInsetPx - slotPadding - it.width
                it.place(x, insetPx + (toolbarPx - it.height) / 2)
            }

            // Center and End alignments already account for the scaled width, so the layer scales
            // about the start edge that placement chose; Start keeps the 1.x geometry.
            val collapsedX = if (isRtl) width - collapsedPaddingStart - title.width else collapsedPaddingStart
            val titleX = collapsedX.roundToInt()
            val titleY = collapsedTop.roundToInt()
            val expandedStart = if (isRtl) width - paddingStart - title.width else paddingStart

            title.placeWithLayer(titleX, titleY) {
                val fraction = headerState.fraction
                val scale = 1f + (collapsedScale - 1f) * fraction
                transformOrigin = TransformOrigin(if (isRtl) 1f else 0f, 0f)
                scaleX = scale
                scaleY = scale
                translationX = (expandedStart - collapsedX) * (1f - fraction)
                translationY = (expandedTop - collapsedTop) * (1f - fraction) + headerState.stretchPx
            }

            subtitle?.placeWithLayer(titleX, titleY + title.height) {
                val fraction = headerState.fraction
                val scale = 1f + (collapsedScale - 1f) * fraction
                transformOrigin = TransformOrigin(if (isRtl) 1f else 0f, 0f)
                scaleX = scale
                scaleY = scale
                translationX = (expandedStart - collapsedX) * (1f - fraction)
                // Follow the title's bottom edge as it scales about its top.
                translationY = (expandedTop - collapsedTop) * (1f - fraction) + title.height * (scale - 1f) + headerState.stretchPx
                alpha = when {
                    keepSubtitle -> 1f
                    titleConfig.animateSubTitleHiding -> 1f - fraction
                    headerState.isCollapsed -> 0f
                    else -> 1f
                }
            }
        }
    }
}
