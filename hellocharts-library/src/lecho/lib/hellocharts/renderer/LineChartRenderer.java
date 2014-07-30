package lecho.lib.hellocharts.renderer;

import lecho.lib.hellocharts.Chart;
import lecho.lib.hellocharts.ChartCalculator;
import lecho.lib.hellocharts.LineChartDataProvider;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.LinePoint;
import lecho.lib.hellocharts.model.SelectedValue;
import lecho.lib.hellocharts.util.Utils;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.FontMetricsInt;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.util.Log;

public class LineChartRenderer implements ChartRenderer {
	private static final float LINE_SMOOTHNES = 0.16f;
	int DEFAULT_LABEL_MARGIN_DP = 4;
	int DEFAULT_LINE_STROKE_WIDTH_DP = 3;
	int DEFAULT_TOUCH_TOLLERANCE_MARGIN_DP = 4;

	private static final int MODE_DRAW = 0;
	private static final int MODE_HIGHLIGHT = 1;

	private Chart chart;
	private LineChartDataProvider dataProvider;

	private int labelOffset;
	private int labelMaring;
	private int touchTolleranceMargin;
	private Path mLinePath = new Path();
	private Paint linePaint = new Paint();
	private Paint pointPaint = new Paint();
	private Paint labelPaint = new Paint();
	private RectF labelRect = new RectF();
	private SelectedValue selectedValue = new SelectedValue();
	private SelectedValue oldSelectedValue = new SelectedValue();
	private char[] labelBuffer = new char[32];
	private FontMetricsInt fontMetrics = new FontMetricsInt();
	protected RectF maxViewport = new RectF();

	private float density;
	private float scaledDensity;

	private boolean isMaxViewportAutoCalculated = true;
	private boolean isViewportAutoCalculated = true;

	public LineChartRenderer(Context context, Chart chart, LineChartDataProvider dataProvider) {
		this.chart = chart;
		this.dataProvider = dataProvider;
		density = context.getResources().getDisplayMetrics().density;
		scaledDensity = context.getResources().getDisplayMetrics().scaledDensity;

		labelMaring = Utils.dp2px(density, DEFAULT_LABEL_MARGIN_DP);
		labelOffset = labelMaring;
		touchTolleranceMargin = Utils.dp2px(density, DEFAULT_TOUCH_TOLLERANCE_MARGIN_DP);

		linePaint.setAntiAlias(true);
		linePaint.setStyle(Paint.Style.STROKE);
		linePaint.setStrokeWidth(Utils.dp2px(density, DEFAULT_LINE_STROKE_WIDTH_DP));

		pointPaint.setAntiAlias(true);
		pointPaint.setStyle(Paint.Style.FILL);

		labelPaint.setAntiAlias(true);
		labelPaint.setStyle(Paint.Style.FILL);
		labelPaint.setTextAlign(Align.LEFT);
		labelPaint.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
		labelPaint.setColor(Color.WHITE);
	}

	public void initRenderer() {
		if (isMaxViewportAutoCalculated) {
			calculateMaxViewport();
			chart.getChartCalculator().calculateMaxViewport(maxViewport);
			Log.e("tag", "initialize MAXviewport");
		}
		if (isViewportAutoCalculated) {
			Log.e("tag", "initialize viewport");
			chart.getChartCalculator().setCurrentViewport(maxViewport);
		}
		chart.getChartCalculator().setInternalMargin(calculateContentAreaMargin());

		labelPaint.setTextSize(Utils.sp2px(scaledDensity, chart.getChartData().getValueLabelTextSize()));
		labelPaint.getFontMetricsInt(fontMetrics);
	}

	@Override
	public void fastInitRenderer() {
		if (isMaxViewportAutoCalculated) {
			calculateMaxViewport();
			chart.getChartCalculator().calculateMaxViewport(maxViewport);
		}
		if (isViewportAutoCalculated) {
			chart.getChartCalculator().setCurrentViewport(maxViewport);
		}
	}

	@Override
	public void draw(Canvas canvas) {
		final LineChartData data = dataProvider.getLineChartData();
		for (Line line : data.lines) {
			if (line.hasLines()) {
				if (line.isSmooth()) {
					drawSmoothPath(canvas, line);
				} else {
					drawPath(canvas, line);
				}
			}
			mLinePath.reset();
		}
	}

	@Override
	public void drawUnclipped(Canvas canvas) {
		final LineChartData data = dataProvider.getLineChartData();
		int lineIndex = 0;
		for (Line line : data.lines) {
			if (line.hasPoints()) {
				drawPoints(canvas, line, lineIndex, MODE_DRAW);
			}
			mLinePath.reset();
			++lineIndex;
		}
		if (isTouched()) {
			// Redraw touched point to bring it to the front
			highlightPoints(canvas);
		}
	}

	@Override
	public boolean checkTouch(float touchX, float touchY) {
		final LineChartData data = dataProvider.getLineChartData();
		final ChartCalculator chartCalculator = chart.getChartCalculator();
		oldSelectedValue.firstIndex = selectedValue.firstIndex;
		oldSelectedValue.secondIndex = selectedValue.secondIndex;
		selectedValue.clear();
		int lineIndex = 0;
		for (Line line : data.lines) {
			int pointRadius = Utils.dp2px(density, line.getPointRadius());
			int valueIndex = 0;
			for (LinePoint linePoint : line.getPoints()) {
				final float rawValueX = chartCalculator.calculateRawX(linePoint.getX());
				final float rawValueY = chartCalculator.calculateRawY(linePoint.getY());
				if (isInArea(rawValueX, rawValueY, touchX, touchY, pointRadius + touchTolleranceMargin)) {
					selectedValue.firstIndex = lineIndex;
					selectedValue.secondIndex = valueIndex;
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

	@Override
	public boolean isTouched() {
		return selectedValue.isSet();
	}

	@Override
	public void clearTouch() {
		selectedValue.clear();
		oldSelectedValue.clear();

	}

	@Override
	public void callTouchListener() {
		chart.callTouchListener(selectedValue);
	}

	@Override
	public void setMaxViewport(RectF maxViewport) {
		if (null == maxViewport) {
			isMaxViewportAutoCalculated = true;
			fastInitRenderer();
		} else {
			isMaxViewportAutoCalculated = false;
			this.maxViewport = maxViewport;
			chart.getChartCalculator().calculateMaxViewport(maxViewport);
		}
	}

	@Override
	public RectF getMaxViewport() {
		return maxViewport;
	}

	@Override
	public void setViewport(RectF viewport) {
		if (null == viewport) {
			isViewportAutoCalculated = true;
			chart.getChartCalculator().setCurrentViewport(maxViewport);
		} else {
			isViewportAutoCalculated = false;
			chart.getChartCalculator().setCurrentViewport(viewport);
		}
	}

	@Override
	public RectF getViewport() {
		return chart.getChartCalculator().getCurrentViewport();
	}

	private void calculateMaxViewport() {
		maxViewport.set(Float.MAX_VALUE, Float.MAX_VALUE, Float.MIN_VALUE, Float.MIN_VALUE);
		LineChartData data = dataProvider.getLineChartData();
		// TODO: optimize
		for (Line line : data.lines) {
			for (LinePoint linePoint : line.getPoints()) {
				if (linePoint.getX() < maxViewport.left) {
					maxViewport.left = linePoint.getX();
				}
				if (linePoint.getX() > maxViewport.right) {
					maxViewport.right = linePoint.getX();
				}
				if (linePoint.getY() < maxViewport.top) {
					maxViewport.top = linePoint.getY();
				}
				if (linePoint.getY() > maxViewport.bottom) {
					maxViewport.bottom = linePoint.getY();
				}

			}
		}
	}

	private int calculateContentAreaMargin() {
		int contentAreaMargin = 0;
		LineChartData data = dataProvider.getLineChartData();
		for (Line line : data.lines) {
			if (line.hasPoints()) {
				int margin = line.getPointRadius() + DEFAULT_TOUCH_TOLLERANCE_MARGIN_DP;
				if (margin > contentAreaMargin) {
					contentAreaMargin = margin;
				}
			}
		}
		return Utils.dp2px(density, contentAreaMargin);
	}

	private void drawPath(Canvas canvas, final Line line) {

		final ChartCalculator chartCalculator = chart.getChartCalculator();
		int valueIndex = 0;
		for (LinePoint linePoint : line.getPoints()) {
			final float rawValueX = chartCalculator.calculateRawX(linePoint.getX());
			final float rawValueY = chartCalculator.calculateRawY(linePoint.getY());
			if (valueIndex == 0) {
				mLinePath.moveTo(rawValueX, rawValueY);
			} else {
				mLinePath.lineTo(rawValueX, rawValueY);
			}
			++valueIndex;
		}
		linePaint.setStrokeWidth(Utils.dp2px(density, line.getStrokeWidth()));
		linePaint.setColor(line.getColor());
		canvas.drawPath(mLinePath, linePaint);
		if (line.isFilled()) {
			drawArea(canvas, line.getAreaTransparency());
		}
	}

	private void drawSmoothPath(Canvas canvas, final Line line) {
		final ChartCalculator chartCalculator = chart.getChartCalculator();
		final int lineSize = line.getPoints().size();
		float previousPointX = Float.NaN;
		float previousPointY = Float.NaN;
		float currentPointX = Float.NaN;
		float currentPointY = Float.NaN;
		float nextPointX = Float.NaN;
		float nextPointY = Float.NaN;
		for (int valueIndex = 0; valueIndex < lineSize - 1; ++valueIndex) {
			if (Float.isNaN(currentPointX)) {
				LinePoint linePoint = line.getPoints().get(valueIndex);
				currentPointX = chartCalculator.calculateRawX(linePoint.getX());
				currentPointY = chartCalculator.calculateRawY(linePoint.getY());
			}
			if (Float.isNaN(previousPointX)) {
				if (valueIndex > 0) {
					LinePoint linePoint = line.getPoints().get(valueIndex - 1);
					previousPointX = chartCalculator.calculateRawX(linePoint.getX());
					previousPointY = chartCalculator.calculateRawY(linePoint.getY());
				} else {
					previousPointX = currentPointX;
					previousPointY = currentPointY;
				}
			}
			if (Float.isNaN(nextPointX)) {
				LinePoint linePoint = line.getPoints().get(valueIndex + 1);
				nextPointX = chartCalculator.calculateRawX(linePoint.getX());
				nextPointY = chartCalculator.calculateRawY(linePoint.getY());
			}
			// afterNextPoint is always new one or it is equal nextPoint.
			final float afterNextPointX;
			final float afterNextPointY;
			if (valueIndex < lineSize - 2) {
				LinePoint linePoint = line.getPoints().get(valueIndex + 2);
				afterNextPointX = chartCalculator.calculateRawX(linePoint.getX());
				afterNextPointY = chartCalculator.calculateRawY(linePoint.getY());
			} else {
				afterNextPointX = nextPointX;
				afterNextPointY = nextPointY;
			}
			// Calculate control points.
			final float firstDiffX = (nextPointX - previousPointX);
			final float firstDiffY = (nextPointY - previousPointY);
			final float secondDiffX = (afterNextPointX - currentPointX);
			final float secondDiffY = (afterNextPointY - currentPointY);
			final float firstControlPointX = currentPointX + (LINE_SMOOTHNES * firstDiffX);
			final float firstControlPointY = currentPointY + (LINE_SMOOTHNES * firstDiffY);
			final float secondControlPointX = nextPointX - (LINE_SMOOTHNES * secondDiffX);
			final float secondControlPointY = nextPointY - (LINE_SMOOTHNES * secondDiffY);
			// Move to start point.
			if (valueIndex == 0) {
				mLinePath.moveTo(currentPointX, currentPointY);
			}
			mLinePath.cubicTo(firstControlPointX, firstControlPointY, secondControlPointX, secondControlPointY,
					nextPointX, nextPointY);
			// Shift values by one to prevent recalculation of values that have
			// been already calculated.
			previousPointX = currentPointX;
			previousPointY = currentPointY;
			currentPointX = nextPointX;
			currentPointY = nextPointY;
			nextPointX = afterNextPointX;
			nextPointY = afterNextPointY;
		}
		linePaint.setStrokeWidth(Utils.dp2px(density, line.getStrokeWidth()));
		linePaint.setColor(line.getColor());
		canvas.drawPath(mLinePath, linePaint);
		if (line.isFilled()) {
			drawArea(canvas, line.getAreaTransparency());
		}
	}

	// TODO Drawing points can be done in the same loop as drawing lines but it
	// may cause problems in the future with
	// implementing point styles.
	private void drawPoints(Canvas canvas, Line line, int lineIndex, int mode) {
		final ChartCalculator chartCalculator = chart.getChartCalculator();
		pointPaint.setColor(line.getColor());
		int valueIndex = 0;
		for (LinePoint linePoint : line.getPoints()) {
			int pointRadius = Utils.dp2px(density, line.getPointRadius());
			final float rawValueX = chartCalculator.calculateRawX(linePoint.getX());
			final float rawValueY = chartCalculator.calculateRawY(linePoint.getY());
			if (chartCalculator.isWithinContentRect((int) rawValueX, (int) rawValueY)) {
				// Draw points only if they are within contentRect
				if (MODE_DRAW == mode) {
					drawPoint(canvas, rawValueX, rawValueY, pointRadius, line.getPointShape());
					if (line.hasLabels()) {
						drawLabel(canvas, line, linePoint, rawValueX, rawValueY, pointRadius + labelOffset);
					}
				} else if (MODE_HIGHLIGHT == mode) {
					highlightPoint(canvas, line, linePoint, rawValueX, rawValueY, lineIndex, valueIndex);
				} else {
					throw new IllegalStateException("Cannot process points in mode: " + mode);
				}
			}
			++valueIndex;
		}
	}

	private void drawPoint(Canvas canvas, float rawValueX, float rawValueY, float pointRadius, int pointShape) {
		if (Line.SHAPE_SQUARE == pointShape) {
			canvas.drawRect(rawValueX - pointRadius, rawValueY - pointRadius, rawValueX + pointRadius, rawValueY
					+ pointRadius, pointPaint);
		} else {
			canvas.drawCircle(rawValueX, rawValueY, pointRadius, pointPaint);
		}
	}

	private void highlightPoints(Canvas canvas) {
		int lineIndex = selectedValue.firstIndex;
		Line line = dataProvider.getLineChartData().lines.get(lineIndex);
		drawPoints(canvas, line, lineIndex, MODE_HIGHLIGHT);
	}

	private void highlightPoint(Canvas canvas, Line line, LinePoint linePoint, float rawValueX, float rawValueY,
			int lineIndex, int valueIndex) {
		int pointRadius = Utils.dp2px(density, line.getPointRadius());
		if (selectedValue.firstIndex == lineIndex && selectedValue.secondIndex == valueIndex) {
			pointPaint.setColor(line.getDarkenColor());
			drawPoint(canvas, rawValueX, rawValueY, pointRadius + touchTolleranceMargin, line.getPointShape());
			if (line.hasLabels()) {
				drawLabel(canvas, line, linePoint, rawValueX, rawValueY, pointRadius + labelOffset);
			}
		}
	}

	private void drawLabel(Canvas canvas, Line line, LinePoint linePoint, float rawValueX, float rawValueY, float offset) {
		final ChartCalculator chartCalculator = chart.getChartCalculator();
		final int nummChars = line.getFormatter().formatValue(labelBuffer, linePoint.getY());
		final float labelWidth = labelPaint.measureText(labelBuffer, labelBuffer.length - nummChars, nummChars);
		final int labelHeight = Math.abs(fontMetrics.ascent);
		float left = rawValueX - labelWidth / 2 - labelMaring;
		float right = rawValueX + labelWidth / 2 + labelMaring;
		float top = rawValueY - offset - labelHeight - labelMaring * 2;
		float bottom = rawValueY - offset;
		if (top < chartCalculator.getContentRect().top) {
			top = rawValueY + offset;
			bottom = rawValueY + offset + labelHeight + labelMaring * 2;
		}
		if (left < chartCalculator.getContentRect().left) {
			left = rawValueX;
			right = rawValueX + labelWidth + labelMaring * 2;
		}
		if (right > chartCalculator.getContentRect().right) {
			left = rawValueX - labelWidth - labelMaring * 2;
			right = rawValueX;
		}
		labelRect.set(left, top, right, bottom);
		int orginColor = labelPaint.getColor();
		labelPaint.setColor(line.getDarkenColor());
		canvas.drawRect(left, top, right, bottom, labelPaint);
		labelPaint.setColor(orginColor);
		canvas.drawText(labelBuffer, labelBuffer.length - nummChars, nummChars, left + labelMaring, bottom
				- labelMaring, labelPaint);
	}

	private void drawArea(Canvas canvas, int transparency) {
		final ChartCalculator chartCalculator = chart.getChartCalculator();
		mLinePath.lineTo(chartCalculator.getContentRect().right, chartCalculator.getContentRect().bottom);
		mLinePath.lineTo(chartCalculator.getContentRect().left, chartCalculator.getContentRect().bottom);
		mLinePath.close();
		linePaint.setStyle(Paint.Style.FILL);
		linePaint.setAlpha(transparency);
		canvas.drawPath(mLinePath, linePaint);
		linePaint.setStyle(Paint.Style.STROKE);
	}

	private boolean isInArea(float x, float y, float touchX, float touchY, float radius) {
		float diffX = touchX - x;
		float diffY = touchY - y;
		return Math.pow(diffX, 2) + Math.pow(diffY, 2) <= 2 * Math.pow(radius, 2);
	}

}
