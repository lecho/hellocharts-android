package lecho.lib.hellocharts.renderer;

import lecho.lib.hellocharts.model.ArcValue;
import lecho.lib.hellocharts.model.PieChartData;
import lecho.lib.hellocharts.model.SelectedValue;
import lecho.lib.hellocharts.model.ValueFormatter;
import lecho.lib.hellocharts.provider.PieChartDataProvider;
import lecho.lib.hellocharts.util.Utils;
import lecho.lib.hellocharts.view.Chart;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.FontMetricsInt;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.text.TextUtils;

/**
 * Default renderer for PieChart. PieChart doesn't use viewport concept so it a little different than others chart
 * types.
 */
public class PieChartRenderer extends AbstractChartRenderer {
	private static final float MAX_WIDTH_HEIGHT = 100f;
	private static final int DEFAULT_START_ROTATION = 45;
	private static final float DEFAULT_LABEL_INSIDE_RADIUS_FACTOR = 0.7f;
	private static final float DEFAULT_LABEL_OUTSIDE_RADIUS_FACTOR = 1.0f;
	private static final int DEFAULT_TOUCH_ADDITIONAL_DP = 8;
	private static final int MODE_DRAW = 0;
	private static final int MODE_HIGHLIGHT = 1;

	private PieChartDataProvider dataProvider;

	private Paint arcPaint = new Paint();
	private float maxSum;
	private RectF orginCircleOval = new RectF();
	private RectF drawCircleOval = new RectF();
	private PointF arcVector = new PointF();
	private float[] valuesBuff = new float[1];

	private int touchAdditional;
	private int rotation = DEFAULT_START_ROTATION;
	private float circleFillRatio = 1.0f;

	// Center circle related attributes
	private boolean hasCenterCircle;
	private float centerCircleScale;
	private Paint centerCirclePaint = new Paint();
	// Text1
	private Paint centerCircleText1Paint = new Paint();
	private FontMetricsInt centerCircleText1FontMetrics = new FontMetricsInt();
	// Text2
	private Paint centerCircleText2Paint = new Paint();
	private FontMetricsInt centerCircleText2FontMetrics = new FontMetricsInt();

	private boolean hasLabelsOutside;
	private boolean hasLabels;
	private boolean hasLabelsOnlyForSelected;
	private ValueFormatter valueFormatter;

	public PieChartRenderer(Context context, Chart chart, PieChartDataProvider dataProvider) {
		super(context, chart);
		this.dataProvider = dataProvider;
		touchAdditional = Utils.dp2px(density, DEFAULT_TOUCH_ADDITIONAL_DP);

		arcPaint.setAntiAlias(true);
		arcPaint.setStyle(Paint.Style.FILL);

		centerCirclePaint.setAntiAlias(true);
		centerCirclePaint.setStyle(Paint.Style.FILL);

		centerCircleText1Paint.setAntiAlias(true);
		centerCircleText1Paint.setTextAlign(Align.CENTER);

		centerCircleText2Paint.setAntiAlias(true);
		centerCircleText2Paint.setTextAlign(Align.CENTER);
	}

	@Override
	public void initMaxViewport() {
		if (isViewportCalculationEnabled) {
			calculateMaxViewport();
			chart.getChartComputator().setMaxViewport(tempMaxViewport);
		}
	}

	/**
	 * Most important thing here is {@link #calculateCircleOval()} call. Because {@link #initDataMeasuremetns()} is
	 * usually called from onSizeChanged it is good place to calculate max PieChart circle size.
	 */
	@Override
	public void initDataMeasuremetns() {
		chart.getChartComputator().setInternalMargin(calculateContentAreaMargin());
		calculateCircleOval();
	}

	@Override
	public void initDataAttributes() {
		super.initDataAttributes();

		final PieChartData data = dataProvider.getPieChartData();

		hasLabelsOutside = data.hasLabelsOutside();
		hasLabels = data.hasLabels();
		hasLabelsOnlyForSelected = data.hasLabelsOnlyForSelected();
		valueFormatter = data.getFormatter();

		hasCenterCircle = data.hasCenterCircle();
		centerCircleScale = data.getCenterCircleScale();

		centerCirclePaint.setColor(data.getCenterCircleColor());

		if (null != data.getCenterText1Typeface()) {
			centerCircleText1Paint.setTypeface(data.getCenterText1Typeface());
		}
		centerCircleText1Paint.setTextSize(Utils.sp2px(scaledDensity, data.getCenterText1FontSize()));
		centerCircleText1Paint.setColor(data.getCenterText1Color());
		centerCircleText1Paint.getFontMetricsInt(centerCircleText1FontMetrics);

		if (null != data.getCenterText2Typeface()) {
			centerCircleText2Paint.setTypeface(data.getCenterText2Typeface());
		}
		centerCircleText2Paint.setTextSize(Utils.sp2px(scaledDensity, data.getCenterText2FontSize()));
		centerCircleText2Paint.setColor(data.getCenterText2Color());
		centerCircleText2Paint.getFontMetricsInt(centerCircleText2FontMetrics);
	}

	@Override
	public void draw(Canvas canvas) {
		drawArcs(canvas, MODE_DRAW);

		if (isTouched()) {
			drawArcs(canvas, MODE_HIGHLIGHT);
		}

		if (hasCenterCircle) {
			drawCenterCircle(canvas);
		}

	}

	@Override
	public void drawUnclipped(Canvas canvas) {
	}

	@Override
	public boolean checkTouch(float touchX, float touchY) {
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
		float lastAngle = 0f; // No start angle here, see above
		int arcIndex = 0;
		for (ArcValue arcValue : data.getValues()) {
			final float angle = Math.abs(arcValue.getValue()) * arcScale;
			if (touchAngle >= lastAngle) {
				selectedValue.set(arcIndex, arcIndex, arcIndex);
			}
			lastAngle += angle;
			++arcIndex;
		}
		return isTouched();
	}

	/**
	 * Draw center circle with text if {@link PieChartData#hasCenterCircle()} is set true.
	 */
	private void drawCenterCircle(Canvas canvas) {
		final PieChartData data = dataProvider.getPieChartData();
		final float circleRadius = orginCircleOval.width() / 2f;
		final float centerRadius = circleRadius * data.getCenterCircleScale();
		final float centerX = orginCircleOval.centerX();
		final float centerY = orginCircleOval.centerY();

		canvas.drawCircle(centerX, centerY, centerRadius, centerCirclePaint);

		// Draw center text1 and text2 if not empty.
		if (!TextUtils.isEmpty(data.getCenterText1())) {

			final int text1Height = Math.abs(centerCircleText1FontMetrics.ascent);

			if (!TextUtils.isEmpty(data.getCenterText2())) {
				// Draw text 2 only if text 1 is not empty.
				final int text2Height = Math.abs(centerCircleText2FontMetrics.ascent);
				canvas.drawText(data.getCenterText1(), centerX, centerY - text1Height * 0.2f, centerCircleText1Paint);
				canvas.drawText(data.getCenterText2(), centerX, centerY + text2Height, centerCircleText2Paint);
			} else {
				canvas.drawText(data.getCenterText1(), centerX, centerY + text1Height / 4, centerCircleText1Paint);
			}
		}
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
		for (ArcValue arcValue : data.getValues()) {
			final float angle = Math.abs(arcValue.getValue()) * arcScale;
			if (MODE_DRAW == mode) {
				drawArc(canvas, arcValue, lastAngle, angle, mode);
			} else if (MODE_HIGHLIGHT == mode) {
				highlightArc(canvas, arcValue, lastAngle, angle, arcIndex);
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
	private void drawArc(Canvas canvas, ArcValue arcValue, float lastAngle, float angle, int mode) {
		arcVector.set((float) (Math.cos(Math.toRadians(lastAngle + angle / 2))),
				(float) (Math.sin(Math.toRadians(lastAngle + angle / 2))));
		normalizeVector(arcVector);

		drawCircleOval.set(orginCircleOval);
		final int arcSpacing = Utils.dp2px(density, arcValue.getArcSpacing());
		drawCircleOval.inset(arcSpacing, arcSpacing);
		drawCircleOval.offset((float) (arcVector.x * arcSpacing), (float) (arcVector.y * arcSpacing));
		if (MODE_HIGHLIGHT == mode) {
			// Add additional touch feedback by setting bigger radius for that arc and darken color.
			drawCircleOval.inset(-touchAdditional, -touchAdditional);
			arcPaint.setColor(arcValue.getDarkenColor());
			canvas.drawArc(drawCircleOval, lastAngle, angle, true, arcPaint);
			if (hasLabels || hasLabelsOnlyForSelected) {
				drawLabel(canvas, arcValue);
			}
		} else {
			arcPaint.setColor(arcValue.getColor());
			canvas.drawArc(drawCircleOval, lastAngle, angle, true, arcPaint);
			if (hasLabels) {
				drawLabel(canvas, arcValue);
			}
		}
	}

	private void highlightArc(Canvas canvas, ArcValue arcValue, float lastAngle, float angle, int arcIndex) {
		if (selectedValue.getFirstIndex() != arcIndex) {
			// Not that arc.
			return;
		}
		drawArc(canvas, arcValue, lastAngle, angle, MODE_HIGHLIGHT);
	}

	private void drawLabel(Canvas canvas, ArcValue arcValue) {
		valuesBuff[0] = arcValue.getValue();
		final int numChars = valueFormatter.formatValue(labelBuffer, valuesBuff, arcValue.getLabel());

		if (numChars == 0) {
			// No need to draw empty label
			return;
		}

		final float labelWidth = labelPaint.measureText(labelBuffer, labelBuffer.length - numChars, numChars);
		final int labelHeight = Math.abs(fontMetrics.ascent);

		final float centerX = orginCircleOval.centerX();
		final float centerY = orginCircleOval.centerY();
		final float circleRadius = orginCircleOval.width() / 2f;
		final float labelRadius;

		if (hasLabelsOutside) {
			labelRadius = circleRadius * DEFAULT_LABEL_OUTSIDE_RADIUS_FACTOR;
		} else {
			if (hasCenterCircle) {
				labelRadius = circleRadius - (circleRadius - (circleRadius * centerCircleScale)) / 2;
			} else {
				labelRadius = circleRadius * DEFAULT_LABEL_INSIDE_RADIUS_FACTOR;
			}
		}

		final float rawX = labelRadius * arcVector.x + centerX;
		final float rawY = labelRadius * arcVector.y + centerY;

		float left;
		float right;
		float top;
		float bottom;

		if (hasLabelsOutside) {
			if (rawX > centerX) {
				// Right half.
				left = rawX + labelMargin;
				right = rawX + labelWidth + labelMargin * 3;
			} else {
				left = rawX - labelWidth - labelMargin * 3;
				right = rawX - labelMargin;
			}

			if (rawY > centerY) {
				// Lower half.
				top = rawY + labelMargin;
				bottom = rawY + labelHeight + labelMargin * 3;
			} else {
				top = rawY - labelHeight - labelMargin * 3;
				bottom = rawY - labelMargin;
			}
		} else {
			left = rawX - labelWidth / 2 - labelMargin;
			right = rawX + labelWidth / 2 + labelMargin;
			top = rawY - labelHeight / 2 - labelMargin;
			bottom = rawY + labelHeight / 2 + labelMargin;
		}

		labelBackgroundRect.set(left, top, right, bottom);
		drawLabelTextAndBackground(canvas, labelBuffer, labelBuffer.length - numChars, numChars,
				arcValue.getDarkenColor());
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
		Rect contentRect = chart.getChartComputator().getContentRect();
		final float circleRadius = Math.min(contentRect.width() / 2f, contentRect.height() / 2f);
		final float centerX = contentRect.centerX();
		final float centerY = contentRect.centerY();
		final float left = centerX - circleRadius + touchAdditional;
		final float top = centerY - circleRadius + touchAdditional;
		final float right = centerX + circleRadius - touchAdditional;
		final float bottom = centerY + circleRadius - touchAdditional;
		orginCircleOval.set(left, top, right, bottom);
		final float inest = 0.5f * orginCircleOval.width() * (1.0f - circleFillRatio);
		orginCircleOval.inset(inest, inest);
	}

	/**
	 * Viewport is not really important for PieChart, this kind of chart doesn't relay on viewport but uses pixels
	 * coordinates instead. This method also calculates sum of all ArcValues.
	 */
	private void calculateMaxViewport() {
		tempMaxViewport.set(0, MAX_WIDTH_HEIGHT, MAX_WIDTH_HEIGHT, 0);
		maxSum = 0.0f;
		for (ArcValue arcValue : dataProvider.getPieChartData().getValues()) {
			maxSum += Math.abs(arcValue.getValue());
		}
	}

	/**
	 * No margin for this chart. Margin will be calculated with CircleOval.
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

	public int getChartRotation() {
		return rotation;
	}

	public void setChartRotation(int rotation) {
		rotation = (rotation % 360 + 360) % 360;
		this.rotation = rotation;
	}

	/**
	 * Returns ArcValue that is under given angle, selectedValue (if not null) will be hold arc index.
	 */
	public ArcValue getValueForAngle(int angle, SelectedValue selectedValue) {
		final PieChartData data = dataProvider.getPieChartData();
		final float touchAngle = (angle - rotation + 360f) % 360f;
		final float arcScale = 360f / maxSum;
		float lastAngle = 0f;
		int arcIndex = 0;
		for (ArcValue arcValue : data.getValues()) {
			final float tempAngle = Math.abs(arcValue.getValue()) * arcScale;
			if (touchAngle >= lastAngle) {
				if (null != selectedValue) {
					selectedValue.set(arcIndex, arcIndex, arcIndex);
				}
				return arcValue;
			}
			lastAngle += tempAngle;
			++arcIndex;
		}
		return null;
	}

	/**
	 * @see #setCircleFillRatio(float)
	 */
	public float getCircleFillRatio() {
		return this.circleFillRatio;
	}

	/**
	 * Set how much of view area should be taken by chart circle. Value should be between 0 and 1. Default is 1 so
	 * circle will have radius equals min(View.width, View.height).
	 */
	public void setCircleFillRatio(float fillRatio) {
		if (fillRatio < 0) {
			fillRatio = 0;
		} else if (fillRatio > 1) {
			fillRatio = 1;
		}

		this.circleFillRatio = fillRatio;
		calculateCircleOval();
	}

}
