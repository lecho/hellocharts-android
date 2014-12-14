package lecho.lib.hellocharts.model;

import lecho.lib.hellocharts.util.Utils;
import lecho.lib.hellocharts.view.Chart;

/**
 * Single sub-column value for ColumnChart.
 */
public class SubcolumnValue {

	private float value;
	private float orginValue;
	private float diff;
	private int color = Utils.DEFAULT_COLOR;
	private int darkenColor = Utils.DEFAULT_DARKEN_COLOR;
	private char[] label;

	public SubcolumnValue() {
		setValue(0);
	}

	public SubcolumnValue(float value) {
		// point and targetPoint have to be different objects
		setValue(value);
	}

	public SubcolumnValue(float value, int color) {
		// point and targetPoint have to be different objects
		setValue(value);
		setColor(color);
	}

	public SubcolumnValue(SubcolumnValue columnValue) {
		setValue(columnValue.value);
		setColor(columnValue.color);
		this.label = columnValue.label;
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

	public SubcolumnValue setValue(float value) {
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
	public SubcolumnValue setTarget(float target) {
		setValue(value);
		this.diff = target - orginValue;
		return this;
	}

	public int getColor() {
		return color;
	}

	public SubcolumnValue setColor(int color) {
		this.color = color;
		this.darkenColor = Utils.darkenColor(color);
		return this;
	}

	public int getDarkenColor() {
		return darkenColor;
	}

	public char[] getLabel() {
		return label;
	}

	public SubcolumnValue setLabel(char[] label) {
		this.label = label;
		return this;
	}

	@Override
	public String toString() {
		return "ColumnValue [value=" + value + "]";
	}

}
