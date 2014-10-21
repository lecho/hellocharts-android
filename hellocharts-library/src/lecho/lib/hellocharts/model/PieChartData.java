package lecho.lib.hellocharts.model;

import java.util.ArrayList;
import java.util.List;

import lecho.lib.hellocharts.view.Chart;
import lecho.lib.hellocharts.view.PieChartView;
import android.graphics.Color;
import android.graphics.Typeface;

/**
 * Data for PieChart, by default it doesn't have axes.
 * 
 */
public class PieChartData extends AbstractChartData {
	public static final int DEFAULT_CENTER_TEXT1_SIZE_SP = 42;
	public static final int DEFAULT_CENTER_TEXT2_SIZE_SP = 16;
	public static final float DEFAULT_CENTER_CIRCLE_SCALE = 0.6f;

	private ValueFormatter formatter = new SimpleValueFormatter();
	private boolean hasLabels = false;
	private boolean hasLabelsOnlyForSelected = false;
	private boolean hasLabelsOutside = false;

	private boolean hasCenterCircle = false;
	private int centerCircleColor = Color.WHITE;
	private float centerCircleScale = 0.6f;

	private int centerText1Color = Color.BLACK;
	private int centerText1FontSize = DEFAULT_CENTER_TEXT1_SIZE_SP;
	private Typeface centerText1Typeface;
	private String centerText1;

	private int centerText2Color = Color.BLACK;
	private int centerText2FontSize = DEFAULT_CENTER_TEXT2_SIZE_SP;
	private Typeface centerText2Typeface;
	private String centerText2;

	private List<ArcValue> values = new ArrayList<ArcValue>();

	public PieChartData() {
		setAxisXBottom(null);
		setAxisYLeft(null);
	};

	public PieChartData(List<ArcValue> values) {
		setValues(values);
		// Empty axes. Pie chart don't need axes.
		setAxisXBottom(null);
		setAxisYLeft(null);
	}

	public PieChartData(PieChartData data) {
		super(data);
		this.formatter = data.formatter;
		this.hasLabels = data.hasLabels;
		this.hasLabelsOnlyForSelected = data.hasLabelsOnlyForSelected;
		this.hasLabelsOutside = data.hasLabelsOutside;

		this.hasCenterCircle = data.hasCenterCircle;
		this.centerCircleColor = data.centerCircleColor;
		this.centerCircleScale = data.centerCircleScale;

		this.centerText1Color = data.centerText1Color;
		this.centerText1FontSize = data.centerText1FontSize;
		this.centerText1Typeface = data.centerText1Typeface;
		this.centerText1 = data.centerText1;

		this.centerText2Color = data.centerText2Color;
		this.centerText2FontSize = data.centerText2FontSize;
		this.centerText2Typeface = data.centerText2Typeface;
		this.centerText2 = data.centerText2;

		for (ArcValue arcValue : data.values) {
			this.values.add(new ArcValue(arcValue));
		}
	}

	@Override
	public void update(float scale) {
		for (ArcValue value : values) {
			value.update(scale);
		}
	}

	@Override
	public void finish() {
		for (ArcValue value : values) {
			value.finish();
		}
	}

	/**
	 * PieChart does not support axes so method call will be ignored
	 */
	@Override
	public void setAxisXBottom(Axis axisX) {
		super.setAxisXBottom(null);
	}

	/**
	 * PieChart does not support axes so method call will be ignored
	 */
	@Override
	public void setAxisYLeft(Axis axisY) {
		super.setAxisYLeft(null);
	}

	public List<ArcValue> getValues() {
		return values;
	}

	public PieChartData setValues(List<ArcValue> values) {
		if (null == values) {
			this.values = new ArrayList<ArcValue>();
		} else {
			this.values = values;
		}
		return this;
	}

	public boolean hasLabels() {
		return hasLabels;
	}

	public PieChartData setHasLabels(boolean hasLabels) {
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
	public PieChartData setHasLabelsOnlyForSelected(boolean hasLabelsOnlyForSelected) {
		this.hasLabelsOnlyForSelected = hasLabelsOnlyForSelected;
		if (hasLabelsOnlyForSelected) {
			this.hasLabels = false;
		}
		return this;
	}

	public boolean hasLabelsOutside() {
		return hasLabelsOutside;
	}

	/**
	 * Set if labels should be drawn inside circle(false) or outside(true). By default false. If you set it to true you
	 * should also change chart fill ration using {@link PieChartView#setCircleFillRatio(float)}. This flag is used only
	 * if you also set hasLabels or hasLabelsOnlyForSelected flags.
	 */
	public void setHasLabelsOutside(boolean hasLabelsOutside) {
		this.hasLabelsOutside = hasLabelsOutside;
	}

	public boolean hasCenterCircle() {
		return hasCenterCircle;
	}

	public void setHasCenterCircle(boolean hasCenterCircle) {
		this.hasCenterCircle = hasCenterCircle;
	}

	public int getCenterCircleColor() {
		return centerCircleColor;
	}

	public void setCenterCircleColor(int centerCircleColor) {
		this.centerCircleColor = centerCircleColor;
	}

	public float getCenterCircleScale() {
		return centerCircleScale;
	}

	public void setCenterCircleScale(float centerCircleScale) {
		this.centerCircleScale = centerCircleScale;
	}

	public int getCenterText1Color() {
		return centerText1Color;
	}

	public void setCenterText1Color(int centerText1Color) {
		this.centerText1Color = centerText1Color;
	}

	public int getCenterText1FontSize() {
		return centerText1FontSize;
	}

	public void setCenterText1FontSize(int centerText1FontSize) {
		this.centerText1FontSize = centerText1FontSize;
	}

	public Typeface getCenterText1Typeface() {
		return centerText1Typeface;
	}

	public void setCenterText1Typeface(Typeface text1Typeface) {
		this.centerText1Typeface = text1Typeface;
	}

	public String getCenterText1() {
		return centerText1;
	}

	public void setCenterText1(String centerText1) {
		this.centerText1 = centerText1;
	}

	public String getCenterText2() {
		return centerText2;
	}

	/**
	 * Note that centerText2 will be drawn only if centerText1 is not empty/null.
	 */
	public void setCenterText2(String centerText2) {
		this.centerText2 = centerText2;
	}

	public int getCenterText2Color() {
		return centerText2Color;
	}

	public void setCenterText2Color(int centerText2Color) {
		this.centerText2Color = centerText2Color;
	}

	public int getCenterText2FontSize() {
		return centerText2FontSize;
	}

	public void setCenterText2FontSize(int centerText2FontSize) {
		this.centerText2FontSize = centerText2FontSize;
	}

	public Typeface getCenterText2Typeface() {
		return centerText2Typeface;
	}

	public void setCenterText2Typeface(Typeface text2Typeface) {
		this.centerText2Typeface = text2Typeface;
	}

	public ValueFormatter getFormatter() {
		return formatter;
	}

	public PieChartData setFormatter(ValueFormatter formatter) {
		if (null == formatter) {
			this.formatter = new SimpleValueFormatter();
		} else {
			this.formatter = formatter;
		}
		return this;
	}

	public static PieChartData generateDummyData() {
		final int numValues = 4;
		PieChartData data = new PieChartData();
		List<ArcValue> values = new ArrayList<ArcValue>(numValues);
		values.add(new ArcValue(40f));
		values.add(new ArcValue(20f));
		values.add(new ArcValue(30f));
		values.add(new ArcValue(50f));
		data.setValues(values);
		return data;
	}
}
