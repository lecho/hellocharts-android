package lecho.lib.hellocharts.model;

import java.util.List;

import android.graphics.Color;

import lecho.lib.hellocharts.utils.Config;

public class Axis {

	private List<Float> values;
	private List<String> stringValues;
	private String valueFormatter;
	private int color;
	private boolean isStringAxis;

	public Axis() {
		this.valueFormatter = Config.DEFAULT_AXES_FORMAT;
		this.color = Color.LTGRAY;
		this.isStringAxis = false;
	}

	public List<Float> getValues() {
		return values;
	}

	public void setValues(List<Float> values) {
		this.values = values;
	}

	public List<String> getStringValues() {
		return stringValues;
	}

	public void setStringValues(List<String> stringValues) {
		this.stringValues = stringValues;
	}

	public String getValueFormatter() {
		return valueFormatter;
	}

	public void setValueFormatter(String valueFormatter) {
		this.valueFormatter = valueFormatter;
	}

	public int getColor() {
		return color;
	}

	public void setColor(int color) {
		this.color = color;
	}

	public boolean isStringAxis() {
		return isStringAxis;
	}

	public void setStringAxis(boolean isStringAxis) {
		this.isStringAxis = isStringAxis;
	}

}
