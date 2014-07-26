package lecho.lib.hellocharts.model;

import java.util.ArrayList;
import java.util.List;

public class ColumnChartData extends AbstractChartData {
	public static final float DEFAULT_FILL_RATIO = 0.75f;
	private List<Column> columns = new ArrayList<Column>();
	private boolean mIsStacked = false;
	private float fillRatio = DEFAULT_FILL_RATIO;

	public List<Column> getColumns() {
		return columns;
	}

	public void setColumns(List<Column> columns) {
		if (null == columns) {
			this.columns = new ArrayList<Column>();
		} else {
			this.columns = columns;
		}
	}

	public boolean isStacked() {
		return mIsStacked;
	}

	public void setStacked(boolean isStacked) {
		this.mIsStacked = isStacked;
	}

	public float getFillRatio() {
		return fillRatio;
	}

	public void setFillRatio(float fillRatio) {
		if (fillRatio < 0) {
			fillRatio = 0;
		}
		if (fillRatio > 1) {
			fillRatio = 1;
		}
		this.fillRatio = fillRatio;
	}
}
