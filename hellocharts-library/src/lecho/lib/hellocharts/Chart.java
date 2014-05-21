package lecho.lib.hellocharts;

import lecho.lib.hellocharts.gestures.ChartZoomAndScrollHandler;
import lecho.lib.hellocharts.model.ChartData;

public interface Chart {

	public ChartData getData();

	public AxesRenderer getAxesRenderer();

	public ChartCalculator getChartCalculator();

	public ChartZoomAndScrollHandler getTouchHandler();

	public void animationUpdate(float scale);

}
