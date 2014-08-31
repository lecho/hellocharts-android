package lecho.lib.hellocharts.samples;

import java.util.ArrayList;
import java.util.List;

import lecho.lib.hellocharts.Chart;
import lecho.lib.hellocharts.gesture.ChartZoomer;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.Viewport;
import lecho.lib.hellocharts.util.Utils;
import lecho.lib.hellocharts.view.LineChartView;
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
	 * A fragment containing a line chart.
	 */
	public static class PlaceholderFragment extends Fragment {

		private LineChartView chart;
		private LineChartData data;
		private boolean hasAxes = true;

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

		@Override
		public boolean onOptionsItemSelected(MenuItem item) {
			int id = item.getItemId();
			if (id == R.id.action_reset) {
				generateDefaultData();
				chart.setLineChartData(data);
				return true;
			}
			if (id == R.id.action_add_line) {
				addLineToData();
				chart.setLineChartData(data);
				return true;
			}
			if (id == R.id.action_toggle_lines) {
				toggleLines();
				chart.setLineChartData(data);
				return true;
			}
			if (id == R.id.action_toggle_points) {
				togglePoints();
				chart.setLineChartData(data);
				return true;
			}
			if (id == R.id.action_toggle_bezier) {
				toggleBezier();
				chart.setLineChartData(data);

				// It is good idea to manually set a little higher max viewport for cubic lines because sometimes line
				// go above or below max/min. To do that use Viewport.inest() method and pas negative value as dy
				// parameter.
				// Remember to set viewport after you call setLineChartData().
				Viewport v = chart.getMaxViewport();
				float dy = v.height() * 0.05f;// just 10%, 5% on the top and 5% on the bottom
				v.inset(0, -dy);
				chart.setMaxViewport(v);
				chart.setViewport(v, true);
				return true;
			}
			if (id == R.id.action_toggle_area) {
				toggleArea();
				chart.setLineChartData(data);
				return true;
			}
			if (id == R.id.action_shape_circles) {
				setCircles();
				chart.setLineChartData(data);
				return true;
			}
			if (id == R.id.action_shape_square) {
				setSquares();
				chart.setLineChartData(data);
				return true;
			}
			if (id == R.id.action_toggle_labels) {
				toggleLabels();
				chart.setLineChartData(data);
				return true;
			}
			if (id == R.id.action_toggle_axes) {
				toggleAxes();
				chart.setLineChartData(data);
				return true;
			}
			if (id == R.id.action_toggle_axes_names) {
				toggleAxesNames();
				chart.setLineChartData(data);
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
			if (id == R.id.action_toggle_label_for_selected) {
				toggleLabelForSelected();
				chart.setLineChartData(data);
				Toast.makeText(
						getActivity(),
						"Label for selected to " + data.getLines().get(0).hasLabelsOnlyForSelected()
								+ ". Works best with value selection mode.", Toast.LENGTH_SHORT).show();
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
			int numValues = 12;

			List<PointValue> values = new ArrayList<PointValue>();
			for (int i = 0; i < numValues; ++i) {
				values.add(new PointValue(i, (float) Math.random() * 100f));
			}

			Line line = new Line(values);
			line.setColor(Utils.COLOR_GREEN);

			List<Line> lines = new ArrayList<Line>();
			lines.add(line);

			data = new LineChartData(lines);
			data.getAxisXBottom().setName("Axis X");
			data.getAxisYLeft().setName("Axis Y");

		}

		/**
		 * Adds lines to data, after that data should be set again with
		 * {@link LineChartView#setLineChartData(LineChartData)}. Last 4th line has non-monotonically x values.
		 */
		private void addLineToData() {
			if (data.getLines().size() >= 4) {
				Toast.makeText(getActivity(), "Samples app uses max 4 lines!", Toast.LENGTH_SHORT).show();
				return;
			}

			int numValues = 12;
			List<PointValue> values = new ArrayList<PointValue>();
			for (int i = 0; i < numValues; ++i) {
				values.add(new PointValue(i, (float) Math.random() * 100));
			}

			Line line = new Line();

			int linesNum = data.getLines().size();
			switch (linesNum) {
			case 1:
				line.setColor(Utils.COLOR_BLUE);
				break;
			case 2:
				line.setColor(Utils.COLOR_ORANGE);
				break;
			default:
				// Line chart support lines with different X values and X values don't have to be monotonically.
				line.setColor(Utils.COLOR_VIOLET);
				values = new ArrayList<PointValue>();
				for (int i = 0; i < numValues; ++i) {
					values.add(new PointValue((float) Math.random() * 12, (float) Math.random() * 100));
				}
				Toast.makeText(getActivity(), "Crazy violet line:)", Toast.LENGTH_SHORT).show();
				break;
			}

			line.setValues(values);
			data.getLines().add(line);
		}

		private void toggleLines() {
			for (Line line : data.getLines()) {
				line.setHasLines(!line.hasLines());
			}
		}

		private void togglePoints() {
			for (Line line : data.getLines()) {
				line.setHasPoints(!line.hasPoints());
			}
		}

		private void toggleBezier() {
			for (Line line : data.getLines()) {
				line.setSmooth(!line.isSmooth());
			}
		}

		private void toggleArea() {
			for (Line line : data.getLines()) {
				line.setFilled(!line.isFilled());
			}
		}

		private void setCircles() {
			for (Line line : data.getLines()) {
				line.setPointShape(Line.SHAPE_CIRCLE);
			}
		}

		private void setSquares() {
			for (Line line : data.getLines()) {
				line.setPointShape(Line.SHAPE_SQUARE);
			}
		}

		private void toggleLabels() {
			for (Line line : data.getLines()) {
				line.setHasLabels(!line.hasLabels());
			}
		}

		private void toggleLabelForSelected() {
			for (Line line : data.getLines()) {
				line.setHasLabelsOnlyForSelected(!line.hasLabelsOnlyForSelected());
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
		 * method(don't confuse with View.animate()). If you operate on data that was set before you don't have to call
		 * {@link LineChartView#setLineChartData(LineChartData)} again.
		 */
		private void prepareDataAnimation() {
			for (Line line : data.getLines()) {
				for (PointValue value : line.getValues()) {
					// Here I modify target only for Y values but it is OK to modify X targets as well.
					value.setTarget(value.getX(), (float) Math.random() * 100);
				}
			}
		}

		private class ValueTouchListener implements LineChartView.LineChartOnValueTouchListener {

			@Override
			public void onValueTouched(int selectedLine, int selectedValue, PointValue value) {
				Toast.makeText(getActivity(), "Selected: " + value, Toast.LENGTH_SHORT).show();

			}

		}
	}
}
