package lecho.lib.hellocharts.model;

import lecho.lib.hellocharts.util.Utils;
import lecho.lib.hellocharts.view.Chart;

/**
 * Single value for ColumnChart. Column values are drown side by side for default Column configuration. If ColumnChart
 * is stacked ColumnValues are drown above or below each other.
 * 
 */
public class ColumnValue {

	private float value;
	private float orginValue;
	private float diff;
	private int color = Utils.DEFAULT_COLOR;
	private int darkenColor = Utils.DEFAULT_DARKEN_COLOR;
	private char[] label;

	public ColumnValue() {
		setValue(0);
	}

	public ColumnValue(float value) {
		// point and targetPoint have to be different objects
		setValue(value);
	}

	public ColumnValue(float value, int color) {
		// point and targetPoint have to be different objects
		setValue(value);
		setColor(color);
	}

	public ColumnValue(ColumnValue columnValue) {
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

	public ColumnValue setValue(float value) {
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

	public char[] getLabel() {
		return label;
	}

	public ColumnValue setLabel(char[] label) {
		this.label = label;
		return this;
	}

	@Override
	public String toString() {
		return "ColumnValue [value=" + value + "]";
	}

}
