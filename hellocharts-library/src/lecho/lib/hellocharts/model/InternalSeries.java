package lecho.lib.hellocharts.model;

import java.util.List;

public class InternalSeries {
	private final int mColor;
	private final List<AnimatedValue> mValues;

	public InternalSeries(int color, List<AnimatedValue> values) {
		this.mColor = color;
		this.mValues = values;
	}

	public int getColor() {
		return mColor;
	}

	public List<AnimatedValue> getValues() {
		return mValues;
	}
}
