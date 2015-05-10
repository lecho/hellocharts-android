package lecho.lib.hellocharts.formatter;

import lecho.lib.hellocharts.model.PointValue;

public class SimpleLineChartValueFormatter implements LineChartValueFormatter {

    private ValueFormatterHelper valueFormatterHelper = new ValueFormatterHelper();

    public SimpleLineChartValueFormatter() {
        valueFormatterHelper.determineDecimalSeparator();
    }

    public SimpleLineChartValueFormatter(int decimalDigitsNumber) {
        this();
        valueFormatterHelper.setDecimalDigitsNumber(decimalDigitsNumber);
    }

    @Override
    public int formatChartValue(char[] formattedValue, PointValue value) {
        return valueFormatterHelper.formatFloatValueWithPrependedAndAppendedText(formattedValue, value.getY(), value
                .getLabelAsChars());
    }

    public int getDecimalDigitsNumber() {
        return valueFormatterHelper.getDecimalDigitsNumber();
    }

    public SimpleLineChartValueFormatter setDecimalDigitsNumber(int decimalDigitsNumber) {
        valueFormatterHelper.setDecimalDigitsNumber(decimalDigitsNumber);
        return this;
    }

    public char[] getAppendedText() {
        return valueFormatterHelper.getAppendedText();
    }

    public SimpleLineChartValueFormatter setAppendedText(char[] appendedText) {
        valueFormatterHelper.setAppendedText(appendedText);
        return this;
    }

    public char[] getPrependedText() {
        return valueFormatterHelper.getPrependedText();
    }

    public SimpleLineChartValueFormatter setPrependedText(char[] prependedText) {
        valueFormatterHelper.setPrependedText(prependedText);
        return this;
    }

    public char getDecimalSeparator() {
        return valueFormatterHelper.getDecimalSeparator();
    }

    public SimpleLineChartValueFormatter setDecimalSeparator(char decimalSeparator) {
        valueFormatterHelper.setDecimalSeparator(decimalSeparator);
        return this;
    }
}
