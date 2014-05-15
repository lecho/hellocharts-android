package lecho.lib.hellocharts.model;

import android.graphics.RectF;

public abstract class AbstractChartData implements ChartData {
	protected Axis mAxisX = new Axis();
	protected Axis mAxisY = new Axis();
	protected RectF mBoundaries = new RectF();
	protected boolean mManualBoundaries = false;

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

}
