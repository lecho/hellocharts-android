package lecho.lib.hellocharts.renderer;

import lecho.lib.hellocharts.Chart;
import lecho.lib.hellocharts.ChartCalculator;
import lecho.lib.hellocharts.LineChartDataProvider;
import lecho.lib.hellocharts.compressor.DownsamplingCompressor;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.Viewport;
import lecho.lib.hellocharts.util.PathCompat;
import lecho.lib.hellocharts.util.Utils;
import lecho.lib.hellocharts.util.XYDataset;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff.Mode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.Log;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class LineChartRenderer extends AbstractChartRenderer {
    private static final String TAG = "LineChartRenderer";
	private static final float LINE_SMOOTHNESS = 0.3f;
	private static final int DEFAULT_LINE_STROKE_WIDTH_DP = 3;
	private static final int DEFAULT_TOUCH_TOLERANCE_MARGIN_DP = 4;

	private static final int MODE_DRAW = 0;
	private static final int MODE_HIGHLIGHT = 1;

	private LineChartDataProvider dataProvider;

	private int touchToleranceMargin;
	private Paint linePaint = new Paint();
	private Paint pointPaint = new Paint();
	private RectF labelRect = new RectF();

	/**
	 * Not hardware accelerated bitmap used to draw Path(smooth lines and filled area). Bitmap has size of contentRect
	 * so it is usually smaller than the view so you should used relative coordinates to draw on it.
	 */
	private Bitmap secondBitmap;
	/**
	 * Canvas to draw on secondBitmap.
	 */
	private Canvas secondCanvas = new Canvas();

    /**
     * Determine if we should use a fast rendering method or not
     */
    private boolean useFastRender = false;
    private boolean drawPoints = true;

    /**
     * Size for the groups when grouping data. If the points in the screen are greater than
     *  compressorThreshold, then we render the series averaging groups of size compressorThreshold
     *  reducing the number of points to draw.
     */
    private int compressorThreshold = 0;
    private float prevViewportWidth = 0, prevViewportHeight = 0;

    /**
     * Lists containing the grouped series when compressorThreshold > 0
     */
    protected XYDataset groupedXYDatasets[];
    /**
     * Simple Path implementation that uses drawLines() method which is way faster than using Path
     */
    protected PathCompat pathCompatArray[];
    protected Path paths[];

    private lecho.lib.hellocharts.compressor.DataCompressor dataCompressor;

	public LineChartRenderer(Context context, Chart chart, LineChartDataProvider dataProvider) {
        super(context, chart);
        this.dataProvider = dataProvider;

        touchToleranceMargin = Utils.dp2px(density, DEFAULT_TOUCH_TOLERANCE_MARGIN_DP);

        linePaint.setAntiAlias(true);
        linePaint.setStyle(Paint.Style.STROKE);
        linePaint.setStrokeWidth(Utils.dp2px(density, DEFAULT_LINE_STROKE_WIDTH_DP));

        pointPaint.setAntiAlias(true);
        pointPaint.setStyle(Paint.Style.FILL);

        setDataCompressor(new DownsamplingCompressor(100));
        Log.i(TAG, Runtime.getRuntime().availableProcessors() + " available cores");
	}

    public void setUseFastRender(boolean useFastRender) {
        this.useFastRender = useFastRender;
    }

    public boolean isUsingFastRender() {
        return useFastRender;
    }

    public boolean isDrawingPoints() {
        return drawPoints;
    }

    public void setDrawPoints(boolean drawPoints) {
        this.drawPoints = drawPoints;
    }

    /**
     * Set the threshold for the compressor usage. If there is more than the specified points
     *  to draw in the screen the compressor will be used, otherwise no compression will be
     *  applied in order to speed up drawing
     * @param value if there is more than this quantity of points to draw, a compression will be applied
     */
    public void setCompressorThreshold(int value){
        compressorThreshold = value;
        prevViewportWidth = 0;      // Force to recreate the groupedXYDatasets
    }

    /**
     * Set the {@link lecho.lib.hellocharts.compressor.DataCompressor} to uso to compress the data
     *  when necessary.
     * @param dataCompressor compressor to use
     */
    public void setDataCompressor(lecho.lib.hellocharts.compressor.DataCompressor dataCompressor){
        this.dataCompressor = dataCompressor;
        prevViewportWidth = 0;      // Force to recreate the groupedXYDatasets
    }

	@Override
	public void initMaxViewport() {
		calculateMaxViewport();
		chart.getChartCalculator().setMaxViewport(tempMaxViewport);
	}

	@Override
	public void initDataAttributes() {
		chart.getChartCalculator().setInternalMargin(calculateContentAreaMargin());
		labelPaint.setTextSize(Utils.sp2px(scaledDensity, chart.getChartData().getValueLabelTextSize()));
		labelPaint.getFontMetricsInt(fontMetrics);

		Rect contentRect = chart.getChartCalculator().getContentRect();
		final int width = contentRect.width();
		final int height = contentRect.height();
		if (width > 0 && height > 0) {
			secondBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
			secondCanvas.setBitmap(secondBitmap);
		}

        pathCompatArray = new PathCompat[dataProvider.getLineChartData().getLines().size()];
        paths = new Path[pathCompatArray.length];
        groupedXYDatasets = new XYDataset[pathCompatArray.length];
        for(int n = 0; n < pathCompatArray.length; ++n) {
            int quantity = dataProvider.getLineChartData().getLines().get(n).getPoints().size();
            paths[n] = new Path();

            if(dataProvider.getLineChartData().getLines().get(n).isSmooth()){
                // TODO: we are creating a really big buffer, we need to find a way to calculate the
                //  necessary buffer size when using cubicTo(). This causes problems on devices with
                //  low memory, they just can't allocate that much.
                pathCompatArray[n] = new PathCompat(quantity*50, 8);
            }else {
                // The buffer size for the current data is given by:
                //  [Points Quantity]*2 + ((Points Quantity]-2)*2)
                pathCompatArray[n] = new PathCompat(quantity * 2 + (quantity - 2) * 2, 8);
            }
        }
        prevViewportWidth = 0;      // Force to recreate the groupedXYDatasets
	}

	@Override
	public void draw(Canvas canvas) {
        final LineChartData data = dataProvider.getLineChartData();
		final ChartCalculator calculator = chart.getChartCalculator();
		final Rect contentRect = calculator.getContentRect();
		secondCanvas.drawColor(Color.TRANSPARENT, Mode.CLEAR);

        computeDataGrouping();
        computePaths();

        // Set the paint for each processed line and draw it in the canvas!
        for(int n = 0; n < pathCompatArray.length; ++n){
            Line line = data.getLines().get(n);
            linePaint.setStrokeWidth(Utils.dp2px(density, line.getStrokeWidth()));
            linePaint.setColor(line.getColor());

            // If the line is filled, draw the area with Path, not PathCompat
            if(line.isFilled()) {
                drawArea(secondCanvas, paths[n], line.getAreaTransparency(), linePaint);
                paths[n].reset();
            } else {
                pathCompatArray[n].drawPath(canvas, linePaint);
            }
        }

		canvas.drawBitmap(secondBitmap, contentRect.left, contentRect.top, null);
	}

    /**
     * Compute the data grouping if necessary (only when zoom changes) according to the value of
     *  compressorThreshold. This is a lossy compression of the data.
     *  @return true if data grouping was applied, false if there was insufficient points to apply
     *          data grouping or if zoom didn't change from the last time
     */
    protected boolean computeDataGrouping(){
        final Viewport viewport = chart.getViewport();

        // If there was a change in zoom, we have to rebuild the groupedXYDatasets using data grouping
        ExecutorService taskExecutor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        if(compressorThreshold > 0 && (Math.abs(prevViewportWidth - viewport.width()) > 0.01 || Math.abs(prevViewportHeight - viewport.height()) > 0.01)){
            prevViewportWidth = viewport.width();
            prevViewportHeight = viewport.height();
            for(int n = 0; n < groupedXYDatasets.length; ++n){
                taskExecutor.execute(new DataCompressor(dataProvider.getLineChartData().getLines().get(n), groupedXYDatasets, n));
            }
            taskExecutor.shutdown();
            try { taskExecutor.awaitTermination(1000, TimeUnit.MILLISECONDS); } catch (InterruptedException e) { e.printStackTrace(); }
            return true;
        }
        return false;
    }

    /**
     * Compute the paths to draw. Here we create all the paths but we don't draw anything, we just
     *  create them in a buffer so then we can draw all at once
     */
    protected void computePaths(){
        final LineChartData data = dataProvider.getLineChartData();
        // Creating the path for each line in parallel reduces the execution time to half when handling
        //  large data in multiple series
        ExecutorService taskExecutor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        for(int n = 0; n < pathCompatArray.length; ++n){
            if(compressorThreshold == 0) groupedXYDatasets[n] = data.getLines().get(n).getPoints();
            taskExecutor.execute(new PathDrawer(data.getLines().get(n), pathCompatArray[n], paths[n], groupedXYDatasets[n]));
        }
        taskExecutor.shutdown();
        try { taskExecutor.awaitTermination(1000, TimeUnit.MILLISECONDS); } catch (InterruptedException e) { e.printStackTrace(); }
    }

    private class DataCompressor implements Runnable {
        private final Line line;
        private final XYDataset[] xyDatasets;
        private final int index;
        final Viewport viewport = chart.getViewport();

        /**
         * This groups up the data in order to reduce the number of points to draw providing
         *  an approximation to the real data
         * @param line line from which take the data to group up
         * @param xyDatasets array containing XYDataset where to store the grouped data
         * @param index index to use in the xyDatasets array
         */
        private DataCompressor(final Line line, final XYDataset[] xyDatasets, final int index) {
            this.line = line;
            this.xyDatasets = xyDatasets;
            this.index = index;
        }

        @Override
        public void run() {
            /* Lossy data compression. If the size of the current viewport has more points than the
             *  established compressorThreshold we calculate an average for a group of points. The data
             *  won't look the same, but instead will show the 'tendency'. On this way we decrease the
             *  number of points to draw.
             */
            if(line.getPoints().subList(viewport.left, viewport.left + viewport.width()).size() > compressorThreshold) {
                xyDatasets[index] = dataCompressor.compress(line, chart);
            } else{
                xyDatasets[index] = line.getPoints();
            }
            Log.i(TAG, "Compressed " + index + " to " + xyDatasets[index].size() + " points");
        }
    }

    private class PathDrawer implements Runnable {
        final Line line;
        final PathCompat pathCompat;
        final Path path;
        final XYDataset xyDataset;

        /**
         * Runnable that generates a {@link lecho.lib.hellocharts.util.PathCompat} to be renderer for
         *  a {@link lecho.lib.hellocharts.model.Line}.
         *
         * @param line line from which generate the path
         * @param pathCompat {@link lecho.lib.hellocharts.util.PathCompat} where to store the generated path
         * @param xyDataset {@link lecho.lib.hellocharts.util.XYDataset} where the algorithm takes the
         *                      original data to generate the path. When using data grouping this contains
         *                      the grouped data.
         */
        public PathDrawer(final Line line, final PathCompat pathCompat, final Path path, final XYDataset xyDataset){
            this.line = line;
            this.pathCompat = pathCompat;
            this.path = path;
            this.xyDataset = xyDataset;
        }

        @Override
        public void run() {
            final ChartCalculator calculator = chart.getChartCalculator();
            final Viewport viewport = chart.getViewport();

            int valueIndex = 0;
            final List<PointValue> dataToRender;

            // Use fast renderer, so we use a resultList instead of the ArrayList and we create groupedXYDatasets
            //  containing only the visible data, otherwise we use the normal rendering method iterating
            //  over the entire ArrayList. This is provided because LineChartPreview needs to render
            //  all the data and not just the visible part.
            if(xyDataset == null) {
                Log.i(TAG, "NULL DATASET!!!!!");
                return;
            }
            if(useFastRender) {
                // Get a subList containing only the visible part of the data we need to render, on this
                //  way we are significatively reducing the time spent in the loop when we are zooming and
                //  a portion of the data is not shown.
                dataToRender = xyDataset.subList(viewport.left, viewport.left + viewport.width());
            }else {
                dataToRender = xyDataset;
            }

            // Draw smooth line
            if(line.isSmooth()){
                drawSmoothPath(dataToRender, pathCompat, path, line);

            } else {
                for (PointValue pointValue : dataToRender) {
                    final float rawX, rawY;
                    if(!line.isFilled()) {
                        rawX = calculator.calculateRawX(pointValue.getX());
                        rawY = calculator.calculateRawY(pointValue.getY());
                    }else {
                        rawX = calculator.calculateRelativeRawX(pointValue.getX());
                        rawY = calculator.calculateRelativeRawY(pointValue.getY());
                    }

                    if (valueIndex == 0) {
                        if(!line.isFilled()) pathCompat.moveTo(rawX, rawY);
                        else path.moveTo(rawX, rawY);
                        ++valueIndex;
                    } else {
                        if(!line.isFilled()) pathCompat.lineTo(rawX, rawY);
                        else path.lineTo(rawX, rawY);
                    }
                }
            }
        }
    }

	@Override
	public void drawUnclipped(Canvas canvas) {
        if(!drawPoints) return;

		final LineChartData data = dataProvider.getLineChartData();
		int lineIndex = 0;
		for (Line line : data.getLines()) {
			if (line.hasPoints()) {
				drawPoints(canvas, line, lineIndex, MODE_DRAW);
			}
			++lineIndex;
		}
		if (isTouched()) {
			// Redraw touched point to bring it to the front
			highlightPoints(canvas);
		}
	}

	@Override
	public boolean checkTouch(float touchX, float touchY) {
        final Viewport viewport = chart.getViewport();
		oldSelectedValue.set(selectedValue);
		selectedValue.clear();
		final LineChartData data = dataProvider.getLineChartData();
		final ChartCalculator calculator = chart.getChartCalculator();
		int lineIndex = 0;
		for (Line line : data.getLines()) {
			int pointRadius = Utils.dp2px(density, line.getPointRadius());
			int valueIndex = 0;
			for (PointValue pointValue : groupedXYDatasets[lineIndex].subList(viewport.left, viewport.left + viewport.width())) {
				final float rawValueX = calculator.calculateRawX(pointValue.getX());
				final float rawValueY = calculator.calculateRawY(pointValue.getY());
				if (isInArea(rawValueX, rawValueY, touchX, touchY, pointRadius + touchToleranceMargin)) {
					selectedValue.set(lineIndex, valueIndex);
				}
				++valueIndex;
			}
			++lineIndex;
		}
		// Check if touch is still on the same value, if not return false.
		if (oldSelectedValue.isSet() && selectedValue.isSet() && !oldSelectedValue.equals(selectedValue)) {
			return false;
		}
		return isTouched();
	}

	private void calculateMaxViewport() {
		tempMaxViewport.set(Float.MAX_VALUE, Float.MIN_VALUE, Float.MIN_VALUE, Float.MAX_VALUE);
		LineChartData data = dataProvider.getLineChartData();

        // TODO: optimize?
		for (Line line : data.getLines()) {
			// Calculate max and min for viewport.
			for (PointValue pointValue : line.getPoints()) {
				if (pointValue.getX() < tempMaxViewport.left) {
					tempMaxViewport.left = pointValue.getX();
				}
				if (pointValue.getX() > tempMaxViewport.right) {
					tempMaxViewport.right = pointValue.getX();
				}
				if (pointValue.getY() < tempMaxViewport.bottom) {
					tempMaxViewport.bottom = pointValue.getY();
				}
				if (pointValue.getY() > tempMaxViewport.top) {
					tempMaxViewport.top = pointValue.getY();
				}

			}
		}
	}

	private int calculateContentAreaMargin() {
		int contentAreaMargin = 0;
		final LineChartData data = dataProvider.getLineChartData();
		for (Line line : data.getLines()) {
			if (line.hasPoints()) {
				int margin = line.getPointRadius() + DEFAULT_TOUCH_TOLERANCE_MARGIN_DP;
				if (margin > contentAreaMargin) {
					contentAreaMargin = margin;
				}
			}
		}
		return Utils.dp2px(density, contentAreaMargin);
	}

    // Draws Besier's curve. It uses PathCompat if line is not filled, otherwise the slower Path is used
    private void drawSmoothPath(List<PointValue> dataset, PathCompat pathCompat, Path path, Line line) {
        final ChartCalculator calculator = chart.getChartCalculator();
        final int lineSize = dataset.size();
        float prepreviousPointX = Float.NaN;
        float prepreviousPointY = Float.NaN;
        float previousPointX = Float.NaN;
        float previousPointY = Float.NaN;
        float currentPointX = Float.NaN;
        float currentPointY = Float.NaN;
        float nextPointX = Float.NaN;
        float nextPointY = Float.NaN;
        for (int valueIndex = 0; valueIndex < lineSize; ++valueIndex) {
            if (Float.isNaN(currentPointX)) {
                PointValue linePoint = dataset.get(valueIndex);
                currentPointX = calculator.calculateRawX(linePoint.getX());
                currentPointY = calculator.calculateRawY(linePoint.getY());
            }
            if (Float.isNaN(previousPointX)) {
                if (valueIndex > 0) {
                    PointValue linePoint = dataset.get(valueIndex - 1);
                    previousPointX = calculator.calculateRawX(linePoint.getX());
                    previousPointY = calculator.calculateRawY(linePoint.getY());
                } else {
                    previousPointX = currentPointX;
                    previousPointY = currentPointY;
                }
            }

            if (Float.isNaN(prepreviousPointX)) {
                if (valueIndex > 1) {
                    PointValue linePoint = dataset.get(valueIndex - 2);
                    prepreviousPointX = calculator.calculateRawX(linePoint.getX());
                    prepreviousPointY = calculator.calculateRawY(linePoint.getY());
                } else {
                    prepreviousPointX = previousPointX;
                    prepreviousPointY = previousPointY;
                }
            }

            // nextPoint is always new one or it is equal currentPoint.
            if (valueIndex < lineSize - 1) {
                PointValue linePoint = dataset.get(valueIndex + 1);
                nextPointX = calculator.calculateRawX(linePoint.getX());
                nextPointY = calculator.calculateRawY(linePoint.getY());
            } else {
                nextPointX = currentPointX;
                nextPointY = currentPointY;
            }

            // Calculate control points.
            final float firstDiffX = (currentPointX - prepreviousPointX);
            final float firstDiffY = (currentPointY - prepreviousPointY);
            final float secondDiffX = (nextPointX - previousPointX);
            final float secondDiffY = (nextPointY - previousPointY);
            final float firstControlPointX = previousPointX + (LINE_SMOOTHNESS * firstDiffX);
            final float firstControlPointY = previousPointY + (LINE_SMOOTHNESS * firstDiffY);
            final float secondControlPointX = currentPointX - (LINE_SMOOTHNESS * secondDiffX);
            final float secondControlPointY = currentPointY - (LINE_SMOOTHNESS * secondDiffY);

            if (valueIndex == 0) {
                // Move to start point.
                if(line.isFilled()) path.moveTo(currentPointX, currentPointY);
                else pathCompat.moveTo(currentPointX, currentPointY);
            } else {
                if(line.isFilled()) path.cubicTo(firstControlPointX, firstControlPointY, secondControlPointX, secondControlPointY,
                        currentPointX, currentPointY);
                else pathCompat.cubicTo(firstControlPointX, firstControlPointY, secondControlPointX, secondControlPointY,
                        currentPointX, currentPointY);
            }

            // Shift values by one back to prevent recalculation of values that have
            // been already calculated.
            prepreviousPointX = previousPointX;
            prepreviousPointY = previousPointY;
            previousPointX = currentPointX;
            previousPointY = currentPointY;
            currentPointX = nextPointX;
            currentPointY = nextPointY;
        }
    }

	// TODO: Drawing points can be done in the same loop as drawing lines but it
	// may cause problems in the future with
	// implementing point styles.
	private void drawPoints(Canvas canvas, Line line, int lineIndex, int mode) {
		final ChartCalculator calculator = chart.getChartCalculator();
        final Viewport viewport = chart.getViewport();
        final List<PointValue> dataToRender = useFastRender ?
                groupedXYDatasets[lineIndex].subList(viewport.left, viewport.left + viewport.width()) :
                groupedXYDatasets[lineIndex];

        pointPaint.setColor(line.getColor());
        int valueIndex = 0;

		for (PointValue pointValue : dataToRender) {
			int pointRadius = Utils.dp2px(density, line.getPointRadius());
			final float rawX = calculator.calculateRawX(pointValue.getX());
			final float rawY = calculator.calculateRawY(pointValue.getY());

            if (calculator.isWithinContentRect((int)rawX, (int)rawY)) {
                if (MODE_DRAW == mode) {
                    drawPoint(canvas, line, rawX, rawY, pointRadius);
                    if (line.hasLabels()) {
                        drawLabel(canvas, calculator, line, pointValue, rawX, rawY, pointRadius + labelOffset);
                    }
                } else if (MODE_HIGHLIGHT == mode) {
                    highlightPoint(canvas, calculator, line, pointValue, rawX, rawY, lineIndex, valueIndex);
                } else {
                    throw new IllegalStateException("Cannot process points in mode: " + mode);
                }
            }
			++valueIndex;
		}
	}

	private void drawPoint(Canvas canvas, Line line, float rawX, float rawY, float pointRadius) {
		if (Line.SHAPE_SQUARE == line.getPointShape()) {
			canvas.drawRect(rawX - pointRadius, rawY - pointRadius, rawX + pointRadius, rawY + pointRadius, pointPaint);
		} else {
			canvas.drawCircle(rawX, rawY, pointRadius, pointPaint);
		}
	}

	private void highlightPoints(Canvas canvas) {
		int lineIndex = selectedValue.firstIndex;
		Line line = dataProvider.getLineChartData().getLines().get(lineIndex);
		drawPoints(canvas, line, lineIndex, MODE_HIGHLIGHT);
	}

	private void highlightPoint(Canvas canvas, ChartCalculator calculator, Line line, PointValue pointValue,
			float rawX, float rawY, int lineIndex, int valueIndex) {
		if (selectedValue.firstIndex == lineIndex && selectedValue.secondIndex == valueIndex) {
			int pointRadius = Utils.dp2px(density, line.getPointRadius());
			pointPaint.setColor(line.getDarkenColor());
			drawPoint(canvas, line, rawX, rawY, pointRadius + touchToleranceMargin);
			if (line.hasLabels() || line.hasLabelsOnlyForSelected()) {
				drawLabel(canvas, calculator, line, pointValue, rawX, rawY, pointRadius + labelOffset);
			}
		}
	}

	private void drawLabel(Canvas canvas, ChartCalculator calculator, Line line, PointValue pointValue, float rawX,
			float rawY, float offset) {
		final Rect contentRect = calculator.getContentRect();
		final int nummChars = line.getFormatter().formatValue(labelBuffer, pointValue.getY());
		final float labelWidth = labelPaint.measureText(labelBuffer, labelBuffer.length - nummChars, nummChars);
		final int labelHeight = Math.abs(fontMetrics.ascent);
		float left = rawX - labelWidth / 2 - labelMargin;
		float right = rawX + labelWidth / 2 + labelMargin;
		float top = rawY - offset - labelHeight - labelMargin * 2;
		float bottom = rawY - offset;
		if (top < contentRect.top) {
			top = rawY + offset;
			bottom = rawY + offset + labelHeight + labelMargin * 2;
		}
		if (left < contentRect.left) {
			left = rawX;
			right = rawX + labelWidth + labelMargin * 2;
		}
		if (right > contentRect.right) {
			left = rawX - labelWidth - labelMargin * 2;
			right = rawX;
		}
		labelRect.set(left, top, right, bottom);
		int orginColor = labelPaint.getColor();
		labelPaint.setColor(line.getDarkenColor());
		canvas.drawRect(left, top, right, bottom, labelPaint);
		labelPaint.setColor(orginColor);
		canvas.drawText(labelBuffer, labelBuffer.length - nummChars, nummChars, left + labelMargin, bottom
				- labelMargin, labelPaint);
	}

	protected void drawArea(final Canvas canvas, final Path path, final int transparency, final Paint linePaint) {
		final ChartCalculator calculator = chart.getChartCalculator();
		final Rect contentRect = calculator.getContentRect();

		path.lineTo(contentRect.width(), contentRect.height());
		path.lineTo(0, contentRect.height());
		path.close();

		linePaint.setStyle(Paint.Style.FILL);
		linePaint.setAlpha(transparency);
        canvas.drawPath(path, linePaint);
		linePaint.setStyle(Paint.Style.STROKE);
	}

	private boolean isInArea(float x, float y, float touchX, float touchY, float radius) {
		float diffX = touchX - x;
		float diffY = touchY - y;
		return Math.pow(diffX, 2) + Math.pow(diffY, 2) <= 2 * Math.pow(radius, 2);
	}

}
