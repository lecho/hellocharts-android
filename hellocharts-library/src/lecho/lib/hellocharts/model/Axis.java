package lecho.lib.hellocharts.model;

import java.util.List;

public class Axis {

	private List<Float> mValues;
	private List<String> mStringValues;
	private String mValueFormatter;
	private int mColor;

	public List<Float> getValues() {
		return mValues;
	}

	public void setValues(List<Float> values) {
		this.mValues = values;
	}

	public List<String> getStringValues() {
		return mStringValues;
	}

	public void setStringValues(List<String> stringValues) {
		this.mStringValues = stringValues;
	}

	public String getValueFormatter() {
		return mValueFormatter;
	}

	public void setValueFormatter(String valueFormatter) {
		this.mValueFormatter = valueFormatter;
	}

	public int getColor() {
		return mColor;
	}

	public void setColor(int color) {
		this.mColor = color;
	}

}
