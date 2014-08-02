package lecho.lib.hellocharts.animation;

public interface ChartDataAnimator {

	public static final int DEFAULT_ANIMATION_DURATION = 500;

	public void startAnimation();

	public void cancelAnimation();

	public boolean isAnimationStarted();

	public void setChartAnimationListener(ChartAnimationListener animationListener);

}
