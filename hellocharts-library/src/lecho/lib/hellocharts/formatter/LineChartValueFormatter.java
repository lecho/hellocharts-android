package lecho.lib.hellocharts.formatter;


import lecho.lib.hellocharts.model.PointValue;

public interface LineChartValueFormatter {

    public int formatChartValue(char[] formattedValue, PointValue value);
}
