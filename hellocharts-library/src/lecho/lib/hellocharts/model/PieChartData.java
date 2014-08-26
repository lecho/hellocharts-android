package lecho.lib.hellocharts.model;

import java.util.ArrayList;
import java.util.List;

import android.graphics.Color;

/**
 * Data for PieChart, by default it doesn't have axes.
 * 
 * @author Leszek Wach
 * 
 */
public class PieChartData extends AbstractChartData {
	public static final int DEFAULT_CENTER_TEXT_SIZE_SP = 16;
	public static final float DEFAULT_CENTER_CIRCLE_SCALE = 0.6f;
	private ValueFormatter formatter = new NumberValueFormatter();
	private boolean hasLabels = false;
	private boolean hasLabelsOnlyForSelected = false;
	private boolean hasCenterCircle = false;
	private int centerTextColor = Color.BLACK;
	private int centerTextFontSize = DEFAULT_CENTER_TEXT_SIZE_SP;
	private String centerText;
	private int centerCircleColor = Color.WHITE;
	private float centerCircleScale = 0.6f;
	private List<ArcValue> values = new ArrayList<ArcValue>();

	public PieChartData() {
	};

	public PieChartData(List<ArcValue> values) {
		setValues(values);
		// Empty axes. Pie chart don't need axes.
		setAxisX(null);
		setAxisY(null);
	}

	public PieChartData(PieChartData data) {
		super(data);
		this.formatter = data.formatter;
		this.hasLabels = data.hasLabels;
		this.hasLabelsOnlyForSelected = data.hasLabelsOnlyForSelected;

		for (ArcValue arcValue : data.values) {
			this.values.add(new ArcValue(arcValue));
		}
	}

	/**
	 * PieChart does not support axes so method call will be ignored
	 */
	@Override
	public void setAxisX(Axis axisX) {
		super.setAxisX(null);
	}

	/**
	 * PieChart does not support axes so method call will be ignored
	 */
	@Override
	public void setAxisY(Axis axisY) {
		super.setAxisY(null);
	}

	public List<ArcValue> getValues() {
		return values;
	}

	public PieChartData setValues(List<ArcValue> values) {
		if (null == values) {
			this.values = new ArrayList<ArcValue>();
		} else {
			this.values = values;
		}
		return this;
	}

	public boolean hasLabels() {
		return hasLabels;
	}

	public PieChartData setHasLabels(boolean hasLabels) {
		this.hasLabels = hasLabels;
		if (hasLabels) {
			hasLabelsOnlyForSelected = false;
		}
		return this;
	}

	public boolean hasLabelsOnlyForSelected() {
		return hasLabelsOnlyForSelected;
	}

	public PieChartData setHasLabelsOnlyForSelected(boolean hasLabelsOnlyForSelected) {
		this.hasLabelsOnlyForSelected = hasLabelsOnlyForSelected;
		if (hasLabelsOnlyForSelected) {
			this.hasLabels = false;
		}
		return this;
	}

	public boolean hasCenterCircle() {
		return hasCenterCircle;
	}

	public void setHasCenterCircle(boolean hasCenterCircle) {
		this.hasCenterCircle = hasCenterCircle;
	}

	public int getCenterTextColor() {
		return centerTextColor;
	}

	public void setCenterTextColor(int centerTextColor) {
		this.centerTextColor = centerTextColor;
	}

	public int getCenterTextFontSize() {
		return centerTextFontSize;
	}

	public void setCenterTextFontSize(int centerTextFontSize) {
		this.centerTextFontSize = centerTextFontSize;
	}

	public String getCenterText() {
		return centerText;
	}

	public void setCenterText(String centerText) {
		this.centerText = centerText;
	}

	public int getCenterCircleColor() {
		return centerCircleColor;
	}

	public void setCenterCircleColor(int centerCircleColor) {
		this.centerCircleColor = centerCircleColor;
	}

	public float getCenterCircleScale() {
		return centerCircleScale;
	}

	public void setCenterCircleScale(float centerCircleScale) {
		this.centerCircleScale = centerCircleScale;
	}

	public ValueFormatter getFormatter() {
		return formatter;
	}

	public PieChartData setFormatter(ValueFormatter formatter) {
		if (null == formatter) {
			this.formatter = new NumberValueFormatter();
		} else {
			this.formatter = formatter;
		}
		return this;
	}
}
