package lecho.lib.hellocharts.view;

import android.content.Context;
import android.graphics.RectF;
import android.os.Build;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import lecho.lib.hellocharts.BuildConfig;
import lecho.lib.hellocharts.animation.PieChartRotationAnimator;
import lecho.lib.hellocharts.animation.PieChartRotationAnimatorV14;
import lecho.lib.hellocharts.animation.PieChartRotationAnimatorV8;
import lecho.lib.hellocharts.gesture.PieChartTouchHandler;
import lecho.lib.hellocharts.listener.DummyPieChartOnValueSelectListener;
import lecho.lib.hellocharts.listener.PieChartOnValueSelectListener;
import lecho.lib.hellocharts.model.ChartData;
import lecho.lib.hellocharts.model.PieChartData;
import lecho.lib.hellocharts.model.SelectedValue;
import lecho.lib.hellocharts.model.SliceValue;
import lecho.lib.hellocharts.provider.PieChartDataProvider;
import lecho.lib.hellocharts.renderer.PieChartRenderer;

/**
 * PieChart is a little different than others charts. It doesn't have axes. It doesn't support viewport so changing
 * viewport wont work. Instead it support "Circle Oval". Pinch-to-Zoom and double tap zoom wont work either. Instead of
 * scroll there is chart rotation if isChartRotationEnabled is set to true. PieChart looks the best when it has the same
 * width and height, drawing chart on rectangle with proportions other than 1:1 will left some empty spaces.
 *
 * @author Leszek Wach
 */
public class PieChartView extends AbstractChartView implements PieChartDataProvider {
    private static final String TAG = "PieChartView";
    protected PieChartData data;
    protected PieChartOnValueSelectListener onValueTouchListener = new DummyPieChartOnValueSelectListener();
    protected PieChartRenderer pieChartRenderer;
    protected PieChartRotationAnimator rotationAnimator;

    public PieChartView(Context context) {
        this(context, null, 0);
    }

    public PieChartView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PieChartView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        pieChartRenderer = new PieChartRenderer(context, this, this);
        touchHandler = new PieChartTouchHandler(context, this);
        setChartRenderer(pieChartRenderer);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            this.rotationAnimator = new PieChartRotationAnimatorV8(this);
        } else {
            this.rotationAnimator = new PieChartRotationAnimatorV14(this);
        }
        setPieChartData(PieChartData.generateDummyData());
    }

    @Override
    public PieChartData getPieChartData() {
        return data;
    }

    @Override
    public void setPieChartData(PieChartData data) {
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "Setting data for ColumnChartView");
        }

        if (null == data) {
            this.data = PieChartData.generateDummyData();
        } else {
            this.data = data;
        }

        super.onChartDataChange();
    }

    @Override
    public ChartData getChartData() {
        return data;
    }

    @Override
    public void callTouchListener() {
        SelectedValue selectedValue = chartRenderer.getSelectedValue();

        if (selectedValue.isSet()) {
            SliceValue sliceValue = data.getValues().get(selectedValue.getFirstIndex());
            onValueTouchListener.onValueSelected(selectedValue.getFirstIndex(), sliceValue);
        } else {
            onValueTouchListener.onValueDeselected();
        }
    }

    public PieChartOnValueSelectListener getOnValueTouchListener() {
        return onValueTouchListener;
    }

    public void setOnValueTouchListener(PieChartOnValueSelectListener touchListener) {
        if (null != touchListener) {
            this.onValueTouchListener = touchListener;
        }
    }

    /**
     * Returns rectangle that will constraint pie chart area.
     */
    public RectF getCircleOval() {
        return pieChartRenderer.getCircleOval();
    }

    /**
     * Use this to change pie chart area. Because by default CircleOval is calculated onSizeChanged() you must call this
     * method after size of PieChartView is calculated. In most cases it will probably be easier to use
     * {@link #setCircleFillRatio(float)} to change chart area or just use view padding.
     */
    public void setCircleOval(RectF orginCircleOval) {
        pieChartRenderer.setCircleOval(orginCircleOval);
        ViewCompat.postInvalidateOnAnimation(this);
    }

    /**
     * Returns pie chart rotation, 0 rotation means that 0 degrees is at 3 o'clock. Don't confuse with
     * {@link View#getRotation()}.
     *
     * @return
     */
    public int getChartRotation() {
        return pieChartRenderer.getChartRotation();
    }

    /**
     * Set pie chart rotation. Don't confuse with {@link View#getRotation()}.
     *
     * @param rotation
     * @see #getChartRotation()
     */
    public void setChartRotation(int rotation, boolean isAnimated) {
        if (isAnimated) {
            rotationAnimator.cancelAnimation();
            rotationAnimator.startAnimation(pieChartRenderer.getChartRotation(), rotation);
        } else {
            pieChartRenderer.setChartRotation(rotation);
        }
        ViewCompat.postInvalidateOnAnimation(this);
    }

    public boolean isChartRotationEnabled() {
        if (touchHandler instanceof PieChartTouchHandler) {
            return ((PieChartTouchHandler) touchHandler).isRotationEnabled();
        } else {
            return false;
        }
    }

    /**
     * Set false if you don't wont the chart to be rotated by touch gesture. Rotating programmatically will still work.
     *
     * @param isRotationEnabled
     */
    public void setChartRotationEnabled(boolean isRotationEnabled) {
        if (touchHandler instanceof PieChartTouchHandler) {
            ((PieChartTouchHandler) touchHandler).setRotationEnabled(isRotationEnabled);
        }
    }

    /**
     * Returns SliceValue that is under given angle, selectedValue (if not null) will be hold slice index.
     */
    public SliceValue getValueForAngle(int angle, SelectedValue selectedValue) {
        return pieChartRenderer.getValueForAngle(angle, selectedValue);
    }

    /**
     * @see #setCircleFillRatio(float)
     */
    public float getCircleFillRatio() {
        return pieChartRenderer.getCircleFillRatio();
    }

    /**
     * Set how much of view area should be taken by chart circle. Value should be between 0 and 1. Default is 1 so
     * circle will have radius equals min(View.width, View.height).
     */
    public void setCircleFillRatio(float fillRatio) {
        pieChartRenderer.setCircleFillRatio(fillRatio);
        ViewCompat.postInvalidateOnAnimation(this);
    }
}
