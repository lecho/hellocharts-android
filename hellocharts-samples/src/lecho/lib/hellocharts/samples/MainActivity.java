package lecho.lib.hellocharts.samples;

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
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import lecho.lib.hellocharts.view.AbstractChartView;
import lecho.lib.hellocharts.view.BubbleChartView;
import lecho.lib.hellocharts.view.ColumnChartView;
import lecho.lib.hellocharts.view.LineChartView;
import lecho.lib.hellocharts.view.PieChartView;
import lecho.lib.hellocharts.view.PreviewColumnChartView;
import lecho.lib.hellocharts.view.PreviewLineChartView;

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
        if (id == R.id.action_about) {
            Intent intent = new Intent(this, AboutActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public enum ChartType {
        LINE_CHART, COLUMN_CHART, PIE_CHART, BUBBLE_CHART, PREVIEW_LINE_CHART, PREVIEW_COLUMN_CHART, OTHER
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

            list.add(new ChartSampleDescription("Line Chart", "", ChartType.LINE_CHART));
            list.add(new ChartSampleDescription("Column Chart", "", ChartType.COLUMN_CHART));
            list.add(new ChartSampleDescription("Pie Chart", "", ChartType.PIE_CHART));
            list.add(new ChartSampleDescription("Bubble Chart", "", ChartType.BUBBLE_CHART));
            list.add(new ChartSampleDescription("Preview Line Chart",
                    "Control line chart viewport with another line chart.", ChartType.PREVIEW_LINE_CHART));
            list.add(new ChartSampleDescription("Preview Column Chart",
                    "Control column chart viewport with another column chart.", ChartType.PREVIEW_COLUMN_CHART));
            list.add(new ChartSampleDescription("Combo Line/Column Chart", "Combo chart with lines and columns.",
                    ChartType.OTHER));
            list.add(new ChartSampleDescription("Line/Column Chart Dependency",
                    "LineChart responds(with animation) to column chart value selection.", ChartType.OTHER));
            list.add(new ChartSampleDescription(
                    "Tempo Chart",
                    "Presents tempo and height values on a signle chart. Example of multiple axes and reverted Y axis" +
                            " with time format [mm:ss].",
                    ChartType.OTHER));
            list.add(new ChartSampleDescription("Speed Chart",
                    "Presents speed and height values on a signle chart. Exapmle of multiple axes inside chart area.",
                    ChartType.OTHER));
            list.add(new ChartSampleDescription("Good/Bad Chart",
                    "Example of filled area line chart with custom labels", ChartType.OTHER));
            list.add(new ChartSampleDescription("ViewPager with Charts",
                    "Interactive charts within ViewPager. Each chart can be zoom/scroll except pie chart.",
                    ChartType.OTHER));

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
                holder.chartLayout = (FrameLayout) convertView.findViewById(R.id.chart_layout);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            ChartSampleDescription item = getItem(position);

            holder.chartLayout.setVisibility(View.VISIBLE);
            holder.chartLayout.removeAllViews();
            AbstractChartView chart;
            switch (item.chartType) {
                case LINE_CHART:
                    chart = new LineChartView(getContext());
                    holder.chartLayout.addView(chart);
                    break;
                case COLUMN_CHART:
                    chart = new ColumnChartView(getContext());
                    holder.chartLayout.addView(chart);
                    break;
                case PIE_CHART:
                    chart = new PieChartView(getContext());
                    holder.chartLayout.addView(chart);
                    break;
                case BUBBLE_CHART:
                    chart = new BubbleChartView(getContext());
                    holder.chartLayout.addView(chart);
                    break;
                case PREVIEW_LINE_CHART:
                    chart = new PreviewLineChartView(getContext());
                    holder.chartLayout.addView(chart);
                    break;
                case PREVIEW_COLUMN_CHART:
                    chart = new PreviewColumnChartView(getContext());
                    holder.chartLayout.addView(chart);
                    break;
                default:
                    chart = null;
                    holder.chartLayout.setVisibility(View.GONE);
                    break;
            }

            if (null != chart) {
                chart.setInteractive(false);// Disable touch handling for chart on the ListView.
            }
            holder.text1.setText(item.text1);
            holder.text2.setText(item.text2);

            return convertView;
        }

        private class ViewHolder {

            TextView text1;
            TextView text2;
            FrameLayout chartLayout;
        }

    }

    public static class ChartSampleDescription {
        String text1;
        String text2;
        ChartType chartType;

        public ChartSampleDescription(String text1, String text2, ChartType chartType) {
            this.text1 = text1;
            this.text2 = text2;
            this.chartType = chartType;
        }
    }

}
