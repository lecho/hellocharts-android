package lecho.lib.hellocharts.model;

import android.graphics.drawable.Drawable;

import java.util.Arrays;

import lecho.lib.hellocharts.util.ChartUtils;
import lecho.lib.hellocharts.view.Chart;

/**
 * Single sub-column value for ColumnChart.
 */
public class SubcolumnValue {

    private float value;
    private float originValue;
    private float diff;
    private int color = ChartUtils.DEFAULT_COLOR;
    private int darkenColor = ChartUtils.DEFAULT_DARKEN_COLOR;
    private char[] label;
    private Drawable picture;
    private float subcolumnWidhtRatio = 1; // 1 means that the subcolumn widht equals the other, 0.5 means that the widht is the half of the others

    public SubcolumnValue() {
        setValue(0);
    }

    public SubcolumnValue(float value) {
        // point and targetPoint have to be different objects
        setValue(value);
    }

    public SubcolumnValue(float value, int color) {
        // point and targetPoint have to be different objects
        setValue(value);
        setColor(color);
    }

    public SubcolumnValue(float value, int color, Drawable picture) {
        // point and targetPoint have to be different objects
        setValue(value);
        setColor(color);
        setPicture(picture);
    }

    public SubcolumnValue(float value, int color, Drawable picture, float subcolumnWidhtRatio) {
        // point and targetPoint have to be different objects
        setValue(value);
        setColor(color);
        setPicture(picture);
        setSubcolumnWidhtRatio(subcolumnWidhtRatio);
    }

    public SubcolumnValue(SubcolumnValue columnValue) {
        setValue(columnValue.value);
        setColor(columnValue.color);
        setPicture(columnValue.picture);
        setSubcolumnWidhtRatio(subcolumnWidhtRatio);
        this.label = columnValue.label;
    }

    public void update(float scale) {
        value = originValue + diff * scale;
    }

    public void finish() {
        setValue(originValue + diff);
    }

    public float getValue() {
        return value;
    }

    public SubcolumnValue setValue(float value) {
        this.value = value;
        this.originValue = value;
        this.diff = 0;
        return this;
    }

    /**
     * Set target value that should be reached when data animation finish then call {@link Chart#startDataAnimation()}
     *
     * @param target
     * @return
     */
    public SubcolumnValue setTarget(float target) {
        setValue(value);
        this.diff = target - originValue;
        return this;
    }

    public int getColor() {
        return color;
    }

    public SubcolumnValue setColor(int color) {
        this.color = color;
        this.darkenColor = ChartUtils.darkenColor(color);
        return this;
    }

    public int getDarkenColor() {
        return darkenColor;
    }

    @Deprecated
    public char[] getLabel() {
        return label;
    }

    public SubcolumnValue setLabel(String label) {
        this.label = label.toCharArray();
        return this;
    }

    public char[] getLabelAsChars() {
        return label;
    }

    @Deprecated
    public SubcolumnValue setLabel(char[] label) {
        this.label = label;
        return this;
    }

    @Override
    public String toString() {
        return "ColumnValue [value=" + value + "]";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SubcolumnValue that = (SubcolumnValue) o;

        if (color != that.color) return false;
        if (darkenColor != that.darkenColor) return false;
        if (Float.compare(that.diff, diff) != 0) return false;
        if (Float.compare(that.originValue, originValue) != 0) return false;
        if (Float.compare(that.value, value) != 0) return false;
        if (!Arrays.equals(label, that.label)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = (value != +0.0f ? Float.floatToIntBits(value) : 0);
        result = 31 * result + (originValue != +0.0f ? Float.floatToIntBits(originValue) : 0);
        result = 31 * result + (diff != +0.0f ? Float.floatToIntBits(diff) : 0);
        result = 31 * result + color;
        result = 31 * result + darkenColor;
        result = 31 * result + (label != null ? Arrays.hashCode(label) : 0);
        result = 31 * result + (picture != null ? picture.hashCode() : 0);
        return result;
    }

    public Drawable getPicture() {
        return picture;
    }

    public void setPicture(Drawable picture) {
        this.picture = picture;
    }

    public float getSubcolumnWidhtRatio() {
        return subcolumnWidhtRatio;
    }

    public void setSubcolumnWidhtRatio(float subcolumnWidhtRatio) {
        this.subcolumnWidhtRatio = subcolumnWidhtRatio;
    }
}
