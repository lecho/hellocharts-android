package lecho.lib.hellocharts.view;

import lecho.lib.hellocharts.ChartCalculator;
import lecho.lib.hellocharts.anim.ChartAnimator;
import lecho.lib.hellocharts.anim.ChartAnimatorV11;
import lecho.lib.hellocharts.anim.ChartAnimatorV8;
import lecho.lib.hellocharts.gesture.DefaultTouchHandler;
import lecho.lib.hellocharts.model.ColumnChartData;
import lecho.lib.hellocharts.model.ColumnValue;
import lecho.lib.hellocharts.model.SelectedValue;
import lecho.lib.hellocharts.renderer.AxesRenderer;
import lecho.lib.hellocharts.renderer.ColumnChartRenderer;
import android.content.Context;
import android.graphics.Canvas;
import android.os.Build;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

public class ColumnChartView extends AbstractChartView {
	private static final String TAG = "BarChart";
	private ColumnChartData mData;
	private ChartAnimator mAnimator;
	private BarChartOnValueTouchListener onValueTouchListener = new DummyOnValueTouchListener();

	public ColumnChartView(Context context) {
		this(context, null, 0);
	}

	public ColumnChartView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public ColumnChartView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initAnimatiors();
		mChartRenderer = new ColumnChartRenderer(context, this);
		mChartCalculator = new ChartCalculator(context, this);
		mAxesRenderer = new AxesRenderer(context, this);
		mTouchHandler = new DefaultTouchHandler(context, this);
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
		mChartCalculator.calculateViewport();
		mChartCalculator.calculateContentArea(getWidth(), getHeight(), getPaddingLeft(), getPaddingTop(),
				getPaddingRight(), getPaddingBottom());
		mChartCalculator.setAxesMargin(mAxesRenderer.getAxisXHeight(), mAxesRenderer.getAxisYWidth());
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
		mAxesRenderer.draw(canvas);
		int clipRestoreCount = canvas.save();
		canvas.clipRect(mChartCalculator.mContentRect);
		mChartRenderer.draw(canvas);
		canvas.restoreToCount(clipRestoreCount);
		mChartRenderer.drawUnclipped(canvas);
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

	public void setData(final ColumnChartData data) {
		mData = data;
		mData.calculateBoundaries();
		mChartCalculator.calculateViewport();
		mChartCalculator.calculateContentArea(getWidth(), getHeight(), getPaddingLeft(), getPaddingTop(),
				getPaddingRight(), getPaddingBottom());
		mAxesRenderer.initRenderer();
		mChartCalculator.setAxesMargin(mAxesRenderer.getAxisXHeight(), mAxesRenderer.getAxisYWidth());

		mChartRenderer.setTextColor(mData.getLabelsTextColor());
		mChartRenderer.setTextSize(mData.getLabelsTextSize());
		ViewCompat.postInvalidateOnAnimation(ColumnChartView.this);
	}

	public ColumnChartData getData() {
		return mData;
	}

	public void animationUpdate(float scale) {
		// for (AnimatedPoint animatedPoint : mData.lines.get(0).animatedPoints)
		// {
		// animatedPoint.update(scale);
		// }
		// mData.calculateBoundaries();
		// mChartCalculator.calculateViewport();
		// ViewCompat.postInvalidateOnAnimation(BarChart.this);
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
	// ViewCompat.postInvalidateOnAnimation(BarChart.this);
	// }

	@Override
	public void callTouchListener(SelectedValue selectedValue) {
		ColumnValue value = mData.getColumns().get(selectedValue.firstIndex).getValues().get(selectedValue.secondIndex);
		onValueTouchListener.onValueTouched(selectedValue.firstIndex, selectedValue.secondIndex, value);

	}

	public BarChartOnValueTouchListener getOnValueTouchListener() {
		return onValueTouchListener;
	}

	public void setOnValueTouchListener(BarChartOnValueTouchListener touchListener) {
		if (null == touchListener) {
			this.onValueTouchListener = new DummyOnValueTouchListener();
		} else {
			this.onValueTouchListener = touchListener;
		}
	}

	public interface BarChartOnValueTouchListener {
		public void onValueTouched(int selectedLine, int selectedValue, ColumnValue point);
	}

	private static class DummyOnValueTouchListener implements BarChartOnValueTouchListener {

		@Override
		public void onValueTouched(int selectedLine, int selectedValue, ColumnValue point) {
			// do nothing
		}
	}

}
