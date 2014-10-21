package lecho.lib.hellocharts.animation;

/**
 * Listener used to listen for chart animation start and stop events. Implementations of this iterface can be used for
 * all types of chart animations(data, viewport, PieChart rotation).
 * 
 */
public interface ChartAnimationListener {

	public void onAnimationStarted();

	public void onAnimationFinished();

}
