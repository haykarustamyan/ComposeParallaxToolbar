package am.highapps.parallaxtoolbar

/**
 * How the header and toolbar react to the body scrolling.
 */
public enum class ScrollMode {
    /**
     * Scrolling up collapses the header before the body scrolls. Scrolling down expands it only
     * once the body is back at its top. The toolbar stays pinned. This is the classic behavior.
     */
    ExitUntilCollapsed,

    /**
     * Scrolling up collapses the header. Scrolling down expands it immediately, wherever the body
     * is, so the header is always one gesture away. The toolbar stays pinned.
     */
    EnterAlways,

    /**
     * Scrolling up collapses the header and then slides the toolbar off screen too, giving the
     * body the whole viewport. Scrolling down brings the toolbar back immediately; the header
     * expands only once the body is back at its top.
     */
    EnterAlwaysCollapsed
}
