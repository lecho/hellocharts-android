package lecho.lib.hellocharts.anim;

import lecho.lib.hellocharts.Chart;
import android.os.Handler;
import android.os.SystemClock;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;

public class ChartAnimatorV8 implements ChartAnimator {

	long mStart;
	final Chart mChart;
	final long mDuration;
	final Handler mHandler;
	final Interpolator mInterpolator = new LinearInterpolator();
	private final Runnable mRunnable = new Runnable() {

		@Override
		public void run() {
			long elapsed = SystemClock.uptimeMillis() - mStart;
			float dt = Math.min(mInterpolator.getInterpolation((float) elapsed / mDuration), 1);
			if (dt < 1.0) {
				mChart.animationDataUpdate(dt);
				mHandler.postDelayed(this, 16);
			} else {
				mChart.animationDataUpdate(1.0f);
			}

		}
	};

	public ChartAnimatorV8(final Chart chart) {
		this(chart, DEFAULT_ANIMATION_DURATION);
	}

	public ChartAnimatorV8(final Chart chart, final long duration) {
		mChart = chart;
		mDuration = duration;
		mHandler = new Handler();
	}

	@Override
	public void startAnimation() {
		mStart = SystemClock.uptimeMillis();
		mHandler.post(mRunnable);

	}

	@Override
	public void cancelAnimation() {
		mHandler.removeCallbacks(mRunnable);
	}

}
