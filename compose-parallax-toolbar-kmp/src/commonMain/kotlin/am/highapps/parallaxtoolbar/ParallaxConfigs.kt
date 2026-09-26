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
 */
@Immutable
class ParallaxHeaderConfig(
    val height: HeaderHeight,
    val gradient: Brush?,
    val isExpandedWhenFirstDisplayed: Boolean = true,
    val parallaxMultiplier: Float = ParallaxToolbarDefaults.HeaderParallaxMultiplier
) {
    fun copy(
        height: HeaderHeight = this.height,
        gradient: Brush? = this.gradient,
        isExpandedWhenFirstDisplayed: Boolean = this.isExpandedWhenFirstDisplayed,
        parallaxMultiplier: Float = this.parallaxMultiplier
    ) = ParallaxHeaderConfig(height, gradient, isExpandedWhenFirstDisplayed, parallaxMultiplier)

    override fun equals(other: Any?): Boolean = other is ParallaxHeaderConfig &&
            height == other.height &&
            gradient == other.gradient &&
            isExpandedWhenFirstDisplayed == other.isExpandedWhenFirstDisplayed &&
            parallaxMultiplier == other.parallaxMultiplier

    override fun hashCode(): Int {
        var result = height.hashCode()
        result = 31 * result + gradient.hashCode()
        result = 31 * result + isExpandedWhenFirstDisplayed.hashCode()
        result = 31 * result + parallaxMultiplier.hashCode()
        return result
    }

    override fun toString(): String = "ParallaxHeaderConfig(height=$height, gradient=$gradient, " +
            "isExpandedWhenFirstDisplayed=$isExpandedWhenFirstDisplayed, parallaxMultiplier=$parallaxMultiplier)"
}

/**
 * Toolbar colors, elevation and height.
 *
 * @param height Height of the pinned toolbar, excluding the status bar inset.
 */
@Immutable
class ParallaxToolbarConfig(
    val initialColor: Color,
    val targetColor: Color,
    val elevation: Dp,
    val animationSpec: AnimationSpec<Color>,
    val height: Dp = ParallaxToolbarDefaults.ToolbarHeight
) {
    fun copy(
        initialColor: Color = this.initialColor,
        targetColor: Color = this.targetColor,
        elevation: Dp = this.elevation,
        animationSpec: AnimationSpec<Color> = this.animationSpec,
        height: Dp = this.height
    ) = ParallaxToolbarConfig(initialColor, targetColor, elevation, animationSpec, height)

    override fun equals(other: Any?): Boolean = other is ParallaxToolbarConfig &&
            initialColor == other.initialColor &&
            targetColor == other.targetColor &&
            elevation == other.elevation &&
            animationSpec == other.animationSpec &&
            height == other.height

    override fun hashCode(): Int {
        var result = initialColor.hashCode()
        result = 31 * result + targetColor.hashCode()
        result = 31 * result + elevation.hashCode()
        result = 31 * result + animationSpec.hashCode()
        result = 31 * result + height.hashCode()
        return result
    }

    override fun toString(): String = "ParallaxToolbarConfig(initialColor=$initialColor, " +
            "targetColor=$targetColor, elevation=$elevation, animationSpec=$animationSpec, height=$height)"
}

/**
 * Title and subtitle placement and collapse behavior.
 *
 * @param collapsedScale Scale of the title block once collapsed, e.g. 0.8f to shrink it into the
 *   toolbar. It scales about its start edge.
 */
@Immutable
class ParallaxTitleConfig(
    val paddingBottom: Dp,
    val paddingStart: Dp,
    val collapsedPaddingStart: Dp,
    val keepSubtitleAfterCollapse: Boolean,
    val animateSubTitleHiding: Boolean,
    val collapsedScale: Float = ParallaxToolbarDefaults.TitleCollapsedScale
) {
    fun copy(
        paddingBottom: Dp = this.paddingBottom,
        paddingStart: Dp = this.paddingStart,
        collapsedPaddingStart: Dp = this.collapsedPaddingStart,
        keepSubtitleAfterCollapse: Boolean = this.keepSubtitleAfterCollapse,
        animateSubTitleHiding: Boolean = this.animateSubTitleHiding,
        collapsedScale: Float = this.collapsedScale
    ) = ParallaxTitleConfig(
        paddingBottom, paddingStart, collapsedPaddingStart,
        keepSubtitleAfterCollapse, animateSubTitleHiding, collapsedScale
    )

    override fun equals(other: Any?): Boolean = other is ParallaxTitleConfig &&
            paddingBottom == other.paddingBottom &&
            paddingStart == other.paddingStart &&
            collapsedPaddingStart == other.collapsedPaddingStart &&
            keepSubtitleAfterCollapse == other.keepSubtitleAfterCollapse &&
            animateSubTitleHiding == other.animateSubTitleHiding &&
            collapsedScale == other.collapsedScale

    override fun hashCode(): Int {
        var result = paddingBottom.hashCode()
        result = 31 * result + paddingStart.hashCode()
        result = 31 * result + collapsedPaddingStart.hashCode()
        result = 31 * result + keepSubtitleAfterCollapse.hashCode()
        result = 31 * result + animateSubTitleHiding.hashCode()
        result = 31 * result + collapsedScale.hashCode()
        return result
    }

    override fun toString(): String = "ParallaxTitleConfig(paddingBottom=$paddingBottom, " +
            "paddingStart=$paddingStart, collapsedPaddingStart=$collapsedPaddingStart, " +
            "keepSubtitleAfterCollapse=$keepSubtitleAfterCollapse, " +
            "animateSubTitleHiding=$animateSubTitleHiding, collapsedScale=$collapsedScale)"
}

/** Body spacing. */
@Immutable
class ParallaxBodyConfig(
    val minBottomSpacerHeight: Dp
) {
    fun copy(minBottomSpacerHeight: Dp = this.minBottomSpacerHeight) =
        ParallaxBodyConfig(minBottomSpacerHeight)

    override fun equals(other: Any?): Boolean =
        other is ParallaxBodyConfig && minBottomSpacerHeight == other.minBottomSpacerHeight

    override fun hashCode(): Int = minBottomSpacerHeight.hashCode()

    override fun toString(): String = "ParallaxBodyConfig(minBottomSpacerHeight=$minBottomSpacerHeight)"
}

/** Options forwarded to the `LazyColumn` used by [ParallaxContent.Lazy]. */
@Immutable
class LazyColumnConfig(
    val contentPadding: PaddingValues = PaddingValues(0.dp),
    val verticalArrangement: Arrangement.Vertical = Arrangement.Top,
    val horizontalAlignment: Alignment.Horizontal = Alignment.Start,
    val flingBehavior: FlingBehavior? = null,
    val userScrollEnabled: Boolean = true,
    val overscrollEffect: OverscrollEffect? = null
) {
    fun copy(
        contentPadding: PaddingValues = this.contentPadding,
        verticalArrangement: Arrangement.Vertical = this.verticalArrangement,
        horizontalAlignment: Alignment.Horizontal = this.horizontalAlignment,
        flingBehavior: FlingBehavior? = this.flingBehavior,
        userScrollEnabled: Boolean = this.userScrollEnabled,
        overscrollEffect: OverscrollEffect? = this.overscrollEffect
    ) = LazyColumnConfig(
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
