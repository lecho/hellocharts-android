package lecho.lib.hellocharts.renderer;

import android.graphics.Canvas;

public interface ChartRenderer {

	public void draw(Canvas canvas);

	public void drawUnclipped(Canvas canvas);

	public boolean checkTouch(float touchX, float touchY);

	public boolean isTouched();

	public void clearTouch();

	public void callTouchListener();

	public void setTextColor(int color);

	public void setTextSize(int size);

}
