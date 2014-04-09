package lecho.lib.hellocharts.model;

import java.util.Collections;
import java.util.List;

import android.graphics.Color;

public class Axis {
	private static final int DEFAULT_AXIS_TEXT_SIZE_SP = 10;
	private static final int DEFAULT_AXIS_COLOR = Color.LTGRAY;
	public List<AxisValue> values = Collections.emptyList();
	public String name = "";
	public int color = DEFAULT_AXIS_COLOR;
	public int textSize = DEFAULT_AXIS_TEXT_SIZE_SP;
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
