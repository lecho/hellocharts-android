package lecho.lib.hellocharts;

import java.util.ArrayList;
import java.util.List;

import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.ChartData;
import lecho.lib.hellocharts.model.Series;
import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

public class MainActivity extends Activity {
	private LineChart chart;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		Button btn = (Button) findViewById(R.id.button);
		btn.setOnClickListener(new View.OnClickListener() {
			int counter = 0;

			@Override
			public void onClick(View v) {
				if (++counter % 2 != 0) {
					List<Float> s1 = new ArrayList<Float>();
					s1.add(200f);
					s1.add(400f);
					s1.add(700f);
					s1.add(600f);
					s1.add(800f);
					chart.animateSeries(0, s1);
				} else {
					List<Float> s2 = new ArrayList<Float>();
					s2.add(300f);
					s2.add(500f);
					s2.add(550f);
					s2.add(800f);
					s2.add(1700f);
					chart.animateSeries(0, s2);
				}

			}
		});

		LinearLayout layout = (LinearLayout) findViewById(R.id.layout);
		chart = new LineChart(this);
		List<Float> domain = new ArrayList<Float>();
		domain.add(1f);
		domain.add(2f);
		domain.add(3f);
		domain.add(4f);
		domain.add(5f);
		final ChartData data = new ChartData(domain);
		List<Float> s1 = new ArrayList<Float>();
		s1.add(150f);
		s1.add(500f);
		s1.add(600f);
		s1.add(100f);
		s1.add(300f);
		s1.add(150f);
		List<Float> s2 = new ArrayList<Float>();
		s2.add(500f);
		s2.add(300f);
		s2.add(800f);
		s2.add(650f);
		s2.add(150f);
		data.addSeries(new Series(Color.parseColor("#FFBB33"), s1));
		data.addSeries(new Series(Color.parseColor("#99CC00"), s2));
		List<Float> yRules = new ArrayList<Float>();
		yRules.add(100f);
		yRules.add(300f);
		yRules.add(500f);
		yRules.add(700f);
		yRules.add(900f);
		yRules.add(1100f);
		yRules.add(1300f);
		yRules.add(1500f);
		yRules.add(1700f);
		yRules.add(1900f);
		Axis yAxis = new Axis();
		yAxis.setValues(yRules);
		data.setYAxis(yAxis);

		List<Float> xRules = new ArrayList<Float>();
		xRules.add(1f);
		xRules.add(2f);
		xRules.add(3f);
		xRules.add(4f);
		xRules.add(5f);
		Axis xAxis = new Axis();
		xAxis.setValues(xRules);
		List<String> xRulesText = new ArrayList<String>();
		xRulesText.add("Mon");
		xRulesText.add("Tue");
		xRulesText.add("Wen");
		xRulesText.add("Thu");
		xRulesText.add("Fri");
		xAxis.setStringValues(xRulesText);
		xAxis.setStringAxis(true);
		data.setXAxis(xAxis);

		chart.setData(data);
		// chart.setBackgroundColor(Color.WHITE);
		chart.setOnPointClickListener(new OnPointClickListener() {

			@Override
			public void onPointClick(int selectedSeriesIndex, int selectedValueIndex, float x, float y) {
				Toast.makeText(
						getApplicationContext(),
						"Series index: " + selectedSeriesIndex + " Value index: " + selectedValueIndex
								+ " That gives point: x=" + x + ", y=" + y, Toast.LENGTH_SHORT).show();

			}
		});
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
		return super.onOptionsItemSelected(item);
	}
}
