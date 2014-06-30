package lecho.lib.hellocharts.model;

import java.util.Collections;
import java.util.List;

import lecho.lib.hellocharts.ChartCalculator;
import lecho.lib.hellocharts.LineChartRenderer;

public class LineChartData extends AbstractChartData {

	public List<Line> lines = Collections.emptyList();
	private int pointAdditionalMargin;

	@Override
	public void calculateBoundaries() {
		// margin should be always calculated
		calculatePointAdditionalMargin();

		if (mManualBoundaries) {
			return;
		}
		mBoundaries.set(Float.MAX_VALUE, Float.MIN_VALUE, Float.MIN_VALUE, Float.MAX_VALUE);
		// TODO: optimize
		for (Line line : lines) {
			for (LinePoint linePoint : line.getPoints()) {
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

	/**
	 * Method specific for LineChart that why it is not declared in ChartData interface. I have to know max point radius
	 * for whole data-set to determine margin, that prevents cutting of points that are too close to chart boundaries.
	 */
	private void calculatePointAdditionalMargin() {
		pointAdditionalMargin = ChartCalculator.DEFAULT_COMMON_MARGIN_DP;
		for (Line line : lines) {
			if (line.hasPoints()) {
				int pointMargin = line.getPointRadius() + LineChartRenderer.DEFAULT_TOUCH_TOLLERANCE_DP;
				if (pointMargin > this.pointAdditionalMargin) {
					this.pointAdditionalMargin = pointMargin;
				}
			}
		}
	}

	public int getPointAdditionalMargin() {
		return pointAdditionalMargin;
	}
}
