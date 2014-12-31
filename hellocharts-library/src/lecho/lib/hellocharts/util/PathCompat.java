package lecho.lib.hellocharts.util;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;

/**
 * PathCompat uses Canvas.drawLines instead Canvas.drawPath. Supports only normal lines. Warning!:line has to be
 * continuous and doesn't support filled area, dashed lines etc. For complete implementation with Bezier's curves
 * see gist {@link https://gist.github.com/lecho/a903e68fe7cccac131d0}
 */
public class PathCompat {

    /**
     * Buffer for point coordinates to avoid calling drawLine for every line segment, instead call drawLines.
     */
    private final float[] buffer;

    /**
     * Number of points in buffer, index where put next line segment coordinate.
     */
    private int bufferIndex = 0;

    /**
     * De Casteljau's algorithm implementation to draw cubic Bezier's curves with hardware acceleration without
     * using Path. For filling area Path still has to be used but it will be clipped to contentRect.
     */
    private CasteljauComputator casteljauComputator = new CasteljauComputator();

    /**
     * Buffer for cubic Bezier's curve points coordinate, four points(start point, end point, two control points),
     * two coordinate each.
     */
    private float[] bezierBuffer = new float[8];

    /**
     * Computed bezier line point, as private member to avoid allocation.
     */
    private PointF bezierOutPoint = new PointF();

    /**
     * Step in pixels for drawing Bezier's curve
     */
    private int pixelStep = 8;

    public PathCompat(int bufferSize, int pixelStep){
        this.pixelStep = pixelStep;
        buffer = new float[bufferSize];
    }

    public void moveTo(float x, float y) {
        if (bufferIndex != 0) {
            // Move too only works for starting point.
            return;
        }
        buffer[bufferIndex++] = x;
        buffer[bufferIndex++] = y;
    }

    public void lineTo(float x, float y) {
        addLineToBuffer(x, y);
    }

    public void cubicTo(float x1, float y1, float x2, float y2, float x3, float y3) {
        if (bufferIndex == 0) {
            // No moveTo, set starting point to 0,0.
            bezierBuffer[0] = 0;
            bezierBuffer[1] = 0;
        } else {
            bezierBuffer[0] = buffer[bufferIndex - 2];
            bezierBuffer[1] = buffer[bufferIndex - 1];
        }
        bezierBuffer[2] = x1;
        bezierBuffer[3] = y1;
        bezierBuffer[4] = x2;
        bezierBuffer[5] = y2;
        bezierBuffer[6] = x3;
        bezierBuffer[7] = y3;

        // First subline
        addLineToBuffer(bezierBuffer[0], bezierBuffer[1]);

        final float stepT = 1.0f / (Math.abs((bezierBuffer[0] - x3)) / pixelStep);
        for (float t = stepT; t < 1.0f; t += stepT) {
            casteljauComputator.computePoint(t, bezierBuffer, bezierOutPoint);
            addLineToBuffer(bezierOutPoint.x, bezierOutPoint.y);
        }

        // Last subline
        addLineToBuffer(x3, y3);
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

    public int getBufferIndex(){
        return bufferIndex;
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
