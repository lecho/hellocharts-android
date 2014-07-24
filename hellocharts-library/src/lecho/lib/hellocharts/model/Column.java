package lecho.lib.hellocharts.model;

import java.util.ArrayList;
import java.util.List;

public class Column {
	private boolean hasLabels = false;
	private ValueFormatter formatter = new NumberValueFormatter();
	// TODO: consider Collections.emptyList()
	private List<ColumnValue> values = new ArrayList<ColumnValue>();

	public Column(List<ColumnValue> values) {
		setValues(values);
	}

	public List<ColumnValue> getValues() {
		return values;
	}

	public void setValues(List<ColumnValue> values) {
		if (null == values) {
			this.values = new ArrayList<ColumnValue>();
		} else {
			this.values = values;
		}
	}

	public boolean hasLabels() {
		return hasLabels;
	}

	public Column setHasLabels(boolean hasLabels) {
		this.hasLabels = hasLabels;
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
