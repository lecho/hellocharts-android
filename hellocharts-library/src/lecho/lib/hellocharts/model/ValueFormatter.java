package lecho.lib.hellocharts.model;

/**
 * Interface for all value formatters used for axes and value labels.
 * 
 * @author Leszek Wach
 * 
 */
public interface ValueFormatter {

	/**
	 * Formats float with given number of digits after decimal separator.
	 */
	public int formatValue(final char[] formattedValue, float value, int digits);

	/**
	 * Returns float value formatted as char array.
	 */
	public int formatValue(final char[] formattedValue, float value);

}
