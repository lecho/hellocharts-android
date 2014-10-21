package lecho.lib.hellocharts.renderer;

import lecho.lib.hellocharts.ChartComputator;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Viewport;
import lecho.lib.hellocharts.util.AxisAutoValues;
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
 */
public class AxesRenderer {
	private static final int DEFAULT_AXIS_MARGIN_DP = 2;

	/** Axis positions indexes, used for indexing tabs that holds axes parameters, see below. */
	private static final int TOP = 0;
	private static final int LEFT = 1;
	private static final int RIGHT = 2;
	private static final int BOTTOM = 3;

	/**
	 * Used to measure label width. If label has mas 5 characters only 5 first characters of this array are used to
	 * measure text width.
	 **/
	private static final char[] labelWidthChars = new char[] { '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0',
			'0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0' };

	private Chart chart;
	private int axisMargin;

	/** 4 text paints for every axis, not all have to be used, indexed with TOP, LEFT, RIGHT, BOTTOM. */
	private Paint[] textPaintTab = new Paint[] { new Paint(), new Paint(), new Paint(), new Paint() };
	private Paint linePaint;

	/** Holds number of values that should be drown for each axis. */
	private int[] axisValuesToDrawNumTab = new int[4];

	/** Holds raw values to draw for each axis. */
	private float[][] axisRawValuesTab = new float[4][0];

	/**
	 * Holds auto-generated values that should be drawn, i.e if axis is inside not all auto-generated values should be
	 * drawn to avoid overdrawing. Used only for auto axes.
	 **/
	private float[][] axisAutoValuesToDrawTab = new float[4][0];

	/** Holds custom values that should be drawn, used only for custom axes. */
	private AxisValue[][] axisValuesToDrawTab = new AxisValue[4][0];

	/** Buffers for axes lines coordinates(to draw grid in the background). */
	private float[][] axisLinesDrawBufferTab = new float[4][0];

	/** Buffers for auto-generated values for each axis, used only if there are auto axes. */
	private AxisAutoValues[] axisAutoValuesBufferTab = new AxisAutoValues[] { new AxisAutoValues(),
			new AxisAutoValues(), new AxisAutoValues(), new AxisAutoValues() };

	/** Holds fixed coordinates for each axis, for horizontal axes Y value is fixed, for vertical axes X value is fixed. */
	private float[] axisFixedCoordinateTab = new float[4];

	/** Holds baselines for axes names. */
	private float[] axisNameBaselineTab = new float[4];

	/** Holds fixed coordinate for axes separations lines, used only for horizontal axes where fixed value is Y. */
	private float[] axisSeparationLineTab = new float[4];

	/** Label max width for each axis. */
	private int[] axisLabelWidthTab = new int[4];

	/** Label height for each axis. */
	private int[] axisLabelTextAscentTab = new int[4];

	/** Label descent for each axis. **/
	private int[] axisLabelTextDescentTab = new int[4];

	/** Font metrics for each axis. */
	private FontMetricsInt[] fontMetricsTab = new FontMetricsInt[] { new FontMetricsInt(), new FontMetricsInt(),
			new FontMetricsInt(), new FontMetricsInt() };

	/** Buffer used to pass axis value to ValueFormatter implementation. */
	private float[] valuesBuff = new float[1];

	/** Holds formated axis value label. */
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

	/**
	 * Prepare axes coordinates and draw axes lines(if enabled) in the background.
	 * 
	 * @param canvas
	 */
	public void drawInBackground(Canvas canvas) {
		Axis axis = chart.getChartData().getAxisYLeft();
		if (null != axis) {
			prepareAxis(axis, LEFT);
			drawAxisVerticalLines(canvas, axis, LEFT);
		}

		axis = chart.getChartData().getAxisYRight();
		if (null != axis) {
			prepareAxis(axis, RIGHT);
			drawAxisVerticalLines(canvas, axis, RIGHT);
		}

		axis = chart.getChartData().getAxisXBottom();
		if (null != axis) {
			prepareAxis(axis, BOTTOM);
			drawAxisHorizontalLines(canvas, axis, BOTTOM);
		}

		axis = chart.getChartData().getAxisXTop();
		if (null != axis) {
			prepareAxis(axis, TOP);
			drawAxisHorizontalLines(canvas, axis, TOP);
		}
	}

	/**
	 * Draw axes labels and names in the foreground.
	 * 
	 * @param canvas
	 */
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

	private void prepareAxis(Axis axis, int position) {
		final ChartComputator computator = chart.getChartComputator();

		if (BOTTOM == position) {
			textPaintTab[position].setTextAlign(Align.CENTER);

			if (axis.isInside()) {
				axisFixedCoordinateTab[position] = computator.getContentRectWithMargins().bottom - axisMargin
						- axisLabelTextDescentTab[position];
				axisNameBaselineTab[position] = computator.getContentRectWithMargins().bottom
						+ axisLabelTextAscentTab[position] + axisMargin;
			} else {
				axisFixedCoordinateTab[position] = computator.getContentRectWithMargins().bottom
						+ axisLabelTextAscentTab[position] + axisMargin;
				axisNameBaselineTab[position] = axisFixedCoordinateTab[position] + axisMargin
						+ axisLabelTextAscentTab[position] + axisLabelTextDescentTab[position];
			}

			axisSeparationLineTab[position] = computator.getContentRect().bottom;

		} else if (TOP == position) {
			textPaintTab[position].setTextAlign(Align.CENTER);

			if (axis.isInside()) {
				axisFixedCoordinateTab[position] = computator.getContentRectWithMargins().top + axisMargin
						+ axisLabelTextAscentTab[position];
				axisNameBaselineTab[position] = computator.getContentRectWithMargins().top - axisMargin
						- axisLabelTextDescentTab[position];
			} else {
				axisFixedCoordinateTab[position] = computator.getContentRectWithMargins().top - axisMargin
						- axisLabelTextDescentTab[position];
				axisNameBaselineTab[position] = axisFixedCoordinateTab[position] - axisMargin
						- axisLabelTextAscentTab[position] - axisLabelTextDescentTab[position];
			}

			axisSeparationLineTab[position] = computator.getContentRect().top;

		} else if (LEFT == position) {

			if (axis.isInside()) {
				textPaintTab[position].setTextAlign(Align.LEFT);
				axisFixedCoordinateTab[position] = computator.getContentRectWithMargins().left + axisMargin;
				axisNameBaselineTab[position] = computator.getContentRectWithMargins().left - axisMargin
						- axisLabelTextDescentTab[position];
			} else {
				textPaintTab[position].setTextAlign(Align.RIGHT);
				axisFixedCoordinateTab[position] = computator.getContentRectWithMargins().left - axisMargin;
				axisNameBaselineTab[position] = axisFixedCoordinateTab[position] - axisLabelWidthTab[position]
						- axisMargin - axisLabelTextDescentTab[position];
			}

			axisSeparationLineTab[position] = computator.getContentRect().left;

		} else if (RIGHT == position) {

			if (axis.isInside()) {
				textPaintTab[position].setTextAlign(Align.RIGHT);
				axisFixedCoordinateTab[position] = computator.getContentRectWithMargins().right - axisMargin;
				axisNameBaselineTab[position] = computator.getContentRectWithMargins().right + axisMargin
						+ axisLabelTextAscentTab[position];
			} else {
				textPaintTab[position].setTextAlign(Align.LEFT);
				axisFixedCoordinateTab[position] = computator.getContentRectWithMargins().right + axisMargin;
				axisNameBaselineTab[position] = axisFixedCoordinateTab[position] + axisLabelWidthTab[position]
						+ axisMargin + axisLabelTextAscentTab[position];
			}

			axisSeparationLineTab[position] = computator.getContentRect().right;

		} else {
			throw new IllegalArgumentException("Invalid position for horizontal axis: " + position);
		}

		if (TOP == position || BOTTOM == position) {
			if (axis.isAutoGenerated()) {
				prepareAxisHorizontalAuto(axis, position);
			} else {
				prepareAxisHorizontalCustom(axis, position);
			}
		} else if (LEFT == position || RIGHT == position) {
			if (axis.isAutoGenerated()) {
				prepareAxisVerticalAuto(axis, position);
			} else {
				prepareAxisVerticalCustom(axis, position);
			}
		}

	}

	// ********** HORIZONTAL X AXES ****************

	private void prepareAxisHorizontalCustom(Axis axis, int position) {
		final ChartComputator computator = chart.getChartComputator();
		final Viewport maxViewport = computator.getMaximumViewport();
		final Viewport visibleViewport = computator.getVisibleViewport();
		final Rect contentRect = computator.getContentRect();
		float scale = maxViewport.width() / visibleViewport.width();

		int module = (int) Math.ceil((axis.getValues().size() * axisLabelWidthTab[position])
				/ (contentRect.width() * scale));

		if (module < 1) {
			module = 1;
		}

		if (axis.hasLines() && axisLinesDrawBufferTab[position].length < axis.getValues().size() * 4) {
			axisLinesDrawBufferTab[position] = new float[axis.getValues().size() * 4];
		}

		if (axisRawValuesTab[position].length < axis.getValues().size()) {
			axisRawValuesTab[position] = new float[axis.getValues().size()];
			axisValuesToDrawTab[position] = new AxisValue[axis.getValues().size()];
		}

		int valueIndex = 0;
		int valueToDrawIndex = 0;

		for (AxisValue axisValue : axis.getValues()) {
			final float value = axisValue.getValue();

			// Draw axis values that area within visible viewport.
			if (value >= visibleViewport.left && value <= visibleViewport.right) {

				// Draw axis values that have 0 module value, this will hide some labels if there is no place for them.
				if (0 == valueIndex % module) {

					final float rawX = computator.computeRawX(axisValue.getValue());

					if (checkRawX(contentRect, rawX, axis.isInside(), position)) {

						axisRawValuesTab[position][valueToDrawIndex] = rawX;
						axisValuesToDrawTab[position][valueToDrawIndex] = axisValue;

						++valueToDrawIndex;

					}
				}
				// If within viewport - increment valueIndex;
				++valueIndex;
			}
		}

		axisValuesToDrawNumTab[position] = valueToDrawIndex;

	}

	private void prepareAxisHorizontalAuto(Axis axis, int position) {
		final ChartComputator computator = chart.getChartComputator();
		final Viewport visibleViewport = computator.getVisibleViewport();
		final Rect contentRect = computator.getContentRect();

		Utils.computeAxisAutoValues(visibleViewport.left, visibleViewport.right, contentRect.width()
				/ axisLabelWidthTab[position] / 2, axisAutoValuesBufferTab[position]);

		if (axis.hasLines()
				&& axisLinesDrawBufferTab[position].length < axisAutoValuesBufferTab[position].valuesNumber * 4) {
			axisLinesDrawBufferTab[position] = new float[axisAutoValuesBufferTab[position].valuesNumber * 4];
		}

		if (axisRawValuesTab[position].length < axisAutoValuesBufferTab[position].valuesNumber) {
			axisRawValuesTab[position] = new float[axisAutoValuesBufferTab[position].valuesNumber];
			axisAutoValuesToDrawTab[position] = new float[axisAutoValuesBufferTab[position].valuesNumber];
		}

		int valueToDrawIndex = 0;

		for (int i = 0; i < axisAutoValuesBufferTab[position].valuesNumber; ++i) {

			final float rawX = computator.computeRawX(axisAutoValuesBufferTab[position].values[i]);

			if (checkRawX(contentRect, rawX, axis.isInside(), position)) {

				axisRawValuesTab[position][valueToDrawIndex] = rawX;
				axisAutoValuesToDrawTab[position][valueToDrawIndex] = axisAutoValuesBufferTab[position].values[i];

				++valueToDrawIndex;
			}
		}

		axisValuesToDrawNumTab[position] = valueToDrawIndex;

	}

	private void drawAxisHorizontalLines(Canvas canvas, Axis axis, int position) {
		final Rect contentRectMargins = chart.getChartComputator().getContentRectWithMargins();

		// Draw separation line with the same color as axis text. Only horizontal axes have separation lines.
		canvas.drawLine(contentRectMargins.left, axisSeparationLineTab[position], contentRectMargins.right,
				axisSeparationLineTab[position], textPaintTab[position]);

		if (!axis.hasLines()) {
			return;
		}

		int valueToDrawIndex = 0;

		for (; valueToDrawIndex < axisValuesToDrawNumTab[position]; ++valueToDrawIndex) {

			axisLinesDrawBufferTab[position][valueToDrawIndex * 4 + 0] = axisRawValuesTab[position][valueToDrawIndex];
			axisLinesDrawBufferTab[position][valueToDrawIndex * 4 + 1] = contentRectMargins.top;
			axisLinesDrawBufferTab[position][valueToDrawIndex * 4 + 2] = axisRawValuesTab[position][valueToDrawIndex];
			axisLinesDrawBufferTab[position][valueToDrawIndex * 4 + 3] = contentRectMargins.bottom;
		}

		linePaint.setColor(axis.getLineColor());
		canvas.drawLines(axisLinesDrawBufferTab[position], 0, valueToDrawIndex * 4, linePaint);
	}

	private void drawAxisHorizontalLabels(Canvas canvas, Axis axis, int position) {
		final Rect contentRectMargins = chart.getChartComputator().getContentRectWithMargins();

		int valueToDrawIndex = 0;

		for (; valueToDrawIndex < axisValuesToDrawNumTab[position]; ++valueToDrawIndex) {

			final int numChars;

			if (axis.isAutoGenerated()) {
				valuesBuff[0] = axisAutoValuesToDrawTab[position][valueToDrawIndex];

				numChars = axis.getFormatter().formatAutoValue(labelBuffer, valuesBuff,
						axisAutoValuesBufferTab[position].decimals);
			} else {
				valuesBuff[0] = axisValuesToDrawTab[position][valueToDrawIndex].getValue();

				numChars = axis.getFormatter().formatValue(labelBuffer, valuesBuff,
						axisValuesToDrawTab[position][valueToDrawIndex].getLabel());
			}

			canvas.drawText(labelBuffer, labelBuffer.length - numChars, numChars,
					axisRawValuesTab[position][valueToDrawIndex], axisFixedCoordinateTab[position],
					textPaintTab[position]);

		}

		// Drawing axis name
		if (!TextUtils.isEmpty(axis.getName())) {
			textPaintTab[position].setTextAlign(Align.CENTER);
			canvas.drawText(axis.getName(), contentRectMargins.centerX(), axisNameBaselineTab[position],
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

	private void prepareAxisVerticalCustom(Axis axis, int position) {
		final ChartComputator computator = chart.getChartComputator();
		final Viewport maxViewport = computator.getMaximumViewport();
		final Viewport visibleViewport = computator.getVisibleViewport();
		final Rect contentRect = computator.getContentRect();
		float scale = maxViewport.height() / visibleViewport.height();

		int module = (int) Math.ceil((axis.getValues().size() * axisLabelTextAscentTab[position] * 2)
				/ (contentRect.height() * scale));

		if (module < 1) {
			module = 1;
		}

		if (axis.hasLines() && axisLinesDrawBufferTab[position].length < axis.getValues().size() * 4) {
			axisLinesDrawBufferTab[position] = new float[axis.getValues().size() * 4];
		}

		if (axisRawValuesTab[position].length < axis.getValues().size()) {
			axisRawValuesTab[position] = new float[axis.getValues().size()];
			axisValuesToDrawTab[position] = new AxisValue[axis.getValues().size()];
		}

		int valueIndex = 0;
		int valueToDrawIndex = 0;

		for (AxisValue axisValue : axis.getValues()) {
			final float value = axisValue.getValue();

			// Draw axis values that area within visible viewport.
			if (value >= visibleViewport.bottom && value <= visibleViewport.top) {

				// Draw axis values that have 0 module value, this will hide some labels if there is no place for them.
				if (0 == valueIndex % module) {

					final float rawY = computator.computeRawY(value);

					if (checkRawY(contentRect, rawY, axis.isInside(), position)) {

						axisRawValuesTab[position][valueToDrawIndex] = rawY;
						axisValuesToDrawTab[position][valueToDrawIndex] = axisValue;

						++valueToDrawIndex;
					}
				}
				// If within viewport - increment valueIndex;
				++valueIndex;
			}
		}

		axisValuesToDrawNumTab[position] = valueToDrawIndex;
	}

	private void prepareAxisVerticalAuto(Axis axis, int position) {
		final ChartComputator computator = chart.getChartComputator();
		final Viewport visibleViewport = computator.getVisibleViewport();
		final Rect contentRect = computator.getContentRect();

		Utils.computeAxisAutoValues(visibleViewport.bottom, visibleViewport.top, contentRect.height()
				/ axisLabelTextAscentTab[position] / 2, axisAutoValuesBufferTab[position]);

		if (axis.hasLines()
				&& axisLinesDrawBufferTab[position].length < axisAutoValuesBufferTab[position].valuesNumber * 4) {
			axisLinesDrawBufferTab[position] = new float[axisAutoValuesBufferTab[position].valuesNumber * 4];
		}

		if (axisRawValuesTab[position].length < axisAutoValuesBufferTab[position].valuesNumber) {
			axisRawValuesTab[position] = new float[axisAutoValuesBufferTab[position].valuesNumber];
			axisAutoValuesToDrawTab[position] = new float[axisAutoValuesBufferTab[position].valuesNumber];
		}

		int stopsToDrawIndex = 0;

		for (int i = 0; i < axisAutoValuesBufferTab[position].valuesNumber; i++) {
			final float rawY = computator.computeRawY(axisAutoValuesBufferTab[position].values[i]);

			if (checkRawY(contentRect, rawY, axis.isInside(), position)) {

				axisRawValuesTab[position][stopsToDrawIndex] = rawY;
				axisAutoValuesToDrawTab[position][stopsToDrawIndex] = axisAutoValuesBufferTab[position].values[i];

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
			axisLinesDrawBufferTab[position][stopsToDrawIndex * 4 + 1] = axisRawValuesTab[position][stopsToDrawIndex];
			axisLinesDrawBufferTab[position][stopsToDrawIndex * 4 + 2] = contentRectMargins.right;
			axisLinesDrawBufferTab[position][stopsToDrawIndex * 4 + 3] = axisRawValuesTab[position][stopsToDrawIndex];
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
				valuesBuff[0] = axisAutoValuesToDrawTab[position][stopsToDrawIndex];

				numChars = axis.getFormatter().formatAutoValue(labelBuffer, valuesBuff,
						axisAutoValuesBufferTab[position].decimals);
			} else {
				valuesBuff[0] = axisValuesToDrawTab[position][stopsToDrawIndex].getValue();

				numChars = axis.getFormatter().formatValue(labelBuffer, valuesBuff,
						axisValuesToDrawTab[position][stopsToDrawIndex].getLabel());
			}

			canvas.drawText(labelBuffer, labelBuffer.length - numChars, numChars, axisFixedCoordinateTab[position],
					axisRawValuesTab[position][stopsToDrawIndex], textPaintTab[position]);

		}

		// drawing axis name
		if (!TextUtils.isEmpty(axis.getName())) {
			textPaintTab[position].setTextAlign(Align.CENTER);
			canvas.save();
			canvas.rotate(-90, contentRectMargins.centerY(), contentRectMargins.centerY());
			canvas.drawText(axis.getName(), contentRectMargins.centerY(), axisNameBaselineTab[position],
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
