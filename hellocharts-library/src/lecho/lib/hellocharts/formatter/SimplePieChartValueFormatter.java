package lecho.lib.hellocharts.formatter;

import lecho.lib.hellocharts.model.SliceValue;


public class SimplePieChartValueFormatter implements PieChartValueFormatter {

    private ValueFormatterHelper valueFormatterHelper = new ValueFormatterHelper();

    public SimplePieChartValueFormatter() {
        valueFormatterHelper.determineDecimalSeparator();
    }

    public SimplePieChartValueFormatter(int decimalDigitsNumber) {
        this();
        valueFormatterHelper.setDecimalDigitsNumber(decimalDigitsNumber);
    }

    @Override
    public int formatChartValue(char[] formattedValue, SliceValue value) {
        return valueFormatterHelper.formatFloatValueWithPrependedAndAppendedText(formattedValue, value.getValue(),
                value.getLabelAsChars());
    }

    public int getDecimalDigitsNumber() {
        return valueFormatterHelper.getDecimalDigitsNumber();
    }

    public SimplePieChartValueFormatter setDecimalDigitsNumber(int decimalDigitsNumber) {
        valueFormatterHelper.setDecimalDigitsNumber(decimalDigitsNumber);
        return this;
    }

    public char[] getAppendedText() {
        return valueFormatterHelper.getAppendedText();
    }

    public SimplePieChartValueFormatter setAppendedText(char[] appendedText) {
        valueFormatterHelper.setAppendedText(appendedText);
        return this;
    }

    public char[] getPrependedText() {
        return valueFormatterHelper.getPrependedText();
    }

    public SimplePieChartValueFormatter setPrependedText(char[] prependedText) {
        valueFormatterHelper.setPrependedText(prependedText);
        return this;
    }

    public char getDecimalSeparator() {
        return valueFormatterHelper.getDecimalSeparator();
    }

    public SimplePieChartValueFormatter setDecimalSeparator(char decimalSeparator) {
        valueFormatterHelper.setDecimalSeparator(decimalSeparator);
        return this;
    }
}
