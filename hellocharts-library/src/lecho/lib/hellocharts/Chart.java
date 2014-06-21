package lecho.lib.hellocharts;

import lecho.lib.hellocharts.gestures.ChartTouchHandler;
import lecho.lib.hellocharts.gestures.ZoomMode;
import lecho.lib.hellocharts.model.ChartData;
import lecho.lib.hellocharts.model.SelectedValue;

public interface Chart {

	public ChartData getData();

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

}
