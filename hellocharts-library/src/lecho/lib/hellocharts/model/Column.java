package lecho.lib.hellocharts.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Single column for ColumnChart. One column can be divided into multiple sub-columns(ColumnValues) especially for
 * stacked ColumnChart.
 */
public class Column {
	private boolean hasLabels = false;
	private boolean hasLabelsOnlyForSelected = false;
	private ValueFormatter formatter = new NumberValueFormatter();
	// TODO: consider Collections.emptyList()
	private List<ColumnValue> values = new ArrayList<ColumnValue>();

	public Column(List<ColumnValue> values) {
		setValues(values);
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

	public boolean hasLabelsOnlyForSelected() {
		return hasLabelsOnlyForSelected;
	}

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
			this.formatter = new NumberValueFormatter();
		} else {
			this.formatter = formatter;
		}
		return this;
	}
}
