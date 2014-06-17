package lecho.lib.hellocharts.model;

import lecho.lib.hellocharts.ChartCalculator;
import lecho.lib.hellocharts.utils.Utils;
import android.graphics.Canvas;
import android.graphics.Paint;

public class PathCompat {
	private static final float LINE_SMOOTHNES = 0.16f;
	private static final int STEP_FOR_SMOOTH_LINE_MM = 4;
	// Preallocated place for one path segment to avoid allocation during onDraw().
	private float[] mSegment = new float[4];

	// TODO: add index validations

	public void cubicTo(float firstControlPointX, float firstControlPointY, float secondControlPointX,
			float secondControlPointY, float nextPointX, float nextPointY) {

	}

	public void drawPath(final Canvas canvas, ChartCalculator chartCalculator, final Line line, final Paint paint) {
		int valueIndex = 0;
		for (AnimatedPoint animatedPoint : line.animatedPoints) {
			final float rawValueX = chartCalculator.calculateRawX(animatedPoint.point.x);
			final float rawValueY = chartCalculator.calculateRawY(animatedPoint.point.y);
			if (valueIndex == 0) {
				mSegment[0] = rawValueX;
				mSegment[1] = rawValueY;
				mSegment[2] = rawValueX;
				mSegment[3] = rawValueY;
			} else {
				mSegment[0] = mSegment[2];
				mSegment[1] = mSegment[3];
				mSegment[2] = rawValueX;
				mSegment[3] = rawValueY;
				canvas.drawLine(mSegment[0], mSegment[1], mSegment[2], mSegment[3], paint);
			}
			++valueIndex;
		}
	}

	private float calculateBezierValue(float x, float p0, float p1, float p2, float p3) {
		final float u = 1 - x;
		final float tt = x * x;
		final float uu = u * u;
		final float uuu = uu * u;
		final float ttt = tt * x;

		float result = uuu * p0; // first term
		result += 3 * uu * x * p1; // second term
		result += 3 * u * tt * p2; // third term
		result += ttt * p3; // fourth term

		return result;
	}

	public void drawCubicPath(final Canvas canvas, ChartCalculator chartCalculator, final Line line, final Paint paint) {
		int step = 6;
		final int lineSize = line.animatedPoints.size();
		float previousPointX = Float.NaN;
		float previousPointY = Float.NaN;
		float currentPointX = Float.NaN;
		float currentPointY = Float.NaN;
		float nextPointX = Float.NaN;
		float nextPointY = Float.NaN;
		for (int valueIndex = 0; valueIndex < lineSize; ++valueIndex) {
			if (Float.isNaN(currentPointX)) {
				AnimatedPoint animatedPoint = line.animatedPoints.get(valueIndex);
				currentPointX = chartCalculator.calculateRawX(animatedPoint.point.x);
				currentPointY = chartCalculator.calculateRawY(animatedPoint.point.y);
			}
			if (Float.isNaN(previousPointX)) {
				if (valueIndex > 0) {
					AnimatedPoint animatedPoint = line.animatedPoints.get(valueIndex - 1);
					previousPointX = chartCalculator.calculateRawX(animatedPoint.point.x);
					previousPointY = chartCalculator.calculateRawY(animatedPoint.point.y);
				} else {
					previousPointX = currentPointX;
					previousPointY = currentPointY;
				}
			}
			if (Float.isNaN(nextPointX)) {
				if (valueIndex < lineSize - 1) {
					AnimatedPoint animatedPoint = line.animatedPoints.get(valueIndex + 1);
					nextPointX = chartCalculator.calculateRawX(animatedPoint.point.x);
					nextPointY = chartCalculator.calculateRawY(animatedPoint.point.y);
				} else {
					nextPointX = currentPointX;
					nextPointY = currentPointY;
				}
			}
			// afterNextPoint is always new one or it is equal nextPoint.
			final float afterNextPointX;
			final float afterNextPointY;
			if (valueIndex < lineSize - 2) {
				AnimatedPoint animatedPoint = line.animatedPoints.get(valueIndex + 2);
				afterNextPointX = chartCalculator.calculateRawX(animatedPoint.point.x);
				afterNextPointY = chartCalculator.calculateRawY(animatedPoint.point.y);
			} else {
				afterNextPointX = nextPointX;
				afterNextPointY = nextPointY;
			}
			// Calculate control points.
			final float firstDiffX = (nextPointX - previousPointX);
			final float firstDiffY = (nextPointY - previousPointY);
			final float secondDiffX = (afterNextPointX - currentPointX);
			final float secondDiffY = (afterNextPointY - currentPointY);
			final float firstControlPointX = currentPointX + (LINE_SMOOTHNES * firstDiffX);
			final float firstControlPointY = currentPointY + (LINE_SMOOTHNES * firstDiffY);
			final float secondControlPointX = nextPointX - (LINE_SMOOTHNES * secondDiffX);
			final float secondControlPointY = nextPointY - (LINE_SMOOTHNES * secondDiffY);
			// Move to start point.
			// if (valueIndex == 0) {
			// mLinePath.moveTo(currentPointX, currentPointY);
			// }
			// mLinePath.cubicTo(firstControlPointX, firstControlPointY, secondControlPointX, secondControlPointY,
			// nextPointX, nextPointY);
			// Shift values by one to prevent recalculation of values that have been already calculated.
			if (valueIndex > 0) {
				int numSteps = (int) (currentPointX - previousPointX) / step;
				for (int i = 0; i <= numSteps; ++i) {
					if (i == 0) {
						mSegment[0] = previousPointX;
						mSegment[1] = previousPointY;
						mSegment[2] = previousPointX;
						mSegment[3] = previousPointY;
					} else {
						float t = i / (float) numSteps;
						mSegment[0] = mSegment[2];
						mSegment[1] = mSegment[3];
						mSegment[2] = calculateBezierValue(t, previousPointX, firstControlPointX, secondControlPointX,
								currentPointX);
						mSegment[3] = calculateBezierValue(t, previousPointY, firstControlPointY, secondControlPointY,
								currentPointY);
						canvas.drawLine(mSegment[0], mSegment[1], mSegment[2], mSegment[3], paint);
					}
				}
			}
			previousPointX = currentPointX;
			previousPointY = currentPointY;
			currentPointX = nextPointX;
			currentPointY = nextPointY;
			nextPointX = afterNextPointX;
			nextPointY = afterNextPointY;
		}
		// mLinePaint.setColor(line.color);
		// canvas.drawPath(mLinePath, mLinePaint);
		//
		// if (line.isFilled) {
		// drawArea(canvas);
		// }
	}
}
