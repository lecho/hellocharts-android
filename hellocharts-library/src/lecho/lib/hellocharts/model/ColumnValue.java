package lecho.lib.hellocharts.model;

import lecho.lib.hellocharts.util.Utils;
import android.graphics.Color;

public class ColumnValue {

	private float value;
	private float targetValue;
	private int color = Color.LTGRAY;
	private int darkenColor = Color.GRAY;

	public ColumnValue(float value) {
		// point and targetPoint have to be different objects
		this.value = value;
		this.targetValue = value;
	}

	public ColumnValue(float value, int color) {
		// point and targetPoint have to be different objects
		this.value = value;
		this.targetValue = value;
		this.color = color;
		this.darkenColor = Utils.darkenColor(color);
	}

	public void update(float scale) {
		final float diff = targetValue - value;
		value = diff * scale;
	}

	public float getValue() {
		return value;
	}

	public void setValue(float value) {
		this.value = value;
	}

	public float getTargetValue() {
		return targetValue;
	}

	public void setTargetValue(float targetValue) {
		this.targetValue = targetValue;
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
