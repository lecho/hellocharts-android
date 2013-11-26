package lecho.lib.hellocharts.model;

public class AnimatedValue {

	private float mTargetPosition;
	private float mPosition;

	public AnimatedValue(float position, float targetPosition) {
		this.mPosition = position;
		this.mTargetPosition = targetPosition;
	}

	public void setPosition(float position) {
		this.mPosition = position;
	}

	public void setTargetPosition(float targetPosition) {
		this.mTargetPosition = targetPosition;
	}

	public void update(float scale) {
		float x = mTargetPosition - mPosition;
		mPosition = mPosition + x * scale;
	}

	public void finish() {
		mPosition = mTargetPosition;
	}

	public float getPosition() {
		return mPosition;
	}

	public float getTargetPos() {
		return mTargetPosition;
	}
}
