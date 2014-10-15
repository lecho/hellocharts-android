package lecho.lib.hellocharts.view;

import lecho.lib.hellocharts.BuildConfig;
import lecho.lib.hellocharts.model.ChartData;
import lecho.lib.hellocharts.model.ColumnChartData;
import lecho.lib.hellocharts.model.ColumnValue;
import lecho.lib.hellocharts.model.ComboLineColumnChartData;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.SelectedValue;
import lecho.lib.hellocharts.provider.ColumnChartDataProvider;
import lecho.lib.hellocharts.provider.ComboLineColumnChartDataProvider;
import lecho.lib.hellocharts.provider.LineChartDataProvider;
import lecho.lib.hellocharts.renderer.ComboLineColumnChartRenderer;
import android.content.Context;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.util.Log;

/**
 * ComboChart, supports ColumnChart combined with LineChart. Lines are always drawn on top.
 * 
 * @author Leszek Wach
 * 
 */
public class ComboLineColumnChartView extends AbstractChartView implements ComboLineColumnChartDataProvider {
	private static final String TAG = "ComboLineColumnChartView";
	protected ComboLineColumnChartData data;
	protected ColumnChartDataProvider columnChartDataProvider = new ComboColumnChartDataProvider();
	protected LineChartDataProvider lineChartDataProvider = new ComboLineChartDataProvider();
	protected ComboLineColumnChartOnValueTouchListener onValueTouchListener = new DummyOnValueTouchListener();

	public ComboLineColumnChartView(Context context) {
		this(context, null, 0);
	}

	public ComboLineColumnChartView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public ComboLineColumnChartView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		chartRenderer = new ComboLineColumnChartRenderer(context, this, columnChartDataProvider, lineChartDataProvider);
		setComboLineColumnChartData(ComboLineColumnChartData.generateDummyData());
	}

	@Override
	public ComboLineColumnChartData getComboLineColumnChartData() {
		return data;
	}

	@Override
	public void setComboLineColumnChartData(ComboLineColumnChartData data) {
		if (BuildConfig.DEBUG) {
			Log.d(TAG, "Setting data for ComboLineColumnChartView");
		}

		if (null == data) {
			this.data = null;// generateDummyData();
		} else {
			this.data = data;
		}

		axesRenderer.initAxesAttributes();
		chartRenderer.initDataAttributes();
		chartRenderer.initMaxViewport();
		chartRenderer.initCurrentViewport();

		ViewCompat.postInvalidateOnAnimation(ComboLineColumnChartView.this);
	}

	@Override
	public ChartData getChartData() {
		return data;
	}

	@Override
	public void callTouchListener() {
		SelectedValue selectedValue = chartRenderer.getSelectedValue();

		if (selectedValue.isSet()) {

			if (ComboLineColumnChartRenderer.TYPE_COLUMN == selectedValue.getThirdIndex()) {

				ColumnValue value = data.getColumnChartData().getColumns().get(selectedValue.getFirstIndex())
						.getValues().get(selectedValue.getSecondIndex());
				onValueTouchListener.onColumnValueTouched(selectedValue.getFirstIndex(),
						selectedValue.getSecondIndex(), value);

			} else if (ComboLineColumnChartRenderer.TYPE_LINE == selectedValue.getThirdIndex()) {

				PointValue value = data.getLineChartData().getLines().get(selectedValue.getFirstIndex()).getValues()
						.get(selectedValue.getSecondIndex());
				onValueTouchListener.onPointValueTouched(selectedValue.getFirstIndex(), selectedValue.getSecondIndex(),
						value);

			} else {
				throw new IllegalArgumentException("Invalid selected value type " + selectedValue.getThirdIndex());
			}
		} else {
			onValueTouchListener.onNothingTouched();
		}
	}

	public ComboLineColumnChartOnValueTouchListener getOnValueTouchListener() {
		return onValueTouchListener;
	}

	public void setOnValueTouchListener(ComboLineColumnChartOnValueTouchListener touchListener) {
		if (null == touchListener) {
			this.onValueTouchListener = new DummyOnValueTouchListener();
		} else {
			this.onValueTouchListener = touchListener;
		}
	}

	public interface ComboLineColumnChartOnValueTouchListener {
		public void onColumnValueTouched(int selectedLine, int selectedValue, ColumnValue value);

		public void onPointValueTouched(int selectedLine, int selectedValue, PointValue value);

		public void onNothingTouched();
	}

	private static class DummyOnValueTouchListener implements ComboLineColumnChartOnValueTouchListener {

		@Override
		public void onNothingTouched() {
		}

		@Override
		public void onColumnValueTouched(int selectedLine, int selectedValue, ColumnValue value) {

		}

		@Override
		public void onPointValueTouched(int selectedLine, int selectedValue, PointValue value) {

		}
	}

	private class ComboLineChartDataProvider implements LineChartDataProvider {

		@Override
		public LineChartData getLineChartData() {
			return ComboLineColumnChartView.this.data.getLineChartData();
		}

		@Override
		public void setLineChartData(LineChartData data) {
			ComboLineColumnChartView.this.data.setLineChartData(data);

		}

	}

	private class ComboColumnChartDataProvider implements ColumnChartDataProvider {

		@Override
		public ColumnChartData getColumnChartData() {
			return ComboLineColumnChartView.this.data.getColumnChartData();
		}

		@Override
		public void setColumnChartData(ColumnChartData data) {
			ComboLineColumnChartView.this.data.setColumnChartData(data);

		}

	}

}
