package lecho.lib.hellocharts.anim;

import lecho.lib.hellocharts.LineChart;
import android.os.Handler;
import android.os.SystemClock;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;

public class ChartAnimatorV8 implements ChartAnimator {

	private final LineChart mChart;
	private final long mDuration;

	public ChartAnimatorV8(final LineChart chart, final long duration) {
		mChart = chart;
		mDuration = duration;
	}

	@Override
	public void startAnimation() {
		final Handler handler = new Handler();
		final long start = SystemClock.uptimeMillis();
		final Interpolator interpolator = new LinearInterpolator();

		handler.post(new Runnable() {
			@Override
			public void run() {
				long elapsed = SystemClock.uptimeMillis() - start;
				float dt = Math.min(interpolator.getInterpolation((float) elapsed / mDuration), 1);
				if (dt < 1.0) {
					mChart.animationUpdate(dt);
					handler.postDelayed(this, 16);
				} else {
					mChart.animationUpdate(1.0f);
				}
			}
		});

	}

}
