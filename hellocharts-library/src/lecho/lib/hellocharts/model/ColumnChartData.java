package lecho.lib.hellocharts.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Data model for column chart. Note: you can set X value for columns or sub-columns, columns are by default indexed
 * from 0 to numOfColumns-1 and
 * column index is used as column X value, so first column has X value 0, second clumn has X value 1 etc.
 * If you want to display AxisValue for given column you should initialize AxisValue with X value of that column.
 */
public class ColumnChartData extends AbstractChartData {
    public static final float DEFAULT_FILL_RATIO = 0.75f;
    public static final float DEFAULT_BASE_VALUE = 0.0f;
    private float fillRatio = DEFAULT_FILL_RATIO;
    private float baseValue = DEFAULT_BASE_VALUE;
    private List<Column> columns = new ArrayList<Column>();
    private boolean isStacked = false;

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

    public static ColumnChartData generateDummyData() {
        final int numColumns = 4;
        ColumnChartData data = new ColumnChartData();
        List<Column> columns = new ArrayList<Column>(numColumns);
        List<SubcolumnValue> values;
        Column column;
        for (int i = 1; i <= numColumns; ++i) {
            values = new ArrayList<SubcolumnValue>(numColumns);
            values.add(new SubcolumnValue(i));
            column = new Column(values);
            columns.add(column);
        }

        data.setColumns(columns);
        return data;
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

    /**
     * Set true if you want stacked column chart.
     *
     * @param isStacked
     * @return
     */
    public ColumnChartData setStacked(boolean isStacked) {
        this.isStacked = isStacked;
        return this;
    }

    public float getFillRatio() {
        return fillRatio;
    }

    /**
     * Set fill ration for columns, value from 0 to 1, 1 means that there will be almost no free space between columns,
     * 0 means that columns will have minimum width(2px).
     *
     * @param fillRatio
     * @return
     */
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

    /**
     * @see #setBaseValue(float)
     */
    public float getBaseValue() {
        return baseValue;
    }

    /**
     * Set value below which values will be drawn as negative, by default 0.
     */
    public ColumnChartData setBaseValue(float baseValue) {
        this.baseValue = baseValue;
        return this;
    }

}
