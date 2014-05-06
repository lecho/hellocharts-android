package lecho.lib.hellocharts;

import java.util.List;

import lecho.lib.hellocharts.anim.ChartAnimator;
import lecho.lib.hellocharts.anim.ChartAnimatorV11;
import lecho.lib.hellocharts.anim.ChartAnimatorV8;
import lecho.lib.hellocharts.gestures.ChartTouchHandler;
import lecho.lib.hellocharts.model.AnimatedPoint;
import lecho.lib.hellocharts.model.Data;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Cap;
import android.os.Build;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

public class LineChart extends View {
	private static final String TAG = "LineChart";
	private ChartCalculator mChartCalculator;
	private AxesRenderer mAxisRenderer;
	private LineChartRenderer mLineChartRenderer;
	private ChartTouchHandler mTouchHandler;
	private Paint mLinePaint = new Paint();
	private Paint mTextPaint = new Paint();
	private Data mData;
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
		initPaints();
		initAnimatiors();
		mChartCalculator = new ChartCalculator(context);
		mAxisRenderer = new AxesRenderer();
		mLineChartRenderer = new LineChartRenderer(context, this);
		mTouchHandler = new ChartTouchHandler(context, this);
	}

	@SuppressLint("NewApi")
	private void initAttributes() {
		setLayerType(LAYER_TYPE_SOFTWARE, null);
	}

	private void initPaints() {
		mLinePaint.setAntiAlias(true);
		mLinePaint.setStyle(Paint.Style.STROKE);
		mLinePaint.setStrokeCap(Cap.ROUND);

		mTextPaint.setAntiAlias(true);
		mTextPaint.setStyle(Paint.Style.FILL);
		mTextPaint.setStrokeWidth(1);
	}

	private void initAnimatiors() {
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
			mAnimator = new ChartAnimatorV8(this);
		} else {
			mAnimator = new ChartAnimatorV11(this);
		}
	}

	@Override
	protected void onSizeChanged(int width, int height, int oldWidth, int oldHeight) {
		super.onSizeChanged(width, height, oldWidth, oldHeight);
		// TODO mPointRadus can change, recalculate in setter
		mChartCalculator.calculateContentArea(this);
		mChartCalculator.calculateViewport(mData);
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
			mAxisRenderer.drawAxisX(getContext(), canvas, mData.axisX, getChartCalculator());
			mAxisRenderer.drawAxisY(getContext(), canvas, mData.axisY, getChartCalculator());
		}
		int clipRestoreCount = canvas.save();
		mChartCalculator.calculateClippingArea();// only if zoom is enabled
		canvas.clipRect(mChartCalculator.mClippingRect);
		// TODO: draw lines
		mLineChartRenderer.drawLines(canvas);
		canvas.restoreToCount(clipRestoreCount);
		Log.v(TAG, "onDraw [ms]: " + (System.nanoTime() - time) / 1000000f);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		super.onTouchEvent(event);
		if (mTouchHandler.handleTouchEvent(event, mData, mChartCalculator)) {
			ViewCompat.postInvalidateOnAnimation(this);
		}
		return true;
	}

	@Override
	public void computeScroll() {
		super.computeScroll();
		if (mTouchHandler.computeScroll(this, mChartCalculator)) {
			ViewCompat.postInvalidateOnAnimation(this);
		}
	}

	public void setData(final Data data) {
		mData = data;
		mData.calculateRanges();
		mChartCalculator.calculateAxesMargins(getContext(), mAxisRenderer, mData);
		mChartCalculator.calculateViewport(mData);
		ViewCompat.postInvalidateOnAnimation(LineChart.this);
	}

	public Data getData() {
		return mData;
	}

	public void animationUpdate(float scale) {
		for (AnimatedPoint animatedPoint : mData.lines.get(0).animatedPoints) {
			animatedPoint.update(scale);
		}
		mData.calculateRanges();
		mChartCalculator.calculateViewport(mData);
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

	public LineChartRenderer getLineChartRenderer() {
		return mLineChartRenderer;
	}

	public void setLineChartRenderer(LineChartRenderer lineChartRenderer) {
		this.mLineChartRenderer = lineChartRenderer;
	}

	public ChartTouchHandler getTouchHandler() {
		return mTouchHandler;
	}

	public void setTouchHandler(ChartTouchHandler touchHandler) {
		this.mTouchHandler = touchHandler;
	}

	public ChartCalculator getChartCalculator() {
		return mChartCalculator;
	}

	public void setChartCalculator(ChartCalculator chartCalculator) {
		this.mChartCalculator = chartCalculator;
	}

}
