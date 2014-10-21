package lecho.lib.hellocharts.model;

import lecho.lib.hellocharts.util.Utils;
import lecho.lib.hellocharts.view.Chart;

/**
 * Single value drawn as bubble on BubbleChart.
 * 
 */
public class BubbleValue {

	/** Current X value. */
	private float x;
	/** Current Y value. */
	private float y;
	/** Current Z value , third bubble value interpreted as bubble area. */
	private float z;

	/** Origin X value, used during value animation. */
	private float orginX;
	/** Origin Y value, used during value animation. */
	private float orginY;
	/** Origin Z value, used during value animation. */
	private float orginZ;

	/** Difference between originX value and target X value. */
	private float diffX;

	/** Difference between originX value and target X value. */
	private float diffY;

	/** Difference between originX value and target X value. */
	private float diffZ;
	private int color = Utils.DEFAULT_COLOR;
	private int darkenColor = Utils.DEFAULT_DARKEN_COLOR;
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
		x = orginX + diffX * scale;
		y = orginY + diffY * scale;
		z = orginZ + diffZ * scale;
	}

	public void finish() {
		set(orginX + diffX, orginY + diffY, orginZ + diffZ);
	}

	public BubbleValue set(float x, float y, float z) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.orginX = x;
		this.orginY = y;
		this.orginZ = z;
		this.diffX = 0;
		this.diffY = 0;
		this.diffZ = 0;
		return this;
	}

	/**
	 * Set target values that should be reached when data animation finish then call {@link Chart#startDataAnimation()}
	 * 
	 * @param target
	 * @return
	 */
	public BubbleValue setTarget(float targetX, float targetY, float targetZ) {
		set(x, y, z);
		this.diffX = targetX - orginX;
		this.diffY = targetY - orginY;
		this.diffZ = targetZ - orginZ;
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
		this.darkenColor = Utils.darkenColor(color);
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

	public char[] getLabel() {
		return label;
	}

	public BubbleValue setLabel(char[] label) {
		this.label = label;
		return this;
	}

	@Override
	public String toString() {
		return "BubbleValue [x=" + x + ", y=" + y + ", z=" + z + "]";
	}

}
