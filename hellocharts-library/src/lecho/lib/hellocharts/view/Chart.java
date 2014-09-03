package lecho.lib.hellocharts.view;

import lecho.lib.hellocharts.ChartComputator;
import lecho.lib.hellocharts.ViewportChangeListener;
import lecho.lib.hellocharts.animation.ChartAnimationListener;
import lecho.lib.hellocharts.gesture.ChartTouchHandler;
import lecho.lib.hellocharts.model.ChartData;
import lecho.lib.hellocharts.model.SelectedValue;
import lecho.lib.hellocharts.model.Viewport;
import lecho.lib.hellocharts.renderer.AxesRenderer;
import lecho.lib.hellocharts.renderer.ChartRenderer;

/**
 * Interface for all charts. Every chart must implements this interface but chart doesn't really have to extends View or
 * ViewGroup class. It can be any java class for example chart that only draw on in-memory bitmap and saves it on sd
 * card.
 */
public interface Chart {

	/**
	 * Returns generic chart data. For specific class call get*ChartData method from data provider implementation.
	 */
	public ChartData getChartData();

	public ChartRenderer getChartRenderer();

	public AxesRenderer getAxesRenderer();

	public ChartComputator getChartComputator();

	public ChartTouchHandler getTouchHandler();

	/**
	 * Updates chart data with given scale. Called during chart data animation update.
	 */
	public void animationDataUpdate(float scale);

	/**
	 * Called when data animation finished.
	 */
	public void animationDataFinished();

	/**
	 * Starts chart data animation. Before you call this method you should change target values of chart data.
	 */
	public void startDataAnimation();

	/**
	 * Stops chart data animation. All chart data values are set to their target values.
	 */
	public void cancelDataAnimation();

	/**
	 * Return true if viewport calculation on animation is enabled, otherwise false.
	 */
	public boolean isViewportCalculationOnAnimationEnabled();

	/**
	 * Enable or disable viewport recalculations on data animation.
	 */
	public void setViewportCalculationOnAnimationEnabled(boolean isEnabled);

	/**
	 * Set listener for data animation to be notified when data animation started and finished. By default that flag is
	 * set to true so be careful with animation and custom viewports.
	 */
	public void setDataAnimationListener(ChartAnimationListener animationListener);

	/**
	 * Set listener for viewport animation to be notified when viewport animation started and finished.
	 */
	public void setViewportAnimationListener(ChartAnimationListener animationListener);

	/**
	 * Set listener for current viewport changes. It will be called when viewport change either by gesture or
	 * programmatically. Warning! This method works only for preview charts. It is intentionally disabled for other
	 * types of charts to avoid unnecessary method calls during invalidation.
	 * 
	 */
	public void setViewportChangeListener(ViewportChangeListener viewportChangeListener);

	public void callTouchListener();

	/**
	 * Returns true if chart is interactive.
	 * 
	 * @see #setInteractive(boolean)
	 */
	public boolean isInteractive();

	/**
	 * Set true to allow user use touch gestures. If set to false user will not be able zoom, scroll or select/touch
	 * value. By default true.
	 */
	public void setInteractive(boolean isInteractive);

	/**
	 * Returns true if pitch to zoom and double tap zoom is enabled.
	 * 
	 * @see #setZoomEnabled(boolean)
	 */
	public boolean isZoomEnabled();

	/**
	 * Set true to enable zoom, false to disable, by default true;
	 */
	public void setZoomEnabled(boolean isZoomEnabled);

	/**
	 * Returns true if scrolling is enabled.
	 * 
	 * @see #setScrollEnabled(boolean)
	 */
	public boolean isScrollEnabled();

	/**
	 * Set true to enable scroll, false to disable, by default true;
	 */
	public void setScrollEnabled(boolean isScrollEnabled);

	/**
	 * Returns current zoom type for this chart.
	 * 
	 * @see #setZoomType(int)
	 */
	public int getZoomType();

	/**
	 * Set zoom type, available options: ChartZoomer.ZOOM_HORIZONTAL_AND_VERTICAL, ChartZoomer.ZOOM_HORIZONTAL,
	 * ChartZoomer.ZOOM_VERTICAL. By default ChartZoomer.ZOOM_HORIZONTAL_AND_VERTICAL.
	 */
	public void setZoomType(int zoomType);

	/**
	 * Return true if chart value can be touched.
	 * 
	 * @see #setValueTouchEnabled(boolean)
	 */
	public boolean isValueTouchEnabled();

	/**
	 * Set true if you want allow user to click value on chart, set false to disable that option. By default true.
	 */
	public void setValueTouchEnabled(boolean isValueTouchEnabled);

	/**
	 * Returns maximum viewport for this chart. Don't modify it directly, use {@link #setMaxViewport(Viewport)} instead.
	 * 
	 * @see #setMaxViewport(Viewport)
	 */
	public Viewport getMaxViewport();

	/**
	 * Set maximum viewport. If you set bigger maximum viewport data will be more concentrate and there will be more
	 * empty spaces on sides.
	 * 
	 * Note. MaxViewport have to be set after chartData has been set.
	 */
	public void setMaxViewport(Viewport maxViewport);

	/**
	 * Returns current viewport. Don't modify it directly, use {@link #setViewport(Viewport, boolean)} instead.
	 * 
	 * @see #setViewport(Viewport, boolean)
	 */
	public Viewport getViewport();

	/**
	 * Sets current viewport. If isAnimated is true chart will be animated during viewport changes.
	 * 
	 * * Note. viewport have to be set after chartData has been set.
	 */
	public void setViewport(Viewport targetViewport, boolean isAnimated);

	/**
	 * Programatically zoom chart to given point(its viewport point). Negative zoomAmount parameter means zoom out.
	 */
	public void zoom(float x, float y, float zoomAmout);

	/**
	 * Return true if value selection mode is enabled.
	 * 
	 * @see #setValueSelectionEnabled(boolean)
	 */
	public boolean isValueSelectionEnabled();

	/**
	 * Set true if you want value selection with touch - value will stay selected until you touch somewhere else on the
	 * chart area. By default false and value is automatically unselected when user stop pressing on it.
	 */
	public void setValueSelectionEnabled(boolean isValueSelectionEnabled);

	/**
	 * Select single value on chart. If indexes are not valid IndexOutOfBoundsException will be thrown.
	 */
	public void selectValue(SelectedValue selectedValue);

	/**
	 * Return currently selected value indexes.
	 */
	public SelectedValue getSelectedValue();

}
