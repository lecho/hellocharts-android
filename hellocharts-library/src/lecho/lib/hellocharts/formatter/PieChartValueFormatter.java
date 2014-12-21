package lecho.lib.hellocharts.formatter;

import lecho.lib.hellocharts.model.ArcValue;

public interface PieChartValueFormatter {

	public int formatChartValue(char[] formattedValue, ArcValue value);
}
