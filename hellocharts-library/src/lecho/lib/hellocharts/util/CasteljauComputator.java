package lecho.lib.hellocharts.util;

import android.graphics.PointF;

/**
 * Iterative implementation of de Casteljau's algorithm for cubic Bezier's curves.
 * 
 */
public class CasteljauComputator {
	private static final int DEFAULT_CURVE_DEGREE = 3;// By default cubic.

	private int pointsNum = DEFAULT_CURVE_DEGREE + 1;
	private int coordsNum = pointsNum * 2;
	/**
	 * This implementation uses single dimension array for storing partial results. If you need to see whole triangular
	 * computation scheme use two-dimensional array when first dimension is indexed by pointsNum(variable i in first FOR
	 * loop) and second dimension is indexed by coordsNum(variables ix and iy in second FOR loop).
	 */
	private float[] points;

	public CasteljauComputator() {
		points = new float[coordsNum];
	}

	public CasteljauComputator(int curveDegree) {
		if (curveDegree < 1) {
			curveDegree = 1;
		}
		pointsNum = curveDegree + 1;
		coordsNum = pointsNum * 2;
		points = new float[coordsNum];
	}

	public void computePoint(float t, float[] startPoints, PointF outPoint) {
		if (startPoints.length != coordsNum) {
			throw new IllegalArgumentException(
					"Invalid points number, points array should have (curveDegree+1) * 2 values");
		}

		// Copy first raw of points into this.points[0] array.
		System.arraycopy(startPoints, 0, points, 0, coordsNum);

		for (int i = 1, length = coordsNum - 2; i < pointsNum; ++i, length -= 2) {

			for (int ix = 0, iy = 1; iy <= length; ix += 2, iy += 2) {

				// X value.
				points[ix] = (1 - t) * points[ix] + t * points[ix + 2];
				// Y value.
				points[iy] = (1 - t) * points[iy] + t * points[iy + 2];

			}

		}

		outPoint.set(points[0], points[1]);
	}
}
