package lecho.lib.hellocharts.gesture;

import lecho.lib.hellocharts.Chart;
import lecho.lib.hellocharts.ChartComputator;
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

	protected boolean isInteractive = true;
	protected boolean isZoomEnabled = true;
	private boolean isScrollEnabled = true;
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

		final ChartComputator computator = chart.getChartComputator();

		boolean needInvalidate = false;
		if (isScrollEnabled && chartScroller.computeScrollOffset(computator)) {
			needInvalidate = true;
		}
		if (isZoomEnabled && chartZoomer.computeZoom(computator)) {
			needInvalidate = true;
		}
		return needInvalidate;
	}

	public boolean handleTouchEvent(MotionEvent event) {
		if (!isInteractive) {
			return false;
		}
		boolean needInvalidate = false;
		// TODO: What the heck, why onTouchEvent() always return true?

		needInvalidate = scaleGestureDetector.onTouchEvent(event);
		needInvalidate = gestureDetector.onTouchEvent(event) || needInvalidate;

		if (isValueTouchEnabled) {
			needInvalidate = computeTouch(event) || needInvalidate;
		}

		return needInvalidate;
	}

	private boolean computeTouch(MotionEvent event) {
		final ChartRenderer renderer = chart.getChartRenderer();

		boolean needInvalidate = false;
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			boolean wasTouched = renderer.isTouched();
			boolean isTouched = renderer.checkTouch(event.getX(), event.getY());
			if (wasTouched != isTouched) {
				needInvalidate = true;
			}
			break;
		case MotionEvent.ACTION_UP:
			if (renderer.isTouched()) {
				if (renderer.checkTouch(event.getX(), event.getY())) {
					renderer.callChartTouchListener();
					if (!isValueSelectionEnabled) {
						renderer.clearTouch();
					}
				} else {
					renderer.clearTouch();
				}
				needInvalidate = true;
			}
			break;
		case MotionEvent.ACTION_MOVE:
			// If value was touched and now touch point is outside of value area - clear touch and invalidate, user
			// probably moved finger away from point without leaving finger of the screen surface
			if (renderer.isTouched()) {
				if (!renderer.checkTouch(event.getX(), event.getY())) {
					renderer.clearTouch();
					needInvalidate = true;
				}
			}
			break;
		case MotionEvent.ACTION_CANCEL:
			if (renderer.isTouched()) {
				renderer.clearTouch();
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

	public boolean isScrollEnabled() {
		return isScrollEnabled;
	}

	public void setScrollEnabled(boolean isScrollEnabled) {
		this.isScrollEnabled = isScrollEnabled;
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
		chartZoomer.startZoom(x, y, zoom, chart.getChartComputator());

	}

	private class ChartScaleGestureListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {

		@Override
		public boolean onScale(ScaleGestureDetector detector) {
			if (isZoomEnabled) {
				float scale = 2.0f - detector.getScaleFactor();
				if (Float.isInfinite(scale)) {
					scale = 1;
				}
				return chartZoomer.scale(chart.getChartComputator(), detector.getFocusX(), detector.getFocusY(), scale);
			} else {
				return false;
			}
		}
	}

	private class ChartGestureListener extends GestureDetector.SimpleOnGestureListener {
		@Override
		public boolean onDown(MotionEvent e) {
			if (isScrollEnabled) {
				return chartScroller.startScroll(chart.getChartComputator());
			} else {
				return false;
			}
		}

		@Override
		public boolean onDoubleTap(MotionEvent e) {
			if (isZoomEnabled) {
				return chartZoomer.startZoom(e, chart.getChartComputator());
			} else {
				return false;
			}
		}

		@Override
		public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
			if (isScrollEnabled) {
				return chartScroller.scroll(distanceX, distanceY, chart.getChartComputator());
			} else {
				return false;
			}
		}

		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
			if (isScrollEnabled) {
				return chartScroller.fling((int) -velocityX, (int) -velocityY, chart.getChartComputator());
			} else {
				return false;
			}
		}
	}

}
