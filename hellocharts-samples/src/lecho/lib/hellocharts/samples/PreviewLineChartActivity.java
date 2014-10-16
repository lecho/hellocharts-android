package lecho.lib.hellocharts.samples;

import java.util.ArrayList;
import java.util.List;

import lecho.lib.hellocharts.ViewportChangeListener;
import lecho.lib.hellocharts.gesture.ZoomType;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.Viewport;
import lecho.lib.hellocharts.util.Utils;
import lecho.lib.hellocharts.view.LineChartView;
import lecho.lib.hellocharts.view.PreviewLineChartView;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

public class PreviewLineChartActivity extends ActionBarActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_preview_line_chart);
		if (savedInstanceState == null) {
			getSupportFragmentManager().beginTransaction().add(R.id.container, new PlaceholderFragment()).commit();
		}
	}

	/**
	 * A fragment containing a line chart and preview line chart.
	 */
	public static class PlaceholderFragment extends Fragment {

		private LineChartView chart;
		private PreviewLineChartView previewChart;
		private LineChartData data;
		/**
		 * Deep copy of data.
		 */
		private LineChartData previewData;

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
			setHasOptionsMenu(true);
			View rootView = inflater.inflate(R.layout.fragment_preview_line_chart, container, false);

			chart = (LineChartView) rootView.findViewById(R.id.chart);
			previewChart = (PreviewLineChartView) rootView.findViewById(R.id.chart_preview);

			// Generate data for previewed chart and copy of that data for preview chart.
			generateDefaultData();

			chart.setLineChartData(data);
			// Disable zoom/scroll for previewed chart, visible chart ranges depends on preview chart viewport so
			// zoom/scroll is unnecessary.
			chart.setZoomEnabled(false);
			chart.setScrollEnabled(false);

			previewChart.setLineChartData(previewData);
			previewChart.setViewportChangeListener(new ViewportListener());

			previewX(false);

			return rootView;
		}

		// MENU
		@Override
		public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
			inflater.inflate(R.menu.preview_line_chart, menu);
		}

		@Override
		public boolean onOptionsItemSelected(MenuItem item) {
			int id = item.getItemId();
			if (id == R.id.action_reset) {
				generateDefaultData();
				chart.setLineChartData(data);
				previewChart.setLineChartData(previewData);
				previewX(true);
				return true;
			}
			if (id == R.id.action_preview_both) {
				previewXY();
				previewChart.setZoomType(ZoomType.HORIZONTAL_AND_VERTICAL);
				return true;
			}
			if (id == R.id.action_preview_horizontal) {
				previewX(true);
				return true;
			}
			if (id == R.id.action_preview_vertical) {
				previewY();
				return true;
			}
			if (id == R.id.action_change_color) {
				int color = Utils.pickColor();
				while (color == previewChart.getPreviewColor()) {
					color = Utils.pickColor();
				}
				previewChart.setPreviewColor(color);
				return true;
			}
			return super.onOptionsItemSelected(item);
		}

		private void generateDefaultData() {
			int numValues = 50;

			List<PointValue> values = new ArrayList<PointValue>();
			for (int i = 0; i < numValues; ++i) {
				values.add(new PointValue(i, (float) Math.random() * 100f));
			}

			Line line = new Line(values);
			line.setColor(Utils.COLOR_GREEN);
			line.setHasPoints(false);// too many values so don't draw points.

			List<Line> lines = new ArrayList<Line>();
			lines.add(line);

			data = new LineChartData(lines);
			data.setAxisXBottom(new Axis());
			data.setAxisYLeft(new Axis().setHasLines(true));

			// prepare preview data, is better to use separate deep copy for preview chart.
			// Set color to grey to make preview area more visible.
			previewData = new LineChartData(data);
			previewData.getLines().get(0).setColor(Utils.DEFAULT_DARKEN_COLOR);

		}

		private void previewY() {
			Viewport tempViewport = new Viewport(chart.getMaximumViewport());
			float dy = tempViewport.height() / 4;
			tempViewport.inset(0, dy);
			previewChart.setCurrentViewport(tempViewport, true);
			previewChart.setZoomType(ZoomType.VERTICAL);
		}

		private void previewX(boolean animate) {
			Viewport tempViewport = new Viewport(chart.getMaximumViewport());
			float dx = tempViewport.width() / 4;
			tempViewport.inset(dx, 0);
			previewChart.setCurrentViewport(tempViewport, animate);
			previewChart.setZoomType(ZoomType.HORIZONTAL);
		}

		private void previewXY() {
			// Better to not modify viewport of any chart directly so create a copy.
			Viewport tempViewport = new Viewport(chart.getMaximumViewport());
			// Make temp viewport smaller.
			float dx = tempViewport.width() / 4;
			float dy = tempViewport.height() / 4;
			tempViewport.inset(dx, dy);
			previewChart.setCurrentViewport(tempViewport, true);
		}

		/**
		 * Viewport listener for preview chart(lower one). in {@link #onViewportChanged(Viewport)} method change
		 * viewport of upper chart.
		 */
		private class ViewportListener implements ViewportChangeListener {

			@Override
			public void onViewportChanged(Viewport newViewport) {
				// don't use animation, it is unnecessary when using preview chart.
				chart.setCurrentViewport(newViewport, false);
			}

		}

	}
}
