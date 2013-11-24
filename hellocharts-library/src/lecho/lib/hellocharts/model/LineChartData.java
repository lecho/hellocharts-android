package lecho.lib.hellocharts.model;

import java.util.ArrayList;
import java.util.List;

public class LineChartData {

	public final List<Float> domain;
	public final List<LineSeries> series = new ArrayList<LineSeries>();

	public LineChartData(List<Float> domain) {
		this.domain = domain;
	}

	public void addSeries(int color, List<Float> values) {
		this.series.add(new LineSeries(color, values));
	}

}
