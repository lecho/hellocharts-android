package lecho.lib.hellocharts;

import java.util.ArrayList;
import java.util.List;

import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.LineSeries;
import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.RelativeLayout;

public class MainActivity extends Activity {
	private LineChart chart;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		RelativeLayout layout = (RelativeLayout) findViewById(R.id.layout);
		chart = new LineChart(this);
		List<Float> domain = new ArrayList<Float>();
		domain.add(1f);
		domain.add(2f);
		domain.add(3f);
		domain.add(4f);
		domain.add(5f);
		// domain.add(6f);
		// domain.add(7f);
		// domain.add(8f);
		// domain.add(9f);
		// domain.add(10f);
		LineChartData data = new LineChartData(domain);
		List<Float> s1 = new ArrayList<Float>();
		s1.add(1f);
		s1.add(5f);
		s1.add(6f);
		s1.add(10f);
		s1.add(3f);
		// s1.add(12f);
		// s1.add(8f);
		// s1.add(4f);
		// s1.add(6f);
		// s1.add(5f);
		List<Float> s2 = new ArrayList<Float>();
		s2.add(5f);
		s2.add(3f);
		s2.add(8f);
		s2.add(6f);
		s2.add(10f);
		// s2.add(3f);
		// s2.add(1f);
		// s2.add(3f);
		// s2.add(10f);
		// s2.add(5f);
		LineSeries l1 = new LineSeries(Color.parseColor("#FFBB33"), s1);
		LineSeries l2 = new LineSeries(Color.parseColor("#99CC00"), s2);
		data.addSeries(l1);
		data.addSeries(l2);
		chart.setData(data);
		layout.addView(chart);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.action_settings) {
			chart.invalidate();
		}
		return super.onOptionsItemSelected(item);
	}
}
