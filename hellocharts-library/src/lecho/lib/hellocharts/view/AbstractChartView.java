package lecho.lib.hellocharts.view;

import lecho.lib.hellocharts.ChartComputator;
import lecho.lib.hellocharts.ViewportChangeListener;
import lecho.lib.hellocharts.animation.ChartAnimationListener;
import lecho.lib.hellocharts.animation.ChartDataAnimator;
import lecho.lib.hellocharts.animation.ChartDataAnimatorV14;
import lecho.lib.hellocharts.animation.ChartDataAnimatorV8;
import lecho.lib.hellocharts.animation.ChartViewportAnimator;
import lecho.lib.hellocharts.animation.ChartViewportAnimatorV14;
import lecho.lib.hellocharts.animation.ChartViewportAnimatorV8;
import lecho.lib.hellocharts.gesture.ChartTouchHandler;
import lecho.lib.hellocharts.gesture.ContainerScrollType;
import lecho.lib.hellocharts.gesture.ZoomType;
import lecho.lib.hellocharts.model.SelectedValue;
import lecho.lib.hellocharts.model.Viewport;
import lecho.lib.hellocharts.renderer.AxesRenderer;
import lecho.lib.hellocharts.renderer.ChartRenderer;
import lecho.lib.hellocharts.util.Utils;
import android.content.Context;
import android.graphics.Canvas;
import android.os.Build;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * Abstract class for charts views.
 * 
 * @author Leszek Wach
 * 
 */
public abstract class AbstractChartView extends View implements Chart {
	protected ChartComputator chartComputator;
	protected AxesRenderer axesRenderer;
	protected ChartTouchHandler touchHandler;
	protected ChartRenderer chartRenderer;
	protected ChartDataAnimator dataAnimator;
	protected ChartViewportAnimator viewportAnimator;
	protected boolean isInteractive = true;
	protected boolean isContainerScrollEnabled = false;
	protected ContainerScrollType containerScrollType;

	public AbstractChartView(Context context) {
		this(context, null, 0);
	}

	public AbstractChartView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public AbstractChartView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		chartComputator = new ChartComputator();
		touchHandler = new ChartTouchHandler(context, this);
		axesRenderer = new AxesRenderer(context, this);

		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
			this.dataAnimator = new ChartDataAnimatorV8(this);
			this.viewportAnimator = new ChartViewportAnimatorV8(this);
		} else {
			this.viewportAnimator = new ChartViewportAnimatorV14(this);
			this.dataAnimator = new ChartDataAnimatorV14(this);
		}
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}

	@Override
	protected void onSizeChanged(int width, int height, int oldWidth, int oldHeight) {
		super.onSizeChanged(width, height, oldWidth, oldHeight);
		chartComputator.setContentArea(getWidth(), getHeight(), getPaddingLeft(), getPaddingTop(), getPaddingRight(),
				getPaddingBottom());
		axesRenderer.initAxesAttributes();
		chartRenderer.initDataMeasuremetns();
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		if (isEnabled()) {
			axesRenderer.drawInBackground(canvas);
			int clipRestoreCount = canvas.save();
			canvas.clipRect(chartComputator.getContentRect());
			chartRenderer.draw(canvas);
			canvas.restoreToCount(clipRestoreCount);
			chartRenderer.drawUnclipped(canvas);
			axesRenderer.drawInForeground(canvas);
		} else {
			canvas.drawColor(Utils.DEFAULT_COLOR);
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		super.onTouchEvent(event);

		if (isInteractive) {

			boolean needInvalidate;

			if (isContainerScrollEnabled) {
				needInvalidate = touchHandler.handleTouchEvent(event, getParent(), containerScrollType);
			} else {
				needInvalidate = touchHandler.handleTouchEvent(event);
			}

			if (needInvalidate) {
				ViewCompat.postInvalidateOnAnimation(this);
			}

			return true;
		} else {

			return false;
		}
	}

	@Override
	public void computeScroll() {
		super.computeScroll();
		if (isInteractive) {
			if (touchHandler.computeScroll()) {
				ViewCompat.postInvalidateOnAnimation(this);
			}
		}
	}

	@Override
	public void startDataAnimation() {
		dataAnimator.startAnimation(Long.MIN_VALUE);
	}

	@Override
	public void startDataAnimation(long duration) {
		dataAnimator.startAnimation(duration);
	}

	@Override
	public void cancelDataAnimation() {
		dataAnimator.cancelAnimation();
	}

	@Override
	public void animationDataUpdate(float scale) {
		getChartData().update(scale);
		chartRenderer.initMaxViewport();
		chartRenderer.initCurrentViewport();
		ViewCompat.postInvalidateOnAnimation(this);
	}

	@Override
	public void animationDataFinished() {
		getChartData().finish();
		chartRenderer.initMaxViewport();
		chartRenderer.initCurrentViewport();
		ViewCompat.postInvalidateOnAnimation(this);
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
		chartComputator.setViewportChangeListener(viewportChangeListener);
	}

	@Override
	public ChartRenderer getChartRenderer() {
		return chartRenderer;
	}

	@Override
	public AxesRenderer getAxesRenderer() {
		return axesRenderer;
	}

	@Override
	public ChartComputator getChartComputator() {
		return chartComputator;
	}

	@Override
	public ChartTouchHandler getTouchHandler() {
		return touchHandler;
	}

	@Override
	public boolean isInteractive() {
		return isInteractive;
	}

	@Override
	public void setInteractive(boolean isInteractive) {
		this.isInteractive = isInteractive;
	}

	@Override
	public boolean isZoomEnabled() {
		return touchHandler.isZoomEnabled();
	}

	@Override
	public void setZoomEnabled(boolean isZoomEnabled) {
		touchHandler.setZoomEnabled(isZoomEnabled);
	}

	@Override
	public boolean isScrollEnabled() {
		return touchHandler.isScrollEnabled();
	}

	@Override
	public void setScrollEnabled(boolean isScrollEnabled) {
		touchHandler.setScrollEnabled(isScrollEnabled);
	}

	public void moveTo(float x, float y, boolean isAnimated) {
		Viewport maxViewport = getMaximumViewport();

		if (maxViewport.contains(x, y)) {

			Viewport currentViewport = getCurrentViewport();
			Viewport scrollViewport = new Viewport(currentViewport);

			final float width = currentViewport.width();
			final float height = currentViewport.height();

			final float halfWidth = width / 2;
			final float halfHeight = height / 2;

			float left = x - halfWidth;
			float top = y + halfHeight;

			left = Math.max(maxViewport.left, Math.min(left, maxViewport.right - width));
			top = Math.max(maxViewport.bottom + height, Math.min(top, maxViewport.top));

			scrollViewport.set(left, top, left + height, top - height);
			
			setCurrentViewport(scrollViewport, isAnimated);
		}
	}

	@Override
	public boolean isValueTouchEnabled() {
		return touchHandler.isValueTouchEnabled();
	}

	@Override
	public void setValueTouchEnabled(boolean isValueTouchEnabled) {
		touchHandler.setValueTouchEnabled(isValueTouchEnabled);

	}

	@Override
	public ZoomType getZoomType() {
		return touchHandler.getZoomType();
	}

	@Override
	public void setZoomType(ZoomType zoomType) {
		touchHandler.setZoomType(zoomType);
	}

	public float getMaxZoom() {
		return chartComputator.getMaxZoom();
	}

	public void setMaxZoom(float maxZoom) {
		chartComputator.setMaxZoom(maxZoom);
		ViewCompat.postInvalidateOnAnimation(this);
	}

	@Override
	public float getZoomLevel() {
		Viewport maxViewport = getMaximumViewport();
		Viewport currentViewport = getCurrentViewport();

		return Math.max(maxViewport.width() / currentViewport.width(), maxViewport.height() / currentViewport.height());

	}

	@Override
	public void setZoomLevel(float x, float y, float zoomLevel, boolean isAnimated) {
		Viewport zoomViewport = new Viewport(getMaximumViewport());

		if (zoomViewport.contains(x, y)) {
			// Compute zoom by changing chart current viewport.

			if (zoomLevel < 1) {
				zoomLevel = 1;
			} else if (zoomLevel > getMaxZoom()) {
				zoomLevel = getMaxZoom();
			}

			float newWidth = zoomViewport.width();
			float newHeight = zoomViewport.height();

			ZoomType zoomType = getZoomType();
			if (ZoomType.HORIZONTAL_AND_VERTICAL == zoomType) {
				newWidth = zoomViewport.width() / zoomLevel;
				newHeight = zoomViewport.height() / zoomLevel;
			} else if (ZoomType.HORIZONTAL == zoomType) {
				newWidth = zoomViewport.width() / zoomLevel;
			} else if (ZoomType.VERTICAL == zoomType) {
				newHeight = zoomViewport.height() / zoomLevel;
			}

			final float halfWidth = newWidth / 2;
			final float halfHeight = newHeight / 2;

			zoomViewport.set(x - halfWidth, y + halfHeight, x + halfWidth, y - halfHeight);

			setCurrentViewport(zoomViewport, isAnimated);
		}
	}

	@Override
	public void setMaximumViewport(Viewport maxViewport) {
		chartRenderer.setMaxViewport(maxViewport);
		ViewCompat.postInvalidateOnAnimation(this);
	}

	@Override
	public Viewport getMaximumViewport() {
		return chartRenderer.getMaxViewport();
	}

	@Override
	public void setCurrentViewport(Viewport targetViewport, boolean isAnimated) {
		if (isAnimated && null != targetViewport) {
			viewportAnimator.cancelAnimation();
			viewportAnimator.startAnimation(getCurrentViewport(), targetViewport);
		} else {
			chartRenderer.setCurrentViewport(targetViewport);
		}
		ViewCompat.postInvalidateOnAnimation(this);
	}

	@Override
	public Viewport getCurrentViewport() {
		return getChartRenderer().getCurrentViewport();
	}

	@Override
	public void resetViewports() {
		chartRenderer.setMaxViewport(null);
		chartRenderer.setCurrentViewport(null);
	}

	@Override
	public boolean isViewportCalculationEnabled() {
		return chartRenderer.isViewportCalculationEnabled();
	}

	@Override
	public void setViewportCalculationEnabled(boolean isEnabled) {
		chartRenderer.setViewportCalculationEnabled(isEnabled);
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

	@Override
	public boolean isContainerScrollEnabled() {
		return isContainerScrollEnabled;
	}

	@Override
	public void setContainerScrollEnabled(boolean isContainerScrollEnabled, ContainerScrollType containerScrollType) {
		this.isContainerScrollEnabled = isContainerScrollEnabled;
		this.containerScrollType = containerScrollType;
	}

}
