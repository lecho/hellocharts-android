package lecho.lib.hellocharts.animation;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.annotation.SuppressLint;

import lecho.lib.hellocharts.view.PieChartView;

@SuppressLint("NewApi")
public class PieChartRotationAnimatorV14 implements PieChartRotationAnimator, AnimatorListener, AnimatorUpdateListener {
    private final PieChartView chart;
    private ValueAnimator animator;
    private float startRotation = 0;
    private float targetRotation = 0;
    private ChartAnimationListener animationListener = new DummyChartAnimationListener();

    public PieChartRotationAnimatorV14(PieChartView chart) {
        this(chart, FAST_ANIMATION_DURATION);
    }

    public PieChartRotationAnimatorV14(PieChartView chart, long duration) {
        this.chart = chart;
        animator = ValueAnimator.ofFloat(0.0f, 1.0f);
        animator.setDuration(duration);
        animator.addListener(this);
        animator.addUpdateListener(this);
    }

    @Override
    public void startAnimation(float startRotation, float targetRotation) {
        this.startRotation = (startRotation % 360 + 360) % 360;
        this.targetRotation = (targetRotation % 360 + 360) % 360;
        animator.start();
    }

    @Override
    public void cancelAnimation() {
        animator.cancel();
    }

    @Override
    public void onAnimationUpdate(ValueAnimator animation) {
        float scale = animation.getAnimatedFraction();
        float rotation = startRotation + (targetRotation - startRotation) * scale;
        rotation = (rotation % 360 + 360) % 360;
        chart.setChartRotation((int) rotation, false);
    }

    @Override
    public void onAnimationCancel(Animator animation) {
    }

    @Override
    public void onAnimationEnd(Animator animation) {
        chart.setChartRotation((int) targetRotation, false);
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
