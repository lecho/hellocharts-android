package lecho.lib.hellocharts.renderer;

import lecho.lib.hellocharts.Chart;
import lecho.lib.hellocharts.ChartCalculator;
import lecho.lib.hellocharts.LineChartDataProvider;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.Viewport;
import lecho.lib.hellocharts.util.Utils;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;

public class PreviewLineChartRenderer extends LineChartRenderer {
    private static final String TAG = "PreviewLineChartRenderer";
	private static final int DEFAULT_PREVIEW_TRANSPARENCY = 64;
	private static final int FULL_ALPHA = 255;
	private static final int DEFAULT_PREVIEW_STROKE_WIDTH_DP = 2;

	private final Paint previewPaint = new Paint();

    private final LineChartDataProvider dataProvider;
    private final Canvas chartCanvas = new Canvas();
    private Bitmap chartBitmap;
    private float prevViewportWidth = 0, prevViewportHeight = 0;
    private boolean firstTime = true;
    private boolean prevDataGroupingResult = false;

	public PreviewLineChartRenderer(Context context, Chart chart, LineChartDataProvider dataProvider) {
		super(context, chart, dataProvider);
        this.dataProvider = dataProvider;
		previewPaint.setAntiAlias(true);
		previewPaint.setColor(Color.LTGRAY);
		previewPaint.setStrokeWidth(Utils.dp2px(density, DEFAULT_PREVIEW_STROKE_WIDTH_DP));
	}

    /**
     * We override the draw method because we won't be using the LineChartRenderer method to draw
     *  the PreviewChartRenderer; instead we create a bitmap every time the viewport size changes
     *  AND there was a change in the data grouping. On this way there is a lot less of computing
     *  to do and chart panning is really smooth
     */
    @Override
    public void draw(Canvas canvas) {
        final Rect contentRect = chart.getChartCalculator().getContentRect();
        final Viewport viewport = chart.getViewport();

        // Recreate the bitmap only when the zoom changed because that is when data grouping may
        //  change so we need to update the view!
        if(Math.abs(prevViewportWidth - viewport.width()) > 0.01 || Math.abs(prevViewportHeight - viewport.height()) > 0.01){
            prevViewportWidth = viewport.width();
            prevViewportHeight = viewport.height();

            // Ok, zoom changed but maybe the data grouping is still applied, there is no need to rebuild
            //  the Bitmap, only rebuild it if the data grouping changed or if it's the first time we
            //  are here!
            boolean result = computeDataGrouping();
            if(firstTime || prevDataGroupingResult != result) {
                firstTime = false;
                prevDataGroupingResult = result;

                final LineChartData data = dataProvider.getLineChartData();
                final Paint linePaint = new Paint();
                final int width = contentRect.width();
                final int height = contentRect.height();

                computePaths();
                chartBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
                chartCanvas.setBitmap(chartBitmap);

                // Set the paint for each processed line and draw it in the bitmap!
                for(int n = 0; n < pathCompatArray.length; ++n){
                    Line line = data.getLines().get(n);
                    linePaint.setStrokeWidth(Utils.dp2px(density, line.getStrokeWidth()));
                    linePaint.setColor(line.getColor());

                    // If the line is filled, draw the area with Path, not PathCompat
                    if(line.isFilled()) {
                        drawArea(chartCanvas, paths[n], line.getAreaTransparency(), linePaint);
                        paths[n].reset();
                    } else {
                        pathCompatArray[n].drawPath(chartCanvas, linePaint);
                    }
                }
            }
        }

        // Draw this amazing thing!
        canvas.drawBitmap(chartBitmap, 0, 0, null);
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