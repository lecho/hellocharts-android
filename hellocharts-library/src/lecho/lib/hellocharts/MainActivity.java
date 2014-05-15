package lecho.lib.hellocharts;

import java.util.ArrayList;
import java.util.List;

import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.Axis.AxisValue;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.Point;
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
	private static final int NUM_OF_VALUES = 5;

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
					List<Point> s1 = generateValues(NUM_OF_VALUES, 1.0f);
					chart.animateSeries(0, s1);
				} else {
					List<Point> s2 = generateValues(NUM_OF_VALUES, 1.0f);
					chart.animateSeries(0, s2);
				}

			}
		});

		LinearLayout layout = (LinearLayout) findViewById(R.id.layout);
		chart = new LineChart(this);
		final LineChartData data = new LineChartData();
		List<Point> s1 = generateValues(NUM_OF_VALUES, 1.0f);
		List<Point> s2 = generateValues(NUM_OF_VALUES, 1.0f);
		Line l1 = new Line(s1);
		l1.color = Color.parseColor("#FFBB33");
		l1.hasPoints = true;
		l1.isSmooth = true;
		l1.hasValuesPopups = true;
		Line l2 = new Line(s2);
		l2.color = Color.parseColor("#99CC00");
		l2.isSmooth = true;
		l2.isFilled = true;
		List<Line> lines = new ArrayList<Line>();
		lines.add(l2);
		lines.add(l1);
		data.lines = lines;
		Axis axisX = new Axis();
		axisX.values = generateAxis(0.0f, 100.0f, 1.0f);
		axisX.name = "Axis X";
		axisX.textSize = 14;
		axisX.color = Color.parseColor("#FFBB33");
		data.setAxisX(axisX);

		Axis axisY = new Axis();
		axisY.values = generateAxis(0.0f, 100.0f, 15.0f);
		axisY.name = "Axis Y";
		axisY.textSize = 14;
		axisY.color = Color.parseColor("#99CC00");
		data.setAxisY(axisY);
		chart.setData(data);
		chart.setBackgroundColor(Color.WHITE);
		// chart.setPadding(10, 10, 10, 20);
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

	private List<Point> generateValues(int num, float step) {
		float x = 0.0f;
		List<Point> result = new ArrayList<Point>();
		for (float f = 0.0f; f < num; f += step) {
			result.add(new Point(x, (float) Math.random() * 100.0f));
			x += step;
		}
		return result;
	}

	private List<AxisValue> generateAxis(float min, float max, float step) {
		List<AxisValue> result = new ArrayList<AxisValue>();
		for (float f = min; f <= max; f += step) {
			result.add(new AxisValue(f));
		}
		return result;
	}
}
