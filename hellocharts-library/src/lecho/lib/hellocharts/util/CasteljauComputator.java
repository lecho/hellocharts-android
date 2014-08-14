package lecho.lib.hellocharts.util;

import android.graphics.PointF;

/**
 * Iterative implementation of de Casteljau's algorithm for cubic Bezier's curves.
 * 
 * @author Leszek Wach
 * 
 */
public class CasteljauComputator {
	private static final int DEFAULT_CURVE_DEGREE = 3;// By default cubic.

	private int curveDegree = DEFAULT_CURVE_DEGREE;
	private int pointsNumber = (curveDegree + 1) * 2;
	// TODO: Use one-dimensional array.
	private float[][] points;

	public CasteljauComputator() {
		points = new float[curveDegree + 1][pointsNumber];
	}

	public CasteljauComputator(int curveDegree) {
		if (curveDegree < 1) {
			curveDegree = 1;
		}
		this.curveDegree = curveDegree;
		pointsNumber = (curveDegree + 1) * 2;
		points = new float[curveDegree + 1][pointsNumber];
	}

	public void computePoint(float t, float[] startPoints, PointF outPoint) {
		if (startPoints.length != pointsNumber) {
			throw new IllegalArgumentException(
					"Invalid points number, points array should have (curveDegree+1) * 2 values");
		}

		// Copy first raw of points into this.points[0] array.
		System.arraycopy(points[0], 0, startPoints, 0, pointsNumber);

		for (int i = 1, pointsIndex = pointsNumber; i < curveDegree; ++i, pointsIndex -= 2) {

			for (int indexX = 0, indexY = 1; indexY < pointsIndex; indexX += 2, indexY += 2) {

				// X value.
				points[i][indexX] = (1 - t) * points[i - 1][indexX] + t * points[i - 1][indexX + 2];
				// Y value.
				points[i][indexY] = (1 - t) * points[i - 1][indexY] + t * points[i - 1][indexY + 2];

			}
		}

		outPoint.set(points[curveDegree - 1][0], points[curveDegree - 1][1]);
	}
}
