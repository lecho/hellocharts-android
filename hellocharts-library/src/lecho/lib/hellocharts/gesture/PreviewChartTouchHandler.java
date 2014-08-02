package lecho.lib.hellocharts.gesture;

import lecho.lib.hellocharts.Chart;
import android.content.Context;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;

public class PreviewChartTouchHandler extends ChartTouchHandler {

	public PreviewChartTouchHandler(Context context, Chart chart) {
		super(context, chart);
		this.chart = chart;
		gestureDetector = new GestureDetector(context, new ChartGestureListener());
		scaleGestureDetector = new ScaleGestureDetector(context, new ChartScaleGestureListener());

		// Disable value touch, not needed for preview chart.
		isValueTouchEnabled = false;
		isValueSelectionEnabled = false;
	}

	private class ChartScaleGestureListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {

		@Override
		public boolean onScale(ScaleGestureDetector detector) {
			float scale = detector.getCurrentSpan() / detector.getPreviousSpan();
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
			return chartScroller.scroll(-distanceX, -distanceY, chart.getChartCalculator());
		}

		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
			return chartScroller.fling((int) velocityX, (int) velocityY, chart.getChartCalculator());
		}
	}

}
