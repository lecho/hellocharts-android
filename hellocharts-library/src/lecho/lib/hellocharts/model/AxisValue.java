package lecho.lib.hellocharts.model;

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