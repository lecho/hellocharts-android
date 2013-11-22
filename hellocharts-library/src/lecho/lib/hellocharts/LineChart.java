package lecho.lib.hellocharts;

import java.util.ArrayList;
import java.util.List;

import lecho.lib.hellocharts.utils.SplineInterpolator;
import lehco.lib.hellocharts.model.LineChartData;
import lehco.lib.hellocharts.model.LineSeries;
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

/**
 * TODO nullcheck for mData
 * 
 * @author lecho
 * 
 */
public class LineChart extends View {
	protected LineChartData mData;
	protected List<Float> mGeneratedX;
	protected List<SplineInterpolator> mSplineInterpolators;
	protected Bitmap mBitmap;
	protected Canvas mCanvas;
	protected Paint mLinePaint = new Paint();
	protected Path mLinePath = new Path();
	protected Paint mPointPaint = new Paint();
	protected float mLineWidth = 4.0f;
	protected float mPointRadius = 12.0f;
	protected float minXValue = Float.MAX_VALUE;
	protected float maxXValue = Float.MIN_VALUE;
	protected float minYValue = Float.MAX_VALUE;
	protected float maxYValue = Float.MIN_VALUE;
	protected float mXMultiplier;
	protected float mYMultiplier;

	public LineChart(Context context) {
		super(context);
		initPaint();
	}

	public LineChart(Context context, AttributeSet attrs) {
		super(context, attrs);
		initPaint();
	}

	public LineChart(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initPaint();
	}

	private void initPaint() {
		mLinePaint.setAntiAlias(true);
		mLinePaint.setColor(Color.BLACK);
		mLinePaint.setStyle(Paint.Style.STROKE);
		mLinePaint.setStrokeWidth(mLineWidth);

		mPointPaint.setAntiAlias(true);
		mPointPaint.setColor(Color.BLACK);
		mPointPaint.setStyle(Paint.Style.FILL);
	}

	@Override
	protected void onSizeChanged(int width, int height, int oldWidth, int oldHeight) {
		super.onSizeChanged(width, height, oldWidth, oldHeight);
		mBitmap = Bitmap.createBitmap(getWidth(), getHeight(), Config.ARGB_8888);
		mCanvas = new Canvas(mBitmap);

		float availableWidth = getWidth() - getPaddingLeft() - getPaddingRight();
		float availableHeight = getHeight() - getPaddingTop() - getPaddingBottom();
		mXMultiplier = availableWidth / (maxXValue - minXValue);
		mYMultiplier = availableHeight / (maxYValue - minYValue);
		generateXForInterpolation();
	}

	@Override
	protected void onDraw(Canvas canvas) {
		long time = System.nanoTime();
		int seriesIndex = 0;
		for (LineSeries lineSeries : mData.series) {
			mLinePaint.setColor(lineSeries.color);
			int valueIndex = 0;
			for (float valueX : mGeneratedX) {
				float rawValueX = calculateX(valueX);
				float rawValueY = calculateY(mSplineInterpolators.get(seriesIndex).interpolate(valueX));
				if (valueIndex == 0) {
					mLinePath.moveTo(rawValueX, rawValueY);
				} else {
					mLinePath.lineTo(rawValueX, rawValueY);
				}
				++valueIndex;
			}
			mCanvas.drawPath(mLinePath, mLinePaint);
			mLinePath.reset();
			++seriesIndex;
		}
		// TODO check if point drawing on
		for (LineSeries lineSeries : mData.series) {
			int valueIndex = 0;
			for (Float valueX : mData.domain) {
				float rawValueX = calculateX(valueX);
				float rawValueY = calculateY(lineSeries.values.get(valueIndex));
				mPointPaint.setColor(lineSeries.color);
				mCanvas.drawCircle(rawValueX, rawValueY, mPointRadius, mPointPaint);
				++valueIndex;
			}
		}
		Log.v("TAG", "Narysowane w [ms]: " + (System.nanoTime() - time) / 1000000);
		canvas.drawBitmap(mBitmap, 0, 0, null);
		Log.v("TAG", "Wyświetlone w [ms]: " + (System.nanoTime() - time) / 1000000);
	}

	private float calculateX(float valueX) {
		return getPaddingLeft() + (valueX - minXValue) * mXMultiplier;
	}

	private float calculateY(float valueY) {
		return getHeight() - getPaddingBottom() - (valueY - minYValue) * mYMultiplier;
	}

	/**
	 * Generates additional X values for interpolation. Should be called after any view size changes.
	 */
	private void generateXForInterpolation() {
		// TODO check null mData and domain.size()>2
		final int size = mData.domain.size();
		final float density = getResources().getDisplayMetrics().density;
		final float range = mData.domain.get(size - 1) - mData.domain.get(0);
		final float step = range / mXMultiplier * density;
		mGeneratedX = new ArrayList<Float>();
		int i = 0;
		for (float value : mData.domain) {
			mGeneratedX.add(value);
			if (i < mData.domain.size() - 1) {
				for (float f = value + step; f < mData.domain.get(i + 1) - step; f += step) {
					mGeneratedX.add(f);
				}
			}
			++i;
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
		// TODO check if interpolation on and series number
		generateSplineInterpolators(data);
		postInvalidate();
	}

	private void generateSplineInterpolators(final LineChartData data) {
		mSplineInterpolators = new ArrayList<SplineInterpolator>();
		for (LineSeries lineSeries : data.series) {
			mSplineInterpolators.add(SplineInterpolator.createMonotoneCubicSpline(data.domain, lineSeries.values));
		}
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
			for (Float value : lineSeries.values) {
				if (value < minYValue) {
					minYValue = value;
				} else if (value > maxYValue) {
					maxYValue = value;
				}
			}
		}
	}

}
