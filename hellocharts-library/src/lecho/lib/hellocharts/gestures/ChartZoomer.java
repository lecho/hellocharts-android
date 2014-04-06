package lecho.lib.hellocharts.gestures;

import lecho.lib.hellocharts.ChartCalculator;
import android.content.Context;
import android.graphics.PointF;
import android.graphics.RectF;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;

public class ChartZoomer {
	private static final float ZOOM_AMOUNT = 0.25f;
	private ZoomerCompat mZoomer;
	private PointF mZoomFocalPoint = new PointF();// Used for double tap zoom
	private PointF mViewportFocus = new PointF();
	public RectF mScrollerStartViewport = new RectF(); // Used only for zooms and flings

	public ChartZoomer(Context context) {
		mZoomer = new ZoomerCompat(context);
	}

	public void startZoom(MotionEvent e, ChartCalculator chartCalculator) {
		mZoomer.forceFinished(true);
		mScrollerStartViewport.set(chartCalculator.mCurrentViewport);
		if (chartCalculator.rawPixelsToDataPoint(e.getX(), e.getY(), mZoomFocalPoint)) {
			mZoomer.startZoom(ZOOM_AMOUNT);
		}
	}

	public boolean computeZoom(ChartCalculator chartCalculator) {
		if (mZoomer.computeZoom()) {
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

	public void scale(ChartCalculator chartCalculator, ScaleGestureDetector detector) {
		/**
		 * Smaller viewport means bigger zoom so for zoomIn scale should have value <1, for zoomOout >1
		 */
		float scale = 2.0f - detector.getScaleFactor();
		final float newWidth = scale * chartCalculator.mCurrentViewport.width();
		final float newHeight = scale * chartCalculator.mCurrentViewport.height();
		final float focusX = detector.getFocusX();
		final float focusY = detector.getFocusY();
		chartCalculator.rawPixelsToDataPoint(focusX, focusY, mViewportFocus);
		chartCalculator.mCurrentViewport.left = mViewportFocus.x - (focusX - chartCalculator.mContentRect.left)
				* (newWidth / chartCalculator.mContentRect.width());
		chartCalculator.mCurrentViewport.top = mViewportFocus.y - (chartCalculator.mContentRect.bottom - focusY)
				* (newHeight / chartCalculator.mContentRect.height());
		chartCalculator.mCurrentViewport.right = chartCalculator.mCurrentViewport.left + newWidth;
		chartCalculator.mCurrentViewport.bottom = chartCalculator.mCurrentViewport.top + newHeight;
		chartCalculator.constrainViewport();
	}

}
