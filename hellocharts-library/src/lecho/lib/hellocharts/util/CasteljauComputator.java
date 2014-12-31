package lecho.lib.hellocharts.util;

import android.graphics.PointF;

/**
 * Iterative implementation of de Casteljau's algorithm for cubic Bezier's curves.
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

    /**
     * Calculate the control points for a Bezier curve based on 3 points so the curve will pass
     *  for all the 3 points. Based on: scaledinnovation.com/analytics/splines/aboutSplines.html
     * @param t typically from 0 to 1
     * @return float[4] array containing the 2 control points (first x, then y)
     */
    public float[] computeControlPoints(float t, float x0, float y0, float x1, float y1, float x2, float y2){
        double d01 = Math.sqrt(Math.pow(x1-x0,2)+Math.pow(y1-y0,2));
        double d12 = Math.sqrt(Math.pow(x2-x1,2)+Math.pow(y2-y1,2));

        double fa = t*d01/(d01+d12);            // scaling factor for triangle Ta
        double fb = t*d12/(d01+d12);            // ditto for Tb, simplifies to fb=t-fa

        double p1x = x1-fa*(x2-x0);             // x2-x0 is the width of triangle T
        double p1y = y1-fa*(y2-y0);             // y2-y0 is the height of T
        double p2x = x1+fb*(x2-x0);
        double p2y = y1+fb*(y2-y0);

        return new float[]{(float)p1x, (float)p1y, (float)p2x, (float)p2y};
    }
}
