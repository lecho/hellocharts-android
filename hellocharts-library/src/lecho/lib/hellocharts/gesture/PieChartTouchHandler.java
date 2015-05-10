package lecho.lib.hellocharts.gesture;

import android.content.Context;
import android.graphics.RectF;
import android.support.v4.widget.ScrollerCompat;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;

import lecho.lib.hellocharts.view.PieChartView;

/**
 * Touch handler for PieChart. It doesn't handle zoom and scroll like default ChartTouchHandler. Instead it uses
 * Scroller(ScrollerCompat) directly to compute PieChart rotation when user scroll. ChartScroller and ChartZoomer are
 * not really used here.
 */
public class PieChartTouchHandler extends ChartTouchHandler {
    /**
     * The initial fling velocity is divided by this amount.
     */
    public static final int FLING_VELOCITY_DOWNSCALE = 4;
    /**
     * PieChartTouchHandler uses its own instance of Scroller.
     */
    protected ScrollerCompat scroller;
    /**
     * Reference to PieChartView to use some methods specific for that kind of chart.
     */
    protected PieChartView pieChart;

    private boolean isRotationEnabled = true;

    public PieChartTouchHandler(Context context, PieChartView chart) {
        super(context, chart);
        pieChart = (PieChartView) chart;
        scroller = ScrollerCompat.create(context);
        gestureDetector = new GestureDetector(context, new ChartGestureListener());
        scaleGestureDetector = new ScaleGestureDetector(context, new ChartScaleGestureListener());
        isZoomEnabled = false;// Zoom is not supported by PieChart.
    }

    @Override
    public boolean computeScroll() {
        if (!isRotationEnabled) {
            return false;
        }
        if (scroller.computeScrollOffset()) {
            pieChart.setChartRotation(scroller.getCurrY(), false);
            // pieChart.setChartRotation() will invalidate view so no need to return true;
        }
        return false;
    }

    @Override
    public boolean handleTouchEvent(MotionEvent event) {
        boolean needInvalidate = super.handleTouchEvent(event);

        if (isRotationEnabled) {
            needInvalidate = gestureDetector.onTouchEvent(event) || needInvalidate;
        }
        return needInvalidate;
    }

    public boolean isRotationEnabled() {
        return isRotationEnabled;
    }

    public void setRotationEnabled(boolean isRotationEnabled) {
        this.isRotationEnabled = isRotationEnabled;
    }

    private class ChartScaleGestureListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {

        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            // No scale for PieChart.
            return false;
        }
    }

    private class ChartGestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onDown(MotionEvent e) {
            if (isRotationEnabled) {
                scroller.abortAnimation();
                return true;
            }

            return false;

        }

        @Override
        public boolean onDoubleTap(MotionEvent e) {
            return false;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            if (isRotationEnabled) {
                // Set the pie rotation directly.
                final RectF circleOval = pieChart.getCircleOval();
                final float centerX = circleOval.centerX();
                final float centerY = circleOval.centerY();
                float scrollTheta = vectorToScalarScroll(distanceX, distanceY, e2.getX() - centerX, e2.getY() -
                        centerY);
                pieChart.setChartRotation(pieChart.getChartRotation() - (int) scrollTheta / FLING_VELOCITY_DOWNSCALE,
                        false);
                return true;
            }

            return false;
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            if (isRotationEnabled) {
                // Set up the Scroller for a fling
                final RectF circleOval = pieChart.getCircleOval();
                final float centerX = circleOval.centerX();
                final float centerY = circleOval.centerY();
                float scrollTheta = vectorToScalarScroll(velocityX, velocityY, e2.getX() - centerX, e2.getY() -
                        centerY);
                scroller.abortAnimation();
                scroller.fling(0, (int) pieChart.getChartRotation(), 0, (int) scrollTheta / FLING_VELOCITY_DOWNSCALE,
                        0, 0, Integer.MIN_VALUE, Integer.MAX_VALUE);
                return true;
            }

            return false;
        }

        /**
         * Helper method for translating (x,y) scroll vectors into scalar rotation of the pie.
         *
         * @param dx The x component of the current scroll vector.
         * @param dy The y component of the current scroll vector.
         * @param x  The x position of the current touch, relative to the pie center.
         * @param y  The y position of the current touch, relative to the pie center.
         * @return The scalar representing the change in angular position for this scroll.
         */
        private float vectorToScalarScroll(float dx, float dy, float x, float y) {
            // get the length of the vector
            float l = (float) Math.sqrt(dx * dx + dy * dy);

            // decide if the scalar should be negative or positive by finding
            // the dot product of the vector perpendicular to (x,y).
            float crossX = -y;
            float crossY = x;

            float dot = (crossX * dx + crossY * dy);
            float sign = Math.signum(dot);

            return l * sign;
        }
    }

}
