package lecho.lib.hellocharts.view;

import lecho.lib.hellocharts.Chart;
import lecho.lib.hellocharts.ChartCalculator;
import lecho.lib.hellocharts.gesture.ChartTouchHandler;
import lecho.lib.hellocharts.gesture.ChartZoomer;
import lecho.lib.hellocharts.renderer.AxesRenderer;
import lecho.lib.hellocharts.renderer.ChartRenderer;
import android.content.Context;
import android.graphics.RectF;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.View;

public abstract class AbstractChartView extends View implements Chart {
	protected ChartCalculator chartCalculator;
	protected AxesRenderer axesRenderer;
	protected ChartTouchHandler touchHandler;
	protected ChartRenderer chartRenderer;

	public AbstractChartView(Context context) {
		this(context, null, 0);
	}

	public AbstractChartView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public AbstractChartView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	public ChartRenderer getChartRenderer() {
		return chartRenderer;
	}

	public AxesRenderer getAxesRenderer() {
		return axesRenderer;
	}

	public ChartCalculator getChartCalculator() {
		return chartCalculator;
	}

	public ChartTouchHandler getTouchHandler() {
		return touchHandler;
	}

	public boolean isInteractive() {
		return touchHandler.isInteractive();
	}

	public void setInteractive(boolean isInteractive) {
		touchHandler.setInteractive(isInteractive);
	}

	public boolean isZoomEnabled() {
		return touchHandler.isZoomEnabled();
	}

	public void setZoomEnabled(boolean isZoomEnabled) {
		touchHandler.setZoomEnabled(isZoomEnabled);
	}

	public boolean isValueTouchEnabled() {
		return touchHandler.isValueTouchEnabled();
	}

	@Override
	public void setValueTouchEnabled(boolean isValueTouchEnabled) {
		touchHandler.setValueTouchEnabled(isValueTouchEnabled);

	}

	@Override
	public int getZoomType() {
		return touchHandler.getZoomType();
	}

	@Override
	public void setZoomType(int zoomType) {
		touchHandler.setZoomType(zoomType);
	}

	@Override
	public void setDataBoundaries(RectF dataBoundaries) {
		chartRenderer.setDataBoundaries(dataBoundaries);
		ViewCompat.postInvalidateOnAnimation(this);
	}

	@Override
	public RectF getDataBoundaries() {
		return chartRenderer.getDataBoundaries();
	}

	@Override
	public void setViewport(RectF viewport) {
		chartRenderer.setViewport(viewport);
		ViewCompat.postInvalidateOnAnimation(this);
	}

	@Override
	public RectF getViewport() {
		return getChartRenderer().getViewport();
	}

	/**
	 * Smoothly zooms the chart in one step.
	 */
	public void zoomIn(float x, float y) {
		if (chartCalculator.mCurrentViewport.contains(x, y)) {
			touchHandler.startZoom(x, y, ChartZoomer.ZOOM_AMOUNT);
			ViewCompat.postInvalidateOnAnimation(this);
		}
	}

	/**
	 * Smoothly zooms the chart out one step.
	 */
	public void zoomOut(float x, float y) {
		if (chartCalculator.mCurrentViewport.contains(x, y)) {
			touchHandler.startZoom(x, y, -ChartZoomer.ZOOM_AMOUNT);
			ViewCompat.postInvalidateOnAnimation(this);
		}
	}

}
