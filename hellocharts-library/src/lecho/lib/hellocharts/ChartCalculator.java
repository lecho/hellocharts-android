package lecho.lib.hellocharts;

import lecho.lib.hellocharts.model.Viewport;
import lecho.lib.hellocharts.util.Utils;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Rect;

public class ChartCalculator {
	protected static final float MAXIMUM_SCALE = 10f;
	/**
	 * The current area (in pixels) for chart data, including mCoomonMargin. Labels are drawn outside this area.
	 */
	protected Rect contentRect = new Rect();
	protected Rect contentRectWithMargins = new Rect();
	/**
	 * This rectangle represents the currently visible chart values ranges. The currently visible chart X values are
	 * from this rectangle's left to its right. The currently visible chart Y values are from this rectangle's top to
	 * its bottom.
	 * <p>
	 * Note that this rectangle's top is actually the smaller Y value, and its bottom is the larger Y value. Since the
	 * chart is drawn onscreen in such a way that chart Y values increase towards the top of the screen (decreasing
	 * pixel Y positions), this rectangle's "top" is drawn above this rectangle's "bottom" value.
	 * 
	 */
	protected Viewport currentViewport = new Viewport();
	protected Viewport maxViewport = new Viewport();// Viewport for whole data ranges

	protected float minViewportWidth;
	protected float minViewportHeight;

	/**
	 * Warning! Viewport listener is disabled for all charts beside preview charts to avoid addidtional method calls
	 * during animations.
	 */
	protected ViewportChangeListener viewportChangeListener = new DummyVieportChangeListener();

	/**
	 * Calculates available width and height. Should be called when chart dimensions or chart data change.
	 */
	public void calculateContentArea(int width, int height, int paddingLeft, int paddingTop, int paddingRight,
			int paddingBottom) {
		contentRectWithMargins.set(paddingLeft, paddingTop, width - paddingRight, height - paddingBottom);
		contentRect.set(contentRectWithMargins);
	}

	public void setInternalMargin(int margin) {
		contentRect.left = contentRectWithMargins.left + margin;
		contentRect.top = contentRectWithMargins.top + margin;
		contentRect.right = contentRectWithMargins.right - margin;
		contentRect.bottom = contentRectWithMargins.bottom - margin;
	}

	public void setInternalMargin(int marginLeft, int marginTop, int marginRight, int marginBottom) {
		contentRect.left = contentRectWithMargins.left + marginLeft;
		contentRect.top = contentRectWithMargins.top + marginTop;
		contentRect.right = contentRectWithMargins.right - marginRight;
		contentRect.bottom = contentRectWithMargins.bottom - marginBottom;
	}

	public void setAxesMargin(int axisXMarginTop, int axisXMarginBottom, int axisYMarginLeft, int axisYMarginRight) {
		contentRectWithMargins.bottom = contentRectWithMargins.bottom - axisXMarginBottom;
		contentRectWithMargins.left = contentRectWithMargins.left + axisYMarginLeft;
		contentRect.left = contentRect.left + axisYMarginLeft;
		contentRect.bottom = contentRect.bottom - axisXMarginBottom;
	}

	public void constrainViewport(float left, float top, float right, float bottom) {
		if (right - left < minViewportWidth || top - bottom < minViewportHeight) {
			// Maximum zoom!
			left = currentViewport.left;
			top = currentViewport.top;
			right = left + minViewportWidth;
			bottom = top - minViewportHeight;
		}

		currentViewport.left = Math.max(maxViewport.left, left);
		currentViewport.top = Math.max(Utils.nextUpF(bottom), Math.min(maxViewport.top, top));
		currentViewport.right = Math.max(Utils.nextUpF(left), Math.min(maxViewport.right, right));
		currentViewport.bottom = Math.max(maxViewport.bottom, bottom);
	}

	/**
	 * Sets the current viewport (defined by {@link #currentViewport}) to the given X and Y positions. Note that the Y
	 * value represents the topmost pixel position, and thus the bottom of the {@link #currentViewport} rectangle. For
	 * more details on why top and bottom are flipped, see {@link #currentViewport}.
	 */
	public void setViewportBottomLeft(float left, float bottom) {
		/**
		 * Constrains within the scroll range. The scroll range is simply the viewport extremes (AXIS_X_MAX, etc.) minus
		 * the viewport size. For example, if the extrema were 0 and 10, and the viewport size was 2, the scroll range
		 * would be 0 to 8.
		 */

		final float curWidth = currentViewport.width();
		final float curHeight = currentViewport.height();
		left = Math.max(maxViewport.left, Math.min(left, maxViewport.right - curWidth));
		bottom = Math.max(maxViewport.top + curHeight, Math.min(bottom, maxViewport.bottom));
		constrainViewport(left, bottom - curHeight, left + curWidth, bottom);
	}

	public float calculateRawX(float valueX) {
		final float pixelOffset = (valueX - currentViewport.left) * (contentRect.width() / currentViewport.width());
		return contentRect.left + pixelOffset;
	}

	public float calculateRawY(float valueY) {
		final float pixelOffset = (valueY - currentViewport.bottom) * (contentRect.height() / currentViewport.height());
		return contentRect.bottom - pixelOffset;
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
				currentViewport.bottom + (y - contentRect.top) * currentViewport.height() / -contentRect.height());
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
		this.maxViewport.set(maxViewport.left, maxViewport.top, maxViewport.right, maxViewport.bottom);
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