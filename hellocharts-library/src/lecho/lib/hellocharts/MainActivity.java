package lecho.lib.hellocharts;

import java.util.ArrayList;
import java.util.List;

import lehco.lib.hellocharts.model.LineChartData;
import lehco.lib.hellocharts.model.LineSeries;
import android.os.Bundle;
import android.app.Activity;
import android.graphics.Color;
import android.view.Menu;
import android.widget.RelativeLayout;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		RelativeLayout layout = (RelativeLayout) findViewById(R.id.layout);
		LineChart lineChart = new LineChart(this);
		List<Float> domain = new ArrayList<Float>();
		domain.add(1f);
		domain.add(2f);
		domain.add(3f);
		domain.add(4f);
		domain.add(5f);
		LineChartData data = new LineChartData(domain);
		List<Float> s1 = new ArrayList<Float>();
		s1.add(1f);
		s1.add(5f);
		s1.add(6f);
		s1.add(10f);
		s1.add(3f);
		List<Float> s2 = new ArrayList<Float>();
		s2.add(5f);
		s2.add(3f);
		s2.add(8f);
		s2.add(6f);
		s2.add(10f);
		LineSeries l1 = new LineSeries(Color.parseColor("#FFBB33"), s1);
		LineSeries l2 = new LineSeries(Color.parseColor("#99CC00"), s2);
		data.addSeries(l1);
		data.addSeries(l2);
		lineChart.setData(data);
		lineChart.setPadding(20, 20, 20, 20);
		layout.addView(lineChart);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
