package am.highapps.parallaxtoolbar

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.tween
import androidx.compose.foundation.OverscrollEffect
import androidx.compose.foundation.gestures.FlingBehavior
import androidx.compose.foundation.gestures.ScrollableDefaults
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.rememberOverscrollEffect
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Default values and factory methods for [ComposeParallaxToolbarLayout].
 */
object ParallaxToolbarDefaults {
    // Header defaults
    val HeaderHeightDp: Dp = 450.dp
    const val HeaderParallaxMultiplier: Float = 0.5f

    // Toolbar defaults
    val ToolbarHeight: Dp = 64.dp
    val ToolbarMinWidth: Dp = 56.dp
    val ToolbarIconSize: Dp = 24.dp
    val ToolbarIconSpacing: Dp = 8.dp

    // Title and subtitle defaults
    val TitlePaddingBottom: Dp = (-16).dp
    val TitlePaddingStart: Dp = 16.dp
    val TitleCollapsedPaddingStart: Dp = 64.dp
    const val TitleCollapsedScale: Float = 1f

    // Body defaults
    val BodyMinBottomSpacing: Dp = 0.dp

    @Composable
    fun headerConfig(
        height: HeaderHeight = HeaderHeight.Fixed(HeaderHeightDp),
        gradient: Brush? = null,
        isExpandedWhenFirstDisplayed: Boolean = true,
        parallaxMultiplier: Float = HeaderParallaxMultiplier
    ): ParallaxHeaderConfig = ParallaxHeaderConfig(
        height = height,
        gradient = gradient,
        isExpandedWhenFirstDisplayed = isExpandedWhenFirstDisplayed,
        parallaxMultiplier = parallaxMultiplier
    )

    @Composable
    fun headerConfigWithAspectRatio(
        aspectRatio: Float = 16f / 9f,
        gradient: Brush? = null,
        isExpandedWhenFirstDisplayed: Boolean = true
    ): ParallaxHeaderConfig = ParallaxHeaderConfig(
        height = HeaderHeight.AspectRatio(aspectRatio),
        gradient = gradient,
        isExpandedWhenFirstDisplayed = isExpandedWhenFirstDisplayed
    )

    @Composable
    fun headerConfigWithPercentage(
        heightPercentage: Float = 0.4f,
        gradient: Brush? = null,
        isExpandedWhenFirstDisplayed: Boolean = true
    ): ParallaxHeaderConfig = ParallaxHeaderConfig(
        height = HeaderHeight.Percentage(heightPercentage),
        gradient = gradient,
        isExpandedWhenFirstDisplayed = isExpandedWhenFirstDisplayed
    )

    @Composable
    fun toolbarConfig(
        initialColor: Color = Color.Transparent,
        targetColor: Color = Color.Black,
        elevation: Dp = 0.dp,
        iconSize: Dp = ToolbarIconSize,
        iconSpacing: Dp = ToolbarIconSpacing,
        animationSpec: AnimationSpec<Color> = tween(durationMillis = 300),
        height: Dp = ToolbarHeight
    ): ParallaxToolbarConfig = ParallaxToolbarConfig(
        initialColor = initialColor,
        targetColor = targetColor,
        elevation = elevation,
        iconSize = iconSize,
        iconSpacing = iconSpacing,
        animationSpec = animationSpec,
        height = height
    )

    @Composable
    fun titleConfig(
        paddingBottom: Dp = TitlePaddingBottom,
        paddingStart: Dp = TitlePaddingStart,
        collapsedPaddingStart: Dp = TitleCollapsedPaddingStart,
        keepSubtitleAfterCollapse: Boolean = false,
        animateSubTitleHiding: Boolean = true,
        collapsedScale: Float = TitleCollapsedScale
    ): ParallaxTitleConfig = ParallaxTitleConfig(
        paddingBottom = paddingBottom,
        paddingStart = paddingStart,
        collapsedPaddingStart = collapsedPaddingStart,
        keepSubtitleAfterCollapse = keepSubtitleAfterCollapse,
        animateSubTitleHiding = animateSubTitleHiding,
        collapsedScale = collapsedScale
    )

    @Composable
    fun bodyConfig(
        minBottomSpacerHeight: Dp = BodyMinBottomSpacing
    ): ParallaxBodyConfig = ParallaxBodyConfig(
        minBottomSpacerHeight = minBottomSpacerHeight
    )

    @Composable
    fun lazyColumnConfig(
        contentPadding: PaddingValues = PaddingValues(0.dp),
        verticalArrangement: Arrangement.Vertical = Arrangement.Top,
        horizontalAlignment: Alignment.Horizontal = Alignment.Start,
        flingBehavior: FlingBehavior? = null,
        userScrollEnabled: Boolean = true,
        overscrollEffect: OverscrollEffect? = null
    ): LazyColumnConfig = LazyColumnConfig(
        contentPadding = contentPadding,
        verticalArrangement = verticalArrangement,
        horizontalAlignment = horizontalAlignment,
        flingBehavior = flingBehavior ?: ScrollableDefaults.flingBehavior(),
        userScrollEnabled = userScrollEnabled,
        overscrollEffect = overscrollEffect ?: rememberOverscrollEffect()
    )
}
