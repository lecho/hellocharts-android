package lecho.lib.hellocharts.renderer;

import lecho.lib.hellocharts.model.SelectedValue;
import lecho.lib.hellocharts.model.Viewport;
import android.graphics.Canvas;

/**
 * Interface for all chart renderer.
 */
public interface ChartRenderer {

	/**
	 * Initialize maximum viewport, called when chart data changed.Usually you will have to do some calculation in
	 * implementation of that method.
	 */
	public void initMaxViewport();

	/**
	 * Initialize currentViewport, usually set it equals to maxViewport.
	 */
	public void initCurrentViewport();

	/**
	 * Initialize data measurements like font sizes, font colors.
	 */
	public void initDataMeasuremetns();

	/**
	 * Draw chart data.
	 */
	public void draw(Canvas canvas);

	/**
	 * Draw chart data that should not be clipped to contentRect area.
	 */
	public void drawUnclipped(Canvas canvas);

	/**
	 * Checks if given pixel coordinates corresponds to any chart value. If yes return true and set selectedValue, if
	 * not selectedValue should be *cleared* and method should return false.
	 */
	public boolean checkTouch(float touchX, float touchY);

	/**
	 * Returns true if there is value selected.
	 */
	public boolean isTouched();

	/**
	 * Clear value selection.
	 */
	public void clearTouch();

	public void setMaxViewport(Viewport maxViewport);

	public Viewport getMaxViewport();

	public void setViewport(Viewport viewport);

	public Viewport getViewport();

	public void selectValue(SelectedValue selectedValue);

	public SelectedValue getSelectedValue();

}
