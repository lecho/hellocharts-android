package lecho.lib.hellocharts.model;

/**
 * Single axis value, Use it to manually set axis labels position. Warning! valueName is not used for now.
 * 
 * @author Leszek Wach
 */
public class AxisValue {
	private float value;
	private String valueName;

	public AxisValue(float value) {
		this.value = value;
	}

	public AxisValue(float value, String valueName) {
		this.value = value;
		this.valueName = valueName;
	}

	public AxisValue(AxisValue axisValue) {
		this.value = axisValue.value;
		this.valueName = axisValue.valueName;
	}

	public float getValue() {
		return value;
	}

	public AxisValue setValue(float value) {
		this.value = value;
		return this;
	}

	public String getValueName() {
		return valueName;
	}

	public AxisValue setValueName(String valueName) {
		this.valueName = valueName;
		return this;
	}
}