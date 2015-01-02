package lecho.lib.hellocharts.model;


import lecho.lib.hellocharts.compressor.Point;

public class PointValue implements Point {

	private float x;
	private float y;
	private float originX;
	private float originY;
	private float diffX;
	private float diffY;

	public PointValue(float x, float y) {
		set(x, y);
	}

	public void update(float scale) {
		x = originX + diffX * scale;
		y = originY + diffY * scale;
	}

	public void finish(boolean isFinishedSuccess) {
		if (isFinishedSuccess) {
			set(originX + diffX, originY + diffY);
		} else {
			set(x, y);
		}
	}

	public PointValue set(float x, float y) {
		this.x = x;
		this.y = y;
		this.originX = x;
		this.originY = y;
		this.diffX = 0;
		this.diffY = 0;
		return this;
	}

	public PointValue setTarget(float targetX, float targetY) {
		set(x, y);
		this.diffX = targetX - originX;
		this.diffY = targetY - originY;
		return this;
	}

	public float getX() {
		return this.x;
	}

	public float getY() {
		return this.y;
	}

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PointValue)) return false;

        PointValue that = (PointValue) o;

        if (Float.compare(that.x, x) != 0) return false;
        if (Float.compare(that.y, y) != 0) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = (x != +0.0f ? Float.floatToIntBits(x) : 0);
        result = 31 * result + (y != +0.0f ? Float.floatToIntBits(y) : 0);
        return result;
    }
}
