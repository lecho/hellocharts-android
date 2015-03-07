package lecho.lib.hellocharts.renderer;

import android.content.Context;

import lecho.lib.hellocharts.provider.ColumnChartDataProvider;
import lecho.lib.hellocharts.provider.LineChartDataProvider;
import lecho.lib.hellocharts.view.Chart;

public class ComboLineColumnChartRenderer extends ComboChartRenderer {

    private ColumnChartRenderer columnChartRenderer;
    private LineChartRenderer lineChartRenderer;

    public ComboLineColumnChartRenderer(Context context, Chart chart, ColumnChartDataProvider columnChartDataProvider,
                                        LineChartDataProvider lineChartDataProvider) {
        super(context, chart);

        columnChartRenderer = new ColumnChartRenderer(context, chart, columnChartDataProvider);
        lineChartRenderer = new LineChartRenderer(context, chart, lineChartDataProvider);

        renderers.add(columnChartRenderer);
        renderers.add(lineChartRenderer);
    }
}
