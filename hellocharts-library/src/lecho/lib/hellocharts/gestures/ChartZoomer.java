package lecho.lib.hellocharts.gestures;

import lecho.lib.hellocharts.ChartCalculator;
import android.content.Context;
import android.graphics.PointF;
import android.graphics.RectF;
import android.view.MotionEvent;

public class ChartZoomer {
	private static final float ZOOM_AMOUNT = 0.25f;
	private ZoomerCompat mZoomer;
	private PointF mZoomFocalPoint = new PointF();// Used for double tap zoom

	public ChartZoomer(Context context) {
		mZoomer = new ZoomerCompat(context);
	}

	public void startZoom(MotionEvent e, ChartCalculator chartCalculator) {
		mZoomer.forceFinished(true);
		if (chartCalculator.rawPixelsToDataPoint(e.getX(), e.getY(), mZoomFocalPoint)) {
			mZoomer.startZoom(ZOOM_AMOUNT);
		}
	}

	public boolean computeZoom(ChartCalculator chartCalculator, ChartScroller chartScroller) {
		if (mZoomer.computeZoom()) {
			RectF mScrollerStartViewport = chartScroller.mScrollerStartViewport;
			// Performs the zoom since a zoom is in progress (either programmatically or via
			// double-touch).
			final float newWidth = (1.0f - mZoomer.getCurrZoom()) * mScrollerStartViewport.width();
			final float newHeight = (1.0f - mZoomer.getCurrZoom()) * mScrollerStartViewport.height();
			final float pointWithinViewportX = (mZoomFocalPoint.x - mScrollerStartViewport.left)
					/ mScrollerStartViewport.width();
			final float pointWithinViewportY = (mZoomFocalPoint.y - mScrollerStartViewport.top)
					/ mScrollerStartViewport.height();
			chartCalculator.mCurrentViewport.left = mZoomFocalPoint.x - newWidth * pointWithinViewportX;
			chartCalculator.mCurrentViewport.top = mZoomFocalPoint.y - newHeight * pointWithinViewportY;
			chartCalculator.mCurrentViewport.right = mZoomFocalPoint.x + newWidth * (1 - pointWithinViewportX);
			chartCalculator.mCurrentViewport.bottom = mZoomFocalPoint.y + newHeight * (1 - pointWithinViewportY);
			chartCalculator.constrainViewport();
			return true;
		}
		return false;
	}

}
