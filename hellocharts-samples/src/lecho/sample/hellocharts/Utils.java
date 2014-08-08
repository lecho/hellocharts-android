package lecho.sample.hellocharts;

import java.util.ArrayList;
import java.util.List;

import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Column;
import lecho.lib.hellocharts.model.ColumnValue;
import lecho.lib.hellocharts.model.PointValue;
import android.graphics.Color;

public class Utils {

	public static final int COLOR_BLUE = Color.parseColor("#33B5E5");
	public static final int COLOR_VIOLET = Color.parseColor("#AA66CC");
	public static final int COLOR_GREEN = Color.parseColor("#99CC00");
	public static final int COLOR_ORANGE = Color.parseColor("#FFBB33");
	public static final int COLOR_RED = Color.parseColor("#FF4444");

	public static final int pickColor() {
		final int[] colors = new int[] { COLOR_BLUE, COLOR_VIOLET, COLOR_GREEN, COLOR_ORANGE, COLOR_RED };
		return colors[(int) Math.round(Math.random() * (colors.length - 1))];
	}

	public static List<PointValue> generatePoints(int num, float step) {
		float x = 0.0f;
		List<PointValue> result = new ArrayList<PointValue>();
		for (float f = 0.0f; f < num; f += step) {
			result.add(new PointValue(x, (float) Math.random() * 100.0f));
			x += step;
		}
		return result;
	}

	public static List<AxisValue> generateAxis(float min, float max, float step) {
		List<AxisValue> result = new ArrayList<AxisValue>();
		for (float f = min; f <= max; f += step) {
			result.add(new AxisValue(f));
		}
		return result;
	}

	public static List<ColumnValue> generateValues(int num) {
		float x = 0.0f;
		int[] sign = new int[] { 1, 1 };
		List<ColumnValue> result = new ArrayList<ColumnValue>();
		for (int i = 0; i < num; ++i) {

			result.add(new ColumnValue((float) Math.random() * 3.0f * sign[(int) Math.round(Math.random())], Utils
					.pickColor()));
		}
		return result;
	}

	public static Column generateColumns() {
		List<ColumnValue> s1 = generateValues(1);
		Column l1 = new Column(s1);
		// l1.setHasLabels(true);
		return l1;
	}

}
