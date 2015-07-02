package lecho.lib.hellocharts.model;

import android.graphics.PathEffect;

import java.util.ArrayList;
import java.util.List;

import lecho.lib.hellocharts.formatter.LineChartValueFormatter;
import lecho.lib.hellocharts.formatter.SimpleLineChartValueFormatter;
import lecho.lib.hellocharts.util.ChartUtils;
import lecho.lib.hellocharts.view.Chart;

/**
 * Single line for line chart.
 */
public class Line {
    private static final int DEFAULT_LINE_STROKE_WIDTH_DP = 3;
    private static final int DEFAULT_POINT_RADIUS_DP = 6;
    private static final int DEFAULT_AREA_TRANSPARENCY = 64;
    public static final int UNINITIALIZED = 0;
    private int color = ChartUtils.DEFAULT_COLOR;
    private int pointColor = UNINITIALIZED;
    private int darkenColor = ChartUtils.DEFAULT_DARKEN_COLOR;
    /**
     * Transparency of area when line is filled. *
     */
    private int areaTransparency = DEFAULT_AREA_TRANSPARENCY;
    private int strokeWidth = DEFAULT_LINE_STROKE_WIDTH_DP;
    private int pointRadius = DEFAULT_POINT_RADIUS_DP;
    private boolean hasPoints = true;
    private boolean hasLines = true;
    private boolean hasLabels = false;
    private boolean hasLabelsOnlyForSelected = false;
    private boolean isCubic = false;
    private boolean isSquare = false;
    private boolean isFilled = false;
    private ValueShape shape = ValueShape.CIRCLE;
    private PathEffect pathEffect;
    private LineChartValueFormatter formatter = new SimpleLineChartValueFormatter();
    private List<PointValue> values = new ArrayList<PointValue>();

    public Line() {

    }

    public Line(List<PointValue> values) {
        setValues(values);
    }

    public Line(Line line) {
        this.color = line.color;
        this.pointColor = line.pointColor;
        this.darkenColor = line.darkenColor;
        this.areaTransparency = line.areaTransparency;
        this.strokeWidth = line.strokeWidth;
        this.pointRadius = line.pointRadius;
        this.hasPoints = line.hasPoints;
        this.hasLines = line.hasLines;
        this.hasLabels = line.hasLabels;
        this.hasLabelsOnlyForSelected = line.hasLabelsOnlyForSelected;
        this.isSquare = line.isSquare;
        this.isCubic = line.isCubic;
        this.isFilled = line.isFilled;
        this.shape = line.shape;
        this.pathEffect = line.pathEffect;
        this.formatter = line.formatter;

        for (PointValue pointValue : line.values) {
            this.values.add(new PointValue(pointValue));
        }
    }

    public void update(float scale) {
        for (PointValue value : values) {
            value.update(scale);
        }
    }

    public void finish() {
        for (PointValue value : values) {
            value.finish();
        }
    }

    public List<PointValue> getValues() {
        return this.values;
    }

    public void setValues(List<PointValue> values) {
        if (null == values) {
            this.values = new ArrayList<PointValue>();
        } else {
            this.values = values;
        }
    }

    public int getColor() {
        return color;
    }

    public Line setColor(int color) {
        this.color = color;
        if (pointColor == UNINITIALIZED) {
            this.darkenColor = ChartUtils.darkenColor(color);
        }
        return this;
    }

    public int getPointColor() {
        if (pointColor == UNINITIALIZED) {
            return color;
        } else {
            return pointColor;
        }
    }

    public Line setPointColor(int pointColor) {
        this.pointColor = pointColor;
        if (pointColor == UNINITIALIZED) {
            this.darkenColor = ChartUtils.darkenColor(color);
        } else {
            this.darkenColor = ChartUtils.darkenColor(pointColor);
        }
        return this;
    }

    public int getDarkenColor() {
        return darkenColor;
    }

    /**
     * @see #setAreaTransparency(int)
     */
    public int getAreaTransparency() {
        return areaTransparency;
    }

    /**
     * Set area transparency(255 is full opacity) for filled lines
     *
     * @param areaTransparency
     * @return
     */
    public Line setAreaTransparency(int areaTransparency) {
        this.areaTransparency = areaTransparency;
        return this;
    }

    public int getStrokeWidth() {
        return strokeWidth;
    }

    public Line setStrokeWidth(int strokeWidth) {
        this.strokeWidth = strokeWidth;
        return this;
    }

    public boolean hasPoints() {
        return hasPoints;
    }

    public Line setHasPoints(boolean hasPoints) {
        this.hasPoints = hasPoints;
        return this;
    }

    public boolean hasLines() {
        return hasLines;
    }

    public Line setHasLines(boolean hasLines) {
        this.hasLines = hasLines;
        return this;
    }

    public boolean hasLabels() {
        return hasLabels;
    }

    public Line setHasLabels(boolean hasLabels) {
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
    public Line setHasLabelsOnlyForSelected(boolean hasLabelsOnlyForSelected) {
        this.hasLabelsOnlyForSelected = hasLabelsOnlyForSelected;
        if (hasLabelsOnlyForSelected) {
            this.hasLabels = false;
        }
        return this;
    }

    public int getPointRadius() {
        return pointRadius;
    }

    /**
     * Set radius for points for this line.
     *
     * @param pointRadius
     * @return
     */
    public Line setPointRadius(int pointRadius) {
        this.pointRadius = pointRadius;
        return this;
    }

    public boolean isCubic() {
        return isCubic;
    }

    public Line setCubic(boolean isCubic) {
        this.isCubic = isCubic;
        if (isSquare)
            setSquare(false);
        return this;
    }

    public boolean isSquare() {
        return isSquare;
    }

    public Line setSquare(boolean isSquare) {
        this.isSquare = isSquare;
        if (isCubic)
            setCubic(false);
        return this;
    }

    public boolean isFilled() {
        return isFilled;
    }

    public Line setFilled(boolean isFilled) {
        this.isFilled = isFilled;
        return this;
    }

    /**
     * @see #setShape(ValueShape)
     */
    public ValueShape getShape() {
        return shape;
    }

    /**
     * Set shape for points, possible values: SQUARE, CIRCLE
     *
     * @param shape
     * @return
     */
    public Line setShape(ValueShape shape) {
        this.shape = shape;
        return this;
    }

    public PathEffect getPathEffect() {
        return pathEffect;
    }

    /**
     * Set path effect for this line, note: it will slow down drawing, try to not use complicated effects,
     * DashPathEffect should be safe choice.
     *
     * @param pathEffect
     */
    public void setPathEffect(PathEffect pathEffect) {
        this.pathEffect = pathEffect;
    }

    public LineChartValueFormatter getFormatter() {
        return formatter;
    }

    public Line setFormatter(LineChartValueFormatter formatter) {
        if (null != formatter) {
            this.formatter = formatter;
        }
        return this;
    }
}
