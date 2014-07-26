package lecho.lib.hellocharts.renderer;

import lecho.lib.hellocharts.ChartCalculator;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.LinePoint;
import lecho.lib.hellocharts.model.SelectedValue;
import lecho.lib.hellocharts.util.Utils;
import lecho.lib.hellocharts.view.LineChartView;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.FontMetricsInt;
import android.graphics.Color;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Typeface;

public class LineChartRenderer implements ChartRenderer {
	private static final float LINE_SMOOTHNES = 0.16f;
	int DEFAULT_TEXT_SIZE_SP = 12;
	int DEFAULT_LABEL_MARGIN_DP = 4;
	int DEFAULT_CONTENT_AREA_MARGIN_DP = 4;
	int DEFAULT_AXES_NAME_MARGIN_DP = 4;
	int DEFAULT_LINE_STROKE_WIDTH_DP = 3;
	int DEFAULT_POINT_RADIUS_DP = 6;
	int DEFAULT_TOUCH_TOLLERANCE_MARGIN_DP = 4;
	int DEFAULT_SUBCOLUMN_SPACING_DP = 1;
	int DEFAULT_COLUMN_TOUCH_ADDITIONAL_WIDTH_DP = 2;

	private static final int MODE_DRAW = 0;
	private static final int MODE_HIGHLIGHT = 1;
	private Context context;
	private int labelOffset;
	private int labelMaring;
	private int defaultPointRadius;
	private int touchTolleranceMargin;
	private Path mLinePath = new Path();
	private Paint linePaint = new Paint();
	private Paint pointPaint = new Paint();
	private Paint labelPaint = new Paint();
	private RectF labelRect = new RectF();
	private LineChartView chart;
	private SelectedValue mSelectedValue = new SelectedValue();
	private char[] labelBuffer = new char[32];
	private FontMetricsInt fontMetrics = new FontMetricsInt();
	protected RectF dataBoundaries = new RectF();

	public LineChartRenderer(Context context, LineChartView chart) {
		this.context = context;
		this.chart = chart;

		labelMaring = Utils.dp2px(context, DEFAULT_LABEL_MARGIN_DP);
		labelOffset = labelMaring;
		defaultPointRadius = Utils.dp2px(context, DEFAULT_POINT_RADIUS_DP);
		touchTolleranceMargin = Utils.dp2px(context, DEFAULT_TOUCH_TOLLERANCE_MARGIN_DP);

		linePaint.setAntiAlias(true);
		linePaint.setStyle(Paint.Style.STROKE);
		linePaint.setStrokeWidth(Utils.dp2px(context, DEFAULT_LINE_STROKE_WIDTH_DP));

		pointPaint.setAntiAlias(true);
		pointPaint.setStyle(Paint.Style.FILL);

		labelPaint.setAntiAlias(true);
		labelPaint.setStyle(Paint.Style.FILL);
		labelPaint.setTextAlign(Align.LEFT);
		labelPaint.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
		labelPaint.setColor(Color.WHITE);
		labelPaint.setTextSize(Utils.sp2px(context, DEFAULT_TEXT_SIZE_SP));
		labelPaint.getFontMetricsInt(fontMetrics);
	}

	@Override
	public void draw(Canvas canvas) {
		final LineChartData data = chart.getData();
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
		final LineChartData data = chart.getData();
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
		final LineChartData data = chart.getData();
		final ChartCalculator chartCalculator = chart.getChartCalculator();
		int lineIndex = 0;
		for (Line line : data.lines) {
			int valueIndex = 0;
			for (LinePoint linePoint : line.getPoints()) {
				final float rawValueX = chartCalculator.calculateRawX(linePoint.getX());
				final float rawValueY = chartCalculator.calculateRawY(linePoint.getY());
				if (isInArea(rawValueX, rawValueY, touchX, touchY, defaultPointRadius + touchTolleranceMargin)) {
					mSelectedValue.firstIndex = lineIndex;
					mSelectedValue.secondIndex = valueIndex;
				}
				++valueIndex;
			}
			++lineIndex;
		}
		return isTouched();
	}

	@Override
	public boolean isTouched() {
		return mSelectedValue.isSet();
	}

	@Override
	public void clearTouch() {
		mSelectedValue.clear();

	}

	@Override
	public void callTouchListener() {
		chart.callTouchListener(mSelectedValue);
	}

	public SelectedValue getSelectedValue() {
		return mSelectedValue;
	}

	// public void calculateDataBoundaries() {
	// dataBoundaries.set(Float.MAX_VALUE, Float.MIN_VALUE, Float.MIN_VALUE, Float.MAX_VALUE);
	// LineChartData data = chart.getData();
	// // TODO: optimize
	// for (Line line : data.lines) {
	// for (LinePoint linePoint : line.getPoints()) {
	// if (linePoint.getX() < dataBoundaries.left) {
	// dataBoundaries.left = linePoint.getX();
	// }
	// if (linePoint.getX() > dataBoundaries.right) {
	// dataBoundaries.right = linePoint.getX();
	// }
	// if (linePoint.getY() < dataBoundaries.bottom) {
	// dataBoundaries.bottom = linePoint.getY();
	// }
	// if (linePoint.getY() > dataBoundaries.top) {
	// dataBoundaries.top = linePoint.getY();
	// }
	//
	// }
	// }
	// }

	// private void calculateContentAreaMargin() {
	// int contentAreaMargin = 0;
	// LineChartData data = chart.getData();
	// for (Line line : data.lines) {
	// if (line.hasPoints()) {
	// int margin = line.getPointRadius() + touchTolleranceMargin;
	// if (margin > contentAreaMargin) {
	// contentAreaMargin = margin;
	// }
	// if (contentAreaMargin == 0) {
	// contentAreaMargin = defaultPointRadius;
	// }
	// }
	// }
	// }

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
			final float rawValueX = chartCalculator.calculateRawX(linePoint.getX());
			final float rawValueY = chartCalculator.calculateRawY(linePoint.getY());
			if (chartCalculator.isWithinContentRect((int) rawValueX, (int) rawValueY)) {
				// Draw points only if they are within contentRect
				if (MODE_DRAW == mode) {
					canvas.drawCircle(rawValueX, rawValueY, defaultPointRadius, pointPaint);
					if (line.hasLabels()) {
						drawLabel(canvas, line, linePoint, rawValueX, rawValueY, defaultPointRadius + labelOffset);
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

	private void highlightPoints(Canvas canvas) {
		int lineIndex = mSelectedValue.firstIndex;
		Line line = chart.getData().lines.get(lineIndex);
		drawPoints(canvas, line, lineIndex, MODE_HIGHLIGHT);
	}

	private void highlightPoint(Canvas canvas, Line line, LinePoint linePoint, float rawValueX, float rawValueY,
			int lineIndex, int valueIndex) {
		if (mSelectedValue.firstIndex == lineIndex && mSelectedValue.secondIndex == valueIndex) {
			pointPaint.setColor(line.getDarkenColor());
			canvas.drawCircle(rawValueX, rawValueY, defaultPointRadius + touchTolleranceMargin, pointPaint);
			if (line.hasLabels()) {
				drawLabel(canvas, line, linePoint, rawValueX, rawValueY, defaultPointRadius + labelOffset);
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
		if (top < chartCalculator.mContentRect.top) {
			top = rawValueY + offset;
			bottom = rawValueY + offset + labelHeight + labelMaring * 2;
		}
		if (left < chartCalculator.mContentRect.left) {
			left = rawValueX;
			right = rawValueX + labelWidth + labelMaring * 2;
		}
		if (right > chartCalculator.mContentRect.right) {
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
		mLinePath.lineTo(chartCalculator.mContentRect.right, chartCalculator.mContentRect.bottom);
		mLinePath.lineTo(chartCalculator.mContentRect.left, chartCalculator.mContentRect.bottom);
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
