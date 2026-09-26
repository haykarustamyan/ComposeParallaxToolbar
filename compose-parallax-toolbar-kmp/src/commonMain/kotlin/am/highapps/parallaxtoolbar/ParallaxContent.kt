package am.highapps.parallaxtoolbar

import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable

/**
 * The scrollable body of a [ComposeParallaxToolbarLayout]. The lambda receives `true` once the
 * toolbar is collapsed.
 */
public sealed class ParallaxContent {
    /** Regular content laid out in a `Column` with vertical scroll. */
    public data class Regular(val content: @Composable (Boolean) -> Unit) : ParallaxContent()

    /**
     * Content laid out in a `LazyColumn`, for long lists.
     *
     * @param content The items to display.
     * @param config LazyColumn behavior (padding, arrangement, fling, overscroll).
     * @param lazyListState State to observe or control the list; one is remembered when null.
     */
    public data class Lazy(
        val content: LazyListScope.(Boolean) -> Unit,
        val config: LazyColumnConfig = LazyColumnConfig(),
        val lazyListState: LazyListState? = null
    ) : ParallaxContent()
}
