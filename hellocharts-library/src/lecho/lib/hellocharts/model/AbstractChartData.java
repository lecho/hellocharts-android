package lecho.lib.hellocharts.model;

import android.graphics.Color;
import android.graphics.RectF;

public abstract class AbstractChartData implements ChartData {
	protected Axis mAxisX = new Axis();
	protected Axis mAxisY = new Axis();
	protected RectF mBoundaries = new RectF();
	protected boolean mManualBoundaries = false;
	protected int axesColor = Color.LTGRAY;
	protected int axesTextSize = 16;
	protected int labelsTextColor = Color.WHITE;
	protected int labelsTextSize = 16;

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

	public void setBoundaries(RectF boundaries) {
		if (null == boundaries) {
			mManualBoundaries = false;
			return;
		}
		mManualBoundaries = true;
		mBoundaries.set(boundaries);
	}

	public RectF getBoundaries() {
		return mBoundaries;
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
