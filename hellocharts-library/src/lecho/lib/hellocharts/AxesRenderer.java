package lecho.lib.hellocharts;

import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.Axis.AxisValue;
import lecho.lib.hellocharts.utils.Utils;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Path;
import android.graphics.Rect;
import android.text.TextUtils;
import android.util.Pair;

public class AxesRenderer {

	public Paint mAxisTextPaint;
	private Paint mAxisLinePaint;
	private Path mAxisYNamePath;
	private Chart mChart;
	private Context mContext;

	public AxesRenderer(Context context, Chart chart) {
		mContext = context;
		mChart = chart;
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

	/**
	 * 
	 * @param context
	 * @param axisX
	 * @return height of axis values and axis name
	 */
	public Pair<Integer, Integer> getAxisXHeight() {
		final Axis axisX = mChart.getData().getAxisX();
		// TODO: maybe get rid of Utils.sp2px
		int valuesHeight = 0;
		mAxisTextPaint.setTextSize(Utils.sp2px(mContext, axisX.textSize));
		if (!axisX.values.isEmpty()) {
			final Rect textBounds = new Rect();
			final String text = axisX.formatter.formatValue(axisX.values.get(0));
			if (!TextUtils.isEmpty(text)) {
				mAxisTextPaint.getTextBounds(text, 0, 1, textBounds);
				valuesHeight = textBounds.height();
			}
		}
		int nameHeight = 0;
		if (!TextUtils.isEmpty(axisX.name)) {
			final Rect textBounds = new Rect();
			mAxisTextPaint.getTextBounds(axisX.name, 0, 1, textBounds);
			nameHeight = textBounds.height();
		}
		return new Pair<Integer, Integer>(valuesHeight, nameHeight);
	}

	public Pair<Integer, Integer> getAxisYWidth() {
		final Axis axisY = mChart.getData().getAxisY();
		int valuesWidth = 0;
		mAxisTextPaint.setTextSize(Utils.sp2px(mContext, axisY.textSize));
		if (!axisY.values.isEmpty()) {
			final Rect textBounds = new Rect();
			final String text;
			// to simplify I assume that the widest value will be the first or the last.
			if (Math.abs(axisY.values.get(0).value) >= Math.abs(axisY.values.get(axisY.values.size() - 1).value)) {
				text = axisY.formatter.formatValue(axisY.values.get(0));
			} else {
				text = axisY.formatter.formatValue(axisY.values.get(axisY.values.size() - 1));
			}
			if (!TextUtils.isEmpty(text)) {
				mAxisTextPaint.getTextBounds(text, 0, text.length(), textBounds);
				valuesWidth = textBounds.width();
			}
		}
		int nameWidth = 0;
		if (!TextUtils.isEmpty(axisY.name)) {
			final Rect textBounds = new Rect();
			mAxisTextPaint.getTextBounds(axisY.name, 0, 1, textBounds);
			// Y axis name is rotated by 90 degrees so use height instead of width.
			nameWidth = textBounds.height();
		}
		return new Pair<Integer, Integer>(valuesWidth, nameWidth);
	}

	public void drawAxisX(Canvas canvas) {
		final ChartCalculator chartCalculator = mChart.getChartCalculator();
		final Axis axisX = mChart.getData().getAxisX();
		mAxisLinePaint.setColor(axisX.color);
		mAxisTextPaint.setColor(axisX.color);
		mAxisTextPaint.setTextSize(Utils.sp2px(mContext, axisX.textSize));
		mAxisTextPaint.setTextAlign(Align.CENTER);
		if (axisX.values.size() > 0) {
			canvas.drawLine(chartCalculator.mContentRectWithMargins.left, chartCalculator.mContentRect.bottom,
					chartCalculator.mContentRectWithMargins.right, chartCalculator.mContentRect.bottom, mAxisLinePaint);
		}
		// drawing axis values
		float baseline = chartCalculator.mContentRectWithMargins.bottom + chartCalculator.mAxisXHeight.first;
		for (AxisValue axisValue : axisX.values) {
			final float rawX = chartCalculator.calculateRawX(axisValue.value);
			final int rawXround = (int) rawX;
			if (rawXround >= chartCalculator.mContentRect.left && rawXround <= chartCalculator.mContentRect.right) {
				final String text = axisX.formatter.formatValue(axisValue);
				if (!TextUtils.isEmpty(text)) {
					canvas.drawText(text, rawX, baseline, mAxisTextPaint);
				}
			}
		}
		// drawing axis name
		if (chartCalculator.mAxisXHeight.second > 0) {
			baseline = chartCalculator.mContentRectWithMargins.bottom + chartCalculator.mAxisXMargin;
			canvas.drawText(axisX.name, chartCalculator.mContentRect.centerX(), baseline, mAxisTextPaint);
		}
	}

	public void drawAxisY(Canvas canvas) {
		final ChartCalculator chartCalculator = mChart.getChartCalculator();
		final Axis axisY = mChart.getData().getAxisY();
		mAxisLinePaint.setColor(axisY.color);
		mAxisTextPaint.setColor(axisY.color);
		mAxisTextPaint.setTextSize(Utils.sp2px(mContext, axisY.textSize));
		// drawing axis values
		mAxisTextPaint.setTextAlign(Align.RIGHT);
		float baseline = chartCalculator.mContentRectWithMargins.left;
		for (AxisValue axisValue : axisY.values) {
			// TODO: compare axisValue with current viewport to skip calculations for values out of range
			final String text = axisY.formatter.formatValue(axisValue);
			final float rawY = chartCalculator.calculateRawY(axisValue.value);
			final int rawYround = (int) rawY;
			if (rawYround >= chartCalculator.mContentRect.top && rawYround <= chartCalculator.mContentRect.bottom) {
				canvas.drawLine(baseline, rawY, chartCalculator.mContentRectWithMargins.right, rawY, mAxisLinePaint);
				if (!TextUtils.isEmpty(text)) {
					canvas.drawText(text, baseline, rawY, mAxisTextPaint);
				}
			}
		}
		// drawing axis name
		mAxisTextPaint.setTextAlign(Align.CENTER);
		if (chartCalculator.mAxisYWidth.second > 0) {
			baseline = chartCalculator.mContentRectWithMargins.left - chartCalculator.mAxisYMargin
					+ chartCalculator.mAxisYWidth.second;
			mAxisYNamePath.moveTo(baseline, chartCalculator.mContentRect.bottom);
			mAxisYNamePath.lineTo(baseline, chartCalculator.mContentRect.top);
			canvas.drawTextOnPath(axisY.name, mAxisYNamePath, 0, 0, mAxisTextPaint);
			mAxisYNamePath.reset();
		}
	}
}
