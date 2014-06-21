package lecho.lib.hellocharts.gestures;

import lecho.lib.hellocharts.Chart;
import lecho.lib.hellocharts.ChartCalculator;
import lecho.lib.hellocharts.ChartRenderer;
import android.content.Context;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;

public class DefaultTouchHandler implements ChartTouchHandler {

	private GestureDetector mGestureDetector;
	private ScaleGestureDetector mScaleGestureDetector;
	private ChartScroller mChartScroller;
	private ChartZoomer mChartZoomer;
	private Chart mChart;
	// TODO: consider using dummy zoomer and scroller instead of boolean flags
	private boolean isZoomEnabled = true;
	private boolean isTouchEnabled = true;

	public DefaultTouchHandler(Context context, Chart chart) {
		mChart = chart;
		mGestureDetector = new GestureDetector(context, new ChartGestureListener());
		mScaleGestureDetector = new ScaleGestureDetector(context, new ChartScaleGestureListener());
		mChartScroller = new ChartScroller(context);
		mChartZoomer = new ChartZoomer(context, ZoomMode.HORIZONTAL_AND_VERTICAL);
	}

	public boolean computeScroll() {
		final ChartCalculator chartCalculator = mChart.getChartCalculator();
		boolean needInvalidate = false;
		if (isZoomEnabled && mChartScroller.computeScrollOffset(chartCalculator)) {
			needInvalidate = true;
		}
		if (isZoomEnabled && mChartZoomer.computeZoom(chartCalculator)) {
			needInvalidate = true;
		}
		return needInvalidate;
	}

	public boolean handleTouchEvent(MotionEvent event) {
		boolean needInvalidate = false;
		if (isZoomEnabled) {
			needInvalidate = mScaleGestureDetector.onTouchEvent(event);
			needInvalidate = mGestureDetector.onTouchEvent(event) || needInvalidate;
		}
		if (isTouchEnabled) {
			needInvalidate = computeTouch(event) || needInvalidate;
		}
		return needInvalidate;
	}

	private boolean computeTouch(MotionEvent event) {
		boolean needInvalidate = false;
		final ChartRenderer chartRenderer = mChart.getChartRenderer();
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			needInvalidate = mChart.getChartRenderer().checkTouch(event.getX(), event.getY());
			break;
		case MotionEvent.ACTION_UP:
			if (chartRenderer.isTouched()) {
				chartRenderer.callTouchListener();
				chartRenderer.clearTouch();
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

	@Override
	public void setZoomEnabled(boolean isZoomEnabled) {
		this.isZoomEnabled = isZoomEnabled;

	}

	@Override
	public void setZoomMode(ZoomMode zoomMode) {
		mChartZoomer.setZoomMode(zoomMode);
	}

	@Override
	public void setTouchEnabled(boolean isTouchEnable) {
		this.isTouchEnabled = isTouchEnable;
	}

	private class ChartScaleGestureListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {

		@Override
		public boolean onScale(ScaleGestureDetector detector) {
			return mChartZoomer.scale(detector, mChart.getChartCalculator());
		}
	}

	private class ChartGestureListener extends GestureDetector.SimpleOnGestureListener {
		@Override
		public boolean onDown(MotionEvent e) {
			return mChartScroller.startScroll(mChart.getChartCalculator());
		}

		@Override
		public boolean onDoubleTap(MotionEvent e) {
			return mChartZoomer.startZoom(e, mChart.getChartCalculator());
		}

		@Override
		public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
			return mChartScroller.scroll(distanceX, distanceY, mChart.getChartCalculator());
		}

		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
			return mChartScroller.fling((int) -velocityX, (int) -velocityY, mChart.getChartCalculator());
		}
	}

}
