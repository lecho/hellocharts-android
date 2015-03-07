package lecho.lib.hellocharts.listener;


import lecho.lib.hellocharts.model.SubcolumnValue;

public interface ColumnChartOnValueSelectListener extends OnValueDeselectListener {

    public void onValueSelected(int columnIndex, int subcolumnIndex, SubcolumnValue value);

}
