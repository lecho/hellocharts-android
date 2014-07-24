package lecho.lib.hellocharts.view;

import lecho.lib.hellocharts.Chart;
import lecho.lib.hellocharts.ChartCalculator;
import lecho.lib.hellocharts.gesture.ChartTouchHandler;
import lecho.lib.hellocharts.gesture.ZoomMode;
import lecho.lib.hellocharts.model.ChartData;
import lecho.lib.hellocharts.model.SelectedValue;
import lecho.lib.hellocharts.renderer.AxesRenderer;
import lecho.lib.hellocharts.renderer.ChartRenderer;
import lecho.lib.hellocharts.util.Utils;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

public abstract class AbstractChartView extends View implements Chart {
	protected ChartCalculator mChartCalculator;
	protected AxesRenderer mAxesRenderer;
	protected ChartTouchHandler mTouchHandler;
	protected ChartRenderer mChartRenderer;
	protected boolean isInteractive = true;
	protected boolean isZoomEnabled = true;
	protected boolean isValueTouchEnabled = true;
	protected ZoomMode zoomMode = ZoomMode.HORIZONTAL_AND_VERTICAL;
	protected int defaultTextSize;
	protected int defaultLabelMargin;
	protected int defaultContentAreaMargin;
	protected int defaultAxesNameMargin;
	protected int defaultLineStrokeWidth;
	protected int defaultPointRadius;
	protected int defaultTouchTolleranceMargin;
	protected int defaultSubcolumnSpacing;
	protected int defaultColumnTouchAdditionalWidth;

	public AbstractChartView(Context context) {
		this(context, null, 0);
	}

	public AbstractChartView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public AbstractChartView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		defaultTextSize = Utils.sp2px(context, DEFAULT_TEXT_SIZE_SP);
		defaultLabelMargin = Utils.sp2px(context, DEFAULT_LABEL_MARGIN_DP);
		defaultContentAreaMargin = Utils.dp2px(context, DEFAULT_CONTENT_AREA_MARGIN_DP);
		defaultAxesNameMargin = Utils.dp2px(context, DEFAULT_AXES_NAME_MARGIN_DP);
		defaultLineStrokeWidth = Utils.dp2px(context, DEFAULT_LINE_STROKE_WIDTH_DP);
		defaultPointRadius = Utils.dp2px(context, DEFAULT_POINT_RADIUS_DP);
		defaultTouchTolleranceMargin = Utils.dp2px(context, DEFAULT_TOUCH_TOLLERANCE_MARGIN_DP);
		defaultSubcolumnSpacing = Utils.dp2px(context, DEFAULT_SUBCOLUMN_SPACING_DP);
		defaultColumnTouchAdditionalWidth = Utils.dp2px(context, DEFAULT_COLUMN_TOUCH_ADDITIONAL_WIDTH_DP);
	}

	public ChartRenderer getChartRenderer() {
		return mChartRenderer;
	}

	public AxesRenderer getAxesRenderer() {
		return mAxesRenderer;
	}

	public ChartCalculator getChartCalculator() {
		return mChartCalculator;
	}

	public ChartTouchHandler getTouchHandler() {
		return mTouchHandler;
	}

	public boolean isInteractive() {
		return isInteractive;
	}

	public void setInteractive(boolean isInteractive) {
		this.isInteractive = isInteractive;
	}

	public boolean isZoomEnabled() {
		return isZoomEnabled;
	}

	public void setZoomEnabled(boolean isZoomEnabled) {
		this.isZoomEnabled = isZoomEnabled;
	}

	public boolean isValueTouchEnabled() {
		return isValueTouchEnabled;
	}

	@Override
	public void setValueTouchEnabled(boolean isValueTouchEnabled) {
		this.isValueTouchEnabled = isValueTouchEnabled;

	}

	public ZoomMode getZoomMode() {
		return zoomMode;
	}

	public void setZoomMode(ZoomMode zoomMode) {
		this.zoomMode = zoomMode;
	}

	@Override
	public ChartData getData() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void animationUpdate(float scale) {
		// TODO Auto-generated method stub

	}

	@Override
	public void callTouchListener(SelectedValue selectedValue) {
		// TODO Auto-generated method stub

	}

	@Override
	public int getDefaultTextSize() {
		return defaultTextSize;
	}

	@Override
	public int getDefaultLabelMargin() {
		return defaultLabelMargin;
	}

	@Override
	public int getDefaultContentAreaMargin() {
		return defaultContentAreaMargin;
	}

	@Override
	public int getDefaultAxesNameMargin() {
		return defaultAxesNameMargin;
	}

	@Override
	public int getDefaultLineStrokeWidth() {
		return defaultLineStrokeWidth;
	}

	@Override
	public int getDefaultPointRadius() {
		return defaultPointRadius;
	}

	@Override
	public int getDefaultTouchTolleranceMargin() {
		return defaultTouchTolleranceMargin;
	}

	@Override
	public int getDefaultSubcolumnSpacing() {
		return defaultSubcolumnSpacing;
	}

	@Override
	public int getDefaultColumnTouchAdditionalWidth() {
		return defaultColumnTouchAdditionalWidth;
	}
}
