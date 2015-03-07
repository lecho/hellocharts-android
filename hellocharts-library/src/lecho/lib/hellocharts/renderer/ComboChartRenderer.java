package lecho.lib.hellocharts.renderer;

import android.content.Context;
import android.graphics.Canvas;

import java.util.ArrayList;
import java.util.List;

import lecho.lib.hellocharts.model.Viewport;
import lecho.lib.hellocharts.view.Chart;

public class ComboChartRenderer extends AbstractChartRenderer {

    protected List<ChartRenderer> renderers;
    protected Viewport unionViewport = new Viewport();

    public ComboChartRenderer(Context context, Chart chart) {
        super(context, chart);
        this.renderers = new ArrayList<>();
    }

    @Override
    public void onChartSizeChanged() {
        for (ChartRenderer renderer : renderers) {
            renderer.onChartSizeChanged();
        }
    }

    @Override
    public void onChartDataChanged() {
        super.onChartDataChanged();
        for (ChartRenderer renderer : renderers) {
            renderer.onChartDataChanged();
        }
        onChartViewportChanged();
    }

    @Override
    public void onChartViewportChanged() {
        if (isViewportCalculationEnabled) {
            int rendererIndex = 0;
            for (ChartRenderer renderer : renderers) {
                renderer.onChartViewportChanged();
                if (rendererIndex == 0) {
                    unionViewport.set(renderer.getMaximumViewport());
                } else {
                    unionViewport.union(renderer.getMaximumViewport());
                }
                ++rendererIndex;
            }
            computator.setMaxViewport(unionViewport);
            computator.setCurrentViewport(unionViewport);
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
        for (rendererIndex--; rendererIndex >= 0; rendererIndex--) {
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
