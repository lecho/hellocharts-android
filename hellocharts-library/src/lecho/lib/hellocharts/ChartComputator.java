package lecho.lib.hellocharts;

import lecho.lib.hellocharts.model.Viewport;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Rect;

/**
 * Computes raw points coordinates(in pixels), holds content area dimensions and chart viewport.
 * 
 */
public class ChartComputator {
	/**
	 * Maximum chart zoom.
	 */
	protected static final float MAXIMUM_SCALE = 10f;

	/**
	 * The current area (in pixels) for chart data, including mCoomonMargin. Labels are drawn outside this area.
	 */
	protected Rect contentRect = new Rect();
	protected Rect contentRectWithMargins = new Rect();
	protected Rect maxContentRect = new Rect();

	/**
	 * Internal margins i.e. for axes.
	 */
	protected int marginLeft;
	protected int marginTop;
	protected int marginRight;
	protected int marginBottom;

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

	/**
	 * Warning! Viewport listener is disabled for all charts beside preview charts to avoid additional method calls
	 * during animations.
	 */
	protected ViewportChangeListener viewportChangeListener = new DummyVieportChangeListener();

	/**
	 * Calculates available width and height. Should be called when chart dimensions change.
	 */
	public void setContentArea(int width, int height, int paddingLeft, int paddingTop, int paddingRight,
			int paddingBottom) {
		maxContentRect.set(paddingLeft, paddingTop, width - paddingRight, height - paddingBottom);
		contentRectWithMargins.set(maxContentRect);
		contentRect.set(maxContentRect);
	}

	public void setInternalMargin(int margin) {
		setInternalMargin(margin, margin, margin, margin);
	}

	public void setInternalMargin(int marginLeft, int marginTop, int marginRight, int marginBottom) {
		this.marginLeft = marginLeft;
		this.marginTop = marginTop;
		this.marginRight = marginRight;
		this.marginBottom = marginBottom;

		contentRect.left = contentRectWithMargins.left + marginLeft;
		contentRect.top = contentRectWithMargins.top + marginTop;
		contentRect.right = contentRectWithMargins.right - marginRight;
		contentRect.bottom = contentRectWithMargins.bottom - marginBottom;
	}

	public void setAxesMargin(int axisXMarginTop, int axisXMarginBottom, int axisYMarginLeft, int axisYMarginRight) {
		contentRectWithMargins.left = maxContentRect.left + axisYMarginLeft;
		contentRectWithMargins.top = maxContentRect.top + axisXMarginTop;
		contentRectWithMargins.right = maxContentRect.right - axisYMarginRight;
		contentRectWithMargins.bottom = maxContentRect.bottom - axisXMarginBottom;

		setInternalMargin(marginLeft, marginTop, marginRight, marginBottom);
	}

	/**
	 * Checks if new viewport dimensions doesn't exceed max available viewport.
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

	/**
	 * Translates chart value into raw pixel value. Returned value is absolute pixel X coordinate. If this method return
	 * 0 that means left most pixel of the screen.
	 */
	public float computeRawX(float valueX) {
		// TODO: (contentRect.width() / currentViewport.width()) can be recalculated only when viewport change.
		final float pixelOffset = (valueX - currentViewport.left) * (contentRect.width() / currentViewport.width());
		return contentRect.left + pixelOffset;
	}

	/**
	 * Translates chart value into raw pixel value. Returned value is absolute pixel Y coordinate. If this method return
	 * 0 that means top most pixel of the screen.
	 */
	public float computeRawY(float valueY) {
		final float pixelOffset = (valueY - currentViewport.bottom) * (contentRect.height() / currentViewport.height());
		return contentRect.bottom - pixelOffset;
	}

	/**
	 * Translates chart value into relative pixel value. Returned value is relative pixel X coordinate. If this method
	 * return 0 that means left most pixel of chart(not the screen).
	 */
	public float computeRelativeRawX(float valueX) {
		// TODO: (contentRect.width() / currentViewport.width()) can be recalculated only when viewport change.
		final float pixelOffset = (valueX - currentViewport.left) * (contentRect.width() / currentViewport.width());
		return pixelOffset;
	}

	/**
	 * Translates chart value into relative pixel value. Returned value is relative pixel Y coordinate.If this method
	 * return 0 that means top most pixel of chart(not the screen).
	 */
	public float computeRelativeRawY(float valueY) {
		final float pixelOffset = (valueY - currentViewport.bottom) * (contentRect.height() / currentViewport.height());
		return contentRect.height() - pixelOffset;
	}

	/**
	 * Translates viewport distance int pixel distance for X coordinates.
	 */
	public float computeRawDistanceX(float distance) {
		return distance * (contentRect.width() / currentViewport.width());
	}

	/**
	 * Translates viewport distance int pixel distance for X coordinates.
	 */
	public float calculateRawDistanceY(float distance) {
		return distance * (contentRect.height() / currentViewport.height());
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
		out.set((int) (maxViewport.width() * contentRect.width() / currentViewport.width()),
				(int) (maxViewport.height() * contentRect.height() / currentViewport.height()));
	}

	/**
	 * Check if given coordinates lies inside contentRect.
	 */
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

}