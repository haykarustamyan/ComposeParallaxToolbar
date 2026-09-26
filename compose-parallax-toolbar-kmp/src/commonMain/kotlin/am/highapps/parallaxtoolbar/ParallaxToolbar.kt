package am.highapps.parallaxtoolbar

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp

/** The pinned app bar. It carries the navigation icon and actions; the title is drawn separately. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun ParallaxToolbar(
    collapseState: CollapseState,
    initialColor: Color,
    targetColor: Color,
    colorAnimationSpec: AnimationSpec<Color>,
    elevation: Dp,
    navigationIcon: @Composable () -> Unit,
    actions: @Composable RowScope.() -> Unit
) {
    val isCollapsed by remember(collapseState) { derivedStateOf { collapseState.isCollapsed } }

    val backgroundColor by animateColorAsState(
        targetValue = if (isCollapsed) targetColor else initialColor,
        animationSpec = colorAnimationSpec
    )

    TopAppBar(
        modifier = Modifier.shadow(elevation = elevation),
        title = {},
        navigationIcon = navigationIcon,
        actions = actions,
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = backgroundColor
        )
    )
}
