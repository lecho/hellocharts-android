package lecho.lib.hellocharts;

import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.Line.LineStyle;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.LinePoint;
import lecho.lib.hellocharts.utils.Utils;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;

public class LineChartRenderer implements ChartRenderer {
	private static final float LINE_SMOOTHNES = 0.16f;
	private static final int DEFAULT_POPUP_MARGIN_DP = 4;
	private static final int DEFAULT_TOUCH_TOLLERANCE_DP = 4;
	private static final int MODE_DRAW = 0;
	private static final int MODE_HIGHLIGHT = 1;
	private int mPopupMargin;
	private Path mLinePath = new Path();
	private Paint mLinePaint = new Paint();
	private Paint mPointPaint = new Paint();
	private Paint popupPaint = new Paint();
	private Context mContext;
	private LineChart mChart;
	private SelectedValue mSelectedValue = new SelectedValue();

	public LineChartRenderer(Context context, LineChart chart) {
		mContext = context;
		mChart = chart;
		mPopupMargin = Utils.dp2px(context, DEFAULT_POPUP_MARGIN_DP);

		mLinePaint.setAntiAlias(true);
		mLinePaint.setStyle(Paint.Style.STROKE);

		mPointPaint.setAntiAlias(true);
		mPointPaint.setStyle(Paint.Style.FILL);

		popupPaint.setAntiAlias(true);
		popupPaint.setStyle(Paint.Style.FILL);
		popupPaint.setTextAlign(Align.LEFT);
		popupPaint.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
	}

	@Override
	public void draw(Canvas canvas) {
		final LineChartData data = mChart.getData();
		int lineIndex = 0;
		for (Line line : data.lines) {
			if (line.getStyle().hasLines()) {
				if (line.getStyle().isSmooth()) {
					drawSmoothPath(canvas, line);
				} else {
					drawPath(canvas, line);
				}
			}
			if (line.getStyle().hasPoints()) {
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
		final LineChartData data = mChart.getData();
		final ChartCalculator chartCalculator = mChart.getChartCalculator();
		int lineIndex = 0;
		for (Line line : data.lines) {
			final int touchRadius = Utils.dp2px(mContext, line.getStyle().getPointRadius()
					+ DEFAULT_TOUCH_TOLLERANCE_DP);
			int valueIndex = 0;
			for (LinePoint linePoint : line.getPoints()) {
				final float rawValueX = chartCalculator.calculateRawX(linePoint.getX());
				final float rawValueY = chartCalculator.calculateRawY(linePoint.getY());
				if (isInArea(rawValueX, rawValueY, touchX, touchY, touchRadius)) {
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
		final LineStyle style = line.getStyle();
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
		mLinePaint.setColor(style.getColor());
		mLinePaint.setStrokeWidth(Utils.dp2px(mContext, style.getLineWidth()));
		canvas.drawPath(mLinePath, mLinePaint);
		if (style.isFilled()) {
			drawArea(canvas, style);
		}
	}

	private void drawSmoothPath(Canvas canvas, final Line line) {
		final ChartCalculator chartCalculator = mChart.getChartCalculator();
		final LineStyle style = line.getStyle();
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
			// Shift values by one to prevent recalculation of values that have been already calculated.
			previousPointX = currentPointX;
			previousPointY = currentPointY;
			currentPointX = nextPointX;
			currentPointY = nextPointY;
			nextPointX = afterNextPointX;
			nextPointY = afterNextPointY;
		}
		mLinePaint.setColor(style.getColor());
		mLinePaint.setStrokeWidth(Utils.dp2px(mContext, style.getLineWidth()));
		canvas.drawPath(mLinePath, mLinePaint);
		if (style.isFilled()) {
			drawArea(canvas, style);
		}
	}

	// TODO Drawing points can be done in the same loop as drawing lines but it may cause problems in the future with
	// implementing point styles.
	private void drawPoints(Canvas canvas, Line line, int lineIndex, int mode) {
		final ChartCalculator chartCalculator = mChart.getChartCalculator();
		final LineStyle style = line.getStyle();
		mPointPaint.setColor(style.getColor());
		final int pointRadius = Utils.dp2px(mContext, style.getPointRadius());
		int valueIndex = 0;
		for (LinePoint linePoint : line.getPoints()) {
			final float rawValueX = chartCalculator.calculateRawX(linePoint.getX());
			final float rawValueY = chartCalculator.calculateRawY(linePoint.getY());
			if (MODE_DRAW == mode) {
				canvas.drawCircle(rawValueX, rawValueY, pointRadius, mPointPaint);
				if (style.hasAnnotations()) {
					drawValuePopup(canvas, style, linePoint, rawValueX, rawValueY);
				}
			} else if (MODE_HIGHLIGHT == mode) {
				highlightPoint(canvas, style, linePoint, rawValueX, rawValueY, lineIndex, valueIndex);
			} else {
				throw new IllegalStateException("Cannot process points in mode: " + mode);
			}
			++valueIndex;
		}
	}

	private void highlightPoints(Canvas canvas) {
		int lineIndex = mSelectedValue.selectedLine;
		Line line = mChart.getData().lines.get(lineIndex);
		drawPoints(canvas, line, lineIndex, MODE_HIGHLIGHT);
	}

	private void highlightPoint(Canvas canvas, LineStyle lineStyle, LinePoint linePoint, float rawValueX,
			float rawValueY, int lineIndex, int valueIndex) {
		if (mSelectedValue.selectedLine == lineIndex && mSelectedValue.selectedValue == valueIndex) {
			final int touchRadius = Utils.dp2px(mContext, lineStyle.getPointRadius() + DEFAULT_TOUCH_TOLLERANCE_DP);
			canvas.drawCircle(rawValueX, rawValueY, touchRadius, mPointPaint);
			if (lineStyle.hasAnnotations()) {
				drawValuePopup(canvas, lineStyle, linePoint, rawValueX, rawValueY);
			}
		}
	}

	private void drawValuePopup(Canvas canvas, LineStyle style, LinePoint linePoint, float rawValueX, float rawValueY) {
		final ChartCalculator chartCalculator = mChart.getChartCalculator();
		final String text = style.getLineValueFormatter().formatValue(linePoint);
		final Rect textBounds = new Rect();
		popupPaint.setTextSize(Utils.sp2px(mContext, style.getTextSize()));
		popupPaint.getTextBounds(text, 0, text.length(), textBounds);
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
		popupPaint.setColor(style.getColor());
		canvas.drawRoundRect(popup, mPopupMargin, mPopupMargin, popupPaint);
		popupPaint.setColor(style.getTextColor());
		canvas.drawText(text, left + mPopupMargin, bottom - mPopupMargin, popupPaint);
	}

	private void drawArea(Canvas canvas, LineStyle style) {
		final ChartCalculator chartCalculator = mChart.getChartCalculator();
		mLinePath.lineTo(chartCalculator.mContentRect.right, chartCalculator.mContentRect.bottom);
		mLinePath.lineTo(chartCalculator.mContentRect.left, chartCalculator.mContentRect.bottom);
		mLinePath.close();
		mLinePaint.setStyle(Paint.Style.FILL);
		mLinePaint.setAlpha(style.getAreaTransparency());
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
