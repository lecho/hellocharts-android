package lecho.lib.hellocharts.gesture;

import lecho.lib.hellocharts.Chart;
import lecho.lib.hellocharts.ChartCalculator;
import lecho.lib.hellocharts.renderer.ChartRenderer;
import android.content.Context;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;

public class ChartTouchHandler {
	protected GestureDetector gestureDetector;
	protected ScaleGestureDetector scaleGestureDetector;
	protected ChartScroller chartScroller;
	protected ChartZoomer chartZoomer;
	protected Chart chart;
	// TODO: consider using dummy zoomer and scroller instead of boolean flags
	protected boolean isInteractive = true;
	protected boolean isZoomEnabled = true;
	protected boolean isValueTouchEnabled = true;
	protected boolean isValueSelectionEnabled = false;

	public ChartTouchHandler(Context context, Chart chart) {
		this.chart = chart;
		gestureDetector = new GestureDetector(context, new ChartGestureListener());
		scaleGestureDetector = new ScaleGestureDetector(context, new ChartScaleGestureListener());
		chartScroller = new ChartScroller(context);
		chartZoomer = new ChartZoomer(context, ChartZoomer.ZOOM_HORIZONTAL_AND_VERTICAL);
	}

	/**
	 * Using first approach of fling animation described here {@link http
	 * ://developer.android.com/training/custom-views/making-interactive.html}. Consider use of second option with
	 * ValueAnimator.
	 * 
	 * @return
	 */
	public boolean computeScroll() {
		if (!isInteractive) {
			return false;
		}
		final ChartCalculator chartCalculator = chart.getChartCalculator();
		boolean needInvalidate = false;
		if (isZoomEnabled && chartScroller.computeScrollOffset(chartCalculator)) {
			needInvalidate = true;
		}
		if (isZoomEnabled && chartZoomer.computeZoom(chartCalculator)) {
			needInvalidate = true;
		}
		return needInvalidate;
	}

	public boolean handleTouchEvent(MotionEvent event) {
		if (!isInteractive) {
			return false;
		}
		boolean needInvalidate = false;
		if (isZoomEnabled) {
			// TODO: What the heck, why detectors onTouchEvent() always return true?
			needInvalidate = scaleGestureDetector.onTouchEvent(event);
			needInvalidate = gestureDetector.onTouchEvent(event) || needInvalidate;
		}
		if (isValueTouchEnabled) {
			needInvalidate = computeTouch(event) || needInvalidate;
		}
		return needInvalidate;
	}

	private boolean computeTouch(MotionEvent event) {
		boolean needInvalidate = false;
		final ChartRenderer chartRenderer = chart.getChartRenderer();
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			needInvalidate = chart.getChartRenderer().checkTouch(event.getX(), event.getY());
			break;
		case MotionEvent.ACTION_UP:
			if (chartRenderer.isTouched()) {
				if (chartRenderer.checkTouch(event.getX(), event.getY())) {
					chartRenderer.callChartTouchListener();
					if (!isValueSelectionEnabled) {
						chartRenderer.clearTouch();
					}
				} else {
					chartRenderer.clearTouch();
				}
				needInvalidate = true;
			}
			break;
		case MotionEvent.ACTION_MOVE:
			// If value was touched and now touch point is outside of value area - clear touch and invalidate, user
			// probably moved finger away from point without leaving finger of the screen surface
			if (chartRenderer.isTouched()) {
				if (!chartRenderer.checkTouch(event.getX(), event.getY())) {
					chartRenderer.clearTouch();
					needInvalidate = true;
				}
			}
			break;
		case MotionEvent.ACTION_CANCEL:
			if (chartRenderer.isTouched()) {
				chartRenderer.clearTouch();
				needInvalidate = true;
			}
			break;
		}
		return needInvalidate;
	}

	public boolean isInteractive() {
		return isInteractive;
	}

	public void setInteractive(boolean isInteractive) {
		this.isInteractive = isInteractive;
	}

	public void setZoomEnabled(boolean isZoomEnabled) {
		this.isZoomEnabled = isZoomEnabled;

	}

	public boolean isZoomEnabled() {
		return isZoomEnabled;
	}

	public void setZoomType(int zoomType) {
		chartZoomer.setZoomType(zoomType);
	}

	public int getZoomType() {
		return chartZoomer.getZoomType();
	}

	public boolean isValueTouchEnabled() {
		return isValueTouchEnabled;
	}

	public void setValueTouchEnabled(boolean isValueTouchEnabled) {
		this.isValueTouchEnabled = isValueTouchEnabled;
	}

	public boolean isValueSelectionEnabled() {
		return isValueSelectionEnabled;
	}

	public void setValueSelectionEnabled(boolean isValueSelectionEnabled) {
		this.isValueSelectionEnabled = isValueSelectionEnabled;
	}

	public void startZoom(float x, float y, float zoom) {
		chartZoomer.startZoom(x, y, zoom, chart.getChartCalculator());

	}

	private class ChartScaleGestureListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {

		@Override
		public boolean onScale(ScaleGestureDetector detector) {
			float scale = 2.0f - detector.getScaleFactor();
			if (Float.isInfinite(scale)) {
				scale = 1;
			}
			return chartZoomer.scale(chart.getChartCalculator(), detector.getFocusX(), detector.getFocusY(), scale);
		}
	}

	private class ChartGestureListener extends GestureDetector.SimpleOnGestureListener {
		@Override
		public boolean onDown(MotionEvent e) {
			return chartScroller.startScroll(chart.getChartCalculator());
		}

		@Override
		public boolean onDoubleTap(MotionEvent e) {
			return chartZoomer.startZoom(e, chart.getChartCalculator());
		}

		@Override
		public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
			return chartScroller.scroll(distanceX, distanceY, chart.getChartCalculator());
		}

		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
			return chartScroller.fling((int) -velocityX, (int) -velocityY, chart.getChartCalculator());
		}
	}

}
