package lecho.lib.hellocharts.gesture;

import lecho.lib.hellocharts.ChartCalculator;
import android.content.Context;
import android.graphics.Point;
import android.graphics.RectF;
import android.support.v4.widget.ScrollerCompat;

public class ChartScroller {

	private RectF scrollerStartViewport = new RectF(); // Used only for zooms and flings
	private Point surfaceSizeBuffer = new Point();// Used for scroll and flings
	private ScrollerCompat scroller;

	public ChartScroller(Context context) {
		scroller = ScrollerCompat.create(context);
	}

	public boolean startScroll(ChartCalculator chartCalculator) {
		scroller.abortAnimation();
		scrollerStartViewport.set(chartCalculator.mCurrentViewport);
		return true;
	}

	public boolean scroll(float distanceX, float distanceY, ChartCalculator chartCalculator) {
		// Scrolling uses math based on the viewport (as opposed to math using pixels).
		/**
		 * Pixel offset is the offset in screen pixels, while viewport offset is the offset within the current viewport.
		 * For additional information on surface sizes and pixel offsets, see the docs for {@link
		 * computeScrollSurfaceSize()}. For additional information about the viewport, see the comments for
		 * {@link mCurrentViewport}.
		 */
		float viewportOffsetX = distanceX * chartCalculator.mCurrentViewport.width()
				/ chartCalculator.mContentRect.width();
		float viewportOffsetY = -distanceY * chartCalculator.mCurrentViewport.height()
				/ chartCalculator.mContentRect.height();
		chartCalculator.computeScrollSurfaceSize(surfaceSizeBuffer);
		chartCalculator.setViewportBottomLeft(chartCalculator.mCurrentViewport.left + viewportOffsetX,
				chartCalculator.mCurrentViewport.bottom + viewportOffsetY);
		return true;
	}

	public boolean computeScrollOffset(ChartCalculator chartCalculator) {
		if (scroller.computeScrollOffset()) {
			// The scroller isn't finished, meaning a fling or programmatic pan operation is
			// currently active.
			chartCalculator.computeScrollSurfaceSize(surfaceSizeBuffer);
			float currXRange = chartCalculator.mMaximumViewport.left + chartCalculator.mMaximumViewport.width()
					* scroller.getCurrX() / surfaceSizeBuffer.x;
			float currYRange = chartCalculator.mMaximumViewport.bottom - chartCalculator.mMaximumViewport.height()
					* scroller.getCurrY() / surfaceSizeBuffer.y;
			chartCalculator.setViewportBottomLeft(currXRange, currYRange);
			return true;
		}
		return false;
	}

	public boolean fling(int velocityX, int velocityY, ChartCalculator chartCalculator) {
		// Flings use math in pixels (as opposed to math based on the viewport).
		chartCalculator.computeScrollSurfaceSize(surfaceSizeBuffer);
		scrollerStartViewport.set(chartCalculator.mCurrentViewport);
		int startX = (int) (surfaceSizeBuffer.x
				* (scrollerStartViewport.left - chartCalculator.mMaximumViewport.left) / chartCalculator.mMaximumViewport
				.width());
		int startY = (int) (surfaceSizeBuffer.y
				* (chartCalculator.mMaximumViewport.bottom - scrollerStartViewport.bottom) / chartCalculator.mMaximumViewport
				.height());
		scroller.abortAnimation();// probably should be mScroller.forceFinish but compat doesn't have that method.
		scroller.fling(startX, startY, velocityX, velocityY, 0,
				surfaceSizeBuffer.x - chartCalculator.mContentRect.width(), 0, surfaceSizeBuffer.y
						- chartCalculator.mContentRect.height(), chartCalculator.mContentRect.width() / 2,
				chartCalculator.mContentRect.height() / 2);
		return true;
	}

}
