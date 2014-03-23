package lecho.lib.hellocharts.model;

import java.util.Collections;
import java.util.List;

import android.graphics.Color;

public class Axis {

	public List<AxisValue> values = Collections.emptyList();
	public int color = Color.LTGRAY;
	public String name = "name";
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
		public String formatValue(AxisValue value);
	}

	public static class DefaultAxisValueFormatter implements AxisValueFormatter {

		@Override
		public String formatValue(AxisValue axisValue) {
			return String.format("%.0f", axisValue.value);
		}

	}
}
