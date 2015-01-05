package lecho.lib.hellocharts.renderer;

import android.content.Context;
import android.graphics.Canvas;

import java.util.ArrayList;
import java.util.List;

import lecho.lib.hellocharts.model.SelectedValue.SelectedValueType;
import lecho.lib.hellocharts.provider.ColumnChartDataProvider;
import lecho.lib.hellocharts.provider.LineChartDataProvider;
import lecho.lib.hellocharts.view.Chart;

public class ComboChartRenderer extends AbstractChartRenderer {

    protected List<ChartRenderer> renderers;

	public ComboChartRenderer(Context context, Chart chart) {
		super(context, chart);
        this.renderers = new ArrayList<>();
	}

	@Override
	public void initMaxViewport() {
		if (isViewportCalculationEnabled) {
            tempMaxViewport = null;
            for (ChartRenderer renderer : renderers) {
                renderer.initMaxViewport();

                // Union maxViewports from all renderers.
                if (tempMaxViewport == null) {
                    tempMaxViewport = renderer.getMaxViewport();
                }
                else {
                    tempMaxViewport.union(renderer.getMaxViewport());
                }
            }
			chart.getChartComputator().setMaxViewport(tempMaxViewport);
		}
	}

	@Override
	public void initDataMeasurements() {
        for (ChartRenderer renderer : renderers) {
            renderer.initDataMeasurements();
        }
	}

	@Override
	public void initDataAttributes() {
        for (ChartRenderer renderer : renderers) {
            renderer.initDataAttributes();
        }
	}

	public void draw(Canvas canvas) {
        for (ChartRenderer renderer : renderers) {
            renderer.draw(canvas);
        }
	}

	@Override
	public void drawUnclipped(Canvas canvas) {
        for (ChartRenderer renderer : renderers) {
            renderer.drawUnclipped(canvas);
        }
	}

	public boolean checkTouch(float touchX, float touchY) {
	    selectedValue.clear();
        int rendererIndex = renderers.size() - 1;
        for (; rendererIndex >= 0; rendererIndex--) {
            ChartRenderer renderer = renderers.get(rendererIndex);
            if (renderer.checkTouch(touchX, touchY)) {
                selectedValue.set(renderer.getSelectedValue());
                break;
            }
        }

        //clear the rest of renderers if value was selected, if value was not selected this loop
        // will not be executed.
        for(rendererIndex--; rendererIndex >=0; rendererIndex--){
            ChartRenderer renderer = renderers.get(rendererIndex);
            renderer.clearTouch();
        }

		return isTouched();
	}

	@Override
	public void clearTouch() {
        for (ChartRenderer renderer : renderers) {
            renderer.clearTouch();
        }
		selectedValue.clear();
	}
}
