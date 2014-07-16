package lecho.lib.hellocharts.model;

import lecho.lib.hellocharts.util.Utils;

public class NumberValueFormatter implements ValueFormatter {
	protected static final int DEFAULT_DIGITS_NUMBER = 1;
	protected int digitsNumber;
	protected char[] appednedText;

	public NumberValueFormatter() {
		digitsNumber = DEFAULT_DIGITS_NUMBER;
	}

	public NumberValueFormatter(int digitsNumber) {
		this.digitsNumber = digitsNumber;
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
			return numChars;
		}
	}

	public int getDigitsNumber() {
		return digitsNumber;
	}

	public char[] getAppednedText() {
		return appednedText;
	}

	public void setAppednedText(char[] appednedText) {
		this.appednedText = appednedText;
	}

}
