package lecho.lib.hellocharts.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Data model for column chart.
 * 
 * @author Leszek Wach
 * 
 */
public class ColumnChartData extends AbstractChartData {
	public static final float DEFAULT_FILL_RATIO = 0.75f;
	private List<Column> columns = new ArrayList<Column>();
	private boolean isStacked = false;
	private float fillRatio = DEFAULT_FILL_RATIO;

	public ColumnChartData() {
	}

	public ColumnChartData(List<Column> columns) {
		setColumns(columns);
	}

	/**
	 * Copy constructor for deep copy.
	 */
	public ColumnChartData(ColumnChartData data) {
		super(data);
		this.isStacked = data.isStacked;
		this.fillRatio = data.fillRatio;

		for (Column column : data.columns) {
			this.columns.add(new Column(column));
		}
	}

	@Override
	public void update(float scale) {
		for (Column column : columns) {
			column.update(scale);
		}

	}

	@Override
	public void finish() {
		for (Column column : columns) {
			column.finish();
		}
	}

	public List<Column> getColumns() {
		return columns;
	}

	public ColumnChartData setColumns(List<Column> columns) {
		if (null == columns) {
			this.columns = new ArrayList<Column>();
		} else {
			this.columns = columns;
		}
		return this;
	}

	public boolean isStacked() {
		return isStacked;
	}

	public ColumnChartData setStacked(boolean isStacked) {
		this.isStacked = isStacked;
		return this;
	}

	public float getFillRatio() {
		return fillRatio;
	}

	public ColumnChartData setFillRatio(float fillRatio) {
		if (fillRatio < 0) {
			fillRatio = 0;
		}
		if (fillRatio > 1) {
			fillRatio = 1;
		}
		this.fillRatio = fillRatio;
		return this;
	}

	public static ColumnChartData generateDummyData() {
		final int numColumns = 4;
		ColumnChartData data = new ColumnChartData();
		List<Column> columns = new ArrayList<Column>(numColumns);
		List<ColumnValue> values;
		Column column;
		for (int i = 1; i <= numColumns; ++i) {
			values = new ArrayList<ColumnValue>(numColumns);
			values.add(new ColumnValue(i));
			column = new Column(values);
			columns.add(column);
		}

		data.setColumns(columns);
		return data;
	}

}
