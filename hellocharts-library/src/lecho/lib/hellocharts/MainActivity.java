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
	private static final int NUM_OF_VALUES = 100;

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
					List<Float> s1 = generateValues(NUM_OF_VALUES, 1.0f);
					chart.animateSeries(0, s1);
				} else {
					List<Float> s2 = generateValues(NUM_OF_VALUES, 1.0f);
					chart.animateSeries(0, s2);
				}

			}
		});

		LinearLayout layout = (LinearLayout) findViewById(R.id.layout);
		chart = new LineChart(this);
		List<Float> domain = generateDomein(NUM_OF_VALUES, 1.0f);
		final ChartData data = new ChartData(domain);
		List<Float> s1 = generateValues(NUM_OF_VALUES, 1.0f);
		List<Float> s2 = generateValues(NUM_OF_VALUES, 1.0f);
		data.addSeries(new Series(Color.parseColor("#FFBB33"), s1));
		//data.addSeries(new Series(Color.parseColor("#99CC00"), s2));
		List<Float> yRules = generateAxis(0, 100, 10.0f);
		Axis yAxis = new Axis();
		yAxis.setValues(yRules);
		data.setYAxis(yAxis);
		List<Float> xRules = generateAxis(0, 100, 20.0f);
		Axis xAxis = new Axis();
		xAxis.setValues(xRules);
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

	private List<Float> generateDomein(int num, float step) {
		List<Float> result = new ArrayList<Float>();
		for (float f = 0.0f; f < num; f += step) {
			result.add(f);
		}
		return result;
	}

	private List<Float> generateValues(int num, float step) {
		List<Float> result = new ArrayList<Float>();
		for (float f = 0.0f; f < num; f += step) {
			result.add((float) Math.random() * 100.0f);
		}
		return result;
	}

	private List<Float> generateAxis(float min, float max, float step) {
		List<Float> result = new ArrayList<Float>();
		for (float f = min; f <= max; f += step) {
			result.add(f);
		}
		return result;
	}
}
