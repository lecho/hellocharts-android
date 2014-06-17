package lecho.lib.hellocharts.model;

import java.util.Collections;
import java.util.List;

public class Line {
	public List<LinePoint> points = Collections.emptyList();
	private LineStyle style = new LineStyle();

	public Line(List<LinePoint> points) {
		this.points = points;
	}

	public void setPoints(List<LinePoint> points) {
		this.points = points;
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

		public void setColor(int color) {
			this.color = color;
		}

		public int getTextSize() {
			return textSize;
		}

		public void setTextSize(int textSize) {
			this.textSize = textSize;
		}

		public boolean isHasPoints() {
			return hasPoints;
		}

		public void setHasPoints(boolean hasPoints) {
			this.hasPoints = hasPoints;
		}

		public boolean isHasLines() {
			return hasLines;
		}

		public void setHasLines(boolean hasLines) {
			this.hasLines = hasLines;
		}

		public boolean isHasAnnotations() {
			return hasAnnotations;
		}

		public void setHasAnnotations(boolean hasAnnotations) {
			this.hasAnnotations = hasAnnotations;
		}

		public boolean isSmooth() {
			return isSmooth;
		}

		public void setSmooth(boolean isSmooth) {
			this.isSmooth = isSmooth;
		}

		public boolean isFilled() {
			return isFilled;
		}

		public void setFilled(boolean isFilled) {
			this.isFilled = isFilled;
		}

		public LineValueFormatter getLineValueFormatter() {
			return lineValueFormatter;
		}

		public void setLineValueFormatter(LineValueFormatter lineValueFormatter) {
			if (null == lineValueFormatter) {
				this.lineValueFormatter = new DefaultLineValueFormatter();
			} else {
				this.lineValueFormatter = lineValueFormatter;
			}
		}

	}

}
