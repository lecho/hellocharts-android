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
	float bubbleScaleX;
	float bubbleScaleY;
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

	public void initMaxViewport() {
		calculateMaxViewport();
		chart.getChartCalculator().setMaxViewport(tempMaxViewport);
	}

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
		final BubbleChartData data = dataProvider.getBubbleChartData();
		final ChartCalculator chartCalculator = chart.getChartCalculator();
		for (BubbleValue bubbleValue : data.getValues()) {
			bubblePaint.setColor(bubbleValue.getColor());
			final float rawX = chartCalculator.calculateRawX(bubbleValue.getX());
			final float rawY = chartCalculator.calculateRawY(bubbleValue.getY());
			final float radius;
			final float rawRadius;
			if (isBubbleScaledByX) {
				radius = (float) Math.sqrt(bubbleValue.getZ() / Math.PI) * bubbleScaleX;
				rawRadius = chartCalculator.calculateRawDistanceX(radius);
			} else {
				radius = (float) Math.sqrt(bubbleValue.getZ() / Math.PI) * bubbleScaleY;
				rawRadius = chartCalculator.calculateRawDistanceY(radius);
			}

			canvas.drawCircle(rawX, rawY, rawRadius, bubblePaint);
		}
	}

	@Override
	public void drawUnclipped(Canvas canvas) {
	}

	@Override
	public boolean checkTouch(float touchX, float touchY) {
		oldSelectedValue.set(selectedValue);
		selectedValue.clear();
		// Check if touch is still on the same value, if not return false.
		if (oldSelectedValue.isSet() && selectedValue.isSet() && !oldSelectedValue.equals(selectedValue)) {
			return false;
		}
		return isTouched();
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
		return touchAdditional;
	}
}
