package lecho.lib.hellocharts.model;

import java.util.Collections;
import java.util.List;

public class BarChartData extends AbstractChartData {

	public List<Bar> bars = Collections.emptyList();

	@Override
	public void calculateBoundaries() {
		if (mManualBoundaries) {
			return;
		}
		mBoundaries.set(-0.5f, Float.MIN_VALUE, bars.size() - 0.5f, 0);
		for (Bar bar : bars) {
			for (AnimatedValueWithColor animatedValue : bar.animatedValues) {
				if (animatedValue.value > mBoundaries.top) {
					mBoundaries.top = animatedValue.value;
				}

			}
		}
	}

	public void updateLineTarget(int index, List<Point> points) {
		// TODO check if points have exactly the same size as line under index
		// int pointIndex = 0;
		// for (AnimatedValueWithColor animatedValues : bars.get(index).animatedValues) {
		// animatedValues.targetPoint.set(points.get(pointIndex));
		// ++pointIndex;
		// }
	}

	public void updateLine(int index, List<Point> points) {
		// TODO check if points have exactly the same size as line under index
		// int pointIndex = 0;
		// for (AnimatedValueWithColor animatedValues : bars.get(index).animatedValues) {
		// animatedValues.point.set(points.get(pointIndex));
		// ++pointIndex;
		// }
	}
}
