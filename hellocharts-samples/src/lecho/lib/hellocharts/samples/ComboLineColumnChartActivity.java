package lecho.lib.hellocharts.samples;

import java.util.ArrayList;
import java.util.List;

import lecho.lib.hellocharts.gesture.ZoomType;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.Column;
import lecho.lib.hellocharts.model.ColumnChartData;
import lecho.lib.hellocharts.model.ColumnValue;
import lecho.lib.hellocharts.model.ComboLineColumnChartData;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.util.Utils;
import lecho.lib.hellocharts.view.ComboLineColumnChartView;
import lecho.lib.hellocharts.view.ComboLineColumnChartView.ComboLineColumnChartOnValueTouchListener;
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

public class ComboLineColumnChartActivity extends ActionBarActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_combo_line_column_chart);
		if (savedInstanceState == null) {
			getSupportFragmentManager().beginTransaction().add(R.id.container, new PlaceholderFragment()).commit();
		}
	}

	/**
	 * A fragment containing a combo line/column chart view.
	 */
	public static class PlaceholderFragment extends Fragment {

		private ComboLineColumnChartView chart;
		private ComboLineColumnChartData data;
		private boolean hasAxes = true;
		private boolean hasAxesNames = true;
		private boolean hasPoints = true;
		private boolean hasLines = true;
		private boolean isCubic = false;
		private boolean hasLabels = false;
		private boolean labelForSelected = false;

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
			setHasOptionsMenu(true);
			View rootView = inflater.inflate(R.layout.fragment_combo_line_column_chart, container, false);

			chart = (ComboLineColumnChartView) rootView.findViewById(R.id.chart);
			chart.setOnValueTouchListener(new ValueTouchListener());

			generateDefaultData();
			chart.setComboLineColumnChartData(data);

			return rootView;
		}

		// MENU
		@Override
		public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
			inflater.inflate(R.menu.combo_line_column_chart, menu);
		}

		@Override
		public boolean onOptionsItemSelected(MenuItem item) {
			int id = item.getItemId();
			if (id == R.id.action_reset) {
				generateDefaultData();
				chart.setComboLineColumnChartData(data);
				return true;
			}
			if (id == R.id.action_add_line) {
				addLineToData();
				chart.setComboLineColumnChartData(data);
				return true;
			}
			if (id == R.id.action_toggle_lines) {
				toggleLines();
				chart.setComboLineColumnChartData(data);
				return true;
			}
			if (id == R.id.action_toggle_points) {
				togglePoints();
				chart.setComboLineColumnChartData(data);
				return true;
			}
			if (id == R.id.action_toggle_cubic) {
				toggleBezier();
				chart.setComboLineColumnChartData(data);
				return true;
			}
			if (id == R.id.action_toggle_labels) {
				toggleLabels();
				chart.setComboLineColumnChartData(data);
				return true;
			}
			if (id == R.id.action_toggle_axes) {
				toggleAxes();
				chart.setComboLineColumnChartData(data);
				return true;
			}
			if (id == R.id.action_toggle_axes_names) {
				toggleAxesNames();
				chart.setComboLineColumnChartData(data);
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
				chart.setZoomType(ZoomType.HORIZONTAL_AND_VERTICAL);
				return true;
			}
			if (id == R.id.action_zoom_horizontal) {
				chart.setZoomType(ZoomType.HORIZONTAL);
				return true;
			}
			if (id == R.id.action_zoom_vertical) {
				chart.setZoomType(ZoomType.VERTICAL);
				return true;
			}
			return super.onOptionsItemSelected(item);
		}

		private void generateDefaultData() {
			// Chart looks the best when line data and column data have similar maximum viewports.
			data = new ComboLineColumnChartData(generateDefaultColumnData(), generateDefaultLineData());
			data.setAxisXBottom(new Axis().setName("Axis X"));
			data.setAxisYLeft(new Axis().setName("Axis Y").setHasLines(true));
		}

		private LineChartData generateDefaultLineData() {
			int numValues = 12;

			List<PointValue> values = new ArrayList<PointValue>();
			for (int i = 0; i < numValues; ++i) {
				values.add(new PointValue(i, (float) Math.random() * 50 + 5));
			}

			Line line = new Line(values);
			line.setColor(Utils.COLOR_ORANGE);

			List<Line> lines = new ArrayList<Line>();
			lines.add(line);

			LineChartData lineChartData = new LineChartData(lines);

			return lineChartData;

		}

		private ColumnChartData generateDefaultColumnData() {
			int numSubcolumns = 1;
			int numColumns = 12;
			// Column can have many subcolumns, here by default I use 1 subcolumn in each of 8 columns.
			List<Column> columns = new ArrayList<Column>();
			List<ColumnValue> values;
			for (int i = 0; i < numColumns; ++i) {

				values = new ArrayList<ColumnValue>();
				for (int j = 0; j < numSubcolumns; ++j) {
					values.add(new ColumnValue((float) Math.random() * 50 + 5, Utils.COLOR_GREEN));
				}

				columns.add(new Column(values));
			}

			ColumnChartData columnChartData = new ColumnChartData(columns);
			return columnChartData;
		}

		/**
		 * Adds lines to data, after that data should be set again with
		 * {@link LineChartView#setLineChartData(LineChartData)}. Last 4th line has non-monotonically x values.
		 */
		private void addLineToData() {
			if (data.getLineChartData().getLines().size() >= 4) {
				Toast.makeText(getActivity(), "Samples app uses max 4 lines!", Toast.LENGTH_SHORT).show();
				return;
			}

			int numValues = 12;
			List<PointValue> values = new ArrayList<PointValue>();
			for (int i = 0; i < numValues; ++i) {
				values.add(new PointValue(i, (float) Math.random() * 50 + 5));
			}

			Line line = new Line();

			int linesNum = data.getLineChartData().getLines().size();
			switch (linesNum) {
			case 1:
				line.setColor(Utils.COLOR_BLUE);
				break;
			case 2:
				line.setColor(Utils.COLOR_RED);
				break;
			default:
				// Line chart support lines with different X values and X values don't have to be monotonically.
				line.setColor(Utils.COLOR_VIOLET);
				values = new ArrayList<PointValue>();
				for (int i = 0; i < numValues; ++i) {
					values.add(new PointValue((float) Math.random() * 12, (float) Math.random() * 50f));
				}
				Toast.makeText(getActivity(), "Crazy violet line:)", Toast.LENGTH_SHORT).show();
				break;
			}

			line.setValues(values);
			data.getLineChartData().getLines().add(line);
		}

		private void toggleLines() {
			hasLines = !hasLines;
			for (Line line : data.getLineChartData().getLines()) {
				line.setHasLines(hasLines);
			}
		}

		private void togglePoints() {
			hasPoints = !hasPoints;
			for (Line line : data.getLineChartData().getLines()) {
				line.setHasPoints(hasPoints);
			}
		}

		private void toggleBezier() {
			isCubic = !isCubic;
			for (Line line : data.getLineChartData().getLines()) {
				line.setCubic(isCubic);
			}
		}

		private void toggleLabels() {
			hasLabels = !hasLabels;
			for (Line line : data.getLineChartData().getLines()) {
				line.setHasLabels(hasLabels);
			}

			for (Column column : data.getColumnChartData().getColumns()) {
				column.setHasLabels(hasLabels);
			}

		}

		private void toggleLabelForSelected() {
			labelForSelected = !labelForSelected;
			for (Line line : data.getLineChartData().getLines()) {
				line.setHasLabelsOnlyForSelected(labelForSelected);
			}

			for (Column column : data.getColumnChartData().getColumns()) {
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
			hasAxesNames = false;
		}

		private void toggleAxesNames() {
			if (hasAxes) {
				hasAxesNames = !hasAxesNames;
				// by default axes are auto-generated;
				Axis axisX = data.getAxisXBottom();
				if (hasAxesNames) {
					axisX.setName("Axis X");
				} else {
					axisX.setName(null);
				}

				Axis axisY = data.getAxisYLeft();
				if (hasAxesNames) {
					axisY.setName("Axis Y");
				} else {
					axisY.setName(null);
				}
			}
		}

		private void prepareDataAnimation() {

			// Line animations
			for (Line line : data.getLineChartData().getLines()) {
				for (PointValue value : line.getValues()) {
					// Here I modify target only for Y values but it is OK to modify X targets as well.
					value.setTarget(value.getX(), (float) Math.random() * 50 + 5);
				}
			}

			// Columns animations
			for (Column column : data.getColumnChartData().getColumns()) {
				for (ColumnValue value : column.getValues()) {
					value.setTarget((float) Math.random() * 50 + 5);
				}
			}
		}

		private class ValueTouchListener implements ComboLineColumnChartOnValueTouchListener {

			@Override
			public void onNothingTouched() {
				// TODO Auto-generated method stub

			}

			@Override
			public void onColumnValueTouched(int selectedLine, int selectedValue, ColumnValue value) {
				Toast.makeText(getActivity(), "Selected column: " + value, Toast.LENGTH_SHORT).show();

			}

			@Override
			public void onPointValueTouched(int selectedLine, int selectedValue, PointValue value) {
				Toast.makeText(getActivity(), "Selected line point: " + value, Toast.LENGTH_SHORT).show();

			}

		}
	}
}
