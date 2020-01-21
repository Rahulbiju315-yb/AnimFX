/**
 * A rectangle with cordinates and dimensions of type double.
 * The class is used by {@code Drawable} to store the rectangular
 * bounds of the drawing.
 *
 * @see Drawable#getBounds
 */

public class RealRectangle
{
	/**
	 * Top - left cordinate of the rectangle.
	 */
	double x, y;

	/**
	 * Bottom - right cordinate of the rectangle.
	 */
	double x1, y1;

	/**
	 * Width and height of the rectangle.
	 * Width is the span of the rectangle along the X - axis.
	 * Height is its span along the Y - axis.
	 */
	double width, height;

	/**
	 * Creates a RealRectangle with the specified top-left cordinates and
	 * dimensions.
	 */
	public RealRectangle(double x, double y, double width, double height)
	{
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;

		x1 = x + width;
		y1 = y + height;
	}

	/**
	 * Returns the x cordinate of the top - left point.
	 */
	public double getX()
	{
		return x;
	}

	/**
	 * Returns the y cordinate of the top - left point.
	 */
	public double getY()
	{
		return y;
	}

	/**
	 * Returns the x cordinate of the bottom - right point.
	 */
	public double getRX()
	{
		return x1;
	}

	/**
	 * Returns the y cordinate of the bottom - right point.
	 */
	public double getRY()
	{
		return y1;
	}

	/**
	 * Returns the width of the rectangle.
	 */
	public double getWidth()
	{
		return width;
	}

	/**
	 * Returns the height of the rectangle.
	 */
	public double getHeight()
	{
		return height;
	}
}
