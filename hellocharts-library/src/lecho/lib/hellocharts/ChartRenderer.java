package lecho.lib.hellocharts;

import android.graphics.Canvas;

public interface ChartRenderer {

	public void draw(Canvas canvas);

	public boolean checkTouch(float touchX, float touchY);

	public boolean isTouched();

	public void clearTouch();

}
