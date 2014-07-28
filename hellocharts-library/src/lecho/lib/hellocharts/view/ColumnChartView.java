package lecho.lib.hellocharts.view;

import java.util.ArrayList;
import java.util.List;

import lecho.lib.hellocharts.ChartCalculator;
import lecho.lib.hellocharts.ColumnChartDataProvider;
import lecho.lib.hellocharts.anim.ChartAnimationListener;
import lecho.lib.hellocharts.anim.ChartAnimator;
import lecho.lib.hellocharts.anim.ChartAnimatorV11;
import lecho.lib.hellocharts.anim.ChartAnimatorV8;
import lecho.lib.hellocharts.gesture.ChartTouchHandler;
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
	private ColumnChartData data;
	private ChartAnimator animator;
	private ColumnChartOnValueTouchListener onValueTouchListener = new DummyOnValueTouchListener();

	public ColumnChartView(Context context) {
		this(context, null, 0);
	}

	public ColumnChartView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public ColumnChartView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initAnimatiors();
		chartCalculator = new ChartCalculator();
		chartRenderer = new ColumnChartRenderer(context, this, this);
		axesRenderer = new AxesRenderer(context, this);
		touchHandler = new ChartTouchHandler(context, this);
		setColumnChartData(generateDummyData());
	}

	private void initAnimatiors() {
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
			animator = new ChartAnimatorV8(this);
		} else {
			animator = new ChartAnimatorV11(this);
		}
	}

	@Override
	protected void onSizeChanged(int width, int height, int oldWidth, int oldHeight) {
		super.onSizeChanged(width, height, oldWidth, oldHeight);
		// TODO mPointRadus can change, recalculate in setter
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
		canvas.clipRect(chartCalculator.getContentRect());
		chartRenderer.draw(canvas);
		canvas.restoreToCount(clipRestoreCount);
		chartRenderer.drawUnclipped(canvas);
		Log.v(TAG, "onDraw [ms]: " + (System.nanoTime() - time) / 1000000f);
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

	@Override
	public ColumnChartData getColumnChartData() {
		return data;
	}

	@Override
	public void setColumnChartData(ColumnChartData data) {
		if (null == data) {
			this.data = generateDummyData();
		} else {
			this.data = data;
		}
		chartCalculator.calculateContentArea(getWidth(), getHeight(), getPaddingLeft(), getPaddingTop(),
				getPaddingRight(), getPaddingBottom());
		chartRenderer.initRenderer();
		axesRenderer.initRenderer();

		ViewCompat.postInvalidateOnAnimation(ColumnChartView.this);

	}

	@Override
	public ColumnChartData getChartData() {
		return data;
	}

	@Override
	public void callTouchListener(SelectedValue selectedValue) {
		ColumnValue value = data.getColumns().get(selectedValue.firstIndex).getValues().get(selectedValue.secondIndex);
		onValueTouchListener.onValueTouched(selectedValue.firstIndex, selectedValue.secondIndex, value);

	}

	public ColumnChartOnValueTouchListener getOnValueTouchListener() {
		return onValueTouchListener;
	}

	public void setOnValueTouchListener(ColumnChartOnValueTouchListener touchListener) {
		if (null == touchListener) {
			this.onValueTouchListener = new DummyOnValueTouchListener();
		} else {
			this.onValueTouchListener = touchListener;
		}
	}

	@Override
	public void animationDataUpdate(float scale) {
		for (Column column : data.getColumns()) {
			for (ColumnValue value : column.getValues()) {
				value.update(scale);
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

	private ColumnChartData generateDummyData() {
		final int numValues = 4;
		ColumnChartData data = new ColumnChartData();
		List<ColumnValue> values = new ArrayList<ColumnValue>(numValues);
		for (int i = 1; i <= numValues; ++i) {
			values.add(new ColumnValue(i));
		}
		Column column = new Column(values);
		List<Column> columns = new ArrayList<Column>(1);
		columns.add(column);
		data.setColumns(columns);
		return data;
	}

	public interface ColumnChartOnValueTouchListener {
		public void onValueTouched(int selectedLine, int selectedValue, ColumnValue point);
	}

	private static class DummyOnValueTouchListener implements ColumnChartOnValueTouchListener {

		@Override
		public void onValueTouched(int selectedLine, int selectedValue, ColumnValue point) {
			// do nothing
		}
	}

}
