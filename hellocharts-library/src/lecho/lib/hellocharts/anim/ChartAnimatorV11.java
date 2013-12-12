package lecho.lib.hellocharts.anim;

import lecho.lib.hellocharts.LineChart;
import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.annotation.SuppressLint;
import android.view.animation.LinearInterpolator;

@SuppressLint("NewApi")
public class ChartAnimatorV11 implements ChartAnimator, AnimatorListener, AnimatorUpdateListener {
	@SuppressWarnings("unused")
	private float mScale;
	private ObjectAnimator mObjectAnimator;
	private final LineChart mChart;
	private final long mDuration;

	public ChartAnimatorV11(final LineChart chart, final long duration) {
		mChart = chart;
		mDuration = duration;
		mObjectAnimator = ObjectAnimator.ofFloat(this, "mScale", 0.0f, 1.0f);
		mObjectAnimator.setDuration(mDuration);
		mObjectAnimator.setInterpolator(new LinearInterpolator());
		mObjectAnimator.addListener(this);
		mObjectAnimator.addUpdateListener(this);
	}

	@Override
	public void startAnimation() {
		mScale = 0.0f;
		mObjectAnimator.start();
	}

	@Override
	public void cancelAnimation() {
		mObjectAnimator.cancel();
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
		mChart.animationUpdate(1.0f);
	}

	@Override
	public void onAnimationRepeat(Animator animation) {
	}

	@Override
	public void onAnimationStart(Animator animation) {
	}

}
