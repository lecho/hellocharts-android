package lecho.lib.hellocharts;

import java.util.List;

import lecho.lib.hellocharts.model.DynamicValue;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.LineSeries;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.AnimationUtils;

/**
 * TODO nullcheck for mData
 * 
 * @author lecho
 * 
 */
public class LineChart extends View {
	private static final String TAG = "LineChart";
	private static final float LINE_SMOOTHNES = 0.16f;
	private LineChartData mData;
	private Path mLinePath = new Path();
	private Paint mLinePaint = new Paint();
	private Paint mPointPaint = new Paint();
	private Paint mRulersPaint = new Paint();
	private float mLineWidth;
	private float mPointRadius;
	private float minXValue = Float.MAX_VALUE;
	private float maxXValue = Float.MIN_VALUE;
	private float minYValue = Float.MAX_VALUE;
	private float maxYValue = Float.MIN_VALUE;
	private float mXMultiplier;
	private float mYMultiplier;
	private float mAvailableWidth;
	private float mAvailableHeight;
	private int mhorizontalRulersDivider;
	boolean mInterpolationOn = true;
	boolean mHorizontalRulersOn = false;
	boolean mPointsOn = true;

	private Runnable animator = new Runnable() {
		@Override
		public void run() {
			boolean needNewFrame = false;
			long now = AnimationUtils.currentAnimationTimeMillis();
			for (DynamicValue dynamicValue : mData.series.get(0).values) {
				dynamicValue.update(now);
				if (!dynamicValue.isAtRest()) {
					needNewFrame = true;
				}
			}
			if (needNewFrame) {
				postDelayed(this, 20);
			}
			invalidate();
		}
	};

	public LineChart(Context context) {
		super(context);
		initAttributes();
		initPaints();
	}

	public LineChart(Context context, AttributeSet attrs) {
		super(context, attrs);
		initAttributes();
		initPaints();
	}

	public LineChart(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initAttributes();
		initPaints();
	}

	private void initAttributes() {
		mLineWidth = dp2px(getContext(), 3);
		mPointRadius = dp2px(getContext(), 8);
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
		mXMultiplier = mAvailableWidth / (maxXValue - minXValue);
		mYMultiplier = mAvailableHeight / (maxYValue - minYValue);
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
		for (LineSeries lineSeries : mData.series) {
			if (mInterpolationOn) {
				prepareSmoothPath(lineSeries);
			} else {
				preparePath(lineSeries);
			}
			mLinePaint.setColor(lineSeries.color);
			canvas.drawPath(mLinePath, mLinePaint);
			mLinePath.reset();
		}
	}

	private void drawPoints(Canvas canvas) {
		for (LineSeries lineSeries : mData.series) {
			mPointPaint.setColor(lineSeries.color);
			int valueIndex = 0;
			for (float valueX : mData.domain) {
				final float rawValueX = calculateX(valueX);
				final float rawValueY = calculateY(lineSeries.values.get(valueIndex).getPosition());
				canvas.drawCircle(rawValueX, rawValueY, mPointRadius, mPointPaint);
				++valueIndex;
			}
		}
	}

	private void preparePath(final LineSeries lineSeries) {
		int valueIndex = 0;
		for (float valueX : mData.domain) {
			final float rawValueX = calculateX(valueX);
			final float rawValueY = calculateY(lineSeries.values.get(valueIndex).getPosition());
			if (valueIndex == 0) {
				mLinePath.moveTo(rawValueX, rawValueY);
			} else {
				mLinePath.lineTo(rawValueX, rawValueY);
			}
			++valueIndex;
		}
	}

	private void prepareSmoothPath(final LineSeries lineSeries) {
		for (int pointIndex = 0; pointIndex < mData.domain.size() - 1; ++pointIndex) {
			final float currentPointX = calculateX(mData.domain.get(pointIndex));
			final float currentPointY = calculateY(lineSeries.values.get(pointIndex).getPosition());
			final float nextPointX = calculateX(mData.domain.get(pointIndex + 1));
			final float nextPointY = calculateY(lineSeries.values.get(pointIndex + 1).getPosition());
			final float previousPointX;
			final float previousPointY;
			if (pointIndex > 0) {
				previousPointX = calculateX(mData.domain.get(pointIndex - 1));
				previousPointY = calculateY(lineSeries.values.get(pointIndex - 1).getPosition());
			} else {
				previousPointX = currentPointX;
				previousPointY = currentPointY;
			}
			final float afterNextPointX;
			final float afterNextPointY;
			if (pointIndex < mData.domain.size() - 2) {
				afterNextPointX = calculateX(mData.domain.get(pointIndex + 2));
				afterNextPointY = calculateY(lineSeries.values.get(pointIndex + 2).getPosition());
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
		return getPaddingLeft() + mPointRadius + (valueX - minXValue) * mXMultiplier;
	}

	private float calculateY(float valueY) {
		return getHeight() - getPaddingBottom() - mPointRadius - (valueY - minYValue) * mYMultiplier;
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
		float rawMinX = calculateX(minXValue) - mPointRadius;
		float rawMinY = calculateY(minYValue);
		float rawMaxX = calculateX(maxXValue) + mPointRadius;
		float rawMaxY = calculateY(maxYValue);
		mLinePath.moveTo(rawMinX, rawMinY);
		mLinePath.lineTo(rawMaxX, rawMinY);
		canvas.drawPath(mLinePath, mRulersPaint);
		mLinePath.reset();
		mLinePath.moveTo(rawMinX, rawMaxY);
		mLinePath.lineTo(rawMaxX, rawMaxY);
		canvas.drawPath(mLinePath, mRulersPaint);
		mLinePath.reset();
		final float step = (maxYValue - minYValue) / mhorizontalRulersDivider;
		for (int i = 1; i < mhorizontalRulersDivider; ++i) {
			final float rawValueY = calculateY(minYValue + step * i);
			mLinePath.moveTo(rawMinX, rawValueY);
			mLinePath.lineTo(rawMaxX, rawValueY);
			canvas.drawPath(mLinePath, mRulersPaint);
			mLinePath.reset();
		}
	}

	/**
	 * Sets chart data.
	 * 
	 * @param data
	 */
	public void setData(final LineChartData data) {
		mData = data;
		calculateRanges();
		postInvalidate();
	}

	public void animateSeries(int index, List<Float> values) {
		int valueIndex = 0;
		long now = AnimationUtils.currentAnimationTimeMillis();
		for (DynamicValue value : mData.series.get(index).values) {
			value.setTargetPosition(values.get(valueIndex), now);
			++valueIndex;
		}
		removeCallbacks(animator);
		post(animator);
	}

	private void calculateRanges() {
		for (Float value : mData.domain) {
			if (value < minXValue) {
				minXValue = value;
			} else if (value > maxXValue) {
				maxXValue = value;
			}
		}
		for (LineSeries lineSeries : mData.series) {
			for (DynamicValue value : lineSeries.values) {
				if (value.getPosition() < minYValue) {
					minYValue = value.getPosition();
				} else if (value.getPosition() > maxYValue) {
					maxYValue = value.getPosition();
				}
			}
		}
	}

	private static int dp2px(Context context, int dp) {
		// Get the screen's density scale
		final float scale = context.getResources().getDisplayMetrics().density;
		// Convert the dps to pixels, based on density scale
		return (int) (dp * scale + 0.5f);

	}

}
