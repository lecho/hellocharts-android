package lecho.lib.hellocharts.formatter;

import lecho.lib.hellocharts.model.SubcolumnValue;

public class SimpleColumnChartValueFormatter extends AbstractValueFormatter implements ColumnChartValueFormatter {

    public SimpleColumnChartValueFormatter(){};

    public SimpleColumnChartValueFormatter(int decimalDigitsNumber){
        setDecimalDigitsNumber(decimalDigitsNumber);
    }

	@Override
	public int formatChartValue(char[] formattedValue, SubcolumnValue value) {
		return super.formatFloatValueWithPrependedAndAppendedText(formattedValue, value.getValue(), value.getLabel());
	}
}
