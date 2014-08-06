package lecho.lib.hellocharts.renderer;

import lecho.lib.hellocharts.model.SelectedValue;
import lecho.lib.hellocharts.model.Viewport;
import android.graphics.Canvas;

public interface ChartRenderer {

	public void initMaxViewport();

	public void initCurrentViewport();

	public void initDataAttributes();

	public void draw(Canvas canvas);

	public void drawUnclipped(Canvas canvas);

	public boolean checkTouch(float touchX, float touchY);

	public boolean isTouched();

	public void clearTouch();

	public void callChartTouchListener();

	public void setMaxViewport(Viewport maxViewport);

	public Viewport getMaxViewport();

	public void setViewport(Viewport viewport);

	public Viewport getViewport();

	public void selectValue(SelectedValue selectedValue);

	public SelectedValue getSelectedValue();

}
