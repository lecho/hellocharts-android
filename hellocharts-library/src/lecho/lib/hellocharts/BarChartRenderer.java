package lecho.lib.hellocharts;

import lecho.lib.hellocharts.model.AnimatedValueWithColor;
import lecho.lib.hellocharts.model.Bar;
import lecho.lib.hellocharts.model.BarChartData;
import lecho.lib.hellocharts.model.IntPair;
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

public class BarChartRenderer implements ChartRenderer {
	private static final float DEFAULT_FILL_RATIO = 0.75f;
	private static final int DEFAULT_SUBBAR_SPACING_DP = 1;
	private static final int DEFAULT_TOUCH_STROKE_WIDTH_DP = 4;
	private static final float DEFAULT_BASE_VALUE = 0.0f;
	private static final int DEFAULT_POPUP_MARGIN_DP = 4;
	private static final int DEFAULT_TEXT_COLOR = Color.WHITE;
	private int mPopupMargin;
	private Paint mBarPaint = new Paint();
	private Paint mPointAndPopupPaint = new Paint();
	private Context mContext;
	private BarChart mChart;
	private int mSubbarSpacing;
	private IntPair mSelectedBarAndValue = new IntPair(Integer.MIN_VALUE, Integer.MIN_VALUE);
	private RectF mRectToDraw = new RectF();

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
		if (data.isStacked) {
			drawStackedBars(canvas);
			if (isValueTouched()) {
				redrawSelectedValueForStacked(canvas);
			}
		} else {
			drawDefaultBars(canvas);
			if (isValueTouched()) {
				redrawSelectedValueForDefault(canvas);
			}
		}
	}

	private void drawDefaultBars(Canvas canvas) {
		final BarChartData data = mChart.getData();
		final ChartCalculator chartCalculator = mChart.getChartCalculator();
		final float barWidth = calculateBarhWidth(chartCalculator);
		final float halfBarWidth = barWidth / 2;
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
			float subbarRawValueX = rawValueX - halfBarWidth;
			for (AnimatedValueWithColor animatedValueWithColor : bar.animatedValues) {
				if (subbarRawValueX > rawValueX + halfBarWidth) {
					break;
				}
				mBarPaint.setColor(animatedValueWithColor.color);
				final float rawValueY = chartCalculator.calculateRawY(animatedValueWithColor.value);
				calculateRectToDraw(subbarRawValueX, subbarRawValueX + subbarWidth, rawBaseValueY, rawValueY);
				canvas.drawRect(mRectToDraw, mBarPaint);
				if (bar.hasValuesPopups) {
					drawValuePopup(canvas, bar, animatedValueWithColor, rawValueX, rawValueY);
				}
				subbarRawValueX += subbarWidth + mSubbarSpacing;
			}
			++barIndex;
		}
	}

	private void redrawSelectedValueForDefault(Canvas canvas) {
		final BarChartData data = mChart.getData();
		final ChartCalculator chartCalculator = mChart.getChartCalculator();
		final float barWidth = calculateBarhWidth(chartCalculator);
		final float halfBarWidth = barWidth / 2;
		// Bars are indexes from 0 to n, bar index is also bar X value
		final float rawBaseValueY = chartCalculator.calculateRawY(DEFAULT_BASE_VALUE);
		final Bar bar = data.bars.get(mSelectedBarAndValue.first);
		// For n subbars there will be n-1 spacing and there will be one subbar for every animatedValue
		float subbarWidth = (barWidth - (mSubbarSpacing * (bar.animatedValues.size() - 1))) / bar.animatedValues.size();
		if (subbarWidth < 1) {
			subbarWidth = 1;
		}
		final float rawValueX = chartCalculator.calculateRawX(mSelectedBarAndValue.first);
		// First subbar will starts at the left edge of current bar, rawValueX is horizontal center of that bar
		float subbarRawValueX = rawValueX - halfBarWidth;
		int valueIndex = 0;
		for (AnimatedValueWithColor animatedValueWithColor : bar.animatedValues) {
			if (subbarRawValueX > rawValueX + halfBarWidth) {
				break;
			}
			mBarPaint.setColor(animatedValueWithColor.color);
			final float rawValueY = chartCalculator.calculateRawY(animatedValueWithColor.value);
			if (mSelectedBarAndValue.second == valueIndex) {
				mBarPaint.setStyle(Paint.Style.FILL_AND_STROKE);
				calculateRectToDraw(subbarRawValueX, subbarRawValueX + subbarWidth, rawBaseValueY, rawValueY);
				canvas.drawRect(mRectToDraw, mBarPaint);
				if (bar.hasValuesPopups) {
					drawValuePopup(canvas, bar, animatedValueWithColor, rawValueX, rawValueY);
				}
				mBarPaint.setStyle(Paint.Style.FILL);
			}
			subbarRawValueX += subbarWidth + mSubbarSpacing;
			++valueIndex;
		}
	}

	private void drawStackedBars(Canvas canvas) {
		final BarChartData data = mChart.getData();
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
				final float rawBaseValueY = chartCalculator.calculateRawY(baseValue);
				final float rawValueY = chartCalculator.calculateRawY(baseValue + animatedValueWithColor.value);
				calculateRectToDraw(rawValueX - halfBarWidth, rawValueX + halfBarWidth, rawBaseValueY, rawValueY);
				canvas.drawRect(mRectToDraw, mBarPaint);
				if (bar.hasValuesPopups) {
					drawValuePopup(canvas, bar, animatedValueWithColor, rawValueX, rawValueY);
				}
			}
			++barIndex;
		}
	}

	private void redrawSelectedValueForStacked(Canvas canvas) {
		final BarChartData data = mChart.getData();
		final ChartCalculator chartCalculator = mChart.getChartCalculator();
		final float barWidth = calculateBarhWidth(chartCalculator);
		final float halfBarWidth = barWidth / 2;
		Bar bar = data.bars.get(mSelectedBarAndValue.first);
		final float rawValueX = chartCalculator.calculateRawX(mSelectedBarAndValue.first);
		float mostPositiveValue = DEFAULT_BASE_VALUE;
		float mostNegativeValue = DEFAULT_BASE_VALUE;
		float baseValue = DEFAULT_BASE_VALUE;
		int valueIndex = 0;
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
			final float rawBaseValueY = chartCalculator.calculateRawY(baseValue);
			final float rawValueY = chartCalculator.calculateRawY(baseValue + animatedValueWithColor.value);
			if (mSelectedBarAndValue.second == valueIndex) {
				mBarPaint.setStyle(Paint.Style.FILL_AND_STROKE);
				calculateRectToDraw(rawValueX - halfBarWidth, rawValueX + halfBarWidth, rawBaseValueY, rawValueY);
				canvas.drawRect(mRectToDraw, mBarPaint);
				if (bar.hasValuesPopups) {
					drawValuePopup(canvas, bar, animatedValueWithColor, rawValueX, rawValueY);
				}
				mBarPaint.setStyle(Paint.Style.FILL);
			}
			++valueIndex;
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

	public boolean checkValueTouch(float touchX, float touchY) {
		final BarChartData data = mChart.getData();
		if (data.isStacked) {
			return checkTouchForStacked(touchX, touchY);
		} else {
			return checkTouchForDefault(touchX, touchY);
		}
	}

	public boolean isValueTouched() {
		if (mSelectedBarAndValue.first >= 0 && mSelectedBarAndValue.second >= 0) {
			return true;
		} else {
			return false;
		}
	}

	public void clearValueTouch() {
		mSelectedBarAndValue = new IntPair(Integer.MIN_VALUE, Integer.MIN_VALUE);
	}

	private boolean checkTouchForDefault(float touchX, float touchY) {
		// TODO: Extract common code with drawDefaultBars if possible.
		final BarChartData data = mChart.getData();
		final ChartCalculator chartCalculator = mChart.getChartCalculator();
		final float barWidth = calculateBarhWidth(chartCalculator);
		final float halfBarWidth = barWidth / 2;
		final float rawBaseValueY = chartCalculator.calculateRawY(DEFAULT_BASE_VALUE);
		int barIndex = 0;
		for (Bar bar : data.bars) {
			float subbarWidth = (barWidth - (mSubbarSpacing * (bar.animatedValues.size() - 1)))
					/ bar.animatedValues.size();
			if (subbarWidth < 1) {
				subbarWidth = 1;
			}
			final float rawValueX = chartCalculator.calculateRawX(barIndex);
			float subbarRawValueX = rawValueX - (barWidth / 2);
			if (touchX >= rawValueX - halfBarWidth && touchX <= rawValueX + halfBarWidth) {
				int valueIndex = 0;
				for (AnimatedValueWithColor animatedValueWithColor : bar.animatedValues) {
					if (subbarRawValueX > rawValueX + (barWidth / 2)) {
						break;
					}
					final float rawValueY = chartCalculator.calculateRawY(animatedValueWithColor.value);
					final RectF subbarArea = new RectF();
					subbarArea.left = subbarRawValueX;
					subbarArea.right = subbarRawValueX + subbarWidth;
					if (rawValueY <= rawBaseValueY) {
						subbarArea.top = rawValueY;
						subbarArea.bottom = rawBaseValueY;
					} else {
						subbarArea.bottom = rawValueY;
						subbarArea.top = rawBaseValueY;
					}
					if (subbarArea.contains(touchX, touchY)) {
						mSelectedBarAndValue = new IntPair(barIndex, valueIndex);
						return true;
					}
					subbarRawValueX += subbarWidth + mSubbarSpacing;
					++valueIndex;
				}
			}
			++barIndex;
		}
		return false;
	}

	private boolean checkTouchForStacked(float touchX, float touchY) {
		final BarChartData data = mChart.getData();
		final ChartCalculator chartCalculator = mChart.getChartCalculator();
		final float barWidth = calculateBarhWidth(chartCalculator);
		final float halfBarWidth = barWidth / 2;
		int barIndex = 0;
		for (Bar bar : data.bars) {
			final float rawValueX = chartCalculator.calculateRawX(barIndex);
			if (touchX >= rawValueX - halfBarWidth && touchX <= rawValueX + halfBarWidth) {
				float mostPositiveValue = DEFAULT_BASE_VALUE;
				float mostNegativeValue = DEFAULT_BASE_VALUE;
				float baseValue = DEFAULT_BASE_VALUE;
				int valueIndex = 0;
				for (AnimatedValueWithColor animatedValueWithColor : bar.animatedValues) {
					if (animatedValueWithColor.value >= 0) {
						baseValue = mostPositiveValue;
						mostPositiveValue += animatedValueWithColor.value;
					} else {
						baseValue = mostNegativeValue;
						mostNegativeValue += animatedValueWithColor.value;
					}
					final float rawBaseValueY = chartCalculator.calculateRawY(baseValue);
					final float rawValueY = chartCalculator.calculateRawY(baseValue + animatedValueWithColor.value);
					final RectF subbarArea = new RectF();
					subbarArea.left = rawValueX - halfBarWidth;
					subbarArea.right = rawValueX + halfBarWidth;
					if (rawValueY <= rawBaseValueY) {
						subbarArea.top = rawValueY;
						subbarArea.bottom = rawBaseValueY;
					} else {
						subbarArea.bottom = rawValueY;
						subbarArea.top = rawBaseValueY;
					}
					if (subbarArea.contains(touchX, touchY)) {
						mSelectedBarAndValue = new IntPair(barIndex, valueIndex);
						return true;
					}
					++valueIndex;
				}
			}
			++barIndex;
		}
		return false;
	}

}
