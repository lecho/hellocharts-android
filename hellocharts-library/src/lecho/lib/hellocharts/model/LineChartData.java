package lecho.lib.hellocharts.model;

import java.util.Collections;
import java.util.List;

public class LineChartData extends AbstractChartData {

	public List<Line> lines = Collections.emptyList();

	@Override
	public void calculateBoundaries() {
		if (mManualBoundaries) {
			return;
		}
		mBoundaries.set(Float.MAX_VALUE, Float.MIN_VALUE, Float.MIN_VALUE, Float.MAX_VALUE);
		// TODO: optimize with 3/2 algo
		for (Line line : lines) {
			for (LinePoint linePoint : line.points) {
				if (linePoint.getX() < mBoundaries.left) {
					mBoundaries.left = linePoint.getX();
				}
				if (linePoint.getX() > mBoundaries.right) {
					mBoundaries.right = linePoint.getX();
				}
				if (linePoint.getY() < mBoundaries.bottom) {
					mBoundaries.bottom = linePoint.getY();
				}
				if (linePoint.getY() > mBoundaries.top) {
					mBoundaries.top = linePoint.getY();
				}

			}
		}
	}
}
