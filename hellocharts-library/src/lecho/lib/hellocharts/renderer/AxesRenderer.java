package lecho.lib.hellocharts.renderer;

import android.graphics.Canvas;

/**
 * Base interface for all axes renderers.
 */
public interface AxesRenderer {

	/**
	 * Initialize measurements, font sizes etc. Called when chart data changed or when chart dimension changed.
	 */
	public void initAxesMeasurements();

	/**
	 * Draw axes
	 * 
	 * @param canvas
	 */
	public void draw(Canvas canvas);
}
