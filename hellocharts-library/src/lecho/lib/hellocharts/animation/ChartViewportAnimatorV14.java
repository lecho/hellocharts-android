package lecho.lib.hellocharts.animation;

import lecho.lib.hellocharts.DummyChartAnimationListener;
import lecho.lib.hellocharts.model.Viewport;
import lecho.lib.hellocharts.view.Chart;
import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.annotation.SuppressLint;

@SuppressLint("NewApi")
public class ChartViewportAnimatorV14 implements ChartViewportAnimator, AnimatorListener, AnimatorUpdateListener {
	private ValueAnimator animator;
	private final Chart chart;
	private Viewport startViewport = new Viewport();
	private Viewport targetViewport = new Viewport();
	private Viewport newViewport = new Viewport();
	private ChartAnimationListener animationListener = new DummyChartAnimationListener();

	public ChartViewportAnimatorV14(Chart chart) {
		this(chart, FAST_ANIMATION_DURATION);
	}

	public ChartViewportAnimatorV14(Chart chart, long duration) {
		this.chart = chart;
		animator = ValueAnimator.ofFloat(0.0f, 1.0f);
		animator.setDuration(duration);
		animator.addListener(this);
		animator.addUpdateListener(this);
	}

	@Override
	public void startAnimation(Viewport startViewport, Viewport targetViewport) {
		this.startViewport.set(startViewport);
		this.targetViewport.set(targetViewport);
		animator.start();
	}

	@Override
	public void cancelAnimation() {
		animator.cancel();
	}

	@Override
	public void onAnimationUpdate(ValueAnimator animation) {
		float scale = animation.getAnimatedFraction();
		float diffLeft = (targetViewport.left - startViewport.left) * scale;
		float diffTop = (targetViewport.top - startViewport.top) * scale;
		float diffRight = (targetViewport.right - startViewport.right) * scale;
		float diffBottom = (targetViewport.bottom - startViewport.bottom) * scale;
		newViewport.set(startViewport.left + diffLeft, startViewport.top + diffTop, startViewport.right + diffRight,
				startViewport.bottom + diffBottom);
		chart.setCurrentViewport(newViewport, false);
	}

	@Override
	public void onAnimationCancel(Animator animation) {
	}

	@Override
	public void onAnimationEnd(Animator animation) {
		chart.setCurrentViewport(targetViewport, false);
		animationListener.onAnimationFinished();
	}

	@Override
	public void onAnimationRepeat(Animator animation) {
	}

	@Override
	public void onAnimationStart(Animator animation) {
		animationListener.onAnimationStarted();
	}

	@Override
	public boolean isAnimationStarted() {
		return animator.isStarted();
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
