package lecho.lib.hellocharts.util;

public class FloatUtils {
    public static final int POW10[] = {1, 10, 100, 1000, 10000, 100000, 1000000};

    /**
     * Returns next bigger float value considering precision of the argument.
     */
    public static float nextUpF(float f) {
        if (Float.isNaN(f) || f == Float.POSITIVE_INFINITY) {
            return f;
        } else {
            f += 0.0f;
            return Float.intBitsToFloat(Float.floatToRawIntBits(f) + ((f >= 0.0f) ? +1 : -1));
        }
    }

    /**
     * Returns next smaller float value considering precision of the argument.
     */
    public static float nextDownF(float f) {
        if (Float.isNaN(f) || f == Float.NEGATIVE_INFINITY) {
            return f;
        } else {
            if (f == 0.0f) {
                return -Float.MIN_VALUE;
            } else {
                return Float.intBitsToFloat(Float.floatToRawIntBits(f) + ((f > 0.0f) ? -1 : +1));
            }
        }
    }

    /**
     * Returns next bigger double value considering precision of the argument.
     */
    public static double nextUp(double d) {
        if (Double.isNaN(d) || d == Double.POSITIVE_INFINITY) {
            return d;
        } else {
            d += 0.0;
            return Double.longBitsToDouble(Double.doubleToRawLongBits(d) + ((d >= 0.0) ? +1 : -1));
        }
    }

    /**
     * Returns next smaller float value considering precision of the argument.
     */
    public static double nextDown(double d) {
        if (Double.isNaN(d) || d == Double.NEGATIVE_INFINITY) {
            return d;
        } else {
            if (d == 0.0f) {
                return -Float.MIN_VALUE;
            } else {
                return Double.longBitsToDouble(Double.doubleToRawLongBits(d) + ((d > 0.0f) ? -1 : +1));
            }
        }
    }

    /**
     * Method checks if two float numbers are similar.
     */
    public static boolean almostEqual(float a, float b, float absoluteDiff, float relativeDiff) {
        float diff = Math.abs(a - b);
        if (diff <= absoluteDiff) {
            return true;
        }

        a = Math.abs(a);
        b = Math.abs(b);
        float largest = (a > b) ? a : b;

        if (diff <= largest * relativeDiff) {
            return true;
        }

        return false;
    }

    /**
     * Rounds the given number to the given number of significant digits. Based on an answer on <a
     * href="http://stackoverflow.com/questions/202302">Stack Overflow</a>.
     */
    public static float roundToOneSignificantFigure(double num) {
        final float d = (float) Math.ceil((float) Math.log10(num < 0 ? -num : num));
        final int power = 1 - (int) d;
        final float magnitude = (float) Math.pow(10, power);
        final long shifted = Math.round(num * magnitude);
        return shifted / magnitude;
    }

    /**
     * Formats a float value to the given number of decimals. Returns the length of the string. The string begins at
     * [endIndex] - [return value] and ends at [endIndex]. It's up to you to check indexes correctness.
     * Parameter [endIndex] can be helpful when you want to append some text to formatted value.
     *
     * @return number of characters of formatted value
     */
    public static int formatFloat(final char[] formattedValue, float value, int endIndex, int digits, char separator) {
        if (digits >= POW10.length) {
            formattedValue[endIndex - 1] = '.';
            return 1;
        }
        boolean negative = false;
        if (value == 0) {
            formattedValue[endIndex - 1] = '0';
            return 1;
        }
        if (value < 0) {
            negative = true;
            value = -value;
        }
        if (digits > POW10.length) {
            digits = POW10.length - 1;
        }
        value *= POW10[digits];
        long lval = Math.round(value);
        int index = endIndex - 1;
        int charsNumber = 0;
        while (lval != 0 || charsNumber < (digits + 1)) {
            int digit = (int) (lval % 10);
            lval = lval / 10;
            formattedValue[index--] = (char) (digit + '0');
            charsNumber++;
            if (charsNumber == digits) {
                formattedValue[index--] = separator;
                charsNumber++;
            }
        }
        if (formattedValue[index + 1] == separator) {
            formattedValue[index--] = '0';
            charsNumber++;
        }
        if (negative) {
            formattedValue[index--] = '-';
            charsNumber++;
        }
        return charsNumber;
    }

    /**
     * Computes the set of axis labels to show given start and stop boundaries and an ideal number of stops between
     * these boundaries.
     *
     * @param start     The minimum extreme (e.g. the left edge) for the axis.
     * @param stop      The maximum extreme (e.g. the right edge) for the axis.
     * @param steps     The ideal number of stops to create. This should be based on available screen space; the more
     *                  space
     *                  there is, the more stops should be shown.
     * @param outValues The destination {@link AxisAutoValues} object to populate.
     */
    public static void computeAutoGeneratedAxisValues(float start, float stop, int steps, AxisAutoValues outValues) {
        double range = stop - start;
        if (steps == 0 || range <= 0) {
            outValues.values = new float[]{};
            outValues.valuesNumber = 0;
            return;
        }

        double rawInterval = range / steps;
        double interval = roundToOneSignificantFigure(rawInterval);
        double intervalMagnitude = Math.pow(10, (int) Math.log10(interval));
        int intervalSigDigit = (int) (interval / intervalMagnitude);
        if (intervalSigDigit > 5) {
            // Use one order of magnitude higher, to avoid intervals like 0.9 or 90
            interval = Math.floor(10 * intervalMagnitude);
        }

        double first = Math.ceil(start / interval) * interval;
        double last = nextUp(Math.floor(stop / interval) * interval);

        double intervalValue;
        int valueIndex;
        int valuesNum = 0;
        for (intervalValue = first; intervalValue <= last; intervalValue += interval) {
            ++valuesNum;
        }

        outValues.valuesNumber = valuesNum;

        if (outValues.values.length < valuesNum) {
            // Ensure stops contains at least numStops elements.
            outValues.values = new float[valuesNum];
        }

        for (intervalValue = first, valueIndex = 0; valueIndex < valuesNum; intervalValue += interval, ++valueIndex) {
            outValues.values[valueIndex] = (float) intervalValue;
        }

        if (interval < 1) {
            outValues.decimals = (int) Math.ceil(-Math.log10(interval));
        } else {
            outValues.decimals = 0;
        }
    }
}
