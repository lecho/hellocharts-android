package lecho.lib.hellocharts;

import lecho.lib.hellocharts.gestures.ChartGestureHandler;
import lecho.lib.hellocharts.model.ChartData;

public interface Chart {

	public ChartData getData();

	public LineChartRenderer getChartRenderer();

	public AxesRenderer getAxesRenderer();

	public ChartCalculator getChartCalculator();

	public ChartGestureHandler getTouchHandler();

	public void animationUpdate(float scale);

}
