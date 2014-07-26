package lecho.lib.hellocharts;

import android.graphics.RectF;
import lecho.lib.hellocharts.gesture.ChartTouchHandler;
import lecho.lib.hellocharts.gesture.ZoomMode;
import lecho.lib.hellocharts.model.ChartData;
import lecho.lib.hellocharts.model.SelectedValue;
import lecho.lib.hellocharts.renderer.AxesRenderer;
import lecho.lib.hellocharts.renderer.ChartRenderer;

public interface Chart {

	public ChartData getChartData();

	public ChartRenderer getChartRenderer();

	public AxesRenderer getAxesRenderer();

	public ChartCalculator getChartCalculator();

	public ChartTouchHandler getTouchHandler();

	public void animationUpdate(float scale);

	public void callTouchListener(SelectedValue selectedValue);

	public boolean isInteractive();

	public void setInteractive(boolean isInteractive);

	public boolean isZoomEnabled();

	public void setZoomEnabled(boolean isZoomEnabled);

	public ZoomMode getZoomMode();

	public void setZoomMode(ZoomMode zoomMode);

	public boolean isValueTouchEnabled();

	public void setValueTouchEnabled(boolean isValueTouchEnabled);

	public void setDataBoundaries(RectF boundaries);

	public RectF getDataBoundaries();

	public void setViewport(RectF viewport);

	public RectF getViewport();

}
