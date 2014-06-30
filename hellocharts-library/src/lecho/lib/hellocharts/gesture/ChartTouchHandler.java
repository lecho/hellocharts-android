package lecho.lib.hellocharts.gesture;

import android.view.MotionEvent;

public interface ChartTouchHandler {

	public boolean computeScroll();

	public boolean handleTouchEvent(MotionEvent event);

	public void setZoomEnabled(boolean isZoomEnabled);

	public void setZoomMode(ZoomMode zoomMode);

	public void setValueTouchEnabled(boolean isValueTouchEnabled);
}
