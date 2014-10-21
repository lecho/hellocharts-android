package lecho.lib.hellocharts.model;

/**
 * Holds selected values indexes, i.e. for LineChartModel it will be firstIndex=lineIndex; secondIndex=valueIndex.
 * 
 */
public class SelectedValue {

	/**
	 * First index i.e for LineChart that will be line index.
	 */
	private int firstIndex;

	/**
	 * Second index i.e for LineChart that will be PointValue index.
	 */
	private int secondIndex;

	/**
	 * Used only for combo charts i.e 1 means user selected LinePoint, 2 means user selected ColumnValue.
	 */
	private int thirdIndex;

	public SelectedValue() {
		clear();
	}

	public SelectedValue(int firstIndex, int secondIndex, int dataType) {
		set(firstIndex, secondIndex, dataType);
	}

	public void set(int firstIndex, int secondIndex, int third) {
		this.firstIndex = firstIndex;
		this.secondIndex = secondIndex;
		this.thirdIndex = third;
	}

	public void set(SelectedValue selectedValue) {
		this.firstIndex = selectedValue.firstIndex;
		this.secondIndex = selectedValue.secondIndex;
		this.thirdIndex = selectedValue.thirdIndex;
	}

	public void clear() {
		set(Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE);
	}

	/**
	 * Return true if selected value have meaningful value.
	 */
	public boolean isSet() {
		if (firstIndex >= 0 && secondIndex >= 0 && thirdIndex >= 0) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * First index i.e for LineChart that will be line index.
	 */
	public int getFirstIndex() {
		return firstIndex;
	}

	public void setFirstIndex(int firstIndex) {
		this.firstIndex = firstIndex;
	}

	/**
	 * Second index i.e for LineChart that will be PointValue index.
	 */
	public int getSecondIndex() {
		return secondIndex;
	}

	public void setSecondIndex(int secondIndex) {
		this.secondIndex = secondIndex;
	}

	/**
	 * Used only for combo charts i.e 1 means user selected LinePoint, 2 means user selected ColumnValue,.
	 */
	public int getThirdIndex() {
		return thirdIndex;
	}

	public void setThirdIndex(int thirdIndex) {
		this.thirdIndex = thirdIndex;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + thirdIndex;
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
		if (thirdIndex != other.thirdIndex)
			return false;
		if (firstIndex != other.firstIndex)
			return false;
		if (secondIndex != other.secondIndex)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "SelectedValue [firstIndex=" + firstIndex + ", secondIndex=" + secondIndex + ", thirdIndex="
				+ thirdIndex + "]";
	}

}
