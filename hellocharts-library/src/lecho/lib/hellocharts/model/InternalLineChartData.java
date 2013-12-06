package lecho.lib.hellocharts.model;

import java.util.ArrayList;
import java.util.List;

public class InternalLineChartData {

	private final List<Float> mDomain;
	private final List<InternalSeries> mInternalsSeries = new ArrayList<InternalSeries>();
	private float mMinXValue = Float.MAX_VALUE;
	private float mMaxXValue = Float.MIN_VALUE;
	private float mMinYValue = Float.MAX_VALUE;
	private float mMaxYValue = Float.MIN_VALUE;

	public InternalLineChartData(List<Float> domain) {
		this.mDomain = new ArrayList<Float>(domain);
	}

	public static InternalLineChartData createFromRawDara(ChartData rawData) {
		InternalLineChartData internalData = new InternalLineChartData(rawData.getDomain());
		for (Series series : rawData.getAllSeries()) {
			internalData.addSeries(series);
		}
		return internalData;
	}

	public void addSeries(Series series) {
		List<AnimatedValue> values = new ArrayList<AnimatedValue>(series.getValues().size());
		for (Float f : series.getValues()) {
			values.add(new AnimatedValue(f, f));
		}
		mInternalsSeries.add(new InternalSeries(series.getColor(), values));
	}

	public void updateSeries(int seriesIndex, List<Float> newValues) {
		if (seriesIndex < 0 || seriesIndex > mInternalsSeries.size()) {
			throw new IllegalArgumentException("Invalid sereis index!");
		}
		InternalSeries internalSeries = mInternalsSeries.get(seriesIndex);
		final int domainSize = internalSeries.values.size();
		if (newValues.size() != domainSize) {
			throw new IllegalArgumentException("Series and domain sizes differs!");
		}
		for (int i = 0; i < domainSize; ++i) {
			AnimatedValue value = internalSeries.values.get(i);
			value.position = newValues.get(i);
			value.targetPosition = value.position;
		}
	}

	public void updateSeriesTargetPositions(int seriesIndex, List<Float> newValues) {
		if (seriesIndex < 0 || seriesIndex > mInternalsSeries.size()) {
			throw new IllegalArgumentException("Invalid sereis index!");
		}
		InternalSeries internalSeries = mInternalsSeries.get(seriesIndex);
		final int domainSize = mDomain.size();
		if (newValues.size() != domainSize) {
			throw new IllegalArgumentException("Series sizes differs!");
		}
		for (int i = 0; i < domainSize; ++i) {
			internalSeries.values.get(i).targetPosition = newValues.get(i);
		}
	}

	public void calculateRanges() {
		for (Float value : mDomain) {
			if (value < mMinXValue) {
				mMinXValue = value;
			} else if (value > mMaxXValue) {
				mMaxXValue = value;
			}
		}
		for (InternalSeries internalSeries : mInternalsSeries) {
			for (AnimatedValue value : internalSeries.values) {
				if (value.position < mMinYValue) {
					mMinYValue = value.position;
				} else if (value.position > mMaxYValue) {
					mMaxYValue = value.position;
				}
			}
		}
	}

	public List<Float> getDomain() {
		return mDomain;
	}

	public List<InternalSeries> getInternalsSeries() {
		return mInternalsSeries;
	}

	public float getMinXValue() {
		return mMinXValue;
	}

	public float getMaxXValue() {
		return mMaxXValue;
	}

	public float getMinYValue() {
		return mMinYValue;
	}

	public float getMaxYValue() {
		return mMaxYValue;
	}

	public ChartData getRawData() {
		ChartData rawData = new ChartData(mDomain);
		for (InternalSeries internalSeries : mInternalsSeries) {
			List<Float> values = new ArrayList<Float>(mDomain.size());
			for (AnimatedValue value : internalSeries.values) {
				values.add(value.position);
			}
			rawData.addSeries(new Series(internalSeries.color, values));
		}
		return rawData;
	}

}
