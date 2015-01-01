package lecho.lib.hellocharts;

import lecho.lib.hellocharts.model.Viewport;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Rect;

public class ChartCalculator {
	protected static float MAXIMUM_SCALE = 20f;
	/**
	 * The current area (in pixels) for chart data, including mCoomonMargin. Labels are drawn outside this area.
	 */
	protected Rect contentRect = new Rect();
	protected Rect contentRectWithMargins = new Rect();
	/**
	 * This rectangle represents the currently visible chart values ranges. The currently visible chart X values are
	 * from this rectangle's left to its right. The currently visible chart Y values are from this rectangle's top to
	 * its bottom.
	 * 
	 */
	protected Viewport currentViewport = new Viewport();
	protected Viewport maxViewport = new Viewport();// Viewport for whole data ranges

	protected float minViewportWidth;
	protected float minViewportHeight;

    protected float widthRelation = 0;
    protected float heightRelation = 0;

	/**
	 * Warning! Viewport listener is disabled for all charts beside preview charts to avoid addidtional method calls
	 * during animations.
	 */
	protected ViewportChangeListener viewportChangeListener = new DummyVieportChangeListener();

    public void setMaxZoom(float maxZoom) {
        if (maxZoom < 1) {
            maxZoom = 1;
        }
        MAXIMUM_SCALE = maxZoom;

        computeMinimumWidthAndHeight();
        setCurrentViewport(currentViewport);
    }

    public float getMaxZoom(){
        return MAXIMUM_SCALE;
    }

    protected void calculateWidthHeightRelation(){
        widthRelation = contentRect.width() / currentViewport.width();
        heightRelation = contentRect.height() / currentViewport.height();
    }

	/**
	 * Calculates available width and height. Should be called when chart dimensions or chart data change.
	 */
	public void calculateContentArea(int width, int height, int paddingLeft, int paddingTop, int paddingRight,
			int paddingBottom) {
		contentRectWithMargins.set(paddingLeft, paddingTop, width - paddingRight, height - paddingBottom);
		contentRect.set(contentRectWithMargins);

        calculateWidthHeightRelation();
	}

	public void setInternalMargin(int margin) {
		contentRect.left = contentRectWithMargins.left + margin;
		contentRect.top = contentRectWithMargins.top + margin;
		contentRect.right = contentRectWithMargins.right - margin;
		contentRect.bottom = contentRectWithMargins.bottom - margin;
        calculateWidthHeightRelation();
	}

	public void setInternalMargin(int marginLeft, int marginTop, int marginRight, int marginBottom) {
		contentRect.left = contentRectWithMargins.left + marginLeft;
		contentRect.top = contentRectWithMargins.top + marginTop;
		contentRect.right = contentRectWithMargins.right - marginRight;
		contentRect.bottom = contentRectWithMargins.bottom - marginBottom;
        calculateWidthHeightRelation();
	}

	public void setAxesMargin(int axisXMarginTop, int axisXMarginBottom, int axisYMarginLeft, int axisYMarginRight) {
		contentRectWithMargins.bottom = contentRectWithMargins.bottom - axisXMarginBottom;
		contentRectWithMargins.left = contentRectWithMargins.left + axisYMarginLeft;
		contentRect.left = contentRect.left + axisYMarginLeft;
		contentRect.bottom = contentRect.bottom - axisXMarginBottom;
        calculateWidthHeightRelation();
	}

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
        calculateWidthHeightRelation();
	}

	/**
	 * Sets the current viewport (defined by {@link #currentViewport}) to the given X and Y positions.
	 */
	public void setViewportTopLeft(float left, float top) {
		/**
		 * Constrains within the scroll range. The scroll range is simply the viewport extremes (AXIS_X_MAX, etc.) minus
		 * the viewport size. For example, if the extrema were 0 and 10, and the viewport size was 2, the scroll range
		 * would be 0 to 8.
		 */

		final float curWidth = currentViewport.width();
		final float curHeight = currentViewport.height();
		left = Math.max(maxViewport.left, Math.min(left, maxViewport.right - curWidth));
		top = Math.max(maxViewport.bottom + curHeight, Math.min(top, maxViewport.top));
		constrainViewport(left, top, left + curWidth, top - curHeight);
	}

	public float calculateRawX(float valueX) {
		final float pixelOffset = (valueX - currentViewport.left) * widthRelation;
		return contentRect.left + pixelOffset;
	}

	public float calculateRawY(float valueY) {
		final float pixelOffset = (valueY - currentViewport.bottom) * heightRelation;
		return contentRect.bottom - pixelOffset;
	}

	public float calculateRelativeRawX(float valueX) {
		final float pixelOffset = (valueX - currentViewport.left) * widthRelation;
		return pixelOffset;
	}

	public float calculateRelativeRawY(float valueY) {
		final float pixelOffset = (valueY - currentViewport.bottom) * heightRelation;
		return contentRect.height() - pixelOffset;
	}

	public float calculateRawDistanceX(float distance) {
		return distance * widthRelation;
	}

	public float calculateRawDistanceY(float distance) {
		return distance * heightRelation;
	}

	/**
	 * Finds the chart point (i.e. within the chart's domain and range) represented by the given pixel coordinates, if
	 * that pixel is within the chart region described by {@link #contentRect}. If the point is found, the "dest"
	 * argument is set to the point and this function returns true. Otherwise, this function returns false and "dest" is
	 * unchanged.
	 */
	public boolean rawPixelsToDataPoint(float x, float y, PointF dest) {
		if (!contentRect.contains((int) x, (int) y)) {
			return false;
		}
		dest.set(currentViewport.left + (x - contentRect.left) * currentViewport.width() / contentRect.width(),
				currentViewport.bottom + (y - contentRect.bottom) * currentViewport.height() / -contentRect.height());
		return true;
	}

	/**
	 * Computes the current scrollable surface size, in pixels. For example, if the entire chart area is visible, this
	 * is simply the current size of {@link #contentRect}. If the chart is zoomed in 200% in both directions, the
	 * returned size will be twice as large horizontally and vertically.
	 */
	public void computeScrollSurfaceSize(Point out) {
		out.set((int) (maxViewport.width() * widthRelation),
				(int) (maxViewport.height() * heightRelation));
	}

	public boolean isWithinContentRect(int x, int y) {
		if (x >= contentRect.left && x <= contentRect.right) {
			if (y <= contentRect.bottom && y >= contentRect.top) {
				return true;
			}
		}
		return false;
	}

	public Rect getContentRect() {
		return contentRect;
	}

	public Rect getContentRectWithMargins() {
		return contentRectWithMargins;
	}

	public Viewport getCurrentViewport() {
		return currentViewport;
	}

	public void setCurrentViewport(float left, float top, float right, float bottom) {
		constrainViewport(left, top, right, bottom);
	}

	public void setCurrentViewport(Viewport viewport) {
		constrainViewport(viewport.left, viewport.top, viewport.right, viewport.bottom);
	}

	public Viewport getMaximumViewport() {
		return maxViewport;
	}

	public void setMaxViewport(Viewport maxViewport) {
		setMaxViewport(maxViewport.left, maxViewport.top, maxViewport.right, maxViewport.bottom);
        computeMinimumWidthAndHeight();
	}

	public void setMaxViewport(float left, float top, float right, float bottom) {
		this.maxViewport.set(left, top, right, bottom);
		minViewportWidth = this.maxViewport.width() / MAXIMUM_SCALE;
		minViewportHeight = this.maxViewport.height() / MAXIMUM_SCALE;
	}

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

    private void computeMinimumWidthAndHeight() {
        minViewportWidth = this.maxViewport.width() / MAXIMUM_SCALE;
        minViewportHeight = this.maxViewport.height() / MAXIMUM_SCALE;
    }

}