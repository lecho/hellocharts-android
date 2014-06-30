package lecho.sample.hellocharts;

import java.util.ArrayList;
import java.util.List;

import lecho.lib.hellocharts.ColumnChartView;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.Column;
import lecho.lib.hellocharts.model.ColumnChartData;
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

public class ColumnChartActivity extends ActionBarActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_column_chart);

		if (savedInstanceState == null) {
			getSupportFragmentManager().beginTransaction().add(R.id.container, new PlaceholderFragment()).commit();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.column_chart, menu);
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

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_column_chart, container, false);
			LinearLayout layout = (LinearLayout) rootView.findViewById(R.id.layout);

			ColumnChartView chart = new ColumnChartView(getActivity());
			final ColumnChartData data = new ColumnChartData();
			// List<ValueWithColor> s1 = generateValues(1);
			// List<ValueWithColor> s2 = generateValues(1);
			// Bar l1 = new Bar(s1);
			// l1.hasValuesPopups = true;
			// Bar l2 = new Bar(s2);
			List<Column> columns = new ArrayList<Column>();
			for (int i = 0; i < 12; ++i) {
				columns.add(Utils.generateColumns());
			}
			data.setColumns(columns);
			Axis axisX = new Axis();
			axisX.values = Utils.generateAxis(0.0f, 10.0f, 2.0f);
			axisX.name = "Axis X";
			axisX.textSize = 14;
			axisX.color = Color.parseColor("#FFBB33");
			data.setAxisX(axisX);

			Axis axisY = new Axis();
			axisY.values = Utils.generateAxis(0.0f, 10.0f, 2.0f);
			axisY.name = "Axis Y";
			axisY.textSize = 14;
			axisY.color = Color.parseColor("#99CC00");
			data.setAxisY(axisY);
			data.setStacked(true);

			chart.setData(data);
			// chart.setBackgroundColor(Color.WHITE);
			// // chart.setPadding(10, 10, 10, 20);
			layout.addView(chart);
			return rootView;
		}
	}

}
