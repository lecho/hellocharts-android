package lecho.lib.hellocharts.renderer;

import lecho.lib.hellocharts.Chart;
import lecho.lib.hellocharts.PieChartDataProvider;
import lecho.lib.hellocharts.model.ArcValue;
import lecho.lib.hellocharts.model.PieChartData;
import lecho.lib.hellocharts.util.Utils;
import lecho.lib.hellocharts.view.PieChartView;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;

/**
 * Default renderer for PieChart.
 * 
 * @see PieChartView
 * 
 * @author Leszek Wach
 * 
 */
public class PieChartRenderer extends AbstractChartRenderer {
	private static final float MAX_WIDTH_HEIGHT = 100f;
	private static final int DEFAULT_START_ROTATION = 45;
	private static final float DEFAULT_ARC_VECTOR_RADIUS_FACTOR = 0.7f;
	private static final int DEFAULT_TOUCH_ADDITIONAL_DP = 8;
	private static final int MODE_DRAW = 0;
	private static final int MODE_HIGHLIGHT = 1;

	private PieChartDataProvider dataProvider;

	private Paint arcPaint = new Paint();
	private float maxSum;
	private RectF orginCircleOval = new RectF();
	private RectF drawCircleOval = new RectF();
	private PointF arcVector = new PointF();
	private int touchAdditional;
	private float rotation = DEFAULT_START_ROTATION;

	public PieChartRenderer(Context context, Chart chart, PieChartDataProvider dataProvider) {
		super(context, chart);
		this.dataProvider = dataProvider;
		touchAdditional = Utils.dp2px(density, DEFAULT_TOUCH_ADDITIONAL_DP);

		arcPaint.setAntiAlias(true);
		arcPaint.setStyle(Paint.Style.FILL);
		arcPaint.setColor(Color.LTGRAY);

		labelPaint.setTextAlign(Align.CENTER);
	}

	public void initMaxViewport() {
		calculateMaxViewport();
		chart.getChartCalculator().setMaxViewport(tempMaxViewport);
	}

	/**
	 * Most important thing here is {@link #calculateCircleOval()} call. Because {@link #initDataAttributes()} is
	 * usually called from onSizeChanged it is good place to calculate max PieChart circle size.
	 */
	public void initDataAttributes() {
		chart.getChartCalculator().setInternalMargin(calculateContentAreaMargin());
		labelPaint.setTextSize(Utils.sp2px(scaledDensity, chart.getChartData().getValueLabelTextSize()));
		labelPaint.getFontMetricsInt(fontMetrics);
		calculateCircleOval();
	}

	@Override
	public void draw(Canvas canvas) {
		drawArcs(canvas, MODE_DRAW);
		if (isTouched()) {
			drawArcs(canvas, MODE_HIGHLIGHT);
		}
	}

	@Override
	public void drawUnclipped(Canvas canvas) {
	}

	@Override
	public boolean checkTouch(float touchX, float touchY) {
		oldSelectedValue.set(selectedValue);
		selectedValue.clear();
		final PieChartData data = dataProvider.getPieChartData();
		final float centerX = orginCircleOval.centerX();
		final float centerY = orginCircleOval.centerY();
		final float circleRadius = orginCircleOval.width() / 2f;
		// Check if touch is on circle area, if not return false;
		arcVector.set(touchX - centerX, touchY - centerY);
		if (arcVector.length() > circleRadius + touchAdditional) {
			return false;
		}
		// Get touchAngle and align touch 0 degrees with chart 0 degrees, that why I subtracting start angle, adding 360
		// and modulo 360 translates i.e -20 degrees to 340 degrees.
		final float touchAngle = (pointToAngle(touchX, touchY, centerX, centerY) - rotation + 360f) % 360f;
		final float arcScale = 360f / maxSum;
		float lastAngle = 0f; // No start angle here, see abowe
		int arcIndex = 0;
		for (ArcValue arcValue : data.getArcs()) {
			final float angle = Math.abs(arcValue.getValue()) * arcScale;
			if (touchAngle >= lastAngle) {
				selectedValue.set(arcIndex, arcIndex);
			}
			lastAngle += angle;
			++arcIndex;
		}
		// Check if touch is still on the same value, if not return false.
		if (oldSelectedValue.isSet() && selectedValue.isSet() && !oldSelectedValue.equals(selectedValue)) {
			return false;
		}
		return isTouched();
	}

	/**
	 * Draw all arcs for this PieChart, if mode == {@link #MODE_HIGHLIGHT} currently selected arc will be redrawn and
	 * highlighted.
	 * 
	 * @param canvas
	 * @param mode
	 */
	private void drawArcs(Canvas canvas, int mode) {
		final PieChartData data = dataProvider.getPieChartData();
		final float arcScale = 360f / maxSum;
		float lastAngle = rotation;
		int arcIndex = 0;
		for (ArcValue arcValue : data.getArcs()) {
			final float angle = Math.abs(arcValue.getValue()) * arcScale;
			if (MODE_DRAW == mode) {
				drawArc(canvas, data, arcValue, lastAngle, angle, mode);
			} else if (MODE_HIGHLIGHT == mode) {
				highlightArc(canvas, data, arcValue, lastAngle, angle, arcIndex);
			} else {
				throw new IllegalStateException("Cannot process arc in mode: " + mode);
			}
			lastAngle += angle;
			++arcIndex;
		}
	}

	/**
	 * Method draws single arc from lastAngle to lastAngle+angle, if mode = {@link #MODE_HIGHLIGHT} arc will be darken
	 * and will have bigger radius.
	 */
	private void drawArc(Canvas canvas, PieChartData data, ArcValue arcValue, float lastAngle, float angle, int mode) {
		// TODO: Maybe move centerX/Y out of this method.
		final float centerX = orginCircleOval.centerX();
		final float centerY = orginCircleOval.centerY();
		final float circleRadius = orginCircleOval.width() / 2f;
		final float arcCenterX = (float) (circleRadius * DEFAULT_ARC_VECTOR_RADIUS_FACTOR
				* Math.cos(Math.toRadians(lastAngle + angle / 2)) + centerX);
		final float arcCenterY = (float) (circleRadius * DEFAULT_ARC_VECTOR_RADIUS_FACTOR
				* Math.sin(Math.toRadians(lastAngle + angle / 2)) + centerY);

		// Move arc along vector to add spacing between arcs.
		arcVector.set(arcCenterX - centerX, arcCenterY - centerY);
		normalizeVector(arcVector);
		drawCircleOval.set(orginCircleOval);
		final float arcSpacing = Utils.dp2px(density, arcValue.getArcSpacing());
		drawCircleOval.inset(arcSpacing, arcSpacing);
		drawCircleOval.offset(arcVector.x * arcSpacing, arcVector.y * arcSpacing);
		if (MODE_HIGHLIGHT == mode) {
			// Add additional touch feedback by setting bigger radius for that arc and darken color.
			drawCircleOval.inset(-touchAdditional, -touchAdditional);
			arcPaint.setColor(arcValue.getDarkenColor());
			canvas.drawArc(drawCircleOval, lastAngle, angle, true, arcPaint);
			if (data.hasLabels() || MODE_HIGHLIGHT == mode) {
				drawLabel(canvas, data, arcValue, arcCenterX, arcCenterY);
			}
		} else {
			arcPaint.setColor(arcValue.getColor());
			canvas.drawArc(drawCircleOval, lastAngle, angle, true, arcPaint);
			if (data.hasLabels()) {
				drawLabel(canvas, data, arcValue, arcCenterX, arcCenterY);
			}
		}
	}

	private void drawLabel(Canvas canvas, PieChartData data, ArcValue arcValue, final float arcCenterX,
			final float arcCenterY) {
		final int nummChars = data.getFormatter().formatValue(labelBuffer, arcValue.getValue());
		canvas.drawText(labelBuffer, labelBuffer.length - nummChars, nummChars, arcCenterX, arcCenterY, labelPaint);
	}

	private void highlightArc(Canvas canvas, PieChartData data, ArcValue arcValue, float lastAngle, float angle,
			int arcIndex) {
		if (selectedValue.firstIndex != arcIndex) {
			// Not that arc.
			return;
		}
		drawArc(canvas, data, arcValue, lastAngle, angle, MODE_HIGHLIGHT);
	}

	private void normalizeVector(PointF point) {
		final float abs = point.length();
		point.set(point.x / abs, point.y / abs);
	}

	/**
	 * Calculates angle of touched point.
	 */
	private float pointToAngle(float x, float y, float centerX, float centerY) {
		double diffX = x - centerX;
		double diffY = y - centerY;
		// Pass -diffX to get clockwise degrees order.
		double radian = Math.atan2(-diffX, diffY);

		float angle = ((float) Math.toDegrees(radian) + 360) % 360;
		// Add 90 because atan2 returns 0 degrees at 6 o'clock.
		angle += 90f;
		return angle;
	}

	/**
	 * Calculates rectangle(square) that will constraint chart circle.
	 */
	private void calculateCircleOval() {
		Rect contentRect = chart.getChartCalculator().getContentRect();
		final float circleRadius = Math.min(contentRect.width() / 2f, contentRect.height() / 2f);
		final float centerX = contentRect.centerX();
		final float centerY = contentRect.centerY();
		final float left = centerX - circleRadius + touchAdditional;
		final float top = centerY - circleRadius + touchAdditional;
		final float right = centerX + circleRadius - touchAdditional;
		final float bottom = centerY + circleRadius - touchAdditional;
		orginCircleOval.set(left, top, right, bottom);
	}

	/**
	 * Viewport is not really important for PieChart, this kind of chart doesn't relay on viewport but uses pixels
	 * coordinates instead. This method also calculates sum of all ArcValues.
	 */
	private void calculateMaxViewport() {
		tempMaxViewport.set(0, MAX_WIDTH_HEIGHT, MAX_WIDTH_HEIGHT, 0);
		maxSum = 0.0f;
		for (ArcValue arcValue : dataProvider.getPieChartData().getArcs()) {
			maxSum += Math.abs(arcValue.getValue());
		}
	}

	/**
	 * No margin for this chart.
	 * 
	 * @see #calculateCircleOval()
	 * 
	 * @return
	 */
	private int calculateContentAreaMargin() {
		return 0;
	}

	public RectF getCircleOval() {
		return orginCircleOval;
	}

	public void setCircleOval(RectF orginCircleOval) {
		this.orginCircleOval = orginCircleOval;
	}

	public float getChartRotation() {
		return rotation;
	}

	public void setChartRotation(float rotation) {
		rotation = (rotation % 360 + 360) % 360;
		this.rotation = rotation;
	}

}
