package lecho.lib.hellocharts;

import lecho.lib.hellocharts.gesture.ChartTouchHandler;
import lecho.lib.hellocharts.gesture.ZoomMode;
import lecho.lib.hellocharts.model.ChartData;
import lecho.lib.hellocharts.model.SelectedValue;
import lecho.lib.hellocharts.renderer.AxesRenderer;
import lecho.lib.hellocharts.renderer.ChartRenderer;

public interface Chart {
	int DEFAULT_TEXT_SIZE_SP = 12;
	int DEFAULT_LABEL_MARGIN_DP = 4;
	int DEFAULT_CONTENT_AREA_MARGIN_DP = 4;
	int DEFAULT_AXES_NAME_MARGIN_DP = 4;
	int DEFAULT_LINE_STROKE_WIDTH_DP = 3;
	int DEFAULT_POINT_RADIUS_DP = 6;
	int DEFAULT_TOUCH_TOLLERANCE_MARGIN_DP = 4;
	int DEFAULT_SUBCOLUMN_SPACING_DP = 1;
	int DEFAULT_COLUMN_TOUCH_ADDITIONAL_WIDTH_DP = 2;

	public ChartData getData();

	public ChartRenderer getChartRenderer();

	public AxesRenderer getAxesRenderer();

	public ChartCalculator getChartCalculator();

	public ChartTouchHandler getTouchHandler();

	public void animationUpdate(float scale);

	public void callTouchListener(SelectedValue selectedValue);

	public boolean isInteractive();

	public void setInteractive(boolean isInteractive);

	public boolean isZoomEnabled();

	public void setZoomEnabled(boolean isZoomEnabled);

	public ZoomMode getZoomMode();

	public void setZoomMode(ZoomMode zoomMode);

	public boolean isValueTouchEnabled();

	public void setValueTouchEnabled(boolean isValueTouchEnabled);

	public int getDefaultTextSize();

	public int getDefaultLabelMargin();

	public int getDefaultContentAreaMargin();

	public int getDefaultAxesNameMargin();

	public int getDefaultLineStrokeWidth();

	public int getDefaultPointRadius();

	public int getDefaultTouchTolleranceMargin();

	public int getDefaultSubcolumnSpacing();

	public int getDefaultColumnTouchAdditionalWidth();
}
