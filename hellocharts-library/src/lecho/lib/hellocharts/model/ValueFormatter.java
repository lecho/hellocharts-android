package lecho.lib.hellocharts.model;

/**
 * Interface for all value formatters used for axes and value labels. Implementations of this interface should be fast,
 * use constants, char characters etc. Avoid string operations.
 * 
 * @author Leszek Wach
 * 
 */
public interface ValueFormatter {

	/**
	 * Formats values with given number of digits after decimal separator. Result is stored in given array. Method
	 * returns number of chars for formatted value. The formated value starts at index [formattedValue.length -
	 * nummChars] and ends at index [formatteValue.length-1].
	 */
	public int formatValue(char[] formattedValue, float[] values, char[] label);

	/**
	 * Used mostly for auto-generated axes. Formats values with given number of digits after decimal separator. Result
	 * is stored in given array. Method returns number of chars for formatted value. The formated value starts at index
	 * [formattedValue.length - nummChars] and ends at index [formatteValue.length-1].
	 */
	public int formatValue(char[] formattedValue, float[] values, char[] label, int digits);
}
