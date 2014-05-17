package lecho.lib.hellocharts.model;

import java.util.Collections;
import java.util.List;

public class BarChartData extends AbstractChartData {

	public List<Line> lines = Collections.emptyList();

	@Override
	public void calculateBoundaries() {
		if (mManualBoundaries) {
			return;
		}
		mBoundaries.set(Float.MAX_VALUE, Float.MIN_VALUE, Float.MIN_VALUE, Float.MAX_VALUE);
		// TODO: optimize with 3/2 algo
		for (Line line : lines) {
			for (AnimatedPoint animatedPoint : line.animatedPoints) {
				if (animatedPoint.point.x < mBoundaries.left) {
					mBoundaries.left = animatedPoint.point.x;
				}
				if (animatedPoint.point.x > mBoundaries.right) {
					mBoundaries.right = animatedPoint.point.x;
				}
				if (animatedPoint.point.y < mBoundaries.bottom) {
					mBoundaries.bottom = animatedPoint.point.y;
				}
				if (animatedPoint.point.y > mBoundaries.top) {
					mBoundaries.top = animatedPoint.point.y;
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
