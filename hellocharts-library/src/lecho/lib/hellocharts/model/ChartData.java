package lecho.lib.hellocharts.model;

import java.util.ArrayList;
import java.util.List;

public class ChartData {

	private final List<Float> mDomain;
	private final List<Series> mValuesSeries = new ArrayList<Series>();
	private List<Float> mYRules;

	public ChartData(List<Float> domain) {
		this.mDomain = domain;
	}

	public List<Float> getDomain() {
		return mDomain;
	}

	public Series getSeries(int index) {
		return mValuesSeries.get(index);
	}

	public List<Series> getAllSeries() {
		return mValuesSeries;
	}

	public void addSeries(Series series) {
		mValuesSeries.add(series);
	}

	public void setYRules(List<Float> yRules) {
		mYRules = yRules;
	}

	public List<Float> getYRules() {
		return mYRules;
	}

}
