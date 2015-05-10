package lecho.lib.hellocharts.animation;

import android.os.Handler;
import android.os.SystemClock;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Interpolator;

import lecho.lib.hellocharts.view.PieChartView;

public class PieChartRotationAnimatorV8 implements PieChartRotationAnimator {

    final PieChartView chart;
    final long duration;
    final Handler handler;
    final Interpolator interpolator = new AccelerateDecelerateInterpolator();
    long start;
    boolean isAnimationStarted = false;
    private float startRotation = 0;
    private float targetRotation = 0;
    private ChartAnimationListener animationListener = new DummyChartAnimationListener();
    private final Runnable runnable = new Runnable() {

        @Override
        public void run() {
            long elapsed = SystemClock.uptimeMillis() - start;
            if (elapsed > duration) {
                isAnimationStarted = false;
                handler.removeCallbacks(runnable);
                chart.setChartRotation((int) targetRotation, false);
                animationListener.onAnimationFinished();
                return;
            }
            float scale = Math.min(interpolator.getInterpolation((float) elapsed / duration), 1);
            float rotation = startRotation + (targetRotation - startRotation) * scale;
            rotation = (rotation % 360 + 360) % 360;
            chart.setChartRotation((int) rotation, false);
            handler.postDelayed(this, 16);
        }
    };

    public PieChartRotationAnimatorV8(PieChartView chart) {
        this(chart, FAST_ANIMATION_DURATION);
    }

    public PieChartRotationAnimatorV8(PieChartView chart, long duration) {
        this.chart = chart;
        this.duration = duration;
        this.handler = new Handler();
    }

    @Override
    public void startAnimation(float startRotation, float targetRotation) {
        this.startRotation = (startRotation % 360 + 360) % 360;
        this.targetRotation = (targetRotation % 360 + 360) % 360;
        isAnimationStarted = true;
        animationListener.onAnimationStarted();
        start = SystemClock.uptimeMillis();
        handler.post(runnable);
    }

    @Override
    public void cancelAnimation() {
        isAnimationStarted = false;
        handler.removeCallbacks(runnable);
        chart.setChartRotation((int) targetRotation, false);
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
