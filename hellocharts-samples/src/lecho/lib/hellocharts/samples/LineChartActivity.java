package lecho.lib.hellocharts.samples;

import java.util.ArrayList;
import java.util.List;

import lecho.lib.hellocharts.gesture.ChartZoomer;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.util.Utils;
import lecho.lib.hellocharts.view.LineChartView;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

public class LineChartActivity extends ActionBarActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_line_chart);
		if (savedInstanceState == null) {
			getSupportFragmentManager().beginTransaction().add(R.id.container, new PlaceholderFragment()).commit();
		}
	}

	/**
	 * A placeholder fragment containing a line chart.
	 */
	public static class PlaceholderFragment extends Fragment {

		private LineChartView chart;
		private LineChartData data;

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
			setHasOptionsMenu(true);
			View rootView = inflater.inflate(R.layout.fragment_line_chart, container, false);
			chart = (LineChartView) rootView.findViewById(R.id.chart);
			chart.setOnValueTouchListener(new ValueTouchListener());

			generateDefaultData();
			chart.setLineChartData(data);

			return rootView;
		}

		// MENU
		@Override
		public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
			inflater.inflate(R.menu.line_chart, menu);
		}

		private void generateDefaultData() {
			final int numValues = 12;

			List<PointValue> values = new ArrayList<PointValue>();
			for (int i = 0; i < numValues; ++i) {
				values.add(new PointValue(i, (float) Math.random() * 100f));
			}

			Line line = new Line(values);
			line.setColor(Utils.COLOR_ORANGE);

			List<Line> lines = new ArrayList<Line>();
			lines.add(line);

			data = new LineChartData(lines);

			data.getAxisX().setName("Axis X");
			data.getAxisY().setName("Axis Y");

		}

		@Override
		public boolean onOptionsItemSelected(MenuItem item) {
			// Handle action bar item clicks here. The action bar will
			// automatically handle clicks on the Home/Up button, so long
			// as you specify a parent activity in AndroidManifest.xml.
			int id = item.getItemId();
			if (id == R.id.action_reset) {
				generateDefaultData();
				chart.setLineChartData(data);
				return true;
			}
			if (id == R.id.action_add_line) {
				return true;
			}
			if (id == R.id.action_toggle_lines) {
				return true;
			}
			if (id == R.id.action_toggle_points) {
				return true;
			}
			if (id == R.id.action_toggle_bezier) {
				return true;
			}
			if (id == R.id.action_toggle_area) {
				return true;
			}
			if (id == R.id.action_shape_circles) {
				return true;
			}
			if (id == R.id.action_shape_square) {
				return true;
			}
			if (id == R.id.action_toggle_axes) {
				return true;
			}
			if (id == R.id.action_toggle_axes_names) {
				return true;
			}
			if (id == R.id.action_toggle_labels) {
				return true;
			}
			if (id == R.id.action_animate) {
				return true;
			}
			if (id == R.id.action_toggle_selection_mode) {
				chart.setValueSelectionEnabled(!chart.isValueSelectionEnabled());
				return true;
			}
			if (id == R.id.action_toggle_touch_zoom) {
				chart.setZoomEnabled(!chart.isZoomEnabled());
				return true;
			}
			if (id == R.id.action_zoom_both) {
				chart.setZoomType(ChartZoomer.ZOOM_HORIZONTAL_AND_VERTICAL);
				return true;
			}
			if (id == R.id.action_zoom_horizontal) {
				chart.setZoomType(ChartZoomer.ZOOM_HORIZONTAL);
				return true;
			}
			if (id == R.id.action_zoom_vertical) {
				chart.setZoomType(ChartZoomer.ZOOM_VERTICAL);
				return true;
			}
			return super.onOptionsItemSelected(item);
		}

		private class ValueTouchListener implements LineChartView.LineChartOnValueTouchListener {

			@Override
			public void onValueTouched(int selectedLine, int selectedValue, PointValue value) {
				Toast.makeText(getActivity(), "Selected: " + value, Toast.LENGTH_SHORT).show();

			}

		}
	}
}
