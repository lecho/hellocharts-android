package lecho.lib.hellocharts;

import lecho.lib.hellocharts.gestures.ChartTouchHandler;
import lecho.lib.hellocharts.gestures.ZoomMode;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

public abstract class AbstractChart extends View implements Chart {
	protected ChartCalculator mChartCalculator;
	protected AxesRenderer mAxesRenderer;
	protected ChartTouchHandler mTouchHandler;
	protected ChartRenderer mChartRenderer;
	protected boolean isInteractive = true;
	protected boolean isZoomEnabled = true;
	protected boolean isValueTouchEnabled = true;
	protected ZoomMode zoomMode = ZoomMode.HORIZONTAL_AND_VERTICAL;

	public AbstractChart(Context context) {
		super(context);
	}

	public AbstractChart(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public AbstractChart(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	public ChartRenderer getChartRenderer() {
		return mChartRenderer;
	}

	public AxesRenderer getAxesRenderer() {
		return mAxesRenderer;
	}

	public ChartCalculator getChartCalculator() {
		return mChartCalculator;
	}

	public ChartTouchHandler getTouchHandler() {
		return mTouchHandler;
	}

	public boolean isInteractive() {
		return isInteractive;
	}

	public void setInteractive(boolean isInteractive) {
		this.isInteractive = isInteractive;
	}

	public boolean isZoomEnabled() {
		return isZoomEnabled;
	}

	public void setZoomEnabled(boolean isZoomEnabled) {
		this.isZoomEnabled = isZoomEnabled;
	}

	public boolean isValueTouchEnabled() {
		return isValueTouchEnabled;
	}

	@Override
	public void setValueTouchEnabled(boolean isValueTouchEnabled) {
		this.isValueTouchEnabled = isValueTouchEnabled;

	}

	public ZoomMode getZoomMode() {
		return zoomMode;
	}

	public void setZoomMode(ZoomMode zoomMode) {
		this.zoomMode = zoomMode;
	}
}
