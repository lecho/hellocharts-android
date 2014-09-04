package lecho.lib.hellocharts.util;

import android.graphics.Canvas;
import android.graphics.Paint;

/**
 * Note: PathCompat is written for fun and play with clipping cubic lines but it works quite well so I left it here.
 * PathCompat uses Canvas.drawLines instead Canvas.drawPath. Supports only normal lines. Warning!:line has to be
 * continuous and doesn't support filled area, dashed lines etc. For complete implementation with Bezier's curves see
 * that gist {@link https://gist.github.com/lecho/a903e68fe7cccac131d0}
 */
public class PathCompat {
	/**
	 * Initial buffer size, enough for 64 line segments = 65 points
	 */
	private static final int DEFAULT_BUFFER_SIZE = 256;

	/**
	 * Buffer for point coordinates to avoid calling drawLine for every line segment, instead call drawLines.
	 */
	private float[] buffer = new float[DEFAULT_BUFFER_SIZE];

	/**
	 * Number of points in buffer, index where put next line segment coordinate.
	 */
	private int bufferIndex = 0;

	public void moveTo(float x, float y) {
		if (bufferIndex != 0) {
			// Move too only works for starting point.
			return;
		}
		buffer[bufferIndex++] = x;
		buffer[bufferIndex++] = y;
	}

	public void lineTo(Canvas canvas, Paint paint, float x, float y) {

		addLineToBuffer(x, y);

		drawLinesIfNeeded(canvas, paint);
	}

	private void drawLinesIfNeeded(Canvas canvas, Paint paint) {
		if (bufferIndex == buffer.length) {
			// Buffer full, draw lines and remember last point as the first point in buffer.
			canvas.drawLines(buffer, 0, bufferIndex, paint);
			final float lastX = buffer[bufferIndex - 2];
			final float lastY = buffer[bufferIndex - 1];
			bufferIndex = 0;
			buffer[bufferIndex++] = lastX;
			buffer[bufferIndex++] = lastY;
		}
	}

	private void addLineToBuffer(float x, float y) {
		if (bufferIndex == 0) {
			// No moveTo, set starting point to 0,0.
			buffer[bufferIndex++] = 0;
			buffer[bufferIndex++] = 0;
		}

		if (bufferIndex == 2) {
			// First segment.
			buffer[bufferIndex++] = x;
			buffer[bufferIndex++] = y;
		} else {
			final float lastX = buffer[bufferIndex - 2];
			final float lastY = buffer[bufferIndex - 1];
			buffer[bufferIndex++] = lastX;
			buffer[bufferIndex++] = lastY;
			buffer[bufferIndex++] = x;
			buffer[bufferIndex++] = y;
		}
	}

	/**
	 * Resets internal state of PathCompat and prepare it to draw next line.
	 */
	public void reset() {
		bufferIndex = 0;
	}

	/**
	 * Draw line segment if there is any not drawn before.
	 * 
	 */
	public void drawPath(Canvas canvas, Paint paint) {
		if (bufferIndex >= 4) {
			canvas.drawLines(buffer, 0, bufferIndex, paint);
		}
		reset();
	}

}