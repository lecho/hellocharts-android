package lecho.lib.hellocharts.model;

import android.graphics.RectF;

public interface ChartData {

	public void setAxisX(Axis axisX);

	public Axis getAxisX();

	public void setAxisY(Axis axisY);

	public Axis getAxisY();

	public void setBoundaries(RectF boundaries);

	public RectF getBoundaries();

	public void calculateBoundaries();

}
