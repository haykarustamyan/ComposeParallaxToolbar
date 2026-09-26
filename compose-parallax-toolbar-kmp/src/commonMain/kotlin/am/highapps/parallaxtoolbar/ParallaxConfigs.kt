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
 */

/** Header appearance and initial state. */
@Immutable
data class ParallaxHeaderConfig(
    val height: HeaderHeight,
    val gradient: Brush?,
    val isExpandedWhenFirstDisplayed: Boolean = true
)

/** Toolbar colors, elevation and icon metrics. */
@Immutable
data class ParallaxToolbarConfig(
    val initialColor: Color,
    val targetColor: Color,
    val elevation: Dp,
    val iconSize: Dp,
    val iconSpacing: Dp,
    val animationSpec: AnimationSpec<Color>
)

/** Title and subtitle placement and collapse behavior. */
@Immutable
data class ParallaxTitleConfig(
    val paddingBottom: Dp,
    val paddingStart: Dp,
    val collapsedPaddingStart: Dp,
    val keepSubtitleAfterCollapse: Boolean,
    val animateSubTitleHiding: Boolean
)

/** Body spacing. */
@Immutable
data class ParallaxBodyConfig(
    val minBottomSpacerHeight: Dp
)

/** Options forwarded to the `LazyColumn` used by [ParallaxContent.Lazy]. */
@Immutable
data class LazyColumnConfig(
    val contentPadding: PaddingValues = PaddingValues(0.dp),
    val verticalArrangement: Arrangement.Vertical = Arrangement.Top,
    val horizontalAlignment: Alignment.Horizontal = Alignment.Start,
    val flingBehavior: FlingBehavior? = null,
    val userScrollEnabled: Boolean = true,
    val overscrollEffect: OverscrollEffect? = null
)
