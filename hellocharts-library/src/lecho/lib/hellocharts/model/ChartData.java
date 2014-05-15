package lecho.lib.hellocharts.model;

import android.graphics.RectF;

public interface ChartData {

	public Axis getAxisX();

	public Axis getAxisY();

	public void setBoundaries(RectF boundaries);

	public RectF getBoundaries();

	public void calculateBoundaries();

}
