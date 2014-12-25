package lecho.lib.hellocharts.renderer;

import lecho.lib.hellocharts.Chart;
import lecho.lib.hellocharts.ChartCalculator;
import lecho.lib.hellocharts.LineChartDataProvider;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.Viewport;
import lecho.lib.hellocharts.util.PathCompat;
import lecho.lib.hellocharts.util.Utils;
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

import java.util.Map;
import java.util.SortedMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class LineChartRenderer extends AbstractChartRenderer {
    private static final String TAG = "LineChartRenderer";
	private static final float LINE_SMOOTHNESS = 0.16f;
	private static final int DEFAULT_LINE_STROKE_WIDTH_DP = 3;
	private static final int DEFAULT_TOUCH_TOLERANCE_MARGIN_DP = 4;

	private static final int MODE_DRAW = 0;
	private static final int MODE_HIGHLIGHT = 1;

	private LineChartDataProvider dataProvider;

	private int touchToleranceMargin;
	private Path path = new Path();
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
	 * Simple Path implementation that uses drawLines() method which is way faster than using Path
	 */
    private PathCompat pathCompatArray[] = new PathCompat[8];
    private final boolean useFastRender;

	public LineChartRenderer(Context context, Chart chart, LineChartDataProvider dataProvider) {
		this(context, chart, dataProvider, false);
	}

    public LineChartRenderer(Context context, Chart chart, LineChartDataProvider dataProvider, boolean fastRender) {
        super(context, chart);
        this.dataProvider = dataProvider;

        touchToleranceMargin = Utils.dp2px(density, DEFAULT_TOUCH_TOLERANCE_MARGIN_DP);

        linePaint.setAntiAlias(true);
        linePaint.setStyle(Paint.Style.STROKE);
        linePaint.setStrokeWidth(Utils.dp2px(density, DEFAULT_LINE_STROKE_WIDTH_DP));

        pointPaint.setAntiAlias(true);
        pointPaint.setStyle(Paint.Style.FILL);

        for(int n = 0; n < pathCompatArray.length; ++n){
            //TODO: the buffer size should be according to the number of data points to draw
            pathCompatArray[n] = new PathCompat(65535*2);
        }
        useFastRender = fastRender;
        Log.i("", Runtime.getRuntime().availableProcessors() + " available cores");
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
	}

	@Override
	public void draw(Canvas canvas) {
		final LineChartData data = dataProvider.getLineChartData();
		final ChartCalculator calculator = chart.getChartCalculator();
		final Rect contentRect = calculator.getContentRect();
		secondCanvas.drawColor(Color.TRANSPARENT, Mode.CLEAR);

        //long time = System.nanoTime();
        // Creating the path for each line in parallel reduces the execution time to half when handling
        //  large data, when we start zooming and subMap iteration gets faster this almost has no advantage
        //  saving just a few milliseconds, still worth it.
        ExecutorService taskExecutor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        for(int n = 0; n < data.getLines().size(); ++n){
            taskExecutor.execute(new PathDrawer(data.getLines().get(n), pathCompatArray[n]));
        }
        taskExecutor.shutdown();
        try { taskExecutor.awaitTermination(1000, TimeUnit.MILLISECONDS); } catch (InterruptedException e) { e.printStackTrace(); }
        //Log.i(TAG, "Calculated in [ms]: " + (System.nanoTime() - time) / 1000000f);

        // Set the paint for each processed line and draw it in the canvas!
        for(int n = 0; n < data.getLines().size(); ++n){
            Line line = data.getLines().get(n);
            linePaint.setStrokeWidth(Utils.dp2px(density, line.getStrokeWidth()));
            linePaint.setColor(line.getColor());
            pathCompatArray[n].drawPath(canvas, linePaint);
        }
        /*
        for (Line line : data.getLines()) {
			if (line.hasLines()) {
				if (line.isSmooth()) {
					drawSmoothPath(canvas, line);
				} else {
					drawPath(canvas, line);
				}
			}
		}*/
		canvas.drawBitmap(secondBitmap, contentRect.left, contentRect.top, null);
	}

    private class PathDrawer implements Runnable {
        final Line line;
        final PathCompat pathCompat;

        public PathDrawer(final Line line, final PathCompat p){
            this.line = line;
            pathCompat = p;
        }

        @Override
        public void run() {
            final ChartCalculator calculator = chart.getChartCalculator();
            final Viewport viewport = chart.getViewport();

            int valueIndex = 0;
            float prevRawX = 0;

            // Use fast renderer, so we use a map instead of the ArrayList and we create subMaps
            //  containing only the visible data, otherwise we use the normal rendering method iterating
            //  over the entire ArrayList. This is provided because LineChartPreview needs to render
            //  all the data and not just the visible part.
            if(useFastRender) {
                // Get a subMap containing only the visible part of the data we need to render, on this
                //  way we are significatively reducing the time spent in the loop when we are zooming and
                //  a portion of the data is not shown.
                // Access time is O(log(n)) compared to O(1) for the array list, but even with a small amount
                //  of zoom the time to iterate drops rapidly outperforming ArrayList due to the fact
                //  that we are creating a smaller map.
                // Creating the subMap is extremely fast, it takes a worst case of 200uS on my Nexus 5.
                final SortedMap<Float, Float> subMap = line.getPointsMap().subMap(viewport.left, viewport.left + viewport.width());

                for (Map.Entry<Float, Float> entry : subMap.entrySet()) {
                    float rawX = calculator.calculateRawX(entry.getKey());
                    float rawY = calculator.calculateRawY(entry.getValue());

                    if (valueIndex == 0) {
                        prevRawX = rawX;
                        pathCompat.moveTo(rawX, rawY);
                        ++valueIndex;
                    } else {
                        if ((rawX - prevRawX) > 3.0f) {
                            prevRawX = rawX;
                            pathCompat.lineTo(rawX, rawY);
                        }
                    }
                }
            }else {
                for (PointValue point : line.getPoints()) {
                    float rawX = calculator.calculateRawX(point.getX());
                    float rawY = calculator.calculateRawY(point.getY());

                    if (valueIndex == 0) {
                        prevRawX = rawX;
                        pathCompat.moveTo(rawX, rawY);
                        ++valueIndex;
                    } else {
                        if ((rawX - prevRawX) > 1.0f) {
                            prevRawX = rawX;
                            pathCompat.lineTo(rawX, rawY);
                        }
                    }

                    if (line.isFilled()) {
                        // For filled line use path.
                        rawX = calculator.calculateRelativeRawX(point.getX());
                        rawY = calculator.calculateRelativeRawY(point.getY());
                        if (valueIndex == 0) {
                            path.moveTo(rawX, rawY);
                        } else {
                            path.lineTo(rawX, rawY);
                        }
                    }
                }
            }
        }
    }

	@Override
	public void drawUnclipped(Canvas canvas) {
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
		oldSelectedValue.set(selectedValue);
		selectedValue.clear();
		final LineChartData data = dataProvider.getLineChartData();
		final ChartCalculator calculator = chart.getChartCalculator();
		int lineIndex = 0;
		for (Line line : data.getLines()) {
			int pointRadius = Utils.dp2px(density, line.getPointRadius());
			int valueIndex = 0;
			for (PointValue pointValue : line.getPoints()) {
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

		// TODO: Optimize.
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

	/**
	 * Draws Besier's curve. Uses path so drawing has to be done on secondCanvas to avoid problem with hardware
	 * acceleration.
	 */
	private void drawSmoothPath(Canvas canvas, final Line line) {
		final ChartCalculator calculator = chart.getChartCalculator();
		linePaint.setStrokeWidth(Utils.dp2px(density, line.getStrokeWidth()));
		linePaint.setColor(line.getColor());
		final int lineSize = line.getPoints().size();
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
				PointValue linePoint = line.getPoints().get(valueIndex);
				currentPointX = calculator.calculateRelativeRawX(linePoint.getX());
				currentPointY = calculator.calculateRelativeRawY(linePoint.getY());
			}
			if (Float.isNaN(previousPointX)) {
				if (valueIndex > 0) {
					PointValue linePoint = line.getPoints().get(valueIndex - 1);
					previousPointX = calculator.calculateRelativeRawX(linePoint.getX());
					previousPointY = calculator.calculateRelativeRawY(linePoint.getY());
				} else {
					previousPointX = currentPointX;
					previousPointY = currentPointY;
				}
			}

			if (Float.isNaN(prepreviousPointX)) {
				if (valueIndex > 1) {
					PointValue linePoint = line.getPoints().get(valueIndex - 2);
					prepreviousPointX = calculator.calculateRelativeRawX(linePoint.getX());
					prepreviousPointY = calculator.calculateRelativeRawY(linePoint.getY());
				} else {
					prepreviousPointX = previousPointX;
					prepreviousPointY = previousPointY;
				}
			}

			// nextPoint is always new one or it is equal currentPoint.
			if (valueIndex < lineSize - 1) {
				PointValue linePoint = line.getPoints().get(valueIndex + 1);
				nextPointX = calculator.calculateRelativeRawX(linePoint.getX());
				nextPointY = calculator.calculateRelativeRawY(linePoint.getY());
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
				path.moveTo(currentPointX, currentPointY);
			} else {
				path.cubicTo(firstControlPointX, firstControlPointY, secondControlPointX, secondControlPointY,
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

		secondCanvas.drawPath(path, linePaint);
		if (line.isFilled()) {
			drawArea(canvas, line.getAreaTransparency());
		}
		path.reset();
	}

	// TODO Drawing points can be done in the same loop as drawing lines but it
	// may cause problems in the future with
	// implementing point styles.
	private void drawPoints(Canvas canvas, Line line, int lineIndex, int mode) {
		final ChartCalculator calculator = chart.getChartCalculator();
		pointPaint.setColor(line.getColor());
		int valueIndex = 0;
		for (PointValue pointValue : line.getPoints()) {
			int pointRadius = Utils.dp2px(density, line.getPointRadius());
			final float rawX = calculator.calculateRawX(pointValue.getX());
			final float rawY = calculator.calculateRawY(pointValue.getY());
			if (calculator.isWithinContentRect((int) rawX, (int) rawY)) {
				// Draw points only if they are within contentRect
				if (MODE_DRAW == mode) {
					drawPoint(canvas, line, pointValue, rawX, rawY, pointRadius);
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

	private void drawPoint(Canvas canvas, Line line, PointValue pointValue, float rawX, float rawY, float pointRadius) {
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
			drawPoint(canvas, line, pointValue, rawX, rawY, pointRadius + touchToleranceMargin);
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

	private void drawArea(Canvas canvas, int transparency) {
		final ChartCalculator calculator = chart.getChartCalculator();
		final Rect contentRect = calculator.getContentRect();
		path.lineTo(contentRect.width(), contentRect.height());
		path.lineTo(0, contentRect.height());
		path.close();
		linePaint.setStyle(Paint.Style.FILL);
		linePaint.setAlpha(transparency);
		secondCanvas.drawPath(path, linePaint);
		linePaint.setStyle(Paint.Style.STROKE);
	}

	private boolean isInArea(float x, float y, float touchX, float touchY, float radius) {
		float diffX = touchX - x;
		float diffY = touchY - y;
		return Math.pow(diffX, 2) + Math.pow(diffY, 2) <= 2 * Math.pow(radius, 2);
	}

}
