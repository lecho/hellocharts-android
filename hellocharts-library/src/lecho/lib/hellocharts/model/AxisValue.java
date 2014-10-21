package lecho.lib.hellocharts.model;

/**
 * Single axis value, use it to manually set axis labels position. You can use label attribute to display text instead
 * of number but value formatter implementation have to handle it.
 * 
 */
public class AxisValue {
	private float value;
	private char[] label;

	public AxisValue(float value) {
		this.value = value;
	}

	public AxisValue(float value, char[] label) {
		this.value = value;
		this.label = label;
	}

	public AxisValue(AxisValue axisValue) {
		this.value = axisValue.value;
		this.label = axisValue.label;
	}

	public float getValue() {
		return value;
	}

	public AxisValue setValue(float value) {
		this.value = value;
		return this;
	}

	public char[] getLabel() {
		return label;
	}

	/**
	 * Set custom label for this axis value.
	 * 
	 * @param label
	 */
	public AxisValue setLabel(char[] label) {
		this.label = label;
		return this;
	}
}