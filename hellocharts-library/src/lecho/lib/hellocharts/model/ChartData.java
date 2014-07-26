package lecho.lib.hellocharts.model;

public interface ChartData {

	public void setAxisX(Axis axisX);

	public Axis getAxisX();

	public void setAxisY(Axis axisY);

	public Axis getAxisY();

	public int getAxesColor();

	public void setAxesColor(int axesColor);

	public int getAxesTextSize();

	public void setAxesTextSize(int axesTextSize);

	public int getLabelsTextColor();

	public void setLabelsTextColor(int labelsTextColor);

	public int getLabelsTextSize();

	public void setLabelsTextSize(int labelsTextSize);

}
