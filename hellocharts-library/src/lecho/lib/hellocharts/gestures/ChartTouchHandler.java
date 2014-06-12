package lecho.lib.hellocharts.gestures;

import lecho.lib.hellocharts.Chart;
import lecho.lib.hellocharts.ChartCalculator;
import lecho.lib.hellocharts.ChartRenderer;
import lecho.lib.hellocharts.OnPointClickListener;
import android.content.Context;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;

public class ChartTouchHandler {

	private GestureDetector mGestureDetector;
	private ScaleGestureDetector mScaleGestureDetector;
	private ChartScroller mChartScroller;
	private ChartZoomer mChartZoomer;
	private Chart mChart;

	public ChartTouchHandler(Context context, Chart chart) {
		mChart = chart;
		mGestureDetector = new GestureDetector(context, new ChartGestureListener());
		mScaleGestureDetector = new ScaleGestureDetector(context, new ChartScaleGestureListener());
		mChartScroller = new ChartScroller(context);
		mChartZoomer = new ChartZoomer(context, ChartZoomer.ZOOM_HORIZONTAL_AND_VERTICAL);
	}

	public boolean computeScroll() {
		final ChartCalculator chartCalculator = mChart.getChartCalculator();
		boolean needInvalidate = false;
		if (mChartScroller.computeScrollOffset(chartCalculator)) {
			needInvalidate = true;
		}
		if (mChartZoomer.computeZoom(chartCalculator)) {
			needInvalidate = true;
		}
		return needInvalidate;
	}

	public boolean handleTouchEvent(MotionEvent event) {
		boolean needInvalidate = mScaleGestureDetector.onTouchEvent(event);
		needInvalidate = mGestureDetector.onTouchEvent(event) || needInvalidate;
		final ChartRenderer chartRenderer = mChart.getChartRenderer();
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			needInvalidate = mChart.getChartRenderer().checkTouch(event.getX(), event.getY());
			break;
		case MotionEvent.ACTION_UP:
			if (chartRenderer.isTouched()) {
				chartRenderer.clearTouch();
				needInvalidate = true;
				// TODO: call touchListener!!!
			}
			break;
		case MotionEvent.ACTION_MOVE:
			// If value was touched and now touch point is outside of value area - clear touch and invalidate, user
			// probably moved finger from value without leaving surface
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
			}
			break;
		}
		return needInvalidate;
	}

	private class ChartScaleGestureListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {

		@Override
		public boolean onScale(ScaleGestureDetector detector) {
			mChartZoomer.scale(detector, mChart.getChartCalculator());
			return true;
		}
	}

	private class ChartGestureListener extends GestureDetector.SimpleOnGestureListener {
		@Override
		public boolean onDown(MotionEvent e) {
			mChartScroller.startScroll(mChart.getChartCalculator());
			return true;
		}

		@Override
		public boolean onDoubleTap(MotionEvent e) {
			mChartZoomer.startZoom(e, mChart.getChartCalculator());
			return true;
		}

		@Override
		public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
			mChartScroller.scroll(distanceX, distanceY, mChart.getChartCalculator());
			return true;
		}

		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
			mChartScroller.fling((int) -velocityX, (int) -velocityY, mChart.getChartCalculator());
			return true;
		}
	}

	// Just empty listener to avoid NPE checks.
	private static class DummyOnPointListener implements OnPointClickListener {

		@Override
		public void onPointClick(int selectedSeriesIndex, int selectedValueIndex, float x, float y) {
			// Do nothing.
		}

	}
}
