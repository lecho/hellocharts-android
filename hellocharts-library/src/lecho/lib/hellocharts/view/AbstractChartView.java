package lecho.lib.hellocharts.view;

import lecho.lib.hellocharts.Chart;
import lecho.lib.hellocharts.ChartCalculator;
import lecho.lib.hellocharts.ViewportChangeListener;
import lecho.lib.hellocharts.animation.ChartAnimationListener;
import lecho.lib.hellocharts.animation.ChartDataAnimator;
import lecho.lib.hellocharts.animation.ChartDataAnimatorV14;
import lecho.lib.hellocharts.animation.ChartDataAnimatorV8;
import lecho.lib.hellocharts.animation.ChartViewportAnimator;
import lecho.lib.hellocharts.animation.ChartViewportAnimatorV14;
import lecho.lib.hellocharts.animation.ChartViewportAnimatorV8;
import lecho.lib.hellocharts.gesture.ChartTouchHandler;
import lecho.lib.hellocharts.model.SelectedValue;
import lecho.lib.hellocharts.model.Viewport;
import lecho.lib.hellocharts.renderer.AxesRenderer;
import lecho.lib.hellocharts.renderer.ChartRenderer;
import android.content.Context;
import android.graphics.Canvas;
import android.os.Build;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

public abstract class AbstractChartView extends View implements Chart {
	private static final String TAG = "AbstractChartView";
	protected ChartCalculator chartCalculator;
	protected AxesRenderer axesRenderer;
	protected ChartTouchHandler touchHandler;
	protected ChartRenderer chartRenderer;
	protected ChartDataAnimator dataAnimator;
	protected ChartViewportAnimator viewportAnimator;

	public AbstractChartView(Context context) {
		this(context, null, 0);
	}

	public AbstractChartView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public AbstractChartView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		chartCalculator = new ChartCalculator();
		axesRenderer = new AxesRenderer(context, this);
		touchHandler = new ChartTouchHandler(context, this);
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
			this.dataAnimator = new ChartDataAnimatorV8(this);
			this.viewportAnimator = new ChartViewportAnimatorV8(this);
		} else {
			this.viewportAnimator = new ChartViewportAnimatorV14(this);
			this.dataAnimator = new ChartDataAnimatorV14(this);
		}
	}

    public void setMaxZoom(float maxZoom){
        chartCalculator.setMaxZoom(maxZoom);
    }

    public float getMaxZoom(){
        return chartCalculator.getMaxZoom();
    }

	@Override
	protected void onSizeChanged(int width, int height, int oldWidth, int oldHeight) {
		super.onSizeChanged(width, height, oldWidth, oldHeight);
		chartCalculator.calculateContentArea(getWidth(), getHeight(), getPaddingLeft(), getPaddingTop(),
				getPaddingRight(), getPaddingBottom());
		axesRenderer.initAxesAttributes();
		chartRenderer.initDataAttributes();
	}

	@Override
	protected void onDraw(Canvas canvas) {
		//long time = System.nanoTime();
		super.onDraw(canvas);
		axesRenderer.draw(canvas);

		int clipRestoreCount = canvas.save();
		canvas.clipRect(chartCalculator.getContentRect());
		chartRenderer.draw(canvas);

		canvas.restoreToCount(clipRestoreCount);
		chartRenderer.drawUnclipped(canvas);

		//Log.v(TAG, this.getClass().getSimpleName() + " - onDraw [ms]: " + (System.nanoTime() - time) / 1000000f);
	}

    @Override
	public boolean onTouchEvent(MotionEvent event) {
		super.onTouchEvent(event);
		if (touchHandler.handleTouchEvent(event)) {
			ViewCompat.postInvalidateOnAnimation(this);
		}
        return true;
	}

	@Override
	public void computeScroll() {
		super.computeScroll();
		if (touchHandler.computeScroll()) {
			ViewCompat.postInvalidateOnAnimation(this);
		}
	}

	@Override
	public void startDataAnimation() {
		dataAnimator.cancelAnimation();
		dataAnimator.startAnimation();
	}

	@Override
	public void setDataAnimationListener(ChartAnimationListener animationListener) {
		dataAnimator.setChartAnimationListener(animationListener);
	}

	@Override
	public void setViewportAnimationListener(ChartAnimationListener animationListener) {
		viewportAnimator.setChartAnimationListener(animationListener);
	}

	@Override
	public void setViewportChangeListener(ViewportChangeListener viewportChangeListener) {
		chartCalculator.setViewportChangeListener(viewportChangeListener);
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
	public void setMaxViewport(Viewport maxViewport) {
		chartRenderer.setMaxViewport(maxViewport);
		ViewCompat.postInvalidateOnAnimation(this);
	}

	@Override
	public Viewport getMaxViewport() {
		return chartRenderer.getMaxViewport();
	}

	@Override
	public void setViewport(Viewport targetViewport, boolean isAnimated) {
		if (isAnimated) {
			viewportAnimator.cancelAnimation();
			viewportAnimator.startAnimation(getViewport(), targetViewport);
		} else {
			chartRenderer.setViewport(targetViewport);
		}
		ViewCompat.postInvalidateOnAnimation(this);
	}

	@Override
	public Viewport getViewport() {
		return getChartRenderer().getViewport();
	}

	/**
	 * Smoothly zooms the chart in or out according to value of zoomAmount.
	 * 
	 * @param zoomAmount positive value for zoom in, negative for zoom out.
	 */
	@Override
	public void zoom(float x, float y, float zoomAmount) {
		if (chartCalculator.getVisibleViewport().contains(x, y)) {
			touchHandler.startZoom(x, y, zoomAmount);
			ViewCompat.postInvalidateOnAnimation(this);
		}
	}

	@Override
	public boolean isValueSelectionEnabled() {
		return touchHandler.isValueSelectionEnabled();
	}

	@Override
	public void setValueSelectionEnabled(boolean isValueSelectionEnabled) {
		touchHandler.setValueSelectionEnabled(isValueSelectionEnabled);
	}

	@Override
	public void selectValue(SelectedValue selectedValue) {
		chartRenderer.selectValue(selectedValue);
		ViewCompat.postInvalidateOnAnimation(this);
	}

	@Override
	public SelectedValue getSelectedValue() {
		return chartRenderer.getSelectedValue();
	}

}
