package lecho.lib.hellocharts.view;

import java.util.ArrayList;
import java.util.List;

import lecho.lib.hellocharts.LineChartDataProvider;
import lecho.lib.hellocharts.model.ChartData;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.LinePoint;
import lecho.lib.hellocharts.model.SelectedValue;
import lecho.lib.hellocharts.renderer.LineChartRenderer;
import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;

public class LineChartView extends AbstractChartView implements LineChartDataProvider {
	private static final String TAG = "LineChartView";
	protected LineChartData data;
	protected LineChartOnValueTouchListener onValueTouchListener = new DummyOnValueTouchListener();

	public LineChartView(Context context) {
		this(context, null, 0);
	}

	public LineChartView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public LineChartView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		chartRenderer = new LineChartRenderer(context, this, this);
		setLineChartData(generateDummyData());
		initAttributes();
	}

	@SuppressLint("NewApi")
	private void initAttributes() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			setLayerType(LAYER_TYPE_SOFTWARE, null);
		}
	}

	public void setLineChartData(LineChartData data) {
		if (null == data) {
			this.data = generateDummyData();
		} else {
			this.data = data;
		}
		chartRenderer.initMaxViewport();
		chartRenderer.initCurrentViewport();
		chartRenderer.initDataAttributes();
		axesRenderer.initAxesAttributes();

		ViewCompat.postInvalidateOnAnimation(LineChartView.this);
	}

	@Override
	public LineChartData getLineChartData() {
		return data;
	}

	@Override
	public ChartData getChartData() {
		return data;
	}

	@Override
	public void callTouchListener(SelectedValue selectedValue) {
		LinePoint point = data.lines.get(selectedValue.firstIndex).getPoints().get(selectedValue.secondIndex);
		onValueTouchListener.onValueTouched(selectedValue.firstIndex, selectedValue.secondIndex, point);
	}

	public LineChartOnValueTouchListener getOnValueTouchListener() {
		return onValueTouchListener;
	}

	public void setOnValueTouchListener(LineChartOnValueTouchListener touchListener) {
		if (null == touchListener) {
			this.onValueTouchListener = new DummyOnValueTouchListener();
		} else {
			this.onValueTouchListener = touchListener;
		}
	}

	@Override
	public void animationDataUpdate(float scale) {
		for (Line line : data.lines) {
			for (LinePoint point : line.getPoints()) {
				point.update(scale);
			}
		}
		chartRenderer.initMaxViewport();
		chartRenderer.initCurrentViewport();
		ViewCompat.postInvalidateOnAnimation(this);
	}

	@Override
	public void animationDataFinished(boolean isFinishedSuccess) {
		for (Line line : data.lines) {
			for (LinePoint point : line.getPoints()) {
				point.finish(isFinishedSuccess);
			}
		}
		chartRenderer.initMaxViewport();
		chartRenderer.initCurrentViewport();
		ViewCompat.postInvalidateOnAnimation(this);
	}

	private LineChartData generateDummyData() {
		final int numValues = 4;
		LineChartData data = new LineChartData();
		List<LinePoint> values = new ArrayList<LinePoint>(numValues);
		for (int i = 1; i <= numValues; ++i) {
			values.add(new LinePoint(i, i));
		}
		Line line = new Line(values);
		List<Line> lines = new ArrayList<Line>(1);
		lines.add(line);
		data.lines = lines;
		return data;
	}

	public interface LineChartOnValueTouchListener {
		public void onValueTouched(int selectedLine, int selectedValue, LinePoint point);
	}

	private static class DummyOnValueTouchListener implements LineChartOnValueTouchListener {

		@Override
		public void onValueTouched(int selectedLine, int selectedValue, LinePoint point) {
			// do nothing
		}
	}
}
