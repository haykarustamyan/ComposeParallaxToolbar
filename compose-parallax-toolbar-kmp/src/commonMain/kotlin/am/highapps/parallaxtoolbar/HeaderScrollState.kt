package am.highapps.parallaxtoolbar

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.animate
import androidx.compose.animation.core.spring
import androidx.compose.foundation.MutatePriority
import androidx.compose.foundation.MutatorMutex
import androidx.compose.foundation.gestures.ScrollScope
import androidx.compose.foundation.gestures.ScrollableState
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.unit.Velocity

/**
 * How far the header has collapsed and, in [ScrollMode.EnterAlwaysCollapsed], how far the toolbar
 * has exited. Both are [ScrollableState] deltas so drags on the header, flings, nested scroll from
 * the body and programmatic animation all go through one clamped path.
 *
 * The canonical values are [fraction] and [exitFraction] (0f..1f); the px offsets derive from
 * them and the ranges the layout sets once it knows its heights. Storing fractions keeps the
 * visual state across rotation even when the heights change.
 *
 * Positive deltas collapse: first the header, then (when an exit range is set) the toolbar.
 * Negative deltas expand in the reverse order: the toolbar re-enters, then the header expands.
 */
private const val PostScrollThresholdPx = 0.5f

@Stable
internal class HeaderScrollState(initialFraction: Float, initialExitFraction: Float = 0f) : ScrollableState {

    var fraction: Float by mutableFloatStateOf(initialFraction.coerceIn(0f, 1f))
        private set

    var exitFraction: Float by mutableFloatStateOf(initialExitFraction.coerceIn(0f, 1f))
        private set

    /** Scroll distance, in px, between fully expanded and fully collapsed. Set by the layout. */
    var collapseRangePx: Float by mutableFloatStateOf(0f)

    /** Scroll distance, in px, for the toolbar to leave the screen. 0 unless the mode exits. */
    var exitRangePx: Float by mutableFloatStateOf(0f)

    val offsetPx: Float
        get() = fraction * collapseRangePx

    val exitOffsetPx: Float
        get() = exitFraction * exitRangePx

    val isCollapsed: Boolean
        get() = fraction >= 1f

    private val scrollScope = object : ScrollScope {
        override fun scrollBy(pixels: Float): Float = dispatchRawDelta(pixels)
    }
    private val mutex = MutatorMutex()

    /** Consumes [delta] into the header, then the exit. Returns the amount actually consumed. */
    override fun dispatchRawDelta(delta: Float): Float = dispatch(delta, allowHeaderExpand = true)

    /** Like [dispatchRawDelta] but never expands the header; used while the body is not at its top. */
    fun dispatchExitOnly(delta: Float): Float = dispatch(delta, allowHeaderExpand = false)

    private fun dispatch(delta: Float, allowHeaderExpand: Boolean): Float {
        var remaining = delta
        if (remaining > 0f) {
            remaining -= moveCollapse(remaining)
            remaining -= moveExit(remaining)
        } else if (remaining < 0f) {
            remaining -= moveExit(remaining)
            if (allowHeaderExpand) remaining -= moveCollapse(remaining)
        }
        return delta - remaining
    }

    private fun moveCollapse(delta: Float): Float {
        val range = collapseRangePx
        if (range <= 0f) return 0f
        val before = offsetPx
        val after = (before + delta).coerceIn(0f, range)
        fraction = after / range
        return after - before
    }

    private fun moveExit(delta: Float): Float {
        val range = exitRangePx
        if (range <= 0f) return 0f
        val before = exitOffsetPx
        val after = (before + delta).coerceIn(0f, range)
        exitFraction = after / range
        return after - before
    }

    override suspend fun scroll(scrollPriority: MutatePriority, block: suspend ScrollScope.() -> Unit) {
        mutex.mutateWith(scrollScope, scrollPriority) {
            isScrollInProgress = true
            try {
                block()
            } finally {
                isScrollInProgress = false
            }
        }
    }

    override var isScrollInProgress: Boolean by mutableStateOf(false)
        private set

    override val canScrollForward: Boolean get() = fraction < 1f || (exitRangePx > 0f && exitFraction < 1f)
    override val canScrollBackward: Boolean get() = fraction > 0f || exitFraction > 0f

    /** Animates the header to [target] and brings the toolbar back. Jumps before the layout has measured. */
    suspend fun animateFractionTo(target: Float, animationSpec: AnimationSpec<Float> = spring()) {
        val to = target.coerceIn(0f, 1f)
        if (collapseRangePx <= 0f) { fraction = to; exitFraction = 0f; return }
        scroll {
            val startExit = exitFraction
            val startFraction = fraction
            animate(initialValue = 0f, targetValue = 1f, animationSpec = animationSpec) { t, _ ->
                exitFraction = startExit * (1f - t)
                fraction = startFraction + (to - startFraction) * t
            }
            // Springs stop within a tolerance; land exactly so isCollapsed and 0f/1f checks hold.
            fraction = to
            exitFraction = 0f
        }
    }

    fun snapFractionTo(target: Float) {
        fraction = target.coerceIn(0f, 1f)
        exitFraction = 0f
    }

    /** Total travel in px, header then exit, and the rest positions a settle may land on. */
    private val totalOffsetPx: Float get() = offsetPx + exitOffsetPx
    private fun restingOffsets(): List<Float> =
        if (exitRangePx > 0f) listOf(0f, collapseRangePx, collapseRangePx + exitRangePx) else listOf(0f, collapseRangePx)

    /** Where a release should settle from the current position, biased by [velocityPx] when given. */
    private fun settleTargetPx(velocityPx: Float): Float? {
        val rests = restingOffsets()
        val current = totalOffsetPx
        if (rests.any { kotlin.math.abs(it - current) < 0.5f }) return null
        val below = rests.last { it < current }
        val above = rests.first { it > current }
        return when {
            velocityPx > 0f -> above
            velocityPx < 0f -> below
            else -> if (current - below <= above - current) below else above
        }
    }

    /** Settles to the nearest resting position, biased by [velocityPx] when given. */
    suspend fun settle(velocityPx: Float = 0f) {
        val targetPx = settleTargetPx(velocityPx) ?: return
        scroll { settleWithin(this, targetPx) }
    }

    /**
     * Same as [settle] but driven through an already running scroll [scope], for use from a
     * fling that holds the scroll mutex; starting a new [scroll] there would cancel the fling.
     */
    suspend fun settleIn(scope: ScrollScope, velocityPx: Float = 0f) {
        val targetPx = settleTargetPx(velocityPx) ?: return
        settleWithin(scope, targetPx)
    }

    private suspend fun settleWithin(scope: ScrollScope, targetPx: Float) {
        var current = totalOffsetPx
        animate(initialValue = current, targetValue = targetPx, animationSpec = spring()) { value, _ ->
            scope.scrollBy(value - current)
            current = value
        }
        scope.scrollBy(targetPx - current)
        // Land exactly on the rest position.
        if (collapseRangePx > 0f) fraction = (targetPx.coerceAtMost(collapseRangePx) / collapseRangePx).coerceIn(0f, 1f)
        if (exitRangePx > 0f) exitFraction = ((targetPx - collapseRangePx).coerceAtLeast(0f) / exitRangePx).coerceIn(0f, 1f)
    }

    /** Nested scroll connection implementing [mode]. */
    fun connection(mode: ScrollMode, snapOnRelease: Boolean): NestedScrollConnection = object : NestedScrollConnection {
        override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
            val delta = -available.y
            val consumed = when {
                // Scrolling up: the header (and then the toolbar) leaves before the body scrolls.
                delta > 0f -> dispatchRawDelta(delta)
                // Scrolling down: EnterAlways expands right away; EnterAlwaysCollapsed only brings
                // the toolbar back; ExitUntilCollapsed waits for the body to reach its top.
                delta < 0f -> when (mode) {
                    ScrollMode.EnterAlways -> dispatchRawDelta(delta)
                    ScrollMode.EnterAlwaysCollapsed -> dispatchExitOnly(delta)
                    ScrollMode.ExitUntilCollapsed -> 0f
                }
                else -> 0f
            }
            return Offset(0f, -consumed)
        }

        override fun onPostScroll(consumed: Offset, available: Offset, source: NestedScrollSource): Offset {
            // Lazy lists leave sub-pixel remainders from rounding even while they can still scroll.
            // Only a real leftover means the body is at its top and the header should expand.
            if (available.y < PostScrollThresholdPx) return Offset.Zero
            val used = dispatchRawDelta(-available.y)
            return Offset(0f, -used)
        }

        override suspend fun onPostFling(consumed: Velocity, available: Velocity): Velocity {
            if (snapOnRelease) settle(velocityPx = -available.y)
            return Velocity.Zero
        }
    }
}
