package am.highapps.parallaxtoolbar

import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable

/**
 * The scrollable body of a [ComposeParallaxToolbarLayout]. The lambda receives `true` once the
 * toolbar is collapsed.
 *
 * The header collapses through nested scrolling, so any vertically scrollable composable works
 * as the body. [Regular] and [Lazy] are conveniences; [Custom] takes anything else, such as a
 * `LazyVerticalGrid`, a `LazyVerticalStaggeredGrid` or a pager whose pages scroll.
 */
public sealed class ParallaxContent {
    /** Regular content laid out in a `Column` with vertical scroll. */
    public data class Regular(val content: @Composable (Boolean) -> Unit) : ParallaxContent()

    /**
     * Content laid out in a `LazyColumn`, for long lists.
     *
     * @param content The items to display.
     * @param config LazyColumn behavior (padding, arrangement, fling, overscroll).
     * @param lazyListState State to observe or control the list; the toolbar state's list state
     *   is used when null.
     */
    public data class Lazy(
        val content: LazyListScope.(Boolean) -> Unit,
        val config: LazyColumnConfig = LazyColumnConfig(),
        val lazyListState: LazyListState? = null
    ) : ParallaxContent()

    /**
     * Any scrollable composable. It is given the full width and the height left below the
     * collapsed toolbar; it should fill that size and scroll vertically so nested scroll events
     * reach the header. `contentPadding` passed to the layout is not applied; apply it inside.
     */
    public data class Custom(val content: @Composable (Boolean) -> Unit) : ParallaxContent()
}
