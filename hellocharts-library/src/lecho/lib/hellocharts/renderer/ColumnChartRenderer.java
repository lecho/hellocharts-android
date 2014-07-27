package lecho.lib.hellocharts.renderer;

import lecho.lib.hellocharts.Chart;
import lecho.lib.hellocharts.ChartCalculator;
import lecho.lib.hellocharts.ColumnChartDataProvider;
import lecho.lib.hellocharts.model.Column;
import lecho.lib.hellocharts.model.ColumnChartData;
import lecho.lib.hellocharts.model.ColumnValue;
import lecho.lib.hellocharts.model.SelectedValue;
import lecho.lib.hellocharts.util.Utils;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.Cap;
import android.graphics.Paint.FontMetricsInt;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.Typeface;

public class ColumnChartRenderer implements ChartRenderer {
	public static final int DEFAULT_LABEL_MARGIN_DP = 4;
	public static final int DEFAULT_SUBCOLUMN_SPACING_DP = 1;
	public static final int DEFAULT_COLUMN_TOUCH_ADDITIONAL_WIDTH_DP = 2;

	private static final float DEFAULT_BASE_VALUE = 0.0f;
	private static final int MODE_DRAW = 0;
	private static final int MODE_CHECK_TOUCH = 1;
	private static final int MODE_HIGHLIGHT = 2;

	private Chart chart;
	private ColumnChartDataProvider dataProvider;

	private int mLabelMargin;
	private int labelOffset;
	private int touchAdditionalWidth;
	private int subcolumnSpacing;
	private Paint mColumnPaint = new Paint();
	private Paint labelPaint = new Paint();
	private RectF drawRect = new RectF();
	private PointF touchedPoint = new PointF();
	private SelectedValue selectedValue = new SelectedValue();
	private char[] labelBuffer = new char[32];
	private FontMetricsInt fontMetrics = new FontMetricsInt();
	private RectF dataBoundaries = new RectF();

	private float density;
	private float scaledDensity;

	private boolean hasAutoDataBoundaries = true;
	private boolean isViewportAutoCalculated = true;

	public ColumnChartRenderer(Context context, Chart chart, ColumnChartDataProvider dataProvider) {
		this.chart = chart;
		this.dataProvider = dataProvider;
		density = context.getResources().getDisplayMetrics().density;
		scaledDensity = context.getResources().getDisplayMetrics().scaledDensity;
		mLabelMargin = Utils.dp2px(density, DEFAULT_LABEL_MARGIN_DP);
		labelOffset = mLabelMargin;
		subcolumnSpacing = Utils.dp2px(density, DEFAULT_SUBCOLUMN_SPACING_DP);
		touchAdditionalWidth = Utils.dp2px(density, DEFAULT_COLUMN_TOUCH_ADDITIONAL_WIDTH_DP);

		mColumnPaint.setAntiAlias(true);
		mColumnPaint.setStyle(Paint.Style.FILL);
		mColumnPaint.setStrokeCap(Cap.SQUARE);

		labelPaint.setAntiAlias(true);
		labelPaint.setColor(Color.WHITE);
		labelPaint.setStyle(Paint.Style.FILL);
		labelPaint.setTextAlign(Align.LEFT);
		labelPaint.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
	}

	@Override
	public void initRenderer() {
		if (hasAutoDataBoundaries) {
			calculateDataBoundaries();
		}
		if (isViewportAutoCalculated) {
			chart.getChartCalculator().calculateViewport(dataBoundaries);
		}
		labelPaint.setTextSize(Utils.sp2px(scaledDensity, chart.getChartData().getLabelsTextSize()));
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
		// Do nothing
	}

	public boolean checkTouch(float touchX, float touchY) {
		final ColumnChartData data = dataProvider.getColumnChartData();
		if (data.isStacked()) {
			checkTouchForStacked(touchX, touchY);
		} else {
			checkTouchForSubcolumns(touchX, touchY);
		}
		return isTouched();
	}

	public boolean isTouched() {
		return selectedValue.isSet();
	}

	public void clearTouch() {
		selectedValue.clear();
	}

	@Override
	public void callTouchListener() {
		chart.callTouchListener(selectedValue);

	}

	@Override
	public void setDataBoundaries(RectF dataBoundaries) {
		if (null == dataBoundaries) {
			hasAutoDataBoundaries = true;
			initRenderer();
		} else {
			hasAutoDataBoundaries = false;
			this.dataBoundaries = dataBoundaries;
		}
	}

	@Override
	public RectF getDataBoundaries() {
		return dataBoundaries;
	}

	@Override
	public void setViewport(RectF viewport) {
		if (null == viewport) {
			this.isViewportAutoCalculated = false;
			chart.getChartCalculator().mCurrentViewport.set(chart.getChartCalculator().mMaximumViewport);
		} else {
			this.isViewportAutoCalculated = true;
			chart.getChartCalculator().mCurrentViewport.set(viewport.left, viewport.bottom, viewport.right,
					viewport.top);
			chart.getChartCalculator().constrainViewport();
		}
	}

	@Override
	public RectF getViewport() {
		RectF viewport = chart.getChartCalculator().mCurrentViewport;
		return new RectF(viewport.left, viewport.bottom, viewport.right, viewport.top);
	}

	private void calculateDataBoundaries() {
		ColumnChartData data = dataProvider.getColumnChartData();
		dataBoundaries.set(-0.5f, 0, data.getColumns().size() - 0.5f, 0);
		if (data.isStacked()) {
			calculateBoundariesStacked(data);
		} else {
			calculateBoundariesForSubcolumns(data);
		}
	}

	private void calculateBoundariesForSubcolumns(ColumnChartData data) {
		for (Column column : data.getColumns()) {
			for (ColumnValue columnValue : column.getValues()) {
				if (columnValue.getValue() >= 0 && columnValue.getValue() > dataBoundaries.top) {
					dataBoundaries.top = columnValue.getValue();
				}
				if (columnValue.getValue() < 0 && columnValue.getValue() < dataBoundaries.bottom) {
					dataBoundaries.bottom = columnValue.getValue();
				}
			}
		}
	}

	private void calculateBoundariesStacked(ColumnChartData data) {
		for (Column column : data.getColumns()) {
			float sumPositive = 0;
			float sumNegative = 0;
			for (ColumnValue columnValue : column.getValues()) {
				if (columnValue.getValue() >= 0) {
					sumPositive += columnValue.getValue();
				} else {
					sumNegative += columnValue.getValue();
				}
			}
			if (sumPositive > dataBoundaries.top) {
				dataBoundaries.top = sumPositive;
			}
			if (sumNegative < dataBoundaries.bottom) {
				dataBoundaries.bottom = sumNegative;
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
		// Columns are indexes from 0 to n, column index is also cikynb X value
		final float rawValueX = chartCalculator.calculateRawX(columnIndex);
		final float halfColumnWidth = columnWidth / 2;
		final float rawBaseValueY = chartCalculator.calculateRawY(DEFAULT_BASE_VALUE);
		// First subcolumn will starts at the left edge of current column,
		// rawValueX is horizontal center of that column
		float subcolumnRawValueX = rawValueX - halfColumnWidth;
		int valueIndex = 0;
		for (ColumnValue columnValue : column.getValues()) {
			mColumnPaint.setColor(columnValue.getColor());
			if (subcolumnRawValueX > rawValueX + halfColumnWidth) {
				break;
			}
			final float rawValueY = chartCalculator.calculateRawY(columnValue.getValue());
			calculateRectToDraw(columnValue, subcolumnRawValueX, subcolumnRawValueX + subcolumnWidth, rawBaseValueY,
					rawValueY);
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
			subcolumnRawValueX += subcolumnWidth + subcolumnSpacing;
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
		final float rawValueX = chartCalculator.calculateRawX(columnIndex);
		final float halfColumnWidth = columnWidth / 2;
		float mostPositiveValue = DEFAULT_BASE_VALUE;
		float mostNegativeValue = DEFAULT_BASE_VALUE;
		float baseValue = DEFAULT_BASE_VALUE;
		int valueIndex = 0;
		for (ColumnValue columnValue : column.getValues()) {
			mColumnPaint.setColor(columnValue.getColor());
			if (columnValue.getValue() >= DEFAULT_BASE_VALUE) {
				// Using values instead of raw pixels make code easier to
				// understand(for me)
				baseValue = mostPositiveValue;
				mostPositiveValue += columnValue.getValue();
			} else {
				baseValue = mostNegativeValue;
				mostNegativeValue += columnValue.getValue();
			}
			final float rawBaseValueY = chartCalculator.calculateRawY(baseValue);
			final float rawValueY = chartCalculator.calculateRawY(baseValue + columnValue.getValue());
			calculateRectToDraw(columnValue, rawValueX - halfColumnWidth, rawValueX + halfColumnWidth, rawBaseValueY,
					rawValueY);
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
		canvas.drawRect(drawRect, mColumnPaint);
		if (column.hasLabels()) {
			drawLabel(canvas, column, columnValue, isStacked, labelOffset);
		}
	}

	private void highlightSubcolumn(Canvas canvas, Column column, ColumnValue columnValue, int valueIndex,
			boolean isStacked) {
		if (selectedValue.secondIndex == valueIndex) {
			mColumnPaint.setColor(columnValue.getDarkenColor());
			canvas.drawRect(drawRect.left - touchAdditionalWidth, drawRect.top, drawRect.right + touchAdditionalWidth,
					drawRect.bottom, mColumnPaint);
			if (column.hasLabels()) {
				drawLabel(canvas, column, columnValue, isStacked, labelOffset);
			}
		}
	}

	private void checkRectToDraw(int columnIndex, int valueIndex) {
		if (drawRect.contains(touchedPoint.x, touchedPoint.y)) {
			selectedValue.firstIndex = columnIndex;
			selectedValue.secondIndex = valueIndex;
		}
	}

	private float calculateColumnWidth(final ChartCalculator chartCalculator, float fillRatio) {
		// columnWidht should be at least 2 px
		float columnWidth = fillRatio * chartCalculator.mContentRect.width() / chartCalculator.mCurrentViewport.width();
		if (columnWidth < 2) {
			columnWidth = 2;
		}
		return columnWidth;
	}

	private void calculateRectToDraw(ColumnValue columnValue, float left, float right, float rawBaseValueY,
			float rawValueY) {
		drawRect.left = left;
		drawRect.right = right;
		if (columnValue.getValue() >= DEFAULT_BASE_VALUE) {
			drawRect.top = rawValueY;
			drawRect.bottom = rawBaseValueY - subcolumnSpacing;
		} else {
			drawRect.bottom = rawValueY;
			drawRect.top = rawBaseValueY + subcolumnSpacing;
		}
	}

	private void drawLabel(Canvas canvas, Column column, ColumnValue columnValue, boolean isStacked, float offset) {
		final ChartCalculator chartCalculator = chart.getChartCalculator();
		final int nummChars = column.getFormatter().formatValue(labelBuffer, columnValue.getValue());
		final float labelWidth = labelPaint.measureText(labelBuffer, labelBuffer.length - nummChars, nummChars);
		final int labelHeight = Math.abs(fontMetrics.ascent);
		float left = drawRect.centerX() - labelWidth / 2 - mLabelMargin;
		float right = drawRect.centerX() + labelWidth / 2 + mLabelMargin;
		float top;
		float bottom;
		if (isStacked && labelHeight < drawRect.height()) {
			if (columnValue.getValue() >= DEFAULT_BASE_VALUE) {
				top = drawRect.top;
				bottom = drawRect.top + labelHeight + mLabelMargin * 2;
			} else {
				top = drawRect.bottom - labelHeight - mLabelMargin * 2;
				bottom = drawRect.bottom;
			}
			canvas.drawText(labelBuffer, labelBuffer.length - nummChars, nummChars, left + mLabelMargin, bottom
					- mLabelMargin, labelPaint);
		} else if (!isStacked) {
			if (columnValue.getValue() >= DEFAULT_BASE_VALUE) {
				top = drawRect.top - offset - labelHeight - mLabelMargin * 2;
				if (top < chartCalculator.mContentRect.top) {
					top = drawRect.top + offset;
					bottom = drawRect.top + offset + labelHeight + mLabelMargin * 2;
				} else {
					bottom = drawRect.top - offset;
				}
			} else {
				bottom = drawRect.bottom + offset + labelHeight + mLabelMargin * 2;
				if (bottom > chartCalculator.mContentRect.bottom) {
					top = drawRect.bottom - offset - labelHeight - mLabelMargin * 2;
					bottom = drawRect.bottom - offset;
				} else {
					top = drawRect.bottom + offset;
				}
			}
			int orginColor = labelPaint.getColor();
			labelPaint.setColor(columnValue.getDarkenColor());
			canvas.drawRect(left, top, right, bottom, labelPaint);
			labelPaint.setColor(orginColor);
			canvas.drawText(labelBuffer, labelBuffer.length - nummChars, nummChars, left + mLabelMargin, bottom
					- mLabelMargin, labelPaint);
		} else {
			// do nothing
		}
	}

}
