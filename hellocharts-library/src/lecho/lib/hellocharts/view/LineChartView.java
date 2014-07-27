package lecho.lib.hellocharts.view;

import lecho.lib.hellocharts.ChartCalculator;
import lecho.lib.hellocharts.LineChartDataProvider;
import lecho.lib.hellocharts.anim.ChartAnimationListener;
import lecho.lib.hellocharts.anim.ChartAnimator;
import lecho.lib.hellocharts.anim.ChartAnimatorV11;
import lecho.lib.hellocharts.anim.ChartAnimatorV8;
import lecho.lib.hellocharts.gesture.ChartTouchHandler;
import lecho.lib.hellocharts.model.ChartData;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.LinePoint;
import lecho.lib.hellocharts.model.SelectedValue;
import lecho.lib.hellocharts.renderer.AxesRenderer;
import lecho.lib.hellocharts.renderer.LineChartRenderer;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.os.Build;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

public class LineChartView extends AbstractChartView implements LineChartDataProvider {
	private static final String TAG = "LineChart";
	private LineChartData data;
	private ChartAnimator animator;
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
		chartCalculator = new ChartCalculator();
		axesRenderer = new AxesRenderer(context, this);
		chartRenderer = new LineChartRenderer(context, this, this);
		touchHandler = new ChartTouchHandler(context, this);
	}

	@SuppressLint("NewApi")
	private void initAttributes() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			setLayerType(LAYER_TYPE_SOFTWARE, null);
		}
	}

	private void initAnimatiors() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			animator = new ChartAnimatorV11(this);
		} else {
			animator = new ChartAnimatorV8(this);
		}
	}

	@Override
	protected void onSizeChanged(int width, int height, int oldWidth, int oldHeight) {
		super.onSizeChanged(width, height, oldWidth, oldHeight);
		chartCalculator.calculateContentArea(getWidth(), getHeight(), getPaddingLeft(), getPaddingTop(),
				getPaddingRight(), getPaddingBottom());
		chartRenderer.initRenderer();
		axesRenderer.initRenderer();
	}

	@Override
	protected void onDraw(Canvas canvas) {
		long time = System.nanoTime();
		super.onDraw(canvas);
		axesRenderer.draw(canvas);
		int clipRestoreCount = canvas.save();
		canvas.clipRect(chartCalculator.mContentRect);
		chartRenderer.draw(canvas);
		canvas.restoreToCount(clipRestoreCount);
		chartRenderer.drawUnclipped(canvas);
		Log.v(TAG, "onDraw [ms]: " + (System.nanoTime() - time) / 1000000.0);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		super.onTouchEvent(event);
		if (touchHandler.handleTouchEvent(event)) {
			ViewCompat.postInvalidateOnAnimation(this);
		}
		return true;
	}

	@Override
	public void computeScroll() {
		super.computeScroll();
		if (touchHandler.computeScroll()) {
			ViewCompat.postInvalidateOnAnimation(this);
		}
	}

	public void setLineChartData(LineChartData data) {
		this.data = data;
		chartCalculator.calculateContentArea(getWidth(), getHeight(), getPaddingLeft(), getPaddingTop(),
				getPaddingRight(), getPaddingBottom());
		chartRenderer.initRenderer();
		axesRenderer.initRenderer();

		ViewCompat.postInvalidateOnAnimation(LineChartView.this);
	}

	@Override
	public LineChartData getLineChartData() {
		return data;
	}

	@Override
	public ChartData getChartData() {
		return data;
	}

	@Override
	public void callTouchListener(SelectedValue selectedValue) {
		LinePoint point = data.lines.get(selectedValue.firstIndex).getPoints().get(selectedValue.secondIndex);
		onValueTouchListener.onValueTouched(selectedValue.firstIndex, selectedValue.secondIndex, point);
	}

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

	@Override
	public void animationDataUpdate(float scale) {
		for (Line line : data.lines) {
			for (LinePoint point : line.getPoints()) {
				point.update(scale);
			}
		}
		chartRenderer.fastInitRenderer();
		ViewCompat.postInvalidateOnAnimation(this);
	}

	@Override
	public void startDataAnimation() {
		animator.startAnimation();
	}

	@Override
	public void setChartAnimationListener(ChartAnimationListener animationListener) {
		animator.setChartAnimationListener(animationListener);
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
