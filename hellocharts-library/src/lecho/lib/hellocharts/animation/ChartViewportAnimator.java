package lecho.lib.hellocharts.animation;

import android.graphics.RectF;

public interface ChartViewportAnimator {

	public static final int FAST_ANIMATION_DURATION = 200;

	public void startAnimation(RectF startViewport, RectF targetViewport);

	public void cancelAnimation();

	public boolean isAnimationStarted();

	public void setChartAnimationListener(ChartAnimationListener animationListener);

}
