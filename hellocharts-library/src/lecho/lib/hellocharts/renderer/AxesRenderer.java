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
import android.graphics.Rect;
import android.text.TextUtils;

public class AxesRenderer {
	private static final int DEFAULT_AXIS_MARGIN_DP = 4;
	private Paint mAxisTextPaint;
	private Paint mAxisLinePaint;
	private Chart mChart;
	private Context mContext;
	private int axisXValueHeight;
	private int axisXNameHeight;
	private int axisYValueWidth;
	private int axisYNameHeight;
	private int axisMargin;
	private Rect textBounds = new Rect();
	// For now don't draw lines for X axis
	// private float[] axisXDrawBuffer;
	private final AxisStops axisXStopsBuffer = new AxisStops();
	private float[] axisYDrawBuffer;
	private final AxisStops axisYStopsBuffer = new AxisStops();
	private int mMaxLabelWidth;
	private int mLabelHeight;

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
		mAxisLinePaint.setTextSize(32);

		axisMargin = Utils.dp2px(mContext, DEFAULT_AXIS_MARGIN_DP);

	}

	public void initRenderer() {
		// I draw lines only for Y axis so initialize drawValues only for that axis.
		axisYDrawBuffer = new float[mChart.getData().getAxisY().getValues().size() * 4];
		mAxisLinePaint.setTextSize(Utils.dp2px(mContext, 16));
		mLabelHeight = (int) Math.abs(mAxisTextPaint.getFontMetrics().top);
		mMaxLabelWidth = (int) mAxisTextPaint.measureText("0000");
	}

	public int getAxisXHeight() {
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

	public int getAxisYWidth() {
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
		drawAxisXAuto(canvas);
		// if (axisX.getValues().size() > 0) {
		// canvas.drawLine(chartCalculator.mContentRectWithMargins.left, chartCalculator.mContentRect.bottom,
		// chartCalculator.mContentRectWithMargins.right, chartCalculator.mContentRect.bottom, mAxisLinePaint);
		// // drawing axis values
		// float baseline = chartCalculator.mContentRectWithMargins.bottom + axisXValueHeight;
		// for (AxisValue axisValue : axisX.getValues()) {
		// final float rawX = chartCalculator.calculateRawX(axisValue.getValue());
		// if (rawX >= chartCalculator.mContentRect.left && rawX <= chartCalculator.mContentRect.right) {
		// canvas.drawText(axisX.getFormatter().formatValue(axisValue), rawX, baseline, mAxisTextPaint);
		// }
		// }
		// }
		// // drawing axis name
		// if (!TextUtils.isEmpty(axisX.getName())) {
		// float baseline = chartCalculator.mContentRectWithMargins.bottom + axisXValueHeight + axisXNameHeight
		// + axisMargin;
		// canvas.drawText(axisX.getName(), chartCalculator.mContentRect.centerX(), baseline, mAxisTextPaint);
		// }
	}

	public void drawAxisY(Canvas canvas) {
		final ChartCalculator chartCalculator = mChart.getChartCalculator();
		final Axis axisY = mChart.getData().getAxisY();
		mAxisLinePaint.setColor(axisY.getColor());
		mAxisTextPaint.setColor(axisY.getColor());
		mAxisTextPaint.setTextSize(Utils.sp2px(mContext, axisY.getTextSize()));
		mAxisTextPaint.setTextAlign(Align.RIGHT);
		// drawing axis values
		float rawX = chartCalculator.mContentRectWithMargins.left;
		int i = 0;
		for (AxisValue axisValue : axisY.getValues()) {
			final float value = axisValue.getValue();
			if (value <= chartCalculator.mCurrentViewport.bottom && value >= chartCalculator.mCurrentViewport.top) {
				final float rawY = chartCalculator.calculateRawY(value);
				axisYDrawBuffer[i++] = rawX;
				axisYDrawBuffer[i++] = rawY;
				axisYDrawBuffer[i++] = chartCalculator.mContentRectWithMargins.right;
				axisYDrawBuffer[i++] = rawY;

				canvas.drawText(axisY.getFormatter().formatValue(axisValue), rawX, rawY, mAxisTextPaint);
			}
		}
		canvas.drawLines(axisYDrawBuffer, 0, i, mAxisLinePaint);
		// drawing axis name
		mAxisTextPaint.setTextAlign(Align.CENTER);
		if (!TextUtils.isEmpty(axisY.getName())) {
			rawX = chartCalculator.mContentRectWithMargins.left - axisYValueWidth - axisMargin;
			canvas.save();
			canvas.rotate(-90, chartCalculator.mContentRect.centerY(), chartCalculator.mContentRect.centerY());
			canvas.drawText(axisY.getName(), chartCalculator.mContentRect.centerY(), rawX, mAxisTextPaint);
			canvas.restore();
		}
	}

	private void drawAxisXAuto(Canvas canvas) {
		ChartCalculator chartCalculator = mChart.getChartCalculator();
		final Axis axisX = mChart.getData().getAxisX();
		computeAxisStops(chartCalculator.mCurrentViewport.left, chartCalculator.mCurrentViewport.right,
				chartCalculator.mContentRect.width() / mMaxLabelWidth / 2, axisXStopsBuffer);
		mAxisTextPaint.setTextAlign(Paint.Align.CENTER);
		float rawY = chartCalculator.mContentRectWithMargins.bottom + axisXValueHeight;
		int i;
		for (i = 0; i < axisXStopsBuffer.numStops; ++i) {
			float rawX = chartCalculator.calculateRawX(axisXStopsBuffer.stops[i]);
			String text = axisX.getFormatter().formatValue(new AxisValue(axisXStopsBuffer.stops[i]));
			canvas.drawText(text, rawX, rawY, mAxisTextPaint);
		}
		// drawing axis name
		if (!TextUtils.isEmpty(axisX.getName())) {
			rawY = chartCalculator.mContentRectWithMargins.bottom + axisXValueHeight + axisXNameHeight + axisMargin;
			canvas.drawText(axisX.getName(), chartCalculator.mContentRect.centerX(), rawY, mAxisTextPaint);
		}
	}

	// /**
	// * Draws the chart axes and labels onto the canvas.
	// */
	// private void drawAxes(Canvas canvas) {
	// // Computes axis stops (in terms of numerical value and position on screen)
	// int i;
	//
	// computeAxisStops(mCurrentViewport.left, mCurrentViewport.right, mContentRect.width() / mMaxLabelWidth / 2,
	// mXStopsBuffer);
	// computeAxisStops(mCurrentViewport.top, mCurrentViewport.bottom, mContentRect.height() / mLabelHeight / 2,
	// mYStopsBuffer);
	//
	// // Avoid unnecessary allocations during drawing. Re-use allocated
	// // arrays and only reallocate if the number of stops grows.
	// if (mAxisXPositionsBuffer.length < mXStopsBuffer.numStops) {
	// mAxisXPositionsBuffer = new float[mXStopsBuffer.numStops];
	// }
	// if (mAxisYPositionsBuffer.length < mYStopsBuffer.numStops) {
	// mAxisYPositionsBuffer = new float[mYStopsBuffer.numStops];
	// }
	// if (mAxisXLinesBuffer.length < mXStopsBuffer.numStops * 4) {
	// mAxisXLinesBuffer = new float[mXStopsBuffer.numStops * 4];
	// }
	// if (mAxisYLinesBuffer.length < mYStopsBuffer.numStops * 4) {
	// mAxisYLinesBuffer = new float[mYStopsBuffer.numStops * 4];
	// }
	//
	// // Compute positions
	// for (i = 0; i < mXStopsBuffer.numStops; i++) {
	// mAxisXPositionsBuffer[i] = getDrawX(mXStopsBuffer.stops[i]);
	// }
	// for (i = 0; i < mYStopsBuffer.numStops; i++) {
	// mAxisYPositionsBuffer[i] = getDrawY(mYStopsBuffer.stops[i]);
	// }
	//
	// // Draws grid lines using drawLines (faster than individual drawLine calls)
	// for (i = 0; i < mXStopsBuffer.numStops; i++) {
	// mAxisXLinesBuffer[i * 4 + 0] = (float) Math.floor(mAxisXPositionsBuffer[i]);
	// mAxisXLinesBuffer[i * 4 + 1] = mContentRect.top;
	// mAxisXLinesBuffer[i * 4 + 2] = (float) Math.floor(mAxisXPositionsBuffer[i]);
	// mAxisXLinesBuffer[i * 4 + 3] = mContentRect.bottom;
	// }
	// canvas.drawLines(mAxisXLinesBuffer, 0, mXStopsBuffer.numStops * 4, mGridPaint);
	//
	// for (i = 0; i < mYStopsBuffer.numStops; i++) {
	// mAxisYLinesBuffer[i * 4 + 0] = mContentRect.left;
	// mAxisYLinesBuffer[i * 4 + 1] = (float) Math.floor(mAxisYPositionsBuffer[i]);
	// mAxisYLinesBuffer[i * 4 + 2] = mContentRect.right;
	// mAxisYLinesBuffer[i * 4 + 3] = (float) Math.floor(mAxisYPositionsBuffer[i]);
	// }
	// canvas.drawLines(mAxisYLinesBuffer, 0, mYStopsBuffer.numStops * 4, mGridPaint);
	//
	// // Draws X labels
	// int labelOffset;
	// int labelLength;
	// mLabelTextPaint.setTextAlign(Paint.Align.CENTER);
	// for (i = 0; i < mXStopsBuffer.numStops; i++) {
	// // Do not use String.format in high-performance code such as onDraw code.
	// labelLength = formatFloat(mLabelBuffer, mXStopsBuffer.stops[i], mXStopsBuffer.decimals);
	// labelOffset = mLabelBuffer.length - labelLength;
	// canvas.drawText(mLabelBuffer, labelOffset, labelLength, mAxisXPositionsBuffer[i], mContentRect.bottom
	// + mLabelHeight + mLabelSeparation, mLabelTextPaint);
	// }
	//
	// // Draws Y labels
	// mLabelTextPaint.setTextAlign(Paint.Align.RIGHT);
	// for (i = 0; i < mYStopsBuffer.numStops; i++) {
	// // Do not use String.format in high-performance code such as onDraw code.
	// labelLength = formatFloat(mLabelBuffer, mYStopsBuffer.stops[i], mYStopsBuffer.decimals);
	// labelOffset = mLabelBuffer.length - labelLength;
	// canvas.drawText(mLabelBuffer, labelOffset, labelLength, mContentRect.left - mLabelSeparation,
	// mAxisYPositionsBuffer[i] + mLabelHeight / 2, mLabelTextPaint);
	// }
	// }

	/**
	 * Computes the set of axis labels to show given start and stop boundaries and an ideal number of stops between
	 * these boundaries.
	 * 
	 * @param start
	 *            The minimum extreme (e.g. the left edge) for the axis.
	 * @param stop
	 *            The maximum extreme (e.g. the right edge) for the axis.
	 * @param steps
	 *            The ideal number of stops to create. This should be based on available screen space; the more space
	 *            there is, the more stops should be shown.
	 * @param outStops
	 *            The destination {@link AxisStops} object to populate.
	 */
	private static void computeAxisStops(float start, float stop, int steps, AxisStops outStops) {
		double range = stop - start;
		if (steps == 0 || range <= 0) {
			outStops.stops = new float[] {};
			outStops.numStops = 0;
			return;
		}

		double rawInterval = range / steps;
		double interval = Utils.roundToOneSignificantFigure(rawInterval);
		double intervalMagnitude = Math.pow(10, (int) Math.log10(interval));
		int intervalSigDigit = (int) (interval / intervalMagnitude);
		if (intervalSigDigit > 5) {
			// Use one order of magnitude higher, to avoid intervals like 0.9 or 90
			interval = Math.floor(10 * intervalMagnitude);
		}

		double first = Math.ceil(start / interval) * interval;
		double last = Utils.nextUp(Math.floor(stop / interval) * interval);

		double f;
		int i;
		int n = 0;
		for (f = first; f <= last; f += interval) {
			++n;
		}

		outStops.numStops = n;

		if (outStops.stops.length < n) {
			// Ensure stops contains at least numStops elements.
			outStops.stops = new float[n];
		}

		for (f = first, i = 0; i < n; f += interval, ++i) {
			outStops.stops[i] = (float) f;
		}

		if (interval < 1) {
			outStops.decimals = (int) Math.ceil(-Math.log10(interval));
		} else {
			outStops.decimals = 0;
		}
	}

	/**
	 * A simple class representing axis label values used only for auto generated axes.
	 * 
	 */
	private static class AxisStops {
		float[] stops = new float[] {};
		int numStops;
		int decimals;
	}
}
