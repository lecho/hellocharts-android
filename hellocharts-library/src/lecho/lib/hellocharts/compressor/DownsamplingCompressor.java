package lecho.lib.hellocharts.compressor;

import lecho.lib.hellocharts.Chart;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.util.XYDataset;

public class DownsamplingCompressor implements DataCompressor {

    final int pointsToSkip;

    public DownsamplingCompressor(int pointsToSkip) {
        this.pointsToSkip = pointsToSkip;
    }

    @Override
    public XYDataset compress(Line line, Chart chart) {
        final XYDataset resultList = new XYDataset();
        int skip = 0;

        for (PointValue pointValue : line.getPoints()) {
            if(skip == 0) {
                resultList.add(pointValue);
                skip = 100;
            }else {
                --skip;
            }
        }
        return resultList;
    }

}
