package lecho.lib.hellocharts.view;

import java.util.ArrayList;
import java.util.List;

import lecho.lib.hellocharts.ColumnChartDataProvider;
import lecho.lib.hellocharts.model.Column;
import lecho.lib.hellocharts.model.ColumnChartData;
import lecho.lib.hellocharts.model.ColumnValue;
import lecho.lib.hellocharts.model.SelectedValue;
import lecho.lib.hellocharts.renderer.ColumnChartRenderer;
import android.content.Context;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;

public class ColumnChartView extends AbstractChartView implements ColumnChartDataProvider {
	private static final String TAG = "ColumnChartView";
	private ColumnChartData data;
	private ColumnChartOnValueTouchListener onValueTouchListener = new DummyOnValueTouchListener();

	public ColumnChartView(Context context) {
		this(context, null, 0);
	}

	public ColumnChartView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public ColumnChartView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		chartRenderer = new ColumnChartRenderer(context, this, this);
		setColumnChartData(generateDummyData());
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
		chartRenderer.initMaxViewport();
		chartRenderer.initCurrentViewport();
		chartRenderer.initDataAttributes();
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
		chartRenderer.initMaxViewport();
		chartRenderer.initCurrentViewport();
		ViewCompat.postInvalidateOnAnimation(this);
	}

	@Override
	public void animationDataFinished(boolean isFinishedSuccess) {
		for (Column column : data.getColumns()) {
			for (ColumnValue value : column.getValues()) {
				value.finish(isFinishedSuccess);
			}
		}
		chartRenderer.initMaxViewport();
		chartRenderer.initCurrentViewport();
		ViewCompat.postInvalidateOnAnimation(this);

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
