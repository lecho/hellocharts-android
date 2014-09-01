package lecho.lib.hellocharts.model;

import lecho.lib.hellocharts.util.Utils;

/**
 * Model representing single slice/arc on PieChart.
 * 
 * @author Leszek Wach
 * 
 */
public class ArcValue {
	private static final int DEFAULT_ARC_SPACING_DP = 2;
	private float value;
	private float orginValue;
	private float diff;
	private int color = Utils.DEFAULT_COLOR;
	private int darkenColor = Utils.DEFAULT_DARKEN_COLOR;
	private int arcSpacing = DEFAULT_ARC_SPACING_DP;
	private char[] label;

	public ArcValue() {
		setValue(0);
	}

	public ArcValue(float value) {
		setValue(value);
	}

	public ArcValue(float value, int color) {
		setValue(value);
		setColor(color);
	}

	public ArcValue(float value, int color, int arcSpacing) {
		setValue(value);
		setColor(color);
		this.arcSpacing = arcSpacing;
	}

	public ArcValue(ArcValue arcValue) {
		setValue(arcValue.value);
		setColor(arcValue.color);
		this.arcSpacing = arcValue.arcSpacing;
		this.label = arcValue.label;
	}

	public void update(float scale) {
		value = orginValue + diff * scale;
	}

	public void finish(boolean isSuccess) {
		if (isSuccess) {
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

	public char[] getLabel() {
		return label;
	}

	public ArcValue setLabel(char[] label) {
		this.label = label;
		return this;
	}

	@Override
	public String toString() {
		return "ArcValue [value=" + value + "]";
	}

}
