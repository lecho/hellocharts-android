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
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;

public class BarChartRenderer {
	private static final float DEFAULT_FILL_RATIO = 0.75f;
	private static final int DEFAULT_SUBBAR_SPACING_DP = 1;
	private static final float DEFAULT_BASE_VALUE = 0.0f;
	private static final int DEFAULT_TOUCH_RADIUS_DP = 12;
	private static final int DEFAULT_POPUP_MARGIN_DP = 4;
	private static final int DEFAULT_TEXT_COLOR = Color.WHITE;
	private int mPopupMargin;
	private Paint mBarPaint = new Paint();
	private Paint mPointAndPopupPaint = new Paint();
	private float mTouchRadius;
	private Context mContext;
	private BarChart mChart;
	private int mSubbarSpacing;

	public BarChartRenderer(Context context, BarChart chart) {
		mContext = context;
		mChart = chart;
		mPopupMargin = Utils.dp2px(context, DEFAULT_POPUP_MARGIN_DP);
		mTouchRadius = Utils.dp2px(context, DEFAULT_TOUCH_RADIUS_DP);
		mSubbarSpacing = Utils.dp2px(mContext, DEFAULT_SUBBAR_SPACING_DP);

		mBarPaint.setAntiAlias(true);
		mBarPaint.setStyle(Paint.Style.FILL);
		mBarPaint.setStrokeCap(Cap.SQUARE);

		mPointAndPopupPaint.setAntiAlias(true);
		mPointAndPopupPaint.setStyle(Paint.Style.FILL);
		mPointAndPopupPaint.setStrokeWidth(1);
		mPointAndPopupPaint.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
	}

	public void draw(Canvas canvas) {
		final BarChartData data = mChart.getData();
		if (data.isStacked) {
			drawStackedBars(canvas, data);
		} else {
			drawDefaultBars(canvas, data);
		}
	}

	private void drawDefaultBars(Canvas canvas, final BarChartData data) {
		final ChartCalculator chartCalculator = mChart.getChartCalculator();
		final float barWidth = calculateBarhWidth(chartCalculator);
		// Bars are indexes from 0 to n, bar index is also bar X value
		final float rawBaseValueY = chartCalculator.calculateRawY(DEFAULT_BASE_VALUE);
		int barIndex = 0;
		for (Bar bar : data.bars) {
			// For n subbars there will be n-1 spacing and there will be one subbar for every animatedValue
			float subbarWidth = (barWidth - (mSubbarSpacing * (bar.animatedValues.size() - 1)))
					/ bar.animatedValues.size();
			if (subbarWidth < 1) {
				subbarWidth = 1;
			}
			final float rawValueX = chartCalculator.calculateRawX(barIndex);
			// First subbar will starts at the left edge of current bar, rawValueX is horizontal center of that bar
			float subbarRawValueX = rawValueX - (barWidth / 2);
			for (AnimatedValueWithColor animatedValueWithColor : bar.animatedValues) {
				if (subbarRawValueX > rawValueX + (barWidth / 2)) {
					break;
				}
				mBarPaint.setColor(animatedValueWithColor.color);
				final float rawValueY = chartCalculator.calculateRawY(animatedValueWithColor.value);
				canvas.drawRect(subbarRawValueX, rawValueY, subbarRawValueX + subbarWidth, rawBaseValueY, mBarPaint);
				if (bar.hasValuesPopups) {
					drawValuePopup(canvas, bar, animatedValueWithColor, subbarRawValueX + (subbarWidth / 2), rawValueY);
				}
				subbarRawValueX += subbarWidth + mSubbarSpacing;
			}
			++barIndex;
		}
	}

	private void drawStackedBars(Canvas canvas, final BarChartData data) {
		final ChartCalculator chartCalculator = mChart.getChartCalculator();
		final float barWidth = calculateBarhWidth(chartCalculator);
		final float halfBarWidth = barWidth / 2;
		// Bars are indexes from 0 to n, bar index is also bar X value
		int barIndex = 0;
		for (Bar bar : data.bars) {
			final float rawValueX = chartCalculator.calculateRawX(barIndex);
			float mostPositiveValue = DEFAULT_BASE_VALUE;
			float mostNegativeValue = DEFAULT_BASE_VALUE;
			float baseValue = DEFAULT_BASE_VALUE;
			for (AnimatedValueWithColor animatedValueWithColor : bar.animatedValues) {
				mBarPaint.setColor(animatedValueWithColor.color);
				if (animatedValueWithColor.value >= 0) {
					// IMO using values instead of raw pixels make code easier to follow
					baseValue = mostPositiveValue;
					mostPositiveValue += animatedValueWithColor.value;
				} else {
					baseValue = mostNegativeValue;
					mostNegativeValue += animatedValueWithColor.value;
				}
				final float rawValueY = chartCalculator.calculateRawY(baseValue + animatedValueWithColor.value);
				final float baseRawValueY = chartCalculator.calculateRawY(baseValue);
				canvas.drawRect(rawValueX - halfBarWidth, rawValueY, rawValueX + halfBarWidth, baseRawValueY, mBarPaint);
				if (bar.hasValuesPopups) {
					drawValuePopup(canvas, bar, animatedValueWithColor, rawValueX, rawValueY);
				}
			}
			++barIndex;
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

	private void drawValuePopup(Canvas canvas, Bar bar, AnimatedValueWithColor animatedValueWithColor, float rawValueX,
			float rawValueY) {
		final ChartCalculator chartCalculator = mChart.getChartCalculator();
		mPointAndPopupPaint.setTextAlign(Align.LEFT);
		mPointAndPopupPaint.setTextSize(Utils.sp2px(mContext, bar.textSize));
		mPointAndPopupPaint.setColor(animatedValueWithColor.color);
		final String text = bar.formatter.formatValue(animatedValueWithColor.value);
		final Rect textBounds = new Rect();
		mPointAndPopupPaint.getTextBounds(text, 0, text.length(), textBounds);
		float left = rawValueX - (textBounds.width() / 2) - mPopupMargin;
		float right = rawValueX + (textBounds.width() / 2) + mPopupMargin;
		float top = rawValueY - mPopupMargin - textBounds.height() - mPopupMargin * 2;
		float bottom = rawValueY - mPopupMargin;
		if (top < chartCalculator.mContentRect.top) {
			top = rawValueY + mPopupMargin;
			bottom = rawValueY + mPopupMargin + textBounds.height() + mPopupMargin * 2;
		}
		final RectF popup = new RectF(left, top, right, bottom);
		canvas.drawRoundRect(popup, mPopupMargin, mPopupMargin, mPointAndPopupPaint);
		final int color = mPointAndPopupPaint.getColor();
		mPointAndPopupPaint.setColor(DEFAULT_TEXT_COLOR);
		canvas.drawText(text, left + mPopupMargin, bottom - mPopupMargin, mPointAndPopupPaint);
		mPointAndPopupPaint.setColor(color);
	}

	public boolean isInArea(float x, float y, float touchX, float touchY) {
		float diffX = touchX - x;
		float diffY = touchY - y;
		return Math.pow(diffX, 2) + Math.pow(diffY, 2) <= 2 * Math.pow(mTouchRadius, 2);
	}

}
