package lecho.lib.hellocharts.renderer;

import lecho.lib.hellocharts.BubbleChartDataProvider;
import lecho.lib.hellocharts.Chart;
import lecho.lib.hellocharts.PieChartDataProvider;
import lecho.lib.hellocharts.model.ArcValue;
import lecho.lib.hellocharts.model.BubbleChartData;
import lecho.lib.hellocharts.model.BubbleValue;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PieChartData;
import lecho.lib.hellocharts.model.PointValue;
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

	public BubbleChartRenderer(Context context, Chart chart, BubbleChartDataProvider dataProvider) {
		super(context, chart);
		this.dataProvider = dataProvider;
		touchAdditional = Utils.dp2px(density, DEFAULT_TOUCH_ADDITIONAL_DP);
	}

	public void initMaxViewport() {
		calculateMaxViewport();
		chart.getChartCalculator().setMaxViewport(tempMaxViewport);
	}

	public void initDataAttributes() {
		chart.getChartCalculator().setInternalMargin(calculateContentAreaMargin());
		labelPaint.setTextSize(Utils.sp2px(scaledDensity, chart.getChartData().getValueLabelTextSize()));
		labelPaint.getFontMetricsInt(fontMetrics);
	}

	@Override
	public void draw(Canvas canvas) {
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
	}

	private int calculateContentAreaMargin() {
		return 0;
	}
}
