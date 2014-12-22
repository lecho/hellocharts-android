package lecho.lib.hellocharts.model;

import lecho.lib.hellocharts.util.ChartUtils;
import lecho.lib.hellocharts.view.Chart;

/**
 * Model representing single slice on PieChart.
 * 
 */
public class SliceValue {
	private static final int DEFAULT_SLICE_SPACING_DP = 2;

	/** Current value of this slice. */
	private float value;

	/** Origin value of this slice, used during value animation. */
	private float orginValue;

	/** Difference between originValue and targetValue. */
	private float diff;

	/** Color of this slice. */
	private int color = ChartUtils.DEFAULT_COLOR;

	/** Darken color used to draw label background and give touch feedback. */
	private int darkenColor = ChartUtils.DEFAULT_DARKEN_COLOR;

	/** Spacing between this slice and its neighbors. */
	private int sliceSpacing = DEFAULT_SLICE_SPACING_DP;

	/** Custom label for this slice, if not set number formatting will be used. */
	private char[] label;

	public SliceValue() {
		setValue(0);
	}

	public SliceValue(float value) {
		setValue(value);
	}

	public SliceValue(float value, int color) {
		setValue(value);
		setColor(color);
	}

	public SliceValue(float value, int color, int sliceSpacing) {
		setValue(value);
		setColor(color);
		this.sliceSpacing = sliceSpacing;
	}

	public SliceValue(SliceValue sliceValue) {
		setValue(sliceValue.value);
		setColor(sliceValue.color);
		this.sliceSpacing = sliceValue.sliceSpacing;
		this.label = sliceValue.label;
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

	public SliceValue setValue(float value) {
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
	public SliceValue setTarget(float target) {
		setValue(value);
		this.diff = target - orginValue;
		return this;
	}

	public int getColor() {
		return color;
	}

	public SliceValue setColor(int color) {
		this.color = color;
		this.darkenColor = ChartUtils.darkenColor(color);
		return this;
	}

	public int getDarkenColor() {
		return darkenColor;
	}

	public int getSliceSpacing() {
		return sliceSpacing;
	}

	public SliceValue setSliceSpacing(int sliceSpacing) {
		this.sliceSpacing = sliceSpacing;
		return this;
	}

	public char[] getLabel() {
		return label;
	}

	public SliceValue setLabel(char[] label) {
		this.label = label;
		return this;
	}

	@Override
	public String toString() {
		return "SliceValue [value=" + value + "]";
	}

}
