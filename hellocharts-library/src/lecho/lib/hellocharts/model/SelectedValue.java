package lecho.lib.hellocharts.model;

public class SelectedValue {
	public int firstIndex;
	public int secondIndex;

	public SelectedValue() {
		clear();
	}

	public SelectedValue(int firstIndex, int secondIndex) {
		set(firstIndex, secondIndex);
	}

	public void set(int firstIndex, int secondIndex) {
		this.firstIndex = firstIndex;
		this.secondIndex = secondIndex;
	}

	public void set(SelectedValue selectedValue) {
		this.firstIndex = selectedValue.firstIndex;
		this.secondIndex = selectedValue.secondIndex;
	}

	public void clear() {
		this.firstIndex = Integer.MIN_VALUE;
		this.secondIndex = Integer.MIN_VALUE;
	}

	public boolean isSet() {
		if (firstIndex >= 0 && secondIndex >= 0) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + firstIndex;
		result = prime * result + secondIndex;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SelectedValue other = (SelectedValue) obj;
		if (firstIndex != other.firstIndex)
			return false;
		if (secondIndex != other.secondIndex)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "SelectedValue [firstIndex=" + firstIndex + ", secondIndex=" + secondIndex + "]";
	}

}
