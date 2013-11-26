package lecho.lib.hellocharts.model;

import java.util.ArrayList;
import java.util.List;

import android.view.animation.AnimationUtils;

public class ValueSeries {

	public final int color;
	public final List<AnimatedValue> values = new ArrayList<AnimatedValue>();

	public ValueSeries(int color, List<Float> values) {
		this.color = color;
		long now = AnimationUtils.currentAnimationTimeMillis();
		for (float value : values) {
			AnimatedValue dv = new AnimatedValue(70f, 1.0f);
			dv.setPosition(value, now);
			this.values.add(dv);
		}

	}
}
