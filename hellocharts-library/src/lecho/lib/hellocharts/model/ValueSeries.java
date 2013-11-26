package lecho.lib.hellocharts.model;

import java.util.ArrayList;
import java.util.List;

public class ValueSeries {

	public final int color;
	public final List<AnimatedValue> values = new ArrayList<AnimatedValue>();

	public ValueSeries(int color, List<Float> values) {
		this.color = color;
		for (float value : values) {
			AnimatedValue dv = new AnimatedValue(value, value);
			this.values.add(dv);
		}

	}
}
