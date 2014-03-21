package lecho.lib.hellocharts.model;

import java.util.Collections;
import java.util.List;

public class Data {

	public List<Line> lines = Collections.emptyList();
	public Axis axisX = new Axis();
	public Axis axisY = new Axis();
	public float minXValue;
	public float maxXValue;
	public float minYValue;
	public float maxYValue;

	public Data() {
	}

	public void calculateRanges() {
		minXValue = Float.MAX_VALUE;
		maxXValue = Float.MIN_VALUE;
		minYValue = Float.MAX_VALUE;
		maxYValue = Float.MIN_VALUE;
		// TODO: optimize with 3/2 algo
		for (Line line : lines) {
			for (AnimatedPoint animatedPoint : line.animatedPoints) {
				if (animatedPoint.point.x < minXValue) {
					minXValue = animatedPoint.point.x;
				}
				if (animatedPoint.point.x > maxXValue) {
					maxXValue = animatedPoint.point.x;
				}
				if (animatedPoint.point.y < minYValue) {
					minYValue = animatedPoint.point.y;
				}
				if (animatedPoint.point.y > maxYValue) {
					maxYValue = animatedPoint.point.y;
				}

			}
		}
	}

	public void updateLineTarget(int index, List<Point> points) {
		// TODO check if points have exactly the same size as line under index
		int pointIndex = 0;
		for (AnimatedPoint animatedPoint : lines.get(index).animatedPoints) {
			animatedPoint.targetPoint.set(points.get(pointIndex));
			++pointIndex;
		}
	}

	public void updateLine(int index, List<Point> points) {
		// TODO check if points have exactly the same size as line under index
		int pointIndex = 0;
		for (AnimatedPoint animatedPoint : lines.get(index).animatedPoints) {
			animatedPoint.point.set(points.get(pointIndex));
			++pointIndex;
		}
	}
}
