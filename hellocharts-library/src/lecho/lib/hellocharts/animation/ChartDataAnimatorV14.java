package lecho.lib.hellocharts.animation;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.annotation.SuppressLint;

import lecho.lib.hellocharts.view.Chart;

@SuppressLint("NewApi")
public class ChartDataAnimatorV14 implements ChartDataAnimator, AnimatorListener, AnimatorUpdateListener {
    private final Chart chart;
    private ValueAnimator animator;
    private ChartAnimationListener animationListener = new DummyChartAnimationListener();

    public ChartDataAnimatorV14(Chart chart) {
        this.chart = chart;
        animator = ValueAnimator.ofFloat(0.0f, 1.0f);
        animator.addListener(this);
        animator.addUpdateListener(this);
    }

    @Override
    public void startAnimation(long duration) {
        if (duration >= 0) {
            animator.setDuration(duration);
        } else {
            animator.setDuration(DEFAULT_ANIMATION_DURATION);
        }
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
    }

    @Override
    public void onAnimationEnd(Animator animation) {
        chart.animationDataFinished();
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
