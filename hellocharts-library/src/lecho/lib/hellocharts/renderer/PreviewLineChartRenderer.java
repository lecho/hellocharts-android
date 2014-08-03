package lecho.lib.hellocharts.renderer;

import lecho.lib.hellocharts.Chart;
import lecho.lib.hellocharts.ChartCalculator;
import lecho.lib.hellocharts.LineChartDataProvider;
import lecho.lib.hellocharts.model.Viewport;
import lecho.lib.hellocharts.util.Utils;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

public class PreviewLineChartRenderer extends LineChartRenderer {
	private static final int DEFAULT_PREVIEW_TRANSPARENCY = 64;
	private static final int FULL_ALPHA = 255;
	private static final int DEFAULT_PREVIEW_STROKE_WIDTH_DP = 2;

	private Paint previewPaint = new Paint();

	public PreviewLineChartRenderer(Context context, Chart chart, LineChartDataProvider dataProvider) {
		super(context, chart, dataProvider);
		previewPaint.setAntiAlias(true);
		previewPaint.setColor(Color.LTGRAY);
		previewPaint.setStrokeWidth(Utils.dp2px(density, DEFAULT_PREVIEW_STROKE_WIDTH_DP));
	}

	@Override
	public void drawUnclipped(Canvas canvas) {
		super.drawUnclipped(canvas);
		final ChartCalculator chartCalculator = chart.getChartCalculator();
		final Viewport currentViewport = chartCalculator.getCurrentViewport();
		final float left = chartCalculator.calculateRawX(currentViewport.left);
		final float top = chartCalculator.calculateRawY(currentViewport.top);
		final float right = chartCalculator.calculateRawX(currentViewport.right);
		final float bottom = chartCalculator.calculateRawY(currentViewport.bottom);
		previewPaint.setAlpha(DEFAULT_PREVIEW_TRANSPARENCY);
		previewPaint.setStyle(Paint.Style.FILL);
		canvas.drawRect(left, top, right, bottom, previewPaint);
		previewPaint.setStyle(Paint.Style.STROKE);
		previewPaint.setAlpha(FULL_ALPHA);
		canvas.drawRect(left, top, right, bottom, previewPaint);
	}

	public void setPreviewColor(int color) {
		previewPaint.setColor(color);
	}

	public int getPreviewColor() {
		return previewPaint.getColor();
	}
}