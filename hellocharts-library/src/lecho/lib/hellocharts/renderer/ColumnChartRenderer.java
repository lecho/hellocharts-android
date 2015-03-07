package lecho.lib.hellocharts.renderer;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Cap;
import android.graphics.PointF;
import android.graphics.RectF;

import lecho.lib.hellocharts.model.Column;
import lecho.lib.hellocharts.model.ColumnChartData;
import lecho.lib.hellocharts.model.SelectedValue.SelectedValueType;
import lecho.lib.hellocharts.model.SubcolumnValue;
import lecho.lib.hellocharts.model.Viewport;
import lecho.lib.hellocharts.provider.ColumnChartDataProvider;
import lecho.lib.hellocharts.util.ChartUtils;
import lecho.lib.hellocharts.view.Chart;

/**
 * Magic renderer for ColumnChart.
 */
public class ColumnChartRenderer extends AbstractChartRenderer {
    public static final int DEFAULT_SUBCOLUMN_SPACING_DP = 1;
    public static final int DEFAULT_COLUMN_TOUCH_ADDITIONAL_WIDTH_DP = 4;

    private static final int MODE_DRAW = 0;
    private static final int MODE_CHECK_TOUCH = 1;
    private static final int MODE_HIGHLIGHT = 2;

    private ColumnChartDataProvider dataProvider;

    /**
     * Additional width for hightlighted column, used to give tauch feedback.
     */
    private int touchAdditionalWidth;

    /**
     * Spacing between sub-columns.
     */
    private int subcolumnSpacing;

    /**
     * Paint used to draw every column.
     */
    private Paint columnPaint = new Paint();

    /**
     * Holds coordinates for currently processed column/sub-column.
     */
    private RectF drawRect = new RectF();

    /**
     * Coordinated of user tauch.
     */
    private PointF touchedPoint = new PointF();

    private float fillRatio;

    private float baseValue;

    private Viewport tempMaximumViewport = new Viewport();

    public ColumnChartRenderer(Context context, Chart chart, ColumnChartDataProvider dataProvider) {
        super(context, chart);
        this.dataProvider = dataProvider;
        subcolumnSpacing = ChartUtils.dp2px(density, DEFAULT_SUBCOLUMN_SPACING_DP);
        touchAdditionalWidth = ChartUtils.dp2px(density, DEFAULT_COLUMN_TOUCH_ADDITIONAL_WIDTH_DP);

        columnPaint.setAntiAlias(true);
        columnPaint.setStyle(Paint.Style.FILL);
        columnPaint.setStrokeCap(Cap.SQUARE);
    }

    @Override
    public void onChartSizeChanged() {
    }

    @Override
    public void onChartDataChanged() {
        super.onChartDataChanged();
        ColumnChartData data = dataProvider.getColumnChartData();
        fillRatio = data.getFillRatio();
        baseValue = data.getBaseValue();

        onChartViewportChanged();
    }

    @Override
    public void onChartViewportChanged() {
        if (isViewportCalculationEnabled) {
            calculateMaxViewport();
            computator.setMaxViewport(tempMaximumViewport);
            computator.setCurrentViewport(computator.getMaximumViewport());
        }
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
        selectedValue.clear();
        final ColumnChartData data = dataProvider.getColumnChartData();
        if (data.isStacked()) {
            checkTouchForStacked(touchX, touchY);
        } else {
            checkTouchForSubcolumns(touchX, touchY);
        }
        return isTouched();
    }

    private void calculateMaxViewport() {
        final ColumnChartData data = dataProvider.getColumnChartData();
        // Column chart always has X values from 0 to numColumns-1, to add some margin on the left and right I added
        // extra 0.5 to the each side, that margins will be negative scaled according to number of columns, so for more
        // columns there will be less margin.
        tempMaximumViewport.set(-0.5f, baseValue, data.getColumns().size() - 0.5f, baseValue);
        if (data.isStacked()) {
            calculateMaxViewportForStacked(data);
        } else {
            calculateMaxViewportForSubcolumns(data);
        }
    }

    private void calculateMaxViewportForSubcolumns(ColumnChartData data) {
        for (Column column : data.getColumns()) {
            for (SubcolumnValue columnValue : column.getValues()) {
                if (columnValue.getValue() >= baseValue && columnValue.getValue() > tempMaximumViewport.top) {
                    tempMaximumViewport.top = columnValue.getValue();
                }
                if (columnValue.getValue() < baseValue && columnValue.getValue() < tempMaximumViewport.bottom) {
                    tempMaximumViewport.bottom = columnValue.getValue();
                }
            }
        }
    }

    private void calculateMaxViewportForStacked(ColumnChartData data) {
        for (Column column : data.getColumns()) {
            float sumPositive = baseValue;
            float sumNegative = baseValue;
            for (SubcolumnValue columnValue : column.getValues()) {
                if (columnValue.getValue() >= baseValue) {
                    sumPositive += columnValue.getValue();
                } else {
                    sumNegative += columnValue.getValue();
                }
            }
            if (sumPositive > tempMaximumViewport.top) {
                tempMaximumViewport.top = sumPositive;
            }
            if (sumNegative < tempMaximumViewport.bottom) {
                tempMaximumViewport.bottom = sumNegative;
            }
        }
    }

    private void drawColumnsForSubcolumns(Canvas canvas) {
        final ColumnChartData data = dataProvider.getColumnChartData();
        final float columnWidth = calculateColumnWidth();
        int columnIndex = 0;
        for (Column column : data.getColumns()) {
            processColumnForSubcolumns(canvas, column, columnWidth, columnIndex, MODE_DRAW);
            ++columnIndex;
        }
    }

    private void highlightColumnsForSubcolumns(Canvas canvas) {
        final ColumnChartData data = dataProvider.getColumnChartData();
        final float columnWidth = calculateColumnWidth();
        Column column = data.getColumns().get(selectedValue.getFirstIndex());
        processColumnForSubcolumns(canvas, column, columnWidth, selectedValue.getFirstIndex(), MODE_HIGHLIGHT);
    }

    private void checkTouchForSubcolumns(float touchX, float touchY) {
        // Using member variable to hold touch point to avoid too much parameters in methods.
        touchedPoint.x = touchX;
        touchedPoint.y = touchY;
        final ColumnChartData data = dataProvider.getColumnChartData();
        final float columnWidth = calculateColumnWidth();
        int columnIndex = 0;
        for (Column column : data.getColumns()) {
            // canvas is not needed for checking touch
            processColumnForSubcolumns(null, column, columnWidth, columnIndex, MODE_CHECK_TOUCH);
            ++columnIndex;
        }
    }

    private void processColumnForSubcolumns(Canvas canvas, Column column, float columnWidth, int columnIndex,
                                            int mode) {
        // For n subcolumns there will be n-1 spacing and there will be one
        // subcolumn for every columnValue
        float subcolumnWidth = (columnWidth - (subcolumnSpacing * (column.getValues().size() - 1)))
                / column.getValues().size();
        if (subcolumnWidth < 1) {
            subcolumnWidth = 1;
        }
        // Columns are indexes from 0 to n, column index is also column X value
        final float rawX = computator.computeRawX(columnIndex);
        final float halfColumnWidth = columnWidth / 2;
        final float baseRawY = computator.computeRawY(baseValue);
        // First subcolumn will starts at the left edge of current column,
        // rawValueX is horizontal center of that column
        float subcolumnRawX = rawX - halfColumnWidth;
        int valueIndex = 0;
        for (SubcolumnValue columnValue : column.getValues()) {
            columnPaint.setColor(columnValue.getColor());
            if (subcolumnRawX > rawX + halfColumnWidth) {
                break;
            }
            final float rawY = computator.computeRawY(columnValue.getValue());
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
        final float columnWidth = calculateColumnWidth();
        // Columns are indexes from 0 to n, column index is also column X value
        int columnIndex = 0;
        for (Column column : data.getColumns()) {
            processColumnForStacked(canvas, column, columnWidth, columnIndex, MODE_DRAW);
            ++columnIndex;
        }
    }

    private void highlightColumnForStacked(Canvas canvas) {
        final ColumnChartData data = dataProvider.getColumnChartData();
        final float columnWidth = calculateColumnWidth();
        // Columns are indexes from 0 to n, column index is also column X value
        Column column = data.getColumns().get(selectedValue.getFirstIndex());
        processColumnForStacked(canvas, column, columnWidth, selectedValue.getFirstIndex(), MODE_HIGHLIGHT);
    }

    private void checkTouchForStacked(float touchX, float touchY) {
        touchedPoint.x = touchX;
        touchedPoint.y = touchY;
        final ColumnChartData data = dataProvider.getColumnChartData();
        final float columnWidth = calculateColumnWidth();
        int columnIndex = 0;
        for (Column column : data.getColumns()) {
            // canvas is not needed for checking touch
            processColumnForStacked(null, column, columnWidth, columnIndex, MODE_CHECK_TOUCH);
            ++columnIndex;
        }
    }

    private void processColumnForStacked(Canvas canvas, Column column, float columnWidth, int columnIndex, int mode) {
        final float rawX = computator.computeRawX(columnIndex);
        final float halfColumnWidth = columnWidth / 2;
        float mostPositiveValue = baseValue;
        float mostNegativeValue = baseValue;
        float subcolumnBaseValue = baseValue;
        int valueIndex = 0;
        for (SubcolumnValue columnValue : column.getValues()) {
            columnPaint.setColor(columnValue.getColor());
            if (columnValue.getValue() >= baseValue) {
                // Using values instead of raw pixels make code easier to
                // understand(for me)
                subcolumnBaseValue = mostPositiveValue;
                mostPositiveValue += columnValue.getValue();
            } else {
                subcolumnBaseValue = mostNegativeValue;
                mostNegativeValue += columnValue.getValue();
            }
            final float rawBaseY = computator.computeRawY(subcolumnBaseValue);
            final float rawY = computator.computeRawY(subcolumnBaseValue + columnValue.getValue());
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

    private void drawSubcolumn(Canvas canvas, Column column, SubcolumnValue columnValue, boolean isStacked) {
        canvas.drawRect(drawRect, columnPaint);
        if (column.hasLabels()) {
            drawLabel(canvas, column, columnValue, isStacked, labelOffset);
        }
    }

    private void highlightSubcolumn(Canvas canvas, Column column, SubcolumnValue columnValue, int valueIndex,
                                    boolean isStacked) {
        if (selectedValue.getSecondIndex() == valueIndex) {
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
            selectedValue.set(columnIndex, valueIndex, SelectedValueType.COLUMN);
        }
    }

    private float calculateColumnWidth() {
        // columnWidht should be at least 2 px
        float columnWidth = fillRatio * computator.getContentRectMinusAllMargins().width() / computator
                .getVisibleViewport().width();
        if (columnWidth < 2) {
            columnWidth = 2;
        }
        return columnWidth;
    }

    private void calculateRectToDraw(SubcolumnValue columnValue, float left, float right, float rawBaseY, float rawY) {
        // Calculate rect that will be drawn as column, subcolumn or label background.
        drawRect.left = left;
        drawRect.right = right;
        if (columnValue.getValue() >= baseValue) {
            drawRect.top = rawY;
            drawRect.bottom = rawBaseY - subcolumnSpacing;
        } else {
            drawRect.bottom = rawY;
            drawRect.top = rawBaseY + subcolumnSpacing;
        }
    }

    private void drawLabel(Canvas canvas, Column column, SubcolumnValue columnValue, boolean isStacked, float offset) {
        final int numChars = column.getFormatter().formatChartValue(labelBuffer, columnValue);

        if (numChars == 0) {
            // No need to draw empty label
            return;
        }

        final float labelWidth = labelPaint.measureText(labelBuffer, labelBuffer.length - numChars, numChars);
        final int labelHeight = Math.abs(fontMetrics.ascent);
        float left = drawRect.centerX() - labelWidth / 2 - labelMargin;
        float right = drawRect.centerX() + labelWidth / 2 + labelMargin;
        float top;
        float bottom;
        if (isStacked && labelHeight < drawRect.height() - (2 * labelMargin)) {
            // For stacked columns draw label only if label height is less than subcolumn height - (2 * labelMargin).
            if (columnValue.getValue() >= baseValue) {
                top = drawRect.top;
                bottom = drawRect.top + labelHeight + labelMargin * 2;
            } else {
                top = drawRect.bottom - labelHeight - labelMargin * 2;
                bottom = drawRect.bottom;
            }
        } else if (!isStacked) {
            // For not stacked draw label at the top for positive and at the bottom for negative values
            if (columnValue.getValue() >= baseValue) {
                top = drawRect.top - offset - labelHeight - labelMargin * 2;
                if (top < computator.getContentRectMinusAllMargins().top) {
                    top = drawRect.top + offset;
                    bottom = drawRect.top + offset + labelHeight + labelMargin * 2;
                } else {
                    bottom = drawRect.top - offset;
                }
            } else {
                bottom = drawRect.bottom + offset + labelHeight + labelMargin * 2;
                if (bottom > computator.getContentRectMinusAllMargins().bottom) {
                    top = drawRect.bottom - offset - labelHeight - labelMargin * 2;
                    bottom = drawRect.bottom - offset;
                } else {
                    top = drawRect.bottom + offset;
                }
            }
        } else {
            // Draw nothing.
            return;
        }

        labelBackgroundRect.set(left, top, right, bottom);
        drawLabelTextAndBackground(canvas, labelBuffer, labelBuffer.length - numChars, numChars,
                columnValue.getDarkenColor());

    }

}
