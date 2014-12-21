package lecho.lib.hellocharts.listener;


import lecho.lib.hellocharts.model.ArcValue;

public interface PieChartOnValueSelectListener extends OnValueDeselectListener {

	public void onValueSelected(int arcIndex, ArcValue value);

}
