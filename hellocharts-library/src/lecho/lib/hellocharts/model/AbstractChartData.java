package lecho.lib.hellocharts.model;

import android.graphics.Color;

public abstract class AbstractChartData implements ChartData {
	public static final int DEFAULT_TEXT_SIZE_SP = 12;
	protected Axis axisX = new Axis();
	protected Axis axisY = new Axis();
	protected int axesColor = Color.LTGRAY;
	protected int axesLabelTextSize = DEFAULT_TEXT_SIZE_SP;
	protected int valueLabelTextColor = Color.WHITE;
	protected int valueLabelTextSize = DEFAULT_TEXT_SIZE_SP;
	protected int maxLabelChars = 5;

	public void setAxisX(Axis axisX) {
		this.axisX = axisX;
	}

	public Axis getAxisX() {
		return axisX;
	}

	public void setAxisY(Axis axisY) {
		this.axisY = axisY;
	}

	public Axis getAxisY() {
		return axisY;
	}

	public int getAxesColor() {
		return axesColor;
	}

	public void setAxesColor(int axesColor) {
		this.axesColor = axesColor;
	}

	public int getAxesLabelTextSize() {
		return axesLabelTextSize;
	}

	public void setAxesLabelTextSize(int axesLabelTextSize) {
		this.axesLabelTextSize = axesLabelTextSize;
	}

	public int getValueLabelTextColor() {
		return valueLabelTextColor;
	}

	public void setValueLabelsTextColor(int valueLabelTextColor) {
		this.valueLabelTextColor = valueLabelTextColor;
	}

	public int getValueLabelTextSize() {
		return valueLabelTextSize;
	}

	public void setValueLabelTextSize(int valueLabelTextSize) {
		this.valueLabelTextSize = valueLabelTextSize;
	}

	@Override
	public void setMaxLabelChars(int numChars) {
		if (numChars < 0) {
			numChars = 0;
		} else if (numChars > 16) {
			numChars = 16;
		}
		maxLabelChars = numChars;
	}

	@Override
	public int getMaxLabelChars() {
		return maxLabelChars;
	}

}