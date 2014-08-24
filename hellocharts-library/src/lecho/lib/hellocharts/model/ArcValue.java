package lecho.lib.hellocharts.model;

import lecho.lib.hellocharts.util.Utils;

/**
 * Model representing single slice/arc on PieChart.
 */
public class ArcValue {
	private static final int DEFAULT_ARC_SPACING_DP = 2;
	private float value;
	private float orginValue;
	private float diff;
	private int color = Utils.DEFAULT_COLOR;
	private int darkenColor = Utils.DEFAULT_DARKEN_COLOR;
	private int arcSpacing = DEFAULT_ARC_SPACING_DP;

	public ArcValue() {

	}

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

	public ArcValue setTarget(float target) {
		setValue(value);
		this.diff = target - orginValue;
		return this;
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
		ArcValue other = (ArcValue) obj;
		if (Float.floatToIntBits(value) != Float.floatToIntBits(other.value))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "ArcValue [value=" + value + "]";
	}

}
