package lecho.lib.hellocharts.anim;

import lecho.lib.hellocharts.LineChart;
import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.annotation.SuppressLint;
import android.view.animation.LinearInterpolator;

@SuppressLint("NewApi")
public class ChartAnimatorV11 implements ChartAnimator, AnimatorListener, AnimatorUpdateListener {
	private ValueAnimator mAnimator;
	private final LineChart mChart;
	private final long mDuration;

	public ChartAnimatorV11(final LineChart chart) {
		this(chart, DEFAULT_ANIMATION_DURATION);
	}

	public ChartAnimatorV11(final LineChart chart, final long duration) {
		mChart = chart;
		mDuration = duration;
		mAnimator = ValueAnimator.ofFloat(0.0f, 1.0f);
		mAnimator.setDuration(mDuration);
		mAnimator.setInterpolator(new LinearInterpolator());
		mAnimator.addListener(this);
		mAnimator.addUpdateListener(this);
	}

	@Override
	public void startAnimation() {
		mAnimator.start();
	}

	@Override
	public void cancelAnimation() {
		mAnimator.cancel();
	}

	@Override
	public void onAnimationUpdate(ValueAnimator animation) {
		mChart.animationUpdate((Float) animation.getAnimatedValue());
	}

	@Override
	public void onAnimationCancel(Animator animation) {
	}

	@Override
	public void onAnimationEnd(Animator animation) {
	}

	@Override
	public void onAnimationRepeat(Animator animation) {
	}

	@Override
	public void onAnimationStart(Animator animation) {
	}

}
