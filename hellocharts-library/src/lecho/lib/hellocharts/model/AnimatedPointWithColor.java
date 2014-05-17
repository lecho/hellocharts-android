package lecho.lib.hellocharts.model;

public class AnimatedPointWithColor {

	public Point point;
	public Point targetPoint;
	public int color;

	public AnimatedPointWithColor(Point point, int color) {
		// point and targetPoint have to be different objects
		this.point = new Point(point);
		this.targetPoint = new Point(point);
	}

	public void update(float scale) {
		final Point diff = point.diff(targetPoint);
		point.offset(diff.x * scale, diff.y * scale);
	}
}
