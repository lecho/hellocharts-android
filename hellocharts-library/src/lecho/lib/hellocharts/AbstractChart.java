package lecho.lib.hellocharts;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import lecho.lib.hellocharts.gestures.ChartGestureHandler;

public abstract class AbstractChart extends View implements Chart {
	protected ChartCalculator mChartCalculator;
	protected AxesRenderer mAxesRenderer;
	protected ChartGestureHandler mTouchHandler;

	public AbstractChart(Context context) {
		super(context);
	}

	public AbstractChart(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public AbstractChart(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	public AxesRenderer getAxesRenderer() {
		return mAxesRenderer;
	}

	public ChartCalculator getChartCalculator() {
		return mChartCalculator;
	}

	public ChartGestureHandler getTouchHandler() {
		return mTouchHandler;
	}
}
