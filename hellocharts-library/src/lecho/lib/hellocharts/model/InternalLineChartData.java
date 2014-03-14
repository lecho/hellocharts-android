package lecho.lib.hellocharts.model;

import java.util.ArrayList;
import java.util.List;

public class InternalLineChartData {

	private final List<Float> mDomain;
	private final List<InternalSeries> mInternalsSeries = new ArrayList<InternalSeries>();
	private Axis yAxis;
	private Axis xAxis;
	private float mMinXValue;
	private float mMaxXValue;
	private float mMinYValue;
	private float mMaxYValue;

	public InternalLineChartData(List<Float> domain) {
		this.mDomain = new ArrayList<Float>(domain);
	}

	public static InternalLineChartData createFromRawData(ChartData rawData) {
		InternalLineChartData internalData = new InternalLineChartData(rawData.getDomain());
		for (Series series : rawData.getAllSeries()) {
			internalData.addSeries(series);
		}
		internalData.yAxis = rawData.getYAxis();
		internalData.xAxis = rawData.getXAxis();
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
		// TODO: Optimize.
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
		mMinXValue = Float.MAX_VALUE;
		mMaxXValue = Float.MIN_VALUE;
		for (Float value : mDomain) {
			if (value < mMinXValue) {
				mMinXValue = value;
			}
			if (value > mMaxXValue) {
				mMaxXValue = value;
			}
		}
	}

	public void calculateYRanges() {
		mMinYValue = Float.MAX_VALUE;
		mMaxYValue = Float.MIN_VALUE;
		for (InternalSeries internalSeries : mInternalsSeries) {
			for (AnimatedValue value : internalSeries.getValues()) {
				if (value.getPosition() < mMinYValue) {
					mMinYValue = value.getPosition();
				}
				if (value.getPosition() > mMaxYValue) {
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

	public Axis getYAxis() {
		return yAxis;
	}

	public void setYAxis(Axis yAxis) {
		this.yAxis = yAxis;
	}

	public Axis getXAxis() {
		return xAxis;
	}

	public void setXAxis(Axis xAxis) {
		this.xAxis = xAxis;
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
