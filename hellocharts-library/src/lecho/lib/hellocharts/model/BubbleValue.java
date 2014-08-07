package lecho.lib.hellocharts.model;

public class BubbleValue {

	private float x;
	private float y;
	private float z;
	private float orginX;
	private float orginY;
	private float orginZ;
	private float diffX;
	private float diffY;
	private float diffZ;

	public BubbleValue(float x, float y, float z) {
		set(x, y, z);
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
}
