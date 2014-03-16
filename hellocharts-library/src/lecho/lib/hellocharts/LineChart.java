package lecho.lib.hellocharts;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import lecho.lib.hellocharts.anim.ChartAnimator;
import lecho.lib.hellocharts.anim.ChartAnimatorV11;
import lecho.lib.hellocharts.anim.ChartAnimatorV8;
import lecho.lib.hellocharts.gestures.Zoomer;
import lecho.lib.hellocharts.model.AnimatedValue;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.ChartData;
import lecho.lib.hellocharts.model.InternalLineChartData;
import lecho.lib.hellocharts.model.InternalSeries;
import lecho.lib.hellocharts.utils.Config;
import lecho.lib.hellocharts.utils.Utils;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.Cap;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Build;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ScrollerCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.OverScroller;
import android.widget.Scroller;

public class LineChart extends View {
	private static final String TAG = "LineChart";
	private static final float LINE_SMOOTHNES = 0.16f;
	private static final int DEFAULT_LINE_WIDTH_DP = 3;
	private static final int DEFAULT_POINT_RADIUS_DP = 6;
	private static final int DEFAULT_POINT_TOUCH_RADIUS_DP = 12;
	private static final int DEFAULT_POINT_PRESSED_RADIUS = DEFAULT_POINT_RADIUS_DP + 4;
	private static final int DEFAULT_POPUP_TEXT_MARGIN = 4;
	private static final int DEFAULT_TEXT_SIZE_DP = 14;
	private static final int DEFAULT_TEXT_COLOR = Color.WHITE;
	private static final int DEFAULT_AXIS_COLOR = Color.LTGRAY;
	private static final int DEFAULT_AREA_TRANSPARENCY = 64;
	private static final float ZOOM_AMOUNT = 0.25f;
	private int mCommonMargin;
	private int mPopupTextMargin;
	private Path mLinePath = new Path();
	private Paint mLinePaint = new Paint();
	private Paint mTextPaint = new Paint();
	private InternalLineChartData mData;
	private float mLineWidth;
	private float mPointRadius;
	private float mPointPressedRadius;
	private float mTouchRadius;
	private int mYAxisMargin = 0;
	private int mXAxisMargin = 0;
	private boolean mLinesOn = true;
	private boolean mInterpolationOn = false;
	private boolean mPointsOn = true;
	private boolean mPopupsOn = false;
	private boolean mAxesOn = true;
	private ChartAnimator mAnimator;
	private int mSelectedSeriesIndex = Integer.MIN_VALUE;
	private int mSelectedValueIndex = Integer.MIN_VALUE;
	/**
	 * The current area (in pixels) for chart data, including mCoomonMargin. Labels are drawn outside this area.
	 */
	private Rect mContentRect = new Rect();
	private Rect mContentRectWithMargins = new Rect();
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
	private Zoomer mZoomer;
	private ScrollerCompat mScroller;
	private PointF mZoomFocalPoint = new PointF();// Used for double tap zoom
	private RectF mScrollerStartViewport = new RectF(); // Used only for zooms and flings.
	private Point mSurfaceSizeBuffer = new Point();

	private OnPointClickListener mOnPointClickListener = new DummyOnPointListener();
	private ScaleGestureDetector mScaleGestureDetector = new ScaleGestureDetector(getContext(),
			new ChartScaleGestureListener());
	private GestureDetector mGestureDetector = new GestureDetector(getContext(), new ChartGestureListener());

	public LineChart(Context context) {
		this(context, null, 0);
	}

	public LineChart(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public LineChart(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initAttributes();
		initPaints();
		initAnimatiors();
	}

	@SuppressLint("NewApi")
	private void initAttributes() {
		setLayerType(LAYER_TYPE_SOFTWARE, null);
		mLineWidth = Utils.dp2px(getContext(), DEFAULT_LINE_WIDTH_DP);
		mPointRadius = Utils.dp2px(getContext(), DEFAULT_POINT_RADIUS_DP);
		mPointPressedRadius = Utils.dp2px(getContext(), DEFAULT_POINT_PRESSED_RADIUS);
		mTouchRadius = Utils.dp2px(getContext(), DEFAULT_POINT_TOUCH_RADIUS_DP);
		mCommonMargin = Utils.dp2px(getContext(), DEFAULT_POINT_PRESSED_RADIUS);
		mPopupTextMargin = Utils.dp2px(getContext(), DEFAULT_POPUP_TEXT_MARGIN);
		mScroller = ScrollerCompat.create(getContext());
		mZoomer = new Zoomer(getContext());
	}

	private void initPaints() {
		mLinePaint.setAntiAlias(true);
		mLinePaint.setStyle(Paint.Style.STROKE);
		mLinePaint.setStrokeCap(Cap.ROUND);

		mTextPaint.setAntiAlias(true);
		mTextPaint.setStyle(Paint.Style.FILL);
		mTextPaint.setStrokeWidth(1);
		mTextPaint.setTextSize(Utils.dp2px(getContext(), DEFAULT_TEXT_SIZE_DP));

	}

	private void initAnimatiors() {
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
			mAnimator = new ChartAnimatorV8(this, Config.DEFAULT_ANIMATION_DURATION);
		} else {
			mAnimator = new ChartAnimatorV11(this, Config.DEFAULT_ANIMATION_DURATION);
		}
	}

	@Override
	protected void onSizeChanged(int width, int height, int oldWidth, int oldHeight) {
		super.onSizeChanged(width, height, oldWidth, oldHeight);
		// TODO mPointRadus can change, recalculate in setter
		calculateContentArea();
		calculateViewport();
	}

	/**
	 * Calculates available width and height. Should be called when chart dimensions or chart data change.
	 */
	private void calculateContentArea() {
		mContentRectWithMargins.set(getPaddingLeft() + mYAxisMargin, getPaddingTop(), getWidth() - getPaddingRight(),
				getHeight() - getPaddingBottom() - mXAxisMargin);
		mContentRect.set(mContentRectWithMargins.left + mCommonMargin, mContentRectWithMargins.top + mCommonMargin,
				mContentRectWithMargins.right - mCommonMargin, mContentRectWithMargins.bottom - mCommonMargin);
	}

	private void calculateViewport() {
		mMaximumViewport.set(mData.getMinXValue(), mData.getMinYValue(), mData.getMaxXValue(), mData.getMaxYValue());
		mCurrentViewport.set(mMaximumViewport);
	}

	private void constrainViewport() {
		// TODO: avoid too much zoom by checking if mMaximumViewport.width/mCurrentViewport.width <= maxZoomLevel
		mCurrentViewport.left = Math.max(mMaximumViewport.left, mCurrentViewport.left);
		mCurrentViewport.top = Math.max(mMaximumViewport.top, mCurrentViewport.top);
		mCurrentViewport.bottom = Math.min(mMaximumViewport.bottom, mCurrentViewport.bottom);
		mCurrentViewport.right = Math.min(mMaximumViewport.right, mCurrentViewport.right);
	}

	private void calculateYAxisMargin() {
		if (mAxesOn) {
			final Rect textBounds = new Rect();
			final String text;
			// TODO: check if axis has at least one element.
			final int axisSize = mData.getYAxis().getValues().size();
			final Axis yAxis = mData.getYAxis();
			if (Math.abs(yAxis.getValues().get(0)) > Math.abs(yAxis.getValues().get(axisSize - 1))) {
				text = getAxisValueToDraw(yAxis, yAxis.getValues().get(0), 0);
			} else {
				text = getAxisValueToDraw(yAxis, yAxis.getValues().get(axisSize - 1), axisSize - 1);
			}
			mTextPaint.getTextBounds(text, 0, text.length(), textBounds);
			mYAxisMargin = textBounds.width();
		} else {
			mYAxisMargin = 0;
		}
	}

	private void calculateXAxisMargin() {
		if (mAxesOn) {
			final Rect textBounds = new Rect();
			// Hard coded only for text height calculation.
			mTextPaint.getTextBounds("X", 0, 1, textBounds);
			mXAxisMargin = textBounds.height();
		} else {
			mYAxisMargin = 0;
		}
	}

	// Automatically calculates Y axis values.
	private Axis calculateYAxis(int numberOfSteps) {
		if (numberOfSteps < 2) {
			throw new IllegalArgumentException("Number or steps have to be grater or equal 2");
		}
		List<Float> values = new ArrayList<Float>();
		final float range = mData.getMaxYValue() - mData.getMinYValue();
		final float tickRange = range / (numberOfSteps - 1);
		final float x = (float) Math.ceil(Math.log10(tickRange) - 1);
		final float pow10x = (float) Math.pow(10, x);
		final float roundedTickRange = (float) Math.ceil(tickRange / pow10x) * pow10x;
		float value = mData.getMinYValue();
		while (value <= mData.getMaxYValue()) {
			values.add(value);
			value += roundedTickRange;
		}
		Axis yAxis = new Axis();
		yAxis.setValues(values);
		return yAxis;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		long time = System.nanoTime();
		super.onDraw(canvas);
		if (mAxesOn) {
			drawXAxis(canvas);
			drawYAxis(canvas);
		}
		int clipRestoreCount = canvas.save();
		if (mMaximumViewport.equals(mCurrentViewport)) {
			canvas.clipRect(mContentRectWithMargins);
		} else {
			canvas.clipRect(mContentRect);
		}
		if (mLinesOn) {
			drawLines(canvas);
		}
		if (mPointsOn) {
			drawPoints(canvas);
		}
		canvas.restoreToCount(clipRestoreCount);
		Log.v(TAG, "onDraw [ms]: " + (System.nanoTime() - time) / 1000000f);
	}

	private void drawXAxis(Canvas canvas) {
		mLinePaint.setStrokeWidth(1);
		mLinePaint.setColor(DEFAULT_AXIS_COLOR);
		mTextPaint.setColor(DEFAULT_AXIS_COLOR);
		mTextPaint.setTextAlign(Align.CENTER);
		final int xAxisBaseline = mContentRectWithMargins.bottom + mXAxisMargin;
		canvas.drawLine(mContentRectWithMargins.left, mContentRect.bottom, mContentRectWithMargins.right,
				mContentRect.bottom, mLinePaint);
		Axis xAxis = mData.getXAxis();
		int index = 0;
		for (float x : xAxis.getValues()) {
			final String text = getAxisValueToDraw(xAxis, x, index);
			// TODO: check if raw x > contentArea.left
			canvas.drawText(text, calculatePixelX(x), xAxisBaseline, mTextPaint);
			++index;
		}
	}

	private void drawYAxis(Canvas canvas) {
		mLinePaint.setStrokeWidth(1);
		mLinePaint.setColor(DEFAULT_AXIS_COLOR);
		mTextPaint.setColor(DEFAULT_AXIS_COLOR);
		mTextPaint.setTextAlign(Align.RIGHT);
		Axis yAxis = mData.getYAxis();
		int index = 0;
		for (float y : yAxis.getValues()) {
			// Draw only if y is in chart range
			if (y >= mData.getMinYValue() && y <= mData.getMaxYValue()) {
				final String text = getAxisValueToDraw(yAxis, y, index);
				float rawY = calculatePixelY(y);
				canvas.drawLine(mContentRectWithMargins.left, rawY, mContentRectWithMargins.right, rawY, mLinePaint);
				canvas.drawText(text, mContentRectWithMargins.left, rawY, mTextPaint);
			}
			++index;
		}
	}

	private String getAxisValueToDraw(Axis axis, Float value, int index) {
		if (axis.isStringAxis()) {
			return axis.getStringValues().get(index);
		} else {
			return String.format(axis.getValueFormatter(), value);
		}
	}

	private void drawLines(Canvas canvas) {
		mLinePaint.setStrokeWidth(mLineWidth);
		for (InternalSeries internalSeries : mData.getInternalsSeries()) {
			if (mInterpolationOn) {
				drawSmoothPath(canvas, internalSeries);
			} else {
				drawPath(canvas, internalSeries);
			}
			mLinePath.reset();
		}
	}

	// TODO Drawing points can be done in the same loop as drawing lines but it may cause problems in the future. Reuse
	// calculated X/Y;
	private void drawPoints(Canvas canvas) {
		for (InternalSeries internalSeries : mData.getInternalsSeries()) {
			mTextPaint.setColor(internalSeries.getColor());
			int valueIndex = 0;
			for (float valueX : mData.getDomain()) {
				final float rawValueX = calculatePixelX(valueX);
				final float valueY = internalSeries.getValues().get(valueIndex).getPosition();
				final float rawValueY = calculatePixelY(valueY);
				canvas.drawCircle(rawValueX, rawValueY, mPointRadius, mTextPaint);
				if (mPopupsOn) {
					drawValuePopup(canvas, mPopupTextMargin, valueY, rawValueX, rawValueY);
				}
				++valueIndex;
			}
		}
		if (mSelectedSeriesIndex >= 0 && mSelectedValueIndex >= 0) {
			final float valueX = mData.getDomain().get(mSelectedValueIndex);
			final float rawValueX = calculatePixelX(valueX);
			final float valueY = mData.getInternalsSeries().get(mSelectedSeriesIndex).getValues()
					.get(mSelectedValueIndex).getPosition();
			final float rawValueY = calculatePixelY(valueY);
			mTextPaint.setColor(mData.getInternalsSeries().get(mSelectedSeriesIndex).getColor());
			canvas.drawCircle(rawValueX, rawValueY, mPointPressedRadius, mTextPaint);
			if (mPopupsOn) {
				drawValuePopup(canvas, mPopupTextMargin, valueY, rawValueX, rawValueY);
			}
		}
	}

	private void drawValuePopup(Canvas canvas, float offset, float valueY, float rawValueX, float rawValueY) {
		mTextPaint.setTextAlign(Align.LEFT);
		final String text = String.format(Locale.ENGLISH, Config.DEFAULT_VALUE_FORMAT, valueY);
		final Rect textBounds = new Rect();
		mTextPaint.getTextBounds(text, 0, text.length(), textBounds);
		float left = rawValueX + offset;
		float right = rawValueX + offset + textBounds.width() + mPopupTextMargin * 2;
		float top = rawValueY - offset - textBounds.height() - mPopupTextMargin * 2;
		float bottom = rawValueY - offset;
		if (top < getPaddingTop() + mCommonMargin) {
			top = rawValueY + offset;
			bottom = rawValueY + offset + textBounds.height() + mPopupTextMargin * 2;
		}
		if (right > getWidth() - getPaddingRight() - mCommonMargin) {
			left = rawValueX - offset - textBounds.width() - mPopupTextMargin * 2;
			right = rawValueX - offset;
		}
		final RectF popup = new RectF(left, top, right, bottom);
		canvas.drawRoundRect(popup, mPopupTextMargin, mPopupTextMargin, mTextPaint);
		final int color = mTextPaint.getColor();
		mTextPaint.setColor(DEFAULT_TEXT_COLOR);
		canvas.drawText(text, left + mPopupTextMargin, bottom - mPopupTextMargin, mTextPaint);
		mTextPaint.setColor(color);
	}

	private void drawPath(Canvas canvas, final InternalSeries internalSeries) {
		int valueIndex = 0;
		for (float valueX : mData.getDomain()) {
			final float rawValueX = calculatePixelX(valueX);
			final float rawValueY = calculatePixelY(internalSeries.getValues().get(valueIndex).getPosition());
			if (valueIndex == 0) {
				mLinePath.moveTo(rawValueX, rawValueY);
			} else {
				mLinePath.lineTo(rawValueX, rawValueY);
			}
			++valueIndex;
		}
		mLinePaint.setColor(internalSeries.getColor());
		canvas.drawPath(mLinePath, mLinePaint);
		// drawArea(canvas);
	}

	private void drawSmoothPath(Canvas canvas, final InternalSeries internalSeries) {
		final int domainSize = mData.getDomain().size();
		float previousPointX = Float.NaN;
		float previousPointY = Float.NaN;
		float currentPointX = Float.NaN;
		float currentPointY = Float.NaN;
		float nextPointX = Float.NaN;
		float nextPointY = Float.NaN;
		for (int valueIndex = 0; valueIndex < domainSize - 1; ++valueIndex) {
			if (Float.isNaN(currentPointX)) {
				currentPointX = calculatePixelX(mData.getDomain().get(valueIndex));
				currentPointY = calculatePixelY(internalSeries.getValues().get(valueIndex).getPosition());
			}
			if (Float.isNaN(previousPointX)) {
				if (valueIndex > 0) {
					previousPointX = calculatePixelX(mData.getDomain().get(valueIndex - 1));
					previousPointY = calculatePixelY(internalSeries.getValues().get(valueIndex - 1).getPosition());
				} else {
					previousPointX = currentPointX;
					previousPointY = currentPointY;
				}
			}
			if (Float.isNaN(nextPointX)) {
				nextPointX = calculatePixelX(mData.getDomain().get(valueIndex + 1));
				nextPointY = calculatePixelY(internalSeries.getValues().get(valueIndex + 1).getPosition());
			}
			// afterNextPoint is always new one or it is equal nextPoint.
			final float afterNextPointX;
			final float afterNextPointY;
			if (valueIndex < domainSize - 2) {
				afterNextPointX = calculatePixelX(mData.getDomain().get(valueIndex + 2));
				afterNextPointY = calculatePixelY(internalSeries.getValues().get(valueIndex + 2).getPosition());
			} else {
				afterNextPointX = nextPointX;
				afterNextPointY = nextPointY;
			}
			// Calculate control points.
			final float firstDiffX = (nextPointX - previousPointX);
			final float firstDiffY = (nextPointY - previousPointY);
			final float secondDiffX = (afterNextPointX - currentPointX);
			final float secondDiffY = (afterNextPointY - currentPointY);
			final float firstControlPointX = currentPointX + (LINE_SMOOTHNES * firstDiffX);
			final float firstControlPointY = currentPointY + (LINE_SMOOTHNES * firstDiffY);
			final float secondControlPointX = nextPointX - (LINE_SMOOTHNES * secondDiffX);
			final float secondControlPointY = nextPointY - (LINE_SMOOTHNES * secondDiffY);
			// Move to start point.
			if (valueIndex == 0) {
				mLinePath.moveTo(currentPointX, currentPointY);
			}
			mLinePath.cubicTo(firstControlPointX, firstControlPointY, secondControlPointX, secondControlPointY,
					nextPointX, nextPointY);
			// Shift values to prevent recalculation of values that have been already calculated.
			previousPointX = currentPointX;
			previousPointY = currentPointY;
			currentPointX = nextPointX;
			currentPointY = nextPointY;
			nextPointX = afterNextPointX;
			nextPointY = afterNextPointY;
		}
		mLinePaint.setColor(internalSeries.getColor());
		canvas.drawPath(mLinePath, mLinePaint);
		drawArea(canvas);
	}

	private void drawArea(Canvas canvas) {
		// TODO: avoid coordinates recalculations
		final float rawStartValueX = calculatePixelX(mData.getDomain().get(0));
		final float rawStartValueY = calculatePixelY(mData.getMinYValue());
		final float rawEndValueX = calculatePixelX(mData.getDomain().get(mData.getDomain().size() - 1));
		final float rawEntValueY = rawStartValueY;
		mLinePaint.setStyle(Paint.Style.FILL);
		mLinePaint.setAlpha(DEFAULT_AREA_TRANSPARENCY);
		mLinePath.lineTo(rawEndValueX, rawEntValueY);
		mLinePath.lineTo(rawStartValueX, rawStartValueY);
		mLinePath.close();
		canvas.drawPath(mLinePath, mLinePaint);
		mLinePaint.setStyle(Paint.Style.STROKE);
	}

	private float calculatePixelX(float valueX) {
		final float pixelOffset = (valueX - mCurrentViewport.left) * (mContentRect.width() / mCurrentViewport.width());
		return mContentRect.left + pixelOffset;
	}

	private float calculatePixelY(float valueY) {
		final float pixelOffset = (valueY - mCurrentViewport.top) * (mContentRect.height() / mCurrentViewport.height());
		return mContentRect.bottom - pixelOffset;
	}

	/**
	 * Finds the chart point (i.e. within the chart's domain and range) represented by the given pixel coordinates, if
	 * that pixel is within the chart region described by {@link #mContentRect}. If the point is found, the "dest"
	 * argument is set to the point and this function returns true. Otherwise, this function returns false and "dest" is
	 * unchanged.
	 */
	private boolean pixelsToPoint(float x, float y, PointF dest) {
		if (!mContentRect.contains((int) x, (int) y)) {
			return false;
		}
		dest.set(mCurrentViewport.left + (x - mContentRect.left) * (mCurrentViewport.width() / mContentRect.width()),
				mCurrentViewport.top + (y - mContentRect.bottom) * (mCurrentViewport.height() / -mContentRect.height()));
		return true;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		mScaleGestureDetector.onTouchEvent(event);
		mGestureDetector.onTouchEvent(event);
		if (!mPointsOn) {
			// No point - no touch events.
			return true;
		}
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			// Select only the first value within touched area.
			// Reverse loop to starts with the line drawn on top.
			for (int seriesIndex = mData.getInternalsSeries().size() - 1; seriesIndex >= 0; --seriesIndex) {
				int valueIndex = 0;
				for (AnimatedValue value : mData.getInternalsSeries().get(seriesIndex).getValues()) {
					final float rawX = calculatePixelX(mData.getDomain().get(valueIndex));
					final float rawY = calculatePixelY(value.getPosition());
					if (Utils.isInArea(rawX, rawY, event.getX(), event.getY(), mTouchRadius)) {
						mSelectedSeriesIndex = seriesIndex;
						mSelectedValueIndex = valueIndex;
						invalidate();
						return true;
					}
					++valueIndex;
				}
			}
			return true;
		case MotionEvent.ACTION_UP:
			// If value was selected call click listener and clear selection.
			if (mSelectedValueIndex >= 0) {
				final float x = mData.getDomain().get(mSelectedValueIndex);
				final float y = mData.getInternalsSeries().get(mSelectedSeriesIndex).getValues()
						.get(mSelectedValueIndex).getPosition();
				mOnPointClickListener.onPointClick(mSelectedSeriesIndex, mSelectedValueIndex, x, y);
				mSelectedSeriesIndex = Integer.MIN_VALUE;
				mSelectedValueIndex = Integer.MIN_VALUE;
				invalidate();
			}
			return true;
		case MotionEvent.ACTION_MOVE:
			// Clear selection if user is now touching outside touch area.
			if (mSelectedValueIndex >= 0) {
				final float x = mData.getDomain().get(mSelectedValueIndex);
				final float y = mData.getInternalsSeries().get(mSelectedSeriesIndex).getValues()
						.get(mSelectedValueIndex).getPosition();
				final float rawX = calculatePixelX(x);
				final float rawY = calculatePixelY(y);
				if (!Utils.isInArea(rawX, rawY, event.getX(), event.getY(), mTouchRadius)) {
					mSelectedSeriesIndex = Integer.MIN_VALUE;
					mSelectedValueIndex = Integer.MIN_VALUE;
					invalidate();
				}
			}
			return true;
		case MotionEvent.ACTION_CANCEL:
			// Clear selection
			if (mSelectedValueIndex >= 0) {
				mSelectedSeriesIndex = Integer.MIN_VALUE;
				mSelectedValueIndex = Integer.MIN_VALUE;
				invalidate();
			}
			return true;
		default:
			return true;
		}
	}

	@Override
	public void computeScroll() {
		super.computeScroll();
		if (mScroller.computeScrollOffset()) {
			// The scroller isn't finished, meaning a fling or programmatic pan operation is
			// currently active.
			computeScrollSurfaceSize(mSurfaceSizeBuffer);
			int currX = mScroller.getCurrX();
			int currY = mScroller.getCurrY();
			float currXRange = mMaximumViewport.left + (mMaximumViewport.width()) * currX / mSurfaceSizeBuffer.x;
			float currYRange = mMaximumViewport.bottom - (mMaximumViewport.height()) * currY / mSurfaceSizeBuffer.y;
			setViewportBottomLeft(currXRange, currYRange);
		}

		if (mZoomer.computeZoom()) {
			// Performs the zoom since a zoom is in progress (either programmatically or via
			// double-touch).
			final float newWidth = (1.0f - mZoomer.getCurrZoom()) * mScrollerStartViewport.width();
			final float newHeight = (1.0f - mZoomer.getCurrZoom()) * mScrollerStartViewport.height();
			final float pointWithinViewportX = (mZoomFocalPoint.x - mScrollerStartViewport.left)
					/ mScrollerStartViewport.width();
			final float pointWithinViewportY = (mZoomFocalPoint.y - mScrollerStartViewport.top)
					/ mScrollerStartViewport.height();
			mCurrentViewport.left = mZoomFocalPoint.x - newWidth * pointWithinViewportX;
			mCurrentViewport.top = mZoomFocalPoint.y - newHeight * pointWithinViewportY;
			mCurrentViewport.right = mZoomFocalPoint.x + newWidth * (1 - pointWithinViewportX);
			mCurrentViewport.bottom = mZoomFocalPoint.y + newHeight * (1 - pointWithinViewportY);
			constrainViewport();
			ViewCompat.postInvalidateOnAnimation(this);
		}
	}

	private void fling(int velocityX, int velocityY) {
		// releaseEdgeEffects();
		// Flings use math in pixels (as opposed to math based on the viewport).
		computeScrollSurfaceSize(mSurfaceSizeBuffer);
		mScrollerStartViewport.set(mCurrentViewport);
		int startX = (int) (mSurfaceSizeBuffer.x * (mScrollerStartViewport.left - mMaximumViewport.left) / mMaximumViewport
				.width());
		int startY = (int) (mSurfaceSizeBuffer.y * (mMaximumViewport.bottom - mScrollerStartViewport.bottom) / mMaximumViewport
				.height());
		mScroller.abortAnimation();// probably should be mScroller.forceFinish but compat doesn't have that method.
		mScroller.fling(startX, startY, velocityX, velocityY, 0, mSurfaceSizeBuffer.x - mContentRect.width(), 0,
				mSurfaceSizeBuffer.y - mContentRect.height(), mContentRect.width() / 2, mContentRect.height() / 2);
		ViewCompat.postInvalidateOnAnimation(this);
	}

	/**
	 * Computes the current scrollable surface size, in pixels. For example, if the entire chart area is visible, this
	 * is simply the current size of {@link #mContentRect}. If the chart is zoomed in 200% in both directions, the
	 * returned size will be twice as large horizontally and vertically.
	 */
	private void computeScrollSurfaceSize(Point out) {
		out.set((int) (mContentRect.width() * mMaximumViewport.width() / mCurrentViewport.width()),
				(int) (mContentRect.height() * mMaximumViewport.height() / mCurrentViewport.height()));
	}

	/**
	 * Sets the current viewport (defined by {@link #mCurrentViewport}) to the given X and Y positions. Note that the Y
	 * value represents the topmost pixel position, and thus the bottom of the {@link #mCurrentViewport} rectangle. For
	 * more details on why top and bottom are flipped, see {@link #mCurrentViewport}.
	 */
	private void setViewportBottomLeft(float x, float y) {
		/**
		 * Constrains within the scroll range. The scroll range is simply the viewport extremes (AXIS_X_MAX, etc.) minus
		 * the viewport size. For example, if the extrema were 0 and 10, and the viewport size was 2, the scroll range
		 * would be 0 to 8.
		 */

		float curWidth = mCurrentViewport.width();
		float curHeight = mCurrentViewport.height();
		x = Math.max(mMaximumViewport.left, Math.min(x, mMaximumViewport.right - curWidth));
		y = Math.max(mMaximumViewport.top + curHeight, Math.min(y, mMaximumViewport.bottom));

		mCurrentViewport.set(x, y - curHeight, x + curWidth, y);
		ViewCompat.postInvalidateOnAnimation(this);
	}

	public void setData(final ChartData rawData) {
		mData = InternalLineChartData.createFromRawData(rawData);
		mData.calculateRanges();
		calculateYAxisMargin();
		calculateXAxisMargin();
		calculateContentArea();
		calculateViewport();
		ViewCompat.postInvalidateOnAnimation(LineChart.this);
	}

	public ChartData getData() {
		return mData.getRawData();
	}

	public void animationUpdate(float scale) {
		for (AnimatedValue value : mData.getInternalsSeries().get(0).getValues()) {
			value.update(scale);
		}
		mData.calculateYRanges();
		calculateYAxisMargin();
		calculateXAxisMargin();
		calculateViewport();
		ViewCompat.postInvalidateOnAnimation(LineChart.this);
	}

	public void animateSeries(int index, List<Float> values) {
		mAnimator.cancelAnimation();
		mData.updateSeriesTargetPositions(index, values);
		mAnimator.startAnimation();
	}

	public void updateSeries(int index, List<Float> values) {
		mData.updateSeries(index, values);
		ViewCompat.postInvalidateOnAnimation(LineChart.this);
	}

	public void setOnPointClickListener(OnPointClickListener listener) {
		if (null == listener) {
			mOnPointClickListener = new DummyOnPointListener();
		} else {
			mOnPointClickListener = listener;
		}
	}

	// Just empty listener to avoid NPE checks.
	private static class DummyOnPointListener implements OnPointClickListener {

		@Override
		public void onPointClick(int selectedSeriesIndex, int selectedValueIndex, float x, float y) {
			// Do nothing.
		}

	}

	private class ChartScaleGestureListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
		private PointF viewportFocus = new PointF();

		@Override
		public boolean onScale(ScaleGestureDetector detector) {
			/**
			 * Smaller viewport means bigger zoom so for zoomIn scale should have value <1, for zoomOout >1
			 */
			float scale = 2.0f - detector.getScaleFactor();
			final float newWidth = scale * mCurrentViewport.width();
			final float newHeight = scale * mCurrentViewport.height();
			final float focusX = detector.getFocusX();
			final float focusY = detector.getFocusY();
			pixelsToPoint(focusX, focusY, viewportFocus);
			mCurrentViewport.left = viewportFocus.x - (focusX - mContentRect.left) * (newWidth / mContentRect.width());
			mCurrentViewport.top = viewportFocus.y - (mContentRect.bottom - focusY)
					* (newHeight / mContentRect.height());
			mCurrentViewport.right = mCurrentViewport.left + newWidth;
			mCurrentViewport.bottom = mCurrentViewport.top + newHeight;
			constrainViewport();
			ViewCompat.postInvalidateOnAnimation(LineChart.this);
			return true;
		}
	}

	private class ChartGestureListener extends GestureDetector.SimpleOnGestureListener {
		@Override
		public boolean onDown(MotionEvent e) {
			// releaseEdgeEffects();
			mScrollerStartViewport.set(mCurrentViewport);
			// mScroller.forceFinished(true);
			ViewCompat.postInvalidateOnAnimation(LineChart.this);
			return true;
		}

		@Override
		public boolean onDoubleTap(MotionEvent e) {
			mZoomer.forceFinished(true);
			if (pixelsToPoint(e.getX(), e.getY(), mZoomFocalPoint)) {
				mZoomer.startZoom(ZOOM_AMOUNT);
			}
			ViewCompat.postInvalidateOnAnimation(LineChart.this);
			return true;
		}

		@Override
		public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
			// Scrolling uses math based on the viewport (as opposed to math using pixels).
			/**
			 * Pixel offset is the offset in screen pixels, while viewport offset is the offset within the current
			 * viewport. For additional information on surface sizes and pixel offsets, see the docs for {@link
			 * computeScrollSurfaceSize()}. For additional information about the viewport, see the comments for
			 * {@link mCurrentViewport}.
			 */
			float viewportOffsetX = distanceX * mCurrentViewport.width() / mContentRect.width();
			float viewportOffsetY = -distanceY * mCurrentViewport.height() / mContentRect.height();
			computeScrollSurfaceSize(mSurfaceSizeBuffer);
			setViewportBottomLeft(mCurrentViewport.left + viewportOffsetX, mCurrentViewport.bottom + viewportOffsetY);
			return true;
		}

		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
			fling((int) -velocityX, (int) -velocityY);
			return true;
		}
	}

}
