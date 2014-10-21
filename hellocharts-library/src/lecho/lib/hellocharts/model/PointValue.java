package lecho.lib.hellocharts.model;

import lecho.lib.hellocharts.view.Chart;

/**
 * Single point coordinates, used for LineChartData.
 * 
 */
public class PointValue {

	private float x;
	private float y;
	private float orginX;
	private float orginY;
	private float diffX;
	private float diffY;
	private char[] label;

	public PointValue() {
		set(0, 0);
	}

	public PointValue(float x, float y) {
		set(x, y);
	}

	public PointValue(PointValue pointValue) {
		set(pointValue.x, pointValue.y);
		this.label = pointValue.label;
	}

	public void update(float scale) {
		x = orginX + diffX * scale;
		y = orginY + diffY * scale;
	}

	public void finish() {
		set(orginX + diffX, orginY + diffY);
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

	/**
	 * Set target values that should be reached when data animation finish then call {@link Chart#startDataAnimation()}
	 * 
	 * @param target
	 * @return
	 */
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

	public char[] getLabel() {
		return label;
	}

	public PointValue setLabel(char[] label) {
		this.label = label;
		return this;
	}

	@Override
	public String toString() {
		return "PointValue [x=" + x + ", y=" + y + "]";
	}

}
