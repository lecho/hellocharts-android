package lecho.lib.hellocharts.renderer;

import lecho.lib.hellocharts.Chart;
import lecho.lib.hellocharts.ChartCalculator;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.util.Utils;
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
		mAxisTextPaint.setTextSize(Utils.sp2px(mContext, axisX.getTextSize()));
		if (!axisX.getValues().isEmpty()) {
			final Rect textBounds = new Rect();
			final String text = axisX.getFormatter().formatValue(axisX.getValues().get(0));
			if (!TextUtils.isEmpty(text)) {
				mAxisTextPaint.getTextBounds(text, 0, 1, textBounds);
				valuesHeight = textBounds.height();
			}
		}
		int nameHeight = 0;
		if (!TextUtils.isEmpty(axisX.getName())) {
			final Rect textBounds = new Rect();
			mAxisTextPaint.getTextBounds(axisX.getName(), 0, 1, textBounds);
			nameHeight = textBounds.height();
		}
		return new Pair<Integer, Integer>(valuesHeight, nameHeight);
	}

	public Pair<Integer, Integer> getAxisYWidth() {
		final Axis axisY = mChart.getData().getAxisY();
		int valuesWidth = 0;
		mAxisTextPaint.setTextSize(Utils.sp2px(mContext, axisY.getTextSize()));
		if (!axisY.getValues().isEmpty()) {
			final Rect textBounds = new Rect();
			final String text;
			// to simplify I assume that the widest value will be the first or the last.
			if (Math.abs(axisY.getValues().get(0).getValue()) >= Math.abs(axisY.getValues()
					.get(axisY.getValues().size() - 1).getValue())) {
				text = axisY.getFormatter().formatValue(axisY.getValues().get(0));
			} else {
				text = axisY.getFormatter().formatValue(axisY.getValues().get(axisY.getValues().size() - 1));
			}
			if (!TextUtils.isEmpty(text)) {
				mAxisTextPaint.getTextBounds(text, 0, text.length(), textBounds);
				valuesWidth = textBounds.width();
			}
		}
		int nameWidth = 0;
		if (!TextUtils.isEmpty(axisY.getName())) {
			final Rect textBounds = new Rect();
			mAxisTextPaint.getTextBounds(axisY.getName(), 0, 1, textBounds);
			// Y axis name is rotated by 90 degrees so use height instead of width.
			nameWidth = textBounds.height();
		}
		return new Pair<Integer, Integer>(valuesWidth, nameWidth);
	}

	public void drawAxisX(Canvas canvas) {
		final ChartCalculator chartCalculator = mChart.getChartCalculator();
		final Axis axisX = mChart.getData().getAxisX();
		mAxisLinePaint.setColor(axisX.getColor());
		mAxisTextPaint.setColor(axisX.getColor());
		mAxisTextPaint.setTextSize(Utils.sp2px(mContext, axisX.getTextSize()));
		mAxisTextPaint.setTextAlign(Align.CENTER);
		if (axisX.getValues().size() > 0) {
			canvas.drawLine(chartCalculator.mContentRectWithMargins.left, chartCalculator.mContentRect.bottom,
					chartCalculator.mContentRectWithMargins.right, chartCalculator.mContentRect.bottom, mAxisLinePaint);
		}
		// drawing axis values
		float baseline = chartCalculator.mContentRectWithMargins.bottom + chartCalculator.mAxisXHeight.first;
		for (AxisValue axisValue : axisX.getValues()) {
			final float rawX = chartCalculator.calculateRawX(axisValue.getValue());
			final int rawXround = (int) rawX;
			if (rawXround >= chartCalculator.mContentRect.left && rawXround <= chartCalculator.mContentRect.right) {
				final String text = axisX.getFormatter().formatValue(axisValue);
				if (!TextUtils.isEmpty(text)) {
					canvas.drawText(text, rawX, baseline, mAxisTextPaint);
				}
			}
		}
		// drawing axis name
		if (chartCalculator.mAxisXHeight.second > 0) {
			baseline = chartCalculator.mContentRectWithMargins.bottom + chartCalculator.mAxisXMargin;
			canvas.drawText(axisX.getName(), chartCalculator.mContentRect.centerX(), baseline, mAxisTextPaint);
		}
	}

	public void drawAxisY(Canvas canvas) {
		final ChartCalculator chartCalculator = mChart.getChartCalculator();
		final Axis axisY = mChart.getData().getAxisY();
		mAxisLinePaint.setColor(axisY.getColor());
		mAxisTextPaint.setColor(axisY.getColor());
		mAxisTextPaint.setTextSize(Utils.sp2px(mContext, axisY.getTextSize()));
		// drawing axis values
		mAxisTextPaint.setTextAlign(Align.RIGHT);
		float baseline = chartCalculator.mContentRectWithMargins.left;
		for (AxisValue axisValue : axisY.getValues()) {
			// TODO: compare axisValue with current viewport to skip calculations for values out of range
			final String text = axisY.getFormatter().formatValue(axisValue);
			final float rawY = chartCalculator.calculateRawY(axisValue.getValue());
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
			canvas.drawTextOnPath(axisY.getName(), mAxisYNamePath, 0, 0, mAxisTextPaint);
			mAxisYNamePath.reset();
		}
	}
}
