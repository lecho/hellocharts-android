package lecho.lib.hellocharts.view;

import java.util.ArrayList;
import java.util.List;

import lecho.lib.hellocharts.PieChartDataProvider;
import lecho.lib.hellocharts.gesture.PieChartTouchHandler;
import lecho.lib.hellocharts.model.ArcValue;
import lecho.lib.hellocharts.model.ChartData;
import lecho.lib.hellocharts.model.PieChartData;
import lecho.lib.hellocharts.model.SelectedValue;
import lecho.lib.hellocharts.renderer.PieChartRenderer;
import android.content.Context;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;

public class PieChartView extends AbstractChartView implements PieChartDataProvider {
	private static final String TAG = "PieChartView";
	protected PieChartData data;
	protected PieChartOnValueTouchListener onValueTouchListener = new DummyOnValueTouchListener();

	public PieChartView(Context context) {
		this(context, null, 0);
	}

	public PieChartView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public PieChartView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		chartRenderer = new PieChartRenderer(context, this, this);
		touchHandler = new PieChartTouchHandler(context, this);
		setPieChartData(generateDummyData());
	}

	@Override
	public void setPieChartData(PieChartData data) {
		if (null == data) {
			this.data = generateDummyData();
		} else {
			this.data = data;
		}
		// TODO calculateContentArea is not needed here.
		chartCalculator.calculateContentArea(getWidth(), getHeight(), getPaddingLeft(), getPaddingTop(),
				getPaddingRight(), getPaddingBottom());
		chartRenderer.initMaxViewport();
		chartRenderer.initCurrentViewport();
		chartRenderer.initDataAttributes();
		axesRenderer.initAxesAttributes();

		ViewCompat.postInvalidateOnAnimation(PieChartView.this);
	}

	@Override
	public PieChartData getPieChartData() {
		return data;
	}

	@Override
	public ChartData getChartData() {
		return data;
	}

	@Override
	public void callTouchListener(SelectedValue selectedValue) {
		ArcValue arcValue = data.getArcs().get(selectedValue.firstIndex);
		onValueTouchListener.onValueTouched(selectedValue.firstIndex, arcValue);
	}

	public PieChartOnValueTouchListener getOnValueTouchListener() {
		return onValueTouchListener;
	}

	public void setOnValueTouchListener(PieChartOnValueTouchListener touchListener) {
		if (null == touchListener) {
			this.onValueTouchListener = new DummyOnValueTouchListener();
		} else {
			this.onValueTouchListener = touchListener;
		}
	}

	@Override
	public void animationDataUpdate(float scale) {
		for (ArcValue arcValue : data.getArcs()) {
			arcValue.update(scale);
		}
		chartRenderer.initMaxViewport();
		chartRenderer.initCurrentViewport();
		ViewCompat.postInvalidateOnAnimation(this);
	}

	@Override
	public void animationDataFinished(boolean isFinishedSuccess) {
		for (ArcValue arcValue : data.getArcs()) {
			arcValue.finish(true);
		}
		chartRenderer.initMaxViewport();
		chartRenderer.initCurrentViewport();
		ViewCompat.postInvalidateOnAnimation(this);
	}

	private PieChartData generateDummyData() {
		final int numValues = 4;
		PieChartData data = new PieChartData();
		List<ArcValue> values = new ArrayList<ArcValue>(numValues);
		values.add(new ArcValue(40f));
		values.add(new ArcValue(20f));
		values.add(new ArcValue(30f));
		values.add(new ArcValue(50f));
		data.setArcs(values);
		return data;
	}

	public interface PieChartOnValueTouchListener {
		public void onValueTouched(int selectedArc, ArcValue arcValue);
	}

	private static class DummyOnValueTouchListener implements PieChartOnValueTouchListener {

		@Override
		public void onValueTouched(int selectedArc, ArcValue arcValue) {
			// do nothing
		}
	}
}
