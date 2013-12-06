package lecho.lib.hellocharts.model;

public class AnimatedValue {

	public float targetPosition;
	public float position;

	public AnimatedValue(float position, float targetPosition) {
		this.position = position;
		this.targetPosition = targetPosition;
	}

	public void update(float scale) {
		float x = targetPosition - position;
		position = position + x * scale;
	}

}
