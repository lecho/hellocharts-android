package lecho.lib.hellocharts.model;

/**
 * Base interface for all chart data models.
 */
public interface ChartData {

	public void setAxisX(Axis axisX);

	public Axis getAxisX();

	public void setAxisY(Axis axisY);

	public Axis getAxisY();

	/**
	 * Returns axes color. Both axes always have the same color.
	 */
	public int getAxesColor();

	/**
	 * Set both axes color.
	 */
	public void setAxesColor(int axesColor);

	/**
	 * Return axes label text size in SP units.
	 */
	public int getAxesLabelTextSize();

	/**
	 * Set axes label text size in SP units. Both axes will have the same test size.
	 */
	public void setAxesLabelTextSize(int axesTextSize);

	/**
	 * Returns color used to draw value label text.
	 */
	public int getValueLabelTextColor();

	/**
	 * Set value label text color, by default Color.WHITE.
	 */
	public void setValueLabelsTextColor(int labelsTextColor);

	/**
	 * Returns text size for value label in SP units.
	 */
	public int getValueLabelTextSize();

	/**
	 * Set text size for value label in SP units.
	 */
	public void setValueLabelTextSize(int labelsTextSize);

	/**
	 * Returns maximum axis label charts number.
	 */
	public int getMaxAxisLabelChars();

	/**
	 * Set max label charts, used only for auto-generated axes to determine width of labels. By default 5. Min 0, max
	 * 32.
	 */
	public void setMaxLabelChars(int numChars);

}
