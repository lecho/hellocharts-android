package lecho.lib.hellocharts.samples;

import java.util.ArrayList;
import java.util.List;

import lecho.lib.hellocharts.Chart;
import lecho.lib.hellocharts.gesture.ChartZoomer;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.BubbleChartData;
import lecho.lib.hellocharts.model.BubbleValue;
import lecho.lib.hellocharts.util.Utils;
import lecho.lib.hellocharts.view.BubbleChartView;
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

public class BubbleChartActivity extends ActionBarActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_bubble_chart);
		if (savedInstanceState == null) {
			getSupportFragmentManager().beginTransaction().add(R.id.container, new PlaceholderFragment()).commit();
		}
	}

	/**
	 * A fragment containing a bubble chart.
	 */
	public static class PlaceholderFragment extends Fragment {

		private static final int BUBBLES_NUM = 8;

		private BubbleChartView chart;
		private BubbleChartData data;
		private boolean hasAxes = true;

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
			setHasOptionsMenu(true);
			View rootView = inflater.inflate(R.layout.fragment_bubble_chart, container, false);

			chart = (BubbleChartView) rootView.findViewById(R.id.chart);
			chart.setOnValueTouchListener(new ValueTouchListener());

			generateDefaultData();
			chart.setBubbleChartData(data);

			return rootView;
		}

		@Override
		public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
			inflater.inflate(R.menu.bubble_chart, menu);
		}

		@Override
		public boolean onOptionsItemSelected(MenuItem item) {
			int id = item.getItemId();
			if (id == R.id.action_reset) {
				generateDefaultData();
				chart.setBubbleChartData(data);
				return true;
			}
			if (id == R.id.action_shape_circles) {
				setCircles();
				chart.setBubbleChartData(data);
				return true;
			}
			if (id == R.id.action_shape_square) {
				setSquares();
				chart.setBubbleChartData(data);
				return true;
			}
			if (id == R.id.action_toggle_labels) {
				toggleLabels();
				chart.setBubbleChartData(data);
				return true;
			}
			if (id == R.id.action_toggle_axes) {
				toggleAxes();
				chart.setBubbleChartData(data);
				return true;
			}
			if (id == R.id.action_toggle_axes_names) {
				toggleAxesNames();
				chart.setBubbleChartData(data);
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
				chart.setBubbleChartData(data);
				Toast.makeText(
						getActivity(),
						"Label for selected to " + data.hasLabelsOnlyForSelected()
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

			List<BubbleValue> values = new ArrayList<BubbleValue>();
			for (int i = 0; i < BUBBLES_NUM; ++i) {
				BubbleValue value = new BubbleValue(i, (float) Math.random() * 100, (float) Math.random() * 1000);
				value.setColor(Utils.pickColor());
				values.add(value);
			}

			data = new BubbleChartData(values);

			data.getAxisXBottom().setName("Axis X");
			data.getAxisYLeft().setName("Axis Y");

		}

		private void setCircles() {
			for (BubbleValue value : data.getValues()) {
				value.setShape(BubbleValue.SHAPE_CIRCLE);
			}
		}

		private void setSquares() {
			for (BubbleValue value : data.getValues()) {
				value.setShape(BubbleValue.SHAPE_SQUARE);
			}
		}

		private void toggleLabels() {
			data.setHasLabels(!data.hasLabels());
		}

		private void toggleLabelForSelected() {
			data.setHasLabelsOnlyForSelected(!data.hasLabelsOnlyForSelected());
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
			for (BubbleValue value : data.getValues()) {
				value.setTarget(value.getX() + (float) Math.random() * 4 * getSign(), (float) Math.random() * 100,
						(float) Math.random() * 1000);
			}
		}

		private int getSign() {
			int[] sign = new int[] { -1, 1 };
			return sign[Math.round((float) Math.random())];
		}

		private class ValueTouchListener implements BubbleChartView.BubbleChartOnValueTouchListener {

			@Override
			public void onValueTouched(int selectedBubble, BubbleValue value) {
				Toast.makeText(getActivity(), "Selected: " + value, Toast.LENGTH_SHORT).show();
			}

		}
	}
}
