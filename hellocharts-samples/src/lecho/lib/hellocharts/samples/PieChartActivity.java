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

			generateData();

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
				reset();
				generateData();
				return true;
			}
			if (id == R.id.action_explode) {
				explodeChart();
				return true;
			}
			if (id == R.id.action_single_arc_separation) {
				separateSingleArc();
				return true;
			}
			if (id == R.id.action_center_circle) {
				hasCenterCircle = !hasCenterCircle;
				if (!hasCenterCircle) {
					hasCenterText1 = false;
					hasCenterText2 = false;
				}

				generateData();
				return true;
			}
			if (id == R.id.action_center_text1) {
				hasCenterText1 = !hasCenterText1;

				if (hasCenterText1) {
					hasCenterCircle = true;
				}

				hasCenterText2 = false;

				generateData();
				return true;
			}
			if (id == R.id.action_center_text2) {
				hasCenterText2 = !hasCenterText2;

				if (hasCenterText2) {
					hasCenterText1 = true;// text 2 need text 1 to by also drawn.
					hasCenterCircle = true;
				}

				generateData();
				return true;
			}
			if (id == R.id.action_toggle_labels) {
				toggleLabels();
				return true;
			}
			if (id == R.id.action_toggle_labels_outside) {
				toggleLabelsOutside();
				return true;
			}
			if (id == R.id.action_animate) {
				prepareDataAnimation();
				chart.startDataAnimation();
				return true;
			}
			if (id == R.id.action_toggle_selection_mode) {
				toggleLabelForSelected();
				Toast.makeText(getActivity(),
						"Selection mode set to " + chart.isValueSelectionEnabled() + " select any point.",
						Toast.LENGTH_SHORT).show();
				return true;
			}
			return super.onOptionsItemSelected(item);
		}

		private void reset() {
			chart.setCircleFillRatio(1.0f);
			hasLabels = false;
			hasLabelsOutside = false;
			hasCenterCircle = false;
			hasCenterText1 = false;
			hasCenterText2 = false;
			isExploaded = false;
			hasArcSeparated = false;
			hasLabelForSelected = false;
		}

		private void generateData() {
			int numValues = 6;

			List<ArcValue> values = new ArrayList<ArcValue>();
			for (int i = 0; i < numValues; ++i) {
				ArcValue arcValue = new ArcValue((float) Math.random() * 30 + 15, Utils.pickColor());

				if (isExploaded) {
					arcValue.setArcSpacing(24);
				}

				if (hasArcSeparated && i == 0) {
					arcValue.setArcSpacing(32);
				}

				values.add(arcValue);
			}

			data = new PieChartData(values);
			data.setHasLabels(hasLabels);
			data.setHasLabelsOnlyForSelected(hasLabelForSelected);
			data.setHasLabelsOutside(hasLabelsOutside);
			data.setHasCenterCircle(hasCenterCircle);

			if (hasCenterText1) {
				data.setCenterText1("Hello!");

				// Get roboto-italic font.
				Typeface tf = Typeface.createFromAsset(getActivity().getAssets(), "Roboto-Italic.ttf");
				data.setCenterText1Typeface(tf);

				// Get font size from dimens.xml and convert it to sp(library uses sp values).
				data.setCenterText1FontSize(Utils.px2sp(getResources().getDisplayMetrics().scaledDensity,
						(int) getResources().getDimension(R.dimen.pie_chart_text1_size)));
			}

			if (hasCenterText2) {
				data.setCenterText2("Charts (Roboto Italic)");

				Typeface tf = Typeface.createFromAsset(getActivity().getAssets(), "Roboto-Italic.ttf");

				data.setCenterText2Typeface(tf);
				data.setCenterText2FontSize(Utils.px2sp(getResources().getDisplayMetrics().scaledDensity,
						(int) getResources().getDimension(R.dimen.pie_chart_text2_size)));
			}

			chart.setPieChartData(data);
		}

		private void explodeChart() {
			isExploaded = !isExploaded;
			generateData();

		}

		private void separateSingleArc() {
			hasArcSeparated = !hasArcSeparated;
			if (hasArcSeparated) {
				isExploaded = false;
			}
			generateData();
		}

		private void toggleLabelsOutside() {
			// has labels have to be true:P
			hasLabelsOutside = !hasLabelsOutside;
			if (hasLabelsOutside) {
				hasLabels = true;
				hasLabelForSelected = false;
				chart.setValueSelectionEnabled(hasLabelForSelected);
			}

			if (hasLabelsOutside) {
				chart.setCircleFillRatio(0.7f);
			} else {
				chart.setCircleFillRatio(1.0f);
			}

			generateData();

		}

		private void toggleLabels() {
			hasLabels = !hasLabels;

			if (hasLabels) {
				hasLabelForSelected = false;
				chart.setValueSelectionEnabled(hasLabelForSelected);

				if (hasLabelsOutside) {
					chart.setCircleFillRatio(0.7f);
				} else {
					chart.setCircleFillRatio(1.0f);
				}
			}

			generateData();
		}

		private void toggleLabelForSelected() {
			hasLabelForSelected = !hasLabelForSelected;

			chart.setValueSelectionEnabled(hasLabelForSelected);

			if (hasLabelForSelected) {
				hasLabels = false;
				hasLabelsOutside = false;

				if (hasLabelsOutside) {
					chart.setCircleFillRatio(0.7f);
				} else {
					chart.setCircleFillRatio(1.0f);
				}
			}

			generateData();
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
