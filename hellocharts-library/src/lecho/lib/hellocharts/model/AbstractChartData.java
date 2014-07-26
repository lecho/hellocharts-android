package lecho.lib.hellocharts.model;

import android.graphics.Color;

public abstract class AbstractChartData implements ChartData {
	public static final int DEFAULT_TEXT_SIZE_SP = 12;
	protected Axis mAxisX = new Axis();
	protected Axis mAxisY = new Axis();
	protected int axesColor = Color.LTGRAY;
	protected int axesTextSize = DEFAULT_TEXT_SIZE_SP;
	protected int labelsTextColor = Color.WHITE;
	protected int labelsTextSize = DEFAULT_TEXT_SIZE_SP;

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

	public int getAxesTextSize() {
		return axesTextSize;
	}

	public void setAxesTextSize(int axesTextSize) {
		this.axesTextSize = axesTextSize;
	}

	public int getLabelsTextColor() {
		return labelsTextColor;
	}

	public void setLabelsTextColor(int labelsTextColor) {
		this.labelsTextColor = labelsTextColor;
	}

	public int getLabelsTextSize() {
		return labelsTextSize;
	}

	public void setLabelsTextSize(int labelsTextSize) {
		this.labelsTextSize = labelsTextSize;
	}

}
