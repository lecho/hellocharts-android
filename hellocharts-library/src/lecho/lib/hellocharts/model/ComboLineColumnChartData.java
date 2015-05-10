package lecho.lib.hellocharts.model;

/**
 * Data model for combo line-column chart. It uses ColumnChartData and LineChartData internally.
 */
public class ComboLineColumnChartData extends AbstractChartData {

    private ColumnChartData columnChartData;
    private LineChartData lineChartData;

    public ComboLineColumnChartData() {
        this.columnChartData = new ColumnChartData();
        this.lineChartData = new LineChartData();
    }

    public ComboLineColumnChartData(ColumnChartData columnChartData, LineChartData lineChartData) {
        setColumnChartData(columnChartData);
        setLineChartData(lineChartData);
    }

    public ComboLineColumnChartData(ComboLineColumnChartData data) {
        super(data);

        setColumnChartData(new ColumnChartData(data.getColumnChartData()));
        setLineChartData(new LineChartData(data.getLineChartData()));
    }

    public static ComboLineColumnChartData generateDummyData() {
        ComboLineColumnChartData data = new ComboLineColumnChartData();
        data.setColumnChartData(ColumnChartData.generateDummyData());
        data.setLineChartData(LineChartData.generateDummyData());
        return data;
    }

    @Override
    public void update(float scale) {
        columnChartData.update(scale);
        lineChartData.update(scale);
    }

    @Override
    public void finish() {
        columnChartData.finish();
        lineChartData.finish();
    }

    public ColumnChartData getColumnChartData() {
        return columnChartData;
    }

    public void setColumnChartData(ColumnChartData columnChartData) {
        if (null == columnChartData) {
            this.columnChartData = new ColumnChartData();
        } else {
            this.columnChartData = columnChartData;
        }
    }

    public LineChartData getLineChartData() {
        return lineChartData;
    }

    public void setLineChartData(LineChartData lineChartData) {
        if (null == lineChartData) {
            this.lineChartData = new LineChartData();
        } else {
            this.lineChartData = lineChartData;
        }
    }

}
