package lecho.lib.hellocharts.renderer;

import lecho.lib.hellocharts.provider.ColumnChartDataProvider;
import lecho.lib.hellocharts.provider.LineChartDataProvider;
import lecho.lib.hellocharts.view.Chart;
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
		if (isViewportCalculationEnabled) {
			this.columnChartRenderer.initMaxViewport();
			this.lineChartRenderer.initMaxViewport();

			// Union maxViewports from both renderers.
			tempMaxViewport = this.lineChartRenderer.tempMaxViewport;
			tempMaxViewport.union(this.columnChartRenderer.tempMaxViewport);
			chart.getChartComputator().setMaxViewport(tempMaxViewport);
		}

	}

	@Override
	public void initDataMeasuremetns() {
		this.columnChartRenderer.initDataMeasuremetns();
		this.lineChartRenderer.initDataMeasuremetns();
	}

	@Override
	public void initDataAttributes() {
		this.columnChartRenderer.initDataAttributes();
		this.lineChartRenderer.initDataAttributes();
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
		selectedValue.clear();

		// Start with LineChartRenderer because lines are drawn on top, if no line point is selected and only then
		// check ColumnChartRenderer.
		if (this.lineChartRenderer.checkTouch(touchX, touchY)) {
			selectedValue.set(this.lineChartRenderer.getSelectedValue());
			selectedValue.setThirdIndex(TYPE_LINE);
		} else if (this.columnChartRenderer.checkTouch(touchX, touchY)) {
			selectedValue.set(this.columnChartRenderer.getSelectedValue());
			selectedValue.setThirdIndex(TYPE_COLUMN);
		}

		return isTouched();
	}

	@Override
	public void clearTouch() {

		this.lineChartRenderer.clearTouch();
		this.columnChartRenderer.clearTouch();

		selectedValue.clear();

	}
}
