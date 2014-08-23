package lecho.sample.hellocharts;

import java.util.ArrayList;
import java.util.List;

import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.Viewport;
import lecho.lib.hellocharts.view.LineChartView;
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

public class LineChartActivity extends ActionBarActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_line_chart);

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
		private static final int NUM_OF_VALUES = 20;

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_line_chart, container, false);
			LinearLayout layout = (LinearLayout) rootView.findViewById(R.id.layout);

			final LineChartView chart = new LineChartView(getActivity());
			final LineChartData data = new LineChartData();
			List<PointValue> s1 = Utils.generatePoints(NUM_OF_VALUES, 1.0f);
			List<PointValue> s2 = Utils.generatePoints(NUM_OF_VALUES, 1.0f);
			Line l1 = new Line(s1);
			l1.setColor(Color.parseColor("#FFBB33"));
			l1.setFilled(false);
			l1.setHasLines(true);
			l1.setSmooth(true);
			Line l2 = new Line(s2);
			l2.setColor(Color.parseColor("#99CC00")).setFilled(true).setHasLines(true).setSmooth(false)
					.setHasLabels(false).setHasPoints(true);
			List<Line> lines = new ArrayList<Line>();
			// l2.getPoints().add(new PointValue(3.5f, 45f));
			lines.add(l2);
			// lines.add(l1);
			data.setLines(lines);
			data.setBaseValue(-23420f);
			Axis axisX = new Axis();
			axisX.setValues(Utils.generateAxis(0.0f, 100.0f, 1.0f));
			axisX.setName("Axis X");
			data.setAxisX(axisX);

			Axis axisY = new Axis();
			axisY.setValues(Utils.generateAxis(0.0f, 95.0f, 5.0f));
			axisY.setName("Axis Y");
			data.setAxisY(axisY);
			chart.setLineChartData(data);
			chart.setOnValueTouchListener(new LineChartView.LineChartOnValueTouchListener() {

				@Override
				public void onValueTouched(int selectedLine, int selectedValue, PointValue point) {
					// Toast.makeText(getActivity(),
					// "" + selectedLine + " " + selectedValue + " " + point.getX() + " " + point.getY(),
					// Toast.LENGTH_SHORT).show();
					// chart.setViewport(new Viewport(2, 45, 4, 20), true);

				}
			});
			// chart.setBackgroundColor(Color.WHITE);
			// chart.setInteractive(false);
			layout.addView(chart);
			return rootView;
		}
	}

}
