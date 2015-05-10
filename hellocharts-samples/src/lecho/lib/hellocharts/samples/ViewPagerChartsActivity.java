package lecho.lib.hellocharts.samples;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.List;

import lecho.lib.hellocharts.gesture.ContainerScrollType;
import lecho.lib.hellocharts.gesture.ZoomType;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.BubbleChartData;
import lecho.lib.hellocharts.model.BubbleValue;
import lecho.lib.hellocharts.model.Column;
import lecho.lib.hellocharts.model.ColumnChartData;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PieChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.SliceValue;
import lecho.lib.hellocharts.model.SubcolumnValue;
import lecho.lib.hellocharts.model.Viewport;
import lecho.lib.hellocharts.util.ChartUtils;
import lecho.lib.hellocharts.view.BubbleChartView;
import lecho.lib.hellocharts.view.ColumnChartView;
import lecho.lib.hellocharts.view.LineChartView;
import lecho.lib.hellocharts.view.PieChartView;
import lecho.lib.hellocharts.view.PreviewLineChartView;

public class ViewPagerChartsActivity extends ActionBarActivity implements ActionBar.TabListener {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every loaded fragment in memory. If this becomes too
     * memory intensive, it may be best to switch to a {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_pager_charts);

        // Set up the action bar.
        final ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        // When swiping between different sections, select the corresponding
        // tab. We can also use ActionBar.Tab#select() to do this if we have
        // a reference to the Tab.
        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                actionBar.setSelectedNavigationItem(position);
            }
        });

        // For each of the sections in the app, add a tab to the action bar.
        for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
            // Create a tab with text corresponding to the page title defined by
            // the adapter. Also specify this Activity object, which implements
            // the TabListener interface, as the callback (listener) for when
            // this tab is selected.
            actionBar.addTab(actionBar.newTab().setText(mSectionsPagerAdapter.getPageTitle(i)).setTabListener(this));
        }
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        // When the given tab is selected, switch to the corresponding page in
        // the ViewPager.
        mViewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        public PlaceholderFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_view_pager_charts, container, false);
            RelativeLayout layout = (RelativeLayout) rootView;
            int sectionNum = getArguments().getInt(ARG_SECTION_NUMBER);
            switch (sectionNum) {
                case 1:
                    LineChartView lineChartView = new LineChartView(getActivity());
                    lineChartView.setLineChartData(generateLineChartData());
                    lineChartView.setZoomType(ZoomType.HORIZONTAL);

                    /** Note: Chart is within ViewPager so enable container scroll mode. **/
                    lineChartView.setContainerScrollEnabled(true, ContainerScrollType.HORIZONTAL);

                    layout.addView(lineChartView);
                    break;
                case 2:
                    ColumnChartView columnChartView = new ColumnChartView(getActivity());
                    columnChartView.setColumnChartData(generateColumnChartData());
                    columnChartView.setZoomType(ZoomType.HORIZONTAL);

                    /** Note: Chart is within ViewPager so enable container scroll mode. **/
                    columnChartView.setContainerScrollEnabled(true, ContainerScrollType.HORIZONTAL);

                    layout.addView(columnChartView);
                    break;
                case 3:
                    BubbleChartView bubbleChartView = new BubbleChartView(getActivity());
                    bubbleChartView.setBubbleChartData(generateBubbleChartData());
                    bubbleChartView.setZoomType(ZoomType.HORIZONTAL_AND_VERTICAL);

                    /** Note: Chart is within ViewPager so enable container scroll mode. **/
                    bubbleChartView.setContainerScrollEnabled(true, ContainerScrollType.HORIZONTAL);

                    layout.addView(bubbleChartView);
                    break;
                case 4:
                    PreviewLineChartView previewLineChartView = new PreviewLineChartView(getActivity());
                    previewLineChartView.setLineChartData(generatePreviewLineChartData());

                    /** Note: Chart is within ViewPager so enable container scroll mode. **/
                    previewLineChartView.setContainerScrollEnabled(true, ContainerScrollType.HORIZONTAL);

                    Viewport tempViewport = new Viewport(previewLineChartView.getMaximumViewport());
                    float dx = tempViewport.width() / 6;
                    tempViewport.inset(dx, 0);
                    previewLineChartView.setCurrentViewport(tempViewport);
                    previewLineChartView.setZoomType(ZoomType.HORIZONTAL);

                    layout.addView(previewLineChartView);
                    break;
                case 5:
                    PieChartView pieChartView = new PieChartView(getActivity());
                    pieChartView.setPieChartData(generatePieChartData());

                    /** Note: Chart is within ViewPager so enable container scroll mode. **/
                    pieChartView.setContainerScrollEnabled(true, ContainerScrollType.HORIZONTAL);

                    layout.addView(pieChartView);
                    break;
            }

            return rootView;
        }

        private LineChartData generateLineChartData() {
            int numValues = 20;

            List<PointValue> values = new ArrayList<PointValue>();
            for (int i = 0; i < numValues; ++i) {
                values.add(new PointValue(i, (float) Math.random() * 100f));
            }

            Line line = new Line(values);
            line.setColor(ChartUtils.COLOR_GREEN);

            List<Line> lines = new ArrayList<Line>();
            lines.add(line);

            LineChartData data = new LineChartData(lines);
            data.setAxisXBottom(new Axis().setName("Axis X"));
            data.setAxisYLeft(new Axis().setName("Axis Y").setHasLines(true));
            return data;

        }

        private ColumnChartData generateColumnChartData() {
            int numSubcolumns = 1;
            int numColumns = 12;
            // Column can have many subcolumns, here by default I use 1 subcolumn in each of 8 columns.
            List<Column> columns = new ArrayList<Column>();
            List<SubcolumnValue> values;
            for (int i = 0; i < numColumns; ++i) {

                values = new ArrayList<SubcolumnValue>();
                for (int j = 0; j < numSubcolumns; ++j) {
                    values.add(new SubcolumnValue((float) Math.random() * 50f + 5, ChartUtils.pickColor()));
                }

                columns.add(new Column(values));
            }

            ColumnChartData data = new ColumnChartData(columns);

            data.setAxisXBottom(new Axis().setName("Axis X"));
            data.setAxisYLeft(new Axis().setName("Axis Y").setHasLines(true));
            return data;

        }

        private BubbleChartData generateBubbleChartData() {
            int numBubbles = 10;

            List<BubbleValue> values = new ArrayList<BubbleValue>();
            for (int i = 0; i < numBubbles; ++i) {
                BubbleValue value = new BubbleValue(i, (float) Math.random() * 100, (float) Math.random() * 1000);
                value.setColor(ChartUtils.pickColor());
                values.add(value);
            }

            BubbleChartData data = new BubbleChartData(values);

            data.setAxisXBottom(new Axis().setName("Axis X"));
            data.setAxisYLeft(new Axis().setName("Axis Y").setHasLines(true));
            return data;
        }

        private LineChartData generatePreviewLineChartData() {
            int numValues = 50;

            List<PointValue> values = new ArrayList<PointValue>();
            for (int i = 0; i < numValues; ++i) {
                values.add(new PointValue(i, (float) Math.random() * 100f));
            }

            Line line = new Line(values);
            line.setColor(ChartUtils.DEFAULT_DARKEN_COLOR);
            line.setHasPoints(false);// too many values so don't draw points.

            List<Line> lines = new ArrayList<Line>();
            lines.add(line);

            LineChartData data = new LineChartData(lines);
            data.setAxisXBottom(new Axis());
            data.setAxisYLeft(new Axis().setHasLines(true));

            return data;

        }

        private PieChartData generatePieChartData() {
            int numValues = 6;

            List<SliceValue> values = new ArrayList<SliceValue>();
            for (int i = 0; i < numValues; ++i) {
                values.add(new SliceValue((float) Math.random() * 30 + 15, ChartUtils.pickColor()));
            }

            PieChartData data = new PieChartData(values);
            return data;
        }

    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return PlaceholderFragment.newInstance(position + 1);
        }

        @Override
        public int getCount() {
            return 5;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "LineChart";
                case 1:
                    return "ColumnChart";
                case 2:
                    return "BubbleChart";
                case 3:
                    return "PreviewLineChart";
                case 4:
                    return "PieChart";
            }
            return null;
        }
    }

}
