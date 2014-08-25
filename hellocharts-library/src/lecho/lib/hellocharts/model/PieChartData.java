package lecho.lib.hellocharts.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Data for PieChart, by default it doesn't have axes.
 * 
 * @author Leszek Wach
 * 
 */
public class PieChartData extends AbstractChartData {
	private ValueFormatter formatter = new NumberValueFormatter();
	private boolean hasLabels = false;
	private boolean hasLabelsOnlyForSelected = false;
	// TODO: consider Collections.emptyList()
	private List<ArcValue> values = new ArrayList<ArcValue>();

	public PieChartData() {
		this(null);
	};

	public PieChartData(List<ArcValue> values) {
		setValues(values);
		// Empty axes. Pie chart don't need axes.
		setAxisX(null);
		setAxisY(null);
	}

	/**
	 * PieChart does not support axes so method call will be ignored
	 */
	@Override
	public void setAxisX(Axis axisX) {
		super.setAxisX(null);
	}

	/**
	 * PieChart does not support axes so method call will be ignored
	 */
	@Override
	public void setAxisY(Axis axisY) {
		super.setAxisY(null);
	}

	public List<ArcValue> getValues() {
		return values;
	}

	public PieChartData setValues(List<ArcValue> values) {
		if (null == values) {
			this.values = new ArrayList<ArcValue>();
		} else {
			this.values = values;
		}
		return this;
	}

	public boolean hasLabels() {
		return hasLabels;
	}

	public PieChartData setHasLabels(boolean hasLabels) {
		this.hasLabels = hasLabels;
		if (hasLabels) {
			hasLabelsOnlyForSelected = false;
		}
		return this;
	}

	public boolean hasLabelsOnlyForSelected() {
		return hasLabelsOnlyForSelected;
	}

	public PieChartData setHasLabelsOnlyForSelected(boolean hasLabelsOnlyForSelected) {
		this.hasLabelsOnlyForSelected = hasLabelsOnlyForSelected;
		if (hasLabelsOnlyForSelected) {
			this.hasLabels = false;
		}
		return this;
	}

	public ValueFormatter getFormatter() {
		return formatter;
	}

	public PieChartData setFormatter(ValueFormatter formatter) {
		if (null == formatter) {
			this.formatter = new NumberValueFormatter();
		} else {
			this.formatter = formatter;
		}
		return this;
	}
}
