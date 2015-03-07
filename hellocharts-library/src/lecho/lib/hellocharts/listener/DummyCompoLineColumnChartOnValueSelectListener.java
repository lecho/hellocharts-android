package lecho.lib.hellocharts.listener;


import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.SubcolumnValue;

public class DummyCompoLineColumnChartOnValueSelectListener implements ComboLineColumnChartOnValueSelectListener {

    @Override
    public void onColumnValueSelected(int columnIndex, int subcolumnIndex, SubcolumnValue value) {

    }

    @Override
    public void onPointValueSelected(int lineIndex, int pointIndex, PointValue value) {

    }

    @Override
    public void onValueDeselected() {

    }
}
