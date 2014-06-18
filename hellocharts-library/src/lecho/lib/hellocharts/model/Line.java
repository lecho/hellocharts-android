package lecho.lib.hellocharts.model;

import java.util.Collections;
import java.util.List;

public class Line {
	private List<LinePoint> points = Collections.emptyList();
	private LineStyle style = new LineStyle();

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

	public LineStyle getStyle() {
		return style;
	}

	public void setStyle(LineStyle lineStyle) {
		if (null == lineStyle) {
			this.style = new LineStyle();
		} else {
			this.style = lineStyle;
		}

	}

	public interface LineValueFormatter {
		public static final String DEFAULT_LINE_VALUE_FORMAT = "%.0f";

		public String formatValue(LinePoint linePoint);
	}

	public static class DefaultLineValueFormatter implements LineValueFormatter {

		@Override
		public String formatValue(LinePoint linePoint) {
			return String.format(DEFAULT_LINE_VALUE_FORMAT, linePoint.getY());
		}
	}

	public static class LineStyle {
		private static final int DEFAULT_LINE_VALUE_TEXT_SIZE_DP = 10;
		private int color;
		private int textSize = DEFAULT_LINE_VALUE_TEXT_SIZE_DP;
		private boolean hasPoints = true;
		private boolean hasLines = true;
		private boolean hasAnnotations = false;
		private boolean isSmooth = false;
		private boolean isFilled = false;
		private LineValueFormatter lineValueFormatter = new DefaultLineValueFormatter();

		public int getColor() {
			return color;
		}

		public LineStyle setColor(int color) {
			this.color = color;
			return this;
		}

		public int getTextSize() {
			return textSize;
		}

		public LineStyle setTextSize(int textSize) {
			this.textSize = textSize;
			return this;
		}

		public boolean hasPoints() {
			return hasPoints;
		}

		public LineStyle setHasPoints(boolean hasPoints) {
			this.hasPoints = hasPoints;
			return this;
		}

		public boolean hasLines() {
			return hasLines;
		}

		public LineStyle setHasLines(boolean hasLines) {
			this.hasLines = hasLines;
			return this;
		}

		public boolean hasAnnotations() {
			return hasAnnotations;
		}

		public LineStyle setHasAnnotations(boolean hasAnnotations) {
			this.hasAnnotations = hasAnnotations;
			return this;
		}

		public boolean isSmooth() {
			return isSmooth;
		}

		public LineStyle setSmooth(boolean isSmooth) {
			this.isSmooth = isSmooth;
			return this;
		}

		public boolean isFilled() {
			return isFilled;
		}

		public LineStyle setFilled(boolean isFilled) {
			this.isFilled = isFilled;
			return this;
		}

		public LineValueFormatter getLineValueFormatter() {
			return lineValueFormatter;
		}

		public LineStyle setLineValueFormatter(LineValueFormatter lineValueFormatter) {
			if (null == lineValueFormatter) {
				this.lineValueFormatter = new DefaultLineValueFormatter();
			} else {
				this.lineValueFormatter = lineValueFormatter;
			}
			return this;
		}

	}

}
