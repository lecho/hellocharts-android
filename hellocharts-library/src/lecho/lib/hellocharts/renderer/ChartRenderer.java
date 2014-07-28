package lecho.lib.hellocharts.renderer;

import android.graphics.Canvas;
import android.graphics.RectF;

public interface ChartRenderer {

	public void initRenderer();

	/**
	 * Used only for chart animations. That method skips some calculations during initialization.
	 */
	public void fastInitRenderer();

	public void draw(Canvas canvas);

	public void drawUnclipped(Canvas canvas);

	public boolean checkTouch(float touchX, float touchY);

	public boolean isTouched();

	public void clearTouch();

	public void callTouchListener();

	public void setMaxViewport(RectF maxViewport);

	public RectF getMaxViewport();

	public void setViewport(RectF viewport);

	public RectF getViewport();

}
