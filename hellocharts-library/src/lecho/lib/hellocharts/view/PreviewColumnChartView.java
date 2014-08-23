package lecho.lib.hellocharts.view;

import lecho.lib.hellocharts.PreviewChartComputator;
import lecho.lib.hellocharts.gesture.PreviewChartTouchHandler;
import lecho.lib.hellocharts.renderer.PreviewColumnChartRenderer;
import android.content.Context;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;

public class PreviewColumnChartView extends ColumnChartView {
	private static final String TAG = "ColumnChartView";

	protected PreviewColumnChartRenderer previewChartRenderer;

	public PreviewColumnChartView(Context context) {
		this(context, null, 0);
	}

	public PreviewColumnChartView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public PreviewColumnChartView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		chartComputator = new PreviewChartComputator();
		previewChartRenderer = new PreviewColumnChartRenderer(context, this, this);
		chartRenderer = previewChartRenderer;
		touchHandler = new PreviewChartTouchHandler(context, this);
	}

	public void setPreviewColor(int color) {
		previewChartRenderer.setPreviewColor(color);
		ViewCompat.postInvalidateOnAnimation(this);
	}

	public int getPreviewColor() {
		return previewChartRenderer.getPreviewColor();
	}

}
