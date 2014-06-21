package lecho.lib.hellocharts.model;

import java.util.ArrayList;
import java.util.List;

import android.util.Log;

public class BarChartData extends AbstractChartData {
	public static final float DEFAULT_FILL_RATIO = 0.75f;
	private List<Bar> bars = new ArrayList<Bar>();
	private boolean mIsStacked = false;
	private float fillRatio = DEFAULT_FILL_RATIO;

	@Override
	public void calculateBoundaries() {
		if (mManualBoundaries) {
			return;
		}
		mBoundaries.set(-0.5f, 0, bars.size() - 0.5f, 0);
		if (mIsStacked) {
			calculateBoundariesStacked();
		} else {
			calculateBoundariesDefault();
		}
	}

	public void calculateBoundariesDefault() {
		for (Bar bar : bars) {
			for (BarValue barValue : bar.getValues()) {
				if (barValue.getValue() >= 0 && barValue.getValue() > mBoundaries.top) {
					mBoundaries.top = barValue.getValue();
				}
				if (barValue.getValue() < 0 && barValue.getValue() < mBoundaries.bottom) {
					mBoundaries.bottom = barValue.getValue();
				}
			}
		}
	}

	public void calculateBoundariesStacked() {
		for (Bar bar : bars) {
			float sumPositive = 0;
			float sumNegative = 0;
			for (BarValue animatedValue : bar.getValues()) {
				if (animatedValue.getValue() >= 0) {
					sumPositive += animatedValue.getValue();
				} else {
					sumNegative += animatedValue.getValue();
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

	public List<Bar> getBars() {
		return bars;
	}

	public void setBars(List<Bar> bars) {
		if (null == bars) {
			this.bars = new ArrayList<Bar>();
		} else {
			this.bars = bars;
		}
	}

	public boolean isStacked() {
		return mIsStacked;
	}

	public void setStacked(boolean isStacked) {
		this.mIsStacked = isStacked;
	}

	public float getFillRatio() {
		return fillRatio;
	}

	public void setFillRatio(float fillRatio) {
		if (fillRatio < 0) {
			fillRatio = 0;
		}
		if (fillRatio > 1) {
			fillRatio = 1;
		}
		this.fillRatio = fillRatio;
	}
}
