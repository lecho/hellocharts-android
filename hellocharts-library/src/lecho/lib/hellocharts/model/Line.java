package lecho.lib.hellocharts.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.graphics.Color;

public class Line {

	public List<AnimatedPoint> animatedPoints = Collections.emptyList();
	public int color = Color.GREEN;

	public Line(List<Point> points) {
		setPoints(points);
	}

	private void setPoints(List<Point> points) {
		animatedPoints = new ArrayList<AnimatedPoint>(points.size());
		for (Point point : points) {
			animatedPoints.add(new AnimatedPoint(point));
		}
	}
}
