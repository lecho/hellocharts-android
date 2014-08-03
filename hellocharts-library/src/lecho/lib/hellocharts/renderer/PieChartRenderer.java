package lecho.lib.hellocharts.renderer;

import lecho.lib.hellocharts.Chart;
import lecho.lib.hellocharts.PieChartDataProvider;
import lecho.lib.hellocharts.model.ArcValue;
import lecho.lib.hellocharts.model.PieChartData;
import lecho.lib.hellocharts.util.Utils;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.Log;

public class PieChartRenderer extends AbstractChartRenderer {
	private static final float MAX_WIDTH_HEIGHT = 100f;
	private static final float DEFAULT_START_ANGLE = 45;
	private static final float DEFAULT_ARC_VECTOR_RADIUS_FACTOR = 0.6f;
	private static final float CIRCLE_360 = 360f;
	private static final int DEFAULT_ARC_SPACING_DP = 2;
	private static final int DEFAULT_TOUCH_ADDITIONAL_DP = 4;
	private static final int MODE_DRAW = 0;
	private static final int MODE_HIGHLIGHT = 1;

	private PieChartDataProvider dataProvider;

	private Paint arcPaint = new Paint();
	private RectF labelRect = new RectF();
	private float maxSum;
	private float circleRadius;
	private RectF orginCircleOval = new RectF();
	private RectF drawCircleOval = new RectF();
	private PointF arcVector = new PointF();
	private float arcSpacing;
	private int touchAdditional;

	public PieChartRenderer(Context context, Chart chart, PieChartDataProvider dataProvider) {
		super(context, chart);
		this.dataProvider = dataProvider;
		arcSpacing = Utils.dp2px(density, DEFAULT_ARC_SPACING_DP);
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

	public void initDataAttributes() {
		chart.getChartCalculator().setInternalMargin(calculateContentAreaMargin());
		labelPaint.setTextSize(Utils.sp2px(scaledDensity, chart.getChartData().getValueLabelTextSize()));
		labelPaint.getFontMetricsInt(fontMetrics);
		calculateCircleOval();
	}

	@Override
	public void draw(Canvas canvas) {
		drawArcs(canvas);
		if (isTouched()) {
			highlightArc(canvas);
		}
	}

	private void drawArcs(Canvas canvas) {
		final PieChartData data = dataProvider.getPieChartData();
		final float centerX = orginCircleOval.centerX();
		final float centerY = orginCircleOval.centerY();
		final float arcScale = CIRCLE_360 / maxSum;
		float lastAngle = DEFAULT_START_ANGLE;
		for (ArcValue arcValue : data.getArcs()) {
			arcPaint.setColor(arcValue.getColor());
			final float angle = arcValue.getValue() * arcScale;

			final float arcCenterX = (float) (circleRadius * DEFAULT_ARC_VECTOR_RADIUS_FACTOR
					* Math.cos(Math.toRadians(lastAngle + angle / 2)) + centerX);
			final float arcCenterY = (float) (circleRadius * DEFAULT_ARC_VECTOR_RADIUS_FACTOR
					* Math.sin(Math.toRadians(lastAngle + angle / 2)) + centerY);

			// Move arc along vector to add spacing between arcs.
			arcVector.set(arcCenterX - centerX, arcCenterY - centerY);
			normalizeVector(arcVector);
			drawCircleOval.set(orginCircleOval);
			drawCircleOval.offset(arcVector.x * arcSpacing, arcVector.y * arcSpacing);
			canvas.drawArc(drawCircleOval, lastAngle, angle, true, arcPaint);

			canvas.drawText("TTT", arcCenterX, arcCenterY, labelPaint);

			lastAngle += angle;
		}
	}

	private void highlightArc(Canvas canvas) {
		final PieChartData data = dataProvider.getPieChartData();
		final float centerX = orginCircleOval.centerX();
		final float centerY = orginCircleOval.centerY();
		final float arcScale = CIRCLE_360 / maxSum;
		float lastAngle = DEFAULT_START_ANGLE;
		int arcIndex = 0;
		for (ArcValue arcValue : data.getArcs()) {
			arcPaint.setColor(arcValue.getDarkenColor());
			final float angle = arcValue.getValue() * arcScale;
			if (selectedValue.firstIndex != arcIndex) {
				// Not that arc.
				lastAngle += angle;
				++arcIndex;
				continue;
			}
			final float arcCenterX = (float) (circleRadius * DEFAULT_ARC_VECTOR_RADIUS_FACTOR
					* Math.cos(Math.toRadians(lastAngle + angle / 2)) + centerX);
			final float arcCenterY = (float) (circleRadius * DEFAULT_ARC_VECTOR_RADIUS_FACTOR
					* Math.sin(Math.toRadians(lastAngle + angle / 2)) + centerY);

			// Move arc along vector to add spacing between arcs.
			arcVector.set(arcCenterX - centerX, arcCenterY - centerY);
			normalizeVector(arcVector);
			drawCircleOval.set(orginCircleOval);
			drawCircleOval.offset(arcVector.x * (arcSpacing + touchAdditional), arcVector.y
					* (arcSpacing + touchAdditional));
			canvas.drawArc(drawCircleOval, lastAngle, angle, true, arcPaint);

			canvas.drawText("TTT", arcCenterX, arcCenterY, labelPaint);

			lastAngle += angle;
			++arcIndex;
		}
	}

	private void normalizeVector(PointF point) {
		final float abs = point.length();
		point.set(point.x / abs, point.y / abs);
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
		// Check if touch is on circle area, if not return false;
		arcVector.set(touchX - centerX, touchY - centerY);
		if (arcVector.length() > circleRadius + arcSpacing) {
			return false;
		}
		final float touchAngle = pointToAngle(touchX, touchY, centerX, centerY);
		final float arcScale = CIRCLE_360 / maxSum;
		float lastAngle = DEFAULT_START_ANGLE;
		int arcIndex = 0;
		for (ArcValue arcValue : data.getArcs()) {
			final float angle = arcValue.getValue() * arcScale;
			final float endAngle = (lastAngle + angle) % CIRCLE_360;
			if(endAngle >= lastAngle){
				
			} else{
				
			}
			if (touchAngle >= lastAngle && touchAngle <= (lastAngle + angle) % CIRCLE_360) {
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

	private float pointToAngle(float x, float y, float centerX, float centerY) {
		double diffX = x - centerX;
		double diffY = y - centerY;
		// Pass -diffX to get clockwise degrees order.
		double radian = Math.atan2(-diffX, diffY);

		float angle = (float) Math.toDegrees(radian);
		// Add 90 because atan2 returns 0 degrees at 6 o'clock.
		angle += 90f;
		if (angle < 0) {
			// In the quarter 12-3 o'clock degrees are negative, translate it to positive values(270,360).
			angle = CIRCLE_360 + angle;
		}
		if (angle > CIRCLE_360) {
			// Just in case.
			angle = angle - CIRCLE_360;
		}
		return angle;
	}

	private void calculateCircleOval() {
		Rect contentRect = chart.getChartCalculator().getContentRect();
		circleRadius = Math.min(contentRect.width() / 2f, contentRect.height() / 2f);
		final float centerX = contentRect.centerX();
		final float centerY = contentRect.centerY();
		final float left = centerX - circleRadius + arcSpacing + touchAdditional;
		final float top = centerY - circleRadius + arcSpacing + touchAdditional;
		final float right = centerX + circleRadius - arcSpacing - touchAdditional;
		final float bottom = centerY + circleRadius - arcSpacing - touchAdditional;
		orginCircleOval.set(left, top, right, bottom);
	}

	private void calculateMaxViewport() {
		tempMaxViewport.set(0, MAX_WIDTH_HEIGHT, MAX_WIDTH_HEIGHT, 0);
		maxSum = 0.0f;
		for (ArcValue arcValue : dataProvider.getPieChartData().getArcs()) {
			maxSum += arcValue.getValue();
		}
	}

	private int calculateContentAreaMargin() {
		// int contentAreaMargin = 0;
		// LineChartData data = dataProvider.getLineChartData();
		// for (Line line : data.lines) {
		// if (line.hasPoints()) {
		// int margin = line.getPointRadius() + DEFAULT_TOUCH_TOLLERANCE_MARGIN_DP;
		// if (margin > contentAreaMargin) {
		// contentAreaMargin = margin;
		// }
		// }
		// }
		// return Utils.dp2px(density, contentAreaMargin);
		return 0;
	}

}
