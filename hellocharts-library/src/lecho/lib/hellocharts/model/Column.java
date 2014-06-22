package lecho.lib.hellocharts.model;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.graphics.Color;

public class Column {
	public static final int DEFAULT_VALUE_TEXT_SIZE_SP = 10;
	public static final int DEFAULT_AREA_TRANSPARENCY = 255;
	private int textColor = Color.WHITE;
	private int textSize = DEFAULT_VALUE_TEXT_SIZE_SP;
	private int areaTransparency = DEFAULT_AREA_TRANSPARENCY;
	private boolean hasAnnotations = false;
	private ColumnValueFormatter formatter = new DefaultColumnValueFormatter();
	// TODO: consider Collections.emptyList()
	private List<ColumnValue> values = new ArrayList<ColumnValue>();

	public Column(List<ColumnValue> values) {
		setValues(values);
	}

	public List<ColumnValue> getValues() {
		return values;
	}

	public void setValues(List<ColumnValue> values) {
		if (null == values) {
			this.values = new ArrayList<ColumnValue>();
		} else {
			this.values = values;
		}
	}

	public int getTextColor() {
		return textColor;
	}

	public Column setTextColor(int textColor) {
		this.textColor = textColor;
		return this;
	}

	public int getTextSize() {
		return textSize;
	}

	public Column setTextSize(int textSize) {
		this.textSize = textSize;
		return this;
	}

	public int getAreaTransparency() {
		return areaTransparency;
	}

	public Column setAreaTransparency(int areaTransparency) {
		this.areaTransparency = areaTransparency;
		return this;
	}

	public boolean hasAnnotations() {
		return hasAnnotations;
	}

	public Column setHasAnnotations(boolean hasAnnotations) {
		this.hasAnnotations = hasAnnotations;
		return this;
	}

	public ColumnValueFormatter getFormatter() {
		return formatter;
	}

	public void setFormatter(ColumnValueFormatter formatter) {
		if (null == formatter) {
			this.formatter = new DefaultColumnValueFormatter();
		} else {
			this.formatter = formatter;
		}
	}

	public interface ColumnValueFormatter {
		public static final String DEFAULT_VALUE_FORMAT = "%.0f";

		public String formatValue(ColumnValue value);
	}

	public static class DefaultColumnValueFormatter implements ColumnValueFormatter {

		@SuppressLint("DefaultLocale")
		@Override
		public String formatValue(ColumnValue value) {
			return String.format(DEFAULT_VALUE_FORMAT, value.getValue());
		}

	}

}
