package lecho.lib.hellocharts.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Data for BubbleChart.
 * 
 * @author Leszek Wach
 * 
 */
public class BubbleChartData extends AbstractChartData {
	public static final int DEFAULT_MIN_BUBBLE_RADIUS_DP = 6;
	public static final float DEFAULT_BUBBLE_SCALE = 1f;
	private ValueFormatter formatter = new NumberValueFormatter();
	private boolean hasLabels = false;
	private boolean hasLabelsOnlyForSelected = false;
	private int minBubbleRadius = DEFAULT_MIN_BUBBLE_RADIUS_DP;
	private float bubbleScale = DEFAULT_BUBBLE_SCALE;
	// TODO: consider Collections.emptyList()
	private List<BubbleValue> values = new ArrayList<BubbleValue>();

	public BubbleChartData() {
	};

	public BubbleChartData(List<BubbleValue> values) {
		setValues(values);
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

	public boolean hasLabelsOnlyForSelected() {
		return hasLabelsOnlyForSelected;
	}

	public BubbleChartData setHasLabelsOnlyForSelected(boolean hasLabelsOnlyForSelected) {
		this.hasLabelsOnlyForSelected = hasLabelsOnlyForSelected;
		if (hasLabelsOnlyForSelected) {
			this.hasLabels = false;
		}
		return this;
	}

	public int getMinBubbleRadius() {
		return minBubbleRadius;
	}

	public void setMinBubbleRadius(int minBubbleRadius) {
		this.minBubbleRadius = minBubbleRadius;
	}

	public float getBubbleScale() {
		return bubbleScale;
	}

	public void setBubbleScale(float bubbleScale) {
		this.bubbleScale = bubbleScale;
	}

	public ValueFormatter getFormatter() {
		return formatter;
	}

	public BubbleChartData setFormatter(ValueFormatter formatter) {
		if (null == formatter) {
			this.formatter = new NumberValueFormatter();
		} else {
			this.formatter = formatter;
		}
		return this;
	}
}
