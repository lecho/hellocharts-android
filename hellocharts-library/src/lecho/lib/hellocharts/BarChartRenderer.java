package lecho.lib.hellocharts;

import lecho.lib.hellocharts.model.AnimatedValueWithColor;
import lecho.lib.hellocharts.model.Bar;
import lecho.lib.hellocharts.model.BarChartData;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.Point;
import lecho.lib.hellocharts.utils.Utils;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.Cap;
import android.graphics.Rect;
import android.graphics.RectF;

public class BarChartRenderer {

	private static final int DEFAULT_TOUCH_RADIUS_DP = 12;
	private static final int DEFAULT_POPUP_MARGIN_DP = 4;
	private static final int DEFAULT_TEXT_COLOR = Color.WHITE;
	private int mPopupMargin;
	private Paint mBarPaint = new Paint();
	private Paint mPointAndPopupPaint = new Paint();
	private float mTouchRadius;
	private Context mContext;
	private BarChart mChart;

	public BarChartRenderer(Context context, BarChart chart) {
		mContext = context;
		mChart = chart;
		mPopupMargin = Utils.dp2px(context, DEFAULT_POPUP_MARGIN_DP);
		mTouchRadius = Utils.dp2px(context, DEFAULT_TOUCH_RADIUS_DP);

		mBarPaint.setAntiAlias(true);
		mBarPaint.setStyle(Paint.Style.FILL);
		mBarPaint.setStrokeCap(Cap.SQUARE);

		mPointAndPopupPaint.setAntiAlias(true);
		mPointAndPopupPaint.setStyle(Paint.Style.FILL);
		mPointAndPopupPaint.setStrokeWidth(1);
	}

	public void draw(Canvas canvas) {
		final BarChartData data = mChart.getData();
		final ChartCalculator chartCalculator = mChart.getChartCalculator();
		final float barWidth = chartCalculator.mContentRect.width() / data.bars.size();
		int barIndex = 0;
		for (Bar bar : data.bars) {
			for (AnimatedValueWithColor animatedValue : bar.animatedValues) {
				final float rawValueX = chartCalculator.calculateRawX(barIndex);
				final float rawValueY = chartCalculator.calculateRawY(animatedValue.value);
				mBarPaint.setColor(animatedValue.color);
				canvas.drawRect(rawValueX - (barWidth / 4), rawValueY, rawValueX + (barWidth / 4),
						chartCalculator.mContentRect.bottom, mBarPaint);
			}
			++barIndex;
		}
	}

	private void drawValuePopup(Canvas canvas, Line line, Point value, float rawValueX, float rawValueY) {
		final ChartCalculator chartCalculator = mChart.getChartCalculator();
		mPointAndPopupPaint.setTextAlign(Align.LEFT);
		mPointAndPopupPaint.setTextSize(Utils.sp2px(mContext, line.textSize));
		final String text = line.formatter.formatValue(value);
		final Rect textBounds = new Rect();
		mPointAndPopupPaint.getTextBounds(text, 0, text.length(), textBounds);
		float left = rawValueX + mPopupMargin;
		float right = rawValueX + mPopupMargin + textBounds.width() + mPopupMargin * 2;
		float top = rawValueY - mPopupMargin - textBounds.height() - mPopupMargin * 2;
		float bottom = rawValueY - mPopupMargin;
		if (top < chartCalculator.mContentRect.top) {
			top = rawValueY + mPopupMargin;
			bottom = rawValueY + mPopupMargin + textBounds.height() + mPopupMargin * 2;
		}
		if (right > chartCalculator.mContentRect.right) {
			left = rawValueX - mPopupMargin - textBounds.width() - mPopupMargin * 2;
			right = rawValueX - mPopupMargin;
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
