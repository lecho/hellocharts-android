package lecho.lib.hellocharts.compressor;

import lecho.lib.hellocharts.Chart;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.util.XYDataset;

public interface DataCompressor {

    public XYDataset compress(final Line line, final Chart chart);

}
