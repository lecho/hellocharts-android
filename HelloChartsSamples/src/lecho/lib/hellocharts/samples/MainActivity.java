package lecho.lib.hellocharts.samples;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class MainActivity extends ActionBarActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		if (savedInstanceState == null) {
			getSupportFragmentManager().beginTransaction().add(R.id.container, new PlaceholderFragment()).commit();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
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
	public static class PlaceholderFragment extends Fragment implements OnItemClickListener {

		private ListView listView;
		private ChartSamplesAdapter adapter;

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_main, container, false);
			listView = (ListView) rootView.findViewById(android.R.id.list);
			adapter = new ChartSamplesAdapter(getActivity(), 0, generateSamplesDescriptions());
			listView.setAdapter(adapter);
			listView.setOnItemClickListener(this);
			return rootView;
		}

		@Override
		public void onItemClick(AdapterView<?> adapter, View view, int position, long id) {
			Intent intent;

			switch (position) {
			case 0:
				// Line Chart;
				intent = new Intent(getActivity(), LineChartActivity.class);
				startActivity(intent);
				break;
			case 1:
				// Column Chart;
				intent = new Intent(getActivity(), ColumnChartActivity.class);
				startActivity(intent);
				break;
			case 2:
				// Pie Chart;
				intent = new Intent(getActivity(), PieChartActivity.class);
				startActivity(intent);
				break;
			case 3:
				// Bubble Chart;
				intent = new Intent(getActivity(), BubbleChartActivity.class);
				startActivity(intent);
				break;
			case 4:
				// Preview Line Chart;
				intent = new Intent(getActivity(), PreviewLineChartActivity.class);
				startActivity(intent);
				break;
			case 5:
				// Preview Column Chart;
				intent = new Intent(getActivity(), PreviewColumnChartActivity.class);
				startActivity(intent);
				break;
			case 6:
				// Combo Chart;
				intent = new Intent(getActivity(), ComboLineColumnChartActivity.class);
				startActivity(intent);
				break;
			case 7:
				// Line Column Dependency;
				intent = new Intent(getActivity(), LineColumnDependencyActivity.class);
				startActivity(intent);
				break;
			case 8:
				// Tempo line chart;
				intent = new Intent(getActivity(), TempoChartActivity.class);
				startActivity(intent);
				break;
			case 9:
				// Speed line chart;
				intent = new Intent(getActivity(), SpeedChartActivity.class);
				startActivity(intent);
				break;
			case 10:
				// Good Bad filled line chart;
				intent = new Intent(getActivity(), GoodBadChartActivity.class);
				startActivity(intent);
				break;
			case 11:
				// Good Bad filled line chart;
				intent = new Intent(getActivity(), ViewPagerChartsActivity.class);
				startActivity(intent);
				break;
			default:
				break;
			}
		}

		private List<ChartSampleDescription> generateSamplesDescriptions() {
			List<ChartSampleDescription> list = new ArrayList<MainActivity.ChartSampleDescription>();

			list.add(new ChartSampleDescription("Line Chart", ""));
			list.add(new ChartSampleDescription("Column Chart", ""));
			list.add(new ChartSampleDescription("Pie Chart", ""));
			list.add(new ChartSampleDescription("Bubble Chart", ""));
			list.add(new ChartSampleDescription("Preview Line Chart",
					"Control Line Chart viewport with another Line Chart "));
			list.add(new ChartSampleDescription("Preview Column Chart",
					"Control Column Chart viewport with another Column Chart"));
			list.add(new ChartSampleDescription("Combo Line/Column Chart", "Combo chart with lines and columns"));
			list.add(new ChartSampleDescription("Line/Column Chart Dependency",
					"LineChart responds(with animation) to ColumnChart value selection"));
			list.add(new ChartSampleDescription("Tempo Chart Sample", "Example of reverted axis with time format mm:ss"));
			list.add(new ChartSampleDescription("Speed Chart Sample", "Exapmle of chart with axes inside chart area"));
			list.add(new ChartSampleDescription("Good/Bad Chart Sample", "Example of filled area line chart"));
			list.add(new ChartSampleDescription("ViewPager with Chart Sample",
					"Charts within ViewPager can be zoomed and scrolled"));

			return list;
		}
	}

	public static class ChartSamplesAdapter extends ArrayAdapter<ChartSampleDescription> {

		public ChartSamplesAdapter(Context context, int resource, List<ChartSampleDescription> objects) {
			super(context, resource, objects);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;

			if (convertView == null) {
				convertView = View.inflate(getContext(), R.layout.list_item_sample, null);

				holder = new ViewHolder();
				holder.text1 = (TextView) convertView.findViewById(R.id.text1);
				holder.text2 = (TextView) convertView.findViewById(R.id.text2);

				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			ChartSampleDescription item = getItem(position);
			holder.text1.setText(item.text1);
			holder.text2.setText(item.text2);

			return convertView;
		}

		private class ViewHolder {

			TextView text1;
			TextView text2;
		}

	}

	public static class ChartSampleDescription {
		String text1;
		String text2;

		public ChartSampleDescription(String text1, String text2) {
			this.text1 = text1;
			this.text2 = text2;
		}
	}

}
