package lecho.lib.hellocharts.gesture;

import android.view.MotionEvent;

public class DummyTouchHandler implements ChartTouchHandler {

	public DummyTouchHandler() {
	}

	public boolean computeScroll() {
		return false;
	}

	public boolean handleTouchEvent(MotionEvent event) {
		return false;
	}

	@Override
	public void setZoomEnabled(boolean isZoomEnabled) {
		// do nothing

	}

	@Override
	public void setZoomType(int zoomType) {
		// do nothing
	}

	@Override
	public int getZoomType() {
		return 0;
	}

	@Override
	public void setValueTouchEnabled(boolean isTouchEnabled) {
		// do nothing

	}

	@Override
	public void startZoom(float x, float y, float zoom) {
		// TODO Auto-generated method stub

	}
}
