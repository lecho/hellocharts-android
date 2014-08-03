package lecho.lib.hellocharts.gesture;

import lecho.lib.hellocharts.ChartCalculator;
import lecho.lib.hellocharts.model.Viewport;
import android.content.Context;
import android.graphics.Point;
import android.support.v4.widget.ScrollerCompat;

public class ChartScroller {

	private Viewport scrollerStartViewport = new Viewport(); // Used only for zooms and flings
	private Point surfaceSizeBuffer = new Point();// Used for scroll and flings
	private ScrollerCompat scroller;

	public ChartScroller(Context context) {
		scroller = ScrollerCompat.create(context);
	}

	public boolean startScroll(ChartCalculator chartCalculator) {
		scroller.abortAnimation();
		scrollerStartViewport.set(chartCalculator.getCurrentViewport());
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
		float viewportOffsetX = distanceX * chartCalculator.getCurrentViewport().width()
				/ chartCalculator.getContentRect().width();
		float viewportOffsetY = -distanceY * chartCalculator.getCurrentViewport().height()
				/ chartCalculator.getContentRect().height();
		chartCalculator.computeScrollSurfaceSize(surfaceSizeBuffer);
		chartCalculator.setViewportTopLeft(chartCalculator.getCurrentViewport().left + viewportOffsetX,
				chartCalculator.getCurrentViewport().top + viewportOffsetY);
		return true;
	}

	public boolean computeScrollOffset(ChartCalculator chartCalculator) {
		if (scroller.computeScrollOffset()) {
			// The scroller isn't finished, meaning a fling or programmatic pan operation is
			// currently active.
			chartCalculator.computeScrollSurfaceSize(surfaceSizeBuffer);
			float currXRange = chartCalculator.getMaximumViewport().left + chartCalculator.getMaximumViewport().width()
					* scroller.getCurrX() / surfaceSizeBuffer.x;
			float currYRange = chartCalculator.getMaximumViewport().top - chartCalculator.getMaximumViewport().height()
					* scroller.getCurrY() / surfaceSizeBuffer.y;
			chartCalculator.setViewportTopLeft(currXRange, currYRange);
			return true;
		}
		return false;
	}

	public boolean fling(int velocityX, int velocityY, ChartCalculator chartCalculator) {
		// Flings use math in pixels (as opposed to math based on the viewport).
		chartCalculator.computeScrollSurfaceSize(surfaceSizeBuffer);
		scrollerStartViewport.set(chartCalculator.getCurrentViewport());
		int startX = (int) (surfaceSizeBuffer.x
				* (scrollerStartViewport.left - chartCalculator.getMaximumViewport().left) / chartCalculator
				.getMaximumViewport().width());
		int startY = (int) (surfaceSizeBuffer.y
				* (chartCalculator.getMaximumViewport().top - scrollerStartViewport.top) / chartCalculator
				.getMaximumViewport().height());
		// TODO probably should be mScroller.forceFinish but ScrollerCompat doesn't have that method.
		scroller.abortAnimation();
		scroller.fling(startX, startY, velocityX, velocityY, 0, surfaceSizeBuffer.x
				- chartCalculator.getContentRect().width(), 0, surfaceSizeBuffer.y
				- chartCalculator.getContentRect().height(), chartCalculator.getContentRect().width() / 2,
				chartCalculator.getContentRect().height() / 2);
		return true;
	}

}
