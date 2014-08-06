package lecho.lib.hellocharts.model;

import lecho.lib.hellocharts.util.Utils;

public class ArcValue {
	private static final int DEFAULT_ARC_SPACING_DP = 2;
	private float value;
	private float orginValue;
	private float diff;
	private int color = Utils.DEFAULT_COLOR;
	private int darkenColor = Utils.DEFAULT_DARKEN_COLOR;
	private int arcSpacing = DEFAULT_ARC_SPACING_DP;

	public ArcValue(float value) {
		// point and targetPoint have to be different objects
		setValue(value);
	}

	public ArcValue(float value, int color) {
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

	public ArcValue setValue(float value) {
		this.value = value;
		this.orginValue = value;
		this.diff = 0;
		return this;
	}

	public void setTarget(float target) {
		setValue(value);
		this.diff = target - orginValue;
	}

	public int getColor() {
		return color;
	}

	public ArcValue setColor(int color) {
		this.color = color;
		this.darkenColor = Utils.darkenColor(color);
		return this;
	}

	public int getDarkenColor() {
		return darkenColor;
	}

	public int getArcSpacing() {
		return arcSpacing;
	}

	public ArcValue setArcSpacing(int arcSpacing) {
		this.arcSpacing = arcSpacing;
		return this;
	}
}
