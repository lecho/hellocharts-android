package lecho.lib.hellocharts.model;

import lecho.lib.hellocharts.util.Utils;

public class ColumnValue {

	private float value;
	private float orginValue;
	private float diff;
	private int color = Utils.DEFAULT_COLOR;
	private int darkenColor = Utils.DEFAULT_DARKEN_COLOR;

	public ColumnValue(float value) {
		// point and targetPoint have to be different objects
		setValue(value);
	}

	public ColumnValue(float value, int color) {
		// point and targetPoint have to be different objects
		setValue(value);
		setColor(color);
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
}
