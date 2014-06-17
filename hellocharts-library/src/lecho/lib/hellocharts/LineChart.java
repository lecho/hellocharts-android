package lecho.lib.hellocharts;

import java.util.List;

import lecho.lib.hellocharts.anim.ChartAnimator;
import lecho.lib.hellocharts.anim.ChartAnimatorV11;
import lecho.lib.hellocharts.anim.ChartAnimatorV8;
import lecho.lib.hellocharts.gestures.ChartTouchHandler;
import lecho.lib.hellocharts.model.AnimatedPoint;
import lecho.lib.hellocharts.model.LineChartData;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.os.Build;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

public class LineChart extends AbstractChart {
	private static final String TAG = "LineChart";
	private LineChartData mData;
	private boolean mAxesOn = true;
	private ChartAnimator mAnimator;

	public LineChart(Context context) {
		this(context, null, 0);
	}

	public LineChart(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public LineChart(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initAttributes();
		initAnimatiors();
		mChartCalculator = new ChartCalculator(context, this);
		mAxesRenderer = new AxesRenderer(context, this);
		mChartRenderer = new LineChartRenderer(context, this);
		mTouchHandler = new ChartTouchHandler(context, this);
	}

	@SuppressLint("NewApi")
	private void initAttributes() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			setLayerType(LAYER_TYPE_SOFTWARE, null);
		}
	}

	private void initAnimatiors() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			mAnimator = new ChartAnimatorV11(this);
		} else {
			mAnimator = new ChartAnimatorV8(this);
		}
	}

	@Override
	protected void onSizeChanged(int width, int height, int oldWidth, int oldHeight) {
		super.onSizeChanged(width, height, oldWidth, oldHeight);
		// TODO mPointRadus can change, recalculate in setter
		mChartCalculator.calculateContentArea(this);
		mChartCalculator.calculateViewport();
	}

	// Automatically calculates Y axis values.
	// private Axis calculateYAxis(int numberOfSteps) {
	// if (numberOfSteps < 2) {
	// throw new IllegalArgumentException("Number or steps have to be grater or equal 2");
	// }
	// List<Float> values = new ArrayList<Float>();
	// final float range = mData.getMaxYValue() - mData.getMinYValue();
	// final float tickRange = range / (numberOfSteps - 1);
	// final float x = (float) Math.ceil(Math.log10(tickRange) - 1);
	// final float pow10x = (float) Math.pow(10, x);
	// final float roundedTickRange = (float) Math.ceil(tickRange / pow10x) * pow10x;
	// float value = mData.getMinYValue();
	// while (value <= mData.getMaxYValue()) {
	// values.add(value);
	// value += roundedTickRange;
	// }
	// Axis yAxis = new Axis();
	// yAxis.setValues(values);
	// return yAxis;
	// }

	@Override
	protected void onDraw(Canvas canvas) {
		long time = System.nanoTime();
		super.onDraw(canvas);
		if (mAxesOn) {
			mAxesRenderer.drawAxisX(canvas);
			mAxesRenderer.drawAxisY(canvas);
		}
		int clipRestoreCount = canvas.save();
		mChartCalculator.calculateClippingArea();// only if zoom is enabled
		canvas.clipRect(mChartCalculator.mClippingRect);
		// TODO: draw lines
		mChartRenderer.draw(canvas);
		canvas.restoreToCount(clipRestoreCount);
		Log.v(TAG, "onDraw [ms]: " + (System.nanoTime() - time) / 1000000f);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		super.onTouchEvent(event);
		if (mTouchHandler.handleTouchEvent(event)) {
			ViewCompat.postInvalidateOnAnimation(this);
		}
		return true;
	}

	@Override
	public void computeScroll() {
		super.computeScroll();
		if (mTouchHandler.computeScroll()) {
			ViewCompat.postInvalidateOnAnimation(this);
		}
	}

	public void setData(final LineChartData data) {
		mData = data;
		mData.calculateBoundaries();
		mChartCalculator.calculateAxesMargins(getContext());
		mChartCalculator.calculateViewport();
		ViewCompat.postInvalidateOnAnimation(LineChart.this);
	}

	public LineChartData getData() {
		return mData;
	}

	public void animationUpdate(float scale) {
		for (AnimatedPoint animatedPoint : mData.lines.get(0).animatedPoints) {
			animatedPoint.update(scale);
		}
		mData.calculateBoundaries();
		mChartCalculator.calculateViewport();
		ViewCompat.postInvalidateOnAnimation(LineChart.this);
	}

	public void animateSeries(int index, List<lecho.lib.hellocharts.model.Point> points) {
		mAnimator.cancelAnimation();
		mData.updateLineTarget(index, points);
		mAnimator.startAnimation();
	}

	public void updateSeries(int index, List<lecho.lib.hellocharts.model.Point> points) {
		mData.updateLine(index, points);
		ViewCompat.postInvalidateOnAnimation(LineChart.this);
	}

	public void setOnPointClickListener(OnPointClickListener listener) {
		// if (null == listener) {
		// mOnPointClickListener = new DummyOnPointListener();
		// } else {
		// mOnPointClickListener = listener;
		// }s
	}

}
