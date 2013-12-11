package lecho.lib.hellocharts;

import java.util.ArrayList;
import java.util.List;

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
					s1.add(600f);
					s1.add(250f);
					s1.add(700f);
					s1.add(600f);
					s1.add(800f);
					chart.animateSeries(0, s1);
				} else {
					List<Float> s2 = new ArrayList<Float>();
					s2.add(300f);
					s2.add(800f);
					s2.add(400f);
					s2.add(800f);
					s2.add(100f);
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
		s1.add(100f);
		s1.add(500f);
		s1.add(600f);
		s1.add(100f);
		s1.add(300f);
		List<Float> s2 = new ArrayList<Float>();
		s2.add(500f);
		s2.add(300f);
		s2.add(800f);
		s2.add(600f);
		s2.add(100f);
		data.addSeries(new Series(Color.parseColor("#FFBB33"), s1));
		data.addSeries(new Series(Color.parseColor("#99CC00"), s2));
		chart.setData(data);
		chart.setBackgroundColor(Color.LTGRAY);
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
