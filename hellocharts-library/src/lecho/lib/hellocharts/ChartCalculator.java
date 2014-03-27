package lecho.lib.hellocharts;

import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.Data;
import lecho.lib.hellocharts.utils.Utils;
import android.content.Context;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.view.View;

public class ChartCalculator {
	private static final int DEFAULT_COMMON_MARGIN_DP = 10;
	private int mCommonMargin;
	private int mYAxisMargin = 0;
	private int mXAxisMargin = 0;
	/**
	 * The current area (in pixels) for chart data, including mCoomonMargin. Labels are drawn outside this area.
	 */
	private Rect mContentRect = new Rect();
	private Rect mContentRectWithMargins = new Rect();
	private Rect mClippingRect = new Rect();
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
	private RectF mCurrentViewport = new RectF();
	private RectF mMaximumViewport = new RectF();// Viewport for whole data ranges

	/**
	 * Constructor
	 */
	public ChartCalculator(Context context) {
		mCommonMargin = Utils.dp2px(context, DEFAULT_COMMON_MARGIN_DP);
	}

	/**
	 * Calculates available width and height. Should be called when chart dimensions or chart data change.
	 */
	private void calculateContentArea(View chart) {
		mContentRectWithMargins.set(chart.getPaddingLeft() + mYAxisMargin, chart.getPaddingTop(), chart.getWidth()
				- chart.getPaddingRight(), chart.getHeight() - chart.getPaddingBottom() - mXAxisMargin);
		mContentRect.set(mContentRectWithMargins.left + mCommonMargin, mContentRectWithMargins.top + mCommonMargin,
				mContentRectWithMargins.right - mCommonMargin, mContentRectWithMargins.bottom - mCommonMargin);
	}

	private void calculateViewport(Data data) {
		mMaximumViewport.set(data.minXValue, data.minYValue, data.maxXValue, data.maxYValue);
		// TODO: don't reset current viewport during animation if zoom is enabled
		mCurrentViewport.set(mMaximumViewport);
	}

	private void constrainViewport() {
		// TODO: avoid too much zoom by checking
		mCurrentViewport.left = Math.max(mMaximumViewport.left, mCurrentViewport.left);
		mCurrentViewport.top = Math.max(mMaximumViewport.top, mCurrentViewport.top);
		mCurrentViewport.bottom = Math.max(Utils.nextUpF(mCurrentViewport.top),
				Math.min(mMaximumViewport.bottom, mCurrentViewport.bottom));
		mCurrentViewport.right = Math.max(Utils.nextUpF(mCurrentViewport.left),
				Math.min(mMaximumViewport.right, mCurrentViewport.right));
	}

	/**
	 * Prevents dot clipping when user scroll to the one of ends of chart or zoom out. calculating pixel value helps to
	 * avoid float rounding error.
	 */
	private void calculateClippingArea() {
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

	private void calculateAxisXMargin(Axis axisX) {
		// mAxisTextPaint.setTextSize(Utils.sp2px(getContext(), mData.axisX.textSize));
		// if (!mData.axisX.values.isEmpty()) {
		// final Rect textBounds = new Rect();
		// // Hard coded only for text height calculation.
		// mAxisTextPaint.getTextBounds("X", 0, 1, textBounds);
		// mXAxisMargin = textBounds.height();
		// }
		// if (!TextUtils.isEmpty(mData.axisX.name)) {
		// final Rect textBounds = new Rect();
		// mAxisTextPaint.getTextBounds("X", 0, 1, textBounds);
		// // Additional margin for axis name.
		// mXAxisMargin += textBounds.height() + mCommonMargin;
		// }
	}

	private void calculateAxisYMargin(Axis axisY) {
		// mAxisTextPaint.setTextSize(Utils.sp2px(getContext(), mData.axisY.textSize));
		// if (!mData.axisY.values.isEmpty()) {
		// final Rect textBounds = new Rect();
		// final String text;
		// final int axisSize = mData.axisY.values.size();
		// final Axis axisY = mData.axisY;
		// if (Math.abs(axisY.values.get(0).value) > Math.abs(axisY.values.get(axisSize - 1).value)) {
		// text = axisY.formatter.formatValue(axisY.values.get(0));
		// } else {
		// text = axisY.formatter.formatValue(axisY.values.get(axisSize - 1));
		// }
		// mAxisTextPaint.getTextBounds(text, 0, text.length(), textBounds);
		// mYAxisMargin = textBounds.width();
		// }
		// if (!TextUtils.isEmpty(mData.axisY.name)) {
		// // Additional margin for axis name.
		// final Rect textBounds = new Rect();
		// mAxisTextPaint.getTextBounds("X", 0, 1, textBounds);
		// mYAxisMargin += textBounds.width() + mCommonMargin;
		// }
	}

	private float calculateRawX(float valueX) {
		final float pixelOffset = (valueX - mCurrentViewport.left) * (mContentRect.width() / mCurrentViewport.width());
		return mContentRect.left + pixelOffset;
	}

	private float calculateRawY(float valueY) {
		final float pixelOffset = (valueY - mCurrentViewport.top) * (mContentRect.height() / mCurrentViewport.height());
		return mContentRect.bottom - pixelOffset;
	}

	/**
	 * Finds the chart point (i.e. within the chart's domain and range) represented by the given pixel coordinates, if
	 * that pixel is within the chart region described by {@link #mContentRect}. If the point is found, the "dest"
	 * argument is set to the point and this function returns true. Otherwise, this function returns false and "dest" is
	 * unchanged.
	 */
	private boolean rawPixelsToDataPoint(float x, float y, PointF dest) {
		if (!mContentRect.contains((int) x, (int) y)) {
			return false;
		}
		dest.set(mCurrentViewport.left + (x - mContentRect.left) * (mCurrentViewport.width() / mContentRect.width()),
				mCurrentViewport.top + (y - mContentRect.bottom) * (mCurrentViewport.height() / -mContentRect.height()));
		return true;
	}
}
