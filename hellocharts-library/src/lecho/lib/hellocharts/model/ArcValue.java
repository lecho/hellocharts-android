package lecho.lib.hellocharts.model;

import lecho.lib.hellocharts.util.Utils;
import lecho.lib.hellocharts.view.Chart;

/**
 * Model representing single slice/arc on PieChart.
 * 
 */
public class ArcValue {
	private static final int DEFAULT_ARC_SPACING_DP = 2;

	/** Current value of this arc. */
	private float value;

	/** Origin value of this arc, used during value animation. */
	private float orginValue;

	/** Difference between originValue and targetValue. */
	private float diff;

	/** Color of this arc. */
	private int color = Utils.DEFAULT_COLOR;

	/** Darken color used to draw label background and give touch feedback. */
	private int darkenColor = Utils.DEFAULT_DARKEN_COLOR;

	/** Spacing between this arc and its neighbors. */
	private int arcSpacing = DEFAULT_ARC_SPACING_DP;

	/** Custom label for this arc, if not set number formatting will be used. */
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

	public void finish() {
		setValue(orginValue + diff);
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

	/**
	 * Set target value that should be reached when data animation finish then call {@link Chart#startDataAnimation()}
	 * 
	 * @param target
	 * @return
	 */
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
