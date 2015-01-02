package lecho.lib.hellocharts.compressor;

/**
 * Helper to get X and Y coordinates from a foreign class T.
 *
 * @author hgoebl
 * @since 06.07.13
 */
public interface PointExtractor<T> {
    float getX(T point);
    float getY(T point);
}
