package lecho.lib.hellocharts.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import lecho.lib.hellocharts.util.Utils;

public class Line {
	public static final int SHAPE_CIRCLE = 1;
	public static final int SHAPE_SQUARE = 2;
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
	private boolean isSmooth = false;
	private boolean isFilled = false;
	private int pointShape = SHAPE_CIRCLE;
	private ValueFormatter formatter = new NumberValueFormatter();
	// TODO: consider Collections.emptyList()
	private List<PointValue> values = new ArrayList<PointValue>();

	public Line() {

	}

	public Line(List<PointValue> values) {
		setPoints(values);
	}

	public void setPoints(List<PointValue> values) {
		if (null == values) {
			this.values = Collections.emptyList();
		} else {
			this.values = values;
		}
	}

	public List<PointValue> getPoints() {
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

	public boolean isSmooth() {
		return isSmooth;
	}

	public Line setSmooth(boolean isSmooth) {
		this.isSmooth = isSmooth;
		return this;
	}

	public boolean isFilled() {
		return isFilled;
	}

	public Line setFilled(boolean isFilled) {
		this.isFilled = isFilled;
		return this;
	}

	public int getPointShape() {
		return pointShape;
	}

	public Line setPointShape(int pointShape) {
		if (SHAPE_SQUARE == pointShape) {
			this.pointShape = SHAPE_SQUARE;
		} else {
			this.pointShape = SHAPE_CIRCLE;
		}
		return this;
	}

	public ValueFormatter getFormatter() {
		return formatter;
	}

	public Line setFormatter(ValueFormatter formatter) {
		if (null == formatter) {
			this.formatter = new NumberValueFormatter();
		} else {
			this.formatter = formatter;
		}
		return this;
	}
}
