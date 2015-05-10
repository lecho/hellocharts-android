package lecho.lib.hellocharts.formatter;

import lecho.lib.hellocharts.model.BubbleValue;


public class SimpleBubbleChartValueFormatter implements BubbleChartValueFormatter {

    private ValueFormatterHelper valueFormatterHelper = new ValueFormatterHelper();

    public SimpleBubbleChartValueFormatter() {
        valueFormatterHelper.determineDecimalSeparator();
    }

    public SimpleBubbleChartValueFormatter(int decimalDigitsNumber) {
        this();
        valueFormatterHelper.setDecimalDigitsNumber(decimalDigitsNumber);
    }

    @Override
    public int formatChartValue(char[] formattedValue, BubbleValue value) {
        return valueFormatterHelper.formatFloatValueWithPrependedAndAppendedText(formattedValue, value.getZ(), value
                .getLabelAsChars());
    }

    public int getDecimalDigitsNumber() {
        return valueFormatterHelper.getDecimalDigitsNumber();
    }

    public SimpleBubbleChartValueFormatter setDecimalDigitsNumber(int decimalDigitsNumber) {
        valueFormatterHelper.setDecimalDigitsNumber(decimalDigitsNumber);
        return this;
    }

    public char[] getAppendedText() {
        return valueFormatterHelper.getAppendedText();
    }

    public SimpleBubbleChartValueFormatter setAppendedText(char[] appendedText) {
        valueFormatterHelper.setAppendedText(appendedText);
        return this;
    }

    public char[] getPrependedText() {
        return valueFormatterHelper.getPrependedText();
    }

    public SimpleBubbleChartValueFormatter setPrependedText(char[] prependedText) {
        valueFormatterHelper.setPrependedText(prependedText);
        return this;
    }

    public char getDecimalSeparator() {
        return valueFormatterHelper.getDecimalSeparator();
    }

    public SimpleBubbleChartValueFormatter setDecimalSeparator(char decimalSeparator) {
        valueFormatterHelper.setDecimalSeparator(decimalSeparator);
        return this;
    }
}
