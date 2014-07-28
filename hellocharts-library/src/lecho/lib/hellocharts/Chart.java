package lecho.lib.hellocharts;

import lecho.lib.hellocharts.anim.ChartAnimationListener;
import lecho.lib.hellocharts.gesture.ChartTouchHandler;
import lecho.lib.hellocharts.model.ChartData;
import lecho.lib.hellocharts.model.SelectedValue;
import lecho.lib.hellocharts.renderer.AxesRenderer;
import lecho.lib.hellocharts.renderer.ChartRenderer;
import android.graphics.RectF;

public interface Chart {

	public ChartData getChartData();

	public ChartRenderer getChartRenderer();

	public AxesRenderer getAxesRenderer();

	public ChartCalculator getChartCalculator();

	public ChartTouchHandler getTouchHandler();

	public void animationDataUpdate(float scale);

	public void startDataAnimation();

	public void setChartAnimationListener(ChartAnimationListener animationListener);

	public void callTouchListener(SelectedValue selectedValue);

	public boolean isInteractive();

	public void setInteractive(boolean isInteractive);

	public boolean isZoomEnabled();

	public void setZoomEnabled(boolean isZoomEnabled);

	public int getZoomType();

	public void setZoomType(int zoomType);

	public boolean isValueTouchEnabled();

	public void setValueTouchEnabled(boolean isValueTouchEnabled);

	public void setMaxViewport(RectF maxViewport);

	public RectF getMaxViewport();

	public void setViewport(RectF viewport);

	public RectF getViewport();

	public void zoom(float x, float y, float zoomAmout);

	public boolean isValueSelectionEnabled();

	public void setValueSelectionEnabled(boolean isValueSelectionEnabled);

}
