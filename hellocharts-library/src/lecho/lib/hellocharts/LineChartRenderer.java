package lecho.lib.hellocharts;

import lecho.lib.hellocharts.model.AnimatedPoint;
import lecho.lib.hellocharts.model.Data;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.Point;
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

public class LineChartRenderer {

	private static final float LINE_SMOOTHNES = 0.16f;
	private static final int DEFAULT_LINE_WIDTH_DP = 3;
	private static final int DEFAULT_POINT_RADIUS_DP = 6;
	private static final int DEFAULT_POINT_PRESSED_RADIUS = DEFAULT_POINT_RADIUS_DP + 4;
	private static final int DEFAULT_POPUP_MARGIN = 4;
	private static final int DEFAULT_TEXT_COLOR = Color.WHITE;
	private static final int DEFAULT_AREA_TRANSPARENCY = 64;
	private int mPopupMargin;
	private Path mLinePath = new Path();
	private Paint mLinePaint = new Paint();
	private Paint mTextPaint = new Paint();
	private float mLineWidth;
	private float mPointRadius;
	private float mPointPressedRadius;
	private Context mContext;

	public LineChartRenderer(Context context) {
		mContext = context;
		mLineWidth = Utils.dp2px(context, DEFAULT_LINE_WIDTH_DP);
		mPointRadius = Utils.dp2px(context, DEFAULT_POINT_RADIUS_DP);
		mPointPressedRadius = Utils.dp2px(context, DEFAULT_POINT_PRESSED_RADIUS);
		mPopupMargin = Utils.dp2px(context, DEFAULT_POPUP_MARGIN);

		mLinePaint.setAntiAlias(true);
		mLinePaint.setStyle(Paint.Style.STROKE);
		mLinePaint.setStrokeCap(Cap.ROUND);

		mTextPaint.setAntiAlias(true);
		mTextPaint.setStyle(Paint.Style.FILL);
		mTextPaint.setStrokeWidth(1);
	}

	public void drawLines(Canvas canvas, Data data, ChartCalculator chartCalculator) {
		mLinePaint.setStrokeWidth(mLineWidth);
		for (Line line : data.lines) {
			if (line.isSmooth) {
				drawSmoothPath(canvas, line, chartCalculator);
			} else {
				drawPath(canvas, line, chartCalculator);
			}
			if (line.hasPoints) {
				drawPoints(canvas, data, chartCalculator);
			}
			mLinePath.reset();
		}
	}

	private void drawPath(Canvas canvas, final Line line, ChartCalculator chartCalculator) {
		int valueIndex = 0;
		for (AnimatedPoint animatedPoint : line.animatedPoints) {
			final float rawValueX = chartCalculator.calculateRawX(animatedPoint.point.x);
			final float rawValueY = chartCalculator.calculateRawY(animatedPoint.point.y);
			if (valueIndex == 0) {
				mLinePath.moveTo(rawValueX, rawValueY);
			} else {
				mLinePath.lineTo(rawValueX, rawValueY);
			}
			++valueIndex;
		}
		mLinePaint.setColor(line.color);
		canvas.drawPath(mLinePath, mLinePaint);

		if (line.isFilled) {
			drawArea(canvas, chartCalculator);
		}
	}

	private void drawSmoothPath(Canvas canvas, final Line line, ChartCalculator chartCalculator) {
		final int lineSize = line.animatedPoints.size();
		float previousPointX = Float.NaN;
		float previousPointY = Float.NaN;
		float currentPointX = Float.NaN;
		float currentPointY = Float.NaN;
		float nextPointX = Float.NaN;
		float nextPointY = Float.NaN;
		for (int valueIndex = 0; valueIndex < lineSize - 1; ++valueIndex) {
			if (Float.isNaN(currentPointX)) {
				AnimatedPoint animatedPoint = line.animatedPoints.get(valueIndex);
				currentPointX = chartCalculator.calculateRawX(animatedPoint.point.x);
				currentPointY = chartCalculator.calculateRawY(animatedPoint.point.y);
			}
			if (Float.isNaN(previousPointX)) {
				if (valueIndex > 0) {
					AnimatedPoint animatedPoint = line.animatedPoints.get(valueIndex - 1);
					previousPointX = chartCalculator.calculateRawX(animatedPoint.point.x);
					previousPointY = chartCalculator.calculateRawY(animatedPoint.point.y);
				} else {
					previousPointX = currentPointX;
					previousPointY = currentPointY;
				}
			}
			if (Float.isNaN(nextPointX)) {
				AnimatedPoint animatedPoint = line.animatedPoints.get(valueIndex + 1);
				nextPointX = chartCalculator.calculateRawX(animatedPoint.point.x);
				nextPointY = chartCalculator.calculateRawY(animatedPoint.point.y);
			}
			// afterNextPoint is always new one or it is equal nextPoint.
			final float afterNextPointX;
			final float afterNextPointY;
			if (valueIndex < lineSize - 2) {
				AnimatedPoint animatedPoint = line.animatedPoints.get(valueIndex + 2);
				afterNextPointX = chartCalculator.calculateRawX(animatedPoint.point.x);
				afterNextPointY = chartCalculator.calculateRawY(animatedPoint.point.y);
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
		mLinePaint.setColor(line.color);
		canvas.drawPath(mLinePath, mLinePaint);

		if (line.isFilled) {
			drawArea(canvas, chartCalculator);
		}
	}

	// TODO Drawing points can be done in the same loop as drawing lines but it may cause problems in the future. Reuse
	// calculated X/Y;
	private void drawPoints(Canvas canvas, Data data, ChartCalculator chartCalculator) {
		for (Line line : data.lines) {
			mTextPaint.setColor(line.color);
			mTextPaint.setTextSize(Utils.sp2px(mContext, line.textSize));
			for (AnimatedPoint animatedPoint : line.animatedPoints) {
				final float rawValueX = chartCalculator.calculateRawX(animatedPoint.point.x);
				final float rawValueY = chartCalculator.calculateRawY(animatedPoint.point.y);
				canvas.drawCircle(rawValueX, rawValueY, mPointRadius, mTextPaint);
				if (line.hasValuesPopups) {
					drawValuePopup(chartCalculator, canvas, line, animatedPoint.point, rawValueX, rawValueY);
				}
			}
		}
		// if (mSelectedLineIndex >= 0 && mSelectedPointIndex >= 0) {
		// final Line line = mData.lines.get(mSelectedLineIndex);
		// final AnimatedPoint animatedPoint = line.animatedPoints.get(mSelectedPointIndex);
		// final float rawValueX = chartCalculator.calculateRawX(animatedPoint.point.x);
		// final float rawValueY = chartCalculator.calculateRawY(animatedPoint.point.y);
		// mTextPaint.setColor(line.color);
		// canvas.drawCircle(rawValueX, rawValueY, mPointPressedRadius, mTextPaint);
		// // if (mPopupsOn) {
		// // drawValuePopup(canvas, mPopupTextMargin, line, animatedPoint.point, rawValueX, rawValueY);
		// // }
		// }
	}

	private void drawValuePopup(ChartCalculator chartCalculator, Canvas canvas, Line line, Point value,
			float rawValueX, float rawValueY) {
		mTextPaint.setTextAlign(Align.LEFT);
		final String text = line.formatter.formatValue(value);
		final Rect textBounds = new Rect();
		mTextPaint.getTextBounds(text, 0, text.length(), textBounds);
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
		canvas.drawRoundRect(popup, mPopupMargin, mPopupMargin, mTextPaint);
		final int color = mTextPaint.getColor();
		mTextPaint.setColor(DEFAULT_TEXT_COLOR);
		canvas.drawText(text, left + mPopupMargin, bottom - mPopupMargin, mTextPaint);
		mTextPaint.setColor(color);
	}

	private void drawArea(Canvas canvas, ChartCalculator chartCalculator) {
		mLinePaint.setStyle(Paint.Style.FILL);
		mLinePaint.setAlpha(DEFAULT_AREA_TRANSPARENCY);
		mLinePath.lineTo(chartCalculator.mContentRect.right, chartCalculator.mContentRect.bottom);
		mLinePath.lineTo(chartCalculator.mContentRect.left, chartCalculator.mContentRect.bottom);
		mLinePath.close();
		canvas.drawPath(mLinePath, mLinePaint);
		mLinePaint.setStyle(Paint.Style.STROKE);
	}

}
