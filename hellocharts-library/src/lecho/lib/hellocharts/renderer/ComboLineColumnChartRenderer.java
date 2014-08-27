package lecho.lib.hellocharts.renderer;

import lecho.lib.hellocharts.Chart;
import lecho.lib.hellocharts.ColumnChartDataProvider;
import lecho.lib.hellocharts.LineChartDataProvider;
import android.content.Context;
import android.graphics.Canvas;

public class ComboLineColumnChartRenderer extends AbstractChartRenderer {
	public static final int TYPE_LINE = 1;
	public static final int TYPE_COLUMN = 2;

	private ColumnChartRenderer columnChartRenderer;
	private LineChartRenderer lineChartRenderer;

	public ComboLineColumnChartRenderer(Context context, Chart chart, ColumnChartDataProvider columnChartDataProvider,
			LineChartDataProvider lineChartDataProvider) {
		super(context, chart);

		this.columnChartRenderer = new ColumnChartRenderer(context, chart, columnChartDataProvider);
		this.lineChartRenderer = new LineChartRenderer(context, chart, lineChartDataProvider);
	}

	@Override
	public void initMaxViewport() {
		// Start with LineChart, ColumnChart maxViewport should override LineChart maxViewport.
		this.lineChartRenderer.initMaxViewport();
		this.columnChartRenderer.initMaxViewport();

	}

	@Override
	public void initDataMeasuremetns() {
		this.columnChartRenderer.initDataMeasuremetns();
		this.lineChartRenderer.initDataMeasuremetns();
	}

	public void draw(Canvas canvas) {
		this.columnChartRenderer.draw(canvas);
		this.lineChartRenderer.draw(canvas);
	}

	@Override
	public void drawUnclipped(Canvas canvas) {
		this.columnChartRenderer.drawUnclipped(canvas);
		this.lineChartRenderer.drawUnclipped(canvas);
	}

	public boolean checkTouch(float touchX, float touchY) {
		oldSelectedValue.set(selectedValue);
		selectedValue.clear();

		// Start with LineChartRenderer because lines are drawn on top, if no line point is selected and only then
		// check ColumnChartRenderer.
		if (!this.lineChartRenderer.checkTouch(touchX, touchY)) {
			if (this.columnChartRenderer.checkTouch(touchX, touchY)) {
				selectedValue.set(this.columnChartRenderer.getSelectedValue());
				selectedValue.setDataType(TYPE_COLUMN);
			}
		} else {
			selectedValue.set(this.lineChartRenderer.getSelectedValue());
			selectedValue.setDataType(TYPE_LINE);
		}

		// Check if touch is still on the same value, if not return false.
		if (oldSelectedValue.isSet() && selectedValue.isSet() && !oldSelectedValue.equals(selectedValue)) {
			return false;
		}

		return this.lineChartRenderer.isTouched() || this.columnChartRenderer.isTouched();
	}
}
