package lecho.lib.hellocharts.view;

import lecho.lib.hellocharts.animation.ChartAnimationListener;
import lecho.lib.hellocharts.computator.ChartComputator;
import lecho.lib.hellocharts.gesture.ChartTouchHandler;
import lecho.lib.hellocharts.gesture.ContainerScrollType;
import lecho.lib.hellocharts.gesture.ZoomType;
import lecho.lib.hellocharts.listener.ViewportChangeListener;
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

    public void setChartRenderer(ChartRenderer renderer);

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
     * Starts chart data animation for given duration. Before you call this method you should change target values of
     * chart data.
     */
    public void startDataAnimation();

    /**
     * Starts chart data animation for given duration. If duration is negative the default value of 500ms will be used.
     * Before you call this method you should change target values of chart data.
     */
    public void startDataAnimation(long duration);

    /**
     * Stops chart data animation. All chart data values are set to their target values.
     */
    public void cancelDataAnimation();

    /**
     * Return true if auto viewports recalculations are enabled, false otherwise.
     */
    public boolean isViewportCalculationEnabled();

    /**
     * Set true to enable viewports(max and current) recalculations during animations or after set*ChartData method is
     * called. If you disable viewports calculations viewports will not change until you change them manually or enable
     * calculations again. Disabled viewport calculations is usefull if you want show only part of chart by setting
     * custom viewport and don't want any operation to change that viewport
     */
    public void setViewportCalculationEnabled(boolean isEnabled);

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
     * programmatically. Note! This method works only for preview charts. It is intentionally disabled for other types
     * of charts to avoid unnecessary method calls during invalidation.
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
     * Set true to enable touch scroll/fling, false to disable touch scroll/fling, by default true;
     */
    public void setScrollEnabled(boolean isScrollEnabled);

    /**
     * Move/Srcoll viewport to position x,y(that position must be within maximum chart viewport). If possible viewport
     * will be centered at this point. Width and height of viewport will not be modified.
     *
     * @see #setCurrentViewport(lecho.lib.hellocharts.model.Viewport)
     */
    public void moveTo(float x, float y);

    /**
     * Animate viewport to position x,y(that position must be within maximum chart viewport). If possible viewport
     * will be centered at this point. Width and height of viewport will not be modified.
     *
     * @see #setCurrentViewport(lecho.lib.hellocharts.model.Viewport) ;
     */
    public void moveToWithAnimation(float x, float y);

    /**
     * Returns current zoom type for this chart.
     *
     * @see #setZoomType(ZoomType)
     */
    public ZoomType getZoomType();

    /**
     * Set zoom type, available options: ZoomType.HORIZONTAL_AND_VERTICAL, ZoomType.HORIZONTAL, ZoomType.VERTICAL. By
     * default HORIZONTAL_AND_VERTICAL.
     */
    public void setZoomType(ZoomType zoomType);

    /**
     * Returns current maximum zoom value.
     */
    public float getMaxZoom();

    /**
     * Set max zoom value. Default maximum zoom is 20.
     */
    public void setMaxZoom(float maxZoom);

    /**
     * Returns current zoom level.
     */
    public float getZoomLevel();

    /**
     * Programatically zoom chart to given point(viewport point). Call this method after chart data had been set.
     *
     * @param x         x within chart maximum viewport
     * @param y         y within chart maximum viewport
     * @param zoomLevel value from 1 to maxZoom(default 20). 1 means chart has no zoom.
     */
    public void setZoomLevel(float x, float y, float zoomLevel);

    /**
     * Programatically zoom chart to given point(viewport point) with animation. Call this method after chart data
     * had been set.
     *
     * @param x         x within chart maximum viewport
     * @param y         y within chart maximum viewport
     * @param zoomLevel value from 1 to maxZoom(default 20). 1 means chart has no zoom.
     */
    public void setZoomLevelWithAnimation(float x, float y, float zoomLevel);

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
     * Returns maximum viewport for this chart. Don't modify it directly, use {@link #setMaximumViewport(Viewport)}
     * instead.
     *
     * @see #setMaximumViewport(Viewport)
     */
    public Viewport getMaximumViewport();

    /**
     * Set maximum viewport. If you set bigger maximum viewport data will be more concentrate and there will be more
     * empty spaces on sides. Note. MaxViewport have to be set after chartData has been set.
     */
    public void setMaximumViewport(Viewport maxViewport);

    /**
     * Returns current viewport. Don't modify it directly, use {@link #setCurrentViewport(Viewport)} instead.
     *
     * @see #setCurrentViewport(Viewport)
     */
    public Viewport getCurrentViewport();

    /**
     * Sets current viewport. Note. viewport have to be set after chartData has been set.
     */
    public void setCurrentViewport(Viewport targetViewport);

    /**
     * Sets current viewport with animation. Note. viewport have to be set after chartData has been set.
     */
    public void setCurrentViewportWithAnimation(Viewport targetViewport);

    /**
     * Sets current viewport with animation. Note. viewport have to be set after chartData has been set.
     */
    public void setCurrentViewportWithAnimation(Viewport targetViewport, long duration);

    /**
     * Reset maximum viewport and current viewport. Values for both viewports will be auto-calculated using current
     * chart data ranges.
     */
    public void resetViewports();

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

    /**
     * @see #setContainerScrollEnabled(boolean, ContainerScrollType)
     */
    public boolean isContainerScrollEnabled();

    /**
     * Set isContainerScrollEnabled to true and containerScrollType to HORIZONTAL or VERTICAL if you are using chart
     * within scroll container.
     */
    public void setContainerScrollEnabled(boolean isContainerScrollEnabled, ContainerScrollType containerScrollType);

}
