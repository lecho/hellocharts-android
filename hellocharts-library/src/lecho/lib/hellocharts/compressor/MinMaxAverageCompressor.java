package lecho.lib.hellocharts.compressor;

import lecho.lib.hellocharts.Chart;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.util.XYDataset;

public class MinMaxAverageCompressor implements DataCompressor {

    final int dataGroupingSize;

    public MinMaxAverageCompressor(int dataGroupingSize) {
        this.dataGroupingSize = dataGroupingSize;
    }

    @Override
    public XYDataset compress(Line line, Chart chart) {
        final XYDataset resultList = new XYDataset();
        int count = 0;
        double sumY = 0, sumX = 0;
        for (PointValue pointValue : line.getPoints()) {
            // First point
            if(count == 0){
                resultList.add(pointValue);
            }
            if (count < dataGroupingSize) {
                sumX += pointValue.getX();
                sumY += pointValue.getY();
                ++count;
                // Average mid point and last point
            } else {
                // Averaged mid point
                resultList.add(new PointValue((float) (sumX / count), (float) (sumY / count)));
                // Last point
                resultList.add(pointValue);
                sumX = sumY = 0;
                count = 0;
            }
        }
        if (count > 0) resultList.add(new PointValue((float) (sumX / count), (float) (sumY / count)));
        return resultList;
    }

}
