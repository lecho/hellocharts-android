package lecho.lib.hellocharts.renderer;

import lecho.lib.hellocharts.BubbleChartDataProvider;
import lecho.lib.hellocharts.Chart;
import lecho.lib.hellocharts.ChartCalculator;
import lecho.lib.hellocharts.model.BubbleChartData;
import lecho.lib.hellocharts.model.BubbleValue;
import lecho.lib.hellocharts.util.Utils;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

/**
 * Default renderer for BubbleChartView.
 * 
 * 
 * @author Leszek Wach
 * 
 */
public class BubbleChartRenderer extends AbstractChartRenderer {
	private static final int DEFAULT_TOUCH_ADDITIONAL_DP = 4;
	private static final int MODE_DRAW = 0;
	private static final int MODE_HIGHLIGHT = 1;

	private BubbleChartDataProvider dataProvider;

	private int touchAdditional;
	/**
	 * Scales for bubble radius value, only one is used depending on screen orientation;
	 */
	private float bubbleScaleX;
	private float bubbleScaleY;
	/**
	 * Maximum bubble radius.
	 */
	private float maxRadius;
	/**
	 * True if bubbleScale = bubbleScaleX so the renderer should used
	 * {@link ChartCalculator#calculateRawDistanceX(float)}, if false bubbleScale = bubbleScaleY and renderer should use
	 * {@link ChartCalculator#calculateRawDistanceY(float)}.
	 */
	private boolean isBubbleScaledByX = true;

	private Paint bubblePaint = new Paint();

	public BubbleChartRenderer(Context context, Chart chart, BubbleChartDataProvider dataProvider) {
		super(context, chart);
		this.dataProvider = dataProvider;

		touchAdditional = Utils.dp2px(density, DEFAULT_TOUCH_ADDITIONAL_DP);

		bubblePaint.setAntiAlias(true);
		bubblePaint.setStyle(Paint.Style.FILL);

	}

	@Override
	public void initMaxViewport() {
		calculateMaxViewport();
		chart.getChartCalculator().setMaxViewport(tempMaxViewport);
	}

	@Override
	public void initDataAttributes() {
		chart.getChartCalculator().setInternalMargin(calculateContentAreaMargin());
		Rect contentRect = chart.getChartCalculator().getContentRect();
		if (contentRect.width() < contentRect.height()) {
			isBubbleScaledByX = true;
		} else {
			isBubbleScaledByX = false;
		}
		labelPaint.setTextSize(Utils.sp2px(scaledDensity, chart.getChartData().getValueLabelTextSize()));
		labelPaint.getFontMetricsInt(fontMetrics);
	}

	@Override
	public void draw(Canvas canvas) {
		drawBubbles(canvas);
		if (isTouched()) {
			highlightBubbles(canvas);
		}
	}

	private void drawBubbles(Canvas canvas) {
		final BubbleChartData data = dataProvider.getBubbleChartData();
		for (BubbleValue bubbleValue : data.getValues()) {
			drawBubble(canvas, data, bubbleValue, MODE_DRAW);
		}
	}

	private void highlightBubbles(Canvas canvas) {
		final BubbleChartData data = dataProvider.getBubbleChartData();
		BubbleValue bubbleValue = data.getValues().get(selectedValue.firstIndex);
		drawBubble(canvas, data, bubbleValue, MODE_HIGHLIGHT);
	}

	private void drawBubble(Canvas canvas, BubbleChartData data, BubbleValue bubbleValue, int mode) {
		final ChartCalculator chartCalculator = chart.getChartCalculator();
		final float rawX = chartCalculator.calculateRawX(bubbleValue.getX());
		final float rawY = chartCalculator.calculateRawY(bubbleValue.getY());
		float radius = (float) Math.sqrt(bubbleValue.getZ() / Math.PI);
		float rawRadius;
		if (isBubbleScaledByX) {
			radius *= bubbleScaleX;
			rawRadius = chartCalculator.calculateRawDistanceX(radius);
		} else {
			radius *= bubbleScaleY;
			rawRadius = chartCalculator.calculateRawDistanceY(radius);
		}
		
		if (MODE_HIGHLIGHT == mode) {
			bubblePaint.setColor(bubbleValue.getDarkenColor());
			if (rawRadius < data.getMinBubbleRadius()) {
				rawRadius = data.getMinBubbleRadius() + touchAdditional;
			}
		} else {
			bubblePaint.setColor(bubbleValue.getColor());
			rawRadius -= touchAdditional;
			if (rawRadius < data.getMinBubbleRadius()) {
				rawRadius = data.getMinBubbleRadius();
			}
		}

		canvas.drawCircle(rawX, rawY, rawRadius, bubblePaint);
	}

	@Override
	public void drawUnclipped(Canvas canvas) {
	}

	@Override
	public boolean checkTouch(float touchX, float touchY) {
		oldSelectedValue.set(selectedValue);
		selectedValue.clear();

		final BubbleChartData data = dataProvider.getBubbleChartData();
		final ChartCalculator chartCalculator = chart.getChartCalculator();
		int valueIndex = 0;
		for (BubbleValue bubbleValue : data.getValues()) {
			final float rawX = chartCalculator.calculateRawX(bubbleValue.getX());
			final float rawY = chartCalculator.calculateRawY(bubbleValue.getY());
			float radius = (float) Math.sqrt(bubbleValue.getZ() / Math.PI);
			float rawRadius;
			if (isBubbleScaledByX) {
				radius *= bubbleScaleX;
				rawRadius = chartCalculator.calculateRawDistanceX(radius);
			} else {
				radius *= bubbleScaleY;
				rawRadius = chartCalculator.calculateRawDistanceY(radius);
			}

			if (rawRadius < data.getMinBubbleRadius()) {
				rawRadius = data.getMinBubbleRadius() + touchAdditional;
			}

			final float diffX = touchX - rawX;
			final float diffY = touchY - rawY;
			final float touchDistance = (float) Math.sqrt((diffX * diffX) + (diffY * diffY));

			if (touchDistance <= rawRadius) {
				selectedValue.set(valueIndex, valueIndex);
			}
			++valueIndex;
		}

		// Check if touch is still on the same value, if not return false.
		if (oldSelectedValue.isSet() && selectedValue.isSet() && !oldSelectedValue.equals(selectedValue)) {
			return false;
		}
		return isTouched();
	}

	/**
	 * Removes empty spaces on sides of chart(left-right for landscape, top-bottom for portrait). *This method should be
	 * called after layout had been drawn*. Because most often chart is drawn as rectangle with proportions other than
	 * 1:1 and bubbles have to be drawn as circles not ellipses I am unable to calculate correct margins based on chart
	 * data only. I need to know chart dimension to remove extra empty spaces, that bad because viewport depends a
	 * little on contentRect.
	 * 
	 */
	public void removeMargins() {
		final ChartCalculator calculator = chart.getChartCalculator();
		final Rect contentRect = calculator.getContentRect();
		if (contentRect.height() == 0 || contentRect.width() == 0) {
			// View probably not yet measured, skip removing margins.
		}
		final float pxX = calculator.calculateRawDistanceX(maxRadius * bubbleScaleX);
		final float pxY = calculator.calculateRawDistanceY(maxRadius * bubbleScaleY);
		final float scaleX = tempMaxViewport.width() / contentRect.width();
		final float scaleY = tempMaxViewport.height() / contentRect.height();
		float dx = 0;
		float dy = 0;
		if (isBubbleScaledByX) {
			dy = (pxY - pxX) * scaleY * 0.75f;
		} else {
			dx = (pxX - pxY) * scaleX * 0.75f;
		}
		float left = tempMaxViewport.left + dx;
		float top = tempMaxViewport.top - dy;
		float right = tempMaxViewport.right - dx;
		float bottom = tempMaxViewport.bottom + dy;
		calculator.setMaxViewport(left, top, right, bottom);
		calculator.setCurrentViewport(left, top, right, bottom);
	}

	private void calculateMaxViewport() {
		float maxZ = Float.MIN_VALUE;
		tempMaxViewport.set(Float.MAX_VALUE, Float.MIN_VALUE, Float.MIN_VALUE, Float.MAX_VALUE);
		BubbleChartData data = dataProvider.getBubbleChartData();
		// TODO: Optimize.
		for (BubbleValue bubbleValue : data.getValues()) {
			if (bubbleValue.getZ() > maxZ) {
				maxZ = bubbleValue.getZ();
			}
			if (bubbleValue.getX() < tempMaxViewport.left) {
				tempMaxViewport.left = bubbleValue.getX();
			}
			if (bubbleValue.getX() > tempMaxViewport.right) {
				tempMaxViewport.right = bubbleValue.getX();
			}
			if (bubbleValue.getY() < tempMaxViewport.bottom) {
				tempMaxViewport.bottom = bubbleValue.getY();
			}
			if (bubbleValue.getY() > tempMaxViewport.top) {
				tempMaxViewport.top = bubbleValue.getY();
			}
		}
		maxRadius = (float) Math.sqrt(maxZ / Math.PI);
		bubbleScaleX = tempMaxViewport.width() / (maxRadius * 4);
		bubbleScaleY = tempMaxViewport.height() / (maxRadius * 4);
		// For cases when user sets different than 1 bubble scale in BubbleChartData.
		bubbleScaleX *= data.getBubbleScale();
		bubbleScaleY *= data.getBubbleScale();
		// Prevent cutting of bubbles on the edges of chart area.
		tempMaxViewport.inset(-maxRadius * bubbleScaleX, -maxRadius * bubbleScaleY);
	}

	private int calculateContentAreaMargin() {
		return 0;
	}
}
