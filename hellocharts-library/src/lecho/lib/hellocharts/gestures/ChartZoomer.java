package lecho.lib.hellocharts.gestures;

import lecho.lib.hellocharts.ChartCalculator;
import android.content.Context;
import android.graphics.PointF;
import android.graphics.RectF;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;

public class ChartZoomer {
	public static final int ZOOM_HORIZONTAL_AND_VERTICAL = 0;
	public static final int ZOOM_HORIZONTAL = 1;
	public static final int ZOOM_VERTICAL = 2;
	private static final float ZOOM_AMOUNT = 0.25f;
	private ZoomerCompat mZoomer;
	private int mZoomType = ZOOM_HORIZONTAL_AND_VERTICAL;
	private PointF mZoomFocalPoint = new PointF();// Used for double tap zoom
	private PointF mViewportFocus = new PointF();
	public RectF mScrollerStartViewport = new RectF(); // Used only for zooms and flings

	public ChartZoomer(Context context, int zoomType) {
		mZoomer = new ZoomerCompat(context);
		mZoomType = zoomType;
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

			float left = mZoomFocalPoint.x - newWidth * pointWithinViewportX;
			float top = mZoomFocalPoint.y - newHeight * pointWithinViewportY;
			float right = mZoomFocalPoint.x + newWidth * (1 - pointWithinViewportX);
			float bottom = mZoomFocalPoint.y + newHeight * (1 - pointWithinViewportY);
			setCurrentViewport(chartCalculator, left, top, right, bottom);
			return true;
		}
		return false;
	}

	public void scale(ScaleGestureDetector detector, ChartCalculator chartCalculator) {
		/**
		 * Smaller viewport means bigger zoom so for zoomIn scale should have value <1, for zoomOout >1
		 */
		float scale = 2.0f - detector.getScaleFactor();
		final float newWidth = scale * chartCalculator.mCurrentViewport.width();
		final float newHeight = scale * chartCalculator.mCurrentViewport.height();
		final float focusX = detector.getFocusX();
		final float focusY = detector.getFocusY();
		chartCalculator.rawPixelsToDataPoint(focusX, focusY, mViewportFocus);

		float left = mViewportFocus.x - (focusX - chartCalculator.mContentRect.left)
				* (newWidth / chartCalculator.mContentRect.width());
		float top = mViewportFocus.y - (chartCalculator.mContentRect.bottom - focusY)
				* (newHeight / chartCalculator.mContentRect.height());
		float right = chartCalculator.mCurrentViewport.left + newWidth;
		float bottom = chartCalculator.mCurrentViewport.top + newHeight;
		setCurrentViewport(chartCalculator, left, top, right, bottom);
	}

	private void setCurrentViewport(ChartCalculator chartCalculator, float left, float top, float right, float bottom) {
		if (mZoomType == ZOOM_HORIZONTAL_AND_VERTICAL || mZoomType == ZOOM_HORIZONTAL) {
			chartCalculator.mCurrentViewport.left = left;
			chartCalculator.mCurrentViewport.right = right;
		}
		if (mZoomType == ZOOM_HORIZONTAL_AND_VERTICAL || mZoomType == ZOOM_VERTICAL) {
			chartCalculator.mCurrentViewport.top = top;
			chartCalculator.mCurrentViewport.bottom = bottom;
		}
		chartCalculator.constrainViewport();
	}

}
