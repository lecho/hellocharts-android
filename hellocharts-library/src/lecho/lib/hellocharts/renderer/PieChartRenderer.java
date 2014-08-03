package lecho.lib.hellocharts.renderer;

import lecho.lib.hellocharts.Chart;
import lecho.lib.hellocharts.ChartCalculator;
import lecho.lib.hellocharts.PieChartDataProvider;
import lecho.lib.hellocharts.model.ArcValue;
import lecho.lib.hellocharts.model.PieChartData;
import lecho.lib.hellocharts.model.Viewport;
import lecho.lib.hellocharts.util.Utils;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;

public class PieChartRenderer extends AbstractChartRenderer {
	private static final float MAX_WIDTH_HEIGHT = 100f;
	private static final int MODE_DRAW = 0;
	private static final int MODE_HIGHLIGHT = 1;

	private PieChartDataProvider dataProvider;

	private Paint arcPaint = new Paint();
	private RectF labelRect = new RectF();
	private float maxSum;
	private float circleRadius;
	private RectF circleOval = new RectF();

	public PieChartRenderer(Context context, Chart chart, PieChartDataProvider dataProvider) {
		super(context, chart);
		this.dataProvider = dataProvider;

		arcPaint.setAntiAlias(true);
		arcPaint.setStyle(Paint.Style.FILL);
		arcPaint.setColor(Color.LTGRAY);

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
		// TODO
		PieChartData data = dataProvider.getPieChartData();
		float lastArc = 0;
		for (ArcValue arcValue : data.getArcs()) {
			float arc = (arcValue.getValue() / maxSum) * 360f;
			arcPaint.setColor(Utils.pickColor());
			canvas.drawArc(circleOval, lastArc, arc, true, arcPaint);
			lastArc += arc;
		}
		// canvas.drawArc(circleOval, 0, 360, true, arcPaint);
	}

	@Override
	public void drawUnclipped(Canvas canvas) {
	}

	@Override
	public boolean checkTouch(float touchX, float touchY) {
		// final LineChartData data = dataProvider.getLineChartData();
		// final ChartCalculator chartCalculator = chart.getChartCalculator();
		// oldSelectedValue.firstIndex = selectedValue.firstIndex;
		// oldSelectedValue.secondIndex = selectedValue.secondIndex;
		// selectedValue.clear();
		// int lineIndex = 0;
		// for (Line line : data.lines) {
		// int pointRadius = Utils.dp2px(density, line.getPointRadius());
		// int valueIndex = 0;
		// for (LinePoint linePoint : line.getPoints()) {
		// final float rawValueX = chartCalculator.calculateRawX(linePoint.getX());
		// final float rawValueY = chartCalculator.calculateRawY(linePoint.getY());
		// if (isInArea(rawValueX, rawValueY, touchX, touchY, pointRadius + touchTolleranceMargin)) {
		// selectedValue.firstIndex = lineIndex;
		// selectedValue.secondIndex = valueIndex;
		// }
		// ++valueIndex;
		// }
		// ++lineIndex;
		// }
		// // Check if touch is still on the same value, if not return false.
		// if (oldSelectedValue.isSet() && selectedValue.isSet() && !oldSelectedValue.equals(selectedValue)) {
		// return false;
		// }
		return isTouched();
	}

	private void calculateCircleOval() {
		Rect contentRect = chart.getChartCalculator().getContentRect();
		circleRadius = Math.min(contentRect.width() / 2f, contentRect.height() / 2f);
		float x = contentRect.centerX();
		float y = contentRect.centerY();
		circleOval.set(x - circleRadius, y - circleRadius, x + circleRadius, y + circleRadius);
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
