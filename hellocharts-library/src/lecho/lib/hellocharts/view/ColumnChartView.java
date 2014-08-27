package lecho.lib.hellocharts.view;

import lecho.lib.hellocharts.BuildConfig;
import lecho.lib.hellocharts.ColumnChartDataProvider;
import lecho.lib.hellocharts.model.Column;
import lecho.lib.hellocharts.model.ColumnChartData;
import lecho.lib.hellocharts.model.ColumnValue;
import lecho.lib.hellocharts.model.SelectedValue;
import lecho.lib.hellocharts.renderer.ColumnChartRenderer;
import android.content.Context;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.util.Log;

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
		setColumnChartData(ColumnChartData.generateDummyData());
	}

	@Override
	public ColumnChartData getColumnChartData() {
		return data;
	}

	@Override
	public void setColumnChartData(ColumnChartData data) {
		if (BuildConfig.DEBUG) {
			Log.d(TAG, "Setting data for ColumnChartView");
		}

		if (null == data) {
			this.data = ColumnChartData.generateDummyData();
		} else {
			this.data = data;
		}
		axesRenderer.initAxesMeasurements();
		chartRenderer.initMaxViewport();
		chartRenderer.initCurrentViewport();
		chartRenderer.initDataMeasuremetns();

		ViewCompat.postInvalidateOnAnimation(ColumnChartView.this);

	}

	@Override
	public ColumnChartData getChartData() {
		return data;
	}

	@Override
	public void callChartTouchListener(SelectedValue selectedValue) {
		ColumnValue value = data.getColumns().get(selectedValue.getFirstIndex()).getValues()
				.get(selectedValue.getSecondIndex());
		onValueTouchListener.onValueTouched(selectedValue.getFirstIndex(), selectedValue.getSecondIndex(), value);

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

	public interface ColumnChartOnValueTouchListener {
		public void onValueTouched(int selectedLine, int selectedValue, ColumnValue point);
	}

	private static class DummyOnValueTouchListener implements ColumnChartOnValueTouchListener {

		@Override
		public void onValueTouched(int selectedLine, int selectedValue, ColumnValue value) {
			// do nothing
		}
	}

}
