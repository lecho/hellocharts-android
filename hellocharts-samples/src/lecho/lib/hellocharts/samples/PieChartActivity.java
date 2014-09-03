package lecho.lib.hellocharts.samples;

import java.util.ArrayList;
import java.util.List;

import lecho.lib.hellocharts.model.ArcValue;
import lecho.lib.hellocharts.model.PieChartData;
import lecho.lib.hellocharts.util.Utils;
import lecho.lib.hellocharts.view.Chart;
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

		private boolean hasLabels = false;
		private boolean hasLabelsOutside = false;
		private boolean hasCenterCircle = false;
		private boolean hasCenterText1 = false;
		private boolean hasCenterText2 = false;
		private boolean isExploaded = false;
		private boolean hasArcSeparated = false;
		private boolean hasLabelForSelected = false;

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
			if (id == R.id.action_center_circle) {
				hasCenterCircle = !hasCenterCircle;

				data.setHasCenterCircle(hasCenterCircle);
				chart.setPieChartData(data);
				return true;
			}
			if (id == R.id.action_center_text1) {
				hasCenterText1 = !hasCenterText1;

				if (hasCenterText1) {
					hasCenterCircle = true;
				}
				hasCenterText2 = false;

				data.setHasCenterCircle(hasCenterCircle);
				if (hasCenterText1) {
					data.setCenterText1("Hello!");
				} else {
					data.setCenterText1(null);
				}
				data.setCenterText2(null);

				// Get roboto-italic font.
				Typeface tf = Typeface.createFromAsset(getActivity().getAssets(), "Roboto-Italic.ttf");
				data.setCenterText1Typeface(tf);

				// Get font size from dimens.xml and convert it to sp(library uses sp values).
				data.setCenterText1FontSize(Utils.px2sp(getResources().getDisplayMetrics().scaledDensity,
						(int) getResources().getDimension(R.dimen.pie_chart_text1_size)));
				chart.setPieChartData(data);
				return true;
			}
			if (id == R.id.action_center_text2) {
				hasCenterText2 = !hasCenterText2;

				if (hasCenterText2) {
					hasCenterText1 = true;// text 2 need text 1 to by also drawn.
					hasCenterCircle = true;
				}

				data.setHasCenterCircle(hasCenterCircle);
				if (hasCenterText2) {
					data.setCenterText1("Hello!");
					data.setCenterText2("Charts (Roboto Italic)");
				} else {
					data.setCenterText2(null);
				}

				Typeface tf = Typeface.createFromAsset(getActivity().getAssets(), "Roboto-Italic.ttf");

				data.setCenterText1Typeface(tf);
				data.setCenterText1FontSize(Utils.px2sp(getResources().getDisplayMetrics().scaledDensity,
						(int) getResources().getDimension(R.dimen.pie_chart_text1_size)));

				data.setCenterText2Typeface(tf);
				data.setCenterText2FontSize(Utils.px2sp(getResources().getDisplayMetrics().scaledDensity,
						(int) getResources().getDimension(R.dimen.pie_chart_text2_size)));

				chart.setPieChartData(data);
				return true;
			}
			if (id == R.id.action_toggle_labels) {
				toggleLabels();
				chart.setPieChartData(data);
				return true;
			}
			if (id == R.id.action_toggle_labels_outside) {
				toggleLabelsOutside();
				chart.setPieChartData(data);
				return true;
			}
			if (id == R.id.action_animate) {
				prepareDataAnimation();
				chart.startDataAnimation();
				return true;
			}
			if (id == R.id.action_toggle_selection_mode) {
				toggleLabelForSelected();
				chart.setPieChartData(data);
				Toast.makeText(getActivity(),
						"Selection mode set to " + chart.isValueSelectionEnabled() + " select any point.",
						Toast.LENGTH_SHORT).show();
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
			isExploaded = !isExploaded;
			if (isExploaded) {
				for (ArcValue value : data.getValues()) {
					value.setArcSpacing(24);
				}
			} else {
				for (ArcValue value : data.getValues()) {
					value.setArcSpacing(2);
				}
			}

		}

		private void separateSingleArc() {
			hasArcSeparated = !hasArcSeparated;
			if (hasArcSeparated) {
				// generateDefaultData();
				data.getValues().get(0).setArcSpacing(32);
			} else {
				data.getValues().get(0).setArcSpacing(2);
			}
		}

		private void toggleLabels() {
			hasLabels = !hasLabels;

			data.setHasLabels(hasLabels);

			if (hasLabels && hasLabelsOutside) {
				chart.setCircleFillRatio(0.7f);
			} else {
				chart.setCircleFillRatio(1.0f);
			}
		}

		private void toggleLabelsOutside() {
			// has labels have to be true:P
			hasLabelsOutside = !hasLabelsOutside;
			if (hasLabelsOutside) {
				hasLabels = true;
			}

			data.setHasLabels(hasLabels);
			data.setHasLabelsOutside(hasLabelsOutside);

			if (hasLabels && hasLabelsOutside) {
				chart.setCircleFillRatio(0.7f);
			} else {
				chart.setCircleFillRatio(1.0f);
			}
		}

		private void toggleLabelForSelected() {
			hasLabelForSelected = !hasLabelForSelected;
			data.setHasLabelsOnlyForSelected(hasLabelForSelected);
			chart.setValueSelectionEnabled(hasLabelForSelected);
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

			@Override
			public void onNothingTouched() {
				// TODO Auto-generated method stub

			}

		}
	}
}
