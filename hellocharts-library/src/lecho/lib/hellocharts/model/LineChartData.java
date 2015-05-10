package lecho.lib.hellocharts.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Data model for LineChartView.
 */
public class LineChartData extends AbstractChartData {
    public static final float DEFAULT_BASE_VALUE = 0.0f;

    private List<Line> lines = new ArrayList<Line>();
    private float baseValue = DEFAULT_BASE_VALUE;

    public LineChartData() {

    }

    public LineChartData(List<Line> lines) {
        setLines(lines);
    }

    /**
     * Copy constructor to perform deep copy of chart data.
     */
    public LineChartData(LineChartData data) {
        super(data);
        this.baseValue = data.baseValue;

        for (Line line : data.lines) {
            this.lines.add(new Line(line));
        }
    }

    public static LineChartData generateDummyData() {
        final int numValues = 4;
        LineChartData data = new LineChartData();
        List<PointValue> values = new ArrayList<PointValue>(numValues);
        values.add(new PointValue(0, 2));
        values.add(new PointValue(1, 4));
        values.add(new PointValue(2, 3));
        values.add(new PointValue(3, 4));
        Line line = new Line(values);
        List<Line> lines = new ArrayList<Line>(1);
        lines.add(line);
        data.setLines(lines);
        return data;
    }

    @Override
    public void update(float scale) {
        for (Line line : lines) {
            line.update(scale);
        }
    }

    @Override
    public void finish() {
        for (Line line : lines) {
            line.finish();
        }
    }

    public List<Line> getLines() {
        return lines;
    }

    public LineChartData setLines(List<Line> lines) {
        if (null == lines) {
            this.lines = new ArrayList<Line>();
        } else {
            this.lines = lines;
        }
        return this;
    }

    /**
     * @see #setBaseValue(float)
     */
    public float getBaseValue() {
        return baseValue;
    }

    /**
     * Set value below which values will be drawn as negative, important attribute for drawing filled area charts, by
     * default 0.
     */
    public LineChartData setBaseValue(float baseValue) {
        this.baseValue = baseValue;
        return this;
    }
}
