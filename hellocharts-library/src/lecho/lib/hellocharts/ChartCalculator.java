package lecho.lib.hellocharts;

import lecho.lib.hellocharts.model.Data;
import lecho.lib.hellocharts.utils.Utils;
import android.content.Context;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.v4.view.ViewCompat;
import android.util.Pair;
import android.view.View;

public class ChartCalculator {
	// TODO: use getters/setters instead of public members
	private static final int DEFAULT_COMMON_MARGIN_DP = 10;
	public int mCommonMargin;
	public int mAxisYMargin;
	public int mAxisXMargin;
	public Pair<Integer, Integer> mAxisXHeight = new Pair<Integer, Integer>(0, 0);
	public Pair<Integer, Integer> mAxisYWidth = new Pair<Integer, Integer>(0, 0);
	/**
	 * The current area (in pixels) for chart data, including mCoomonMargin. Labels are drawn outside this area.
	 */
	public Rect mContentRect = new Rect();
	public Rect mContentRectWithMargins = new Rect();
	public Rect mClippingRect = new Rect();
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
	public RectF mCurrentViewport = new RectF();
	public RectF mMaximumViewport = new RectF();// Viewport for whole data ranges

	/**
	 * Constructor
	 */
	public ChartCalculator(Context context) {
		mCommonMargin = Utils.dp2px(context, DEFAULT_COMMON_MARGIN_DP);
	}

	/**
	 * Calculates available width and height. Should be called when chart dimensions or chart data change.
	 */
	public void calculateContentArea(View chart) {
		mContentRectWithMargins.set(chart.getPaddingLeft() + mAxisYMargin, chart.getPaddingTop(), chart.getWidth()
				- chart.getPaddingRight(), chart.getHeight() - chart.getPaddingBottom() - mAxisXMargin);
		mContentRect.set(mContentRectWithMargins.left + mCommonMargin, mContentRectWithMargins.top + mCommonMargin,
				mContentRectWithMargins.right - mCommonMargin, mContentRectWithMargins.bottom - mCommonMargin);
	}

	public void calculateViewport(Data data) {
		mMaximumViewport.set(data.minXValue, data.minYValue, data.maxXValue, data.maxYValue);
		// TODO: don't reset current viewport during animation if zoom is enabled
		mCurrentViewport.set(mMaximumViewport);
	}

	public void constrainViewport() {
		// TODO: avoid too much zoom
		mCurrentViewport.left = Math.max(mMaximumViewport.left, mCurrentViewport.left);
		mCurrentViewport.top = Math.max(mMaximumViewport.top, mCurrentViewport.top);
		mCurrentViewport.bottom = Math.max(Utils.nextUpF(mCurrentViewport.top),
				Math.min(mMaximumViewport.bottom, mCurrentViewport.bottom));
		mCurrentViewport.right = Math.max(Utils.nextUpF(mCurrentViewport.left),
				Math.min(mMaximumViewport.right, mCurrentViewport.right));
	}

	/**
	 * Sets the current viewport (defined by {@link #mCurrentViewport}) to the given X and Y positions. Note that the Y
	 * value represents the topmost pixel position, and thus the bottom of the {@link #mCurrentViewport} rectangle. For
	 * more details on why top and bottom are flipped, see {@link #mCurrentViewport}.
	 */
	// TODO: move invalidate outside this method
	public void setViewportBottomLeft(float x, float y, View chart) {
		/**
		 * Constrains within the scroll range. The scroll range is simply the viewport extremes (AXIS_X_MAX, etc.) minus
		 * the viewport size. For example, if the extrema were 0 and 10, and the viewport size was 2, the scroll range
		 * would be 0 to 8.
		 */

		final float curWidth = mCurrentViewport.width();
		final float curHeight = mCurrentViewport.height();
		x = Math.max(mMaximumViewport.left, Math.min(x, mMaximumViewport.right - curWidth));
		y = Math.max(mMaximumViewport.top + curHeight, Math.min(y, mMaximumViewport.bottom));
		mCurrentViewport.set(x, y - curHeight, x + curWidth, y);
		ViewCompat.postInvalidateOnAnimation(chart);
	}

	/**
	 * Prevents dot clipping when user scroll to the one of ends of chart or zoom out. calculating pixel value helps to
	 * avoid float rounding error.
	 */
	public void calculateClippingArea() {
		if ((int) calculateRawX(mCurrentViewport.left) == (int) calculateRawX(mMaximumViewport.left)) {
			mClippingRect.left = mContentRectWithMargins.left;
		} else {
			mClippingRect.left = mContentRect.left;
		}

		if ((int) calculateRawY(mCurrentViewport.top) == (int) calculateRawY(mMaximumViewport.top)) {
			mClippingRect.bottom = mContentRectWithMargins.bottom;
		} else {
			mClippingRect.bottom = mContentRect.bottom;
		}

		if ((int) calculateRawX(mCurrentViewport.right) == (int) calculateRawX(mMaximumViewport.right)) {
			mClippingRect.right = mContentRectWithMargins.right;
		} else {
			mClippingRect.right = mContentRect.right;
		}

		if ((int) calculateRawY(mCurrentViewport.bottom) == (int) calculateRawY(mMaximumViewport.bottom)) {
			mClippingRect.top = mContentRectWithMargins.top;
		} else {
			mClippingRect.top = mContentRect.top;
		}
	}

	public void calculateAxesMargins(Context context, AxesRenderer axesRenderer, Data data) {
		mAxisXHeight = axesRenderer.getAxisXHeight(context, data.axisX);
		mAxisXMargin = mAxisXHeight.first + mAxisXHeight.second;
		if (mAxisXHeight.first > 0 && mAxisXHeight.second > 0) {
			// Additional margin for separation axis name from axis values.
			mAxisXMargin += mCommonMargin;
		}

		mAxisYWidth = axesRenderer.getAxisYWidth(context, data.axisY);
		mAxisYMargin = mAxisYWidth.first + mAxisYWidth.second;
		if (mAxisYWidth.first > 0 && mAxisYWidth.second > 0) {
			// Additional margin for separation axis name from axis values.
			mAxisYMargin += mCommonMargin;
		}
	}

	public float calculateRawX(float valueX) {
		final float pixelOffset = (valueX - mCurrentViewport.left) * (mContentRect.width() / mCurrentViewport.width());
		return mContentRect.left + pixelOffset;
	}

	public float calculateRawY(float valueY) {
		final float pixelOffset = (valueY - mCurrentViewport.top) * (mContentRect.height() / mCurrentViewport.height());
		return mContentRect.bottom - pixelOffset;
	}

	/**
	 * Finds the chart point (i.e. within the chart's domain and range) represented by the given pixel coordinates, if
	 * that pixel is within the chart region described by {@link #mContentRect}. If the point is found, the "dest"
	 * argument is set to the point and this function returns true. Otherwise, this function returns false and "dest" is
	 * unchanged.
	 */
	public boolean rawPixelsToDataPoint(float x, float y, PointF dest) {
		if (!mContentRect.contains((int) x, (int) y)) {
			return false;
		}
		dest.set(mCurrentViewport.left + (x - mContentRect.left) * (mCurrentViewport.width() / mContentRect.width()),
				mCurrentViewport.top + (y - mContentRect.bottom) * (mCurrentViewport.height() / -mContentRect.height()));
		return true;
	}

	/**
	 * Computes the current scrollable surface size, in pixels. For example, if the entire chart area is visible, this
	 * is simply the current size of {@link #mContentRect}. If the chart is zoomed in 200% in both directions, the
	 * returned size will be twice as large horizontally and vertically.
	 */
	public void computeScrollSurfaceSize(Point out) {
		out.set((int) (mMaximumViewport.width() * mContentRect.width() / mCurrentViewport.width()),
				(int) (mMaximumViewport.height() * mContentRect.height() / mCurrentViewport.height()));
	}
}
