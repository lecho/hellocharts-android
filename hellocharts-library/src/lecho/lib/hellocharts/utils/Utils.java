package lecho.lib.hellocharts.utils;

import android.content.Context;

public abstract class Utils {

	public static int dp2px(Context context, int dp) {
		// Get the screen's density scale
		final float scale = context.getResources().getDisplayMetrics().density;
		// Convert the dps to pixels, based on density scale
		return (int) (dp * scale + 0.5f);

	}

	public static int sp2px(Context context, int sp) {
		// Get the screen's density scale
		final float scale = context.getResources().getDisplayMetrics().scaledDensity;
		// Convert the sps to pixels, based on scale density scale
		return (int) (sp * scale + 0.5f);
	}

	public static boolean isInArea(float x, float y, float touchX, float touchY, float radius) {
		float diffX = touchX - x;
		float diffY = touchY - y;
		return Math.pow(diffX, 2) + Math.pow(diffY, 2) <= 2 * Math.pow(radius, 2);
	}

	/**
	 * Returns next bigger float value considering precision of the argument.
	 * 
	 */
	public static float nextUpF(float f) {
		if (Float.isNaN(f) || f == Float.POSITIVE_INFINITY) {
			return f;
		} else {
			f += 0.0f;
			return Float.intBitsToFloat(Float.floatToRawIntBits(f) + ((f >= 0.0f) ? +1 : -1));
		}
	}

	/**
	 * Returns next smaller float value considering precision of the argument.
	 * 
	 */
	public static float nextDownF(float f) {
		if (Float.isNaN(f) || f == Float.NEGATIVE_INFINITY) {
			return f;
		} else {
			if (f == 0.0f) {
				return -Float.MIN_VALUE;
			} else {
				return Float.intBitsToFloat(Float.floatToRawIntBits(f) + ((f > 0.0f) ? -1 : +1));
			}
		}
	}

	/**
	 * Checks how many "representable floats" exists between 'a' and 'b' and returns true if that number is less then
	 * maxUlps. If maxUlps = 1 this will have the same results as nextDawnF(a) >= a <= nextUpF(a)
	 * 
	 * @param a
	 * @param b
	 * @param maxUlps
	 *            maximum number of representable floats between "similar" floats
	 */
	public static boolean almostEqualF(float a, float b, int maxUlps) {
		if (a == b) {
			return true;
		}
		int intDiff = Math.abs(Float.floatToRawIntBits(a) - Float.floatToRawIntBits(b));
		if (intDiff < maxUlps) {
			return true;
		}
		return false;
	}
}
