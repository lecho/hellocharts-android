package lecho.lib.hellocharts;

import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.Axis.AxisValue;
import lecho.lib.hellocharts.utils.Utils;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.Paint.Align;
import android.text.TextUtils;

public class AxesRenderer {

	public Paint mAxisTextPaint;
	private Paint mAxisLinePaint;
	private Path mAxisYNamePath;

	public AxesRenderer() {
		mAxisLinePaint = new Paint();
		mAxisLinePaint.setAntiAlias(true);
		mAxisLinePaint.setStyle(Paint.Style.STROKE);
		mAxisLinePaint.setStrokeWidth(1);

		mAxisTextPaint = new Paint();
		mAxisTextPaint.setAntiAlias(true);
		mAxisTextPaint.setStyle(Paint.Style.FILL);
		mAxisTextPaint.setStrokeWidth(1);

		mAxisYNamePath = new Path();
	}

	public int getAxisXHeight(Context context, Axis axisX) {
		// TODO: maybe get rid of Utils.sp2px
		int axisHeight = 0;
		mAxisTextPaint.setTextSize(Utils.sp2px(context, axisX.textSize));
		if (!axisX.values.isEmpty()) {
			final Rect textBounds = new Rect();
			mAxisTextPaint.getTextBounds(axisX.formatter.formatValue(axisX.values.get(0)), 0, 1, textBounds);
			axisHeight += textBounds.height();
		}
		if (!TextUtils.isEmpty(axisX.name)) {
			final Rect textBounds = new Rect();
			mAxisTextPaint.getTextBounds(axisX.name, 0, 1, textBounds);
			axisHeight += textBounds.height();
		}
		return axisHeight;
	}

	public int getAxisYWidth(Context context, Axis axisY) {
		int axisWidth = 0;
		mAxisTextPaint.setTextSize(Utils.sp2px(context, axisY.textSize));
		if (!axisY.values.isEmpty()) {
			final Rect textBounds = new Rect();
			final String text;
			// to simplify I assume that the widest value will be the first or the last.
			if (Math.abs(axisY.values.get(0).value) > Math.abs(axisY.values.get(axisY.values.size() - 1).value)) {
				text = axisY.formatter.formatValue(axisY.values.get(0));
			} else {
				text = axisY.formatter.formatValue(axisY.values.get(axisY.values.size() - 1));
			}
			mAxisTextPaint.getTextBounds(text, 0, 1, textBounds);
			axisWidth += textBounds.width();
		}
		if (!TextUtils.isEmpty(axisY.name)) {
			final Rect textBounds = new Rect();
			mAxisTextPaint.getTextBounds(axisY.name, 0, 1, textBounds);
			// Additional margin for axis name.
			axisWidth += textBounds.width();
		}
		return axisWidth;
	}

	public void drawAxisX(Context context, Canvas canvas, Axis axisX, ChartCalculator chartCalculator) {
		mAxisLinePaint.setColor(axisX.color);
		mAxisTextPaint.setColor(axisX.color);
		mAxisTextPaint.setTextSize(Utils.sp2px(context, axisX.textSize));
		mAxisTextPaint.setTextAlign(Align.CENTER);
		final float baselineY;
		if (TextUtils.isEmpty(axisX.name)) {
			baselineY = chartCalculator.mContentRectWithMargins.bottom + chartCalculator.mAxisXMargin;
		} else {
			baselineY = chartCalculator.mContentRectWithMargins.bottom
					+ (chartCalculator.mAxisXMargin - chartCalculator.mCommonMargin) / 2;
			canvas.drawText(axisX.name, chartCalculator.mContentRect.centerX(),
					chartCalculator.mContentRectWithMargins.bottom + chartCalculator.mAxisXMargin, mAxisTextPaint);
		}
		canvas.drawLine(chartCalculator.mContentRectWithMargins.left, chartCalculator.mContentRect.bottom,
				chartCalculator.mContentRectWithMargins.right, chartCalculator.mContentRect.bottom, mAxisLinePaint);
		for (AxisValue axisValue : axisX.values) {
			final float rawX = chartCalculator.calculateRawX(axisValue.value);
			final int rawXround = (int) rawX;
			if (rawXround >= chartCalculator.mContentRect.left && rawXround <= chartCalculator.mContentRect.right) {
				final String text = axisX.formatter.formatValue(axisValue);
				canvas.drawText(text, rawX, baselineY, mAxisTextPaint);
			}
		}
	}

	public void drawAxisY(Context context, Canvas canvas, Axis axisY, ChartCalculator chartCalculator) {
		mAxisLinePaint.setColor(axisY.color);
		mAxisTextPaint.setColor(axisY.color);
		mAxisTextPaint.setTextSize(Utils.sp2px(context, axisY.textSize));
		mAxisTextPaint.setTextAlign(Align.CENTER);
		if (!TextUtils.isEmpty(axisY.name)) {
			final float baselineY;
			if (axisY.values.isEmpty()) {
				baselineY = chartCalculator.mContentRectWithMargins.left;
			} else {
				baselineY = chartCalculator.mContentRectWithMargins.left
						- (chartCalculator.mAxisYMargin - chartCalculator.mCommonMargin) / 2
						- chartCalculator.mCommonMargin;
			}
			mAxisYNamePath.moveTo(baselineY, chartCalculator.mContentRect.bottom);
			mAxisYNamePath.lineTo(baselineY, chartCalculator.mContentRect.top);
			canvas.drawTextOnPath(axisY.name, mAxisYNamePath, 0, 0, mAxisTextPaint);
			mAxisYNamePath.reset();
		}
		mAxisTextPaint.setTextAlign(Align.RIGHT);
		for (AxisValue axisValue : axisY.values) {
			// TODO: compare axisValue with current viewport to skip calculations for values out of range
			final String text = axisY.formatter.formatValue(axisValue);
			final float rawY = chartCalculator.calculateRawY(axisValue.value);
			final int rawYround = (int) rawY;
			if (rawYround >= chartCalculator.mContentRect.top && rawYround <= chartCalculator.mContentRect.bottom) {
				canvas.drawLine(chartCalculator.mContentRectWithMargins.left, rawY,
						chartCalculator.mContentRectWithMargins.right, rawY, mAxisLinePaint);
				canvas.drawText(text, chartCalculator.mContentRectWithMargins.left, rawY, mAxisTextPaint);
			}
		}
	}
}
