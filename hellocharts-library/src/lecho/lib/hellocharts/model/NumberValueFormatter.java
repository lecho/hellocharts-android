package lecho.lib.hellocharts.model;

import lecho.lib.hellocharts.util.Utils;

public class NumberValueFormatter implements ValueFormatter {
	protected static final int DEFAULT_DIGITS_NUMBER = 0;
	protected final int digitsNumber;
	protected final char[] appednedText;

	public NumberValueFormatter() {
		digitsNumber = DEFAULT_DIGITS_NUMBER;
		appednedText = null;
	}

	public NumberValueFormatter(int digitsNumber) {
		this.digitsNumber = digitsNumber;
		this.appednedText = null;
	}

	public NumberValueFormatter(int digitsNumber, char[] appendedText) {
		this.digitsNumber = digitsNumber;
		this.appednedText = appendedText;
	}

	@Override
	public int formatValue(char[] formattedValue, float value, int digits) {
		if (null == appednedText) {
			return Utils.formatFloat(formattedValue, value, formattedValue.length, digits);
		} else {
			int numChars = Utils
					.formatFloat(formattedValue, value, formattedValue.length - appednedText.length, digits);
			System.arraycopy(appednedText, 0, formattedValue, formattedValue.length - appednedText.length,
					appednedText.length);
			return numChars + appednedText.length;
		}
	}

	@Override
	public int formatValue(char[] formattedValue, float value) {
		return formatValue(formattedValue, value, digitsNumber);
	}

}
