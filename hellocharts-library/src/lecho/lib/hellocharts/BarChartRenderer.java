package lecho.lib.hellocharts;

import lecho.lib.hellocharts.model.AnimatedValueWithColor;
import lecho.lib.hellocharts.model.Bar;
import lecho.lib.hellocharts.model.BarChartData;
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

public class BarChartRenderer implements ChartRenderer {
	private static final float DEFAULT_FILL_RATIO = 0.75f;
	private static final int DEFAULT_SUBBAR_SPACING_DP = 1;
	private static final int DEFAULT_TOUCH_STROKE_WIDTH_DP = 4;
	private static final float DEFAULT_BASE_VALUE = 0.0f;
	private static final int DEFAULT_POPUP_MARGIN_DP = 4;
	private static final int DEFAULT_TEXT_COLOR = Color.WHITE;
	private static final int MODE_DRAW = 0;
	private static final int MODE_CHECK_TOUCH = 1;
	private static final int MODE_HIGHLIGHT = 2;
	private int mPopupMargin;
	private Paint mBarPaint = new Paint();
	private Paint mPointAndPopupPaint = new Paint();
	private Context mContext;
	private BarChart mChart;
	private int mSubbarSpacing;
	private SelectedValue mSelectedValue = new SelectedValue();
	private RectF mRectToDraw = new RectF();
	private Rect mTextBounds = new Rect();
	private PointF mTouchedPoint = new PointF();

	public BarChartRenderer(Context context, BarChart chart) {
		mContext = context;
		mChart = chart;
		mPopupMargin = Utils.dp2px(context, DEFAULT_POPUP_MARGIN_DP);
		mSubbarSpacing = Utils.dp2px(mContext, DEFAULT_SUBBAR_SPACING_DP);

		mBarPaint.setAntiAlias(true);
		mBarPaint.setStyle(Paint.Style.FILL);
		mBarPaint.setStrokeWidth(Utils.dp2px(mContext, DEFAULT_TOUCH_STROKE_WIDTH_DP));
		mBarPaint.setStrokeCap(Cap.SQUARE);

		mPointAndPopupPaint.setAntiAlias(true);
		mPointAndPopupPaint.setStyle(Paint.Style.FILL);
		mPointAndPopupPaint.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
	}

	public void draw(Canvas canvas) {
		final BarChartData data = mChart.getData();
		if (data.isStacked()) {
			drawBarsForStacked(canvas);
			if (isTouched()) {
				highlightBarForStacked(canvas);
			}
		} else {
			drawBarsForSubbars(canvas);
			if (isTouched()) {
				highlightBarForSubbars(canvas);
			}
		}
	}

	public boolean checkTouch(float touchX, float touchY) {
		final BarChartData data = mChart.getData();
		if (data.isStacked()) {
			checkTouchForStacked(touchX, touchY);
		} else {
			checkTouchForSubbars(touchX, touchY);
		}
		return isTouched();
	}

	public boolean isTouched() {
		if (mSelectedValue.selectedBar >= 0 && mSelectedValue.selectedValue >= 0) {
			return true;
		} else {
			return false;
		}
	}

	public void clearTouch() {
		mSelectedValue.clear();
	}

	private void drawBarsForSubbars(Canvas canvas) {
		final BarChartData data = mChart.getData();
		final ChartCalculator chartCalculator = mChart.getChartCalculator();
		final float barWidth = calculateBarhWidth(chartCalculator);
		int barIndex = 0;
		for (Bar bar : data.getBars()) {
			processBarForSubbars(canvas, chartCalculator, bar, barWidth, barIndex, MODE_DRAW);
			++barIndex;
		}
	}

	private void highlightBarForSubbars(Canvas canvas) {
		final BarChartData data = mChart.getData();
		final ChartCalculator chartCalculator = mChart.getChartCalculator();
		final float barWidth = calculateBarhWidth(chartCalculator);
		Bar bar = data.getBars().get(mSelectedValue.selectedBar);
		processBarForSubbars(canvas, chartCalculator, bar, barWidth, mSelectedValue.selectedBar, MODE_HIGHLIGHT);
	}

	private void checkTouchForSubbars(float touchX, float touchY) {
		mTouchedPoint.x = touchX;
		mTouchedPoint.y = touchY;
		final BarChartData data = mChart.getData();
		final ChartCalculator chartCalculator = mChart.getChartCalculator();
		final float barWidth = calculateBarhWidth(chartCalculator);
		int barIndex = 0;
		for (Bar bar : data.getBars()) {
			// canvas is not needed for checking touch
			processBarForSubbars(null, chartCalculator, bar, barWidth, barIndex, MODE_CHECK_TOUCH);
			++barIndex;
		}
	}

	private void processBarForSubbars(Canvas canvas, ChartCalculator chartCalculator, Bar bar, float barWidth,
			int barIndex, int mode) {
		// For n subbars there will be n-1 spacing and there will be one subbar for every animatedValue
		float subbarWidth = (barWidth - (mSubbarSpacing * (bar.animatedValues.size() - 1))) / bar.animatedValues.size();
		if (subbarWidth < 1) {
			subbarWidth = 1;
		}
		// Bars are indexes from 0 to n, bar index is also bar X value
		final float rawValueX = chartCalculator.calculateRawX(barIndex);
		final float halfBarWidth = barWidth / 2;
		final float rawBaseValueY = chartCalculator.calculateRawY(DEFAULT_BASE_VALUE);
		// First subbar will starts at the left edge of current bar, rawValueX is horizontal center of that bar
		float subbarRawValueX = rawValueX - halfBarWidth;
		int valueIndex = 0;
		for (AnimatedValueWithColor animatedValueWithColor : bar.animatedValues) {
			if (subbarRawValueX > rawValueX + halfBarWidth) {
				break;
			}
			final float rawValueY = chartCalculator.calculateRawY(animatedValueWithColor.value);
			calculateRectToDraw(subbarRawValueX, subbarRawValueX + subbarWidth, rawBaseValueY, rawValueY);
			switch (mode) {
			case MODE_DRAW:
				drawSubbar(canvas, bar, animatedValueWithColor);
				break;
			case MODE_HIGHLIGHT:
				highlightSubbar(canvas, bar, animatedValueWithColor, valueIndex);
				break;
			case MODE_CHECK_TOUCH:
				checkRectToDraw(barIndex, valueIndex);
				break;
			default:
				// There no else, every case should be handled or exception will be thrown
				throw new IllegalStateException("Cannot process bar in mode: " + mode);
			}
			subbarRawValueX += subbarWidth + mSubbarSpacing;
			++valueIndex;
		}
	}

	private void drawBarsForStacked(Canvas canvas) {
		final BarChartData data = mChart.getData();
		final ChartCalculator chartCalculator = mChart.getChartCalculator();
		final float barWidth = calculateBarhWidth(chartCalculator);
		// Bars are indexes from 0 to n, bar index is also bar X value
		int barIndex = 0;
		for (Bar bar : data.getBars()) {
			processBarForStacked(canvas, chartCalculator, bar, barWidth, barIndex, MODE_DRAW);
			++barIndex;
		}
	}

	private void highlightBarForStacked(Canvas canvas) {
		final BarChartData data = mChart.getData();
		final ChartCalculator chartCalculator = mChart.getChartCalculator();
		final float barWidth = calculateBarhWidth(chartCalculator);
		// Bars are indexes from 0 to n, bar index is also bar X value
		Bar bar = data.getBars().get(mSelectedValue.selectedBar);
		processBarForStacked(canvas, chartCalculator, bar, barWidth, mSelectedValue.selectedBar, MODE_HIGHLIGHT);
	}

	private void checkTouchForStacked(float touchX, float touchY) {
		mTouchedPoint.x = touchX;
		mTouchedPoint.y = touchY;
		final BarChartData data = mChart.getData();
		final ChartCalculator chartCalculator = mChart.getChartCalculator();
		final float barWidth = calculateBarhWidth(chartCalculator);
		int barIndex = 0;
		for (Bar bar : data.getBars()) {
			// canvas is not needed for checking touch
			processBarForStacked(null, chartCalculator, bar, barWidth, barIndex, MODE_CHECK_TOUCH);
			++barIndex;
		}
	}

	private void processBarForStacked(Canvas canvas, ChartCalculator chartCalculator, Bar bar, float barWidth,
			int barIndex, int mode) {
		final float rawValueX = chartCalculator.calculateRawX(barIndex);
		final float halfBarWidth = barWidth / 2;
		float mostPositiveValue = DEFAULT_BASE_VALUE;
		float mostNegativeValue = DEFAULT_BASE_VALUE;
		float baseValue = DEFAULT_BASE_VALUE;
		int valueIndex = 0;
		for (AnimatedValueWithColor animatedValueWithColor : bar.animatedValues) {
			mBarPaint.setColor(animatedValueWithColor.color);
			if (animatedValueWithColor.value >= 0) {
				// Using values instead of raw pixels make code easier to understand
				baseValue = mostPositiveValue;
				mostPositiveValue += animatedValueWithColor.value;
			} else {
				baseValue = mostNegativeValue;
				mostNegativeValue += animatedValueWithColor.value;
			}
			final float rawBaseValueY = chartCalculator.calculateRawY(baseValue);
			final float rawValueY = chartCalculator.calculateRawY(baseValue + animatedValueWithColor.value);
			calculateRectToDraw(rawValueX - halfBarWidth, rawValueX + halfBarWidth, rawBaseValueY, rawValueY);
			switch (mode) {
			case MODE_DRAW:
				drawSubbar(canvas, bar, animatedValueWithColor);
				break;
			case MODE_HIGHLIGHT:
				highlightSubbar(canvas, bar, animatedValueWithColor, valueIndex);
				break;
			case MODE_CHECK_TOUCH:
				checkRectToDraw(barIndex, valueIndex);
				break;
			default:
				// There no else, every case should be handled or exception will be thrown
				throw new IllegalStateException("Cannot process bar in mode: " + mode);
			}
			++valueIndex;
		}
	}

	private void drawSubbar(Canvas canvas, Bar bar, AnimatedValueWithColor animatedValueWithColor) {
		mBarPaint.setColor(animatedValueWithColor.color);
		canvas.drawRect(mRectToDraw, mBarPaint);
		if (bar.hasValuesPopups) {
			drawValuePopup(canvas, bar, animatedValueWithColor);
		}
	}

	private void highlightSubbar(Canvas canvas, Bar bar, AnimatedValueWithColor animatedValueWithColor, int valueIndex) {
		mBarPaint.setColor(animatedValueWithColor.color);
		if (mSelectedValue.selectedValue == valueIndex) {
			mBarPaint.setStyle(Paint.Style.FILL_AND_STROKE);
			canvas.drawRect(mRectToDraw, mBarPaint);
			mBarPaint.setStyle(Paint.Style.FILL);
			if (bar.hasValuesPopups) {
				drawValuePopup(canvas, bar, animatedValueWithColor);
			}
		}
	}

	private void checkRectToDraw(int barIndex, int valueIndex) {
		if (mRectToDraw.contains(mTouchedPoint.x, mTouchedPoint.y)) {
			mSelectedValue.selectedBar = barIndex;
			mSelectedValue.selectedValue = valueIndex;
		}
	}

	private float calculateBarhWidth(final ChartCalculator chartCalculator) {
		// barWidht should be at least 2 px
		float barWidth = DEFAULT_FILL_RATIO * chartCalculator.mContentRect.width()
				/ chartCalculator.mCurrentViewport.width();
		if (barWidth < 2) {
			barWidth = 2;
		}
		return barWidth;
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

	private void drawValuePopup(Canvas canvas, Bar bar, AnimatedValueWithColor animatedValueWithColor) {
		final ChartCalculator chartCalculator = mChart.getChartCalculator();
		mPointAndPopupPaint.setTextAlign(Align.LEFT);
		mPointAndPopupPaint.setTextSize(Utils.sp2px(mContext, bar.textSize));
		mPointAndPopupPaint.setColor(animatedValueWithColor.color);
		final String text = bar.formatter.formatValue(animatedValueWithColor.value);
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

	private static class SelectedValue {
		public int selectedBar;
		public int selectedValue;

		public SelectedValue() {
			clear();
		}

		public void clear() {
			this.selectedBar = Integer.MIN_VALUE;
			this.selectedBar = Integer.MIN_VALUE;
		}

	}

}
