package lecho.lib.hellocharts.model;

import java.util.ArrayList;
import java.util.List;

public class ChartData {

	private final List<Float> domain;
	private final List<Series> valuesSeries = new ArrayList<Series>();
	private Axis yAxis;
	private Axis xAxis;

	public ChartData(List<Float> domain) {
		this.domain = domain;
	}

	public List<Float> getDomain() {
		return domain;
	}

	public Series getSeries(int index) {
		return valuesSeries.get(index);
	}

	public List<Series> getAllSeries() {
		return valuesSeries;
	}

	public void addSeries(Series series) {
		this.valuesSeries.add(series);
	}

	public void setYAxis(Axis yAxis) {
		this.yAxis = yAxis;
	}

	public Axis getYAxis() {
		return yAxis;
	}

	public void setXAxis(Axis xAxis) {
		this.xAxis = xAxis;
	}

	public Axis getXAxis() {
		return xAxis;
	}

}
