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

public class AxesRenderer {
	private static final int DEFAULT_AXIS_MARGIN_DP = 2;
	private Paint mAxisTextPaint;
	private Paint mAxisLinePaint;
	private Path mAxisYNamePath;
	private Chart mChart;
	private Context mContext;
	private int axisXValueHeight;
	private int axisXNameHeight;
	private int axisYValueWidth;
	private int axisYNameHeight;
	private int axisMargin;
	private Rect textBounds = new Rect();

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
		axisMargin = Utils.dp2px(mContext, DEFAULT_AXIS_MARGIN_DP);
	}

	public int getAxisXHeight(int foo) {
		final Axis axisX = mChart.getData().getAxisX();
		axisXValueHeight = 0;
		if (!axisX.getValues().isEmpty()) {
			axisXValueHeight = Utils.sp2px(mContext, axisX.getTextSize());
		}
		axisXNameHeight = 0;
		if (!TextUtils.isEmpty(axisX.getName())) {
			axisXNameHeight = Utils.sp2px(mContext, axisX.getTextSize());
		}
		return axisXValueHeight + axisXNameHeight + axisMargin;
	}

	public int getAxisYWidth(int foo) {
		final Axis axisY = mChart.getData().getAxisY();
		mAxisTextPaint.setTextSize(Utils.sp2px(mContext, axisY.getTextSize()));
		axisYValueWidth = 0;
		if (!axisY.getValues().isEmpty()) {
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
				axisYValueWidth = textBounds.width();
			}
		}
		axisYNameHeight = 0;
		if (!TextUtils.isEmpty(axisY.getName())) {
			axisYNameHeight = Utils.sp2px(mContext, axisY.getTextSize());
		}
		return axisYValueWidth + axisYNameHeight + axisMargin;
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
			// drawing axis values
			float baseline = chartCalculator.mContentRectWithMargins.bottom + axisXValueHeight;
			for (AxisValue axisValue : axisX.getValues()) {
				final float rawX = chartCalculator.calculateRawX(axisValue.getValue());
				if (rawX >= chartCalculator.mContentRect.left && rawX <= chartCalculator.mContentRect.right) {
					canvas.drawText(axisX.getFormatter().formatValue(axisValue), rawX, baseline, mAxisTextPaint);
				}
			}
		}
		// drawing axis name
		if (!TextUtils.isEmpty(axisX.getName())) {
			float baseline = chartCalculator.mContentRectWithMargins.bottom + axisXValueHeight + axisXNameHeight
					+ axisMargin;
			canvas.drawText(axisX.getName(), chartCalculator.mContentRect.centerX(), baseline, mAxisTextPaint);
		}
	}

	public void drawAxisY(Canvas canvas) {
		final ChartCalculator chartCalculator = mChart.getChartCalculator();
		final Axis axisY = mChart.getData().getAxisY();
		mAxisLinePaint.setColor(axisY.getColor());
		mAxisTextPaint.setColor(axisY.getColor());
		mAxisTextPaint.setTextSize(Utils.sp2px(mContext, axisY.getTextSize()));
		mAxisTextPaint.setTextAlign(Align.RIGHT);
		// drawing axis values
		float baseline = chartCalculator.mContentRectWithMargins.left;
		for (AxisValue axisValue : axisY.getValues()) {
			// TODO: compare axisValue with current viewport to skip calculations for values out of range
			final float rawY = chartCalculator.calculateRawY(axisValue.getValue());
			if (rawY >= chartCalculator.mContentRect.top && rawY <= chartCalculator.mContentRect.bottom) {
				canvas.drawLine(baseline, rawY, chartCalculator.mContentRectWithMargins.right, rawY, mAxisLinePaint);
				canvas.drawText(axisY.getFormatter().formatValue(axisValue), baseline, rawY, mAxisTextPaint);
			}
		}
		// drawing axis name
		mAxisTextPaint.setTextAlign(Align.CENTER);
		if (!TextUtils.isEmpty(axisY.getName())) {
			baseline = chartCalculator.mContentRectWithMargins.left - axisYValueWidth - axisMargin;
			mAxisYNamePath.moveTo(baseline, chartCalculator.mContentRect.bottom);
			mAxisYNamePath.lineTo(baseline, chartCalculator.mContentRect.top);
			canvas.drawTextOnPath(axisY.getName(), mAxisYNamePath, 0, 0, mAxisTextPaint);
			mAxisYNamePath.reset();
		}
	}
}
