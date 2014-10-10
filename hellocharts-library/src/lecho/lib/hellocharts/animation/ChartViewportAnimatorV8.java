package lecho.lib.hellocharts.animation;

import lecho.lib.hellocharts.DummyChartAnimationListener;
import lecho.lib.hellocharts.model.Viewport;
import lecho.lib.hellocharts.view.Chart;
import android.os.Handler;
import android.os.SystemClock;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Interpolator;

public class ChartViewportAnimatorV8 implements ChartViewportAnimator {

	long start;
	boolean isAnimationStarted = false;
	final Chart chart;
	private Viewport startViewport = new Viewport();
	private Viewport targetViewport = new Viewport();
	private Viewport newViewport = new Viewport();
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
				chart.setCurrentViewport(targetViewport, false);
				animationListener.onAnimationFinished();
				return;
			}
			float scale = Math.min(interpolator.getInterpolation((float) elapsed / duration), 1);
			float diffLeft = (targetViewport.left - startViewport.left) * scale;
			float diffTop = (targetViewport.top - startViewport.top) * scale;
			float diffRight = (targetViewport.right - startViewport.right) * scale;
			float diffBottom = (targetViewport.bottom - startViewport.bottom) * scale;
			newViewport.set(startViewport.left + diffLeft, startViewport.top + diffTop,
					startViewport.right + diffRight, startViewport.bottom + diffBottom);
			chart.setCurrentViewport(newViewport, false);

			handler.postDelayed(this, 16);
		}
	};

	public ChartViewportAnimatorV8(Chart chart) {
		this(chart, FAST_ANIMATION_DURATION);
	}

	public ChartViewportAnimatorV8(Chart chart, long duration) {
		this.chart = chart;
		this.duration = duration;
		this.handler = new Handler();
	}

	@Override
	public void startAnimation(Viewport startViewport, Viewport targetViewport) {
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
		handler.removeCallbacks(runnable);
		chart.setCurrentViewport(targetViewport, false);
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
