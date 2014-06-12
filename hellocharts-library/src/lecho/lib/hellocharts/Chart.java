package lecho.lib.hellocharts;

import lecho.lib.hellocharts.gestures.ChartTouchHandler;
import lecho.lib.hellocharts.model.ChartData;

public interface Chart {

	public ChartData getData();

	public ChartRenderer getChartRenderer();

	public AxesRenderer getAxesRenderer();

	public ChartCalculator getChartCalculator();

	public ChartTouchHandler getTouchHandler();

	public void animationUpdate(float scale);

}
