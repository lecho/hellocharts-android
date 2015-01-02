package lecho.lib.hellocharts.compressor;

import android.util.Log;

import java.util.Arrays;

import lecho.lib.hellocharts.Chart;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.util.XYDataset;

public class DouglasPeuckerCompressor implements DataCompressor {

    private final int tolerance;

    public DouglasPeuckerCompressor(int tolerance){
        this.tolerance = tolerance;
    }

    @Override
    public XYDataset compress(Line line, Chart chart) {
        // TODO: optimize
        final Simplify<PointValue> simplify = new Simplify<>(new PointValue[0]);
        PointValue[] tempArray = line.getPoints().toArray(new PointValue[0]);
        if(tempArray == null){
            Log.i("Douglas", "Array is NULL!");
        }
        PointValue[] result = simplify.simplify(tempArray, tolerance, false);
        Log.i("Douglas", "Simplify finished!");

        if(result == null){
            Log.i("Douglas", "Result is NULL!!!");
            return new XYDataset();
        }else {
            XYDataset r = new XYDataset(Arrays.asList(result));
            if (r == null) {
                Log.i("Douglas", "List is NULL!!!");
                return new XYDataset();
            }
            Log.i("Douglas", "Successful compression! " + r.size() + " points!");
            return r;
        }
    }

}
