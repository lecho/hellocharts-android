package lecho.lib.hellocharts.gesture;

import android.content.Context;
import android.graphics.Point;
import android.graphics.Rect;
import android.support.v4.widget.ScrollerCompat;

import lecho.lib.hellocharts.computator.ChartComputator;
import lecho.lib.hellocharts.model.Viewport;

/**
 * Encapsulates scrolling functionality.
 */
public class ChartScroller {

    private Viewport scrollerStartViewport = new Viewport(); // Used only for zooms and flings
    private Point surfaceSizeBuffer = new Point();// Used for scroll and flings
    private ScrollerCompat scroller;

    public ChartScroller(Context context) {
        scroller = ScrollerCompat.create(context);
    }

    public boolean startScroll(ChartComputator computator) {
        scroller.abortAnimation();
        scrollerStartViewport.set(computator.getCurrentViewport());
        return true;
    }

    public boolean scroll(ChartComputator computator, float distanceX, float distanceY, ScrollResult scrollResult) {

        // Scrolling uses math based on the viewport (as opposed to math using pixels). Pixel offset is the offset in
        // screen pixels, while viewport offset is the offset within the current viewport. For additional
        // information on
        // surface sizes and pixel offsets, see the docs for {@link computeScrollSurfaceSize()}. For additional
        // information about the viewport, see the comments for {@link mCurrentViewport}.

        final Viewport maxViewport = computator.getMaximumViewport();
        final Viewport visibleViewport = computator.getVisibleViewport();
        final Viewport currentViewport = computator.getCurrentViewport();
        final Rect contentRect = computator.getContentRectMinusAllMargins();

        final boolean canScrollLeft = currentViewport.left > maxViewport.left;
        final boolean canScrollRight = currentViewport.right < maxViewport.right;
        final boolean canScrollTop = currentViewport.top < maxViewport.top;
        final boolean canScrollBottom = currentViewport.bottom > maxViewport.bottom;

        boolean canScrollX = false;
        boolean canScrollY = false;

        if (canScrollLeft && distanceX <= 0) {
            canScrollX = true;
        } else if (canScrollRight && distanceX >= 0) {
            canScrollX = true;
        }

        if (canScrollTop && distanceY <= 0) {
            canScrollY = true;
        } else if (canScrollBottom && distanceY >= 0) {
            canScrollY = true;
        }

        if (canScrollX || canScrollY) {

            computator.computeScrollSurfaceSize(surfaceSizeBuffer);

            float viewportOffsetX = distanceX * visibleViewport.width() / contentRect.width();
            float viewportOffsetY = -distanceY * visibleViewport.height() / contentRect.height();

            computator
                    .setViewportTopLeft(currentViewport.left + viewportOffsetX, currentViewport.top + viewportOffsetY);
        }

        scrollResult.canScrollX = canScrollX;
        scrollResult.canScrollY = canScrollY;

        return canScrollX || canScrollY;
    }

    public boolean computeScrollOffset(ChartComputator computator) {
        if (scroller.computeScrollOffset()) {
            // The scroller isn't finished, meaning a fling or programmatic pan operation is
            // currently active.

            final Viewport maxViewport = computator.getMaximumViewport();

            computator.computeScrollSurfaceSize(surfaceSizeBuffer);

            final float currXRange = maxViewport.left + maxViewport.width() * scroller.getCurrX() /
                    surfaceSizeBuffer.x;
            final float currYRange = maxViewport.top - maxViewport.height() * scroller.getCurrY() /
                    surfaceSizeBuffer.y;

            computator.setViewportTopLeft(currXRange, currYRange);

            return true;
        }

        return false;
    }

    public boolean fling(int velocityX, int velocityY, ChartComputator computator) {
        // Flings use math in pixels (as opposed to math based on the viewport).
        computator.computeScrollSurfaceSize(surfaceSizeBuffer);
        scrollerStartViewport.set(computator.getCurrentViewport());

        int startX = (int) (surfaceSizeBuffer.x * (scrollerStartViewport.left - computator.getMaximumViewport().left)
                / computator.getMaximumViewport().width());
        int startY = (int) (surfaceSizeBuffer.y * (computator.getMaximumViewport().top - scrollerStartViewport.top) /
                computator.getMaximumViewport().height());

        // TODO probably should be mScroller.forceFinish but ScrollerCompat doesn't have that method.
        scroller.abortAnimation();

        final int width = computator.getContentRectMinusAllMargins().width();
        final int height = computator.getContentRectMinusAllMargins().height();
        scroller.fling(startX, startY, velocityX, velocityY, 0, surfaceSizeBuffer.x - width + 1, 0,
                surfaceSizeBuffer.y - height + 1);
        return true;
    }

    public static class ScrollResult {
        public boolean canScrollX;
        public boolean canScrollY;
    }

}
