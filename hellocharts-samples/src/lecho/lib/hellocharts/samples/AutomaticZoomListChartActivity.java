package lecho.lib.hellocharts.samples;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import lecho.lib.hellocharts.gesture.ContainerScrollType;
import lecho.lib.hellocharts.gesture.ZoomType;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.ValueShape;
import lecho.lib.hellocharts.model.Viewport;
import lecho.lib.hellocharts.renderer.LineChartRenderer;
import lecho.lib.hellocharts.view.AbstractChartView;
import lecho.lib.hellocharts.view.LineChartView;

public class AutomaticZoomListChartActivity extends ActionBarActivity {
	private static final float CHART_VIEWPORT_HEIGHT_PERCENTAGE_CORRECTION = .2f;
	private static final float CHART_VIEWPORT_WIDTH_PERCENTAGE_CORRECTION = .04f;
	public static final String ONE_CHAR_STRING = "W";

	private int mLineChartColor;
	private float mOneCharWidth = -1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_automatic_zoom_list_chart);

		mLineChartColor = getResources().getColor(R.color.holo_blue_dark);

		ListView mListView = (ListView)findViewById(R.id.list);
		MyAdapter adapter = new MyAdapter(this, 0, new Integer[] {1, 2,3, 4, 5, 6, 7} );
		mListView.setAdapter(adapter);

	}

	public class MyAdapter extends ArrayAdapter<Integer> {

		public MyAdapter(Context context, int resource, Integer[] objects) {
			super(context, resource, objects);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			final ViewHolder holder;

			if (convertView == null) {
				convertView = View.inflate(getContext(), R.layout.list_item_chart, null);
				holder = new ViewHolder();

				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			holder.chart = (LineChartView) convertView.findViewById(R.id.chart);

			Random random = new Random();
			List<AxisValue> axisValues = new ArrayList<>();
			List<PointValue> values = new ArrayList<>();
			float minValue = Float.MAX_VALUE;
			float maxValue = Float.MIN_VALUE;
			for (int j = 0; j < 20; ++j) {
				float value = random.nextFloat() * 20;
				if (position % 2 == 0) {
					axisValues.add(new AxisValue(j).setLabel("Test long label " + Float.toString(j)));
				} else {
					axisValues.add(new AxisValue(j).setLabel(Float.toString(j)));
				}
				minValue = Math.min(minValue, value);
				maxValue = Math.max(maxValue, value);
				values.add(new PointValue(j, value));
			}

			bindChart(holder.chart, new ReportData(values, axisValues, minValue, maxValue));

			return convertView;
		}

		// Sets the zoom based on the width of the labels
		private void calculateZoom(LineChartView chart) {
			float width = chart.getChartComputator().getContentRectMinusAllMargins().width();
			Axis axisXBottom = chart.getChartData().getAxisXBottom();
			if (axisXBottom != null) {
				List<AxisValue> values = axisXBottom.getValues();
				int amountOfValues = values.size();
				float maxAxisCharacters = 0;
				int charWidth = 0;
				for (int i = 0; i < values.size(); i++) {
					AxisValue axisValue = values.get(i);
					float length = axisValue.getLabelAsChars().length;
					if (length >= maxAxisCharacters) {
						maxAxisCharacters = length;
						Rect bounds = new Rect();
						Paint textPaint = chart.getChartRenderer().getLabelPaint();
						String maxAxisValue = new String(axisValue.getLabelAsChars());
						textPaint.getTextBounds(maxAxisValue, 0, maxAxisValue.length(), bounds);
						charWidth = Math.max(bounds.width(), charWidth);
					}
				}

				if (mOneCharWidth <= 0) {
					Rect bounds = new Rect();
					Paint textPaint = chart.getChartRenderer().getLabelPaint();
					textPaint.getTextBounds(ONE_CHAR_STRING, 0, ONE_CHAR_STRING.length(), bounds);
					mOneCharWidth = bounds.width();
				}

				float currentWidthPerItem = width / amountOfValues;
				float requiredWidthPerItem = charWidth + mOneCharWidth;
				float calculatedZoom = (requiredWidthPerItem / currentWidthPerItem);
				chart.setZoomLevel(chart.getCurrentViewport().right - 0.1f, chart.getCurrentViewport().centerY(), calculatedZoom);
			}
		}

		private void bindChart(final LineChartView mChart, ReportData reportData) {
			Line line = new Line(reportData.mValues);
			line.setColor(mLineChartColor);
			line.setShape(ValueShape.CIRCLE);
			line.setCubic(false);
			line.setFilled(false);
			line.setHasLabels(true);
			line.setHasLabelsOnlyForSelected(false);
			line.setHasLines(true);
			line.setHasPoints(true);

			List<Line> lineList = new ArrayList<>();
			lineList.add(line);
			LineChartData data = new LineChartData(lineList);

			Axis axisX = new Axis(reportData.mAxisValues)
					.setHasSeparationLine(true)
					.setMaxLabelChars(3)
					.setHasLines(true);

			final Viewport v = new Viewport(mChart.getMaximumViewport());
			float deltaY = (reportData.mMaxValue - reportData.mMinValue) * CHART_VIEWPORT_HEIGHT_PERCENTAGE_CORRECTION;
			float deltaX = reportData.mAxisValues.size() * CHART_VIEWPORT_WIDTH_PERCENTAGE_CORRECTION;

			float topValue = (float) Math.ceil(reportData.mMaxValue + deltaY);
			Axis axisY = new Axis()
					.setHasLines(false)
					.setHasTiltedLabels(false);

			data.setAxisXBottom(axisX);
			data.setAxisYLeft(axisY);
			data.setBaseValue(Float.NEGATIVE_INFINITY);
			data.setValueLabelBackgroundEnabled(false);
			data.setValueLabelsTextColor(mLineChartColor);

			mChart.setOnSizeChangeListener(new AbstractChartView.SizeChangeListener() {
				@Override
				public void onSizeChange() {
					calculateZoom(mChart);
				}
			});

			mChart.setLineChartData(data);
			mChart.setValueTouchEnabled(false);
			mChart.setZoomType(ZoomType.HORIZONTAL);
			mChart.setVerticalFadingEdgeEnabled(true);
			mChart.setHorizontalFadingEdgeEnabled(true);
			mChart.setContainerScrollEnabled(true, ContainerScrollType.VERTICAL);
			mChart.setViewportCalculationEnabled(false);
			LineChartRenderer chartRenderer = (LineChartRenderer) mChart.getChartRenderer();
			chartRenderer.setIsLabelAutopositionEnabled(false);
			chartRenderer.setDrawPointOnTopOfAxis(false);


			v.bottom = (float) Math.floor(reportData.mMinValue - deltaY);
			v.top = topValue;
			v.left = -deltaX;
			v.right = reportData.mAxisValues.size() - 1 + deltaX;

			mChart.setMaximumViewport(v);
			mChart.setCurrentViewport(v);
			mChart.setZoomEnabled(false);
			calculateZoom(mChart);
			mChart.requestLayout();

		}

	}

	private static class ReportData {
		private final List<PointValue> mValues;
		private final List<AxisValue> mAxisValues;
		private final float mMinValue;
		private final float mMaxValue;

		private ReportData(List<PointValue> values, List<AxisValue> axisValues, float minValue, float maxValue) {
			mValues = values;
			mAxisValues = axisValues;
			mMinValue = minValue;
			mMaxValue = maxValue;
		}
	}

	private class ViewHolder {
		public LineChartView chart;
	}
}