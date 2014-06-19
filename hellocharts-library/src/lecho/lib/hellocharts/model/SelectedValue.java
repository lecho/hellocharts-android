package lecho.lib.hellocharts.model;

public class SelectedValue {
	public int firstIndex;
	public int secondIndex;

	public SelectedValue() {
		clear();
	}

	public void clear() {
		this.firstIndex = Integer.MIN_VALUE;
		this.firstIndex = Integer.MIN_VALUE;
	}

	public boolean isSet() {
		if (firstIndex >= 0 && secondIndex >= 0) {
			return true;
		} else {
			return false;
		}
	}
}
