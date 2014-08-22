package lecho.lib.hellocharts.model;

import java.util.ArrayList;
import java.util.List;

public class LineChartData extends AbstractChartData {

	private List<Line> lines = new ArrayList<Line>();
	private float baseValue = Float.NaN;

	public LineChartData() {

	}

	public LineChartData(List<Line> lines) {
		setLines(lines);
	}

	public List<Line> getLines() {
		return lines;
	}

	public LineChartData setLines(List<Line> lines) {
		if (null == lines) {
			this.lines = new ArrayList<Line>();
		} else {
			this.lines = lines;
		}
		return this;
	}

	public float getBaseValue() {
		return baseValue;
	}

	public LineChartData setBaseValue(float baseValue) {
		this.baseValue = baseValue;
		return this;
	}
}
