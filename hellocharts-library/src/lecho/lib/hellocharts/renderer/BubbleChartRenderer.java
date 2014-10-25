package lecho.lib.hellocharts.renderer;

import lecho.lib.hellocharts.ChartComputator;
import lecho.lib.hellocharts.model.BubbleChartData;
import lecho.lib.hellocharts.model.BubbleValue;
import lecho.lib.hellocharts.model.ValueFormatter;
import lecho.lib.hellocharts.model.ValueShape;
import lecho.lib.hellocharts.model.Viewport;
import lecho.lib.hellocharts.provider.BubbleChartDataProvider;
import lecho.lib.hellocharts.util.Utils;
import lecho.lib.hellocharts.view.Chart;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;

public class BubbleChartRenderer extends AbstractChartRenderer {
	private static final int DEFAULT_TOUCH_ADDITIONAL_DP = 4;
	private static final int MODE_DRAW = 0;
	private static final int MODE_HIGHLIGHT = 1;

	private BubbleChartDataProvider dataProvider;

	/** Additional value added to bubble radius when drawing highlighted bubble, used to give tauch feedback. */
	private int touchAdditional;

	/**
	 * Scales for bubble radius value, only one is used depending on screen orientation;
	 */
	private float bubbleScaleX;
	private float bubbleScaleY;

	/**
	 * True if bubbleScale = bubbleScaleX so the renderer should used {@link ChartComputator#computeRawDistanceX(float)}
	 * , if false bubbleScale = bubbleScaleY and renderer should use
	 * {@link ChartComputator#calculateRawDistanceY(float)}.
	 */
	private boolean isBubbleScaledByX = true;

	/**
	 * Maximum bubble radius.
	 */
	private float maxRadius;

	/**
	 * Minimal bubble radius in pixels.
	 */
	private float minRawRadius;
	private PointF bubbleCenter = new PointF();
	private Paint bubblePaint = new Paint();
	private float[] valuesBuff = new float[3];

	/**
	 * Rect used for drawing bubbles with SHAPE_SQUARE.
	 */
	private RectF bubbleRect = new RectF();

	private boolean hasLabels;
	private boolean hasLabelsOnlyForSelected;
	private ValueFormatter valueFormatter;

	public BubbleChartRenderer(Context context, Chart chart, BubbleChartDataProvider dataProvider) {
		super(context, chart);
		this.dataProvider = dataProvider;

		touchAdditional = Utils.dp2px(density, DEFAULT_TOUCH_ADDITIONAL_DP);

		bubblePaint.setAntiAlias(true);
		bubblePaint.setStyle(Paint.Style.FILL);

	}

	@Override
	public void initMaxViewport() {
		if (isViewportCalculationEnabled) {
			calculateMaxViewport();
			chart.getChartComputator().setMaxViewport(tempMaxViewport);
		}
	}

	@Override
	public void initDataMeasuremetns() {
		chart.getChartComputator().setInternalMargin(calculateContentAreaMargin());
		Rect contentRect = chart.getChartComputator().getContentRect();
		if (contentRect.width() < contentRect.height()) {
			isBubbleScaledByX = true;
		} else {
			isBubbleScaledByX = false;
		}
	}

	@Override
	public void initDataAttributes() {
		super.initDataAttributes();

		BubbleChartData data = dataProvider.getBubbleChartData();

		this.hasLabels = data.hasLabels();
		this.hasLabelsOnlyForSelected = data.hasLabelsOnlyForSelected();
		this.valueFormatter = data.getFormatter();
	}

	@Override
	public void draw(Canvas canvas) {
		drawBubbles(canvas);
		if (isTouched()) {
			highlightBubbles(canvas);
		}
	}

	@Override
	public void drawUnclipped(Canvas canvas) {
	}

	@Override
	public boolean checkTouch(float touchX, float touchY) {
		selectedValue.clear();
		final BubbleChartData data = dataProvider.getBubbleChartData();
		int valueIndex = 0;
		for (BubbleValue bubbleValue : data.getValues()) {
			float rawRadius = processBubble(bubbleValue, bubbleCenter);

			if (ValueShape.SQUARE.equals(bubbleValue.getShape())) {
				if (bubbleRect.contains(touchX, touchY)) {
					selectedValue.set(valueIndex, valueIndex, 0);
				}
			} else if (ValueShape.CIRCLE.equals(bubbleValue.getShape())) {
				final float diffX = touchX - bubbleCenter.x;
				final float diffY = touchY - bubbleCenter.y;
				final float touchDistance = (float) Math.sqrt((diffX * diffX) + (diffY * diffY));

				if (touchDistance <= rawRadius) {
					selectedValue.set(valueIndex, valueIndex, 0);
				}
			} else {
				throw new IllegalArgumentException("Invalid bubble shape: " + bubbleValue.getShape());
			}

			++valueIndex;
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
		final ChartComputator computator = chart.getChartComputator();
		final Rect contentRect = computator.getContentRect();
		if (contentRect.height() == 0 || contentRect.width() == 0) {
			// View probably not yet measured, skip removing margins.
			return;
		}
		final float pxX = computator.computeRawDistanceX(maxRadius * bubbleScaleX);
		final float pxY = computator.calculateRawDistanceY(maxRadius * bubbleScaleY);
		final float scaleX = tempMaxViewport.width() / contentRect.width();
		final float scaleY = tempMaxViewport.height() / contentRect.height();
		float dx = 0;
		float dy = 0;
		if (isBubbleScaledByX) {
			dy = (pxY - pxX) * scaleY * 0.75f;
		} else {
			dx = (pxX - pxY) * scaleX * 0.75f;
		}

		Viewport maxViewport = computator.getMaximumViewport();
		maxViewport.inset(dx, dy);
		Viewport currentViewport = computator.getCurrentViewport();
		currentViewport.inset(dx, dy);
		computator.setMaxViewport(maxViewport);
		computator.setCurrentViewport(currentViewport);
	}

	private void drawBubbles(Canvas canvas) {
		final BubbleChartData data = dataProvider.getBubbleChartData();
		for (BubbleValue bubbleValue : data.getValues()) {
			drawBubble(canvas, bubbleValue);
		}
	}

	private void drawBubble(Canvas canvas, BubbleValue bubbleValue) {
		float rawRadius = processBubble(bubbleValue, bubbleCenter);
		// Not touched bubbles are a little smaller than touched to give user touch feedback.
		rawRadius -= touchAdditional;
		bubbleRect.inset(touchAdditional, touchAdditional);
		bubblePaint.setColor(bubbleValue.getColor());
		drawBubbleShapeAndLabel(canvas, bubbleValue, rawRadius, MODE_DRAW);

	}

	private void drawBubbleShapeAndLabel(Canvas canvas, BubbleValue bubbleValue, float rawRadius, int mode) {
		if (ValueShape.SQUARE.equals(bubbleValue.getShape())) {
			canvas.drawRect(bubbleRect, bubblePaint);
		} else if (ValueShape.CIRCLE.equals(bubbleValue.getShape())) {
			canvas.drawCircle(bubbleCenter.x, bubbleCenter.y, rawRadius, bubblePaint);
		} else {
			throw new IllegalArgumentException("Invalid bubble shape: " + bubbleValue.getShape());
		}

		if (MODE_HIGHLIGHT == mode) {
			if (hasLabels || hasLabelsOnlyForSelected) {
				drawLabel(canvas, bubbleValue, bubbleCenter.x, bubbleCenter.y);
			}
		} else if (MODE_DRAW == mode) {
			if (hasLabels) {
				drawLabel(canvas, bubbleValue, bubbleCenter.x, bubbleCenter.y);
			}
		} else {
			throw new IllegalStateException("Cannot process bubble in mode: " + mode);
		}
	}

	private void highlightBubbles(Canvas canvas) {
		final BubbleChartData data = dataProvider.getBubbleChartData();
		BubbleValue bubbleValue = data.getValues().get(selectedValue.getFirstIndex());
		highlightBubble(canvas, bubbleValue);
	}

	private void highlightBubble(Canvas canvas, BubbleValue bubbleValue) {
		float rawRadius = processBubble(bubbleValue, bubbleCenter);
		bubblePaint.setColor(bubbleValue.getDarkenColor());
		drawBubbleShapeAndLabel(canvas, bubbleValue, rawRadius, MODE_HIGHLIGHT);
	}

	/**
	 * Calculate bubble radius and center x and y coordinates. Center x and x will be stored in point parameter, radius
	 * will be returned as float value.
	 * 
	 * @param computator
	 * @param data
	 * @param bubbleValue
	 * @param point
	 * @return
	 */
	private float processBubble(BubbleValue bubbleValue, PointF point) {
		final ChartComputator computator = chart.getChartComputator();

		final float rawX = computator.computeRawX(bubbleValue.getX());
		final float rawY = computator.computeRawY(bubbleValue.getY());
		float radius = (float) Math.sqrt(Math.abs(bubbleValue.getZ()) / Math.PI);
		float rawRadius;
		if (isBubbleScaledByX) {
			radius *= bubbleScaleX;
			rawRadius = computator.computeRawDistanceX(radius);
		} else {
			radius *= bubbleScaleY;
			rawRadius = computator.calculateRawDistanceY(radius);
		}

		if (rawRadius < minRawRadius + touchAdditional) {
			rawRadius = minRawRadius + touchAdditional;
		}

		bubbleCenter.set(rawX, rawY);
		if (ValueShape.SQUARE.equals(bubbleValue.getShape())) {
			bubbleRect.set(rawX - rawRadius, rawY - rawRadius, rawX + rawRadius, rawY + rawRadius);
		}
		return rawRadius;
	}

	private void drawLabel(Canvas canvas, BubbleValue bubbleValue, float rawX, float rawY) {
		final ChartComputator computator = chart.getChartComputator();
		final Rect contentRect = computator.getContentRect();
		valuesBuff[0] = bubbleValue.getX();
		valuesBuff[1] = bubbleValue.getY();
		valuesBuff[2] = bubbleValue.getZ();
		final int numChars = valueFormatter.formatValue(labelBuffer, valuesBuff, bubbleValue.getLabel());

		if (numChars == 0) {
			// No need to draw empty label
			return;
		}

		final float labelWidth = labelPaint.measureText(labelBuffer, labelBuffer.length - numChars, numChars);
		final int labelHeight = Math.abs(fontMetrics.ascent);
		float left = rawX - labelWidth / 2 - labelMargin;
		float right = rawX + labelWidth / 2 + labelMargin;
		float top = rawY - labelHeight / 2 - labelMargin;
		float bottom = rawY + labelHeight / 2 + labelMargin;

		if (top < contentRect.top) {
			top = rawY;
			bottom = rawY + labelHeight + labelMargin * 2;
		}
		if (bottom > contentRect.bottom) {
			top = rawY - labelHeight - labelMargin * 2;
			bottom = rawY;
		}
		if (left < contentRect.left) {
			left = rawX;
			right = rawX + labelWidth + labelMargin * 2;
		}
		if (right > contentRect.right) {
			left = rawX - labelWidth - labelMargin * 2;
			right = rawX;
		}

		labelBackgroundRect.set(left, top, right, bottom);
		drawLabelTextAndBackground(canvas, labelBuffer, labelBuffer.length - numChars, numChars,
				bubbleValue.getDarkenColor());

	}

	private void calculateMaxViewport() {
		float maxZ = Float.MIN_VALUE;
		tempMaxViewport.set(Float.MAX_VALUE, Float.MIN_VALUE, Float.MIN_VALUE, Float.MAX_VALUE);
		BubbleChartData data = dataProvider.getBubbleChartData();
		// TODO: Optimize.
		for (BubbleValue bubbleValue : data.getValues()) {
			if (Math.abs(bubbleValue.getZ()) > maxZ) {
				maxZ = Math.abs(bubbleValue.getZ());
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

		// Number 4 is determined by trials and errors method, no magic behind it:).
		bubbleScaleX = tempMaxViewport.width() / (maxRadius * 4);
		if (bubbleScaleX == 0) {
			// case for 0 viewport width.
			bubbleScaleX = 1;
		}

		bubbleScaleY = tempMaxViewport.height() / (maxRadius * 4);
		if (bubbleScaleY == 0) {
			// case for 0 viewport height.
			bubbleScaleY = 1;
		}

		// For cases when user sets different than 1 bubble scale in BubbleChartData.
		bubbleScaleX *= data.getBubbleScale();
		bubbleScaleY *= data.getBubbleScale();

		// Prevent cutting of bubbles on the edges of chart area.
		tempMaxViewport.inset(-maxRadius * bubbleScaleX, -maxRadius * bubbleScaleY);

		minRawRadius = Utils.dp2px(density, dataProvider.getBubbleChartData().getMinBubbleRadius());
	}

	private int calculateContentAreaMargin() {
		return 0;
	}
}
