package lecho.lib.hellocharts.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.graphics.Color;

public class Line {
	private static final int DEFAULT_LINE_VALUE_TEXT_SIZE_DP = 10;
	public List<AnimatedPoint> animatedPoints = Collections.emptyList();
	public int color = Color.GREEN;
	public int textSize = DEFAULT_LINE_VALUE_TEXT_SIZE_DP;
	public LineValueFormatter formatter = new DefaultLineValueFormatter();

	public Line(List<Point> points) {
		setPoints(points);
	}

	private void setPoints(List<Point> points) {
		animatedPoints = new ArrayList<AnimatedPoint>(points.size());
		for (Point point : points) {
			animatedPoints.add(new AnimatedPoint(point));
		}
	}

	public interface LineValueFormatter {
		public static final String DEFAULT_LINE_VALUE_FORMAT = "%.0f";

		public String formatValue(Point value);
	}

	public static class DefaultLineValueFormatter implements LineValueFormatter {

		@Override
		public String formatValue(Point value) {
			return String.format(DEFAULT_LINE_VALUE_FORMAT, value.y);
		}

	}

}
