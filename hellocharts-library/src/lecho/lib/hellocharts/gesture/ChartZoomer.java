package lecho.lib.hellocharts.gesture;

import lecho.lib.hellocharts.ChartCalculator;
import android.content.Context;
import android.graphics.PointF;
import android.graphics.RectF;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;

public class ChartZoomer {
	public static final float ZOOM_AMOUNT = 0.25f;
	private ZoomerCompat mZoomer;
	private ZoomMode mZoomMode;
	private PointF mZoomFocalPoint = new PointF();// Used for double tap zoom
	private PointF mViewportFocus = new PointF();
	private RectF mScrollerStartViewport = new RectF(); // Used only for zooms and flings

	public ChartZoomer(Context context, ZoomMode zoomType) {
		mZoomer = new ZoomerCompat(context);
		mZoomMode = zoomType;
	}

	public boolean startZoom(MotionEvent e, ChartCalculator chartCalculator) {
		mZoomer.forceFinished(true);
		mScrollerStartViewport.set(chartCalculator.mCurrentViewport);
		if (chartCalculator.rawPixelsToDataPoint(e.getX(), e.getY(), mZoomFocalPoint)) {
			mZoomer.startZoom(ZOOM_AMOUNT);
		}
		return true;
	}

	public boolean startZoom(float x, float y, float zoom, ChartCalculator chartCalculator) {
		mZoomer.forceFinished(true);
		mScrollerStartViewport.set(chartCalculator.mCurrentViewport);
		mZoomFocalPoint.set(x, y);
		mZoomer.startZoom(zoom);
		return true;
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

	public boolean scale(ScaleGestureDetector detector, ChartCalculator chartCalculator) {
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
		float right = left + newWidth;
		float bottom = top + newHeight;
		setCurrentViewport(chartCalculator, left, top, right, bottom);
		return true;
	}

	private void setCurrentViewport(ChartCalculator chartCalculator, float left, float top, float right, float bottom) {
		if (mZoomMode.equals(ZoomMode.HORIZONTAL_AND_VERTICAL) || mZoomMode.equals(ZoomMode.HORIZONTAL)) {
			chartCalculator.mCurrentViewport.left = left;
			chartCalculator.mCurrentViewport.right = right;
		}
		if (mZoomMode.equals(ZoomMode.HORIZONTAL_AND_VERTICAL) || mZoomMode.equals(ZoomMode.VERTICAL)) {
			chartCalculator.mCurrentViewport.top = top;
			chartCalculator.mCurrentViewport.bottom = bottom;
		}
		chartCalculator.constrainViewport();
	}

	public ZoomMode getZoomMode() {
		return mZoomMode;
	}

	public void setZoomMode(ZoomMode zoomMode) {
		this.mZoomMode = zoomMode;
	}
}
