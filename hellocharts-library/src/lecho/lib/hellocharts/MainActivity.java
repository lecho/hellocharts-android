package lecho.lib.hellocharts;

import java.util.ArrayList;
import java.util.List;

import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.Axis.AxisValue;
import lecho.lib.hellocharts.model.Bar;
import lecho.lib.hellocharts.model.BarChartData;
import lecho.lib.hellocharts.model.Point;
import lecho.lib.hellocharts.model.ValueWithColor;
import lecho.lib.hellocharts.utils.Utils;
import android.R.color;
import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

public class MainActivity extends Activity {
	private BarChart chart;
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
					List<Point> s1 = generatePoints(NUM_OF_VALUES, 1.0f);
					chart.animateSeries(0, s1);
				} else {
					List<Point> s2 = generatePoints(NUM_OF_VALUES, 1.0f);
					chart.animateSeries(0, s2);
				}

			}
		});

		LinearLayout layout = (LinearLayout) findViewById(R.id.layout);
		// chart = new LineChart(this);
		// final LineChartData data = new LineChartData();
		// List<Point> s1 = generateValues(NUM_OF_VALUES, 1.0f);
		// List<Point> s2 = generateValues(NUM_OF_VALUES, 1.0f);
		// Line l1 = new Line(s1);
		// l1.color = Color.parseColor("#FFBB33");
		// l1.hasPoints = true;
		// l1.isSmooth = true;
		// l1.hasValuesPopups = true;
		// Line l2 = new Line(s2);
		// l2.color = Color.parseColor("#99CC00");
		// l2.isSmooth = true;
		// l2.isFilled = true;
		// List<Line> lines = new ArrayList<Line>();
		// lines.add(l2);
		// lines.add(l1);
		// data.lines = lines;
		// Axis axisX = new Axis();
		// axisX.values = generateAxis(0.0f, 100.0f, 1.0f);
		// axisX.name = "Axis X";
		// axisX.textSize = 14;
		// axisX.color = Color.parseColor("#FFBB33");
		// data.setAxisX(axisX);
		//
		// Axis axisY = new Axis();
		// axisY.values = generateAxis(0.0f, 100.0f, 15.0f);
		// axisY.name = "Axis Y";
		// axisY.textSize = 14;
		// axisY.color = Color.parseColor("#99CC00");
		// data.setAxisY(axisY);
		// chart.setData(data);
		// chart.setBackgroundColor(Color.WHITE);
		// // chart.setPadding(10, 10, 10, 20);
		// chart.setOnPointClickListener(new OnPointClickListener() {
		//
		// @Override
		// public void onPointClick(int selectedSeriesIndex, int selectedValueIndex, float x, float y) {
		// Toast.makeText(
		// getApplicationContext(),
		// "Series index: " + selectedSeriesIndex + " Value index: " + selectedValueIndex
		// + " That gives point: x=" + x + ", y=" + y, Toast.LENGTH_SHORT).show();
		//
		// }
		// });
		// layout.addView(chart);

		chart = new BarChart(this);
		final BarChartData data = new BarChartData();
		// List<ValueWithColor> s1 = generateValues(1);
		// List<ValueWithColor> s2 = generateValues(1);
		// Bar l1 = new Bar(s1);
		// l1.hasValuesPopups = true;
		// Bar l2 = new Bar(s2);
		List<Bar> bars = new ArrayList<Bar>();
		for (int i = 0; i < 4; ++i) {
			bars.add(generateBar());
		}
		data.bars = bars;
		Axis axisX = new Axis();
		axisX.values = generateAxis(0.0f, 100, 1.0f);
		axisX.name = "Axis X";
		axisX.textSize = 14;
		axisX.color = Color.parseColor("#FFBB33");
		data.setAxisX(axisX);

		Axis axisY = new Axis();
		axisY.values = generateAxis(0.0f, 100.0f, 10.0f);
		axisY.name = "Axis Y";
		axisY.textSize = 14;
		axisY.color = Color.parseColor("#99CC00");
		data.setAxisY(axisY);
		data.isStacked = false;
		chart.setData(data);
		chart.setBackgroundColor(Color.WHITE);
		// // chart.setPadding(10, 10, 10, 20);
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

	private List<Point> generatePoints(int num, float step) {
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

	private List<ValueWithColor> generateValues(int num) {
		float x = 0.0f;
		int[] sign = new int[] { -1, 1 };
		List<ValueWithColor> result = new ArrayList<ValueWithColor>();
		for (int i = 0; i < num; ++i) {

			result.add(new ValueWithColor((float) Math.random() * 30.0f * sign[(int)Math.round(Math.random())], Utils.pickColor()));
		}
		return result;
	}

	private Bar generateBar() {
		List<ValueWithColor> s1 = generateValues(2);
		Bar l1 = new Bar(s1);
		l1.hasValuesPopups = false;
		return l1;
	}
}
