package lecho.lib.hellocharts.anim;

public interface ChartAnimator {

	public static final int DEFAULT_ANIMATION_DURATION = 500;

	public void startAnimation();

	public void cancelAnimation();

	public boolean isAnimationStarted();

	public void setChartAnimationListener(ChartAnimationListener animationListener);

}
