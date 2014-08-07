package lecho.lib.hellocharts.model;


public class PointValue {

	private float x;
	private float y;
	private float orginX;
	private float orginY;
	private float diffX;
	private float diffY;

	public PointValue(float x, float y) {
		set(x, y);
	}

	public void update(float scale) {
		x = orginX + diffX * scale;
		y = orginY + diffY * scale;
	}

	public void finish(boolean isFinishedSuccess) {
		if (isFinishedSuccess) {
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
}
