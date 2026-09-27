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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

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

/** Quiet time after the last wheel or trackpad delta before the header settles. */
private const val WheelSettleDelayMs = 150L

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

    /** How far the header may stretch past its expanded height, in px. 0 disables stretching. */
    var stretchMaxPx: Float by mutableFloatStateOf(0f)

    /** Current stretch past the expanded height, in px. Only non-zero while fully expanded. */
    var stretchPx: Float by mutableFloatStateOf(0f)
        private set

    /** Distance a released stretch must reach to fire [onStretchTrigger]. */
    var stretchTriggerPx: Float = 0f

    /** Called on release once a stretch reached [stretchTriggerPx]. */
    var onStretchTrigger: (() -> Unit)? = null

    /** Spec for snaps, stretch releases and programmatic moves. Set by the layout. */
    var animationSpec: AnimationSpec<Float> = spring()

    /** Progress within a segment at or past which a release without velocity settles forward. */
    var snapThreshold: Float = 0.5f

    /** The header layer's parallax multiplier, so pinned elements can cancel it. Set by the layout. */
    var parallaxMultiplier: Float = ParallaxToolbarDefaults.HeaderParallaxMultiplier

    /**
     * Whether a pointer is currently pressed on the layout. A stretch must follow a held finger
     * or mouse button: wheel and trackpad scrolling also arrive as user input but never fling, so
     * nothing would release a stretch they started.
     */
    var isPointerPressed: Boolean = false

    /** Whether releases settle to a resting position. Set by the layout. */
    var snapOnRelease: Boolean = false

    /** Scope for the settle that follows wheel scrolling. Set by the layout. */
    var settleScope: CoroutineScope? = null
    private var wheelSettleJob: Job? = null

    /**
     * Wheel and trackpad scrolling never fling, so nothing releases them. Any user delta that
     * arrives without a pressed pointer restarts a short timer; when it runs out the header
     * settles as it would after a touch release.
     */
    /** A fling settles on its own; drop a pending wheel settle so it cannot cut the fling short. */
    fun cancelWheelSettle() {
        wheelSettleJob?.cancel()
        wheelSettleJob = null
    }

    private fun noteWheelInput() {
        if (isPointerPressed || !snapOnRelease) return
        val scope = settleScope ?: return
        wheelSettleJob?.cancel()
        wheelSettleJob = scope.launch {
            delay(WheelSettleDelayMs)
            settle()
        }
    }

    val offsetPx: Float
        get() = fraction * collapseRangePx

    val exitOffsetPx: Float
        get() = exitFraction * exitRangePx

    val isCollapsed: Boolean
        get() = fraction >= 1f

    private val scrollScope = object : ScrollScope {
        // Internal animations bypass the wheel timer; only user deltas restart it.
        override fun scrollBy(pixels: Float): Float = dispatch(pixels, allowHeaderExpand = true, allowStretch = isPointerPressed)
    }
    private val mutex = MutatorMutex()

    /** Consumes [delta] into the header, then the exit. Returns the amount actually consumed. */
    override fun dispatchRawDelta(delta: Float): Float {
        noteWheelInput()
        return dispatch(delta, allowHeaderExpand = true, allowStretch = isPointerPressed)
    }

    /** Like [dispatchRawDelta] but never expands the header; used while the body is not at its top. */
    fun dispatchExitOnly(delta: Float): Float = dispatch(delta, allowHeaderExpand = false, allowStretch = false)

    /**
     * Consumes [delta] with stretching gated by [allowStretch]. Stretch must follow the finger
     * only: flings and platform overscroll bounces also arrive as nested scroll and would
     * otherwise keep pulling after release.
     */
    fun dispatchRawDelta(delta: Float, allowStretch: Boolean): Float =
        dispatch(delta, allowHeaderExpand = true, allowStretch = allowStretch && isPointerPressed)

    private fun dispatch(delta: Float, allowHeaderExpand: Boolean, allowStretch: Boolean): Float {
        var remaining = delta
        if (remaining > 0f) {
            remaining -= moveStretch(remaining)
            remaining -= moveCollapse(remaining)
            remaining -= moveExit(remaining)
        } else if (remaining < 0f) {
            remaining -= moveExit(remaining)
            if (allowHeaderExpand) {
                remaining -= moveCollapse(remaining)
                if (allowStretch) remaining -= moveStretch(remaining)
            }
        }
        return delta - remaining
    }

    /** Stretch consumes with resistance: it moves by half the delta but reports it all consumed. */
    private fun moveStretch(delta: Float): Float {
        if (stretchMaxPx <= 0f) return 0f
        if (delta < 0f && (fraction > 0f || exitFraction > 0f)) return 0f
        val before = stretchPx
        if (delta > 0f && before <= 0f) return 0f
        val resistance = if (delta < 0f) ParallaxToolbarDefaults.StretchResistance else 1f
        val after = (before - delta * resistance).coerceIn(0f, stretchMaxPx)
        stretchPx = after
        val moved = before - after
        return if (delta < 0f) moved / resistance else moved
    }

    /** Springs a stretch back and fires the trigger when it was pulled far enough. */
    suspend fun releaseStretch() {
        val start = stretchPx
        if (start <= 0f) return
        val triggered = stretchTriggerPx > 0f && start >= stretchTriggerPx
        scroll {
            animate(initialValue = start, targetValue = 0f, animationSpec = animationSpec) { value, _ ->
                stretchPx = value
            }
            stretchPx = 0f
        }
        if (triggered) onStretchTrigger?.invoke()
    }

    /** Same as [releaseStretch] from inside a running scroll scope. */
    suspend fun releaseStretchIn() {
        val start = stretchPx
        if (start <= 0f) return
        val triggered = stretchTriggerPx > 0f && start >= stretchTriggerPx
        animate(initialValue = start, targetValue = 0f, animationSpec = animationSpec) { value, _ ->
            stretchPx = value
        }
        stretchPx = 0f
        if (triggered) onStretchTrigger?.invoke()
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

    override val canScrollForward: Boolean get() = stretchPx > 0f || fraction < 1f || (exitRangePx > 0f && exitFraction < 1f)
    override val canScrollBackward: Boolean get() = fraction > 0f || exitFraction > 0f || (stretchMaxPx > 0f && stretchPx < stretchMaxPx)

    /** Animates the header to [target] and brings the toolbar back. Jumps before the layout has measured. */
    suspend fun animateFractionTo(target: Float, animationSpec: AnimationSpec<Float> = this.animationSpec) {
        val to = target.coerceIn(0f, 1f)
        if (collapseRangePx <= 0f) {
            fraction = to
            exitFraction = 0f
            return
        }
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
        // Within a hair of a rest: land on it exactly rather than leaving a rounding remainder.
        rests.firstOrNull { kotlin.math.abs(it - current) < 0.5f }?.let { return it }
        val below = rests.last { it < current }
        val above = rests.first { it > current }
        return when {
            velocityPx > 0f -> above
            velocityPx < 0f -> below
            else -> if ((current - below) / (above - below) >= snapThreshold) above else below
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
        animate(initialValue = current, targetValue = targetPx, animationSpec = animationSpec) { value, _ ->
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
            val fromFinger = source == NestedScrollSource.UserInput
            if (fromFinger) noteWheelInput()
            val consumed = when {
                // Scrolling up: the header (and then the toolbar) leaves before the body scrolls.
                delta > 0f -> dispatchRawDelta(delta)
                // Scrolling down: EnterAlways expands right away; EnterAlwaysCollapsed only brings
                // the toolbar back; ExitUntilCollapsed waits for the body to reach its top.
                delta < 0f -> when (mode) {
                    ScrollMode.EnterAlways -> dispatchRawDelta(delta, allowStretch = fromFinger)
                    ScrollMode.EnterAlwaysCollapsed -> dispatchExitOnly(delta)
                    ScrollMode.ExitUntilCollapsed -> 0f
                }
                else -> 0f
            }
            return Offset(0f, -consumed)
        }

        override fun onPostScroll(consumed: Offset, available: Offset, source: NestedScrollSource): Offset {
            // Lazy lists leave sub-pixel remainders from rounding even while they can still scroll.
            // Only a real leftover means the body is at its top and the header should expand. The
            // remainders are claimed so no parent scrollable acts on them either.
            if (kotlin.math.abs(available.y) < PostScrollThresholdPx) return Offset(0f, available.y)
            if (available.y < 0f) return Offset.Zero
            if (source == NestedScrollSource.UserInput) noteWheelInput()
            val used = dispatchRawDelta(-available.y, allowStretch = source == NestedScrollSource.UserInput)
            return Offset(0f, -used)
        }

        override suspend fun onPreFling(available: Velocity): Velocity {
            cancelWheelSettle()
            return Velocity.Zero
        }

        override suspend fun onPostFling(consumed: Velocity, available: Velocity): Velocity {
            cancelWheelSettle()
            releaseStretch()
            if (snapOnRelease) settle(velocityPx = -available.y)
            // The header already took its share frame by frame; claim the rest so the root
            // scrollable does not fling it with whatever velocity the body had left.
            return available
        }
    }
}
