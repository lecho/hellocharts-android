package lecho.lib.hellocharts.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Bar {
	private static final int DEFAULT_VALUE_TEXT_SIZE_DP = 10;
	public List<AnimatedPointWithColor> animatedPoints = Collections.emptyList();
	public int textSize = DEFAULT_VALUE_TEXT_SIZE_DP;
	public LineValueFormatter formatter = new DefaultLineValueFormatter();
	public boolean hasValuesPopups = false;

	public Bar(List<ValueWithColor> values) {
		setValues(values);
	}

	private void setValues(List<ValueWithColor> values) {
		animatedPoints = new ArrayList<AnimatedPointWithColor>(values.size());
		int x = 0;
		for (ValueWithColor valueWithColor : values) {
			animatedPoints.add(new AnimatedPointWithColor(new Point(x, valueWithColor.y), valueWithColor.color));
			++x;
		}
	}

	public interface LineValueFormatter {
		public static final String DEFAULT_LINE_VALUE_FORMAT = "%.0f";

		public String formatValue(Point value);
	}

	public static class DefaultLineValueFormatter implements LineValueFormatter {

		@Override
		public String formatValue(Point value) {
			return String.format(DEFAULT_LINE_VALUE_FORMAT, value.y);
		}

	}

}
