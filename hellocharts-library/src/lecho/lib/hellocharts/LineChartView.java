package lecho.lib.hellocharts;

import lecho.lib.hellocharts.anim.ChartAnimator;
import lecho.lib.hellocharts.anim.ChartAnimatorV11;
import lecho.lib.hellocharts.anim.ChartAnimatorV8;
import lecho.lib.hellocharts.gestures.DefaultTouchHandler;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.LinePoint;
import lecho.lib.hellocharts.model.SelectedValue;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.os.Build;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

public class LineChartView extends AbstractChart {
	private static final String TAG = "LineChart";
	private LineChartData mData;
	private ChartAnimator mAnimator;
	private LineChartOnValueTouchListener onValueTouchListener = new DummyOnValueTouchListener();

	public LineChartView(Context context) {
		this(context, null, 0);
	}

	public LineChartView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public LineChartView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initAttributes();
		initAnimatiors();
		mChartCalculator = new ChartCalculator(context, this);
		mAxesRenderer = new AxesRenderer(context, this);
		mChartRenderer = new LineChartRenderer(context, this);
		mTouchHandler = new DefaultTouchHandler(context, this);
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
		mChartCalculator.calculateAxesMargins(getContext());
		mChartCalculator.calculateViewport();
		mChartCalculator.calculateContentArea(getWidth(), getHeight(), getPaddingLeft(), getPaddingTop(),
				getPaddingRight(), getPaddingBottom());
	}

	// Automatically calculates Y axis values.
	// private Axis calculateYAxis(int numberOfSteps) {
	// if (numberOfSteps < 2) {
	// throw new
	// IllegalArgumentException("Number or steps have to be grater or equal 2");
	// }
	// List<Float> values = new ArrayList<Float>();
	// final float range = mData.getMaxYValue() - mData.getMinYValue();
	// final float tickRange = range / (numberOfSteps - 1);
	// final float x = (float) Math.ceil(Math.log10(tickRange) - 1);
	// final float pow10x = (float) Math.pow(10, x);
	// final float roundedTickRange = (float) Math.ceil(tickRange / pow10x) *
	// pow10x;
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
		mAxesRenderer.drawAxisX(canvas);
		mAxesRenderer.drawAxisY(canvas);
		mChartRenderer.drawUnclipped(canvas);
		int clipRestoreCount = canvas.save();
		canvas.clipRect(mChartCalculator.mContentRect);
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
		mChartCalculator.setCommonMargin(mData.getPointAdditionalMargin());
		mChartCalculator.calculateAxesMargins(getContext());
		mChartCalculator.calculateViewport();
		mChartCalculator.calculateContentArea(getWidth(), getHeight(), getPaddingLeft(), getPaddingTop(),
				getPaddingRight(), getPaddingBottom());
		ViewCompat.postInvalidateOnAnimation(LineChartView.this);
	}

	public LineChartData getData() {
		return mData;
	}

	public void animationUpdate(float scale) {
		for (LinePoint animatedPoint : mData.lines.get(0).getPoints()) {
			animatedPoint.update(scale);
		}
		mData.calculateBoundaries();
		mChartCalculator.calculateViewport();
		ViewCompat.postInvalidateOnAnimation(LineChartView.this);
	}

	@Override
	public void callTouchListener(SelectedValue selectedValue) {
		LinePoint point = mData.lines.get(selectedValue.firstIndex).getPoints().get(selectedValue.secondIndex);
		onValueTouchListener.onValueTouched(selectedValue.firstIndex, selectedValue.secondIndex, point);
	}

	// public void animateSeries(int index,
	// List<lecho.lib.hellocharts.model.Point> points) {
	// mAnimator.cancelAnimation();
	// mData.updateLineTarget(index, points);
	// mAnimator.startAnimation();
	// }
	//
	// public void updateSeries(int index,
	// List<lecho.lib.hellocharts.model.Point> points) {
	// mData.updateLine(index, points);
	// ViewCompat.postInvalidateOnAnimation(LineChart.this);
	// }

	public LineChartOnValueTouchListener getOnValueTouchListener() {
		return onValueTouchListener;
	}

	public void setOnValueTouchListener(LineChartOnValueTouchListener touchListener) {
		if (null == touchListener) {
			this.onValueTouchListener = new DummyOnValueTouchListener();
		} else {
			this.onValueTouchListener = touchListener;
		}
	}

	public interface LineChartOnValueTouchListener {
		public void onValueTouched(int selectedLine, int selectedValue, LinePoint point);
	}

	private static class DummyOnValueTouchListener implements LineChartOnValueTouchListener {

		@Override
		public void onValueTouched(int selectedLine, int selectedValue, LinePoint point) {
			// do nothing
		}
	}
}
