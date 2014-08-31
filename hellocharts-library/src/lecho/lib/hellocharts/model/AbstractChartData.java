package lecho.lib.hellocharts.model;

import android.graphics.Color;

/**
 * Base class for most chart data models.
 * 
 * @author Leszek Wach
 * 
 */
public abstract class AbstractChartData implements ChartData {
	public static final int DEFAULT_TEXT_SIZE_SP = 12;
	protected Axis axisXBottom;
	protected Axis axisYLeft;
	protected Axis axisXTop;
	protected Axis axisYRight;
	protected int valueLabelTextColor = Color.WHITE;
	protected int valueLabelTextSize = DEFAULT_TEXT_SIZE_SP;

	public AbstractChartData() {

	}

	public AbstractChartData(AbstractChartData data) {
		if (null != data.axisXBottom) {
			this.axisXBottom = new Axis(data.axisXBottom);
		}
		if (null != data.axisXTop) {
			this.axisXTop = new Axis(data.axisXTop);
		}
		if (null != data.axisYLeft) {
			this.axisYLeft = new Axis(data.axisYLeft);
		}
		if (null != data.axisYRight) {
			this.axisYRight = new Axis(data.axisYRight);
		}
		this.valueLabelTextColor = data.valueLabelTextColor;
		this.valueLabelTextSize = data.valueLabelTextSize;
	}

	@Override
	public void setAxisXBottom(Axis axisX) {
		this.axisXBottom = axisX;
	}

	@Override
	public Axis getAxisXBottom() {
		return axisXBottom;
	}

	@Override
	public void setAxisYLeft(Axis axisY) {
		this.axisYLeft = axisY;
	}

	@Override
	public Axis getAxisYLeft() {
		return axisYLeft;
	}

	@Override
	public void setAxisXTop(Axis axisX) {
		this.axisXTop = axisX;
	}

	@Override
	public Axis getAxisXTop() {
		return axisXTop;
	}

	@Override
	public void setAxisYRight(Axis axisY) {
		this.axisYRight = axisY;
	}

	@Override
	public Axis getAxisYRight() {
		return axisYRight;
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