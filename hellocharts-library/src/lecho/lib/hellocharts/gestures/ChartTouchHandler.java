package lecho.lib.hellocharts.gestures;

import lecho.lib.hellocharts.ChartCalculator;
import lecho.lib.hellocharts.LineChart;
import lecho.lib.hellocharts.OnPointClickListener;
import lecho.lib.hellocharts.model.AnimatedPoint;
import lecho.lib.hellocharts.model.Data;
import lecho.lib.hellocharts.model.Line;
import android.content.Context;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;

public class ChartTouchHandler {

	private int mSelectedLineIndex = Integer.MIN_VALUE;
	private int mSelectedPointIndex = Integer.MIN_VALUE;
	private GestureDetector mGestureDetector;
	private ScaleGestureDetector mScaleGestureDetector;
	private OnPointClickListener mOnPointClickListener = new DummyOnPointListener();
	private ChartScroller mChartScroller;
	private ChartZoomer mChartZoomer;
	private LineChart mChart;

	public ChartTouchHandler(Context context, LineChart chart) {
		mChart = chart;
		mGestureDetector = new GestureDetector(context, new ChartGestureListener());
		mScaleGestureDetector = new ScaleGestureDetector(context, new ChartScaleGestureListener());
		mChartScroller = new ChartScroller(context);
		mChartZoomer = new ChartZoomer(context, ChartZoomer.ZOOM_HORIZONTAL_AND_VERTICAL);
	}

	public boolean computeScroll(LineChart chart, ChartCalculator chartCalculator) {
		boolean needInvalidate = false;
		if (mChartScroller.computeScrollOffset(chartCalculator)) {
			needInvalidate = true;
		}
		if (mChartZoomer.computeZoom(chartCalculator)) {
			needInvalidate = true;
		}
		return needInvalidate;
	}

	public boolean handleTouchEvent(MotionEvent event, Data data, ChartCalculator chartCalculator) {
		boolean needInvalidate = false;
		needInvalidate = mGestureDetector.onTouchEvent(event);
		needInvalidate = mScaleGestureDetector.onTouchEvent(event);
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			// Select only the first value within touched area.
			// Reverse loop to starts with the line drawn on top.
			for (int lineIndex = data.lines.size() - 1; lineIndex >= 0; --lineIndex) {
				int valueIndex = 0;
				for (AnimatedPoint animatedPoint : data.lines.get(lineIndex).animatedPoints) {
					final float rawX = chartCalculator.calculateRawX(animatedPoint.point.x);
					final float rawY = chartCalculator.calculateRawY(animatedPoint.point.y);
					if (mChart.getLineChartRenderer().isInArea(rawX, rawY, event.getX(), event.getY())) {
						mSelectedLineIndex = lineIndex;
						mSelectedPointIndex = valueIndex;
						needInvalidate = true;
					}
					++valueIndex;
				}
			}
			break;
		case MotionEvent.ACTION_UP:
			// If value was selected call click listener and clear selection.
			if (mSelectedPointIndex >= 0) {
				final Line line = data.lines.get(mSelectedLineIndex);
				final AnimatedPoint animatedPoint = line.animatedPoints.get(mSelectedPointIndex);
				mOnPointClickListener.onPointClick(mSelectedLineIndex, mSelectedPointIndex, animatedPoint.point.x,
						animatedPoint.point.y);
				mSelectedLineIndex = Integer.MIN_VALUE;
				mSelectedPointIndex = Integer.MIN_VALUE;
				needInvalidate = true;
			}
			break;
		case MotionEvent.ACTION_MOVE:
			// Clear selection if user is now touching outside touch area.
			if (mSelectedPointIndex >= 0) {
				final Line line = data.lines.get(mSelectedLineIndex);
				final AnimatedPoint animatedPoint = line.animatedPoints.get(mSelectedPointIndex);
				final float rawX = chartCalculator.calculateRawX(animatedPoint.point.x);
				final float rawY = chartCalculator.calculateRawY(animatedPoint.point.y);
				if (mChart.getLineChartRenderer().isInArea(rawX, rawY, event.getX(), event.getY())) {
					mSelectedLineIndex = Integer.MIN_VALUE;
					mSelectedPointIndex = Integer.MIN_VALUE;
					needInvalidate = true;
				}
			}
			break;
		case MotionEvent.ACTION_CANCEL:
			// Clear selection
			if (mSelectedPointIndex >= 0) {
				mSelectedLineIndex = Integer.MIN_VALUE;
				mSelectedPointIndex = Integer.MIN_VALUE;
				needInvalidate = true;
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
