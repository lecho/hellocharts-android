package lecho.lib.hellocharts.model;

public interface ChartData {

	public void setAxisX(Axis axisX);

	public Axis getAxisX();

	public void setAxisY(Axis axisY);

	public Axis getAxisY();

	public int getAxesColor();

	public void setAxesColor(int axesColor);

	public int getAxesLabelTextSize();

	public void setAxesLabelTextSize(int axesTextSize);

	public int getValueLabelTextColor();

	public void setValueLabelsTextColor(int labelsTextColor);

	public int getValueLabelTextSize();

	public void setValueLabelTextSize(int labelsTextSize);

}
