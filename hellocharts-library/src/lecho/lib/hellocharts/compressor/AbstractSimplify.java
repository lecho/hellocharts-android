package lecho.lib.hellocharts.compressor;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

/**
 * Abstract base class for simplification of a polyline.
 *
 * @author hgoebl
 * @since 06.07.13
 */
abstract class AbstractSimplify<T> {

    private T[] sampleArray;

    protected AbstractSimplify(T[] sampleArray) {
        this.sampleArray = sampleArray;
    }

    /**
     * Simplifies a list of points to a shorter list of points.
     * @param points original list of points
     * @param tolerance tolerance in the same measurement as the point coordinates
     * @param highestQuality <tt>true</tt> for using Douglas-Peucker,
     *                       <tt>false</tt> for Radial-Distance algorithm
     * @return simplified list of points
     */
    public T[] simplify(T[] points, double tolerance, boolean highestQuality) {

        double sqTolerance = tolerance * tolerance;

        if (!highestQuality) {
            points = simplifyRadialDistance(points, sqTolerance);
        } else{

        points = simplifyDouglasPeucker(points, sqTolerance);}

        return points;
    }

    T[] simplifyRadialDistance(T[] points, double sqTolerance) {
        T point = null;
        T prevPoint = points[0];

        List<T> newPoints = new ArrayList<T>();
        newPoints.add(prevPoint);

        for (int i = 1; i < points.length; ++i) {
            point = points[i];

            if (getSquareDistance(point, prevPoint) > sqTolerance) {
                newPoints.add(point);
                prevPoint = point;
            }
        }

        if (prevPoint != point) {
            newPoints.add(point);
        }

        return newPoints.toArray(sampleArray);
    }

    private static class Range {
        private Range(int first, int last) {
            this.first = first;
            this.last = last;
        }

        int first;
        int last;
    }

    T[] simplifyDouglasPeucker(T[] points, double sqTolerance) {

        BitSet bitSet = new BitSet(points.length);
        bitSet.set(0);
        bitSet.set(points.length - 1);

        List<Range> stack = new ArrayList<Range>();
        stack.add(new Range(0, points.length - 1));

        while (!stack.isEmpty()) {
            Range range = stack.remove(stack.size() - 1);

            int index = -1;
            double maxSqDist = 0f;

            // find index of point with maximum square distance from first and last point
            for (int i = range.first + 1; i < range.last; ++i) {
                double sqDist = getSquareSegmentDistance(points[i], points[range.first], points[range.last]);

                if (sqDist > maxSqDist) {
                    index = i;
                    maxSqDist = sqDist;
                }
            }

            if (maxSqDist > sqTolerance) {
                bitSet.set(index);

                stack.add(new Range(range.first, index));
                stack.add(new Range(index, range.last));
            }
        }

        List<T> newPoints = new ArrayList<T>(bitSet.cardinality());
        for (int index = bitSet.nextSetBit(0); index >= 0; index = bitSet.nextSetBit(index + 1)) {
            newPoints.add(points[index]);
        }

        return newPoints.toArray(sampleArray);
    }


    public abstract double getSquareDistance(T p1, T p2);

    public abstract double getSquareSegmentDistance(T p0, T p1, T p2);
}
