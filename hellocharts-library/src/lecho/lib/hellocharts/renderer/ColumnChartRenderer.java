package lecho.lib.hellocharts.renderer;

import lecho.lib.hellocharts.Chart;
import lecho.lib.hellocharts.ChartCalculator;
import lecho.lib.hellocharts.ColumnChartDataProvider;
import lecho.lib.hellocharts.model.Column;
import lecho.lib.hellocharts.model.ColumnChartData;
import lecho.lib.hellocharts.model.ColumnValue;
import lecho.lib.hellocharts.util.Utils;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Cap;
import android.graphics.PointF;
import android.graphics.RectF;

public class ColumnChartRenderer extends AbstractChartRenderer {
	public static final int DEFAULT_SUBCOLUMN_SPACING_DP = 1;
	public static final int DEFAULT_COLUMN_TOUCH_ADDITIONAL_WIDTH_DP = 2;

	private static final float DEFAULT_BASE_VALUE = 0.0f;
	private static final int MODE_DRAW = 0;
	private static final int MODE_CHECK_TOUCH = 1;
	private static final int MODE_HIGHLIGHT = 2;

	private ColumnChartDataProvider dataProvider;

	private int touchAdditionalWidth;
	private int subcolumnSpacing;
	private Paint columnPaint = new Paint();
	private RectF drawRect = new RectF();
	private PointF touchedPoint = new PointF();

	public ColumnChartRenderer(Context context, Chart chart, ColumnChartDataProvider dataProvider) {
		super(context, chart);
		this.dataProvider = dataProvider;
		subcolumnSpacing = Utils.dp2px(density, DEFAULT_SUBCOLUMN_SPACING_DP);
		touchAdditionalWidth = Utils.dp2px(density, DEFAULT_COLUMN_TOUCH_ADDITIONAL_WIDTH_DP);

		columnPaint.setAntiAlias(true);
		columnPaint.setStyle(Paint.Style.FILL);
		columnPaint.setStrokeCap(Cap.SQUARE);
	}

	@Override
	public void initMaxViewport() {
		calculateMaxViewport();
		chart.getChartCalculator().setMaxViewport(tempMaxViewport);
	}

	@Override
	public void initDataAttributes() {
		chart.getChartCalculator().setInternalMargin(labelMargin);// Using label margin because I'm lazy:P
		labelPaint.setTextSize(Utils.sp2px(scaledDensity, chart.getChartData().getValueLabelTextSize()));
		labelPaint.getFontMetricsInt(fontMetrics);
	}

	public void draw(Canvas canvas) {
		final ColumnChartData data = dataProvider.getColumnChartData();
		if (data.isStacked()) {
			drawColumnForStacked(canvas);
			if (isTouched()) {
				highlightColumnForStacked(canvas);
			}
		} else {
			drawColumnsForSubcolumns(canvas);
			if (isTouched()) {
				highlightColumnsForSubcolumns(canvas);
			}
		}
	}

	@Override
	public void drawUnclipped(Canvas canvas) {
		// Do nothing, for this kind of chart there is nothing to draw beyond clipped area
	}

	public boolean checkTouch(float touchX, float touchY) {
		oldSelectedValue.set(selectedValue);
		selectedValue.clear();
		final ColumnChartData data = dataProvider.getColumnChartData();
		if (data.isStacked()) {
			checkTouchForStacked(touchX, touchY);
		} else {
			checkTouchForSubcolumns(touchX, touchY);
		}
		// Check if touch is still on the same value, if not return false.
		if (oldSelectedValue.isSet() && selectedValue.isSet() && !oldSelectedValue.equals(selectedValue)) {
			return false;
		}
		return isTouched();
	}

	private void calculateMaxViewport() {
		final ColumnChartData data = dataProvider.getColumnChartData();
		// Column chart always has X values from 0 to numColumns-1, to add some margin on the left and right I added
		// extra 0.5 to the each side, that margins will be negative scaled according to number of columns, so for more
		// columns there will be less margin.
		tempMaxViewport.set(-0.5f, DEFAULT_BASE_VALUE, data.getColumns().size() - 0.5f, DEFAULT_BASE_VALUE);
		if (data.isStacked()) {
			calculateMaxViewportForStacked(data);
		} else {
			calculateMaxViewportForSubcolumns(data);
		}
	}

	private void calculateMaxViewportForSubcolumns(ColumnChartData data) {
		for (Column column : data.getColumns()) {
			for (ColumnValue columnValue : column.getValues()) {
				if (columnValue.getValue() >= DEFAULT_BASE_VALUE && columnValue.getValue() > tempMaxViewport.top) {
					tempMaxViewport.top = columnValue.getValue();
				}
				if (columnValue.getValue() < DEFAULT_BASE_VALUE && columnValue.getValue() < tempMaxViewport.bottom) {
					tempMaxViewport.bottom = columnValue.getValue();
				}
			}
		}
	}

	private void calculateMaxViewportForStacked(ColumnChartData data) {
		for (Column column : data.getColumns()) {
			float sumPositive = DEFAULT_BASE_VALUE;
			float sumNegative = DEFAULT_BASE_VALUE;
			for (ColumnValue columnValue : column.getValues()) {
				if (columnValue.getValue() >= DEFAULT_BASE_VALUE) {
					sumPositive += columnValue.getValue();
				} else {
					sumNegative += columnValue.getValue();
				}
			}
			if (sumPositive > tempMaxViewport.top) {
				tempMaxViewport.top = sumPositive;
			}
			if (sumNegative < tempMaxViewport.bottom) {
				tempMaxViewport.bottom = sumNegative;
			}
		}
	}

	private void drawColumnsForSubcolumns(Canvas canvas) {
		final ColumnChartData data = dataProvider.getColumnChartData();
		final ChartCalculator chartCalculator = chart.getChartCalculator();
		final float columnWidth = calculateColumnWidth(chartCalculator, data.getFillRatio());
		int columnIndex = 0;
		for (Column column : data.getColumns()) {
			processColumnForSubcolumns(canvas, chartCalculator, column, columnWidth, columnIndex, MODE_DRAW);
			++columnIndex;
		}
	}

	private void highlightColumnsForSubcolumns(Canvas canvas) {
		final ColumnChartData data = dataProvider.getColumnChartData();
		final ChartCalculator chartCalculator = chart.getChartCalculator();
		final float columnWidth = calculateColumnWidth(chartCalculator, data.getFillRatio());
		Column column = data.getColumns().get(selectedValue.firstIndex);
		processColumnForSubcolumns(canvas, chartCalculator, column, columnWidth, selectedValue.firstIndex,
				MODE_HIGHLIGHT);
	}

	private void checkTouchForSubcolumns(float touchX, float touchY) {
		// Using member variable to hold touch point to avoid too much parameters in methods.
		touchedPoint.x = touchX;
		touchedPoint.y = touchY;
		final ColumnChartData data = dataProvider.getColumnChartData();
		final ChartCalculator chartCalculator = chart.getChartCalculator();
		final float columnWidth = calculateColumnWidth(chartCalculator, data.getFillRatio());
		int columnIndex = 0;
		for (Column column : data.getColumns()) {
			// canvas is not needed for checking touch
			processColumnForSubcolumns(null, chartCalculator, column, columnWidth, columnIndex, MODE_CHECK_TOUCH);
			++columnIndex;
		}
	}

	private void processColumnForSubcolumns(Canvas canvas, ChartCalculator chartCalculator, Column column,
			float columnWidth, int columnIndex, int mode) {
		// For n subcolumns there will be n-1 spacing and there will be one
		// subcolumn for every columnValue
		float subcolumnWidth = (columnWidth - (subcolumnSpacing * (column.getValues().size() - 1)))
				/ column.getValues().size();
		if (subcolumnWidth < 1) {
			subcolumnWidth = 1;
		}
		// Columns are indexes from 0 to n, column index is also column X value
		final float rawX = chartCalculator.calculateRawX(columnIndex);
		final float halfColumnWidth = columnWidth / 2;
		final float baseRawY = chartCalculator.calculateRawY(DEFAULT_BASE_VALUE);
		// First subcolumn will starts at the left edge of current column,
		// rawValueX is horizontal center of that column
		float subcolumnRawX = rawX - halfColumnWidth;
		int valueIndex = 0;
		for (ColumnValue columnValue : column.getValues()) {
			columnPaint.setColor(columnValue.getColor());
			if (subcolumnRawX > rawX + halfColumnWidth) {
				break;
			}
			final float rawY = chartCalculator.calculateRawY(columnValue.getValue());
			calculateRectToDraw(columnValue, subcolumnRawX, subcolumnRawX + subcolumnWidth, baseRawY, rawY);
			switch (mode) {
			case MODE_DRAW:
				drawSubcolumn(canvas, column, columnValue, false);
				break;
			case MODE_HIGHLIGHT:
				highlightSubcolumn(canvas, column, columnValue, valueIndex, false);
				break;
			case MODE_CHECK_TOUCH:
				checkRectToDraw(columnIndex, valueIndex);
				break;
			default:
				// There no else, every case should be handled or exception will
				// be thrown
				throw new IllegalStateException("Cannot process column in mode: " + mode);
			}
			subcolumnRawX += subcolumnWidth + subcolumnSpacing;
			++valueIndex;
		}
	}

	private void drawColumnForStacked(Canvas canvas) {
		final ColumnChartData data = dataProvider.getColumnChartData();
		final ChartCalculator chartCalculator = chart.getChartCalculator();
		final float columnWidth = calculateColumnWidth(chartCalculator, data.getFillRatio());
		// Columns are indexes from 0 to n, column index is also column X value
		int columnIndex = 0;
		for (Column column : data.getColumns()) {
			processColumnForStacked(canvas, chartCalculator, column, columnWidth, columnIndex, MODE_DRAW);
			++columnIndex;
		}
	}

	private void highlightColumnForStacked(Canvas canvas) {
		final ColumnChartData data = dataProvider.getColumnChartData();
		final ChartCalculator chartCalculator = chart.getChartCalculator();
		final float columnWidth = calculateColumnWidth(chartCalculator, data.getFillRatio());
		// Columns are indexes from 0 to n, column index is also column X value
		Column column = data.getColumns().get(selectedValue.firstIndex);
		processColumnForStacked(canvas, chartCalculator, column, columnWidth, selectedValue.firstIndex, MODE_HIGHLIGHT);
	}

	private void checkTouchForStacked(float touchX, float touchY) {
		touchedPoint.x = touchX;
		touchedPoint.y = touchY;
		final ColumnChartData data = dataProvider.getColumnChartData();
		final ChartCalculator chartCalculator = chart.getChartCalculator();
		final float columnWidth = calculateColumnWidth(chartCalculator, data.getFillRatio());
		int columnIndex = 0;
		for (Column column : data.getColumns()) {
			// canvas is not needed for checking touch
			processColumnForStacked(null, chartCalculator, column, columnWidth, columnIndex, MODE_CHECK_TOUCH);
			++columnIndex;
		}
	}

	private void processColumnForStacked(Canvas canvas, ChartCalculator chartCalculator, Column column,
			float columnWidth, int columnIndex, int mode) {
		final float rawX = chartCalculator.calculateRawX(columnIndex);
		final float halfColumnWidth = columnWidth / 2;
		float mostPositiveValue = DEFAULT_BASE_VALUE;
		float mostNegativeValue = DEFAULT_BASE_VALUE;
		float baseValue = DEFAULT_BASE_VALUE;
		int valueIndex = 0;
		for (ColumnValue columnValue : column.getValues()) {
			columnPaint.setColor(columnValue.getColor());
			if (columnValue.getValue() >= DEFAULT_BASE_VALUE) {
				// Using values instead of raw pixels make code easier to
				// understand(for me)
				baseValue = mostPositiveValue;
				mostPositiveValue += columnValue.getValue();
			} else {
				baseValue = mostNegativeValue;
				mostNegativeValue += columnValue.getValue();
			}
			final float rawBaseY = chartCalculator.calculateRawY(baseValue);
			final float rawY = chartCalculator.calculateRawY(baseValue + columnValue.getValue());
			calculateRectToDraw(columnValue, rawX - halfColumnWidth, rawX + halfColumnWidth, rawBaseY, rawY);
			switch (mode) {
			case MODE_DRAW:
				drawSubcolumn(canvas, column, columnValue, true);
				break;
			case MODE_HIGHLIGHT:
				highlightSubcolumn(canvas, column, columnValue, valueIndex, true);
				break;
			case MODE_CHECK_TOUCH:
				checkRectToDraw(columnIndex, valueIndex);
				break;
			default:
				// There no else, every case should be handled or exception will
				// be thrown
				throw new IllegalStateException("Cannot process column in mode: " + mode);
			}
			++valueIndex;
		}
	}

	private void drawSubcolumn(Canvas canvas, Column column, ColumnValue columnValue, boolean isStacked) {
		canvas.drawRect(drawRect, columnPaint);
		if (column.hasLabels()) {
			drawLabel(canvas, column, columnValue, isStacked, labelOffset);
		}
	}

	private void highlightSubcolumn(Canvas canvas, Column column, ColumnValue columnValue, int valueIndex,
			boolean isStacked) {
		if (selectedValue.secondIndex == valueIndex) {
			columnPaint.setColor(columnValue.getDarkenColor());
			canvas.drawRect(drawRect.left - touchAdditionalWidth, drawRect.top, drawRect.right + touchAdditionalWidth,
					drawRect.bottom, columnPaint);
			if (column.hasLabels() || column.hasLabelsOnlyForSelected()) {
				drawLabel(canvas, column, columnValue, isStacked, labelOffset);
			}
		}
	}

	private void checkRectToDraw(int columnIndex, int valueIndex) {
		if (drawRect.contains(touchedPoint.x, touchedPoint.y)) {
			selectedValue.set(columnIndex, valueIndex);
		}
	}

	private float calculateColumnWidth(final ChartCalculator chartCalculator, float fillRatio) {
		// columnWidht should be at least 2 px
		float columnWidth = fillRatio * chartCalculator.getContentRect().width()
				/ chartCalculator.getVisibleViewport().width();
		if (columnWidth < 2) {
			columnWidth = 2;
		}
		return columnWidth;
	}

	private void calculateRectToDraw(ColumnValue columnValue, float left, float right, float rawBaseY, float rawY) {
		// Calculate rect that will be drawn as column, subcolumn or label background.
		drawRect.left = left;
		drawRect.right = right;
		if (columnValue.getValue() >= DEFAULT_BASE_VALUE) {
			drawRect.top = rawY;
			drawRect.bottom = rawBaseY - subcolumnSpacing;
		} else {
			drawRect.bottom = rawY;
			drawRect.top = rawBaseY + subcolumnSpacing;
		}
	}

	private void drawLabel(Canvas canvas, Column column, ColumnValue columnValue, boolean isStacked, float offset) {
		final ChartCalculator chartCalculator = chart.getChartCalculator();
		final int nummChars = column.getFormatter().formatValue(labelBuffer, columnValue.getValue());
		final float labelWidth = labelPaint.measureText(labelBuffer, labelBuffer.length - nummChars, nummChars);
		final int labelHeight = Math.abs(fontMetrics.ascent);
		float left = drawRect.centerX() - labelWidth / 2 - labelMargin;
		float right = drawRect.centerX() + labelWidth / 2 + labelMargin;
		float top;
		float bottom;
		if (isStacked && labelHeight < drawRect.height()) {
			// For stacked columns draw label only if label height is less than subcolumn height
			if (columnValue.getValue() >= DEFAULT_BASE_VALUE) {
				top = drawRect.top;
				bottom = drawRect.top + labelHeight + labelMargin * 2;
			} else {
				top = drawRect.bottom - labelHeight - labelMargin * 2;
				bottom = drawRect.bottom;
			}
			canvas.drawText(labelBuffer, labelBuffer.length - nummChars, nummChars, left + labelMargin, bottom
					- labelMargin, labelPaint);
		} else if (!isStacked) {
			// For not stacked draw label at the top for positive and at the bottom for negative values
			if (columnValue.getValue() >= DEFAULT_BASE_VALUE) {
				top = drawRect.top - offset - labelHeight - labelMargin * 2;
				if (top < chartCalculator.getContentRect().top) {
					top = drawRect.top + offset;
					bottom = drawRect.top + offset + labelHeight + labelMargin * 2;
				} else {
					bottom = drawRect.top - offset;
				}
			} else {
				bottom = drawRect.bottom + offset + labelHeight + labelMargin * 2;
				if (bottom > chartCalculator.getContentRect().bottom) {
					top = drawRect.bottom - offset - labelHeight - labelMargin * 2;
					bottom = drawRect.bottom - offset;
				} else {
					top = drawRect.bottom + offset;
				}
			}
			int orginColor = labelPaint.getColor();
			labelPaint.setColor(columnValue.getDarkenColor());
			canvas.drawRect(left, top, right, bottom, labelPaint);
			labelPaint.setColor(orginColor);
			canvas.drawText(labelBuffer, labelBuffer.length - nummChars, nummChars, left + labelMargin, bottom
					- labelMargin, labelPaint);
		} else {
			// do nothing
		}
	}

}
