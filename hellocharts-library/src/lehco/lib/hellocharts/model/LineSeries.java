package lehco.lib.hellocharts.model;

import java.util.List;

public class LineSeries {

	public final int color;
	public final List<Float> values;

	public LineSeries(int color, List<Float> values) {
		this.color = color;
		this.values = values;
	}
}
