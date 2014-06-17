package lecho.lib.hellocharts;

import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.LinePoint;
import lecho.lib.hellocharts.model.Line.LineStyle;
import lecho.lib.hellocharts.utils.Utils;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.Cap;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;

public class LineChartRenderer implements ChartRenderer {
	private static final float LINE_SMOOTHNES = 0.16f;
	private static final int DEFAULT_LINE_WIDTH_DP = 3;
	private static final int DEFAULT_POINT_RADIUS_DP = 6;
	private static final int DEFAULT_POINT_PRESSED_RADIUS = DEFAULT_POINT_RADIUS_DP + 4;
	private static final int DEFAULT_TOUCH_RADIUS_DP = 12;
	private static final int DEFAULT_POPUP_MARGIN_DP = 4;
	private static final int DEFAULT_TEXT_COLOR = Color.WHITE;
	private static final int DEFAULT_AREA_TRANSPARENCY = 64;
	private static final int MODE_DRAW = 0;
	private static final int MODE_HIGHLIGHT = 1;
	private int mPopupMargin;
	private Path mLinePath = new Path();
	private Paint mLinePaint = new Paint();
	private Paint mPointAndPopupPaint = new Paint();
	private float mLineWidth;
	private float mPointRadius;
	private float mPointPressedRadius;
	private float mTouchRadius;
	private Context mContext;
	private LineChart mChart;
	private SelectedValue mSelectedValue = new SelectedValue();

	public LineChartRenderer(Context context, LineChart chart) {
		mContext = context;
		mChart = chart;
		mLineWidth = Utils.dp2px(context, DEFAULT_LINE_WIDTH_DP);
		mPointRadius = Utils.dp2px(context, DEFAULT_POINT_RADIUS_DP);
		mPointPressedRadius = Utils.dp2px(context, DEFAULT_POINT_PRESSED_RADIUS);
		mPopupMargin = Utils.dp2px(context, DEFAULT_POPUP_MARGIN_DP);
		mTouchRadius = Utils.dp2px(context, DEFAULT_TOUCH_RADIUS_DP);

		mLinePaint.setAntiAlias(true);
		mLinePaint.setStyle(Paint.Style.STROKE);
		mLinePaint.setStrokeCap(Cap.ROUND);

		mPointAndPopupPaint.setAntiAlias(true);
		mPointAndPopupPaint.setStyle(Paint.Style.FILL);
		mPointAndPopupPaint.setStrokeWidth(1);
		mPointAndPopupPaint.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
	}

	@Override
	public void draw(Canvas canvas) {
		final LineChartData data = mChart.getData();
		mLinePaint.setStrokeWidth(mLineWidth);
		int lineIndex = 0;
		for (Line line : data.lines) {
			if (line.getStyle().isHasLines()) {
				if (line.getStyle().isSmooth()) {
					drawSmoothPath(canvas, line);
				} else {
					drawPath(canvas, line);
				}
			}
			if (line.getStyle().isHasPoints()) {
				drawPoints(canvas, line, lineIndex, MODE_DRAW);
			}
			if (isTouched()) {
				// Redraw touched point to bring it to the front
				drawPoints(canvas, line, lineIndex, MODE_HIGHLIGHT);
			}
			mLinePath.reset();
			++lineIndex;
		}
	}

	@Override
	public boolean checkTouch(float touchX, float touchY) {
		final LineChartData data = mChart.getData();
		final ChartCalculator chartCalculator = mChart.getChartCalculator();
		mLinePaint.setStrokeWidth(mLineWidth);
		int lineIndex = 0;
		for (Line line : data.lines) {
			int valueIndex = 0;
			for (LinePoint linePoint : line.points) {
				final float rawValueX = chartCalculator.calculateRawX(linePoint.getX());
				final float rawValueY = chartCalculator.calculateRawY(linePoint.getY());
				if (isInArea(rawValueX, rawValueY, touchX, touchY, mTouchRadius)) {
					mSelectedValue.selectedLine = lineIndex;
					mSelectedValue.selectedValue = valueIndex;
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

	private void drawPath(Canvas canvas, final Line line) {
		final ChartCalculator chartCalculator = mChart.getChartCalculator();
		int valueIndex = 0;
		for (LinePoint linePoint : line.points) {
			final float rawValueX = chartCalculator.calculateRawX(linePoint.getX());
			final float rawValueY = chartCalculator.calculateRawY(linePoint.getY());
			if (valueIndex == 0) {
				mLinePath.moveTo(rawValueX, rawValueY);
			} else {
				mLinePath.lineTo(rawValueX, rawValueY);
			}
			++valueIndex;
		}
		mLinePaint.setColor(line.getStyle().getColor());
		canvas.drawPath(mLinePath, mLinePaint);
		if (line.getStyle().isFilled()) {
			drawArea(canvas);
		}
	}

	private void drawSmoothPath(Canvas canvas, final Line line) {
		final ChartCalculator chartCalculator = mChart.getChartCalculator();
		final int lineSize = line.points.size();
		float previousPointX = Float.NaN;
		float previousPointY = Float.NaN;
		float currentPointX = Float.NaN;
		float currentPointY = Float.NaN;
		float nextPointX = Float.NaN;
		float nextPointY = Float.NaN;
		for (int valueIndex = 0; valueIndex < lineSize - 1; ++valueIndex) {
			if (Float.isNaN(currentPointX)) {
				LinePoint linePoint = line.points.get(valueIndex);
				currentPointX = chartCalculator.calculateRawX(linePoint.getX());
				currentPointY = chartCalculator.calculateRawY(linePoint.getY());
			}
			if (Float.isNaN(previousPointX)) {
				if (valueIndex > 0) {
					LinePoint linePoint = line.points.get(valueIndex - 1);
					previousPointX = chartCalculator.calculateRawX(linePoint.getX());
					previousPointY = chartCalculator.calculateRawY(linePoint.getY());
				} else {
					previousPointX = currentPointX;
					previousPointY = currentPointY;
				}
			}
			if (Float.isNaN(nextPointX)) {
				LinePoint linePoint = line.points.get(valueIndex + 1);
				nextPointX = chartCalculator.calculateRawX(linePoint.getX());
				nextPointY = chartCalculator.calculateRawY(linePoint.getY());
			}
			// afterNextPoint is always new one or it is equal nextPoint.
			final float afterNextPointX;
			final float afterNextPointY;
			if (valueIndex < lineSize - 2) {
				LinePoint linePoint = line.points.get(valueIndex + 2);
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
			// Shift values by one to prevent recalculation of values that have been already calculated.
			previousPointX = currentPointX;
			previousPointY = currentPointY;
			currentPointX = nextPointX;
			currentPointY = nextPointY;
			nextPointX = afterNextPointX;
			nextPointY = afterNextPointY;
		}
		mLinePaint.setColor(line.getStyle().getColor());
		canvas.drawPath(mLinePath, mLinePaint);

		if (line.getStyle().isFilled()) {
			drawArea(canvas);
		}
	}

	// TODO Drawing points can be done in the same loop as drawing lines but it may cause problems in the future with
	// implementing point styles.
	private void drawPoints(Canvas canvas, Line line, int lineIndex, int mode) {
		final ChartCalculator chartCalculator = mChart.getChartCalculator();
		mPointAndPopupPaint.setColor(line.getStyle().getColor());
		int valueIndex = 0;
		for (LinePoint linePoint : line.points) {
			final float rawValueX = chartCalculator.calculateRawX(linePoint.getX());
			final float rawValueY = chartCalculator.calculateRawY(linePoint.getY());
			if (MODE_DRAW == mode) {
				canvas.drawCircle(rawValueX, rawValueY, mPointRadius, mPointAndPopupPaint);
				if (line.getStyle().isHasAnnotations()) {
					drawValuePopup(canvas, line.getStyle(), linePoint, rawValueX, rawValueY);
				}
			} else if (MODE_HIGHLIGHT == mode) {
				highlightPoint(canvas, line.getStyle(), linePoint, rawValueX, rawValueY, lineIndex, valueIndex);
			} else {
				throw new IllegalStateException("Cannot process points in mode: " + mode);
			}
			++valueIndex;
		}
	}

	private void highlightPoint(Canvas canvas, LineStyle lineStyle, LinePoint linePoint, float rawValueX,
			float rawValueY, int lineIndex, int valueIndex) {
		if (mSelectedValue.selectedLine == lineIndex && mSelectedValue.selectedValue == valueIndex) {
			canvas.drawCircle(rawValueX, rawValueY, mPointPressedRadius, mPointAndPopupPaint);
			if (lineStyle.isHasAnnotations()) {
				drawValuePopup(canvas, lineStyle, linePoint, rawValueX, rawValueY);
			}
		}
	}

	private void drawValuePopup(Canvas canvas, LineStyle lineStyle, LinePoint linePoint, float rawValueX,
			float rawValueY) {
		final ChartCalculator chartCalculator = mChart.getChartCalculator();
		mPointAndPopupPaint.setTextAlign(Align.LEFT);
		mPointAndPopupPaint.setTextSize(Utils.sp2px(mContext, lineStyle.getTextSize()));
		final String text = lineStyle.getLineValueFormatter().formatValue(linePoint);
		final Rect textBounds = new Rect();
		mPointAndPopupPaint.getTextBounds(text, 0, text.length(), textBounds);
		float left = rawValueX + mPopupMargin;
		float right = rawValueX + mPopupMargin + textBounds.width() + mPopupMargin * 2;
		float top = rawValueY - mPopupMargin - textBounds.height() - mPopupMargin * 2;
		float bottom = rawValueY - mPopupMargin;
		if (top < chartCalculator.mContentRect.top) {
			top = rawValueY + mPopupMargin;
			bottom = rawValueY + mPopupMargin + textBounds.height() + mPopupMargin * 2;
		}
		if (right > chartCalculator.mContentRect.right) {
			left = rawValueX - mPopupMargin - textBounds.width() - mPopupMargin * 2;
			right = rawValueX - mPopupMargin;
		}
		final RectF popup = new RectF(left, top, right, bottom);
		canvas.drawRoundRect(popup, mPopupMargin, mPopupMargin, mPointAndPopupPaint);
		final int color = mPointAndPopupPaint.getColor();
		mPointAndPopupPaint.setColor(DEFAULT_TEXT_COLOR);
		canvas.drawText(text, left + mPopupMargin, bottom - mPopupMargin, mPointAndPopupPaint);
		mPointAndPopupPaint.setColor(color);
	}

	private void drawArea(Canvas canvas) {
		final ChartCalculator chartCalculator = mChart.getChartCalculator();
		mLinePaint.setStyle(Paint.Style.FILL);
		mLinePaint.setAlpha(DEFAULT_AREA_TRANSPARENCY);
		mLinePath.lineTo(chartCalculator.mContentRect.right, chartCalculator.mContentRect.bottom);
		mLinePath.lineTo(chartCalculator.mContentRect.left, chartCalculator.mContentRect.bottom);
		mLinePath.close();
		canvas.drawPath(mLinePath, mLinePaint);
		mLinePaint.setStyle(Paint.Style.STROKE);
	}

	private boolean isInArea(float x, float y, float touchX, float touchY, float radius) {
		float diffX = touchX - x;
		float diffY = touchY - y;
		return Math.pow(diffX, 2) + Math.pow(diffY, 2) <= 2 * Math.pow(radius, 2);
	}

	private static class SelectedValue {
		public int selectedLine;
		public int selectedValue;

		public SelectedValue() {
			clear();
		}

		public void clear() {
			this.selectedLine = Integer.MIN_VALUE;
			this.selectedLine = Integer.MIN_VALUE;
		}

		public boolean isSet() {
			if (selectedLine >= 0 && selectedValue >= 0) {
				return true;
			} else {
				return false;
			}
		}

	}

}
