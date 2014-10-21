package lecho.lib.hellocharts.model;

import java.util.ArrayList;
import java.util.List;

import lecho.lib.hellocharts.view.Chart;

/**
 * Single column for ColumnChart. One column can be divided into multiple sub-columns(ColumnValues) especially for
 * stacked ColumnChart.
 * 
 */
public class Column {
	private boolean hasLabels = false;
	private boolean hasLabelsOnlyForSelected = false;
	private ValueFormatter formatter = new SimpleValueFormatter();
	// TODO: consider Collections.emptyList()
	private List<ColumnValue> values = new ArrayList<ColumnValue>();

	public Column() {

	}

	public Column(List<ColumnValue> values) {
		setValues(values);
	}

	public Column(Column column) {
		this.hasLabels = column.hasLabels;
		this.hasLabelsOnlyForSelected = column.hasLabelsOnlyForSelected;
		this.formatter = column.formatter;

		for (ColumnValue columnValue : column.values) {
			this.values.add(new ColumnValue(columnValue));
		}
	}

	public void update(float scale) {
		for (ColumnValue value : values) {
			value.update(scale);
		}

	}

	public void finish() {
		for (ColumnValue value : values) {
			value.finish();
		}
	}

	public List<ColumnValue> getValues() {
		return values;
	}

	public Column setValues(List<ColumnValue> values) {
		if (null == values) {
			this.values = new ArrayList<ColumnValue>();
		} else {
			this.values = values;
		}
		return this;
	}

	public boolean hasLabels() {
		return hasLabels;
	}

	public Column setHasLabels(boolean hasLabels) {
		this.hasLabels = hasLabels;
		if (hasLabels) {
			this.hasLabelsOnlyForSelected = false;
		}
		return this;
	}

	/**
	 * @see #setHasLabelsOnlyForSelected(boolean)
	 */
	public boolean hasLabelsOnlyForSelected() {
		return hasLabelsOnlyForSelected;
	}

	/**
	 * Set true if you want to show value labels only for selected value, works best when chart has
	 * isValueSelectionEnabled set to true {@link Chart#setValueSelectionEnabled(boolean)}.
	 */
	public Column setHasLabelsOnlyForSelected(boolean hasLabelsOnlyForSelected) {
		this.hasLabelsOnlyForSelected = hasLabelsOnlyForSelected;
		if (hasLabelsOnlyForSelected) {
			this.hasLabels = false;
		}
		return this;
	}

	public ValueFormatter getFormatter() {
		return formatter;
	}

	public Column setFormatter(ValueFormatter formatter) {
		if (null == formatter) {
			this.formatter = new SimpleValueFormatter();
		} else {
			this.formatter = formatter;
		}
		return this;
	}
}
