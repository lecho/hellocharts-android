package lecho.lib.hellocharts.model;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.graphics.Color;

public class Bar {
	public static final int DEFAULT_VALUE_TEXT_SIZE_SP = 10;
	public static final int DEFAULT_AREA_TRANSPARENCY = 255;
	private int textColor = Color.WHITE;
	private int textSize = DEFAULT_VALUE_TEXT_SIZE_SP;
	private int areaTransparency = DEFAULT_AREA_TRANSPARENCY;
	private boolean hasAnnotations = false;
	private BarValueFormatter formatter = new DefaultBarValueFormatter();
	// TODO: consider Collections.emptyList()
	private List<BarValue> values = new ArrayList<BarValue>();

	public Bar(List<BarValue> values) {
		setValues(values);
	}

	public List<BarValue> getValues() {
		return values;
	}

	public void setValues(List<BarValue> values) {
		if (null == values) {
			this.values = new ArrayList<BarValue>();
		} else {
			this.values = values;
		}
	}

	public int getTextColor() {
		return textColor;
	}

	public Bar setTextColor(int textColor) {
		this.textColor = textColor;
		return this;
	}

	public int getTextSize() {
		return textSize;
	}

	public Bar setTextSize(int textSize) {
		this.textSize = textSize;
		return this;
	}

	public int getAreaTransparency() {
		return areaTransparency;
	}

	public Bar setAreaTransparency(int areaTransparency) {
		this.areaTransparency = areaTransparency;
		return this;
	}

	public boolean hasAnnotations() {
		return hasAnnotations;
	}

	public Bar setHasAnnotations(boolean hasAnnotations) {
		this.hasAnnotations = hasAnnotations;
		return this;
	}

	public BarValueFormatter getFormatter() {
		return formatter;
	}

	public void setFormatter(BarValueFormatter formatter) {
		if (null == formatter) {
			this.formatter = new DefaultBarValueFormatter();
		} else {
			this.formatter = formatter;
		}
	}

	public interface BarValueFormatter {
		public static final String DEFAULT_VALUE_FORMAT = "%.0f";

		public String formatValue(BarValue value);
	}

	public static class DefaultBarValueFormatter implements BarValueFormatter {

		@SuppressLint("DefaultLocale")
		@Override
		public String formatValue(BarValue value) {
			return String.format(DEFAULT_VALUE_FORMAT, value.getValue());
		}

	}

}
