package lecho.lib.hellocharts.model;

public class ValueWithColor {
	public float y;
	public int color;

	public ValueWithColor(ValueWithColor valueWithColor) {
		this.y = valueWithColor.y;
		this.color = valueWithColor.color;
	}

	public ValueWithColor(float y, int color) {
		this.y = y;
		this.color = color;
	}

	public void set(ValueWithColor valueWithColor) {
		this.y = valueWithColor.y;
		this.color = valueWithColor.color;
	}

	public void set(float y, int color) {
		this.y = y;
		this.color = color;
	}

	public void offset(float dy) {
		this.y += dy;
	}

	/**
	 * Returns y difference between this value and the one in parameter. Color cannot be compared that way and will stay
	 * the same.
	 */
	public ValueWithColor diff(ValueWithColor valueWithColor) {
		return new ValueWithColor(valueWithColor.y - valueWithColor.y, valueWithColor.color);
	}
}
