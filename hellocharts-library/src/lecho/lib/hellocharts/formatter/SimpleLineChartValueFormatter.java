package lecho.lib.hellocharts.formatter;

import lecho.lib.hellocharts.model.PointValue;

public class SimpleLineChartValueFormatter extends AbstractValueFormatter implements LineChartValueFormatter {

	@Override
	public int formatChartValue(char[] formattedValue, PointValue value) {
		return super.formatFloatValueWithPrependedAndAppendedText(formattedValue, value.getY(), value.getLabel());
	}
}
