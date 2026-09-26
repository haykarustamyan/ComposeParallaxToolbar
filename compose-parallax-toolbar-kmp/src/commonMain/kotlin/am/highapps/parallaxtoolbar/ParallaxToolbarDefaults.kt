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
public object ParallaxToolbarDefaults {
    // Header defaults
    public val HeaderHeightDp: Dp = 450.dp
    public const val HeaderParallaxMultiplier: Float = 0.5f

    // Toolbar defaults
    public val ToolbarHeight: Dp = 64.dp

    // Title and subtitle defaults
    public val TitlePaddingBottom: Dp = (-16).dp
    public val TitlePaddingStart: Dp = 16.dp
    public val TitleCollapsedPaddingStart: Dp = 64.dp
    public const val TitleCollapsedScale: Float = 1f

    // Body defaults
    public val BodyMinBottomSpacing: Dp = 0.dp

    @Composable
    public fun headerConfig(
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
    public fun headerConfigWithAspectRatio(
        aspectRatio: Float = 16f / 9f,
        gradient: Brush? = null,
        isExpandedWhenFirstDisplayed: Boolean = true
    ): ParallaxHeaderConfig = ParallaxHeaderConfig(
        height = HeaderHeight.AspectRatio(aspectRatio),
        gradient = gradient,
        isExpandedWhenFirstDisplayed = isExpandedWhenFirstDisplayed
    )

    @Composable
    public fun headerConfigWithPercentage(
        heightPercentage: Float = 0.4f,
        gradient: Brush? = null,
        isExpandedWhenFirstDisplayed: Boolean = true
    ): ParallaxHeaderConfig = ParallaxHeaderConfig(
        height = HeaderHeight.Percentage(heightPercentage),
        gradient = gradient,
        isExpandedWhenFirstDisplayed = isExpandedWhenFirstDisplayed
    )

    @Composable
    public fun toolbarConfig(
        initialColor: Color = Color.Transparent,
        targetColor: Color = Color.Black,
        elevation: Dp = 0.dp,
        animationSpec: AnimationSpec<Color> = tween(durationMillis = 300),
        height: Dp = ToolbarHeight
    ): ParallaxToolbarConfig = ParallaxToolbarConfig(
        initialColor = initialColor,
        targetColor = targetColor,
        elevation = elevation,
        animationSpec = animationSpec,
        height = height
    )

    @Composable
    public fun titleConfig(
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
    public fun bodyConfig(
        minBottomSpacerHeight: Dp = BodyMinBottomSpacing
    ): ParallaxBodyConfig = ParallaxBodyConfig(
        minBottomSpacerHeight = minBottomSpacerHeight
    )

    @Composable
    public fun lazyColumnConfig(
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
