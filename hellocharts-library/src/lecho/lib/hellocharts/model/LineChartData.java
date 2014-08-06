package lecho.lib.hellocharts.model;

import java.util.ArrayList;
import java.util.List;

public class LineChartData extends AbstractChartData {

	private List<Line> lines = new ArrayList<Line>();

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
}
