package lecho.lib.hellocharts.model;

import java.text.DecimalFormat;
import java.text.NumberFormat;

import lecho.lib.hellocharts.util.Utils;

/**
 * Default value formatter. Can be used for axes and for value labels. Method
 * {@link #formatValue(char[], float, char[], int)} is used only if formatter is used for auto-generated axis. Note:
 * Maximum number of characters in formated value should be less or equals 32 so be careful with appended text length.
 * Note2: this formatter skips value formating if label is not null, in that case it will return label, not formatted
 * number value. Note3: only last value from values array is formated, for LineChart that is Y value, for BubbleChart
 * that is Z value.
 * 
 */
public class SimpleValueFormatter implements ValueFormatter {
	protected static final int DEFAULT_DIGITS_NUMBER = 0;
	protected final int digitsNumber;
	protected final char[] appendedText;
	protected final char[] prependedText;
	protected final char separator;
	protected boolean manualDigitsForAutoAxes = false;

	/**
	 * Creates formatter with default configuration, 0 number of digits after separator and no text appended to value.
	 */
	public SimpleValueFormatter() {
		this(DEFAULT_DIGITS_NUMBER, false, null, null);
	}

	/**
	 * Creates formatter with given number of digits after decimal separator.
	 */
	public SimpleValueFormatter(int digitsNumber) {
		this(digitsNumber, false, null, null);
	}

	/**
	 * Creates formatter with given number of digits after decimal separator and with text prepended and appended to
	 * formated value.
	 */
	public SimpleValueFormatter(int digitsNumber, boolean manualDigitsForAutoAxes, char[] prependedText,
			char[] appendedText) {
		this.digitsNumber = digitsNumber;
		this.manualDigitsForAutoAxes = manualDigitsForAutoAxes;

		if (null == prependedText) {
			this.prependedText = new char[0];
		} else {
			this.prependedText = prependedText;
		}

		if (null == appendedText) {
			this.appendedText = new char[0];
		} else {
			this.appendedText = appendedText;
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
	public int formatValue(char[] formattedValue, float[] values, char[] label) {

		return format(formattedValue, values, label, digitsNumber);

	}

	@Override
	public int formatAutoValue(char[] formattedValue, float[] values, int digits) {

		return format(formattedValue, values, null, digits);

	}

	private int format(char[] formattedValue, float[] values, char[] label, int digits) {
		if (manualDigitsForAutoAxes) {
			digits = digitsNumber;
		}

		if (null != label) {
			// If custom label is not null use only name characters as formatted value.
			// Copy label into formatted value array.
			System.arraycopy(label, 0, formattedValue, formattedValue.length - label.length, label.length);
			return label.length;
		}

		if (null == values || values.length == 0) {
			return 0;
		}

		// Manual label is null so format value as number.
		// Format only last value, in most cases that is enough.
		float value = values[values.length - 1];

		final int numChars = Utils.formatFloat(formattedValue, value, formattedValue.length - appendedText.length,
				digits, separator);

		if (prependedText.length > 0) {
			System.arraycopy(prependedText, 0, formattedValue, formattedValue.length - numChars - appendedText.length
					- prependedText.length, prependedText.length);
		}

		if (appendedText.length > 0) {
			System.arraycopy(appendedText, 0, formattedValue, formattedValue.length - appendedText.length,
					appendedText.length);
		}

		return numChars + prependedText.length + appendedText.length;
	}
}
