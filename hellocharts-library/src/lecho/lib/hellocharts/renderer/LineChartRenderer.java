package lecho.lib.hellocharts.renderer;

import lecho.lib.hellocharts.Chart;
import lecho.lib.hellocharts.ChartComputator;
import lecho.lib.hellocharts.LineChartDataProvider;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.util.Utils;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Typeface;
import android.graphics.PorterDuff.Mode;
import android.graphics.Rect;
import android.graphics.RectF;

public class LineChartRenderer extends AbstractChartRenderer {
	private static final float LINE_SMOOTHNES = 0.16f;
	private static final int DEFAULT_LINE_STROKE_WIDTH_DP = 3;
	private static final int DEFAULT_TOUCH_TOLLERANCE_MARGIN_DP = 4;

	private static final int MODE_DRAW = 0;
	private static final int MODE_HIGHLIGHT = 1;

	private LineChartDataProvider dataProvider;

	private int contentRectCheckPrecission;

	private int touchTolleranceMargin;
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
	 * Indicates when secondBitmap needs to be drawn, that is when chart uses Bezier's curves or filled area. If set to
	 * false secondBitmap will not be drawn and therefore chart will be rendered much faster. This flag is set in
	 * {@link #calculateMaxViewport()} method.
	 */
	private boolean needSecondBitmap = false;

	/**
	 * Simple Path implementation that uses drawLines() method.
	 */
	private PathCompat pathCompat = new PathCompat();

	public LineChartRenderer(Context context, Chart chart, LineChartDataProvider dataProvider) {
		super(context, chart);
		this.dataProvider = dataProvider;

		touchTolleranceMargin = Utils.dp2px(density, DEFAULT_TOUCH_TOLLERANCE_MARGIN_DP);

		linePaint.setAntiAlias(true);
		linePaint.setStyle(Paint.Style.STROKE);
		linePaint.setStrokeWidth(Utils.dp2px(density, DEFAULT_LINE_STROKE_WIDTH_DP));

		pointPaint.setAntiAlias(true);
		pointPaint.setStyle(Paint.Style.FILL);

		contentRectCheckPrecission = Utils.dp2px(density, 4);

	}

	@Override
	public void initMaxViewport() {
		calculateMaxViewport();
		chart.getChartComputator().setMaxViewport(tempMaxViewport);
	}

	@Override
	public void initDataMeasuremetns() {
		chart.getChartComputator().setInternalMargin(calculateContentAreaMargin());

		Typeface typeface = chart.getChartData().getValueLabelTypeface();
		if (null != typeface) {
			labelPaint.setTypeface(typeface);
		}
		labelPaint.setTextSize(Utils.sp2px(scaledDensity, chart.getChartData().getValueLabelTextSize()));
		labelPaint.getFontMetricsInt(fontMetrics);

		Rect contentRect = chart.getChartComputator().getContentRect();
		final int width = contentRect.width();
		final int height = contentRect.height();
		if (width > 0 && height > 0 && needSecondBitmap) {
			secondBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
			secondCanvas.setBitmap(secondBitmap);
		}
	}

	@Override
	public void draw(Canvas canvas) {
		final LineChartData data = dataProvider.getLineChartData();

		if (needSecondBitmap) {
			secondCanvas.drawColor(Color.TRANSPARENT, Mode.CLEAR);
		}

		for (Line line : data.getLines()) {
			if (line.hasLines()) {
				if (line.isSmooth()) {
					drawSmoothPath(canvas, line);
				} else {
					drawPath(canvas, line);
				}
			}
		}

		if (needSecondBitmap) {
			final ChartComputator computator = chart.getChartComputator();
			final Rect contentRect = computator.getContentRect();
			canvas.drawBitmap(secondBitmap, contentRect.left, contentRect.top, null);
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
		// Check if touch is still on the same value, if not return false.
		if (oldSelectedValue.isSet() && selectedValue.isSet() && !oldSelectedValue.equals(selectedValue)) {
			return false;
		}
		return isTouched();
	}

	private void calculateMaxViewport() {
		tempMaxViewport.set(Float.MAX_VALUE, Float.MIN_VALUE, Float.MIN_VALUE, Float.MAX_VALUE);
		LineChartData data = dataProvider.getLineChartData();

		needSecondBitmap = false;
		for (Line line : data.getLines()) {
			if (line.isFilled() || line.isSmooth()) {
				needSecondBitmap = true;
			}
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
		linePaint.setStrokeWidth(Utils.dp2px(density, line.getStrokeWidth()));
		linePaint.setColor(line.getColor());
		int valueIndex = 0;
		for (PointValue pointValue : line.getValues()) {
			float rawX = computator.computeRawX(pointValue.getX());
			float rawY = computator.computeRawY(pointValue.getY());
			if (valueIndex == 0) {
				pathCompat.moveTo(rawX, rawY);
			} else {
				pathCompat.lineTo(canvas, linePaint, rawX, rawY);
			}

			if (line.isFilled()) {
				// For filled line use path.
				rawX = computator.computeRelativeRawX(pointValue.getX());
				rawY = computator.computeRelativeRawY(pointValue.getY());
				if (valueIndex == 0) {
					path.moveTo(rawX, rawY);
				} else {
					path.lineTo(rawX, rawY);
				}
			}
			++valueIndex;
		}

		pathCompat.drawPath(canvas, linePaint);
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
		linePaint.setStrokeWidth(Utils.dp2px(density, line.getStrokeWidth()));
		linePaint.setColor(line.getColor());
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
				currentPointX = computator.computeRelativeRawX(linePoint.getX());
				currentPointY = computator.computeRelativeRawY(linePoint.getY());
			}
			if (Float.isNaN(previousPointX)) {
				if (valueIndex > 0) {
					PointValue linePoint = line.getValues().get(valueIndex - 1);
					previousPointX = computator.computeRelativeRawX(linePoint.getX());
					previousPointY = computator.computeRelativeRawY(linePoint.getY());
				} else {
					previousPointX = currentPointX;
					previousPointY = currentPointY;
				}
			}

			if (Float.isNaN(prepreviousPointX)) {
				if (valueIndex > 1) {
					PointValue linePoint = line.getValues().get(valueIndex - 2);
					prepreviousPointX = computator.computeRelativeRawX(linePoint.getX());
					prepreviousPointY = computator.computeRelativeRawY(linePoint.getY());
				} else {
					prepreviousPointX = previousPointX;
					prepreviousPointY = previousPointY;
				}
			}

			// nextPoint is always new one or it is equal currentPoint.
			if (valueIndex < lineSize - 1) {
				PointValue linePoint = line.getValues().get(valueIndex + 1);
				nextPointX = computator.computeRelativeRawX(linePoint.getX());
				nextPointY = computator.computeRelativeRawY(linePoint.getY());
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
		final ChartComputator computator = chart.getChartComputator();
		pointPaint.setColor(line.getColor());
		int valueIndex = 0;
		for (PointValue pointValue : line.getValues()) {
			int pointRadius = Utils.dp2px(density, line.getPointRadius());
			final float rawX = computator.computeRawX(pointValue.getX());
			final float rawY = computator.computeRawY(pointValue.getY());
			if (computator.isWithinContentRect((int) rawX, (int) rawY, contentRectCheckPrecission)) {
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
		if (Line.SHAPE_SQUARE == line.getPointShape()) {
			canvas.drawRect(rawX - pointRadius, rawY - pointRadius, rawX + pointRadius, rawY + pointRadius, pointPaint);
		} else {
			canvas.drawCircle(rawX, rawY, pointRadius, pointPaint);
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
		final int nummChars = line.getFormatter().formatValue(labelBuffer, pointValue.getY(), pointValue.getLabel());
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
		final ChartComputator computator = chart.getChartComputator();
		final Rect contentRect = computator.getContentRect();
		float baseRelativeRawValue;
		final float baseValue = dataProvider.getLineChartData().getBaseValue();
		if (Float.isNaN(baseValue)) {
			baseRelativeRawValue = contentRect.height();
		} else {
			baseRelativeRawValue = computator.computeRelativeRawY(baseValue);
			baseRelativeRawValue = Math.min(contentRect.height(), Math.max(baseRelativeRawValue, 0));
		}
		path.lineTo(contentRect.width(), baseRelativeRawValue);
		path.lineTo(0, baseRelativeRawValue);
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

	/**
	 * PathCompat uses Canvas.drawLines instead Canvas.drawPath. Supports only normal lines. Warning!:line has to be
	 * continuous and doesn't support filled area, dashed lines etc. For complete implementation with Bezier's curves
	 * see gist {@link https://gist.github.com/lecho/a903e68fe7cccac131d0}
	 */
	private static class PathCompat {
		/**
		 * Initial buffer size, enough for 64 line segments = 65 points
		 */
		private static final int DEFAULT_BUFFER_SIZE = 256;

		/**
		 * Buffer for point coordinates to avoid calling drawLine for every line segment, instead call drawLines.
		 */
		private float[] buffer = new float[DEFAULT_BUFFER_SIZE];

		/**
		 * Number of points in buffer, index where put next line segment coordinate.
		 */
		private int bufferIndex = 0;

		public void moveTo(float x, float y) {
			if (bufferIndex != 0) {
				// Move too only works for starting point.
				return;
			}
			buffer[bufferIndex++] = x;
			buffer[bufferIndex++] = y;
		}

		public void lineTo(Canvas canvas, Paint paint, float x, float y) {

			addLineToBuffer(x, y);

			drawLinesIfNeeded(canvas, paint);
		}

		private void drawLinesIfNeeded(Canvas canvas, Paint paint) {
			if (bufferIndex == buffer.length) {
				// Buffer full, draw lines and remember last point as the first point in buffer.
				canvas.drawLines(buffer, 0, bufferIndex, paint);
				final float lastX = buffer[bufferIndex - 2];
				final float lastY = buffer[bufferIndex - 1];
				bufferIndex = 0;
				buffer[bufferIndex++] = lastX;
				buffer[bufferIndex++] = lastY;
			}
		}

		private void addLineToBuffer(float x, float y) {
			if (bufferIndex == 0) {
				// No moveTo, set starting point to 0,0.
				buffer[bufferIndex++] = 0;
				buffer[bufferIndex++] = 0;
			}

			if (bufferIndex == 2) {
				// First segment.
				buffer[bufferIndex++] = x;
				buffer[bufferIndex++] = y;
			} else {
				final float lastX = buffer[bufferIndex - 2];
				final float lastY = buffer[bufferIndex - 1];
				buffer[bufferIndex++] = lastX;
				buffer[bufferIndex++] = lastY;
				buffer[bufferIndex++] = x;
				buffer[bufferIndex++] = y;
			}
		}

		/**
		 * Resets internal state of PathCompat and prepare it to draw next line.
		 */
		public void reset() {
			bufferIndex = 0;
		}

		/**
		 * Draw line segment if there is any not drawn before.
		 * 
		 */
		public void drawPath(Canvas canvas, Paint paint) {
			if (bufferIndex >= 4) {
				canvas.drawLines(buffer, 0, bufferIndex, paint);
			}
			reset();
		}

	}

}
