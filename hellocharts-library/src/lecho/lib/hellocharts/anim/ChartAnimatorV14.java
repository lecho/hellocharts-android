package lecho.lib.hellocharts.anim;

import lecho.lib.hellocharts.Chart;
import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.annotation.SuppressLint;

@SuppressLint("NewApi")
public class ChartAnimatorV14 implements ChartAnimator, AnimatorListener, AnimatorUpdateListener {
	private ValueAnimator animator;
	private final Chart chart;
	private ChartAnimationListener animationListener = new DummyChartAnimationListener();

	public ChartAnimatorV14(Chart chart) {
		this(chart, DEFAULT_ANIMATION_DURATION);
	}

	public ChartAnimatorV14(final Chart chart, final long duration) {
		this.chart = chart;
		animator = ValueAnimator.ofFloat(0.0f, 1.0f);
		animator.setDuration(duration);
		animator.addListener(this);
		animator.addUpdateListener(this);
	}

	@Override
	public void startAnimation() {
		animationListener.onAnimationStarted();
		animator.start();
	}

	@Override
	public void cancelAnimation() {
		animator.cancel();
	}

	@Override
	public void onAnimationUpdate(ValueAnimator animation) {
		chart.animationDataUpdate(animation.getAnimatedFraction());
	}

	@Override
	public void onAnimationCancel(Animator animation) {
		chart.animationDataFinished(false);
		animationListener.onAnimationFinished();
	}

	@Override
	public void onAnimationEnd(Animator animation) {
		chart.animationDataFinished(true);
		animationListener.onAnimationFinished();
	}

	@Override
	public void onAnimationRepeat(Animator animation) {
	}

	@Override
	public void onAnimationStart(Animator animation) {
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
