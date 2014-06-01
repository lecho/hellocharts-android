package lecho.lib.hellocharts.model;

import java.util.Collections;
import java.util.List;

import android.util.Log;

public class BarChartData extends AbstractChartData {

	public List<Bar> bars = Collections.emptyList();
	public boolean isStacked = false;

	@Override
	public void calculateBoundaries() {
		if (mManualBoundaries) {
			return;
		}
		mBoundaries.set(-0.5f, 0, bars.size() - 0.5f, 0);
		if (isStacked) {
			calculateBoundariesStacked();
		} else {
			calculateBoundariesDefault();
		}
	}

	public void calculateBoundariesDefault() {
		for (Bar bar : bars) {
			for (AnimatedValueWithColor animatedValue : bar.animatedValues) {
				if (animatedValue.value > mBoundaries.top) {
					mBoundaries.top = animatedValue.value;
				}

			}
		}
	}

	public void calculateBoundariesStacked() {
		for (Bar bar : bars) {
			float sumPositive = 0;
			float sumNegative = 0;
			for (AnimatedValueWithColor animatedValue : bar.animatedValues) {
				if (animatedValue.value >= 0) {
					sumPositive += animatedValue.value;
				} else {
					sumNegative += animatedValue.value;
				}
			}
			if (sumPositive > mBoundaries.top) {
				mBoundaries.top = sumPositive;
			}
			if (sumNegative < mBoundaries.bottom) {
				mBoundaries.bottom = sumNegative;
			}
		}
		Log.d("dupa", "boundaries: " + mBoundaries.toString());
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
