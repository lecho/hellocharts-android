package lecho.lib.hellocharts.model;

public class AnimatedValueWithColor {

	public float value;
	public float targetValue;
	public int color;

	public AnimatedValueWithColor(float value, int color) {
		// point and targetPoint have to be different objects
		this.value = value;
		this.targetValue = value;
		this.color = color;
	}

	public void update(float scale) {
		final float diff = targetValue - value;
		value = diff * scale;
	}
}
