package lecho.lib.hellocharts;

import android.graphics.Canvas;

public interface ChartRenderer {

	public void draw(Canvas canvas);

	public boolean checkValueTouch(float touchX, float touchY);

	public boolean isValueTouched();

	public void clearValueTouch();

}
