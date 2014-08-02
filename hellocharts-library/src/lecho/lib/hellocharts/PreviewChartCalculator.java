package lecho.lib.hellocharts;

import lecho.lib.hellocharts.model.Viewport;

public class PreviewChartCalculator extends ChartCalculator {

	public float calculateRawX(float valueX) {
		final float pixelOffset = (valueX - maxViewport.left) * (contentRect.width() / maxViewport.width());
		return contentRect.left + pixelOffset;
	}

	public float calculateRawY(float valueY) {
		final float pixelOffset = (valueY - maxViewport.bottom) * (contentRect.height() / maxViewport.height());
		return contentRect.bottom - pixelOffset;
	}

	public Viewport getVisibleViewport() {
		return maxViewport;
	}

	public void setVisibleViewport(Viewport visibleViewport) {
		setMaxViewport(visibleViewport);
	}

	public void constrainViewport(float left, float top, float right, float bottom) {
		super.constrainViewport(left, top, right, bottom);
		viewportChangeListener.onViewportChanged(currentViewport);
	}

}