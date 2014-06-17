package lecho.lib.hellocharts.model;

import java.util.Collections;
import java.util.List;

import android.graphics.Color;

public class Line {
	private static final int DEFAULT_LINE_VALUE_TEXT_SIZE_DP = 10;
	public List<LinePoint> points = Collections.emptyList();
	public int color = Color.GREEN;
	public int textSize = DEFAULT_LINE_VALUE_TEXT_SIZE_DP;
	public LineValueFormatter formatter = new DefaultLineValueFormatter();
	// TODO: replace boolean flags with something else
	public boolean isFilled = false;
	public boolean hasPoints = false;
	public boolean hasValuesPopups = false;
	public boolean isSmooth = false;

	public Line(List<LinePoint> points) {
		this.points = points;
	}

	public void setPoints(List<LinePoint> points) {
		this.points = points;
	}

	public List<LinePoint> getPoints() {
		return this.points;
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

}
