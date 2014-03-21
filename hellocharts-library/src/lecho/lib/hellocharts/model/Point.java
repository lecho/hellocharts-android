package lecho.lib.hellocharts.model;

public class Point {
	public float x;
	public float y;

	public Point(Point point) {
		this.x = point.x;
		this.y = point.y;
	}

	public Point(float x, float y) {
		this.x = x;
		this.y = y;
	}

	public void set(Point point) {
		this.x = point.x;
		this.y = point.y;
	}

	public void set(float x, float y) {
		this.x = x;
		this.y = y;
	}

	public void offset(float dx, float dy) {
		this.x += dx;
		this.y += dy;
	}

	/**
	 * Returns x and y difference between this point and the one in parameter.
	 */
	public Point diff(Point point) {
		return new Point(point.x - this.x, point.y - this.y);
	}
}
