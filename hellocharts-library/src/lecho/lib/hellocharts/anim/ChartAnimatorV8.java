package lecho.lib.hellocharts.anim;

import lecho.lib.hellocharts.Chart;
import android.os.Handler;
import android.os.SystemClock;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Interpolator;

public class ChartAnimatorV8 implements ChartAnimator {

	long start;
	boolean isAnimationStarted = false;
	final Chart chart;
	final long duration;
	final Handler handler;
	final Interpolator interpolator = new AccelerateDecelerateInterpolator();
	private ChartAnimationListener animationListener = new DummyChartAnimationListener();
	private final Runnable runnable = new Runnable() {

		@Override
		public void run() {
			long elapsed = SystemClock.uptimeMillis() - start;
			if (elapsed > duration) {
				isAnimationStarted = false;
				chart.animationDataFinished(true);
				return;
			}
			float scale = Math.min(interpolator.getInterpolation((float) elapsed / duration), 1);
			chart.animationDataUpdate(scale);
			handler.postDelayed(this, 16);

		}
	};

	public ChartAnimatorV8(final Chart chart) {
		this(chart, DEFAULT_ANIMATION_DURATION);
	}

	public ChartAnimatorV8(final Chart chart, final long duration) {
		this.chart = chart;
		this.duration = duration;
		this.handler = new Handler();
	}

	@Override
	public void startAnimation() {
		isAnimationStarted = true;
		animationListener.onAnimationStarted();
		start = SystemClock.uptimeMillis();
		handler.post(runnable);
	}

	@Override
	public void cancelAnimation() {
		isAnimationStarted = false;
		handler.removeCallbacks(runnable);
		chart.animationDataFinished(false);
		animationListener.onAnimationFinished();
	}

	@Override
	public boolean isAnimationStarted() {
		return isAnimationStarted;
	}

	@Override
	public void setChartAnimationListener(ChartAnimationListener animationListener) {
		if (null == animationListener) {
			this.animationListener = new DummyChartAnimationListener();
		} else {
			this.animationListener = animationListener;
		}
	}
}
