package lecho.lib.hellocharts.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.annotation.SuppressLint;
import android.graphics.Color;

public class Line {
	public static final int DEFAULT_LINE_VALUE_TEXT_SIZE_SP = 10;
	public static final int DEFAULT_LINE_WIDTH_DP = 3;
	public static final int DEFAULT_POINT_RADIUS_DP = 6;
	public static final int DEFAULT_AREA_TRANSPARENCY = 64;
	private int color = Color.LTGRAY;
	private int textColor = Color.WHITE;
	private int textSize = DEFAULT_LINE_VALUE_TEXT_SIZE_SP;
	private int lineWidth = DEFAULT_LINE_WIDTH_DP;
	private int pointRadius = DEFAULT_POINT_RADIUS_DP;
	private int areaTransparency = DEFAULT_AREA_TRANSPARENCY;
	private boolean hasPoints = true;
	private boolean hasLines = true;
	private boolean hasLabels = false;
	private boolean isSmooth = false;
	private boolean isFilled = false;
	private LineValueFormatter lineValueFormatter = new DefaultLineValueFormatter();
	// TODO: consider Collections.emptyList()
	private List<LinePoint> points = new ArrayList<LinePoint>();

	public Line(List<LinePoint> points) {
		setPoints(points);
	}

	public void setPoints(List<LinePoint> points) {
		if (null == points) {
			this.points = Collections.emptyList();
		} else {
			this.points = points;
		}
	}

	public List<LinePoint> getPoints() {
		return this.points;
	}

	public int getColor() {
		return color;
	}

	public Line setColor(int color) {
		this.color = color;
		return this;
	}

	public int getTextColor() {
		return textColor;
	}

	public Line setTextColor(int textColor) {
		this.textColor = textColor;
		return this;
	}

	public int getTextSize() {
		return textSize;
	}

	public Line setTextSize(int textSize) {
		this.textSize = textSize;
		return this;
	}

	public int getLineWidth() {
		return lineWidth;
	}

	public Line setLineWidth(int lineWidth) {
		this.lineWidth = lineWidth;
		return this;
	}

	public int getPointRadius() {
		return pointRadius;
	}

	public Line setPointRadius(int pointRadius) {
		this.pointRadius = pointRadius;
		return this;
	}

	public int getAreaTransparency() {
		return areaTransparency;
	}

	public Line setAreaTransparency(int areaTransparency) {
		this.areaTransparency = areaTransparency;
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

	public LineValueFormatter getLineValueFormatter() {
		return lineValueFormatter;
	}

	public Line setLineValueFormatter(LineValueFormatter lineValueFormatter) {
		if (null == lineValueFormatter) {
			this.lineValueFormatter = new DefaultLineValueFormatter();
		} else {
			this.lineValueFormatter = lineValueFormatter;
		}
		return this;
	}

	public interface LineValueFormatter {
		public static final String DEFAULT_LINE_VALUE_FORMAT = "%.0f";

		public String formatValue(LinePoint linePoint);
	}

	@SuppressLint("DefaultLocale")
	public static class DefaultLineValueFormatter implements LineValueFormatter {

		@Override
		public String formatValue(LinePoint linePoint) {
			return String.format(DEFAULT_LINE_VALUE_FORMAT, linePoint.getY());
		}
	}

}
