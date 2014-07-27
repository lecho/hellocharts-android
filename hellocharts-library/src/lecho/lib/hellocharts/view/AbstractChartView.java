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
	protected ChartCalculator mChartCalculator;
	protected AxesRenderer mAxesRenderer;
	protected ChartTouchHandler mTouchHandler;
	protected ChartRenderer mChartRenderer;
	protected boolean isInteractive = true;
	protected boolean isZoomEnabled = true;
	protected boolean isValueTouchEnabled = true;

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
		return mChartRenderer;
	}

	public AxesRenderer getAxesRenderer() {
		return mAxesRenderer;
	}

	public ChartCalculator getChartCalculator() {
		return mChartCalculator;
	}

	public ChartTouchHandler getTouchHandler() {
		return mTouchHandler;
	}

	public boolean isInteractive() {
		return isInteractive;
	}

	public void setInteractive(boolean isInteractive) {
		this.isInteractive = isInteractive;
	}

	public boolean isZoomEnabled() {
		return isZoomEnabled;
	}

	public void setZoomEnabled(boolean isZoomEnabled) {
		this.isZoomEnabled = isZoomEnabled;
	}

	public boolean isValueTouchEnabled() {
		return isValueTouchEnabled;
	}

	@Override
	public void setValueTouchEnabled(boolean isValueTouchEnabled) {
		this.isValueTouchEnabled = isValueTouchEnabled;

	}

	@Override
	public int getZoomType() {
		return 0;
	}

	@Override
	public void setZoomType(int zoomType) {

	}

	@Override
	public void setDataBoundaries(RectF dataBoundaries) {
		mChartRenderer.setDataBoundaries(dataBoundaries);
		ViewCompat.postInvalidateOnAnimation(this);
	}

	@Override
	public RectF getDataBoundaries() {
		return mChartRenderer.getDataBoundaries();
	}

	@Override
	public void animationUpdate(float scale) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setViewport(RectF viewport) {
		mChartRenderer.setViewport(viewport);
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
		if (mChartCalculator.mCurrentViewport.contains(x, y)) {
			mTouchHandler.startZoom(x, y, ChartZoomer.ZOOM_AMOUNT);
			ViewCompat.postInvalidateOnAnimation(this);
		}
	}

	/**
	 * Smoothly zooms the chart out one step.
	 */
	public void zoomOut(float x, float y) {
		if (mChartCalculator.mCurrentViewport.contains(x, y)) {
			mTouchHandler.startZoom(x, y, -ChartZoomer.ZOOM_AMOUNT);
			ViewCompat.postInvalidateOnAnimation(this);
		}
	}
}
