package lecho.lib.hellocharts;

import java.util.List;

import lecho.lib.hellocharts.anim.ChartAnimator;
import lecho.lib.hellocharts.anim.ChartAnimatorV11;
import lecho.lib.hellocharts.anim.ChartAnimatorV8;
import lecho.lib.hellocharts.model.AnimatedValue;
import lecho.lib.hellocharts.model.ChartData;
import lecho.lib.hellocharts.model.InternalLineChartData;
import lecho.lib.hellocharts.model.InternalSeries;
import lecho.lib.hellocharts.utils.Config;
import lecho.lib.hellocharts.utils.Utils;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

/**
 * TODO nullcheck for mData
 * 
 * @author lecho
 * 
 */
public class LineChart extends View {
	private static final String TAG = "LineChart";
	private static final float LINE_SMOOTHNES = 0.16f;
	private InternalLineChartData mData;
	private Path mLinePath = new Path();
	private Paint mLinePaint = new Paint();
	private Paint mPointPaint = new Paint();
	private Paint mRulersPaint = new Paint();
	private float mLineWidth;
	private float mPointRadius;
	private float mXMultiplier;
	private float mYMultiplier;
	private float mAvailableWidth;
	private float mAvailableHeight;
	private int mhorizontalRulersDivider;
	boolean mInterpolationOn = true;
	boolean mHorizontalRulersOn = false;
	boolean mPointsOn = true;
	private ChartAnimator mAnimator;
	private ObjectAnimator mObjAnimator;
	private int mSelectedLineIndex = Integer.MIN_VALUE;
	private int mSelectedValueIndex = Integer.MIN_VALUE;

	public LineChart(Context context) {
		super(context);
		initAttributes();
		initPaints();
		initAnimatiors();
	}

	public LineChart(Context context, AttributeSet attrs) {
		super(context, attrs);
		initAttributes();
		initPaints();
		initAnimatiors();
	}

	public LineChart(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initAttributes();
		initPaints();
		initAnimatiors();
	}

	private void initAttributes() {
		mLineWidth = Utils.dp2px(getContext(), 3);
		mPointRadius = Utils.dp2px(getContext(), 8);
	}

	private void initPaints() {
		mLinePaint.setAntiAlias(true);
		mLinePaint.setStyle(Paint.Style.STROKE);
		mLinePaint.setStrokeWidth(mLineWidth);

		mPointPaint.setAntiAlias(true);
		mPointPaint.setStyle(Paint.Style.FILL);

		mRulersPaint.setStyle(Paint.Style.STROKE);
		mRulersPaint.setColor(Color.LTGRAY);
		mRulersPaint.setStrokeWidth(1);

	}

	private void initAnimatiors() {
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
			mAnimator = new ChartAnimatorV8(this, Config.ANIMATION_DURATION);
		} else {
			mAnimator = new ChartAnimatorV11(this, Config.ANIMATION_DURATION);
		}
	}

	@Override
	protected void onSizeChanged(int width, int height, int oldWidth, int oldHeight) {
		long time = System.nanoTime();
		super.onSizeChanged(width, height, oldWidth, oldHeight);
		// TODO mPointRadus can change, recalculate in setter
		calculateAvailableDimensions();
		// TODO max-min can chage( - recalculate in setter
		calculateMultipliers();
		if (mHorizontalRulersOn) {
			calculateHorizontalRulersDivider();
		}
		Log.v(TAG, "Zwymiarowane w [ms]: " + (System.nanoTime() - time) / 1000000);
	}

	private void calculateMultipliers() {
		mXMultiplier = mAvailableWidth / (mData.getMaxXValue() - mData.getMinXValue());
		mYMultiplier = mAvailableHeight / (mData.getMaxYValue() - mData.getMinYValue());
	}

	private void calculateAvailableDimensions() {
		mAvailableWidth = getWidth() - getPaddingLeft() - getPaddingRight() - 2 * mPointRadius;
		mAvailableHeight = getHeight() - getPaddingTop() - getPaddingBottom() - 2 * mPointRadius;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		long time = System.nanoTime();
		if (mHorizontalRulersOn) {
			drawHorizontalRulers(canvas);
		}
		drawLines(canvas);
		if (mPointsOn) {
			drawPoints(canvas);
		}
		Log.v(TAG, "Narysowane w [ms]: " + (System.nanoTime() - time) / 1000000);
		Log.v(TAG, "Wyświetlone w [ms]: " + (System.nanoTime() - time) / 1000000);
	}

	private void drawLines(Canvas canvas) {
		for (InternalSeries internalSeries : mData.getInternalsSeries()) {
			if (mInterpolationOn) {
				prepareSmoothPath(internalSeries);
			} else {
				preparePath(internalSeries);
			}
			mLinePaint.setColor(internalSeries.color);
			canvas.drawPath(mLinePath, mLinePaint);
			mLinePath.reset();
		}
	}

	private void drawPoints(Canvas canvas) {
		for (InternalSeries internalSeries : mData.getInternalsSeries()) {
			mPointPaint.setColor(internalSeries.color);
			int valueIndex = 0;
			for (float valueX : mData.getDomain()) {
				final float rawValueX = calculateX(valueX);
				final float rawValueY = calculateY(internalSeries.values.get(valueIndex).getPosition());
				if (mSelectedValueIndex == valueIndex) {
					mPointPaint.setColor(Color.RED);
				} else {
					mPointPaint.setColor(internalSeries.color);
				}
				canvas.drawCircle(rawValueX, rawValueY, mPointRadius, mPointPaint);
				++valueIndex;
			}
		}
	}

	private void preparePath(final InternalSeries internalSeries) {
		int valueIndex = 0;
		for (float valueX : mData.getDomain()) {
			final float rawValueX = calculateX(valueX);
			final float rawValueY = calculateY(internalSeries.values.get(valueIndex).getPosition());
			if (valueIndex == 0) {
				mLinePath.moveTo(rawValueX, rawValueY);
			} else {
				mLinePath.lineTo(rawValueX, rawValueY);
			}
			++valueIndex;
		}
	}

	private void prepareSmoothPath(final InternalSeries internalSeries) {
		for (int pointIndex = 0; pointIndex < mData.getDomain().size() - 1; ++pointIndex) {
			final float currentPointX = calculateX(mData.getDomain().get(pointIndex));
			final float currentPointY = calculateY(internalSeries.values.get(pointIndex).getPosition());
			final float nextPointX = calculateX(mData.getDomain().get(pointIndex + 1));
			final float nextPointY = calculateY(internalSeries.values.get(pointIndex + 1).getPosition());
			final float previousPointX;
			final float previousPointY;
			if (pointIndex > 0) {
				previousPointX = calculateX(mData.getDomain().get(pointIndex - 1));
				previousPointY = calculateY(internalSeries.values.get(pointIndex - 1).getPosition());
			} else {
				previousPointX = currentPointX;
				previousPointY = currentPointY;
			}
			final float afterNextPointX;
			final float afterNextPointY;
			if (pointIndex < mData.getDomain().size() - 2) {
				afterNextPointX = calculateX(mData.getDomain().get(pointIndex + 2));
				afterNextPointY = calculateY(internalSeries.values.get(pointIndex + 2).getPosition());
			} else {
				afterNextPointX = nextPointX;
				afterNextPointY = nextPointY;
			}
			final float firstDiffX = (nextPointX - previousPointX);
			final float firstDiffY = (nextPointY - previousPointY);
			final float secondDiffX = (afterNextPointX - currentPointX);
			final float secondDiffY = (afterNextPointY - currentPointY);
			final float firstControlPointX = currentPointX + (LINE_SMOOTHNES * firstDiffX);
			final float firstControlPointY = currentPointY + (LINE_SMOOTHNES * firstDiffY);
			final float secondControlPointX = nextPointX - (LINE_SMOOTHNES * secondDiffX);
			final float secondControlPointY = nextPointY - (LINE_SMOOTHNES * secondDiffY);
			mLinePath.moveTo(currentPointX, currentPointY);
			mLinePath.cubicTo(firstControlPointX, firstControlPointY, secondControlPointX, secondControlPointY,
					nextPointX, nextPointY);
		}
	}

	private float calculateX(float valueX) {
		return getPaddingLeft() + mPointRadius + (valueX - mData.getMinXValue()) * mXMultiplier;
	}

	private float calculateY(float valueY) {
		return getHeight() - getPaddingBottom() - mPointRadius - (valueY - mData.getMinYValue()) * mYMultiplier;
	}

	/**
	 * Calculates how many horizontal rulers will be visible on chart if user enabled rulers. Should be called before
	 * drawHorizontalRulers().
	 */
	private void calculateHorizontalRulersDivider() {
		final float scale = getResources().getDisplayMetrics().density;
		// divider should be integer
		int divider = Math.round((mAvailableHeight / scale) / 128.0f);
		// if user want rulers give him at least 3 - lower, upper and one in the middle. Three rulers will divide chart
		// into two areas that why divider will be >=2;
		if (divider < 2) {
			divider = 2;
		}
		mhorizontalRulersDivider = divider;
	}

	/**
	 * Draw horizontal Rulers. Number or lines is determined by chart height and screen resolution.
	 */
	private void drawHorizontalRulers(Canvas canvas) {
		float rawMinX = calculateX(mData.getMinXValue()) - mPointRadius;
		float rawMaxX = calculateX(mData.getMaxXValue()) + mPointRadius;
		float rawMinY = calculateY(mData.getMinYValue());
		float rawMaxY = calculateY(mData.getMaxYValue());
		mLinePath.moveTo(rawMinX, rawMinY);
		mLinePath.lineTo(rawMaxX, rawMinY);
		canvas.drawPath(mLinePath, mRulersPaint);
		mLinePath.reset();
		mLinePath.moveTo(rawMinX, rawMaxY);
		mLinePath.lineTo(rawMaxX, rawMaxY);
		canvas.drawPath(mLinePath, mRulersPaint);
		mLinePath.reset();
		final float step = (mData.getMaxYValue() - mData.getMinYValue()) / mhorizontalRulersDivider;
		for (int i = 1; i < mhorizontalRulersDivider; ++i) {
			final float rawValueY = calculateY(mData.getMinYValue() + step * i);
			mLinePath.moveTo(rawMinX, rawValueY);
			mLinePath.lineTo(rawMaxX, rawValueY);
			canvas.drawPath(mLinePath, mRulersPaint);
			mLinePath.reset();
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_DOWN) {

			float touchX = event.getX();
			float touchY = event.getY();
			int lineIndex = 0;
			for (InternalSeries series : mData.getInternalsSeries()) {
				int valueIndex = 0;
				for (AnimatedValue value : series.values) {
					float x = calculateX(mData.getDomain().get(valueIndex));
					float y = calculateY(value.getPosition());
					boolean isInArea = Utils.isInArea(x, y, touchX, touchY, Utils.dp2px(getContext(), 24));
					if (isInArea) {
						mSelectedLineIndex = lineIndex;
						mSelectedValueIndex = valueIndex;
						invalidate();
						return true;
					}
					++valueIndex;
				}
				++lineIndex;
			}
		}
		return true;
	}

	public void setData(final ChartData rawData) {
		mData = InternalLineChartData.createFromRawDara(rawData);
		mData.calculateRanges();
		postInvalidate();
	}

	public ChartData getData() {
		return mData.getRawData();
	}

	public void animationUpdate(float scale) {
		for (AnimatedValue value : mData.getInternalsSeries().get(0).values) {
			value.update(scale);
		}
		mData.calculateRanges();
		calculateAvailableDimensions();
		calculateMultipliers();
		invalidate();
	}

	public void animateSeries(int index, List<Float> values) {
		mData.updateSeriesTargetPositions(index, values);
		mAnimator.startAnimation();
	}

	public void updateSeries(int index, List<Float> values) {
		mData.updateSeries(index, values);
		postInvalidate();
	}

}
