package lecho.lib.hellocharts.formatter;

import java.text.DecimalFormat;
import java.text.NumberFormat;

import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.util.Utils;

public abstract class AbstractValueFormatter {
	protected static final int DEFAULT_DIGITS_NUMBER = 0;

	private int decimalDigitsNumber = DEFAULT_DIGITS_NUMBER;
	private char[] appendedText = new char[0];
	private char[] prependedText = new char[0];
	private char decimalSeparator = '.';

	public AbstractValueFormatter() {
		NumberFormat numberFormat = NumberFormat.getInstance();
		if (numberFormat instanceof DecimalFormat) {
			decimalSeparator = ((DecimalFormat) numberFormat).getDecimalFormatSymbols().getDecimalSeparator();
		}
	}

	public int getDecimalDigitsNumber() {
		return decimalDigitsNumber;
	}

	/**
	 * Sets number of digits after comma, used only for manual axes, this value will not be used for auto-generated axes.
	 */
	public AbstractValueFormatter setDecimalDigitsNumber(int decimalDigitsNumber) {
		this.decimalDigitsNumber = decimalDigitsNumber;
		return this;
	}

	public char[] getAppendedText() {
		return appendedText;
	}

	public AbstractValueFormatter setAppendedText(char[] appendedText) {
		if (null != appendedText) {
			this.appendedText = appendedText;
		}
		return this;
	}

	public char[] getPrependedText() {
		return prependedText;
	}

	public AbstractValueFormatter setPrependedText(char[] prependedText) {
		if (null != prependedText) {
			this.prependedText = prependedText;
		}
		return this;
	}

	public char getDecimalSeparator() {
		return decimalSeparator;
	}

	public AbstractValueFormatter setDecimalSeparator(char decimalSeparator) {
		char nullChar = '\0';
		if (nullChar != decimalSeparator) {
			this.decimalSeparator = decimalSeparator;
		}
		return this;
	}

	/**
	 * Formats float value. Result is stored in (output) formattedValue array. Method
	 * returns number of chars of formatted value. The formatted value starts at index [formattedValue.length -
	 * charsNumber] and ends at index [formattedValue.length-1].
	 *
	 * If label is not null it will be used as formattedValue instead of float value.
	 */
	protected int formatFloatValueWithPrependedAndAppendedText(char[] formattedValue, float value, char[] label) {
		if (null != label) {
			// If custom label is not null use only name characters as formatted value.
			// Copy label into formatted value array.
			System.arraycopy(label, 0, formattedValue, formattedValue.length - label.length, label.length);
			return label.length;
		}

		final int charsNumber = formatFloatValue(formattedValue, value);
		appendText(formattedValue);
		prependText(formattedValue, charsNumber);
		return charsNumber + getPrependedText().length + getAppendedText().length;
	}

	protected int formatFloatValue(char[] formattedValue, float value) {
		return Utils.formatFloat(formattedValue, value, formattedValue.length - appendedText.length, decimalDigitsNumber,
				decimalSeparator);
	}

	protected void appendText(char[] formattedValue) {
		if (appendedText.length > 0) {
			System.arraycopy(appendedText, 0, formattedValue, formattedValue.length - appendedText.length,
					appendedText.length);
		}
	}

	protected void prependText(char[] formattedValue, int charsNumber) {
		if (prependedText.length > 0) {
			System.arraycopy(prependedText, 0, formattedValue, formattedValue.length - charsNumber - appendedText.length
					- prependedText.length, prependedText.length);
		}
	}

}
