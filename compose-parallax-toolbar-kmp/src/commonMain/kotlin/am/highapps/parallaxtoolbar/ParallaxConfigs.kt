package am.highapps.parallaxtoolbar

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.foundation.OverscrollEffect
import androidx.compose.foundation.gestures.FlingBehavior
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/*
 * Configuration holders. They are immutable so the layout can skip recomposition when the parent
 * recomposes with the same values. Build them with the factories in [ParallaxToolbarDefaults].
 *
 * They are deliberately not data classes: a data class exposes componentN() and a copy() whose
 * signatures change whenever a field is added, which breaks compiled consumers. When adding a
 * field here, append it with a default, extend equals/hashCode/toString, and keep the previous
 * copy() overload next to the new one.
 */

/**
 * Header appearance and initial state.
 *
 * @param parallaxMultiplier How much of the scroll distance the header content moves by. 0f pins
 *   it, 1f scrolls it with the body, 0.5f is the classic parallax.
 * @param snapOnRelease When a drag or fling ends with the header partly collapsed, settle it to
 *   the nearer resting position instead of leaving it there.
 * @param scrollMode How the header and toolbar react to the body scrolling; see [ScrollMode].
 * @param fadeOnCollapse Fade the whole header out as it collapses. Turn off, together with a
 *   `parallaxMultiplier` of 0f, when header elements use the per-element modifiers of
 *   [ParallaxToolbarScope] instead.
 * @param stretchEnabled Let a downward drag past the top stretch the header, which zooms its
 *   content and pushes the body down, then springs back on release. Pair with the layout's
 *   `onStretchTrigger` for pull-to-refresh.
 * @param stretchTriggerDistance How far the header must be stretched when released for
 *   `onStretchTrigger` to fire.
 */
@Immutable
public class ParallaxHeaderConfig(
    public val height: HeaderHeight,
    public val gradient: Brush?,
    public val isExpandedWhenFirstDisplayed: Boolean = true,
    public val parallaxMultiplier: Float = ParallaxToolbarDefaults.HeaderParallaxMultiplier,
    public val snapOnRelease: Boolean = false,
    public val scrollMode: ScrollMode = ScrollMode.ExitUntilCollapsed,
    public val fadeOnCollapse: Boolean = true,
    public val stretchEnabled: Boolean = false,
    public val stretchTriggerDistance: Dp = ParallaxToolbarDefaults.StretchTriggerDistance
) {
    public fun copy(
        height: HeaderHeight = this.height,
        gradient: Brush? = this.gradient,
        isExpandedWhenFirstDisplayed: Boolean = this.isExpandedWhenFirstDisplayed,
        parallaxMultiplier: Float = this.parallaxMultiplier,
        snapOnRelease: Boolean = this.snapOnRelease,
        scrollMode: ScrollMode = this.scrollMode,
        fadeOnCollapse: Boolean = this.fadeOnCollapse,
        stretchEnabled: Boolean = this.stretchEnabled,
        stretchTriggerDistance: Dp = this.stretchTriggerDistance
    ): ParallaxHeaderConfig = ParallaxHeaderConfig(
        height, gradient, isExpandedWhenFirstDisplayed, parallaxMultiplier, snapOnRelease, scrollMode,
        fadeOnCollapse, stretchEnabled, stretchTriggerDistance
    )

    override fun equals(other: Any?): Boolean = other is ParallaxHeaderConfig &&
            height == other.height &&
            gradient == other.gradient &&
            isExpandedWhenFirstDisplayed == other.isExpandedWhenFirstDisplayed &&
            parallaxMultiplier == other.parallaxMultiplier &&
            snapOnRelease == other.snapOnRelease &&
            scrollMode == other.scrollMode &&
            fadeOnCollapse == other.fadeOnCollapse &&
            stretchEnabled == other.stretchEnabled &&
            stretchTriggerDistance == other.stretchTriggerDistance

    override fun hashCode(): Int {
        var result = height.hashCode()
        result = 31 * result + gradient.hashCode()
        result = 31 * result + isExpandedWhenFirstDisplayed.hashCode()
        result = 31 * result + parallaxMultiplier.hashCode()
        result = 31 * result + snapOnRelease.hashCode()
        result = 31 * result + scrollMode.hashCode()
        result = 31 * result + fadeOnCollapse.hashCode()
        result = 31 * result + stretchEnabled.hashCode()
        result = 31 * result + stretchTriggerDistance.hashCode()
        return result
    }

    override fun toString(): String = "ParallaxHeaderConfig(height=$height, gradient=$gradient, " +
            "isExpandedWhenFirstDisplayed=$isExpandedWhenFirstDisplayed, " +
            "parallaxMultiplier=$parallaxMultiplier, snapOnRelease=$snapOnRelease, " +
            "scrollMode=$scrollMode, fadeOnCollapse=$fadeOnCollapse, " +
            "stretchEnabled=$stretchEnabled, stretchTriggerDistance=$stretchTriggerDistance)"
}

/**
 * Toolbar colors, elevation and height.
 *
 * @param height Height of the pinned toolbar, excluding the status bar inset.
 * @param alwaysElevated Draw the shadow while expanded too, instead of only once collapsed.
 */
@Immutable
public class ParallaxToolbarConfig(
    public val initialColor: Color,
    public val targetColor: Color,
    public val elevation: Dp,
    public val animationSpec: AnimationSpec<Color>,
    public val height: Dp = ParallaxToolbarDefaults.ToolbarHeight,
    public val alwaysElevated: Boolean = false
) {
    public fun copy(
        initialColor: Color = this.initialColor,
        targetColor: Color = this.targetColor,
        elevation: Dp = this.elevation,
        animationSpec: AnimationSpec<Color> = this.animationSpec,
        height: Dp = this.height,
        alwaysElevated: Boolean = this.alwaysElevated
    ): ParallaxToolbarConfig = ParallaxToolbarConfig(initialColor, targetColor, elevation, animationSpec, height, alwaysElevated)

    override fun equals(other: Any?): Boolean = other is ParallaxToolbarConfig &&
            initialColor == other.initialColor &&
            targetColor == other.targetColor &&
            elevation == other.elevation &&
            animationSpec == other.animationSpec &&
            height == other.height &&
            alwaysElevated == other.alwaysElevated

    override fun hashCode(): Int {
        var result = initialColor.hashCode()
        result = 31 * result + targetColor.hashCode()
        result = 31 * result + elevation.hashCode()
        result = 31 * result + animationSpec.hashCode()
        result = 31 * result + height.hashCode()
        result = 31 * result + alwaysElevated.hashCode()
        return result
    }

    override fun toString(): String = "ParallaxToolbarConfig(initialColor=$initialColor, " +
            "targetColor=$targetColor, elevation=$elevation, animationSpec=$animationSpec, " +
            "height=$height, alwaysElevated=$alwaysElevated)"
}

/**
 * Title and subtitle placement and collapse behavior.
 *
 * @param collapsedScale Scale of the title block once collapsed, e.g. 0.8f to shrink it into the
 *   toolbar. It scales about its start edge.
 * @param collapsedAlignment Where the collapsed title sits in the space between the navigation
 *   icon and the actions: [Alignment.Start] next to the navigation icon at
 *   [collapsedPaddingStart], [Alignment.CenterHorizontally] centered as on iOS, or
 *   [Alignment.End] before the actions.
 */
@Immutable
public class ParallaxTitleConfig(
    public val paddingBottom: Dp,
    public val paddingStart: Dp,
    public val collapsedPaddingStart: Dp,
    public val keepSubtitleAfterCollapse: Boolean,
    public val animateSubTitleHiding: Boolean,
    public val collapsedScale: Float = ParallaxToolbarDefaults.TitleCollapsedScale,
    public val collapsedAlignment: Alignment.Horizontal = Alignment.Start
) {
    public fun copy(
        paddingBottom: Dp = this.paddingBottom,
        paddingStart: Dp = this.paddingStart,
        collapsedPaddingStart: Dp = this.collapsedPaddingStart,
        keepSubtitleAfterCollapse: Boolean = this.keepSubtitleAfterCollapse,
        animateSubTitleHiding: Boolean = this.animateSubTitleHiding,
        collapsedScale: Float = this.collapsedScale,
        collapsedAlignment: Alignment.Horizontal = this.collapsedAlignment
    ): ParallaxTitleConfig = ParallaxTitleConfig(
        paddingBottom, paddingStart, collapsedPaddingStart,
        keepSubtitleAfterCollapse, animateSubTitleHiding, collapsedScale, collapsedAlignment
    )

    override fun equals(other: Any?): Boolean = other is ParallaxTitleConfig &&
            paddingBottom == other.paddingBottom &&
            paddingStart == other.paddingStart &&
            collapsedPaddingStart == other.collapsedPaddingStart &&
            keepSubtitleAfterCollapse == other.keepSubtitleAfterCollapse &&
            animateSubTitleHiding == other.animateSubTitleHiding &&
            collapsedScale == other.collapsedScale &&
            collapsedAlignment == other.collapsedAlignment

    override fun hashCode(): Int {
        var result = paddingBottom.hashCode()
        result = 31 * result + paddingStart.hashCode()
        result = 31 * result + collapsedPaddingStart.hashCode()
        result = 31 * result + keepSubtitleAfterCollapse.hashCode()
        result = 31 * result + animateSubTitleHiding.hashCode()
        result = 31 * result + collapsedScale.hashCode()
        result = 31 * result + collapsedAlignment.hashCode()
        return result
    }

    override fun toString(): String = "ParallaxTitleConfig(paddingBottom=$paddingBottom, " +
            "paddingStart=$paddingStart, collapsedPaddingStart=$collapsedPaddingStart, " +
            "keepSubtitleAfterCollapse=$keepSubtitleAfterCollapse, " +
            "animateSubTitleHiding=$animateSubTitleHiding, collapsedScale=$collapsedScale, " +
            "collapsedAlignment=$collapsedAlignment)"
}

/** Body spacing. */
@Immutable
public class ParallaxBodyConfig(
    public val minBottomSpacerHeight: Dp
) {
    public fun copy(minBottomSpacerHeight: Dp = this.minBottomSpacerHeight): ParallaxBodyConfig =
        ParallaxBodyConfig(minBottomSpacerHeight)

    override fun equals(other: Any?): Boolean =
        other is ParallaxBodyConfig && minBottomSpacerHeight == other.minBottomSpacerHeight

    override fun hashCode(): Int = minBottomSpacerHeight.hashCode()

    override fun toString(): String = "ParallaxBodyConfig(minBottomSpacerHeight=$minBottomSpacerHeight)"
}

/** Options forwarded to the `LazyColumn` used by [ParallaxContent.Lazy]. */
@Immutable
public class LazyColumnConfig(
    public val contentPadding: PaddingValues = PaddingValues(0.dp),
    public val verticalArrangement: Arrangement.Vertical = Arrangement.Top,
    public val horizontalAlignment: Alignment.Horizontal = Alignment.Start,
    public val flingBehavior: FlingBehavior? = null,
    public val userScrollEnabled: Boolean = true,
    public val overscrollEffect: OverscrollEffect? = null
) {
    public fun copy(
        contentPadding: PaddingValues = this.contentPadding,
        verticalArrangement: Arrangement.Vertical = this.verticalArrangement,
        horizontalAlignment: Alignment.Horizontal = this.horizontalAlignment,
        flingBehavior: FlingBehavior? = this.flingBehavior,
        userScrollEnabled: Boolean = this.userScrollEnabled,
        overscrollEffect: OverscrollEffect? = this.overscrollEffect
    ): LazyColumnConfig = LazyColumnConfig(
        contentPadding, verticalArrangement, horizontalAlignment,
        flingBehavior, userScrollEnabled, overscrollEffect
    )

    override fun equals(other: Any?): Boolean = other is LazyColumnConfig &&
            contentPadding == other.contentPadding &&
            verticalArrangement == other.verticalArrangement &&
            horizontalAlignment == other.horizontalAlignment &&
            flingBehavior == other.flingBehavior &&
            userScrollEnabled == other.userScrollEnabled &&
            overscrollEffect == other.overscrollEffect

    override fun hashCode(): Int {
        var result = contentPadding.hashCode()
        result = 31 * result + verticalArrangement.hashCode()
        result = 31 * result + horizontalAlignment.hashCode()
        result = 31 * result + flingBehavior.hashCode()
        result = 31 * result + userScrollEnabled.hashCode()
        result = 31 * result + overscrollEffect.hashCode()
        return result
    }

    override fun toString(): String = "LazyColumnConfig(contentPadding=$contentPadding, " +
            "verticalArrangement=$verticalArrangement, horizontalAlignment=$horizontalAlignment, " +
            "flingBehavior=$flingBehavior, userScrollEnabled=$userScrollEnabled, " +
            "overscrollEffect=$overscrollEffect)"
}

/**
 * Strings the layout exposes to accessibility services. Provide translated values.
 *
 * @param expandedStateDescription Announced state while the header is expanded.
 * @param collapsedStateDescription Announced state once the header is collapsed.
 * @param expandActionLabel Label of the expand action offered while collapsed.
 * @param collapseActionLabel Label of the collapse action offered while expanded.
 */
@Immutable
public class ParallaxSemanticsConfig(
    public val expandedStateDescription: String = "Expanded",
    public val collapsedStateDescription: String = "Collapsed",
    public val expandActionLabel: String = "Expand header",
    public val collapseActionLabel: String = "Collapse header"
) {
    public fun copy(
        expandedStateDescription: String = this.expandedStateDescription,
        collapsedStateDescription: String = this.collapsedStateDescription,
        expandActionLabel: String = this.expandActionLabel,
        collapseActionLabel: String = this.collapseActionLabel
    ): ParallaxSemanticsConfig = ParallaxSemanticsConfig(
        expandedStateDescription, collapsedStateDescription, expandActionLabel, collapseActionLabel
    )

    override fun equals(other: Any?): Boolean = other is ParallaxSemanticsConfig &&
            expandedStateDescription == other.expandedStateDescription &&
            collapsedStateDescription == other.collapsedStateDescription &&
            expandActionLabel == other.expandActionLabel &&
            collapseActionLabel == other.collapseActionLabel

    override fun hashCode(): Int {
        var result = expandedStateDescription.hashCode()
        result = 31 * result + collapsedStateDescription.hashCode()
        result = 31 * result + expandActionLabel.hashCode()
        result = 31 * result + collapseActionLabel.hashCode()
        return result
    }

    override fun toString(): String = "ParallaxSemanticsConfig(expandedStateDescription=$expandedStateDescription, " +
            "collapsedStateDescription=$collapsedStateDescription, expandActionLabel=$expandActionLabel, " +
            "collapseActionLabel=$collapseActionLabel)"
}
