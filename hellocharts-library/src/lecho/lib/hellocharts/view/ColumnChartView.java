package lecho.lib.hellocharts.view;

import lecho.lib.hellocharts.ChartCalculator;
import lecho.lib.hellocharts.ColumnChartDataProvider;
import lecho.lib.hellocharts.anim.ChartAnimationListener;
import lecho.lib.hellocharts.anim.ChartAnimator;
import lecho.lib.hellocharts.anim.ChartAnimatorV11;
import lecho.lib.hellocharts.anim.ChartAnimatorV8;
import lecho.lib.hellocharts.gesture.DefaultTouchHandler;
import lecho.lib.hellocharts.model.Column;
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

public class ColumnChartView extends AbstractChartView implements ColumnChartDataProvider {
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
		mChartCalculator = new ChartCalculator();
		mChartRenderer = new ColumnChartRenderer(context, this, this);
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
		mChartCalculator.calculateContentArea(getWidth(), getHeight(), getPaddingLeft(), getPaddingTop(),
				getPaddingRight(), getPaddingBottom());
		mChartRenderer.initRenderer();
		mAxesRenderer.initRenderer();
	}

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

	@Override
	public ColumnChartData getColumnChartData() {
		return mData;
	}

	@Override
	public void setColumnChartData(ColumnChartData data) {
		mData = data;
		mChartCalculator.calculateContentArea(getWidth(), getHeight(), getPaddingLeft(), getPaddingTop(),
				getPaddingRight(), getPaddingBottom());
		mChartRenderer.initRenderer();
		mAxesRenderer.initRenderer();

		ViewCompat.postInvalidateOnAnimation(ColumnChartView.this);

	}

	@Override
	public ColumnChartData getChartData() {
		return mData;
	}

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

	@Override
	public void animationDataUpdate(float scale) {
		for (Column column : mData.getColumns()) {
			for (ColumnValue value : column.getValues()) {
				value.update(scale);
			}
		}
		mChartRenderer.fastInitRenderer();
		ViewCompat.postInvalidateOnAnimation(this);
	}

	@Override
	public void startDataAnimation() {
		mAnimator.startAnimation();
	}

	@Override
	public void setChartAnimationListener(ChartAnimationListener animationListener) {
		mAnimator.setChartAnimationListener(animationListener);
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
