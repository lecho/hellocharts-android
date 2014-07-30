package lecho.lib.hellocharts.anim;

import android.graphics.RectF;

public interface ViewportAnimator {

	public static final int FAST_ANIMATION_DURATION = 2000;

	public void startAnimation(RectF startViewport, RectF targetViewport);

	public void cancelAnimation();

	public boolean isAnimationStarted();

	public void setChartAnimationListener(ChartAnimationListener animationListener);

}
