package lecho.lib.hellocharts.util;

import android.graphics.RectF;

/**
 * Cohen-Sutherland algorithm implementation based on wikipedia article.
 * 
 * {@link http://en.wikipedia.org/wiki/Cohen-Sutherland_algorithm}
 * 
 */
public abstract class CohenSutherlandComputator {

	// Area codes
	public static final byte INSIDE = 0; // 0000
	public static final byte LEFT = 1; // 0001
	public static final byte RIGHT = 2; // 0010
	public static final byte BOTTOM = 4; // 0100
	public static final byte TOP = 8; // 1000

	public static byte computeCode(RectF clipRect, float x, float y) {

		byte code = INSIDE;

		if (x < clipRect.left) {
			code |= LEFT;
		} else if (x > clipRect.right) {
			code |= RIGHT;
		}

		// Keep in mind that rect bottom has bigger value in pixels.
		if (y < clipRect.top) {
			code |= TOP;
		} else if (y > clipRect.bottom) {
			code |= BOTTOM;
		}

		return code;
	}

	/**
	 * Performs Cohen-Sutherland clipping algorithm.
	 * 
	 * @param clipRect
	 * @param points
	 *            first and last point of the clipped line segment [x1,y1, x2,y2].
	 * @return true if line segment is clipped or is entirely inside clipRect, false if line segment has no intersection
	 *         points with clipRect.
	 */
	public static boolean clipLine(RectF clipRect, float[] points, ClipResult clippResult) {
		if (points.length != 4) {
			throw new IllegalArgumentException("Points array must have 4 values in format [x1,y1, x2,y2]");
		}

		float x1 = points[0];
		float y1 = points[1];
		float x2 = points[2];
		float y2 = points[3];

		// Area code for first line point.
		byte code1 = computeCode(clipRect, x1, y1);
		// Area code for second line point.
		byte code2 = computeCode(clipRect, x2, y2);

		boolean isClipped = false;

		while (true) {
			if ((code1 | code2) == INSIDE) {
				// Bitwise OR is 0. Trivially accept and get out of loop.
				isClipped = true;
				break;
			} else if ((code1 & code2) != INSIDE) {
				// Bitwise AND is not 0. Trivially reject and get out of loop.
				break;
			} else {
				// Failed both tests, so calculate the line segment to clip
				// from an outside point to an intersection with clip edge.
				final float clipX;
				final float clipY;

				// At least one end-point is outside the clip rectangle; pick it.
				final byte outCode;

				if (code1 != INSIDE) {
					outCode = code1;
					clippResult.isFirstClipped = true;
				} else {
					outCode = code2;
					clippResult.isSecondClipped = true;
				}

				// Find the intersection point;
				// Use formulas y = y0 + slope * (x - x0), x = x0 + (1 / slope) * (y - y0)
				if ((outCode & TOP) != INSIDE) {
					// Point is above the clip rectangle
					clipX = x1 + (x2 - x1) * (clipRect.top - y1) / (y2 - y1);
					clipY = clipRect.top;
				} else if ((outCode & BOTTOM) != INSIDE) {
					// Point is below the clip rectangle.
					clipX = x1 + (x2 - x1) * (clipRect.bottom - y1) / (y2 - y1);
					clipY = clipRect.bottom;
				} else if ((outCode & RIGHT) != INSIDE) {
					// Point is to the right of clip rectangle.
					clipY = y1 + (y2 - y1) * (clipRect.right - x1) / (x2 - x1);
					clipX = clipRect.right;
				} else if ((outCode & LEFT) != INSIDE) {
					// Point is to the left of clip rectangle.
					clipY = y1 + (y2 - y1) * (clipRect.left - x1) / (x2 - x1);
					clipX = clipRect.left;
				} else {
					clipX = Float.NaN;
					clipY = Float.NaN;
				}

				// Move outside point to intersection point to clip
				// and get ready for next pass.
				if (outCode == code1) {
					x1 = clipX;
					y1 = clipY;
					code1 = computeCode(clipRect, x1, y1);
				} else {
					x2 = clipX;
					y2 = clipY;
					code2 = computeCode(clipRect, x2, y2);
				}
			}
		}

		// Update points array with clipped line segment.
		points[0] = x1;
		points[1] = y1;
		points[2] = x2;
		points[3] = y2;

		return isClipped;
	}

	public static class ClipResult {
		public boolean isFirstClipped = false;
		public boolean isSecondClipped = false;

		public void reset() {
			isFirstClipped = false;
			isSecondClipped = false;
		}

		@Override
		public String toString() {
			return "ClipResult [isFirstClipped=" + isFirstClipped + ", isSecondClipped=" + isSecondClipped + "]";
		}

	}
}
