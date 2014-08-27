package lecho.lib.hellocharts.model;

/**
 * Holds selected values indexes, i.e. for LineChartModel it will be firstIndex=lineIndex; secondIndex=valueIndex.
 * 
 * @author Leszek Wach
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
	 * Used only for combo charts i.e 1 means user selected LinePoint, 2 means user selected ColumnValue, this attribute
	 * is not used for checking if selectedValue is set.
	 */
	private int dataType;

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
		this.firstIndex = selectedValue.getFirstIndex();
		this.secondIndex = selectedValue.getSecondIndex();
	}

	public void clear() {
		set(Integer.MIN_VALUE, Integer.MIN_VALUE);
		this.dataType = Integer.MIN_VALUE;
	}

	public boolean isSet() {
		if (getFirstIndex() >= 0 && getSecondIndex() >= 0) {
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
	 * Used only for combo charts i.e 1 means user selected LinePoint, 2 means user selected ColumnValue, this attribute
	 * is not used for checking if selectedValue is set.
	 */
	public int getDataType() {
		return dataType;
	}

	public void setDataType(int dataType) {
		this.dataType = dataType;
	}

}
