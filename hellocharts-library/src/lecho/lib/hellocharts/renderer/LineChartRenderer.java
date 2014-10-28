package lecho.lib.hellocharts.renderer;

import lecho.lib.hellocharts.ChartComputator;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.ValueShape;
import lecho.lib.hellocharts.provider.LineChartDataProvider;
import lecho.lib.hellocharts.util.Utils;
import lecho.lib.hellocharts.view.Chart;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Cap;
import android.graphics.Path;
import android.graphics.PathEffect;
import android.graphics.PorterDuff.Mode;
import android.graphics.Rect;

/**
 * 
 * Renderer for line chart. Can draw lines, cubic lines, filled area chart and scattered chart.
 * 
 * @author Leszek Wach
 * 
 */
public class LineChartRenderer extends AbstractChartRenderer {
	private static final float LINE_SMOOTHNES = 0.16f;
	private static final int DEFAULT_LINE_STROKE_WIDTH_DP = 3;
	private static final int DEFAULT_TOUCH_TOLLERANCE_MARGIN_DP = 4;

	private static final int MODE_DRAW = 0;
	private static final int MODE_HIGHLIGHT = 1;

	private LineChartDataProvider dataProvider;

	private int checkPrecission;

	private float baseValue;

	private int touchTolleranceMargin;
	private Path path = new Path();
	private Paint linePaint = new Paint();
	private Paint pointPaint = new Paint();
	private float[] valuesBuff = new float[2];

	/**
	 * Not hardware accelerated bitmap used to draw Path(smooth lines and filled area). Bitmap has size of contentRect
	 * so it is usually smaller than the view so you should used relative coordinates to draw on it.
	 */
	private Bitmap swBitmap;
	/**
	 * Canvas to draw on secondBitmap.
	 */
	private Canvas swCanvas = new Canvas();

	public LineChartRenderer(Context context, Chart chart, LineChartDataProvider dataProvider) {
		super(context, chart);
		this.dataProvider = dataProvider;

		touchTolleranceMargin = Utils.dp2px(density, DEFAULT_TOUCH_TOLLERANCE_MARGIN_DP);

		linePaint.setAntiAlias(true);
		linePaint.setStyle(Paint.Style.STROKE);
		linePaint.setStrokeCap(Cap.ROUND);
		linePaint.setStrokeWidth(Utils.dp2px(density, DEFAULT_LINE_STROKE_WIDTH_DP));

		pointPaint.setAntiAlias(true);
		pointPaint.setStyle(Paint.Style.FILL);

		checkPrecission = Utils.dp2px(density, 2);

	}

	@Override
	public void initMaxViewport() {
		if (isViewportCalculationEnabled) {
			calculateMaxViewport();
			chart.getChartComputator().setMaxViewport(tempMaxViewport);
		}
	}

	@Override
	public void initDataMeasuremetns() {
		final ChartComputator computator = chart.getChartComputator();

		computator.setInternalMargin(calculateContentAreaMargin());

		if (computator.getChartWidth() > 0 && computator.getChartHeight() > 0) {
			swBitmap = Bitmap.createBitmap(computator.getChartWidth(), computator.getChartHeight(),
					Bitmap.Config.ARGB_8888);
			swCanvas.setBitmap(swBitmap);
		}
	}

	@Override
	public void initDataAttributes() {
		super.initDataAttributes();

		LineChartData data = dataProvider.getLineChartData();

		// Set base value for this chart - default is 0.
		baseValue = data.getBaseValue();

	}

	@Override
	public void draw(Canvas canvas) {
		final LineChartData data = dataProvider.getLineChartData();

		final Canvas drawCanvas;

		// swBitmap can be null if chart is rendered in layout editor. In that case use default canvas and not swCanvas.
		if (null != swBitmap) {
			drawCanvas = swCanvas;
			drawCanvas.drawColor(Color.TRANSPARENT, Mode.CLEAR);
		} else {
			drawCanvas = canvas;
		}

		for (Line line : data.getLines()) {
			if (line.hasLines()) {
				if (line.isCubic()) {
					drawSmoothPath(drawCanvas, line);
				} else {
					drawPath(drawCanvas, line);
				}
			}
		}

		if (null != swBitmap) {
			canvas.drawBitmap(swBitmap, 0, 0, null);
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
		selectedValue.clear();
		final LineChartData data = dataProvider.getLineChartData();
		final ChartComputator computator = chart.getChartComputator();
		int lineIndex = 0;
		for (Line line : data.getLines()) {
			int pointRadius = Utils.dp2px(density, line.getPointRadius());
			int valueIndex = 0;
			for (PointValue pointValue : line.getValues()) {
				final float rawValueX = computator.computeRawX(pointValue.getX());
				final float rawValueY = computator.computeRawY(pointValue.getY());
				if (isInArea(rawValueX, rawValueY, touchX, touchY, pointRadius + touchTolleranceMargin)) {
					selectedValue.set(lineIndex, valueIndex, 0);
				}
				++valueIndex;
			}
			++lineIndex;
		}
		return isTouched();
	}

	private void calculateMaxViewport() {
		tempMaxViewport.set(Float.MAX_VALUE, Float.MIN_VALUE, Float.MIN_VALUE, Float.MAX_VALUE);
		LineChartData data = dataProvider.getLineChartData();

		for (Line line : data.getLines()) {
			// Calculate max and min for viewport.
			for (PointValue pointValue : line.getValues()) {
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
				int margin = line.getPointRadius() + DEFAULT_TOUCH_TOLLERANCE_MARGIN_DP;
				if (margin > contentAreaMargin) {
					contentAreaMargin = margin;
				}
			}
		}
		return Utils.dp2px(density, contentAreaMargin);
	}

	/**
	 * Draws lines, uses path for drawing filled area on secondCanvas. Line is drawn with canvas.drawLines() method.
	 */
	private void drawPath(Canvas canvas, final Line line) {
		final ChartComputator computator = chart.getChartComputator();

		prepareLinePaint(line);

		int valueIndex = 0;
		for (PointValue pointValue : line.getValues()) {

			final float rawX = computator.computeRawX(pointValue.getX());
			final float rawY = computator.computeRawY(pointValue.getY());

			if (valueIndex == 0) {
				path.moveTo(rawX, rawY);
			} else {
				path.lineTo(rawX, rawY);
			}

			++valueIndex;

		}

		canvas.drawPath(path, linePaint);

		if (line.isFilled()) {
			drawArea(canvas, line.getAreaTransparency());
		}

		path.reset();
	}

	/**
	 * Draws Besier's curve. Uses path so drawing has to be done on secondCanvas to avoid problem with hardware
	 * acceleration.
	 */
	private void drawSmoothPath(Canvas canvas, final Line line) {
		final ChartComputator computator = chart.getChartComputator();

		prepareLinePaint(line);

		final int lineSize = line.getValues().size();
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
				PointValue linePoint = line.getValues().get(valueIndex);
				currentPointX = computator.computeRawX(linePoint.getX());
				currentPointY = computator.computeRawY(linePoint.getY());
			}
			if (Float.isNaN(previousPointX)) {
				if (valueIndex > 0) {
					PointValue linePoint = line.getValues().get(valueIndex - 1);
					previousPointX = computator.computeRawX(linePoint.getX());
					previousPointY = computator.computeRawY(linePoint.getY());
				} else {
					previousPointX = currentPointX;
					previousPointY = currentPointY;
				}
			}

			if (Float.isNaN(prepreviousPointX)) {
				if (valueIndex > 1) {
					PointValue linePoint = line.getValues().get(valueIndex - 2);
					prepreviousPointX = computator.computeRawX(linePoint.getX());
					prepreviousPointY = computator.computeRawY(linePoint.getY());
				} else {
					prepreviousPointX = previousPointX;
					prepreviousPointY = previousPointY;
				}
			}

			// nextPoint is always new one or it is equal currentPoint.
			if (valueIndex < lineSize - 1) {
				PointValue linePoint = line.getValues().get(valueIndex + 1);
				nextPointX = computator.computeRawX(linePoint.getX());
				nextPointY = computator.computeRawY(linePoint.getY());
			} else {
				nextPointX = currentPointX;
				nextPointY = currentPointY;
			}

			// Calculate control points.
			final float firstDiffX = (currentPointX - prepreviousPointX);
			final float firstDiffY = (currentPointY - prepreviousPointY);
			final float secondDiffX = (nextPointX - previousPointX);
			final float secondDiffY = (nextPointY - previousPointY);
			final float firstControlPointX = previousPointX + (LINE_SMOOTHNES * firstDiffX);
			final float firstControlPointY = previousPointY + (LINE_SMOOTHNES * firstDiffY);
			final float secondControlPointX = currentPointX - (LINE_SMOOTHNES * secondDiffX);
			final float secondControlPointY = currentPointY - (LINE_SMOOTHNES * secondDiffY);

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

		canvas.drawPath(path, linePaint);
		if (line.isFilled()) {
			drawArea(canvas, line.getAreaTransparency());
		}
		path.reset();
	}

	private void prepareLinePaint(final Line line) {
		linePaint.setStrokeWidth(Utils.dp2px(density, line.getStrokeWidth()));
		linePaint.setColor(line.getColor());
		PathEffect pathEffect = line.getPathEffect();
		if (null != pathEffect) {
			linePaint.setPathEffect(pathEffect);
		}
	}

	// TODO Drawing points can be done in the same loop as drawing lines but it
	// may cause problems in the future with
	// implementing point styles.
	private void drawPoints(Canvas canvas, Line line, int lineIndex, int mode) {
		final ChartComputator computator = chart.getChartComputator();
		pointPaint.setColor(line.getColor());
		int valueIndex = 0;
		for (PointValue pointValue : line.getValues()) {
			int pointRadius = Utils.dp2px(density, line.getPointRadius());
			final float rawX = computator.computeRawX(pointValue.getX());
			final float rawY = computator.computeRawY(pointValue.getY());
			if (computator.isWithinContentRect(rawX, rawY, checkPrecission)) {
				// Draw points only if they are within contentRect, using contentRect instead of viewport to avoid some
				// float rounding problems.
				if (MODE_DRAW == mode) {
					drawPoint(canvas, line, pointValue, rawX, rawY, pointRadius);
					if (line.hasLabels()) {
						drawLabel(canvas, line, pointValue, rawX, rawY, pointRadius + labelOffset);
					}
				} else if (MODE_HIGHLIGHT == mode) {
					highlightPoint(canvas, line, pointValue, rawX, rawY, lineIndex, valueIndex);
				} else {
					throw new IllegalStateException("Cannot process points in mode: " + mode);
				}
			}
			++valueIndex;
		}
	}

	private void drawPoint(Canvas canvas, Line line, PointValue pointValue, float rawX, float rawY, float pointRadius) {
		if (ValueShape.SQUARE.equals(line.getShape())) {
			canvas.drawRect(rawX - pointRadius, rawY - pointRadius, rawX + pointRadius, rawY + pointRadius, pointPaint);
		} else if (ValueShape.CIRCLE.equals(line.getShape())) {
			canvas.drawCircle(rawX, rawY, pointRadius, pointPaint);
		} else {
			throw new IllegalArgumentException("Invalid point shape: " + line.getShape());
		}
	}

	private void highlightPoints(Canvas canvas) {
		int lineIndex = selectedValue.getFirstIndex();
		Line line = dataProvider.getLineChartData().getLines().get(lineIndex);
		drawPoints(canvas, line, lineIndex, MODE_HIGHLIGHT);
	}

	private void highlightPoint(Canvas canvas, Line line, PointValue pointValue, float rawX, float rawY, int lineIndex,
			int valueIndex) {
		if (selectedValue.getFirstIndex() == lineIndex && selectedValue.getSecondIndex() == valueIndex) {
			int pointRadius = Utils.dp2px(density, line.getPointRadius());
			pointPaint.setColor(line.getDarkenColor());
			drawPoint(canvas, line, pointValue, rawX, rawY, pointRadius + touchTolleranceMargin);
			if (line.hasLabels() || line.hasLabelsOnlyForSelected()) {
				drawLabel(canvas, line, pointValue, rawX, rawY, pointRadius + labelOffset);
			}
		}
	}

	private void drawLabel(Canvas canvas, Line line, PointValue pointValue, float rawX, float rawY, float offset) {
		final ChartComputator computator = chart.getChartComputator();
		final Rect contentRect = computator.getContentRect();
		valuesBuff[0] = pointValue.getX();
		valuesBuff[1] = pointValue.getY();

		final int numChars = line.getFormatter().formatValue(labelBuffer, valuesBuff, pointValue.getLabel());

		if (numChars == 0) {
			// No need to draw empty label
			return;
		}

		final float labelWidth = labelPaint.measureText(labelBuffer, labelBuffer.length - numChars, numChars);
		final int labelHeight = Math.abs(fontMetrics.ascent);
		float left = rawX - labelWidth / 2 - labelMargin;
		float right = rawX + labelWidth / 2 + labelMargin;

		float top;
		float bottom;

		if (pointValue.getY() >= baseValue) {
			top = rawY - offset - labelHeight - labelMargin * 2;
			bottom = rawY - offset;
		} else {
			top = rawY + offset;
			bottom = rawY + offset + labelHeight + labelMargin * 2;
		}

		if (top < contentRect.top) {
			top = rawY + offset;
			bottom = rawY + offset + labelHeight + labelMargin * 2;
		}
		if (bottom > contentRect.bottom) {
			top = rawY - offset - labelHeight - labelMargin * 2;
			bottom = rawY - offset;
		}
		if (left < contentRect.left) {
			left = rawX;
			right = rawX + labelWidth + labelMargin * 2;
		}
		if (right > contentRect.right) {
			left = rawX - labelWidth - labelMargin * 2;
			right = rawX;
		}

		labelBackgroundRect.set(left, top, right, bottom);
		drawLabelTextAndBackground(canvas, labelBuffer, labelBuffer.length - numChars, numChars, line.getDarkenColor());
	}

	private void drawArea(Canvas canvas, int transparency) {
		final ChartComputator computator = chart.getChartComputator();
		final Rect contentRect = computator.getContentRect();

		float baseRawValue = computator.computeRawY(baseValue);
		baseRawValue = Math.min(contentRect.bottom, Math.max(baseRawValue, contentRect.top));

		path.lineTo(contentRect.right, baseRawValue);
		path.lineTo(contentRect.left, baseRawValue);
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
