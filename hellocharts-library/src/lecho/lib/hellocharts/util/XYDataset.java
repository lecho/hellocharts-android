package lecho.lib.hellocharts.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import lecho.lib.hellocharts.model.PointValue;

/**
 * The purpose of this class is to be a faster replacement to a {@link java.util.TreeMap} with
 *  the ability to get sublist containing a range of x values. ArrayList access time is O(1) while
 *  {@link java.util.TreeMap} is O(log(n)). When large data is handled the impact on performance is
 *  noticeable.
 */
public class XYDataset extends ArrayList<PointValue> {

    private final float COMPARISON_THRESHOLD = 0.01f;

    final Comparator<PointValue> comparator = new Comparator<PointValue>() {
        @Override
        public int compare(PointValue lhs, PointValue rhs) {
            if (Math.abs(lhs.getX() - rhs.getX()) < COMPARISON_THRESHOLD) return 0;
            return lhs.getX() < rhs.getX() ? -1 : 1;
        }
    };

    public XYDataset(int capacity) {
        super(capacity);
    }

    public XYDataset() {
    }

    public XYDataset(Collection<? extends PointValue> collection) {
        super(collection);
    }

    @Override
    public List<PointValue> subList(int start, int end) {
        try{ return super.subList(start, end); } catch (Exception e){ return Collections.emptyList(); }
    }

    /**
     * Generate a sublist containing the range of x values passed
     * @param x1 lower x value
     * @param x2 upper x value
     * @return sublist containing x values from x1 to x2
     */
    public List<PointValue> subList(float x1, float x2){
        /**
         * Collections.binarySearch() returns the index of the search key, if it is contained in the list;
         *  otherwise it returns (-(insertion point) - 1).
         * The insertion point is defined as the point at which the key would be inserted into the list:
         *  the index of the first element greater than the key, or list.size() if all elements in the list
         *  are less than the specified key. Note that this guarantees that the return value will be >= 0 if
         *  and only if the key is found.
         */
        int n1 = Collections.binarySearch(this, new PointValue(x1, 0), comparator);
        int n2 = Collections.binarySearch(this, new PointValue(x2, 0), comparator);

        /**
         * Example, we assume the list is sorted. Based on http://stackoverflow.com/a/19198826/1335209
         *
         * long X = 500;
         * List<Long> foo = new Arraylist<>();
         * foo.add(450L);
         * foo.add(451L);
         * foo.add(499L);
         * foo.add(501L);
         * foo.add(550L);
         *
         * If we search for something that isn't in the list you can work backward from the return value
         *  to the index you want. If you search for 500 in your example list, the algorithm would return (-3 - 1) = -4.
         * Thus, you can add 1 to get back to the insertion point (-3), and then multiply by -1 and subtract 1 to get
         *  the index BEFORE the first element GREATER than the one you searched for, which will either be an index that
         *  meets your 2 criteria OR -1 if all elements in the list are greater than the one you searched for.
         */
        if(n1 < 0) n1 = -n1-1;
        if(n2 < 0) n2 = -n2-1;

        return this.subList(n1, n2);
    }
}
