package am.highapps.parallaxtoolbar

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.animate
import androidx.compose.animation.core.spring
import androidx.compose.foundation.MutatePriority
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
 * How far the header has collapsed, as a [ScrollableState] so drags on the header, flings,
 * nested scroll from the body and programmatic animation all go through one clamped value.
 *
 * The canonical value is [fraction] (0f expanded, 1f collapsed); [offsetPx] derives from it and
 * [collapseRangePx], which the layout sets once it knows the header and toolbar heights. Storing
 * the fraction keeps the visual state across rotation even when the header height changes.
 */
@Stable
internal class HeaderScrollState(initialFraction: Float) : ScrollableState {

    var fraction: Float by mutableFloatStateOf(initialFraction.coerceIn(0f, 1f))
        private set

    /** Scroll distance, in px, between fully expanded and fully collapsed. Set by the layout. */
    var collapseRangePx: Float by mutableFloatStateOf(0f)

    val offsetPx: Float
        get() = fraction * collapseRangePx

    val isCollapsed: Boolean
        get() = fraction >= 1f

    private val scrollScope = object : ScrollScope {
        override fun scrollBy(pixels: Float): Float = dispatchRawDelta(pixels)
    }
    private val mutex = androidx.compose.foundation.MutatorMutex()

    /** Positive delta collapses, negative expands. Returns the amount actually consumed. */
    override fun dispatchRawDelta(delta: Float): Float {
        val range = collapseRangePx
        if (range <= 0f) return 0f
        val before = offsetPx
        val after = (before + delta).coerceIn(0f, range)
        fraction = after / range
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

    override val canScrollForward: Boolean get() = fraction < 1f
    override val canScrollBackward: Boolean get() = fraction > 0f

    /** Animates [fraction] to [target]. Works before the layout has measured, by jumping. */
    suspend fun animateFractionTo(target: Float, animationSpec: AnimationSpec<Float> = spring()) {
        val to = target.coerceIn(0f, 1f)
        if (collapseRangePx <= 0f) { fraction = to; return }
        scroll {
            animate(initialValue = fraction, targetValue = to, animationSpec = animationSpec) { value, _ ->
                fraction = value
            }
            // Springs stop within a tolerance; land exactly so isCollapsed and 0f/1f checks hold.
            fraction = to
        }
    }

    fun snapFractionTo(target: Float) {
        fraction = target.coerceIn(0f, 1f)
    }

    /** Which end to settle to, biased by [velocityPx] when given. */
    private fun settleTarget(velocityPx: Float): Float = when {
        velocityPx > 0f -> 1f
        velocityPx < 0f -> 0f
        else -> if (fraction >= 0.5f) 1f else 0f
    }

    /** Settles to the nearest end, biased by [velocityPx] when given. */
    suspend fun settle(velocityPx: Float = 0f) {
        if (fraction <= 0f || fraction >= 1f) return
        animateFractionTo(settleTarget(velocityPx))
    }

    /**
     * Same as [settle] but driven through an already running scroll [scope], for use from a
     * fling that holds the scroll mutex; starting a new [scroll] there would cancel the fling.
     */
    suspend fun settleIn(scope: ScrollScope, velocityPx: Float = 0f) {
        if (fraction <= 0f || fraction >= 1f || collapseRangePx <= 0f) return
        val targetPx = settleTarget(velocityPx) * collapseRangePx
        var current = offsetPx
        animate(initialValue = current, targetValue = targetPx, animationSpec = spring()) { value, _ ->
            scope.scrollBy(value - current)
            current = value
        }
        scope.scrollBy(targetPx - current)
        fraction = (targetPx / collapseRangePx).coerceIn(0f, 1f)
    }

    /**
     * Exit-until-collapsed behavior: the header consumes upward scroll before the child, and
     * downward scroll only after the child has scrolled to its top.
     */
    fun connection(snapOnRelease: Boolean): NestedScrollConnection = object : NestedScrollConnection {
        override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
            if (available.y >= 0f) return Offset.Zero
            val consumed = dispatchRawDelta(-available.y)
            return Offset(0f, -consumed)
        }

        override fun onPostScroll(consumed: Offset, available: Offset, source: NestedScrollSource): Offset {
            if (available.y <= 0f) return Offset.Zero
            val used = dispatchRawDelta(-available.y)
            return Offset(0f, -used)
        }

        override suspend fun onPostFling(consumed: Velocity, available: Velocity): Velocity {
            if (snapOnRelease) settle(velocityPx = -available.y)
            return Velocity.Zero
        }
    }
}
