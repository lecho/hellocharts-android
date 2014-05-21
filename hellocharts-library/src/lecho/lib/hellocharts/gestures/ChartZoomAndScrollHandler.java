package lecho.lib.hellocharts.gestures;

import lecho.lib.hellocharts.Chart;
import lecho.lib.hellocharts.ChartCalculator;
import lecho.lib.hellocharts.OnPointClickListener;
import android.content.Context;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;

public class ChartZoomAndScrollHandler {

	private int mSelectedLineIndex = Integer.MIN_VALUE;
	private int mSelectedPointIndex = Integer.MIN_VALUE;
	private GestureDetector mGestureDetector;
	private ScaleGestureDetector mScaleGestureDetector;
	private ChartScroller mChartScroller;
	private ChartZoomer mChartZoomer;
	private Chart mChart;

	public ChartZoomAndScrollHandler(Context context, Chart chart) {
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
		// switch (event.getAction()) {
		// case MotionEvent.ACTION_DOWN:
		// // Only one point will be selected even if there are few point in touch area.
		// int lineIndex = 0;
		// for (Line line : data.lines) {
		// int valueIndex = 0;
		// for (AnimatedPoint animatedPoint : line.animatedPoints) {
		// final float rawX = chartCalculator.calculateRawX(animatedPoint.point.x);
		// final float rawY = chartCalculator.calculateRawY(animatedPoint.point.y);
		// if (mChart.getChartRenderer().isInArea(rawX, rawY, event.getX(), event.getY())) {
		// mSelectedLineIndex = lineIndex;
		// mSelectedPointIndex = valueIndex;
		// needInvalidate = true;
		// }
		// ++valueIndex;
		// }
		// ++lineIndex;
		// }
		// break;
		// case MotionEvent.ACTION_UP:
		// // If value was selected call click listener and clear selection.
		// if (mSelectedPointIndex >= 0) {
		// final Line line = data.lines.get(mSelectedLineIndex);
		// final AnimatedPoint animatedPoint = line.animatedPoints.get(mSelectedPointIndex);
		// mOnPointClickListener.onPointClick(mSelectedLineIndex, mSelectedPointIndex, animatedPoint.point.x,
		// animatedPoint.point.y);
		// mSelectedLineIndex = Integer.MIN_VALUE;
		// mSelectedPointIndex = Integer.MIN_VALUE;
		// needInvalidate = true;
		// }
		// break;
		// case MotionEvent.ACTION_MOVE:
		// // Clear selection if user is now touching outside touch area.
		// if (mSelectedPointIndex >= 0) {
		// final Line line = data.lines.get(mSelectedLineIndex);
		// final AnimatedPoint animatedPoint = line.animatedPoints.get(mSelectedPointIndex);
		// final float rawX = chartCalculator.calculateRawX(animatedPoint.point.x);
		// final float rawY = chartCalculator.calculateRawY(animatedPoint.point.y);
		// if (mChart.getChartRenderer().isInArea(rawX, rawY, event.getX(), event.getY())) {
		// mSelectedLineIndex = Integer.MIN_VALUE;
		// mSelectedPointIndex = Integer.MIN_VALUE;
		// needInvalidate = true;
		// }
		// }
		// break;
		// case MotionEvent.ACTION_CANCEL:
		// // Clear selection
		// if (mSelectedPointIndex >= 0) {
		// mSelectedLineIndex = Integer.MIN_VALUE;
		// mSelectedPointIndex = Integer.MIN_VALUE;
		// needInvalidate = true;
		// }
		// break;
		// }
		return needInvalidate;
	}

	public int getSelectedLineIndex() {
		return mSelectedLineIndex;
	}

	public int getSelectedPointIndex() {
		return mSelectedPointIndex;
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
