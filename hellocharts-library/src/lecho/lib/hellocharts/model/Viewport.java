package lecho.lib.hellocharts.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Partial copy of android.graphics.RectF but top should be greater then bottom. Viewport holds four float coordinates
 * for a viewport rectangle. The rectangle is represented by the coordinates of its 4 edges (left, top, right bottom).
 * These fields can be accessed directly. Use width() and height() to retrieve the rectangle's width and height. Note:
 * most methods do not check to see that the coordinates are sorted correctly (i.e. left <= right and bottom <= top).
 * 
 * Viewport implements Parcerable.
 * 
 * @author Leszek Wach
 * 
 */
public class Viewport implements Parcelable {

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

	public float left;
	public float top;
	public float right;
	public float bottom;

	/**
	 * Create a new empty RectF. All coordinates are initialized to 0.
	 */
	public Viewport() {
	}

	/**
	 * Create a new rectangle with the specified coordinates. Note: no range checking is performed, so the caller must
	 * ensure that left <= right and bottom <= top.
	 * 
	 * @param left
	 *            The X coordinate of the left side of the rectangle
	 * @param top
	 *            The Y coordinate of the top of the rectangle
	 * @param right
	 *            The X coordinate of the right side of the rectangle
	 * @param bottom
	 *            The Y coordinate of the bottom of the rectangle
	 */
	public Viewport(float left, float top, float right, float bottom) {
		this.left = left;
		this.top = top;
		this.right = right;
		this.bottom = bottom;
	}

	/**
	 * Create a new rectangle, initialized with the values in the specified rectangle (which is left unmodified).
	 * 
	 * @param v
	 *            The rectangle whose coordinates are copied into the new rectangle.
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

	/**
	 * Returns true if the rectangle is empty (left >= right or bottom >= top)
	 */
	public final boolean isEmpty() {
		return left >= right || bottom >= top;
	}

	/**
	 * Set the rectangle to (0,0,0,0)
	 */
	public void setEmpty() {
		left = right = top = bottom = 0;
	}

	/**
	 * @return the rectangle's width. This does not check for a valid rectangle (i.e. left <= right) so the result may
	 *         be negative.
	 */
	public final float width() {
		return right - left;
	}

	/**
	 * @return the rectangle's height. This does not check for a valid rectangle (i.e. top <= bottom) so the result may
	 *         be negative.
	 */
	public final float height() {
		return top - bottom;
	}

	/**
	 * @return the horizontal center of the rectangle. This does not check for a valid rectangle (i.e. left <= right)
	 */
	public final float centerX() {
		return (left + right) * 0.5f;
	}

	/**
	 * @return the vertical center of the rectangle. This does not check for a valid rectangle (i.e. bottom <= top)
	 */
	public final float centerY() {
		return (top + bottom) * 0.5f;
	}

	/**
	 * Set the rectangle's coordinates to the specified values. Note: no range checking is performed, so it is up to the
	 * caller to ensure that left <= right and bottom <= top.
	 * 
	 * @param left
	 *            The X coordinate of the left side of the rectangle
	 * @param top
	 *            The Y coordinate of the top of the rectangle
	 * @param right
	 *            The X coordinate of the right side of the rectangle
	 * @param bottom
	 *            The Y coordinate of the bottom of the rectangle
	 */
	public void set(float left, float top, float right, float bottom) {
		this.left = left;
		this.top = top;
		this.right = right;
		this.bottom = bottom;
	}

	/**
	 * Copy the coordinates from src into this rectangle.
	 * 
	 * @param src
	 *            The rectangle whose coordinates are copied into this rectangle.
	 */
	public void set(Viewport src) {
		this.left = src.left;
		this.top = src.top;
		this.right = src.right;
		this.bottom = src.bottom;
	}

	/**
	 * Offset the rectangle by adding dx to its left and right coordinates, and adding dy to its top and bottom
	 * coordinates.
	 * 
	 * @param dx
	 *            The amount to add to the rectangle's left and right coordinates
	 * @param dy
	 *            The amount to add to the rectangle's top and bottom coordinates
	 */
	public void offset(float dx, float dy) {
		left += dx;
		top += dy;
		right += dx;
		bottom += dy;
	}

	/**
	 * Offset the rectangle to a specific (left, top) position, keeping its width and height the same.
	 * 
	 * @param newLeft
	 *            The new "left" coordinate for the rectangle
	 * @param newTop
	 *            The new "top" coordinate for the rectangle
	 */
	public void offsetTo(float newLeft, float newTop) {
		right += newLeft - left;
		bottom += newTop - top;
		left = newLeft;
		top = newTop;
	}

	/**
	 * Inset the rectangle by (dx,dy). If dx is positive, then the sides are moved inwards, making the rectangle
	 * narrower. If dx is negative, then the sides are moved outwards, making the rectangle wider. The same holds true
	 * for dy and the top and bottom.
	 * 
	 * @param dx
	 *            The amount to add(subtract) from the rectangle's left(right)
	 * @param dy
	 *            The amount to add(subtract) from the rectangle's top(bottom)
	 */
	public void inset(float dx, float dy) {
		left += dx;
		top -= dy;
		right -= dx;
		bottom += dy;
	}

	/**
	 * Returns true if (x,y) is inside the rectangle. The left and top are considered to be inside, while the right and
	 * bottom are not. This means that for a x,y to be contained: left <= x < right and bottom <= y < top. An empty
	 * rectangle never contains any point.
	 * 
	 * @param x
	 *            The X coordinate of the point being tested for containment
	 * @param y
	 *            The Y coordinate of the point being tested for containment
	 * @return true iff (x,y) are contained by the rectangle, where containment means left <= x < right and top <= y <
	 *         bottom
	 */
	public boolean contains(float x, float y) {
		return left < right && bottom < top // check for empty first
				&& x >= left && x < right && y >= bottom && y < top;
	}

	/**
	 * Returns true iff the 4 specified sides of a rectangle are inside or equal to this rectangle. i.e. is this
	 * rectangle a superset of the specified rectangle. An empty rectangle never contains another rectangle.
	 * 
	 * @param left
	 *            The left side of the rectangle being tested for containment
	 * @param top
	 *            The top of the rectangle being tested for containment
	 * @param right
	 *            The right side of the rectangle being tested for containment
	 * @param bottom
	 *            The bottom of the rectangle being tested for containment
	 * @return true iff the the 4 specified sides of a rectangle are inside or equal to this rectangle
	 */
	public boolean contains(float left, float top, float right, float bottom) {
		// check for empty first
		return this.left < this.right && this.bottom < this.top
		// now check for containment
				&& this.left <= left && this.top >= top && this.right >= right && this.bottom <= bottom;
	}

	/**
	 * Returns true iff the specified rectangle r is inside or equal to this rectangle. An empty rectangle never
	 * contains another rectangle.
	 * 
	 * @param v
	 *            The rectangle being tested for containment.
	 * @return true iff the specified rectangle r is inside or equal to this rectangle
	 */
	public boolean contains(Viewport v) {
		// check for empty first
		return this.left < this.right && this.bottom < this.top
		// now check for containment
				&& left <= v.left && top >= v.top && right >= v.right && bottom <= v.bottom;
	}

	@Override
	public String toString() {
		return "Viewport [left=" + left + ", top=" + top + ", right=" + right + ", bottom=" + bottom + "]";
	}

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

	// ** PARCERABLE **

	/**
	 * Parcelable interface methods
	 */
	public int describeContents() {
		return 0;
	}

	/**
	 * Write this rectangle to the specified parcel. To restore a rectangle from a parcel, use readFromParcel()
	 * 
	 * @param out
	 *            The parcel to write the rectangle's coordinates into
	 */
	public void writeToParcel(Parcel out, int flags) {
		out.writeFloat(left);
		out.writeFloat(top);
		out.writeFloat(right);
		out.writeFloat(bottom);
	}

	public static final Parcelable.Creator<Viewport> CREATOR = new Parcelable.Creator<Viewport>() {
		/**
		 * Return a new rectangle from the data in the specified parcel.
		 */
		public Viewport createFromParcel(Parcel in) {
			Viewport v = new Viewport();
			v.readFromParcel(in);
			return v;
		}

		/**
		 * Return an array of rectangles of the specified size.
		 */
		public Viewport[] newArray(int size) {
			return new Viewport[size];
		}
	};

	/**
	 * Set the rectangle's coordinates from the data stored in the specified parcel. To write a rectangle to a parcel,
	 * call writeToParcel().
	 * 
	 * @param in
	 *            The parcel to read the rectangle's coordinates from
	 */
	public void readFromParcel(Parcel in) {
		left = in.readFloat();
		top = in.readFloat();
		right = in.readFloat();
		bottom = in.readFloat();
	}
}
