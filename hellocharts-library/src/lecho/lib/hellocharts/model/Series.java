package lecho.lib.hellocharts.model;

import java.util.List;

public class Series {

	private final int mColor;
	private final List<Float> mValues;

	public Series(int color, List<Float> values) {
		this.mColor = color;
		mValues = values;
	}

	public int getColor() {
		return mColor;
	}

	public List<Float> getValues() {
		return mValues;
	}

}
