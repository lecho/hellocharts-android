package lecho.lib.hellocharts.provider;

import lecho.lib.hellocharts.model.LineChartData;

public interface LineChartDataProvider {

    public LineChartData getLineChartData();

    public void setLineChartData(LineChartData data);

}
