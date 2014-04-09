package lecho.lib.hellocharts.model;

import java.util.Collections;
import java.util.List;

import lecho.lib.hellocharts.utils.Config;

public class Axis {

	public List<AxisValue> values = Collections.emptyList();
	public String name = "";
	public int color = Config.DEFAULT_AXIS_COLOR;
	public int textSize = Config.DEFAULT_AXIS_TEXT_SIZE_DP;
	public AxisValueFormatter formatter = new DefaultAxisValueFormatter();

	public Axis() {

	}

	public Axis(List<AxisValue> values) {
		this.values = values;
	}

	public static class AxisValue {
		public float value;
		public String valueName;

		public AxisValue(float value) {
			this(value, null);
		}

		public AxisValue(float value, String valueName) {
			this.value = value;
			this.valueName = valueName;
		}
	}

	public interface AxisValueFormatter {
		public static final String DEFAULT_AXES_FORMAT = "%.0f";
		public String formatValue(AxisValue value);
	}

	public static class DefaultAxisValueFormatter implements AxisValueFormatter {

		@Override
		public String formatValue(AxisValue axisValue) {
			return String.format(DEFAULT_AXES_FORMAT, axisValue.value);
		}

	}
}
