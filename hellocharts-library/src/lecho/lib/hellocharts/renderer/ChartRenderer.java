package lecho.lib.hellocharts.renderer;

import android.graphics.Canvas;
import android.graphics.RectF;

public interface ChartRenderer {

	public void initRenderer();

	public void draw(Canvas canvas);

	public void drawUnclipped(Canvas canvas);

	public boolean checkTouch(float touchX, float touchY);

	public boolean isTouched();

	public void clearTouch();

	public void callTouchListener();

	public void setDataBoundaries(RectF dataBoundaries);

	public RectF getDataBoundaries();

}
