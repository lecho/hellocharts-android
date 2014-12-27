package lecho.sample.hellocharts;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import lecho.lib.hellocharts.ViewportChangeListener;
import lecho.lib.hellocharts.gesture.ChartZoomer;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.Viewport;
import lecho.lib.hellocharts.renderer.LineChartRenderer;
import lecho.lib.hellocharts.view.LineChartView;
import lecho.lib.hellocharts.view.PreviewLineChartView;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

public class LineChartActivity extends ActionBarActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_line_chart);

		if (savedInstanceState == null) {
			getSupportFragmentManager().beginTransaction().add(R.id.container, new PlaceholderFragment()).commit();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.line_chart, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {
		private static final int NUM_OF_VALUES = 500000;
        private static final int NUM_OF_SERIES = 1;
        private static final int[] COLORS = {Color.RED, Color.BLACK, Color.YELLOW, Color.GREEN, Color.BLUE, Color.CYAN, Color.MAGENTA, Color.GRAY};
        private static List<Line> linesList = new ArrayList<Line>();

        private static PreviewLineChartView previewChart;
        private static LineChartView chart;

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_line_chart, container, false);
			LinearLayout layout = (LinearLayout) rootView.findViewById(R.id.layout);

            chart = (LineChartView) rootView.findViewById(R.id.chart);
            previewChart = (PreviewLineChartView) rootView.findViewById(R.id.chart_preview);
			//chart = new LineChartView(getActivity());
            //previewChart = new PreviewLineChartView(getActivity());

			final LineChartData data = new LineChartData();

            Random r = new Random();
            for(int n = 0; n < NUM_OF_SERIES; ++n){
                ArrayList<PointValue> points = new ArrayList<>();
                for(int i = 0; i < NUM_OF_VALUES; ++i){
                    //PointValue p = new PointValue(i, r.nextBoolean() ? (n*20)+5 : (n*20)+10);
                    PointValue p = new PointValue(i, r.nextFloat() * 100f);
                    points.add(p);
                }
                Line line = new Line(points);
                //Line line = new Line(Utils.generatePoints(NUM_OF_VALUES, 1.0f));
                line.setColor(COLORS[n]);
                line.setFilled(false);
                line.setHasLines(true);
                line.setSmooth(false);
                line.setHasPoints(false);
                linesList.add(line);
            }
			data.setLines(linesList);

            // Preview data
            final LineChartData previewData = new LineChartData(linesList);
            previewData.getLines().get(0).setColor(Color.GRAY);
            previewChart.setLineChartData(previewData);
            previewChart.setPreviewColor(Color.RED);
            previewChart.setViewportChangeListener(new ViewportChangeListener() {
                @Override
                public void onViewportChanged(Viewport newViewport) {
                    chart.setViewport(newViewport, false);
                }
            });

            Axis axisX = new Axis();
			axisX.setValues(Utils.generateAxis(0.0f, 100.0f, 1.0f));
			axisX.setName("Axis X");
			data.setAxisX(axisX);

			Axis axisY = new Axis();
			axisY.setValues(Utils.generateAxis(0.0f, 95.0f, 5.0f));
			axisY.setName("Axis Y");
			data.setAxisY(axisY);

            ((LineChartRenderer)chart.getChartRenderer()).setUseFastRender(true);
            ((LineChartRenderer)chart.getChartRenderer()).setDataGroupingSize(50000);
            ((LineChartRenderer)previewChart.getChartRenderer()).setDataGroupingSize(50000);
            chart.setMaxZoom(3000f);
			chart.setLineChartData(data);
            chart.setZoomType(ChartZoomer.ZOOM_HORIZONTAL);
            previewX(false);

			return rootView;
		}

        private void previewX(boolean animate) {
            Viewport tempViewport = new Viewport(chart.getMaxViewport());
            float dx = tempViewport.width() / 4;
            tempViewport.inset(dx, 0);
            previewChart.setViewport(tempViewport, animate);
            previewChart.setZoomType(ChartZoomer.ZOOM_HORIZONTAL);
        }
	}

}
