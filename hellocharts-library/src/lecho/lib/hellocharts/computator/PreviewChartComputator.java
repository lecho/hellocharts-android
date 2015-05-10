package lecho.lib.hellocharts.computator;

import lecho.lib.hellocharts.model.Viewport;

/**
 * Version of ChartComputator for preview charts. It always uses maxViewport as visible viewport and currentViewport as
 * preview area.
 */
public class PreviewChartComputator extends ChartComputator {

    public float computeRawX(float valueX) {
        final float pixelOffset = (valueX - maxViewport.left) * (contentRectMinusAllMargins.width() / maxViewport
                .width());
        return contentRectMinusAllMargins.left + pixelOffset;
    }

    public float computeRawY(float valueY) {
        final float pixelOffset = (valueY - maxViewport.bottom) * (contentRectMinusAllMargins.height() / maxViewport
                .height());
        return contentRectMinusAllMargins.bottom - pixelOffset;
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