package lecho.lib.hellocharts.samples;

import java.util.ArrayList;
import java.util.List;

import lecho.lib.hellocharts.Chart;
import lecho.lib.hellocharts.model.ArcValue;
import lecho.lib.hellocharts.model.PieChartData;
import lecho.lib.hellocharts.util.Utils;
import lecho.lib.hellocharts.view.PieChartView;
import lecho.lib.hellocharts.view.PieChartView.PieChartOnValueTouchListener;
import android.graphics.Typeface;
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

public class PieChartActivity extends ActionBarActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_pie_chart);
		if (savedInstanceState == null) {
			getSupportFragmentManager().beginTransaction().add(R.id.container, new PlaceholderFragment()).commit();
		}
	}

	/**
	 * A fragment containing a pie chart.
	 */
	public static class PlaceholderFragment extends Fragment {

		private PieChartView chart;
		private PieChartData data;

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
			setHasOptionsMenu(true);
			View rootView = inflater.inflate(R.layout.fragment_pie_chart, container, false);

			chart = (PieChartView) rootView.findViewById(R.id.chart);
			chart.setOnValueTouchListener(new ValueTouchListener());

			generateDefaultData();
			chart.setPieChartData(data);

			// Typeface myTypeface = Typeface.createFromAsset(getActivity().getAssets(), "Roboto-Italic.ttf");
			// data.setCenterText1Typeface(myTypeface);
			// data.setCenterText1("Hello");

			return rootView;
		}

		// MENU
		@Override
		public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
			inflater.inflate(R.menu.pie_chart, menu);
		}

		@Override
		public boolean onOptionsItemSelected(MenuItem item) {
			int id = item.getItemId();
			if (id == R.id.action_reset) {
				generateDefaultData();
				chart.setPieChartData(data);
				return true;
			}
			if (id == R.id.action_explode) {
				explodeChart();
				chart.setPieChartData(data);
				return true;
			}
			if (id == R.id.action_single_arc_separation) {
				separateSingleArc();
				chart.setPieChartData(data);
				return true;
			}
			if (id == R.id.action_toggle_labels) {
				toggleLabels();
				chart.setPieChartData(data);
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
				chart.setPieChartData(data);
				Toast.makeText(
						getActivity(),
						"Label for selected to " + data.hasLabelsOnlyForSelected()
								+ ". Works best with value selection mode.", Toast.LENGTH_SHORT).show();
				return true;
			}
			return super.onOptionsItemSelected(item);
		}

		private void generateDefaultData() {
			int numValues = 6;

			List<ArcValue> values = new ArrayList<ArcValue>();
			for (int i = 0; i < numValues; ++i) {
				values.add(new ArcValue((float) Math.random() * 30 + 15, Utils.pickColor()));
			}

			data = new PieChartData(values);
		}

		private void explodeChart() {
			for (ArcValue value : data.getValues()) {
				value.setArcSpacing(24);
			}
		}

		private void separateSingleArc() {
			generateDefaultData();
			data.getValues().get(0).setArcSpacing(32);
		}

		private void toggleLabels() {
			data.setHasLabels(!data.hasLabels());
		}

		private void toggleLabelForSelected() {
			data.setHasLabelsOnlyForSelected(!data.hasLabelsOnlyForSelected());
		}

		/**
		 * To animate values you have to change targets values and then call {@link Chart#startDataAnimation()}
		 * method(don't confuse with View.animate()).
		 */
		private void prepareDataAnimation() {
			for (ArcValue value : data.getValues()) {
				value.setTarget((float) Math.random() * 30 + 15);
			}
		}

		private class ValueTouchListener implements PieChartOnValueTouchListener {

			@Override
			public void onValueTouched(int selectedArc, ArcValue value) {
				Toast.makeText(getActivity(), "Selected: " + value, Toast.LENGTH_SHORT).show();

			}

		}
	}
}
