package lecho.lib.hellocharts.model;

import java.util.ArrayList;
import java.util.List;

import android.view.animation.AnimationUtils;

public class LineSeries {

	public final int color;
	public final List<DynamicValue> values = new ArrayList<DynamicValue>();

	public LineSeries(int color, List<Float> values) {
		this.color = color;
		long now = AnimationUtils.currentAnimationTimeMillis();
		for (float value : values) {
			DynamicValue dv = new DynamicValue(70f, 1.0f);
			dv.setPosition(value, now);
			this.values.add(dv);
		}

	}
}
