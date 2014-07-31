package lecho.lib.hellocharts.anim;

import lecho.lib.hellocharts.Chart;
import android.graphics.RectF;
import android.os.Handler;
import android.os.SystemClock;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Interpolator;

public class ChartViewportAnimatorV8 implements ViewportAnimator {

	long start;
	boolean isAnimationStarted = false;
	final Chart chart;
	private RectF startViewport = new RectF();
	private RectF targetViewport = new RectF();
	private RectF newViewport = new RectF();
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
				handler.removeCallbacks(runnable);
				return;
			}
			float scale = Math.min(interpolator.getInterpolation((float) elapsed / duration), 1);
			float diffLeft = (targetViewport.left - startViewport.left) * scale;
			float diffTop = (targetViewport.top - startViewport.top) * scale;
			float diffRight = (targetViewport.right - startViewport.right) * scale;
			float diffBottom = (targetViewport.bottom - startViewport.bottom) * scale;
			newViewport.set(startViewport.left + diffLeft, startViewport.top + diffTop,
					startViewport.right + diffRight, startViewport.bottom + diffBottom);
			chart.setViewport(newViewport, false);

			handler.postDelayed(this, 16);
		}
	};

	public ChartViewportAnimatorV8(final Chart chart) {
		this(chart, FAST_ANIMATION_DURATION);
	}

	public ChartViewportAnimatorV8(final Chart chart, final long duration) {
		this.chart = chart;
		this.duration = duration;
		this.handler = new Handler();
	}

	@Override
	public void startAnimation(RectF startViewport, RectF targetViewport) {
		this.startViewport.set(startViewport);
		this.targetViewport.set(targetViewport);
		isAnimationStarted = true;
		animationListener.onAnimationStarted();
		start = SystemClock.uptimeMillis();
		handler.post(runnable);
	}

	@Override
	public void cancelAnimation() {
		isAnimationStarted = false;
		chart.setViewport(targetViewport, false);
		handler.removeCallbacks(runnable);
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
