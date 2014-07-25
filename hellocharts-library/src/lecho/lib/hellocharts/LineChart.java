package lecho.lib.hellocharts;

public interface LineChart {
	int DEFAULT_LINE_STROKE_WIDTH_DP = 3;
	int DEFAULT_POINT_RADIUS_DP = 6;
	int DEFAULT_TOUCH_TOLLERANCE_MARGIN_DP = 4;

	public int getLineStrokeWidth();

	public int getPointRadius();

	public int getTouchTolleranceMargin();

}
