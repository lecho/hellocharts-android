package lecho.lib.hellocharts.model;

/**
 * Single point coordinates.
 * 
 * @author Leszek Wach
 * 
 */
public class PointValue {

	private float x;
	private float y;
	private float orginX;
	private float orginY;
	private float diffX;
	private float diffY;

	public PointValue() {
		set(0, 0);
	}

	public PointValue(float x, float y) {
		set(x, y);
	}

	public PointValue(PointValue pointValue) {
		set(pointValue.x, pointValue.y);
	}

	public void update(float scale) {
		x = orginX + diffX * scale;
		y = orginY + diffY * scale;
	}

	public void finish(boolean isSuccess) {
		if (isSuccess) {
			set(orginX + diffX, orginY + diffY);
		} else {
			set(x, y);
		}
	}

	public PointValue set(float x, float y) {
		this.x = x;
		this.y = y;
		this.orginX = x;
		this.orginY = y;
		this.diffX = 0;
		this.diffY = 0;
		return this;
	}

	public PointValue setTarget(float targetX, float targetY) {
		set(x, y);
		this.diffX = targetX - orginX;
		this.diffY = targetY - orginY;
		return this;
	}

	public float getX() {
		return this.x;
	}

	public float getY() {
		return this.y;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Float.floatToIntBits(x);
		result = prime * result + Float.floatToIntBits(y);
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
		PointValue other = (PointValue) obj;
		if (Float.floatToIntBits(x) != Float.floatToIntBits(other.x))
			return false;
		if (Float.floatToIntBits(y) != Float.floatToIntBits(other.y))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "PointValue [x=" + x + ", y=" + y + "]";
	}

}
