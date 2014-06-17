package lecho.lib.hellocharts.model;

import java.util.Collections;
import java.util.List;

import android.util.Log;

public class BarChartData extends AbstractChartData {

	private List<Bar> mBars = Collections.emptyList();
	private boolean mIsStacked = false;

	public List<Bar> getBars() {
		return mBars;
	}

	public void setBars(List<Bar> bars) {
		this.mBars = bars;
	}

	public boolean isStacked() {
		return mIsStacked;
	}

	public void setStacked(boolean isStacked) {
		this.mIsStacked = isStacked;
	}

	@Override
	public void calculateBoundaries() {
		if (mManualBoundaries) {
			return;
		}
		mBoundaries.set(-0.5f, 0, mBars.size() - 0.5f, 0);
		if (mIsStacked) {
			calculateBoundariesStacked();
		} else {
			calculateBoundariesDefault();
		}
	}

	public void calculateBoundariesDefault() {
		for (Bar bar : mBars) {
			for (AnimatedValueWithColor animatedValue : bar.animatedValues) {
				if (animatedValue.value >= 0 && animatedValue.value > mBoundaries.top) {
					mBoundaries.top = animatedValue.value;
				}
				if (animatedValue.value < 0 && animatedValue.value < mBoundaries.bottom) {
					mBoundaries.bottom = animatedValue.value;
				}
			}
		}
	}

	public void calculateBoundariesStacked() {
		for (Bar bar : mBars) {
			float sumPositive = 0;
			float sumNegative = 0;
			for (AnimatedValueWithColor animatedValue : bar.animatedValues) {
				if (animatedValue.value >= 0) {
					sumPositive += animatedValue.value;
				} else {
					sumNegative += animatedValue.value;
				}
			}
			if (sumPositive > mBoundaries.top) {
				mBoundaries.top = sumPositive;
			}
			if (sumNegative < mBoundaries.bottom) {
				mBoundaries.bottom = sumNegative;
			}
		}
		Log.d("dupa", "boundaries: " + mBoundaries.toString());
	}
}
