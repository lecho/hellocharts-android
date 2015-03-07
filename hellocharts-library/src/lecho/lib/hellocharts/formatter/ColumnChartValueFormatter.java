package lecho.lib.hellocharts.formatter;

import lecho.lib.hellocharts.model.SubcolumnValue;

public interface ColumnChartValueFormatter {

    public int formatChartValue(char[] formattedValue, SubcolumnValue value);

}
