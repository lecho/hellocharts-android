package lecho.lib.hellocharts.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Partial copy of android.graphics.Rect but here the top should be greater then the bottom. Viewport holds 4 float
 * coordinates for a chart extremes. The viewport is represented by the coordinates of its 4 edges (left, top, right
 * bottom). These fields can be accessed directly. Use width() and height() to retrieve the viewport's width and height.
 * Note: most methods do not check to see that the coordinates are sorted correctly (i.e. left is less than right and
 * bottom is less than top). Viewport implements Parcerable.
 */
public class Viewport implements Parcelable {

    public float left;
    public float top;
    public float right;
    public float bottom;
    public static final Parcelable.Creator<Viewport> CREATOR = new Parcelable.Creator<Viewport>() {
        /**
         * Return a new viewport from the data in the specified parcel.
         */
        public Viewport createFromParcel(Parcel in) {
            Viewport v = new Viewport();
            v.readFromParcel(in);
            return v;
        }

        /**
         * Return an array of viewports of the specified size.
         */
        public Viewport[] newArray(int size) {
            return new Viewport[size];
        }
    };

    /**
     * Create a new empty Viewport. All coordinates are initialized to 0.
     */
    public Viewport() {
    }

    /**
     * Create a new viewport with the specified coordinates. Note: no range checking is performed, so the caller must
     * ensure that left is less than right and bottom is less than top.
     *
     * @param left   The X coordinate of the left side of the viewport
     * @param top    The Y coordinate of the top of the viewport
     * @param right  The X coordinate of the right side of the viewport
     * @param bottom The Y coordinate of the bottom of the viewport
     */
    public Viewport(float left, float top, float right, float bottom) {
        this.left = left;
        this.top = top;
        this.right = right;
        this.bottom = bottom;
    }

    /**
     * Create a new viewport, initialized with the values in the specified viewport (which is left unmodified).
     *
     * @param v The viewport whose coordinates are copied into the new viewport.
     */
    public Viewport(Viewport v) {
        if (v == null) {
            left = top = right = bottom = 0.0f;
        } else {
            left = v.left;
            top = v.top;
            right = v.right;
            bottom = v.bottom;
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Viewport other = (Viewport) obj;
        if (Float.floatToIntBits(bottom) != Float.floatToIntBits(other.bottom))
            return false;
        if (Float.floatToIntBits(left) != Float.floatToIntBits(other.left))
            return false;
        if (Float.floatToIntBits(right) != Float.floatToIntBits(other.right))
            return false;
        if (Float.floatToIntBits(top) != Float.floatToIntBits(other.top))
            return false;
        return true;
    }

    /**
     * Returns true if the viewport is empty {@code left >= right or bottom >= top}
     */
    public final boolean isEmpty() {
        return left >= right || bottom >= top;
    }

    /**
     * Set the viewport to (0,0,0,0)
     */
    public void setEmpty() {
        left = right = top = bottom = 0;
    }

    /**
     * @return the viewport's width. This does not check for a valid viewport (i.e. {@code left <= right}) so the
     * result may be negative.
     */
    public final float width() {
        return right - left;
    }

    /**
     * @return the viewport's height. This does not check for a valid viewport (i.e. {@code top <= bottom}) so the
     * result may be negative.
     */
    public final float height() {
        return top - bottom;
    }

    /**
     * @return the horizontal center of the viewport. This does not check for a valid viewport (i.e. {@code left <=
     * right})
     */
    public final float centerX() {
        return (left + right) * 0.5f;
    }

    /**
     * @return the vertical center of the viewport. This does not check for a valid viewport (i.e. {@code bottom <=
     * top})
     */
    public final float centerY() {
        return (top + bottom) * 0.5f;
    }

    /**
     * Set the viewport's coordinates to the specified values. Note: no range checking is performed, so it is up to the
     * caller to ensure that {@code left <= right and bottom <= top}.
     *
     * @param left   The X coordinate of the left side of the viewport
     * @param top    The Y coordinate of the top of the viewport
     * @param right  The X coordinate of the right side of the viewport
     * @param bottom The Y coordinate of the bottom of the viewport
     */
    public void set(float left, float top, float right, float bottom) {
        this.left = left;
        this.top = top;
        this.right = right;
        this.bottom = bottom;
    }

    /**
     * Copy the coordinates from src into this viewport.
     *
     * @param src The viewport whose coordinates are copied into this viewport.
     */
    public void set(Viewport src) {
        this.left = src.left;
        this.top = src.top;
        this.right = src.right;
        this.bottom = src.bottom;
    }

    /**
     * Offset the viewport by adding dx to its left and right coordinates, and adding dy to its top and bottom
     * coordinates.
     *
     * @param dx The amount to add to the viewport's left and right coordinates
     * @param dy The amount to add to the viewport's top and bottom coordinates
     */
    public void offset(float dx, float dy) {
        left += dx;
        top += dy;
        right += dx;
        bottom += dy;
    }

    /**
     * Offset the viewport to a specific (left, top) position, keeping its width and height the same.
     *
     * @param newLeft The new "left" coordinate for the viewport
     * @param newTop  The new "top" coordinate for the viewport
     */
    public void offsetTo(float newLeft, float newTop) {
        right += newLeft - left;
        bottom += newTop - top;
        left = newLeft;
        top = newTop;
    }

    /**
     * Inset the viewport by (dx,dy). If dx is positive, then the sides are moved inwards, making the viewport narrower.
     * If dx is negative, then the sides are moved outwards, making the viewport wider. The same holds true for dy and
     * the top and bottom.
     *
     * @param dx The amount to add(subtract) from the viewport's left(right)
     * @param dy The amount to add(subtract) from the viewport's top(bottom)
     */
    public void inset(float dx, float dy) {
        left += dx;
        top -= dy;
        right -= dx;
        bottom += dy;
    }

    /**
     * Returns true if (x,y) is inside the viewport. The left and top are considered to be inside, while the right and
     * bottom are not. This means that for a x,y to be contained: {@code left <= x < right and bottom <= y < top}. An
     * empty viewport never contains any point.
     *
     * @param x The X coordinate of the point being tested for containment
     * @param y The Y coordinate of the point being tested for containment
     * @return true iff (x,y) are contained by the viewport, where containment means {@code left <= x < right and top <=
     * y < bottom}
     */
    public boolean contains(float x, float y) {
        return left < right && bottom < top // check for empty first
                && x >= left && x < right && y >= bottom && y < top;
    }

    /**
     * Returns true iff the 4 specified sides of a viewport are inside or equal to this viewport. i.e. is this viewport
     * a superset of the specified viewport. An empty viewport never contains another viewport.
     *
     * @param left   The left side of the viewport being tested for containment
     * @param top    The top of the viewport being tested for containment
     * @param right  The right side of the viewport being tested for containment
     * @param bottom The bottom of the viewport being tested for containment
     * @return true iff the the 4 specified sides of a viewport are inside or equal to this viewport
     */
    public boolean contains(float left, float top, float right, float bottom) {
        // check for empty first
        return this.left < this.right && this.bottom < this.top
                // now check for containment
                && this.left <= left && this.top >= top && this.right >= right && this.bottom <= bottom;
    }

    /**
     * Returns true iff the specified viewport r is inside or equal to this viewport. An empty viewport never contains
     * another viewport.
     *
     * @param v The viewport being tested for containment.
     * @return true iff the specified viewport r is inside or equal to this viewport
     */
    public boolean contains(Viewport v) {
        // check for empty first
        return this.left < this.right && this.bottom < this.top
                // now check for containment
                && left <= v.left && top >= v.top && right >= v.right && bottom <= v.bottom;
    }

    /**
     * Update this Viewport to enclose itself and the specified viewport. If the specified viewport is empty, nothing is
     * done. If this viewport is empty it is set to the specified viewport.
     *
     * @param left   The left edge being unioned with this viewport
     * @param top    The top edge being unioned with this viewport
     * @param right  The right edge being unioned with this viewport
     * @param bottom The bottom edge being unioned with this viewport
     */
    public void union(float left, float top, float right, float bottom) {
        if ((left < right) && (bottom < top)) {
            if ((this.left < this.right) && (this.bottom < this.top)) {
                if (this.left > left)
                    this.left = left;
                if (this.top < top)
                    this.top = top;
                if (this.right < right)
                    this.right = right;
                if (this.bottom > bottom)
                    this.bottom = bottom;
            } else {
                this.left = left;
                this.top = top;
                this.right = right;
                this.bottom = bottom;
            }
        }
    }

    /**
     * Update this Viewport to enclose itself and the specified viewport. If the specified viewport is empty, nothing is
     * done. If this viewport is empty it is set to the specified viewport.
     *
     * @param v The viewport being unioned with this viewport
     */
    public void union(Viewport v) {
        union(v.left, v.top, v.right, v.bottom);
    }

    /**
     * If the viewport specified by left,top,right,bottom intersects this viewport, return true and set this viewport to
     * that intersection, otherwise return false and do not change this viewport. No check is performed to see if either
     * viewport is empty. Note: To just test for intersection, use intersects()
     *
     * @param left   The left side of the viewport being intersected with this viewport
     * @param top    The top of the viewport being intersected with this viewport
     * @param right  The right side of the viewport being intersected with this viewport.
     * @param bottom The bottom of the viewport being intersected with this viewport.
     * @return true if the specified viewport and this viewport intersect (and this viewport is then set to that
     * intersection) else return false and do not change this viewport.
     */
    public boolean intersect(float left, float top, float right, float bottom) {
        if (this.left < right && left < this.right && this.bottom < top && bottom < this.top) {
            if (this.left < left) {
                this.left = left;
            }
            if (this.top > top) {
                this.top = top;
            }
            if (this.right > right) {
                this.right = right;
            }
            if (this.bottom < bottom) {
                this.bottom = bottom;
            }
            return true;
        }
        return false;
    }

    /**
     * If the specified viewport intersects this viewport, return true and set this viewport to that intersection,
     * otherwise return false and do not change this viewport. No check is performed to see if either viewport is empty.
     * To just test for intersection, use intersects()
     *
     * @param v The viewport being intersected with this viewport.
     * @return true if the specified viewport and this viewport intersect (and this viewport is then set to that
     * intersection) else return false and do not change this viewport.
     */
    public boolean intersect(Viewport v) {
        return intersect(v.left, v.top, v.right, v.bottom);
    }

    @Override
    public String toString() {
        return "Viewport [left=" + left + ", top=" + top + ", right=" + right + ", bottom=" + bottom + "]";
    }

    // ** PARCERABLE **

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + Float.floatToIntBits(bottom);
        result = prime * result + Float.floatToIntBits(left);
        result = prime * result + Float.floatToIntBits(right);
        result = prime * result + Float.floatToIntBits(top);
        return result;
    }

    /**
     * Parcelable interface methods
     */
    public int describeContents() {
        return 0;
    }

    /**
     * Write this viewport to the specified parcel. To restore a viewport from a parcel, use readFromParcel()
     *
     * @param out The parcel to write the viewport's coordinates into
     */
    public void writeToParcel(Parcel out, int flags) {
        out.writeFloat(left);
        out.writeFloat(top);
        out.writeFloat(right);
        out.writeFloat(bottom);
    }

    /**
     * Set the viewport's coordinates from the data stored in the specified parcel. To write a viewport to a parcel,
     * call writeToParcel().
     *
     * @param in The parcel to read the viewport's coordinates from
     */
    public void readFromParcel(Parcel in) {
        left = in.readFloat();
        top = in.readFloat();
        right = in.readFloat();
        bottom = in.readFloat();
    }
}
