package lecho.lib.hellocharts.view;

import lecho.lib.hellocharts.BuildConfig;
import lecho.lib.hellocharts.model.ChartData;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.SelectedValue;
import lecho.lib.hellocharts.provider.LineChartDataProvider;
import lecho.lib.hellocharts.renderer.LineChartRenderer;
import android.content.Context;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.util.Log;

/**
 * LineChart, supports cubic lines, filled lines, circle and square points. Point radius and stroke width can be
 * adjusted usind LineChartData attributes.
 * 
 * @author Leszek Wach
 * 
 */
public class LineChartView extends AbstractChartView implements LineChartDataProvider {
	private static final String TAG = "LineChartView";
	protected LineChartData data;
	protected LineChartOnValueTouchListener onValueTouchListener = new DummyOnValueTouchListener();

	public LineChartView(Context context) {
		this(context, null, 0);
	}

	public LineChartView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public LineChartView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		chartRenderer = new LineChartRenderer(context, this, this);
		setLineChartData(LineChartData.generateDummyData());
	}

	@Override
	public void setLineChartData(LineChartData data) {
		if (BuildConfig.DEBUG) {
			Log.d(TAG, "Setting data for LineChartView");
		}

		if (null == data) {
			this.data = LineChartData.generateDummyData();
		} else {
			this.data = data;
		}

		axesRenderer.initAxesAttributes();
		chartRenderer.initDataAttributes();
		chartRenderer.initMaxViewport();
		chartRenderer.initCurrentViewport();

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
	public void callTouchListener() {
		SelectedValue selectedValue = chartRenderer.getSelectedValue();

		if (selectedValue.isSet()) {
			PointValue point = data.getLines().get(selectedValue.getFirstIndex()).getValues()
					.get(selectedValue.getSecondIndex());
			onValueTouchListener.onValueTouched(selectedValue.getFirstIndex(), selectedValue.getSecondIndex(), point);
		} else {
			onValueTouchListener.onNothingTouched();
		}
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

	public interface LineChartOnValueTouchListener {
		public void onValueTouched(int selectedLine, int selectedValue, PointValue value);

		public void onNothingTouched();

	}

	private static class DummyOnValueTouchListener implements LineChartOnValueTouchListener {

		@Override
		public void onValueTouched(int selectedLine, int selectedValue, PointValue value) {
		}

		@Override
		public void onNothingTouched() {
		}

	}
}
