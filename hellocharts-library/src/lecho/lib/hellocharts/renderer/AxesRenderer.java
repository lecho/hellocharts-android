package lecho.lib.hellocharts.renderer;

import lecho.lib.hellocharts.ChartComputator;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Viewport;
import lecho.lib.hellocharts.util.AxisAutoStops;
import lecho.lib.hellocharts.util.Utils;
import lecho.lib.hellocharts.view.Chart;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.FontMetricsInt;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.text.TextUtils;

/**
 * Default axes renderer. Can draw maximum four axes - two horizontal(top/bottom) and two vertical(left/right).
 * 
 * @author Leszek Wach
 */
public class AxesRenderer {
	private static final int DEFAULT_AXIS_MARGIN_DP = 2;
	// Axis positions and also *Tabs indexes.
	private static final int TOP = 0;
	private static final int LEFT = 1;
	private static final int RIGHT = 2;
	private static final int BOTTOM = 3;

	private static final char[] labelWidthChars = new char[] { '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0',
			'0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0' };

	private Chart chart;
	private int axisMargin;

	// 4 text paints for every axis, not all have to be used, indexed with TOP, LEFT, RIGHT, BOTTOM.
	private Paint[] textPaintTab = new Paint[] { new Paint(), new Paint(), new Paint(), new Paint() };
	private Paint linePaint;

	private int[] axisValuesToDrawNumTab = new int[4];
	private float[][] axisRawStopsTab = new float[4][0];
	private float[][] axisAutoStopsToDrawTab = new float[4][0];
	private AxisValue[][] axisValuesToDrawTab = new AxisValue[4][0];
	private float[][] axisLinesDrawBufferTab = new float[4][0];
	private AxisAutoStops[] axisAutoStopsBufferTab = new AxisAutoStops[] { new AxisAutoStops(), new AxisAutoStops(),
			new AxisAutoStops(), new AxisAutoStops() };

	private float[] axisFixedCoordinateTab = new float[4];
	private float[] axisBaselineTab = new float[4];
	private float[] axisSeparationLineTab = new float[4];
	private int[] axisLabelWidthTab = new int[4];
	private int[] axisLabelTextAscentTab = new int[4];
	private int[] axisLabelTextDescentTab = new int[4];
	private FontMetricsInt[] fontMetricsTab = new FontMetricsInt[] { new FontMetricsInt(), new FontMetricsInt(),
			new FontMetricsInt(), new FontMetricsInt() };

	private float[] valuesBuff = new float[1];
	private char[] labelBuffer = new char[32];

	private float density;
	private float scaledDensity;

	public AxesRenderer(Context context, Chart chart) {
		this.chart = chart;

		density = context.getResources().getDisplayMetrics().density;
		scaledDensity = context.getResources().getDisplayMetrics().scaledDensity;
		axisMargin = Utils.dp2px(density, DEFAULT_AXIS_MARGIN_DP);

		linePaint = new Paint();
		linePaint.setAntiAlias(true);
		linePaint.setStyle(Paint.Style.STROKE);
		linePaint.setStrokeWidth(1);

		for (Paint paint : textPaintTab) {
			paint.setAntiAlias(true);
		}
	}

	public void initAxesAttributes() {

		int axisXTopHeight = initAxisAttributes(chart.getChartData().getAxisXTop(), TOP);
		int axisXBottomHeight = initAxisAttributes(chart.getChartData().getAxisXBottom(), BOTTOM);

		int axisYLeftWidth = initAxisAttributes(chart.getChartData().getAxisYLeft(), LEFT);
		int axisYRightWidth = initAxisAttributes(chart.getChartData().getAxisYRight(), RIGHT);

		chart.getChartComputator().setAxesMargin(axisYLeftWidth, axisXTopHeight, axisYRightWidth, axisXBottomHeight);
	}

	public void drawInBackground(Canvas canvas) {
		Axis axis = chart.getChartData().getAxisYLeft();
		if (null != axis) {
			prepareAxisVertical(axis, LEFT);

			drawAxisVerticalLines(canvas, axis, LEFT);
		}

		axis = chart.getChartData().getAxisYRight();
		if (null != axis) {
			prepareAxisVertical(axis, RIGHT);

			drawAxisVerticalLines(canvas, axis, RIGHT);
		}

		axis = chart.getChartData().getAxisXBottom();
		if (null != axis) {
			prepareAxisHorizontal(axis, BOTTOM);

			drawAxisHorizontalLines(canvas, axis, BOTTOM);
		}

		axis = chart.getChartData().getAxisXTop();
		if (null != axis) {
			prepareAxisHorizontal(axis, TOP);

			drawAxisHorizontalLines(canvas, axis, TOP);
		}
	}

	public void drawInForeground(Canvas canvas) {
		Axis axis = chart.getChartData().getAxisYLeft();
		if (null != axis) {
			drawAxisVerticalLabels(canvas, axis, LEFT);
		}

		axis = chart.getChartData().getAxisYRight();
		if (null != axis) {
			drawAxisVerticalLabels(canvas, axis, RIGHT);
		}

		axis = chart.getChartData().getAxisXBottom();
		if (null != axis) {
			drawAxisHorizontalLabels(canvas, axis, BOTTOM);
		}

		axis = chart.getChartData().getAxisXTop();
		if (null != axis) {
			drawAxisHorizontalLabels(canvas, axis, TOP);
		}
	}

	/**
	 * Initialize attributes and measurement for axes(left, right, top, bottom); Returns axis measured width( for left
	 * and right) or height(for top and bottom).
	 */
	private int initAxisAttributes(Axis axis, int position) {
		if (null == axis) {
			return 0;
		}

		Typeface typeface = axis.getTypeface();
		if (null != typeface) {
			textPaintTab[position].setTypeface(typeface);
		}

		textPaintTab[position].setColor(axis.getTextColor());
		textPaintTab[position].setTextSize(Utils.sp2px(scaledDensity, axis.getTextSize()));
		textPaintTab[position].getFontMetricsInt(fontMetricsTab[position]);

		axisLabelTextAscentTab[position] = Math.abs(fontMetricsTab[position].ascent);
		axisLabelTextDescentTab[position] = Math.abs(fontMetricsTab[position].descent);
		axisLabelWidthTab[position] = (int) textPaintTab[position].measureText(labelWidthChars, 0,
				axis.getMaxLabelChars());

		int result = 0;

		if (LEFT == position || RIGHT == position) {

			int width = 0;

			// If auto-generated or has manual values add height for value labels.
			if ((axis.isAutoGenerated() || !axis.getValues().isEmpty()) && !axis.isInside()) {
				width += axisLabelWidthTab[position];
				width += axisMargin;
			}

			// If has name add height for axis name text.
			if (!TextUtils.isEmpty(axis.getName())) {
				width += axisLabelTextAscentTab[position];
				width += axisLabelTextDescentTab[position];
				width += axisMargin;
			}

			result = width;

		} else if (TOP == position || BOTTOM == position) {

			int height = 0;

			// If auto-generated or has manual values add height for value labels.
			if ((axis.isAutoGenerated() || !axis.getValues().isEmpty()) && !axis.isInside()) {
				height += axisLabelTextAscentTab[position];
				height += axisLabelTextDescentTab[position];
				height += axisMargin;
			}

			// If has name add height for axis name text.
			if (!TextUtils.isEmpty(axis.getName())) {
				height += axisLabelTextAscentTab[position];
				height += axisLabelTextDescentTab[position];
				height += axisMargin;
			}

			result = height;

		} else {
			throw new IllegalArgumentException("Invalid axis position: " + position);
		}

		return result;
	}

	// ********** HORIZONTAL X AXES ****************

	private void prepareAxisHorizontal(Axis axis, int position) {
		final ChartComputator computator = chart.getChartComputator();

		textPaintTab[position].setTextAlign(Align.CENTER);

		if (BOTTOM == position) {
			if (axis.isInside()) {
				axisFixedCoordinateTab[position] = computator.getContentRectWithMargins().bottom - axisMargin
						- axisLabelTextDescentTab[position];
				axisBaselineTab[position] = computator.getContentRectWithMargins().bottom
						+ axisLabelTextAscentTab[position] + axisMargin;
			} else {
				axisFixedCoordinateTab[position] = computator.getContentRectWithMargins().bottom
						+ axisLabelTextAscentTab[position] + axisMargin;
				axisBaselineTab[position] = axisFixedCoordinateTab[position] + axisMargin
						+ axisLabelTextAscentTab[position] + axisLabelTextDescentTab[position];
			}

			axisSeparationLineTab[position] = computator.getContentRect().bottom;

		} else if (TOP == position) {
			if (axis.isInside()) {
				axisFixedCoordinateTab[position] = computator.getContentRectWithMargins().top + axisMargin
						+ axisLabelTextAscentTab[position];
				axisBaselineTab[position] = computator.getContentRectWithMargins().top - axisMargin
						- axisLabelTextDescentTab[position];
			} else {
				axisFixedCoordinateTab[position] = computator.getContentRectWithMargins().top - axisMargin
						- axisLabelTextDescentTab[position];
				axisBaselineTab[position] = axisFixedCoordinateTab[position] - axisMargin
						- axisLabelTextAscentTab[position] - axisLabelTextDescentTab[position];
			}

			axisSeparationLineTab[position] = computator.getContentRect().top;

		} else {
			throw new IllegalArgumentException("Invalid position for horizontal axis: " + position);
		}

		if (axis.isAutoGenerated()) {
			prepareAxisHorizontalAuto(axis, position);
		} else {
			prepareAxisHorizontalCustom(axis, position);
		}

	}

	private void prepareAxisHorizontalCustom(Axis axis, int position) {
		final ChartComputator computator = chart.getChartComputator();
		final Viewport maxViewport = computator.getMaximumViewport();
		final Viewport visibleViewport = computator.getVisibleViewport();
		final Rect contentRect = computator.getContentRect();
		float scale = maxViewport.width() / visibleViewport.width();

		final int module = (int) Math.ceil(axis.getValues().size() * axisLabelWidthTab[position]
				/ (contentRect.width() * scale));

		if (axis.hasLines() && axisLinesDrawBufferTab[position].length < axis.getValues().size() * 4) {
			axisLinesDrawBufferTab[position] = new float[axis.getValues().size() * 4];
		}

		if (axisRawStopsTab[position].length < axis.getValues().size()) {
			axisRawStopsTab[position] = new float[axis.getValues().size()];
			axisValuesToDrawTab[position] = new AxisValue[axis.getValues().size()];
		}

		int valueIndex = 0;
		int stopsToDrawIndex = 0;

		for (AxisValue axisValue : axis.getValues()) {
			final float value = axisValue.getValue();

			// Draw axis values that area within visible viewport.
			if (value >= visibleViewport.left && value <= visibleViewport.right) {

				// Draw axis values that have 0 module value, this will hide some labels if there is no place for them.
				if (0 == valueIndex % module) {

					final float rawX = computator.computeRawX(axisValue.getValue());

					if (checkRawX(contentRect, rawX, axis.isInside(), position)) {

						axisRawStopsTab[position][stopsToDrawIndex] = rawX;
						axisValuesToDrawTab[position][stopsToDrawIndex] = axisValue;

						++stopsToDrawIndex;

					}
				}
				// If within viewport - increment valueIndex;
				++valueIndex;
			}
		}

		axisValuesToDrawNumTab[position] = stopsToDrawIndex;

	}

	private void prepareAxisHorizontalAuto(Axis axis, int position) {
		final ChartComputator computator = chart.getChartComputator();
		final Viewport visibleViewport = computator.getVisibleViewport();
		final Rect contentRect = computator.getContentRect();

		Utils.computeAxisStops(visibleViewport.left, visibleViewport.right, contentRect.width()
				/ axisLabelWidthTab[position] / 2, axisAutoStopsBufferTab[position]);

		if (axis.hasLines() && axisLinesDrawBufferTab[position].length < axisAutoStopsBufferTab[position].numStops * 4) {
			axisLinesDrawBufferTab[position] = new float[axisAutoStopsBufferTab[position].numStops * 4];
		}

		if (axisRawStopsTab[position].length < axisAutoStopsBufferTab[position].numStops) {
			axisRawStopsTab[position] = new float[axisAutoStopsBufferTab[position].numStops];
			axisAutoStopsToDrawTab[position] = new float[axisAutoStopsBufferTab[position].numStops];
		}

		int stopsToDrawIndex = 0;

		for (int i = 0; i < axisAutoStopsBufferTab[position].numStops; ++i) {

			final float rawX = computator.computeRawX(axisAutoStopsBufferTab[position].stops[i]);

			if (checkRawX(contentRect, rawX, axis.isInside(), position)) {

				axisRawStopsTab[position][stopsToDrawIndex] = rawX;
				axisAutoStopsToDrawTab[position][stopsToDrawIndex] = axisAutoStopsBufferTab[position].stops[i];

				++stopsToDrawIndex;
			}
		}

		axisValuesToDrawNumTab[position] = stopsToDrawIndex;

	}

	private void drawAxisHorizontalLines(Canvas canvas, Axis axis, int position) {
		if (!axis.hasLines()) {
			return;
		}

		final Rect contentRectMargins = chart.getChartComputator().getContentRectWithMargins();

		int stopsToDrawIndex = 0;

		for (; stopsToDrawIndex < axisValuesToDrawNumTab[position]; ++stopsToDrawIndex) {

			axisLinesDrawBufferTab[position][stopsToDrawIndex * 4 + 0] = axisRawStopsTab[position][stopsToDrawIndex];
			axisLinesDrawBufferTab[position][stopsToDrawIndex * 4 + 1] = contentRectMargins.top;
			axisLinesDrawBufferTab[position][stopsToDrawIndex * 4 + 2] = axisRawStopsTab[position][stopsToDrawIndex];
			axisLinesDrawBufferTab[position][stopsToDrawIndex * 4 + 3] = contentRectMargins.bottom;
		}

		linePaint.setColor(axis.getLineColor());
		canvas.drawLines(axisLinesDrawBufferTab[position], 0, stopsToDrawIndex * 4, linePaint);
	}

	private void drawAxisHorizontalLabels(Canvas canvas, Axis axis, int position) {
		final Rect contentRectMargins = chart.getChartComputator().getContentRectWithMargins();

		int stopsToDrawIndex = 0;

		for (; stopsToDrawIndex < axisValuesToDrawNumTab[position]; ++stopsToDrawIndex) {

			final int numChars;

			if (axis.isAutoGenerated()) {
				valuesBuff[0] = axisAutoStopsToDrawTab[position][stopsToDrawIndex];

				numChars = axis.getFormatter().formatValue(labelBuffer, valuesBuff, null);
			} else {
				valuesBuff[0] = axisValuesToDrawTab[position][stopsToDrawIndex].getValue();

				numChars = axis.getFormatter().formatValue(labelBuffer, valuesBuff,
						axisValuesToDrawTab[position][stopsToDrawIndex].getLabel());
			}

			canvas.drawText(labelBuffer, labelBuffer.length - numChars, numChars,
					axisRawStopsTab[position][stopsToDrawIndex], axisFixedCoordinateTab[position],
					textPaintTab[position]);

		}

		// Draw separation line with the same color as axis text. Only horizontal axes have separation lines.
		canvas.drawLine(contentRectMargins.left, axisSeparationLineTab[position], contentRectMargins.right,
				axisSeparationLineTab[position], textPaintTab[position]);

		// Drawing axis name
		if (!TextUtils.isEmpty(axis.getName())) {
			canvas.drawText(axis.getName(), contentRectMargins.centerX(), axisBaselineTab[position],
					textPaintTab[position]);
		}
	}

	/**
	 * For axis inside chart area this method checks if there is place to draw axis label. If yes returns true,
	 * otherwise false.
	 */
	private boolean checkRawX(Rect rect, float rawX, boolean axisInside, int position) {
		if (axisInside) {
			float margin = axisLabelWidthTab[position] / 2;
			if (rawX >= rect.left + margin && rawX <= rect.right - margin) {
				return true;
			} else {
				return false;
			}
		}

		return true;

	}

	// ********** VERTICAL Y AXES ****************

	private void prepareAxisVertical(Axis axis, int position) {
		final ChartComputator computator = chart.getChartComputator();

		if (LEFT == position) {
			textPaintTab[position].setTextAlign(Align.RIGHT);

			if (axis.isInside()) {
				axisFixedCoordinateTab[position] = computator.getContentRectWithMargins().left + axisMargin
						+ axisLabelWidthTab[position];
				axisBaselineTab[position] = computator.getContentRectWithMargins().left - axisMargin
						- axisLabelTextDescentTab[position];
			} else {
				axisFixedCoordinateTab[position] = computator.getContentRectWithMargins().left - axisMargin;
				axisBaselineTab[position] = axisFixedCoordinateTab[position] - axisLabelWidthTab[position] - axisMargin
						- axisLabelTextDescentTab[position];
			}

		} else if (RIGHT == position) {
			textPaintTab[position].setTextAlign(Align.LEFT);

			if (axis.isInside()) {
				axisFixedCoordinateTab[position] = computator.getContentRectWithMargins().right - axisMargin
						- axisLabelWidthTab[position];
				axisBaselineTab[position] = computator.getContentRectWithMargins().right + axisMargin
						+ axisLabelTextAscentTab[position];
			} else {
				axisFixedCoordinateTab[position] = computator.getContentRectWithMargins().right + axisMargin;
				axisBaselineTab[position] = axisFixedCoordinateTab[position] + axisLabelWidthTab[position] + axisMargin
						+ axisLabelTextAscentTab[position];
			}
		} else {
			throw new IllegalArgumentException("Invalid position for horizontal axis: " + position);
		}

		// drawing axis values
		if (axis.isAutoGenerated()) {
			prepareAxisVerticalAuto(axis, position);
		} else {
			prepareAxisVerticalCustom(axis, position);
		}

	}

	private void prepareAxisVerticalCustom(Axis axis, int position) {
		final ChartComputator computator = chart.getChartComputator();
		final Viewport maxViewport = computator.getMaximumViewport();
		final Viewport visibleViewport = computator.getVisibleViewport();
		final Rect contentRect = computator.getContentRect();
		float scale = maxViewport.height() / visibleViewport.height();

		final int module = (int) Math.ceil(axis.getValues().size() * axisLabelTextAscentTab[position] * 2
				/ (contentRect.height() * scale));

		if (axis.hasLines() && axisLinesDrawBufferTab[position].length < axis.getValues().size() * 4) {
			axisLinesDrawBufferTab[position] = new float[axis.getValues().size() * 4];
		}

		if (axisRawStopsTab[position].length < axis.getValues().size()) {
			axisRawStopsTab[position] = new float[axis.getValues().size()];
			axisValuesToDrawTab[position] = new AxisValue[axis.getValues().size()];
		}

		int valueIndex = 0;
		int stopsToDrawIndex = 0;

		for (AxisValue axisValue : axis.getValues()) {
			final float value = axisValue.getValue();

			// Draw axis values that area within visible viewport.
			if (value >= visibleViewport.bottom && value <= visibleViewport.top) {

				// Draw axis values that have 0 module value, this will hide some labels if there is no place for them.
				if (0 == valueIndex % module) {

					final float rawY = computator.computeRawY(value);

					if (checkRawY(contentRect, rawY, axis.isInside(), position)) {

						axisRawStopsTab[position][stopsToDrawIndex] = rawY;
						axisValuesToDrawTab[position][stopsToDrawIndex] = axisValue;

						++stopsToDrawIndex;
					}
				}
				// If within viewport - increment valueIndex;
				++valueIndex;
			}
		}

		axisValuesToDrawNumTab[position] = stopsToDrawIndex;
	}

	private void prepareAxisVerticalAuto(Axis axis, int position) {
		final ChartComputator computator = chart.getChartComputator();
		final Viewport visibleViewport = computator.getVisibleViewport();
		final Rect contentRect = computator.getContentRect();

		Utils.computeAxisStops(visibleViewport.bottom, visibleViewport.top, contentRect.height()
				/ axisLabelTextAscentTab[position] / 2, axisAutoStopsBufferTab[position]);

		if (axis.hasLines() && axisLinesDrawBufferTab[position].length < axisAutoStopsBufferTab[position].numStops * 4) {
			axisLinesDrawBufferTab[position] = new float[axisAutoStopsBufferTab[position].numStops * 4];
		}

		if (axisRawStopsTab[position].length < axisAutoStopsBufferTab[position].numStops) {
			axisRawStopsTab[position] = new float[axisAutoStopsBufferTab[position].numStops];
			axisAutoStopsToDrawTab[position] = new float[axisAutoStopsBufferTab[position].numStops];
		}

		int stopsToDrawIndex = 0;

		for (int i = 0; i < axisAutoStopsBufferTab[position].numStops; i++) {
			final float rawY = computator.computeRawY(axisAutoStopsBufferTab[position].stops[i]);

			if (checkRawY(contentRect, rawY, axis.isInside(), position)) {

				axisRawStopsTab[position][stopsToDrawIndex] = rawY;
				axisAutoStopsToDrawTab[position][stopsToDrawIndex] = axisAutoStopsBufferTab[position].stops[i];

				++stopsToDrawIndex;
			}
		}

		axisValuesToDrawNumTab[position] = stopsToDrawIndex;
	}

	private void drawAxisVerticalLines(Canvas canvas, Axis axis, int position) {
		if (!axis.hasLines()) {
			return;
		}

		final Rect contentRectMargins = chart.getChartComputator().getContentRectWithMargins();

		int stopsToDrawIndex = 0;

		for (; stopsToDrawIndex < axisValuesToDrawNumTab[position]; ++stopsToDrawIndex) {

			axisLinesDrawBufferTab[position][stopsToDrawIndex * 4 + 0] = contentRectMargins.left;
			axisLinesDrawBufferTab[position][stopsToDrawIndex * 4 + 1] = axisRawStopsTab[position][stopsToDrawIndex];
			axisLinesDrawBufferTab[position][stopsToDrawIndex * 4 + 2] = contentRectMargins.right;
			axisLinesDrawBufferTab[position][stopsToDrawIndex * 4 + 3] = axisRawStopsTab[position][stopsToDrawIndex];
		}

		linePaint.setColor(axis.getLineColor());
		canvas.drawLines(axisLinesDrawBufferTab[position], 0, stopsToDrawIndex * 4, linePaint);
	}

	private void drawAxisVerticalLabels(Canvas canvas, Axis axis, int position) {
		final Rect contentRectMargins = chart.getChartComputator().getContentRectWithMargins();

		int stopsToDrawIndex = 0;

		for (; stopsToDrawIndex < axisValuesToDrawNumTab[position]; ++stopsToDrawIndex) {

			final int numChars;

			if (axis.isAutoGenerated()) {
				valuesBuff[0] = axisAutoStopsToDrawTab[position][stopsToDrawIndex];

				numChars = axis.getFormatter().formatValue(labelBuffer, valuesBuff, null);
			} else {
				valuesBuff[0] = axisValuesToDrawTab[position][stopsToDrawIndex].getValue();

				numChars = axis.getFormatter().formatValue(labelBuffer, valuesBuff,
						axisValuesToDrawTab[position][stopsToDrawIndex].getLabel());
			}

			canvas.drawText(labelBuffer, labelBuffer.length - numChars, numChars, axisFixedCoordinateTab[position],
					axisRawStopsTab[position][stopsToDrawIndex], textPaintTab[position]);

		}

		// drawing axis name
		if (!TextUtils.isEmpty(axis.getName())) {
			textPaintTab[position].setTextAlign(Align.CENTER);
			canvas.save();
			canvas.rotate(-90, contentRectMargins.centerY(), contentRectMargins.centerY());
			canvas.drawText(axis.getName(), contentRectMargins.centerY(), axisBaselineTab[position],
					textPaintTab[position]);
			canvas.restore();
		}

	}

	/**
	 * For axis inside chart area this method checks if there is place to draw axis label. If yes returns true,
	 * otherwise false.
	 */
	private boolean checkRawY(Rect rect, float rawY, boolean axisInside, int position) {
		if (axisInside) {
			float marginBottom = axisLabelTextAscentTab[BOTTOM] + axisMargin;
			float marginTop = axisLabelTextAscentTab[TOP] + axisMargin;
			if (rawY <= rect.bottom - marginBottom && rawY >= rect.top + marginTop) {
				return true;
			} else {
				return false;
			}
		}

		return true;

	}
}
