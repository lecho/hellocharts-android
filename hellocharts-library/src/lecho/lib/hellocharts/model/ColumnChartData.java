package lecho.lib.hellocharts.model;

import java.util.ArrayList;
import java.util.List;

public class ColumnChartData extends AbstractChartData {
	public static final float DEFAULT_FILL_RATIO = 0.75f;
	private List<Column> columns = new ArrayList<Column>();
	private boolean mIsStacked = false;
	private float fillRatio = DEFAULT_FILL_RATIO;

	@Override
	public void calculateBoundaries() {
		if (mManualBoundaries) {
			return;
		}
		mBoundaries.set(-0.5f, 0, columns.size() - 0.5f, 0);
		if (mIsStacked) {
			calculateBoundariesStacked();
		} else {
			calculateBoundariesDefault();
		}
	}

	public void calculateBoundariesDefault() {
		for (Column column : columns) {
			for (ColumnValue columnValue : column.getValues()) {
				if (columnValue.getValue() >= 0 && columnValue.getValue() > mBoundaries.top) {
					mBoundaries.top = columnValue.getValue();
				}
				if (columnValue.getValue() < 0 && columnValue.getValue() < mBoundaries.bottom) {
					mBoundaries.bottom = columnValue.getValue();
				}
			}
		}
	}

	public void calculateBoundariesStacked() {
		for (Column column : columns) {
			float sumPositive = 0;
			float sumNegative = 0;
			for (ColumnValue animatedValue : column.getValues()) {
				if (animatedValue.getValue() >= 0) {
					sumPositive += animatedValue.getValue();
				} else {
					sumNegative += animatedValue.getValue();
				}
			}
			if (sumPositive > mBoundaries.top) {
				mBoundaries.top = sumPositive;
			}
			if (sumNegative < mBoundaries.bottom) {
				mBoundaries.bottom = sumNegative;
			}
		}
	}

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
