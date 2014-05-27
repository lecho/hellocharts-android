package lecho.lib.hellocharts.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Bar {
	private static final int DEFAULT_VALUE_TEXT_SIZE_DP = 10;
	public List<AnimatedValueWithColor> animatedValues = Collections.emptyList();
	public int textSize = DEFAULT_VALUE_TEXT_SIZE_DP;
	public BarValueFormatter formatter = new DefaultBarValueFormatter();
	public boolean hasValuesPopups = false;

	public Bar(List<ValueWithColor> values) {
		setValues(values);
	}

	private void setValues(List<ValueWithColor> values) {
		animatedValues = new ArrayList<AnimatedValueWithColor>(values.size());
		for (ValueWithColor valueWithColor : values) {
			animatedValues.add(new AnimatedValueWithColor(valueWithColor.y, valueWithColor.color));
		}
	}

	public interface BarValueFormatter {
		public static final String DEFAULT_VALUE_FORMAT = "%.0f";

		public String formatValue(float value);
	}

	public static class DefaultBarValueFormatter implements BarValueFormatter {

		@Override
		public String formatValue(float value) {
			return String.format(DEFAULT_VALUE_FORMAT, value);
		}

	}

}
