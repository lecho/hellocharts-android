package lecho.lib.hellocharts.model;

import lecho.lib.hellocharts.util.Utils;
import android.graphics.Color;

public class ColumnValue {

	private float value;
	private float orginValue;
	private float diff;
	private int color = Color.LTGRAY;
	private int darkenColor = Color.GRAY;

	public ColumnValue(float value) {
		// point and targetPoint have to be different objects
		setValue(value);
	}

	public ColumnValue(float value, int color) {
		// point and targetPoint have to be different objects
		setValue(value);
		this.color = color;
		this.darkenColor = Utils.darkenColor(color);
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

	public void setValue(float value) {
		this.value = value;
		this.orginValue = value;
		this.diff = 0;
	}

	public void setTarget(float target) {
		setValue(value);
		this.diff = target - orginValue;
	}

	public int getColor() {
		return color;
	}

	public void setColor(int color) {
		this.color = color;
		this.darkenColor = Utils.darkenColor(color);
	}

	public int getDarkenColor() {
		return darkenColor;
	}
}
