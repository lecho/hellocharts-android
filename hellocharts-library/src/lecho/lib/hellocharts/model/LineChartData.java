package lecho.lib.hellocharts.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class LineChartData {

	private final List<Float> domain;
	private final List<ValueSeries> series = new ArrayList<ValueSeries>();

	public LineChartData(List<Float> domain) {
		this.domain = new ArrayList<Float>(domain);
	}

	public void addSeries(int color, List<Float> values) {
		this.series.add(new ValueSeries(color, values));
	}

	public List<Float> getDomain() {
		return Collections.unmodifiableList(domain);
	}

	public List<ValueSeries> getSeries() {
		return Collections.unmodifiableList(series);
	}

}
