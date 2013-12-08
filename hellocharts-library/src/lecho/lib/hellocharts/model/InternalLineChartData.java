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
		final int domainSize = internalSeries.getValues().size();
		if (newValues.size() != domainSize) {
			throw new IllegalArgumentException("Series and domain sizes differs!");
		}
		for (int i = 0; i < domainSize; ++i) {
			AnimatedValue value = internalSeries.getValues().get(i);
			value.setPosition(newValues.get(i));
			value.setTargetPosition(value.getPosition());
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
			internalSeries.getValues().get(i).setTargetPosition(newValues.get(i));
		}
	}

	public void calculateRanges() {
		calculateXRanges();
		calculateYRanges();
	}

	public void calculateXRanges() {
		for (Float value : mDomain) {
			if (value < mMinXValue) {
				mMinXValue = value;
			} else if (value > mMaxXValue) {
				mMaxXValue = value;
			}
		}
	}

	public void calculateYRanges() {
		for (InternalSeries internalSeries : mInternalsSeries) {
			for (AnimatedValue value : internalSeries.getValues()) {
				if (value.getPosition() < mMinYValue) {
					mMinYValue = value.getPosition();
				} else if (value.getPosition() > mMaxYValue) {
					mMaxYValue = value.getPosition();
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
			for (AnimatedValue value : internalSeries.getValues()) {
				values.add(value.getPosition());
			}
			rawData.addSeries(new Series(internalSeries.getColor(), values));
		}
		return rawData;
	}

}
