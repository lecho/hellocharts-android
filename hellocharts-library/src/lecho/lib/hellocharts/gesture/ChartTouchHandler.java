package lecho.lib.hellocharts.gesture;

import lecho.lib.hellocharts.ChartComputator;
import lecho.lib.hellocharts.gesture.ChartScroller.ScrollResult;
import lecho.lib.hellocharts.model.SelectedValue;
import lecho.lib.hellocharts.renderer.ChartRenderer;
import lecho.lib.hellocharts.view.Chart;
import android.content.Context;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.ViewParent;

/**
 * Default touch handler for most charts. Handles value touch, scroll, fling and zoom.
 * 
 */
public class ChartTouchHandler {
	protected GestureDetector gestureDetector;
	protected ScaleGestureDetector scaleGestureDetector;
	protected ChartScroller chartScroller;
	protected ChartZoomer chartZoomer;
	protected Chart chart;

	protected boolean isZoomEnabled = true;
	protected boolean isScrollEnabled = true;
	protected boolean isValueTouchEnabled = true;
	protected boolean isValueSelectionEnabled = false;

	/**
	 * Used only for selection mode to avoid calling listener multiple times for the same selection. Small thing but it
	 * is more intuitive this way.
	 */
	protected SelectedValue selectionModeOldValue = new SelectedValue();

	protected SelectedValue selectedValue = new SelectedValue();
	protected SelectedValue oldSelectedValue = new SelectedValue();

	/**
	 * ViewParent to disallow touch events interception if chart is within scroll container.
	 */
	protected ViewParent viewParent;

	/**
	 * Type of scroll of container, horizontal or vertical.
	 */
	protected ContainerScrollType containerScrollType;

	public ChartTouchHandler(Context context, Chart chart) {
		this.chart = chart;
		gestureDetector = new GestureDetector(context, new ChartGestureListener());
		scaleGestureDetector = new ScaleGestureDetector(context, new ChartScaleGestureListener());
		chartScroller = new ChartScroller(context);
		chartZoomer = new ChartZoomer(context, ZoomType.HORIZONTAL_AND_VERTICAL);
	}

	/**
	 * Computes scroll and zoom using {@link ChartScroller} and {@link ChartZoomer}. This method returns true if
	 * scroll/zoom was computed and chart needs to be invaliedated.
	 * 
	 * Using first approach of fling animation described here {@link http
	 * ://developer.android.com/training/custom-views/making-interactive.html}. Consider use of second option with
	 * ValueAnimator.
	 * 
	 */
	public boolean computeScroll() {
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

	/**
	 * Handle chart touch event(gestures, clicks). Return true if gesture was handled and chart needs to be invalidated.
	 */
	public boolean handleTouchEvent(MotionEvent event) {
		boolean needInvalidate = false;

		// TODO: detectors always return true, use class member needInvalidate instead local variable as workaround.
		// This flag should be computed inside gesture listeners methods to avoid to many invalidations.
		needInvalidate = gestureDetector.onTouchEvent(event);

		needInvalidate = scaleGestureDetector.onTouchEvent(event) || needInvalidate;

		if (isZoomEnabled && scaleGestureDetector.isInProgress()) {
			// Special case: if view is inside scroll container and user is scaling disable touch interception by
			// parent.
			disallowParentInterceptTouchEvent();
		}

		if (isValueTouchEnabled) {
			needInvalidate = computeTouch(event) || needInvalidate;
		}

		return needInvalidate;
	}

	/**
	 * Handle chart touch event(gestures, clicks). Return true if gesture was handled and chart needs to be invalidated.
	 * If viewParent and containerScrollType are not null chart can be scrolled and scaled within horizontal or vertical
	 * scroll container like ViewPager.
	 */
	public boolean handleTouchEvent(MotionEvent event, ViewParent viewParent, ContainerScrollType containerScrollType) {
		this.viewParent = viewParent;
		this.containerScrollType = containerScrollType;

		return handleTouchEvent(event);
	}

	/**
	 * Disallow parent view from intercepting touch events. Use it for chart that is within some scroll container i.e.
	 * ViewPager.
	 */
	private void disallowParentInterceptTouchEvent() {
		if (null != viewParent) {
			viewParent.requestDisallowInterceptTouchEvent(true);
		}
	}

	/**
	 * Allow parent view to intercept touch events if chart cannot be scroll horizontally or vertically according to the
	 * current value of {@link containerScrollType}.
	 */
	private void allowParentInterceptTouchEvent(ScrollResult scrollResult) {
		if (null != viewParent) {
			if (ContainerScrollType.HORIZONTAL == containerScrollType && !scrollResult.canScrollX
					&& !scaleGestureDetector.isInProgress()) {
				viewParent.requestDisallowInterceptTouchEvent(false);
			} else if (ContainerScrollType.VERTICAL == containerScrollType && !scrollResult.canScrollY
					&& !scaleGestureDetector.isInProgress()) {
				viewParent.requestDisallowInterceptTouchEvent(false);
			}
		}
	}

	private boolean computeTouch(MotionEvent event) {
		final ChartRenderer renderer = chart.getChartRenderer();

		boolean needInvalidate = false;
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			boolean wasTouched = renderer.isTouched();
			boolean isTouched = checkTouch(renderer, event.getX(), event.getY());
			if (wasTouched != isTouched) {
				needInvalidate = true;

				if (isValueSelectionEnabled) {
					selectionModeOldValue.clear();
					if (wasTouched && !renderer.isTouched()) {
						chart.callTouchListener();
					}
				}
			}
			break;
		case MotionEvent.ACTION_UP:
			if (renderer.isTouched()) {
				if (checkTouch(renderer, event.getX(), event.getY())) {
					if (isValueSelectionEnabled) {
						// For selection mode call listener only if selected value changed, that means that should be
						// first(selection) click on given value.
						if (!selectionModeOldValue.equals(selectedValue)) {
							selectionModeOldValue.set(selectedValue);
							chart.callTouchListener();
						}
					} else {
						chart.callTouchListener();
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
				if (!checkTouch(renderer, event.getX(), event.getY())) {
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

	private boolean checkTouch(ChartRenderer renderer, float touchX, float touchY) {
		oldSelectedValue.set(selectedValue);
		selectedValue.clear();

		if (renderer.checkTouch(touchX, touchY)) {
			selectedValue.set(renderer.getSelectedValue());
		}

		// Check if selection is still on the same value, if not return false.
		if (oldSelectedValue.isSet() && selectedValue.isSet() && !oldSelectedValue.equals(selectedValue)) {
			return false;
		} else {
			return renderer.isTouched();
		}
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

	public void setZoomType(ZoomType zoomType) {
		chartZoomer.setZoomType(zoomType);
	}

	public ZoomType getZoomType() {
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

	protected class ChartScaleGestureListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {

		@Override
		public boolean onScale(ScaleGestureDetector detector) {
			if (isZoomEnabled) {
				float scale = 2.0f - detector.getScaleFactor();
				if (Float.isInfinite(scale)) {
					scale = 1;
				}
				return chartZoomer.scale(chart.getChartComputator(), detector.getFocusX(), detector.getFocusY(), scale);
			}

			return false;
		}
	}

	protected class ChartGestureListener extends GestureDetector.SimpleOnGestureListener {

		protected ScrollResult scrollResult = new ScrollResult();

		@Override
		public boolean onDown(MotionEvent e) {
			if (isScrollEnabled) {

				disallowParentInterceptTouchEvent();

				return chartScroller.startScroll(chart.getChartComputator());
			}

			return false;

		}

		@Override
		public boolean onDoubleTap(MotionEvent e) {
			if (isZoomEnabled) {
				return chartZoomer.startZoom(e, chart.getChartComputator());
			}

			return false;
		}

		@Override
		public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
			if (isScrollEnabled) {
				boolean canScroll = chartScroller
						.scroll(chart.getChartComputator(), distanceX, distanceY, scrollResult);

				allowParentInterceptTouchEvent(scrollResult);

				return canScroll;
			}

			return false;

		}

		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
			if (isScrollEnabled) {
				return chartScroller.fling((int) -velocityX, (int) -velocityY, chart.getChartComputator());
			}

			return false;
		}
	}

}
