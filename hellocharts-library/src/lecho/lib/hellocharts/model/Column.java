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
	private boolean hasLabels = false;
	private ValueFormatter formatter = new NumberValueFormatter();
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

	public boolean hasLabels() {
		return hasLabels;
	}

	public Column setHasLabels(boolean hasLabels) {
		this.hasLabels = hasLabels;
		return this;
	}

	public ValueFormatter getFormatter() {
		return formatter;
	}

	public Column setFormatter(ValueFormatter formatter) {
		if (null == formatter) {
			this.formatter = new NumberValueFormatter();
		} else {
			this.formatter = formatter;
		}
		return this;
	}
}
