package lecho.lib.hellocharts.samples;

import java.util.ArrayList;
import java.util.List;

import lecho.lib.hellocharts.Chart;
import lecho.lib.hellocharts.gesture.ChartZoomer;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.Column;
import lecho.lib.hellocharts.model.ColumnChartData;
import lecho.lib.hellocharts.model.ColumnValue;
import lecho.lib.hellocharts.util.Utils;
import lecho.lib.hellocharts.view.ColumnChartView;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

public class ColumnChartActivity extends ActionBarActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_column_chart);
		if (savedInstanceState == null) {
			getSupportFragmentManager().beginTransaction().add(R.id.container, new PlaceholderFragment()).commit();
		}
	}

	/**
	 * A fragment containing a column chart.
	 */
	public static class PlaceholderFragment extends Fragment {

		private ColumnChartView chart;
		private ColumnChartData data;
		private boolean hasAxes = true;

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
			setHasOptionsMenu(true);
			View rootView = inflater.inflate(R.layout.fragment_column_chart, container, false);

			chart = (ColumnChartView) rootView.findViewById(R.id.chart);
			chart.setOnValueTouchListener(new ValueTouchListener());

			generateDefaultData();
			chart.setColumnChartData(data);

			return rootView;
		}

		// MENU
		@Override
		public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
			inflater.inflate(R.menu.column_chart, menu);
		}

		@Override
		public boolean onOptionsItemSelected(MenuItem item) {
			int id = item.getItemId();
			if (id == R.id.action_reset) {
				generateDefaultData();
				chart.setColumnChartData(data);
				return true;
			}
			if (id == R.id.action_subcolumns) {
				showSubcolumns();
				chart.setColumnChartData(data);
				return true;
			}
			if (id == R.id.action_stacked) {
				showStacked();
				chart.setColumnChartData(data);
				return true;
			}
			if (id == R.id.action_negative_subcolumns) {
				showNegativeSubcolumns();
				chart.setColumnChartData(data);
				return true;
			}
			if (id == R.id.action_negative_stacked) {
				showNegativeStacked();
				chart.setColumnChartData(data);
				return true;
			}
			if (id == R.id.action_toggle_labels) {
				toggleLabels();
				chart.setColumnChartData(data);
				return true;
			}
			if (id == R.id.action_toggle_label_for_selected) {
				toggleLabelForSelected();
				chart.setColumnChartData(data);
				Toast.makeText(
						getActivity(),
						"Label for selected to " + data.getColumns().get(0).hasLabelsOnlyForSelected()
								+ ". Works best with value selection mode.", Toast.LENGTH_SHORT).show();
				return true;
			}
			if (id == R.id.action_toggle_axes) {
				toggleAxes();
				chart.setColumnChartData(data);
				return true;
			}
			if (id == R.id.action_toggle_axes_names) {
				toggleAxesNames();
				chart.setColumnChartData(data);
				return true;
			}
			if (id == R.id.action_animate) {
				prepareDataAnimation();
				chart.startDataAnimation();
				return true;
			}
			if (id == R.id.action_toggle_selection_mode) {
				chart.setValueSelectionEnabled(!chart.isValueSelectionEnabled());
				Toast.makeText(getActivity(),
						"Selection mode set to " + chart.isValueSelectionEnabled() + " select any point.",
						Toast.LENGTH_SHORT).show();
				return true;
			}
			if (id == R.id.action_toggle_touch_zoom) {
				chart.setZoomEnabled(!chart.isZoomEnabled());
				Toast.makeText(getActivity(), "IsZoomEnabled " + chart.isZoomEnabled(), Toast.LENGTH_SHORT).show();
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

		private void generateDefaultData() {
			int numSubcolumns = 1;
			int numColumns = 8;
			// Column can have many subcolumns, here by default I use 1 subcolumn in each of 8 columns.
			List<Column> columns = new ArrayList<Column>();
			List<ColumnValue> values;
			for (int i = 0; i < numColumns; ++i) {

				values = new ArrayList<ColumnValue>();
				for (int j = 0; j < numSubcolumns; ++j) {
					values.add(new ColumnValue((float) Math.random() * 50f + 5, Utils.pickColor()));
				}

				columns.add(new Column(values));
			}

			data = new ColumnChartData(columns);

			data.setAxisXBottom(new Axis().setName("Axis X"));
			data.setAxisYLeft(new Axis().setName("Axis Y").setHasLines(true));

		}

		/**
		 * Generates columns with subcolumns, columns have larger separation than subcolumns.
		 */
		private void showSubcolumns() {
			int numSubcolumns = 4;
			int numColumns = 4;
			// Column can have many subcolumns, here I use 4 subcolumn in each of 8 columns.
			List<Column> columns = new ArrayList<Column>();
			List<ColumnValue> values;
			for (int i = 0; i < numColumns; ++i) {

				values = new ArrayList<ColumnValue>();
				for (int j = 0; j < numSubcolumns; ++j) {
					values.add(new ColumnValue((float) Math.random() * 50f + 5, Utils.pickColor()));
				}

				columns.add(new Column(values));
			}

			data = new ColumnChartData(columns);

			data.setAxisXBottom(new Axis().setName("Axis X"));
			data.setAxisYLeft(new Axis().setName("Axis Y").setHasLines(true));

		}

		/**
		 * Generates columns with stacked subcolumns.
		 */
		private void showStacked() {
			int numSubcolumns = 4;
			int numColumns = 8;
			// Column can have many stacked subcolumns, here I use 4 stacke subcolumn in each of 4 columns.
			List<Column> columns = new ArrayList<Column>();
			List<ColumnValue> values;
			for (int i = 0; i < numColumns; ++i) {

				values = new ArrayList<ColumnValue>();
				for (int j = 0; j < numSubcolumns; ++j) {
					values.add(new ColumnValue((float) Math.random() * 20f + 5, Utils.pickColor()));
				}

				columns.add(new Column(values));
			}

			data = new ColumnChartData(columns);

			// Set stacked flag.
			data.setStacked(true);

			data.setAxisXBottom(new Axis().setName("Axis X"));
			data.setAxisYLeft(new Axis().setName("Axis Y").setHasLines(true));
		}

		private void showNegativeSubcolumns() {

			int numSubcolumns = 4;
			int numColumns = 4;
			List<Column> columns = new ArrayList<Column>();
			List<ColumnValue> values;
			for (int i = 0; i < numColumns; ++i) {

				values = new ArrayList<ColumnValue>();
				for (int j = 0; j < numSubcolumns; ++j) {
					int sign = getSign();
					values.add(new ColumnValue((float) Math.random() * 50f * sign + 5 * sign, Utils.pickColor()));
				}

				columns.add(new Column(values));
			}

			data = new ColumnChartData(columns);

			data.setAxisXBottom(new Axis().setName("Axis X"));
			data.setAxisYLeft(new Axis().setName("Axis Y").setHasLines(true));
		}

		private void showNegativeStacked() {

			int numSubcolumns = 4;
			int numColumns = 8;
			// Column can have many stacked subcolumns, here I use 4 stacke subcolumn in each of 4 columns.
			List<Column> columns = new ArrayList<Column>();
			List<ColumnValue> values;
			for (int i = 0; i < numColumns; ++i) {

				values = new ArrayList<ColumnValue>();
				for (int j = 0; j < numSubcolumns; ++j) {
					int sign = getSign();
					values.add(new ColumnValue((float) Math.random() * 20f * sign + 5 * sign, Utils.pickColor()));
				}

				columns.add(new Column(values));
			}

			data = new ColumnChartData(columns);

			// Set stacked flag.
			data.setStacked(true);

			data.setAxisXBottom(new Axis().setName("Axis X"));
			data.setAxisYLeft(new Axis().setName("Axis Y").setHasLines(true));
		}

		private int getSign() {
			int[] sign = new int[] { -1, 1 };
			return sign[Math.round((float) Math.random())];
		}

		private void toggleLabels() {
			for (Column column : data.getColumns()) {
				column.setHasLabels(!column.hasLabels());
			}
		}

		private void toggleLabelForSelected() {
			for (Column column : data.getColumns()) {
				column.setHasLabelsOnlyForSelected(!column.hasLabelsOnlyForSelected());
			}
		}

		private void toggleAxes() {
			if (!hasAxes) {
				// by default axes are auto-generated;
				data.setAxisXBottom(new Axis().setName("Axis X"));
				data.setAxisYLeft(new Axis().setName("Axis Y"));
			} else {
				// to disable axes set them to null;
				data.setAxisXBottom(null);
				data.setAxisYLeft(null);
			}
			hasAxes = !hasAxes;
		}

		private void toggleAxesNames() {
			if (hasAxes) {
				// by default axes are auto-generated;
				Axis axisX = data.getAxisXBottom();
				if (TextUtils.isEmpty(axisX.getName())) {
					axisX.setName("Axis X");
				} else {
					axisX.setName(null);
				}

				Axis axisY = data.getAxisYLeft();
				if (TextUtils.isEmpty(axisY.getName())) {
					axisY.setName("Axis Y");
				} else {
					axisY.setName(null);
				}
			}
		}

		/**
		 * To animate values you have to change targets values and then call {@link Chart#startDataAnimation()}
		 * method(don't confuse with View.animate()).
		 */
		private void prepareDataAnimation() {
			for (Column column : data.getColumns()) {
				for (ColumnValue value : column.getValues()) {
					value.setTarget((float) Math.random() * 100);
				}
			}
		}

		private class ValueTouchListener implements ColumnChartView.ColumnChartOnValueTouchListener {

			@Override
			public void onValueTouched(int selectedLine, int selectedValue, ColumnValue value) {
				Toast.makeText(getActivity(), "Selected: " + value, Toast.LENGTH_SHORT).show();

			}

			@Override
			public void onNothingTouched() {
				// TODO Auto-generated method stub

			}

		}

	}
}
