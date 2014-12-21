package lecho.lib.hellocharts.formatter;

import lecho.lib.hellocharts.model.BubbleValue;


public class SimpleBubbleChartValueFormatter extends AbstractValueFormatter implements BubbleChartValueFormatter {

	@Override
	public int formatChartValue(char[] formattedValue, BubbleValue value) {
		return super.formatFloatValueWithPrependedAndAppendedText(formattedValue, value.getZ(), value.getLabel());
	}
}
