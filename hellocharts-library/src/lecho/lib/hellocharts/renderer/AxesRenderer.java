package lecho.lib.hellocharts.renderer;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.FontMetricsInt;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.text.TextUtils;

import lecho.lib.hellocharts.computator.ChartComputator;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Viewport;
import lecho.lib.hellocharts.util.AxisAutoValues;
import lecho.lib.hellocharts.util.ChartUtils;
import lecho.lib.hellocharts.util.FloatUtils;
import lecho.lib.hellocharts.view.Chart;

/**
 * Default axes renderer. Can draw maximum four axes - two horizontal(top/bottom) and two vertical(left/right).
 */
public class AxesRenderer {
	private static final int DEFAULT_AXIS_MARGIN_DP = 2;

	/**
	 * Axis positions indexes, used for indexing tabs that holds axes parameters, see below.
	 */
	private static final int TOP = 0;
	private static final int LEFT = 1;
	private static final int RIGHT = 2;
	private static final int BOTTOM = 3;

	/**
	 * Used to measure label width. If label has mas 5 characters only 5 first characters of this array are used to
	 * measure text width.
	 */
	private static final char[] labelWidthChars = new char[]{'0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0',
			'0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0'};

	private Chart chart;
	private ChartComputator computator;
	private int axisMargin;
	private float density;
	private float scaledDensity;
	private Paint[] textPaintTab = new Paint[]{new Paint(), new Paint(), new Paint(), new Paint()};
	private Paint linePaint;

	/**
	 * Holds formated axis value label.
	 */
	private char[] labelBuffer = new char[32];

	/**
	 * Holds number of values that should be drown for each axis.
	 */
	private int[] axisValuesToDrawNumTab = new int[4];

	/**
	 * Holds raw values to draw for each axis.
	 */
	private float[][] axisRawValuesTab = new float[4][0];

	/**
	 * Holds auto-generated values that should be drawn, i.e if axis is inside not all auto-generated values should be
	 * drawn to avoid overdrawing. Used only for auto axes.
	 */
	private float[][] axisAutoValuesToDrawTab = new float[4][0];

	/**
	 * Holds custom values that should be drawn, used only for custom axes.
	 */
	private AxisValue[][] axisValuesToDrawTab = new AxisValue[4][0];

	/**
	 * Buffers for axes lines coordinates(to draw grid in the background).
	 */
	private float[][] axisLinesDrawBufferTab = new float[4][0];

	/**
	 * Buffers for auto-generated values for each axis, used only if there are auto axes.
	 */
	private AxisAutoValues[] axisAutoValuesBufferTab = new AxisAutoValues[]{new AxisAutoValues(),
			new AxisAutoValues(), new AxisAutoValues(), new AxisAutoValues()};

	private float[] axisNameBaselineTab = new float[4];
	private float[] axisLabelBaselineTab = new float[4];
	private float[] axisSeparationLineTab = new float[4];
	private int[] axisLabelWidthTab = new int[4];
	private int[] axisLabelTextAscentTab = new int[4];
	private int[] axisLabelTextDescentTab = new int[4];
	private FontMetricsInt[] fontMetricsTab = new FontMetricsInt[]{new FontMetricsInt(), new FontMetricsInt(),
			new FontMetricsInt(), new FontMetricsInt()};

	public AxesRenderer(Context context, Chart chart) {
		this.chart = chart;
		computator = chart.getChartComputator();
		density = context.getResources().getDisplayMetrics().density;
		scaledDensity = context.getResources().getDisplayMetrics().scaledDensity;
		axisMargin = ChartUtils.dp2px(density, DEFAULT_AXIS_MARGIN_DP);
		linePaint = new Paint();
		linePaint.setAntiAlias(true);
		linePaint.setStyle(Paint.Style.STROKE);
		linePaint.setStrokeWidth(1);
		for (Paint paint : textPaintTab) {
			paint.setAntiAlias(true);
		}
	}

	public void onChartDataOrSizeChanged() {
		initAxis(chart.getChartData().getAxisXTop(), TOP);
		initAxis(chart.getChartData().getAxisXBottom(), BOTTOM);
		initAxis(chart.getChartData().getAxisYLeft(), LEFT);
		initAxis(chart.getChartData().getAxisYRight(), RIGHT);
	}

	/**
	 * Initialize attributes and measurement for axes(left, right, top, bottom);
	 */
	private void initAxis(Axis axis, int position) {
		if (null == axis) {
			return;
		}
		initAxisAttributes(axis, position);
		initAxisMargin(axis, position);
		initAxisMeasurements(axis, position);
	}

	private void initAxisAttributes(Axis axis, int position) {
		Typeface typeface = axis.getTypeface();
		if (null != typeface) {
			textPaintTab[position].setTypeface(typeface);
		}

		textPaintTab[position].setColor(axis.getTextColor());
		textPaintTab[position].setTextSize(ChartUtils.sp2px(scaledDensity, axis.getTextSize()));
		textPaintTab[position].getFontMetricsInt(fontMetricsTab[position]);

		axisLabelTextAscentTab[position] = Math.abs(fontMetricsTab[position].ascent);
		axisLabelTextDescentTab[position] = Math.abs(fontMetricsTab[position].descent);
		axisLabelWidthTab[position] = (int) textPaintTab[position].measureText(labelWidthChars, 0,
				axis.getMaxLabelChars());
	}

	private void initAxisMargin(Axis axis, int position) {
		int margin = 0;
		if (LEFT == position || RIGHT == position) {
			// If auto-generated or has manual values add height for value labels.
			int width = 0;
			if ((axis.isAutoGenerated() || !axis.getValues().isEmpty()) && !axis.isInside()) {
				width += axisLabelWidthTab[position];
				width += axisMargin;
			}
			margin = width + getAxisNameMargin(axis, position);
		} else if (TOP == position || BOTTOM == position) {
			// If auto-generated or has manual values add height for value labels.
			int height = 0;
			if ((axis.isAutoGenerated() || !axis.getValues().isEmpty()) && !axis.isInside()) {
				height += axisLabelTextAscentTab[position];
				height += axisLabelTextDescentTab[position];
				height += axisMargin;
			}
			margin = height + getAxisNameMargin(axis, position);
		} else {
			throw new IllegalArgumentException("Invalid axis position: " + position);
		}
		insetContentRectWithAxesMargins(margin, position);
	}

	private int getAxisNameMargin(Axis axis, int position) {
		int margin = 0;
		if (!TextUtils.isEmpty(axis.getName())) {
			margin += axisLabelTextAscentTab[position];
			margin += axisLabelTextDescentTab[position];
			margin += axisMargin;
		}
		return margin;
	}

	private void insetContentRectWithAxesMargins(int axisMargin, int position) {
		switch (position) {
			case LEFT:
				chart.getChartComputator().insetContentRect(axisMargin, 0, 0, 0);
				break;
			case TOP:
				chart.getChartComputator().insetContentRect(0, axisMargin, 0, 0);
				break;
			case RIGHT:
				chart.getChartComputator().insetContentRect(0, 0, axisMargin, 0);
				break;
			case BOTTOM:
				chart.getChartComputator().insetContentRect(0, 0, 0, axisMargin);
				break;
		}
	}

	private void initAxisMeasurements(Axis axis, int position) {
		if (LEFT == position) {
			if (axis.isInside()) {
				axisLabelBaselineTab[position] = computator.getContentRectMinusAxesMargins().left + axisMargin;
				axisNameBaselineTab[position] = computator.getContentRectMinusAxesMargins().left - axisMargin
						- axisLabelTextDescentTab[position];
			} else {
				axisLabelBaselineTab[position] = computator.getContentRectMinusAxesMargins().left - axisMargin;
				axisNameBaselineTab[position] = axisLabelBaselineTab[position] - axisLabelWidthTab[position]
						- axisMargin - axisLabelTextDescentTab[position];
			}
			axisSeparationLineTab[position] = computator.getContentRectMinusAllMargins().left;
		} else if (RIGHT == position) {
			if (axis.isInside()) {
				axisLabelBaselineTab[position] = computator.getContentRectMinusAxesMargins().right - axisMargin;
				axisNameBaselineTab[position] = computator.getContentRectMinusAxesMargins().right + axisMargin
						+ axisLabelTextAscentTab[position];
			} else {
				axisLabelBaselineTab[position] = computator.getContentRectMinusAxesMargins().right + axisMargin;
				axisNameBaselineTab[position] = axisLabelBaselineTab[position] + axisMargin +
						axisLabelWidthTab[position] + axisLabelTextAscentTab[position];
			}
			axisSeparationLineTab[position] = computator.getContentRectMinusAllMargins().right;

		} else if (BOTTOM == position) {
			if (axis.isInside()) {
				axisLabelBaselineTab[position] = computator.getContentRectMinusAxesMargins().bottom - axisMargin
						- axisLabelTextDescentTab[position];
				axisNameBaselineTab[position] = computator.getContentRectMinusAxesMargins().bottom
						+ axisLabelTextAscentTab[position] + axisMargin;
			} else {
				axisLabelBaselineTab[position] = computator.getContentRectMinusAxesMargins().bottom
						+ axisLabelTextAscentTab[position] + axisMargin;
				axisNameBaselineTab[position] = axisLabelBaselineTab[position] + axisMargin
						+ axisLabelTextAscentTab[position] + axisLabelTextDescentTab[position];
			}
			axisSeparationLineTab[position] = computator.getContentRectMinusAllMargins().bottom;
		} else if (TOP == position) {
			if (axis.isInside()) {
				axisLabelBaselineTab[position] = computator.getContentRectMinusAxesMargins().top + axisMargin
						+ axisLabelTextAscentTab[position];
				axisNameBaselineTab[position] = computator.getContentRectMinusAxesMargins().top - axisMargin
						- axisLabelTextDescentTab[position];
			} else {
				axisLabelBaselineTab[position] = computator.getContentRectMinusAxesMargins().top - axisMargin
						- axisLabelTextDescentTab[position];
				axisNameBaselineTab[position] = axisLabelBaselineTab[position] - axisMargin
						- axisLabelTextAscentTab[position] - axisLabelTextDescentTab[position];
			}
			axisSeparationLineTab[position] = computator.getContentRectMinusAllMargins().top;
		} else {
			throw new IllegalArgumentException("Invalid axis position: " + position);
		}
	}

	/**
	 * Prepare axes coordinates and draw axes lines(if enabled) in the background.
	 *
	 * @param canvas
	 */
	public void drawInBackground(Canvas canvas) {
		Axis axis = chart.getChartData().getAxisYLeft();
		if (null != axis) {
			prepareAxisToDraw(axis, LEFT);
			drawAxisVerticalLines(canvas, axis, LEFT);
		}

		axis = chart.getChartData().getAxisYRight();
		if (null != axis) {
			prepareAxisToDraw(axis, RIGHT);
			drawAxisVerticalLines(canvas, axis, RIGHT);
		}

		axis = chart.getChartData().getAxisXBottom();
		if (null != axis) {
			prepareAxisToDraw(axis, BOTTOM);
			drawAxisHorizontalLines(canvas, axis, BOTTOM);
		}

		axis = chart.getChartData().getAxisXTop();
		if (null != axis) {
			prepareAxisToDraw(axis, TOP);
			drawAxisHorizontalLines(canvas, axis, TOP);
		}
	}

	private void prepareAxisToDraw(Axis axis, int position) {
		if (TOP == position || BOTTOM == position) {
			textPaintTab[position].setTextAlign(Align.CENTER);
			if (axis.isAutoGenerated()) {
				prepareAxisHorizontalAuto(axis, position);
			} else {
				prepareAxisHorizontalCustom(axis, position);
			}
		} else if (LEFT == position || RIGHT == position) {
			if (LEFT == position) {
				if (axis.isInside()) {
					textPaintTab[position].setTextAlign(Align.LEFT);
				} else {
					textPaintTab[position].setTextAlign(Align.RIGHT);
				}
			} else if (RIGHT == position) {
				if (axis.isInside()) {
					textPaintTab[position].setTextAlign(Align.RIGHT);
				} else {
					textPaintTab[position].setTextAlign(Align.LEFT);
				}
			}
			if (axis.isAutoGenerated()) {
				prepareAxisVerticalAuto(axis, position);
			} else {
				prepareAxisVerticalCustom(axis, position);
			}
		} else {
			throw new IllegalArgumentException("Invalid position for horizontal axis: " + position);
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

	// ********** HORIZONTAL X AXES ****************

	private void prepareAxisHorizontalCustom(Axis axis, int position) {
		final Viewport maxViewport = computator.getMaximumViewport();
		final Viewport visibleViewport = computator.getVisibleViewport();
		final Rect contentRect = computator.getContentRectMinusAllMargins();
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
			// Draw axis values that are within visible viewport.
			final float value = axisValue.getValue();
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
		final Viewport visibleViewport = computator.getVisibleViewport();
		final Rect contentRect = computator.getContentRectMinusAllMargins();
		FloatUtils.computeAxisAutoValues(visibleViewport.left, visibleViewport.right, contentRect.width()
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
		final Rect contentRectMargins = chart.getChartComputator().getContentRectMinusAxesMargins();
		// Draw separation line with the same color as axis text.
		if (axis.hasSeparationLine()) {
			canvas.drawLine(contentRectMargins.left, axisSeparationLineTab[position], contentRectMargins.right,
					axisSeparationLineTab[position], textPaintTab[position]);
		}
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
		for (int valueToDrawIndex = 0; valueToDrawIndex < axisValuesToDrawNumTab[position]; ++valueToDrawIndex) {
			int charsNumber = 0;
			if (axis.isAutoGenerated()) {
				final float value = axisAutoValuesToDrawTab[position][valueToDrawIndex];
				charsNumber = axis.getFormatter().formatValueForAutoGeneratedAxis(labelBuffer, value,
						axisAutoValuesBufferTab[position].decimals);
			} else {
				AxisValue axisValue = axisValuesToDrawTab[position][valueToDrawIndex];
				charsNumber = axis.getFormatter().formatValueForManualAxis(labelBuffer, axisValue);
			}
			canvas.drawText(labelBuffer, labelBuffer.length - charsNumber, charsNumber,
					axisRawValuesTab[position][valueToDrawIndex], axisLabelBaselineTab[position],
					textPaintTab[position]);

		}

		// Drawing axis name
		final Rect contentRectMargins = chart.getChartComputator().getContentRectMinusAxesMargins();
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
		final Viewport maxViewport = computator.getMaximumViewport();
		final Viewport visibleViewport = computator.getVisibleViewport();
		final Rect contentRect = computator.getContentRectMinusAllMargins();
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
			// Draw axis values that area within visible viewport.
			final float value = axisValue.getValue();
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
		final Viewport visibleViewport = computator.getVisibleViewport();
		final Rect contentRect = computator.getContentRectMinusAllMargins();
		FloatUtils.computeAxisAutoValues(visibleViewport.bottom, visibleViewport.top, contentRect.height()
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
		final Rect contentRectMargins = chart.getChartComputator().getContentRectMinusAxesMargins();
		// Draw separation line with the same color as axis text.
		if (axis.hasSeparationLine()) {
			canvas.drawLine(axisSeparationLineTab[position], contentRectMargins.bottom,
					axisSeparationLineTab[position], contentRectMargins.top, textPaintTab[position]);
		}
		if (!axis.hasLines()) {
			return;
		}

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
		for (int stopsToDrawIndex = 0; stopsToDrawIndex < axisValuesToDrawNumTab[position]; ++stopsToDrawIndex) {
			int charsNumber = 0;
			if (axis.isAutoGenerated()) {
				final float value = axisAutoValuesToDrawTab[position][stopsToDrawIndex];
				charsNumber = axis.getFormatter().formatValueForAutoGeneratedAxis(labelBuffer, value,
						axisAutoValuesBufferTab[position].decimals);
			} else {
				AxisValue axisValue = axisValuesToDrawTab[position][stopsToDrawIndex];
				charsNumber = axis.getFormatter().formatValueForManualAxis(labelBuffer, axisValue);
			}
			canvas.drawText(labelBuffer, labelBuffer.length - charsNumber, charsNumber, axisLabelBaselineTab[position],
					axisRawValuesTab[position][stopsToDrawIndex], textPaintTab[position]);

		}

		// drawing axis name
		final Rect contentRectMargins = chart.getChartComputator().getContentRectMinusAxesMargins();
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