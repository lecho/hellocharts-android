package lecho.lib.hellocharts.model;

import lecho.lib.hellocharts.util.Utils;
import android.graphics.Color;

public class ColumnValue {

	private float value;
	private float target;
	private int color = Color.LTGRAY;
	private int darkenColor = Color.GRAY;

	public ColumnValue(float value) {
		// point and targetPoint have to be different objects
		this.value = value;
		this.target = value;
	}

	public ColumnValue(float value, int color) {
		// point and targetPoint have to be different objects
		this.value = value;
		this.target = value;
		this.color = color;
		this.darkenColor = Utils.darkenColor(color);
	}

	public void update(float scale) {
		final float diff = target - value;
		value = diff * scale;
	}

	public float getValue() {
		return value;
	}

	public void setValue(float value) {
		this.value = value;
	}

	public float getTarget() {
		return target;
	}

	public void setTarget(float target) {
		this.target = target;
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
