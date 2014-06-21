package lecho.lib.hellocharts.gestures;

import lecho.lib.hellocharts.ChartCalculator;
import android.content.Context;
import android.graphics.Point;
import android.graphics.RectF;
import android.support.v4.widget.ScrollerCompat;

public class ChartScroller {

	public RectF mScrollerStartViewport = new RectF(); // Used only for zooms and flings
	private Point mSurfaceSizeBuffer = new Point();// Used for scroll and flings
	public ScrollerCompat mScroller;

	public ChartScroller(Context context) {
		mScroller = ScrollerCompat.create(context);
	}

	public boolean startScroll(ChartCalculator chartCalculator) {
		mScroller.abortAnimation();
		mScrollerStartViewport.set(chartCalculator.mCurrentViewport);
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
		chartCalculator.computeScrollSurfaceSize(mSurfaceSizeBuffer);
		chartCalculator.setViewportBottomLeft(chartCalculator.mCurrentViewport.left + viewportOffsetX,
				chartCalculator.mCurrentViewport.bottom + viewportOffsetY);
		return true;
	}

	public boolean computeScrollOffset(ChartCalculator chartCalculator) {
		if (mScroller.computeScrollOffset()) {
			// The scroller isn't finished, meaning a fling or programmatic pan operation is
			// currently active.
			chartCalculator.computeScrollSurfaceSize(mSurfaceSizeBuffer);
			float currXRange = chartCalculator.mMaximumViewport.left + chartCalculator.mMaximumViewport.width()
					* mScroller.getCurrX() / mSurfaceSizeBuffer.x;
			float currYRange = chartCalculator.mMaximumViewport.bottom - chartCalculator.mMaximumViewport.height()
					* mScroller.getCurrY() / mSurfaceSizeBuffer.y;
			chartCalculator.setViewportBottomLeft(currXRange, currYRange);
			return true;
		}
		return false;
	}

	public boolean fling(int velocityX, int velocityY, ChartCalculator chartCalculator) {
		// Flings use math in pixels (as opposed to math based on the viewport).
		chartCalculator.computeScrollSurfaceSize(mSurfaceSizeBuffer);
		mScrollerStartViewport.set(chartCalculator.mCurrentViewport);
		int startX = (int) (mSurfaceSizeBuffer.x
				* (mScrollerStartViewport.left - chartCalculator.mMaximumViewport.left) / chartCalculator.mMaximumViewport
				.width());
		int startY = (int) (mSurfaceSizeBuffer.y
				* (chartCalculator.mMaximumViewport.bottom - mScrollerStartViewport.bottom) / chartCalculator.mMaximumViewport
				.height());
		mScroller.abortAnimation();// probably should be mScroller.forceFinish but compat doesn't have that method.
		mScroller.fling(startX, startY, velocityX, velocityY, 0,
				mSurfaceSizeBuffer.x - chartCalculator.mContentRect.width(), 0, mSurfaceSizeBuffer.y
						- chartCalculator.mContentRect.height(), chartCalculator.mContentRect.width() / 2,
				chartCalculator.mContentRect.height() / 2);
		return true;
	}

}
