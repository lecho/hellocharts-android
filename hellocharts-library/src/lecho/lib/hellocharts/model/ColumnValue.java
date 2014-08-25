package lecho.lib.hellocharts.model;

import lecho.lib.hellocharts.util.Utils;

/**
 * Single value for ColumnChart. Column values are drown side by side for default Column configuration. If ColumnChart
 * is stacked ColumnValues are drown above or below each other.
 */
public class ColumnValue {

	private float value;
	private float orginValue;
	private float diff;
	private int color = Utils.DEFAULT_COLOR;
	private int darkenColor = Utils.DEFAULT_DARKEN_COLOR;

	public ColumnValue() {
		setValue(0);
	}

	public ColumnValue(float value) {
		// point and targetPoint have to be different objects
		setValue(value);
	}

	public ColumnValue(float value, int color) {
		// point and targetPoint have to be different objects
		setValue(value);
		setColor(color);
	}

	public ColumnValue(ColumnValue columnValue) {
		setValue(columnValue.value);
		setColor(columnValue.color);
	}

	public void update(float scale) {
		value = orginValue + diff * scale;
	}

	public void finish(boolean isFinishedSuccess) {
		if (isFinishedSuccess) {
			setValue(orginValue + diff);
		} else {
			setValue(value);
		}
	}

	public float getValue() {
		return value;
	}

	public ColumnValue setValue(float value) {
		this.value = value;
		this.orginValue = value;
		this.diff = 0;
		return this;
	}

	public ColumnValue setTarget(float target) {
		setValue(value);
		this.diff = target - orginValue;
		return this;
	}

	public int getColor() {
		return color;
	}

	public ColumnValue setColor(int color) {
		this.color = color;
		this.darkenColor = Utils.darkenColor(color);
		return this;
	}

	public int getDarkenColor() {
		return darkenColor;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Float.floatToIntBits(value);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ColumnValue other = (ColumnValue) obj;
		if (Float.floatToIntBits(value) != Float.floatToIntBits(other.value))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "ColumnValue [value=" + value + "]";
	}

}
