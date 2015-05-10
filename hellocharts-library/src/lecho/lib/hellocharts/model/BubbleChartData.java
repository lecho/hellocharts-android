package lecho.lib.hellocharts.model;

import java.util.ArrayList;
import java.util.List;

import lecho.lib.hellocharts.formatter.BubbleChartValueFormatter;
import lecho.lib.hellocharts.formatter.SimpleBubbleChartValueFormatter;
import lecho.lib.hellocharts.view.Chart;

/**
 * Data for BubbleChart.
 */
public class BubbleChartData extends AbstractChartData {
    public static final int DEFAULT_MIN_BUBBLE_RADIUS_DP = 6;
    public static final float DEFAULT_BUBBLE_SCALE = 1f;
    private BubbleChartValueFormatter formatter = new SimpleBubbleChartValueFormatter();
    private boolean hasLabels = false;
    private boolean hasLabelsOnlyForSelected = false;
    private int minBubbleRadius = DEFAULT_MIN_BUBBLE_RADIUS_DP;
    private float bubbleScale = DEFAULT_BUBBLE_SCALE;
    // TODO: consider Collections.emptyList()
    private List<BubbleValue> values = new ArrayList<BubbleValue>();

    public BubbleChartData() {
    }

    public BubbleChartData(List<BubbleValue> values) {
        setValues(values);
    }

    /**
     * Copy constructor for deep copy.
     */
    public BubbleChartData(BubbleChartData data) {
        super(data);
        this.formatter = data.formatter;
        this.hasLabels = data.hasLabels;
        this.hasLabelsOnlyForSelected = data.hasLabelsOnlyForSelected;
        this.minBubbleRadius = data.minBubbleRadius;
        this.bubbleScale = data.bubbleScale;

        for (BubbleValue bubbleValue : data.getValues()) {
            this.values.add(new BubbleValue(bubbleValue));
        }
    }

    public static BubbleChartData generateDummyData() {
        final int numValues = 4;
        BubbleChartData data = new BubbleChartData();
        List<BubbleValue> values = new ArrayList<BubbleValue>(numValues);
        values.add(new BubbleValue(0, 20, 15000));
        values.add(new BubbleValue(3, 22, 20000));
        values.add(new BubbleValue(5, 25, 5000));
        values.add(new BubbleValue(7, 30, 30000));
        values.add(new BubbleValue(11, 22, 10));
        data.setValues(values);
        return data;
    }

    @Override
    public void update(float scale) {
        for (BubbleValue value : values) {
            value.update(scale);
        }
    }

    @Override
    public void finish() {
        for (BubbleValue value : values) {
            value.finish();
        }
    }

    public List<BubbleValue> getValues() {
        return values;
    }

    public BubbleChartData setValues(List<BubbleValue> values) {
        if (null == values) {
            this.values = new ArrayList<BubbleValue>();
        } else {
            this.values = values;
        }
        return this;
    }

    public boolean hasLabels() {
        return hasLabels;
    }

    public BubbleChartData setHasLabels(boolean hasLabels) {
        this.hasLabels = hasLabels;
        if (hasLabels) {
            hasLabelsOnlyForSelected = false;
        }
        return this;
    }

    /**
     * @see #setHasLabelsOnlyForSelected(boolean)
     */
    public boolean hasLabelsOnlyForSelected() {
        return hasLabelsOnlyForSelected;
    }

    /**
     * Set true if you want to show value labels only for selected value, works best when chart has
     * isValueSelectionEnabled set to true {@link Chart#setValueSelectionEnabled(boolean)}.
     */
    public BubbleChartData setHasLabelsOnlyForSelected(boolean hasLabelsOnlyForSelected) {
        this.hasLabelsOnlyForSelected = hasLabelsOnlyForSelected;
        if (hasLabelsOnlyForSelected) {
            this.hasLabels = false;
        }
        return this;
    }

    /**
     * Returns minimal bubble radius in dp.
     *
     * @see #setMinBubbleRadius(int)
     */
    public int getMinBubbleRadius() {
        return minBubbleRadius;
    }

    /**
     * Set minimal bubble radius in dp, helpful when you want small bubbles(bubbles with very small z values compared to
     * other bubbles) to be visible on chart, default 6dp
     */
    public void setMinBubbleRadius(int minBubbleRadius) {
        this.minBubbleRadius = minBubbleRadius;
    }

    /**
     * Returns bubble scale which is used to adjust bubble size.
     *
     * @see #setBubbleScale(float)
     */
    public float getBubbleScale() {
        return bubbleScale;
    }

    /**
     * Set bubble scale which is used to adjust bubble size. If you want smaller bubbles set scale {@code <0, 1>},
     * if you want bigger bubbles set scale greater than 1, default is 1.0f.
     */
    public void setBubbleScale(float bubbleScale) {
        this.bubbleScale = bubbleScale;
    }

    public BubbleChartValueFormatter getFormatter() {
        return formatter;
    }

    public BubbleChartData setFormatter(BubbleChartValueFormatter formatter) {
        if (null != formatter) {
            this.formatter = formatter;
        }
        return this;
    }
}
