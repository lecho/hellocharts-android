package lecho.lib.hellocharts.model;

import lecho.lib.hellocharts.util.Utils;

/**
 * Single value drawn as bubble on BubbleChart.
 * 
 * @author Leszek Wach
 * 
 */
public class BubbleValue {
	public static final int SHAPE_CIRCLE = 1;
	public static final int SHAPE_SQUARE = 2;
	private float x;
	private float y;
	private float z;
	private float orginX;
	private float orginY;
	private float orginZ;
	private float diffX;
	private float diffY;
	private float diffZ;
	private int color = Utils.DEFAULT_COLOR;
	private int darkenColor = Utils.DEFAULT_DARKEN_COLOR;
	private int shape = SHAPE_CIRCLE;

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
	}

	public void update(float scale) {
		x = orginX + diffX * scale;
		y = orginY + diffY * scale;
		z = orginZ + diffZ * scale;
	}

	public void finish(boolean isFinishedSuccess) {
		if (isFinishedSuccess) {
			set(orginX + diffX, orginY + diffY, orginZ + diffZ);
		} else {
			set(x, y, z);
		}
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

	public int getShape() {
		return shape;
	}

	public BubbleValue setShape(int shape) {
		if (SHAPE_SQUARE == shape) {
			this.shape = SHAPE_SQUARE;
		} else {
			this.shape = SHAPE_CIRCLE;
		}
		return this;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Float.floatToIntBits(x);
		result = prime * result + Float.floatToIntBits(y);
		result = prime * result + Float.floatToIntBits(z);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		BubbleValue other = (BubbleValue) obj;
		if (Float.floatToIntBits(x) != Float.floatToIntBits(other.x))
			return false;
		if (Float.floatToIntBits(y) != Float.floatToIntBits(other.y))
			return false;
		if (Float.floatToIntBits(z) != Float.floatToIntBits(other.z))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "BubbleValue [x=" + x + ", y=" + y + ", z=" + z + "]";
	}

}
