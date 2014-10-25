package lecho.lib.hellocharts.view;

import lecho.lib.hellocharts.BuildConfig;
import lecho.lib.hellocharts.model.BubbleChartData;
import lecho.lib.hellocharts.model.BubbleValue;
import lecho.lib.hellocharts.model.ChartData;
import lecho.lib.hellocharts.model.SelectedValue;
import lecho.lib.hellocharts.provider.BubbleChartDataProvider;
import lecho.lib.hellocharts.renderer.BubbleChartRenderer;
import android.content.Context;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.util.Log;

/**
 * BubbleChart, supports circle bubbles and square bubbles.
 * 
 * @author lecho
 * 
 */
public class BubbleChartView extends AbstractChartView implements BubbleChartDataProvider {
	private static final String TAG = "BubbleChartView";
	protected BubbleChartData data;
	protected BubbleChartOnValueTouchListener onValueTouchListener = new DummyOnValueTouchListener();

	protected BubbleChartRenderer bubbleChartRenderer;

	public BubbleChartView(Context context) {
		this(context, null, 0);
	}

	public BubbleChartView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public BubbleChartView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		bubbleChartRenderer = new BubbleChartRenderer(context, this, this);
		chartRenderer = bubbleChartRenderer;
		setBubbleChartData(BubbleChartData.generateDummyData());
	}

	@Override
	public void setBubbleChartData(BubbleChartData data) {
		if (BuildConfig.DEBUG) {
			Log.d(TAG, "Setting data for BubbleChartView");
		}

		if (null == data) {
			this.data = BubbleChartData.generateDummyData();
		} else {
			this.data = data;
		}
		axesRenderer.initAxesAttributes();
		chartRenderer.initDataAttributes();
		chartRenderer.initMaxViewport();
		chartRenderer.initCurrentViewport();

		ViewCompat.postInvalidateOnAnimation(BubbleChartView.this);
	}

	@Override
	public BubbleChartData getBubbleChartData() {
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
			BubbleValue value = data.getValues().get(selectedValue.getFirstIndex());
			onValueTouchListener.onValueTouched(selectedValue.getFirstIndex(), value);
		} else {
			onValueTouchListener.onNothingTouched();
		}
	}

	public BubbleChartOnValueTouchListener getOnValueTouchListener() {
		return onValueTouchListener;
	}

	public void setOnValueTouchListener(BubbleChartOnValueTouchListener touchListener) {
		if (null == touchListener) {
			this.onValueTouchListener = new DummyOnValueTouchListener();
		} else {
			this.onValueTouchListener = touchListener;
		}
	}

	/**
	 * Removes empty spaces, top-bottom for portrait orientation and left-right for landscape. This method has to be
	 * called after view View#onSizeChanged() method is called and chart data is set. This method may be inaccurate.
	 * 
	 * @see BubbleChartRenderer#removeMargins()
	 */
	public void removeMargins() {
		bubbleChartRenderer.removeMargins();
		ViewCompat.postInvalidateOnAnimation(this);
	}

	public interface BubbleChartOnValueTouchListener {
		public void onValueTouched(int selectedBubble, BubbleValue value);

		public void onNothingTouched();

	}

	private static class DummyOnValueTouchListener implements BubbleChartOnValueTouchListener {

		@Override
		public void onValueTouched(int selectedBubble, BubbleValue value) {
		}

		@Override
		public void onNothingTouched() {
		}

	}
}
