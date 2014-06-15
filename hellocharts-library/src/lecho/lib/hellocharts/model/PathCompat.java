package lecho.lib.hellocharts.model;

import lecho.lib.hellocharts.ChartCalculator;
import android.graphics.Canvas;
import android.graphics.Paint;

public class PathCompat {
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
}
