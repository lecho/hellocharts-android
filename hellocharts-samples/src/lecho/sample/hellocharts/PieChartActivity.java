package lecho.sample.hellocharts;

import java.util.ArrayList;
import java.util.List;

import lecho.lib.hellocharts.model.ArcValue;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.LinePoint;
import lecho.lib.hellocharts.model.PieChartData;
import lecho.lib.hellocharts.model.SelectedValue;
import lecho.lib.hellocharts.model.Viewport;
import lecho.lib.hellocharts.view.LineChartView;
import lecho.lib.hellocharts.view.PieChartView;
import lecho.lib.hellocharts.view.PieChartView.PieChartOnValueTouchListener;
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
		private static final int NUM_OF_VALUES = 5;

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_pie_chart, container, false);
			LinearLayout layout = (LinearLayout) rootView.findViewById(R.id.layout);

			final PieChartView chart = new PieChartView(getActivity());
			final PieChartData data = new PieChartData();
			layout.addView(chart);
			chart.setOnValueTouchListener(new PieChartOnValueTouchListener() {

				@Override
				public void onValueTouched(int selectedArc, ArcValue arcValue) {
					Toast.makeText(getActivity(), "listener", Toast.LENGTH_SHORT).show();
					chart.setChartRotation(160, true);

				}
			});
			// chart.setValueSelectionEnabled(true);
			// chart.selectValue(new SelectedValue(0, 0));
			// chart.setChartRotationEnabled(false);
			return rootView;
		}
	}

}
