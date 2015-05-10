package lecho.lib.hellocharts.model;

import java.util.Arrays;

import lecho.lib.hellocharts.util.ChartUtils;
import lecho.lib.hellocharts.view.Chart;

/**
 * Single value drawn as bubble on BubbleChart.
 */
public class BubbleValue {

    /**
     * Current X value.
     */
    private float x;
    /**
     * Current Y value.
     */
    private float y;
    /**
     * Current Z value , third bubble value interpreted as bubble area.
     */
    private float z;

    /**
     * Origin X value, used during value animation.
     */
    private float originX;
    /**
     * Origin Y value, used during value animation.
     */
    private float originY;
    /**
     * Origin Z value, used during value animation.
     */
    private float originZ;

    /**
     * Difference between originX value and target X value.
     */
    private float diffX;

    /**
     * Difference between originX value and target X value.
     */
    private float diffY;

    /**
     * Difference between originX value and target X value.
     */
    private float diffZ;
    private int color = ChartUtils.DEFAULT_COLOR;
    private int darkenColor = ChartUtils.DEFAULT_DARKEN_COLOR;
    private ValueShape shape = ValueShape.CIRCLE;
    private char[] label;

    public BubbleValue() {
        set(0, 0, 0);
    }

    public BubbleValue(float x, float y, float z) {
        set(x, y, z);
    }

    public BubbleValue(float x, float y, float z, int color) {
        set(x, y, z);
        setColor(color);
    }

    public BubbleValue(BubbleValue bubbleValue) {
        set(bubbleValue.x, bubbleValue.y, bubbleValue.z);
        setColor(bubbleValue.color);
        this.label = bubbleValue.label;
    }

    public void update(float scale) {
        x = originX + diffX * scale;
        y = originY + diffY * scale;
        z = originZ + diffZ * scale;
    }

    public void finish() {
        set(originX + diffX, originY + diffY, originZ + diffZ);
    }

    public BubbleValue set(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.originX = x;
        this.originY = y;
        this.originZ = z;
        this.diffX = 0;
        this.diffY = 0;
        this.diffZ = 0;
        return this;
    }

    /**
     * Set target values that should be reached when data animation finish then call {@link Chart#startDataAnimation()}
     */
    public BubbleValue setTarget(float targetX, float targetY, float targetZ) {
        set(x, y, z);
        this.diffX = targetX - originX;
        this.diffY = targetY - originY;
        this.diffZ = targetZ - originZ;
        return this;
    }

    public float getX() {
        return this.x;
    }

    public float getY() {
        return this.y;
    }

    public float getZ() {
        return this.z;
    }

    public int getColor() {
        return color;
    }

    public BubbleValue setColor(int color) {
        this.color = color;
        this.darkenColor = ChartUtils.darkenColor(color);
        return this;
    }

    public int getDarkenColor() {
        return darkenColor;
    }

    public ValueShape getShape() {
        return shape;
    }

    public BubbleValue setShape(ValueShape shape) {
        this.shape = shape;
        return this;
    }

    @Deprecated
    public char[] getLabel() {
        return label;
    }

    public BubbleValue setLabel(String label) {
        this.label = label.toCharArray();
        return this;
    }

    public char[] getLabelAsChars() {
        return label;
    }

    @Deprecated
    public BubbleValue setLabel(char[] label) {
        this.label = label;
        return this;
    }

    @Override
    public String toString() {
        return "BubbleValue [x=" + x + ", y=" + y + ", z=" + z + "]";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BubbleValue that = (BubbleValue) o;

        if (color != that.color) return false;
        if (darkenColor != that.darkenColor) return false;
        if (Float.compare(that.diffX, diffX) != 0) return false;
        if (Float.compare(that.diffY, diffY) != 0) return false;
        if (Float.compare(that.diffZ, diffZ) != 0) return false;
        if (Float.compare(that.originX, originX) != 0) return false;
        if (Float.compare(that.originY, originY) != 0) return false;
        if (Float.compare(that.originZ, originZ) != 0) return false;
        if (Float.compare(that.x, x) != 0) return false;
        if (Float.compare(that.y, y) != 0) return false;
        if (Float.compare(that.z, z) != 0) return false;
        if (!Arrays.equals(label, that.label)) return false;
        if (shape != that.shape) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = (x != +0.0f ? Float.floatToIntBits(x) : 0);
        result = 31 * result + (y != +0.0f ? Float.floatToIntBits(y) : 0);
        result = 31 * result + (z != +0.0f ? Float.floatToIntBits(z) : 0);
        result = 31 * result + (originX != +0.0f ? Float.floatToIntBits(originX) : 0);
        result = 31 * result + (originY != +0.0f ? Float.floatToIntBits(originY) : 0);
        result = 31 * result + (originZ != +0.0f ? Float.floatToIntBits(originZ) : 0);
        result = 31 * result + (diffX != +0.0f ? Float.floatToIntBits(diffX) : 0);
        result = 31 * result + (diffY != +0.0f ? Float.floatToIntBits(diffY) : 0);
        result = 31 * result + (diffZ != +0.0f ? Float.floatToIntBits(diffZ) : 0);
        result = 31 * result + color;
        result = 31 * result + darkenColor;
        result = 31 * result + (shape != null ? shape.hashCode() : 0);
        result = 31 * result + (label != null ? Arrays.hashCode(label) : 0);
        return result;
    }
}
