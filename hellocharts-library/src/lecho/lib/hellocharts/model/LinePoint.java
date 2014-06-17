package lecho.lib.hellocharts.model;

public class LinePoint {

	private float x;
	private float y;
	private float targetX;
	private float targetY;

	public LinePoint(float x, float y) {
		this.x = x;
		this.y = y;
		this.targetX = x;
		this.targetY = y;
	}

	public void update(float scale) {
		float xdiff = targetX - x;
		float ydiff = targetY - y;
		offset(xdiff * scale, ydiff * scale);
	}

	public void set(float x, float y) {
		this.x = x;
		this.y = y;
	}

	public void setTarget(float targetX, float targetY) {
		this.targetX = targetX;
		this.targetY = targetY;
	}

	public void offset(float dx, float dy) {
		this.x += dx;
		this.y += dy;
	}

	public float getX() {
		return this.x;
	}

	public float getY() {
		return this.y;
	}
}
