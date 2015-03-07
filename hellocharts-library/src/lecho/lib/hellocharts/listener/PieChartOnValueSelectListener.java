package lecho.lib.hellocharts.listener;


import lecho.lib.hellocharts.model.SliceValue;

public interface PieChartOnValueSelectListener extends OnValueDeselectListener {

    public void onValueSelected(int arcIndex, SliceValue value);

}
