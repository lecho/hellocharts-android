package lecho.lib.hellocharts.gesture;

import lecho.lib.hellocharts.ChartCalculator;
import android.content.Context;
import android.graphics.PointF;
import android.graphics.RectF;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;

public class ChartZoomer {
	public static final int ZOOM_HORIZONTAL_AND_VERTICAL = 1;
	public static final int ZOOM_HORIZONTAL = 2;
	public static final int ZOOM_VERTICAL = 3;
	public static final float ZOOM_AMOUNT = 0.25f;
	private ZoomerCompat zoomer;
	private int zoomType;
	private PointF zoomFocalPoint = new PointF();// Used for double tap zoom
	private PointF viewportFocus = new PointF();
	private RectF scrollerStartViewport = new RectF(); // Used only for zooms and flings

	public ChartZoomer(Context context, int zoomType) {
		zoomer = new ZoomerCompat(context);
		this.zoomType = zoomType;
	}

	public boolean startZoom(MotionEvent e, ChartCalculator chartCalculator) {
		zoomer.forceFinished(true);
		scrollerStartViewport.set(chartCalculator.mCurrentViewport);
		if (chartCalculator.rawPixelsToDataPoint(e.getX(), e.getY(), zoomFocalPoint)) {
			zoomer.startZoom(ZOOM_AMOUNT);
		}
		return true;
	}

	public boolean startZoom(float x, float y, float zoom, ChartCalculator chartCalculator) {
		zoomer.forceFinished(true);
		scrollerStartViewport.set(chartCalculator.mCurrentViewport);
		zoomFocalPoint.set(x, y);
		zoomer.startZoom(zoom);
		return true;
	}

	public boolean computeZoom(ChartCalculator chartCalculator) {
		if (zoomer.computeZoom()) {
			// Performs the zoom since a zoom is in progress (either programmatically or via
			// double-touch).
			final float newWidth = (1.0f - zoomer.getCurrZoom()) * scrollerStartViewport.width();
			final float newHeight = (1.0f - zoomer.getCurrZoom()) * scrollerStartViewport.height();
			final float pointWithinViewportX = (zoomFocalPoint.x - scrollerStartViewport.left)
					/ scrollerStartViewport.width();
			final float pointWithinViewportY = (zoomFocalPoint.y - scrollerStartViewport.top)
					/ scrollerStartViewport.height();

			float left = zoomFocalPoint.x - newWidth * pointWithinViewportX;
			float top = zoomFocalPoint.y - newHeight * pointWithinViewportY;
			float right = zoomFocalPoint.x + newWidth * (1 - pointWithinViewportX);
			float bottom = zoomFocalPoint.y + newHeight * (1 - pointWithinViewportY);
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
		chartCalculator.rawPixelsToDataPoint(focusX, focusY, viewportFocus);

		float left = viewportFocus.x - (focusX - chartCalculator.mContentRect.left)
				* (newWidth / chartCalculator.mContentRect.width());
		float top = viewportFocus.y - (chartCalculator.mContentRect.bottom - focusY)
				* (newHeight / chartCalculator.mContentRect.height());
		float right = left + newWidth;
		float bottom = top + newHeight;
		setCurrentViewport(chartCalculator, left, top, right, bottom);
		return true;
	}

	private void setCurrentViewport(ChartCalculator chartCalculator, float left, float top, float right, float bottom) {
		if (zoomType == ZOOM_HORIZONTAL_AND_VERTICAL || zoomType == ZOOM_HORIZONTAL) {
			chartCalculator.mCurrentViewport.left = left;
			chartCalculator.mCurrentViewport.right = right;
		}
		if (zoomType == ZOOM_HORIZONTAL_AND_VERTICAL || zoomType == ZOOM_VERTICAL) {
			chartCalculator.mCurrentViewport.top = top;
			chartCalculator.mCurrentViewport.bottom = bottom;
		}
		chartCalculator.constrainViewport();
	}

	public int getZoomType() {
		return zoomType;
	}

	public void setZoomType(int zoomType) {
		this.zoomType = zoomType;
	}
}
