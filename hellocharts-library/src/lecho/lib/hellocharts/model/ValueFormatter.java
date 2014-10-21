package lecho.lib.hellocharts.model;

/**
 * Interface for all value formatters used for axes and value labels. Implementations of this interface should be fast,
 * use constants, char characters etc. Avoid string operations.
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
	 * Used only for auto-generated axes. If you are not going to use your implementation for aut-generated axes you can
	 * skip implementation of this method and just return 0. </br> Formats values with given number of digits after
	 * decimal separator. Result is stored in given array. Method returns number of chars for formatted value. The
	 * formated value starts at index [formattedValue.length - nummChars] and ends at index [formatteValue.length-1].
	 */
	public int formatAutoValue(char[] formattedValue, float[] values, int digits);
}
