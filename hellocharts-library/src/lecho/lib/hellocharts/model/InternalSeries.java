package lecho.lib.hellocharts.model;

import java.util.List;

public class InternalSeries {
	public final int color;
	public final List<AnimatedValue> values;

	public InternalSeries(int color, List<AnimatedValue> values) {
		this.color = color;
		this.values = values;
	}
}
