package lecho.lib.hellocharts.model;

import android.graphics.Color;

public abstract class AbstractChartData implements ChartData {
	public static final int DEFAULT_TEXT_SIZE_SP = 12;
	protected Axis mAxisX = new Axis();
	protected Axis mAxisY = new Axis();
	protected int axesColor = Color.LTGRAY;
	protected int axesLabelTextSize = DEFAULT_TEXT_SIZE_SP;
	protected int valueLabelTextColor = Color.WHITE;
	protected int valueLabelTextSize = DEFAULT_TEXT_SIZE_SP;

	public void setAxisX(Axis axisX) {
		mAxisX = axisX;
	}

	public Axis getAxisX() {
		return mAxisX;
	}

	public void setAxisY(Axis axisY) {
		mAxisY = axisY;
	}

	public Axis getAxisY() {
		return mAxisY;
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

}