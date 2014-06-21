package lecho.sample.hellocharts;

import java.util.ArrayList;
import java.util.List;

import lecho.lib.hellocharts.LineChartView;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.LinePoint;
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
		private static final int NUM_OF_VALUES = 5;

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_line_chart, container, false);
			LinearLayout layout = (LinearLayout) rootView.findViewById(R.id.layout);

			LineChartView chart = new LineChartView(getActivity());
			final LineChartData data = new LineChartData();
			List<LinePoint> s1 = Utils.generatePoints(NUM_OF_VALUES, 1.0f);
			List<LinePoint> s2 = Utils.generatePoints(NUM_OF_VALUES, 1.0f);
			Line l1 = new Line(s1);
			l1.getStyle().setColor(Color.parseColor("#FFBB33"));
			l1.getStyle().setFilled(false);
			l1.getStyle().setHasLines(true);
			l1.getStyle().setSmooth(true);
			Line l2 = new Line(s2);
			l2.getStyle().setColor(Color.parseColor("#99CC00")).setFilled(false).setHasLines(true).setSmooth(true)
					.setHasAnnotations(true);
			List<Line> lines = new ArrayList<Line>();
			lines.add(l2);
			lines.add(l1);
			data.lines = lines;
			Axis axisX = new Axis();
			axisX.values = Utils.generateAxis(0.0f, 100.0f, 1.0f);
			axisX.name = "Axis X";
			axisX.textSize = 14;
			axisX.color = Color.parseColor("#FFBB33");
			data.setAxisX(axisX);

			Axis axisY = new Axis();
			axisY.values = Utils.generateAxis(0.0f, 100.0f, 15.0f);
			axisY.name = "Axis Y";
			axisY.textSize = 14;
			axisY.color = Color.parseColor("#99CC00");
			data.setAxisY(axisY);
			chart.setData(data);
			// chart.setBackgroundColor(Color.WHITE);
			layout.addView(chart);
			return rootView;
		}
	}

}
