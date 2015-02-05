package lecho.lib.hellocharts.view;

import lecho.lib.hellocharts.BuildConfig;
import lecho.lib.hellocharts.computator.PreviewChartComputator;
import lecho.lib.hellocharts.gesture.PreviewChartTouchHandler;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.renderer.PreviewLineChartRenderer;
import android.content.Context;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.util.Log;

/**
 * Preview chart that can be used as overview for other LineChart. When you change Viewport of this chart, visible area
 * of other chart will change. For that you need also to use
 * {@link Chart#setViewportChangeListener(lecho.lib.hellocharts.listener.ViewportChangeListener)}
 * 
 * @author Leszek Wach
 * 
 */
public class PreviewLineChartView extends LineChartView {
	private static final String TAG = "PreviewLineChartView";

	protected PreviewLineChartRenderer previewChartRenderer;

	public PreviewLineChartView(Context context) {
		this(context, null, 0);
	}

	public PreviewLineChartView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public PreviewLineChartView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		chartComputator = new PreviewChartComputator();
		previewChartRenderer = new PreviewLineChartRenderer(context, this, this);
		touchHandler = new PreviewChartTouchHandler(context, this);
		setChartRenderer(previewChartRenderer);
		setLineChartData(LineChartData.generateDummyData());
	}

	public void setPreviewColor(int color) {
		if (BuildConfig.DEBUG) {
			Log.d(TAG, "Changing preview area color");
		}

		previewChartRenderer.setPreviewColor(color);
		ViewCompat.postInvalidateOnAnimation(this);
	}

	public int getPreviewColor() {
		return previewChartRenderer.getPreviewColor();
	}

}
