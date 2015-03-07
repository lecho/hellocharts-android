package lecho.lib.hellocharts.formatter;

import lecho.lib.hellocharts.model.BubbleValue;

public interface BubbleChartValueFormatter {

    public int formatChartValue(char[] formattedValue, BubbleValue value);
}
