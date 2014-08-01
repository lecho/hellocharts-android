package lecho.lib.hellocharts.renderer;

import lecho.lib.hellocharts.Chart;
import lecho.lib.hellocharts.ChartCalculator;
import lecho.lib.hellocharts.LineChartDataProvider;
import lecho.lib.hellocharts.util.Utils;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;

public class PreviewLineChartRenderer extends LineChartRenderer {
	private static final int DEFAULT_PREVIEW_TRANSPARENCY = 64;
	private static final int DEFAULT_PREVIEW_STROKE_WIDTH_DP = 2;

	private Paint previewViewportPaint = new Paint();

	public PreviewLineChartRenderer(Context context, Chart chart, LineChartDataProvider dataProvider) {
		super(context, chart, dataProvider);

		previewViewportPaint.setAntiAlias(true);
		previewViewportPaint.setColor(Color.LTGRAY);
		previewViewportPaint.setStrokeWidth(Utils.dp2px(context.getResources().getDisplayMetrics().density,
				DEFAULT_PREVIEW_STROKE_WIDTH_DP));
	}

	@Override
	public void drawUnclipped(Canvas canvas) {
		super.drawUnclipped(canvas);
		final ChartCalculator chartCalculator = chart.getChartCalculator();
		final RectF currentViewport = chartCalculator.getCurrentViewport();
		final float left = chartCalculator.calculateRawX(currentViewport.left);
		final float top = chartCalculator.calculateRawY(currentViewport.top);
		final float right = chartCalculator.calculateRawX(currentViewport.right);
		final float bottom = chartCalculator.calculateRawY(currentViewport.bottom);
		previewViewportPaint.setAlpha(DEFAULT_PREVIEW_TRANSPARENCY);
		previewViewportPaint.setStyle(Paint.Style.FILL);
		canvas.drawRect(left, top, right, bottom, previewViewportPaint);
		previewViewportPaint.setStyle(Paint.Style.STROKE);
		previewViewportPaint.setAlpha(255);
		canvas.drawRect(left, bottom, right, top, previewViewportPaint);
	}
}