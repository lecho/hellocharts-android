package lecho.lib.hellocharts.gesture;

import android.view.MotionEvent;

public interface ChartTouchHandler {

	public boolean computeScroll();

	public boolean handleTouchEvent(MotionEvent event);

	public void startZoom(float x, float y, float zoom);

	public void setZoomEnabled(boolean isZoomEnabled);

	public void setZoomType(int zoomType);

	public int getZoomType();

	public void setValueTouchEnabled(boolean isValueTouchEnabled);
}
