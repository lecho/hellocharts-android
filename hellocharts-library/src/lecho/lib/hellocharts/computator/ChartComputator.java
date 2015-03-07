package lecho.lib.hellocharts.computator;

import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Rect;

import lecho.lib.hellocharts.listener.DummyVieportChangeListener;
import lecho.lib.hellocharts.listener.ViewportChangeListener;
import lecho.lib.hellocharts.model.Viewport;

/**
 * Computes raw points coordinates(in pixels), holds content area dimensions and chart viewport.
 */
public class ChartComputator {

    /**
     * Maximum chart zoom.
     */
    protected static final float DEFAULT_MAXIMUM_ZOOM = 20f;
    protected float maxZoom = DEFAULT_MAXIMUM_ZOOM;
    protected int chartWidth;
    protected int chartHeight;
    //contentRectMinusAllMargins <= contentRectMinusAxesMargins <= maxContentRect
    protected Rect contentRectMinusAllMargins = new Rect();
    protected Rect contentRectMinusAxesMargins = new Rect();
    protected Rect maxContentRect = new Rect();
    /**
     * This rectangle represents the currently visible chart values ranges. The currently visible chart X values are
     * from this rectangle's left to its right. The currently visible chart Y values are from this rectangle's top to
     * its bottom.
     */
    protected Viewport currentViewport = new Viewport();
    protected Viewport maxViewport = new Viewport();
    protected float minViewportWidth;
    protected float minViewportHeight;
    /**
     * Warning! Viewport listener is disabled for all charts beside preview charts to avoid additional method calls
     * during animations.
     */
    protected ViewportChangeListener viewportChangeListener = new DummyVieportChangeListener();

    /**
     * Calculates available width and height. Should be called when chart dimensions change. ContentRect is relative to
     * chart view not the device's screen.
     */
    public void setContentRect(int width, int height, int paddingLeft, int paddingTop, int paddingRight,
                               int paddingBottom) {
        chartWidth = width;
        chartHeight = height;
        maxContentRect.set(paddingLeft, paddingTop, width - paddingRight, height - paddingBottom);
        contentRectMinusAxesMargins.set(maxContentRect);
        contentRectMinusAllMargins.set(maxContentRect);
    }

    public void resetContentRect() {
        contentRectMinusAxesMargins.set(maxContentRect);
        contentRectMinusAllMargins.set(maxContentRect);
    }

    public void insetContentRect(int deltaLeft, int deltaTop, int deltaRight, int deltaBottom) {
        contentRectMinusAxesMargins.left = contentRectMinusAxesMargins.left + deltaLeft;
        contentRectMinusAxesMargins.top = contentRectMinusAxesMargins.top + deltaTop;
        contentRectMinusAxesMargins.right = contentRectMinusAxesMargins.right - deltaRight;
        contentRectMinusAxesMargins.bottom = contentRectMinusAxesMargins.bottom - deltaBottom;

        insetContentRectByInternalMargins(deltaLeft, deltaTop, deltaRight, deltaBottom);
    }

    public void insetContentRectByInternalMargins(int deltaLeft, int deltaTop, int deltaRight, int deltaBottom) {
        contentRectMinusAllMargins.left = contentRectMinusAllMargins.left + deltaLeft;
        contentRectMinusAllMargins.top = contentRectMinusAllMargins.top + deltaTop;
        contentRectMinusAllMargins.right = contentRectMinusAllMargins.right - deltaRight;
        contentRectMinusAllMargins.bottom = contentRectMinusAllMargins.bottom - deltaBottom;
    }

    /**
     * Checks if new viewport doesn't exceed max available viewport.
     */
    public void constrainViewport(float left, float top, float right, float bottom) {

        if (right - left < minViewportWidth) {
            // Minimum width - constrain horizontal zoom!
            right = left + minViewportWidth;
            if (left < maxViewport.left) {
                left = maxViewport.left;
                right = left + minViewportWidth;
            } else if (right > maxViewport.right) {
                right = maxViewport.right;
                left = right - minViewportWidth;
            }
        }

        if (top - bottom < minViewportHeight) {
            // Minimum height - constrain vertical zoom!
            bottom = top - minViewportHeight;
            if (top > maxViewport.top) {
                top = maxViewport.top;
                bottom = top - minViewportHeight;
            } else if (bottom < maxViewport.bottom) {
                bottom = maxViewport.bottom;
                top = bottom + minViewportHeight;
            }
        }

        currentViewport.left = Math.max(maxViewport.left, left);
        currentViewport.top = Math.min(maxViewport.top, top);
        currentViewport.right = Math.min(maxViewport.right, right);
        currentViewport.bottom = Math.max(maxViewport.bottom, bottom);

        viewportChangeListener.onViewportChanged(currentViewport);
    }

    /**
     * Sets the current viewport (defined by {@link #currentViewport}) to the given X and Y positions.
     */
    public void setViewportTopLeft(float left, float top) {
        /**
         * Constrains within the scroll range. The scroll range is simply the viewport extremes (AXIS_X_MAX,
         * etc.) minus
         * the viewport size. For example, if the extrema were 0 and 10, and the viewport size was 2, the scroll range
         * would be 0 to 8.
         */

        final float curWidth = currentViewport.width();
        final float curHeight = currentViewport.height();

        left = Math.max(maxViewport.left, Math.min(left, maxViewport.right - curWidth));
        top = Math.max(maxViewport.bottom + curHeight, Math.min(top, maxViewport.top));
        constrainViewport(left, top, left + curWidth, top - curHeight);
    }

    /**
     * Translates chart value into raw pixel value. Returned value is absolute pixel X coordinate. If this method
     * return
     * 0 that means left most pixel of the screen.
     */
    public float computeRawX(float valueX) {
        // TODO: (contentRectMinusAllMargins.width() / currentViewport.width()) can be recalculated only when viewport
        // change.
        final float pixelOffset = (valueX - currentViewport.left) * (contentRectMinusAllMargins.width() /
                currentViewport.width());
        return contentRectMinusAllMargins.left + pixelOffset;
    }

    /**
     * Translates chart value into raw pixel value. Returned value is absolute pixel Y coordinate. If this method
     * return
     * 0 that means top most pixel of the screen.
     */
    public float computeRawY(float valueY) {
        final float pixelOffset = (valueY - currentViewport.bottom) * (contentRectMinusAllMargins.height() /
                currentViewport.height());
        return contentRectMinusAllMargins.bottom - pixelOffset;
    }

    /**
     * Translates viewport distance int pixel distance for X coordinates.
     */
    public float computeRawDistanceX(float distance) {
        return distance * (contentRectMinusAllMargins.width() / currentViewport.width());
    }

    /**
     * Translates viewport distance int pixel distance for X coordinates.
     */
    public float computeRawDistanceY(float distance) {
        return distance * (contentRectMinusAllMargins.height() / currentViewport.height());
    }

    /**
     * Finds the chart point (i.e. within the chart's domain and range) represented by the given pixel coordinates, if
     * that pixel is within the chart region described by {@link #contentRectMinusAllMargins}. If the point is found,
     * the "dest"
     * argument is set to the point and this function returns true. Otherwise, this function returns false and
     * "dest" is
     * unchanged.
     */
    public boolean rawPixelsToDataPoint(float x, float y, PointF dest) {
        if (!contentRectMinusAllMargins.contains((int) x, (int) y)) {
            return false;
        }
        dest.set(currentViewport.left + (x - contentRectMinusAllMargins.left) * currentViewport.width() /
                        contentRectMinusAllMargins.width(),
                currentViewport.bottom + (y - contentRectMinusAllMargins.bottom) * currentViewport.height() /
                        -contentRectMinusAllMargins.height());
        return true;
    }

    /**
     * Computes the current scrollable surface size, in pixels. For example, if the entire chart area is visible, this
     * is simply the current size of {@link #contentRectMinusAllMargins}. If the chart is zoomed in 200% in both
     * directions, the
     * returned size will be twice as large horizontally and vertically.
     */
    public void computeScrollSurfaceSize(Point out) {
        out.set((int) (maxViewport.width() * contentRectMinusAllMargins.width() / currentViewport.width()),
                (int) (maxViewport.height() * contentRectMinusAllMargins.height() / currentViewport.height()));
    }

    /**
     * Check if given coordinates lies inside contentRectMinusAllMargins.
     */
    public boolean isWithinContentRect(float x, float y, float precision) {
        if (x >= contentRectMinusAllMargins.left - precision && x <= contentRectMinusAllMargins.right + precision) {
            if (y <= contentRectMinusAllMargins.bottom + precision && y >= contentRectMinusAllMargins.top -
                    precision) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns content rectangle in pixels.
     *
     * @see #setContentRect(int, int, int, int, int, int)
     */
    public Rect getContentRectMinusAllMargins() {
        return contentRectMinusAllMargins;
    }

    /**
     * Returns content rectangle with chart internal margins, for example for LineChart contentRectMinusAxesMargins is
     * bigger
     * than contentRectMinusAllMargins by point radius, thanks to that points are not cut on edges.
     *
     * @see #setContentRect(int, int, int, int, int, int)
     */
    public Rect getContentRectMinusAxesMargins() {
        return contentRectMinusAxesMargins;
    }

    /**
     * Returns current chart viewport, returned object is mutable but should not be modified.
     *
     * @return
     */
    public Viewport getCurrentViewport() {
        return currentViewport;
    }

    /**
     * Set current viewport to the same values as viewport passed in parameter. This method use deep copy so parameter
     * can be safely modified later. Current viewport must be equal or smaller than maximum viewport.
     *
     * @param viewport
     */
    public void setCurrentViewport(Viewport viewport) {
        constrainViewport(viewport.left, viewport.top, viewport.right, viewport.bottom);
    }

    /**
     * Set new values for curent viewport, that will change what part of chart is visible. Current viewport must be
     * equal or smaller than maximum viewport.
     */
    public void setCurrentViewport(float left, float top, float right, float bottom) {
        constrainViewport(left, top, right, bottom);
    }

    /**
     * Returns maximum viewport - values ranges extremes.
     */
    public Viewport getMaximumViewport() {
        return maxViewport;
    }

    /**
     * Set maximum viewport to the same values as viewport passed in parameter. This method use deep copy so parameter
     * can be safely modified later.
     *
     * @param maxViewport
     */
    public void setMaxViewport(Viewport maxViewport) {
        setMaxViewport(maxViewport.left, maxViewport.top, maxViewport.right, maxViewport.bottom);
    }

    /**
     * Set new values for maximum viewport, that will change what part of chart is visible.
     */
    public void setMaxViewport(float left, float top, float right, float bottom) {
        this.maxViewport.set(left, top, right, bottom);
        computeMinimumWidthAndHeight();
    }

    /**
     * Returns viewport for visible part of chart, for most charts it is equal to current viewport.
     *
     * @return
     */
    public Viewport getVisibleViewport() {
        return currentViewport;
    }

    public void setVisibleViewport(Viewport visibleViewport) {
        setCurrentViewport(visibleViewport);
    }

    public float getMinimumViewportWidth() {
        return minViewportWidth;
    }

    public float getMinimumViewportHeight() {
        return minViewportHeight;
    }

    public void setViewportChangeListener(ViewportChangeListener viewportChangeListener) {
        if (null == viewportChangeListener) {
            this.viewportChangeListener = new DummyVieportChangeListener();
        } else {
            this.viewportChangeListener = viewportChangeListener;
        }
    }

    public int getChartWidth() {
        return chartWidth;
    }

    public int getChartHeight() {
        return chartHeight;
    }

    public float getMaxZoom() {
        return maxZoom;
    }

    /**
     * Set maximum zoom level, default is 20.
     *
     * @param maxZoom
     */
    public void setMaxZoom(float maxZoom) {
        if (maxZoom < 1) {
            maxZoom = 1;
        }

        this.maxZoom = maxZoom;

        computeMinimumWidthAndHeight();

        setCurrentViewport(currentViewport);

    }

    private void computeMinimumWidthAndHeight() {
        minViewportWidth = this.maxViewport.width() / maxZoom;
        minViewportHeight = this.maxViewport.height() / maxZoom;
    }

}