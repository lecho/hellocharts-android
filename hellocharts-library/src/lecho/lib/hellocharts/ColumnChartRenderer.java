package lecho.lib.hellocharts;

import lecho.lib.hellocharts.model.Column;
import lecho.lib.hellocharts.model.ColumnChartData;
import lecho.lib.hellocharts.model.ColumnValue;
import lecho.lib.hellocharts.model.SelectedValue;
import lecho.lib.hellocharts.utils.Utils;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.Cap;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;

public class ColumnChartRenderer implements ChartRenderer {
	private static final int DEFAULT_SUBCOLUMN_SPACING_DP = 1;
	private static final int DEFAULT_TOUCH_STROKE_WIDTH_DP = 4;
	private static final float DEFAULT_BASE_VALUE = 0.0f;
	private static final int DEFAULT_POPUP_MARGIN_DP = 4;
	private static final int DEFAULT_TEXT_COLOR = Color.WHITE;
	private static final int MODE_DRAW = 0;
	private static final int MODE_CHECK_TOUCH = 1;
	private static final int MODE_HIGHLIGHT = 2;
	private int mPopupMargin;
	private Paint mColumnPaint = new Paint();
	private Paint mPointAndPopupPaint = new Paint();
	private Context mContext;
	private ColumnChartView mChart;
	private int mSubcolumnSpacing;
	private RectF mRectToDraw = new RectF();
	private Rect mTextBounds = new Rect();
	private PointF mTouchedPoint = new PointF();
	private SelectedValue mSelectedValue = new SelectedValue();

	public ColumnChartRenderer(Context context, ColumnChartView chart) {
		mContext = context;
		mChart = chart;
		mPopupMargin = Utils.dp2px(context, DEFAULT_POPUP_MARGIN_DP);
		mSubcolumnSpacing = Utils.dp2px(mContext, DEFAULT_SUBCOLUMN_SPACING_DP);

		mColumnPaint.setAntiAlias(true);
		mColumnPaint.setStyle(Paint.Style.FILL);
		mColumnPaint.setStrokeWidth(Utils.dp2px(mContext, DEFAULT_TOUCH_STROKE_WIDTH_DP));
		mColumnPaint.setStrokeCap(Cap.SQUARE);

		mPointAndPopupPaint.setAntiAlias(true);
		mPointAndPopupPaint.setStyle(Paint.Style.FILL);
		mPointAndPopupPaint.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
	}

	public void draw(Canvas canvas) {
		final ColumnChartData data = mChart.getData();
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

	public boolean checkTouch(float touchX, float touchY) {
		final ColumnChartData data = mChart.getData();
		if (data.isStacked()) {
			checkTouchForStacked(touchX, touchY);
		} else {
			checkTouchForSubcolumns(touchX, touchY);
		}
		return isTouched();
	}

	public boolean isTouched() {
		return mSelectedValue.isSet();
	}

	public void clearTouch() {
		mSelectedValue.clear();
	}

	@Override
	public void callTouchListener() {
		mChart.callTouchListener(mSelectedValue);

	}

	private void drawColumnsForSubcolumns(Canvas canvas) {
		final ColumnChartData data = mChart.getData();
		final ChartCalculator chartCalculator = mChart.getChartCalculator();
		final float columnWidth = calculateColumnWidth(chartCalculator, data.getFillRatio());
		int columnIndex = 0;
		for (Column column : data.getColumns()) {
			processColumnForSubcolumns(canvas, chartCalculator, column, columnWidth, columnIndex, MODE_DRAW);
			++columnIndex;
		}
	}

	private void highlightColumnsForSubcolumns(Canvas canvas) {
		final ColumnChartData data = mChart.getData();
		final ChartCalculator chartCalculator = mChart.getChartCalculator();
		final float columnWidth = calculateColumnWidth(chartCalculator, data.getFillRatio());
		Column column = data.getColumns().get(mSelectedValue.firstIndex);
		processColumnForSubcolumns(canvas, chartCalculator, column, columnWidth, mSelectedValue.firstIndex, MODE_HIGHLIGHT);
	}

	private void checkTouchForSubcolumns(float touchX, float touchY) {
		mTouchedPoint.x = touchX;
		mTouchedPoint.y = touchY;
		final ColumnChartData data = mChart.getData();
		final ChartCalculator chartCalculator = mChart.getChartCalculator();
		final float columnWidth = calculateColumnWidth(chartCalculator, data.getFillRatio());
		int columnIndex = 0;
		for (Column column : data.getColumns()) {
			// canvas is not needed for checking touch
			processColumnForSubcolumns(null, chartCalculator, column, columnWidth, columnIndex, MODE_CHECK_TOUCH);
			++columnIndex;
		}
	}

	private void processColumnForSubcolumns(Canvas canvas, ChartCalculator chartCalculator, Column column, float columnWidth,
			int columnIndex, int mode) {
		// For n subcolumns there will be n-1 spacing and there will be one subcolumn for every columnValue
		float subcolumnWidth = (columnWidth - (mSubcolumnSpacing * (column.getValues().size() - 1))) / column.getValues().size();
		if (subcolumnWidth < 1) {
			subcolumnWidth = 1;
		}
		// Columns are indexes from 0 to n, column index is also cikynb X value
		final float rawValueX = chartCalculator.calculateRawX(columnIndex);
		final float halfColumnWidth = columnWidth / 2;
		final float rawBaseValueY = chartCalculator.calculateRawY(DEFAULT_BASE_VALUE);
		// First subcolumn will starts at the left edge of current column, rawValueX is horizontal center of that column
		float subcolumnRawValueX = rawValueX - halfColumnWidth;
		int valueIndex = 0;
		for (ColumnValue columnValue : column.getValues()) {
			if (subcolumnRawValueX > rawValueX + halfColumnWidth) {
				break;
			}
			final float rawValueY = chartCalculator.calculateRawY(columnValue.getValue());
			calculateRectToDraw(subcolumnRawValueX, subcolumnRawValueX + subcolumnWidth, rawBaseValueY, rawValueY);
			switch (mode) {
			case MODE_DRAW:
				drawSubcolumn(canvas, column, columnValue);
				break;
			case MODE_HIGHLIGHT:
				highlightSubcolumn(canvas, column, columnValue, valueIndex);
				break;
			case MODE_CHECK_TOUCH:
				checkRectToDraw(columnIndex, valueIndex);
				break;
			default:
				// There no else, every case should be handled or exception will be thrown
				throw new IllegalStateException("Cannot process column in mode: " + mode);
			}
			subcolumnRawValueX += subcolumnWidth + mSubcolumnSpacing;
			++valueIndex;
		}
	}

	private void drawColumnForStacked(Canvas canvas) {
		final ColumnChartData data = mChart.getData();
		final ChartCalculator chartCalculator = mChart.getChartCalculator();
		final float columnWidth = calculateColumnWidth(chartCalculator, data.getFillRatio());
		// Columns are indexes from 0 to n, column index is also column X value
		int columnIndex = 0;
		for (Column column : data.getColumns()) {
			processColumnForStacked(canvas, chartCalculator, column, columnWidth, columnIndex, MODE_DRAW);
			++columnIndex;
		}
	}

	private void highlightColumnForStacked(Canvas canvas) {
		final ColumnChartData data = mChart.getData();
		final ChartCalculator chartCalculator = mChart.getChartCalculator();
		final float columnWidth = calculateColumnWidth(chartCalculator, data.getFillRatio());
		// Columns are indexes from 0 to n, column index is also column X value
		Column column = data.getColumns().get(mSelectedValue.firstIndex);
		processColumnForStacked(canvas, chartCalculator, column, columnWidth, mSelectedValue.firstIndex, MODE_HIGHLIGHT);
	}

	private void checkTouchForStacked(float touchX, float touchY) {
		mTouchedPoint.x = touchX;
		mTouchedPoint.y = touchY;
		final ColumnChartData data = mChart.getData();
		final ChartCalculator chartCalculator = mChart.getChartCalculator();
		final float columnWidth = calculateColumnWidth(chartCalculator, data.getFillRatio());
		int columnIndex = 0;
		for (Column column : data.getColumns()) {
			// canvas is not needed for checking touch
			processColumnForStacked(null, chartCalculator, column, columnWidth, columnIndex, MODE_CHECK_TOUCH);
			++columnIndex;
		}
	}

	private void processColumnForStacked(Canvas canvas, ChartCalculator chartCalculator, Column column, float columnWidth,
			int columnIndex, int mode) {
		final float rawValueX = chartCalculator.calculateRawX(columnIndex);
		final float halfColumnWidth = columnWidth / 2;
		float mostPositiveValue = DEFAULT_BASE_VALUE;
		float mostNegativeValue = DEFAULT_BASE_VALUE;
		float baseValue = DEFAULT_BASE_VALUE;
		int valueIndex = 0;
		for (ColumnValue columnValue : column.getValues()) {
			mColumnPaint.setColor(columnValue.getColor());
			if (columnValue.getValue() >= 0) {
				// Using values instead of raw pixels make code easier to understand
				baseValue = mostPositiveValue;
				mostPositiveValue += columnValue.getValue();
			} else {
				baseValue = mostNegativeValue;
				mostNegativeValue += columnValue.getValue();
			}
			final float rawBaseValueY = chartCalculator.calculateRawY(baseValue);
			final float rawValueY = chartCalculator.calculateRawY(baseValue + columnValue.getValue());
			calculateRectToDraw(rawValueX - halfColumnWidth, rawValueX + halfColumnWidth, rawBaseValueY, rawValueY);
			switch (mode) {
			case MODE_DRAW:
				drawSubcolumn(canvas, column, columnValue);
				break;
			case MODE_HIGHLIGHT:
				highlightSubcolumn(canvas, column, columnValue, valueIndex);
				break;
			case MODE_CHECK_TOUCH:
				checkRectToDraw(columnIndex, valueIndex);
				break;
			default:
				// There no else, every case should be handled or exception will be thrown
				throw new IllegalStateException("Cannot process column in mode: " + mode);
			}
			++valueIndex;
		}
	}

	private void drawSubcolumn(Canvas canvas, Column column, ColumnValue columnValue) {
		mColumnPaint.setColor(columnValue.getColor());
		canvas.drawRect(mRectToDraw, mColumnPaint);
		if (column.hasAnnotations()) {
			drawValueAnnotation(canvas, column, columnValue);
		}
	}

	private void highlightSubcolumn(Canvas canvas, Column column, ColumnValue columnValue, int valueIndex) {
		mColumnPaint.setColor(columnValue.getColor());
		if (mSelectedValue.secondIndex == valueIndex) {
			mColumnPaint.setStyle(Paint.Style.FILL_AND_STROKE);
			canvas.drawRect(mRectToDraw, mColumnPaint);
			mColumnPaint.setStyle(Paint.Style.FILL);
			if (column.hasAnnotations()) {
				drawValueAnnotation(canvas, column, columnValue);
			}
		}
	}

	private void checkRectToDraw(int columnIndex, int valueIndex) {
		if (mRectToDraw.contains(mTouchedPoint.x, mTouchedPoint.y)) {
			mSelectedValue.firstIndex = columnIndex;
			mSelectedValue.secondIndex = valueIndex;
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

	private void calculateRectToDraw(float left, float right, float rawBaseValueY, float rawValueY) {
		mRectToDraw.left = left;
		mRectToDraw.right = right;
		if (rawValueY <= rawBaseValueY) {
			mRectToDraw.top = rawValueY;
			mRectToDraw.bottom = rawBaseValueY;
		} else {
			mRectToDraw.bottom = rawValueY;
			mRectToDraw.top = rawBaseValueY;
		}
	}

	private void drawValueAnnotation(Canvas canvas, Column column, ColumnValue columnValue) {
		final ChartCalculator chartCalculator = mChart.getChartCalculator();
		mPointAndPopupPaint.setTextAlign(Align.LEFT);
		mPointAndPopupPaint.setTextSize(Utils.sp2px(mContext, column.getTextSize()));
		mPointAndPopupPaint.setColor(columnValue.getColor());
		final String text = column.getFormatter().formatValue(columnValue);
		mPointAndPopupPaint.getTextBounds(text, 0, text.length(), mTextBounds);
		float left = mRectToDraw.centerX() - (mTextBounds.width() / 2) - mPopupMargin;
		float right = mRectToDraw.centerX() + (mTextBounds.width() / 2) + mPopupMargin;
		float top = mRectToDraw.top - mPopupMargin - mTextBounds.height() - mPopupMargin * 2;
		float bottom = mRectToDraw.top - mPopupMargin;
		if (top < chartCalculator.mContentRect.top) {
			top = mRectToDraw.top + mPopupMargin;
			bottom = mRectToDraw.top + mPopupMargin + mTextBounds.height() + mPopupMargin * 2;
		}
		final RectF popup = new RectF(left, top, right, bottom);
		canvas.drawRoundRect(popup, mPopupMargin, mPopupMargin, mPointAndPopupPaint);
		final int color = mPointAndPopupPaint.getColor();
		mPointAndPopupPaint.setColor(DEFAULT_TEXT_COLOR);
		canvas.drawText(text, left + mPopupMargin, bottom - mPopupMargin, mPointAndPopupPaint);
		mPointAndPopupPaint.setColor(color);
	}

}
