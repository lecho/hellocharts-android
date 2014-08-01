package lecho.lib.hellocharts;

import android.graphics.RectF;

public class PreviewChartCalculator extends ChartCalculator {

	public float calculateRawX(float valueX) {
		final float pixelOffset = (valueX - maxViewport.left) * (contentRect.width() / maxViewport.width());
		return contentRect.left + pixelOffset;
	}

	public float calculateRawY(float valueY) {
		final float pixelOffset = (valueY - maxViewport.top) * (contentRect.height() / maxViewport.height());
		return contentRect.bottom - pixelOffset;
	}

	public RectF getVisibleViewport() {
		return maxViewport;
	}

	public void setVisibleViewport(RectF visibleViewport) {
		setMaxViewport(visibleViewport);
	}

}