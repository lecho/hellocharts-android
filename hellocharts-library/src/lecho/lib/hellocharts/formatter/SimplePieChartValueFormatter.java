package lecho.lib.hellocharts.formatter;

import lecho.lib.hellocharts.model.SliceValue;


public class SimplePieChartValueFormatter extends AbstractValueFormatter implements PieChartValueFormatter {

	@Override
	public int formatChartValue(char[] formattedValue, SliceValue value) {
		return super.formatFloatValueWithPrependedAndAppendedText(formattedValue, value.getValue(), value.getLabel());
	}
}
