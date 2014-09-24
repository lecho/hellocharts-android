package lecho.lib.hellocharts.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import lecho.lib.hellocharts.util.Utils;

/**
 * Single line for line chart.
 * 
 * @author Leszek Wach
 * 
 */
public class Line {
	private static final int DEFAULT_LINE_STROKE_WIDTH_DP = 3;
	private static final int DEFAULT_POINT_RADIUS_DP = 6;
	private static final int DEFAULT_AREA_TRANSPARENCY = 64;
	private int color = Utils.DEFAULT_COLOR;
	private int darkenColor = Utils.DEFAULT_DARKEN_COLOR;
	private int areaTransparency = DEFAULT_AREA_TRANSPARENCY;
	private int strokeWidth = DEFAULT_LINE_STROKE_WIDTH_DP;
	private int pointRadius = DEFAULT_POINT_RADIUS_DP;
	private boolean hasPoints = true;
	private boolean hasLines = true;
	private boolean hasLabels = false;
	private boolean hasLabelsOnlyForSelected = false;
	private boolean isCubic = false;
	private boolean isFilled = false;
	private ValueShape pointShape = ValueShape.CIRCLE;
	private ValueFormatter formatter = new SimpleValueFormatter();
	private List<PointValue> values = new ArrayList<PointValue>();

	public Line() {

	}

	public Line(List<PointValue> values) {
		setValues(values);
	}

	public Line(Line line) {
		this.color = line.color;
		this.darkenColor = line.color;
		this.areaTransparency = line.areaTransparency;
		this.strokeWidth = line.strokeWidth;
		this.pointRadius = line.pointRadius;
		this.hasPoints = line.hasPoints;
		this.hasLines = line.hasLines;
		this.hasLabels = line.hasLabels;
		this.hasLabelsOnlyForSelected = line.hasLabelsOnlyForSelected;
		this.isCubic = line.isCubic;
		this.isFilled = line.isFilled;
		this.pointShape = line.pointShape;
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

	public void setValues(List<PointValue> values) {
		if (null == values) {
			this.values = Collections.emptyList();
		} else {
			this.values = values;
		}
	}

	public List<PointValue> getValues() {
		return this.values;
	}

	public int getColor() {
		return color;
	}

	public Line setColor(int color) {
		this.color = color;
		this.darkenColor = Utils.darkenColor(color);
		return this;
	}

	public int getDarkenColor() {
		return darkenColor;
	}

	public int getAreaTransparency() {
		return areaTransparency;
	}

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

	public boolean hasLabelsOnlyForSelected() {
		return hasLabelsOnlyForSelected;
	}

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

	public Line setPointRadius(int pointRadius) {
		this.pointRadius = pointRadius;
		return this;
	}

	public boolean isCubic() {
		return isCubic;
	}

	public Line setCubic(boolean isCubic) {
		this.isCubic = isCubic;
		return this;
	}

	public boolean isFilled() {
		return isFilled;
	}

	public Line setFilled(boolean isFilled) {
		this.isFilled = isFilled;
		return this;
	}

	public ValueShape getPointShape() {
		return pointShape;
	}

	public Line setPointShape(ValueShape shape) {
		this.pointShape = shape;
		return this;
	}

	public ValueFormatter getFormatter() {
		return formatter;
	}

	public Line setFormatter(ValueFormatter formatter) {
		if (null == formatter) {
			this.formatter = new SimpleValueFormatter();
		} else {
			this.formatter = formatter;
		}
		return this;
	}
}
