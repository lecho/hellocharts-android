package lecho.lib.hellocharts.model;

import android.graphics.Canvas;
import android.graphics.Paint;

public class PathCompat {
	private float[] mPoints;
	// TODO: add index validations
	private int mIndex = 0;
	private boolean mMoved = false;

	public void moveTo(float x, float y) {
		mPoints[mIndex++] = x;
		mPoints[mIndex++] = y;
		mMoved = true;
	}

	public void lineTo(float x, float y) {
		if (mMoved) {
			mMoved = false;
			mPoints[mIndex++] = x;
			mPoints[mIndex++] = y;
		} else {
			int lastPointIndex = mIndex - 2;
			mPoints[mIndex++] = mPoints[lastPointIndex++];
			mPoints[mIndex++] = mPoints[lastPointIndex++];
			mPoints[mIndex++] = x;
			mPoints[mIndex++] = y;
		}
	}

	public void reset() {
		mIndex = 0;
		mMoved = false;
	}

	public void reset(int size) {
		mIndex = 0;
		mMoved = false;
		mPoints = new float[(size - 1) * 4];
	}

	public void drawPath(final Canvas canvas, final Paint paint) {
		canvas.drawLines(mPoints, paint);
	}
}
