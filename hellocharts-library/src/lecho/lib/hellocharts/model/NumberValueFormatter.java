package lecho.lib.hellocharts.model;

import java.text.DecimalFormat;
import java.text.NumberFormat;

import lecho.lib.hellocharts.util.Utils;

/**
 * Default value formatter. Can be used for axes and for value labels. Method {@link #formatValue(char[], float, int)}
 * is used only if formatter is used for auto-generated axis. Maximum number of characters in formated value should be
 * less or equals 32.
 */
public class NumberValueFormatter implements ValueFormatter {
	protected static final int DEFAULT_DIGITS_NUMBER = 0;
	protected final int digitsNumber;
	protected final char[] appednedText;
	protected final char[] prependedText;
	protected final char separator;

	/**
	 * Creates formatter with default configuration, 0 number of digits after separator and no text appended to value.
	 */
	public NumberValueFormatter() {
		this(DEFAULT_DIGITS_NUMBER, new char[0], new char[0]);
	}

	/**
	 * Creates formatter with given number of digits after decimal separator.
	 */
	public NumberValueFormatter(int digitsNumber) {
		this(digitsNumber, new char[0], new char[0]);
	}

	/**
	 * Creates formatter with given number of digits after decimal separator and with text prepended and appended to
	 * formated value.
	 */
	public NumberValueFormatter(int digitsNumber, char[] prependedText, char[] appendedText) {
		this.digitsNumber = digitsNumber;

		if (null == prependedText) {
			this.prependedText = new char[0];
		} else {
			this.prependedText = prependedText;
		}

		if (null == appendedText) {
			this.appednedText = new char[0];
		} else {
			this.appednedText = appendedText;
		}

		// Get decimal point separator for default locale.
		NumberFormat numberFormat = NumberFormat.getInstance();
		if (numberFormat instanceof DecimalFormat) {
			separator = ((DecimalFormat) numberFormat).getDecimalFormatSymbols().getDecimalSeparator();
		} else {
			separator = '.';
		}
	}

	@Override
	public int formatValue(char[] formattedValue, float value, int digits) {

		final int numChars = Utils.formatFloat(formattedValue, value, formattedValue.length - appednedText.length,
				digits, separator);

		System.arraycopy(prependedText, 0, formattedValue, formattedValue.length - numChars - prependedText.length
				- appednedText.length, appednedText.length);

		System.arraycopy(appednedText, 0, formattedValue, formattedValue.length - appednedText.length,
				appednedText.length);

		return numChars + prependedText.length + appednedText.length;
	}

	@Override
	public int formatValue(char[] formattedValue, float value) {
		return formatValue(formattedValue, value, digitsNumber);
	}

}
