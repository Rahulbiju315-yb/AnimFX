import java.awt.*;
import java.awt.image.*;
import java.util.*;


/**
 * The base class for all classes that can be drawn to a {@code GraphFX}.
 *
 * <p>
 * A {@code Drawable} allows itself to be rendered on a {@code GraphFX}
 * through the {@code plot} function. The state of a {@code Drawable} is
 * updated via the {@code update} function , which happens just prior to
 * the plotting of the {@code Drawable}.
 *
 * <p>
 * The {@code Drawable} also contains a {@code Vector} of {@code UpdateEvent}
 * which is called at the beginning of each call to the {@code update} method.
 * {@code UpdateEvent} allows the user to extend the default animation of each
 * of the predefined {@code Drawables} or add capabilities like boundary detection
 * to the animation.
 *
 * <p>
 * The main properties specified by the {@code Drawable} class are:
 * <pre>
 * 1. {@code name}
 * 2. {@code vx} and {@code vy} (Velocities in X and Y direction)
 * 3. {@code phi}
 *
 * <p>
 * {@code Drawable} can be translated using the {@code shiftX} and {@code shiftY}
 * functions, though the implemenetation of these must be done in the sub class.
 *
 * <p>
 * The drawing of the plot onto the {@code GraphFX} is done by drawing directly
 * onto {@code GraphFX}'s {@code BufferedImage}. The function {@code setBImage}
 * which is called by the {@code addDrawable} method of {@code GraphFX} sets the
 * {@code BufferedImage} onto which drawing is to be done.
 *
 * <p>
 * The function {@code getBounds} returns a {@code RealRectangle} which corresponds
 * to the bounds of {@code Drawable}. The bounding rectangle will be a rectangle
 * such that its top-left cordinates corresponds to (min(X), min((Y))) where X and
 * Y are screen cordinates of the {@code Drawable} , and its bottom right
 * cordinates correspnds to (max(X), max(Y)). These bounds are particularly useful
 * in collision testing.
 *
 * @see GraphFX
 * @see GraphFX#addDrawable
 * @see GraphFX#timer
 * @see RealRectangle
 */
public abstract class Drawable implements Cloneable
{
	/**
	 * The {@code GraphFX} to which the {@code Drawable} is associated with.
	 *
	 * @see GraphFX
	 */
	GraphFX gfx;

	/**
	 * The {@code BufferedImage} of the {@code GraphFX} on which the drawing
	 * is done.
	 */
	BufferedImage bim;

	/**
	 * The {@code Vector} storing all the {@code UpdateEvent}s.
	 *
	 * @see UpdateEvent
	 */
	Vector<UpdateEvent> updateEvents;

	/**
	 * The name of the {@code Drawable}. Each {@code Drawable} assosciated
	 * with a {@code GraphFX} using the {@code addDrawable} method, must
	 * have a unique name, else the addition of the {@code Drawable} fails.
	 *
	 * @see GraphFX#addDrawable
	 * @see #getName
	 * @see #setName
	 */
	String name;

	/**
	 * A {@code RealRectangle} to return from {@code getBoundedRectangle}
	 *
	 * @see RealRectangle
	 * @see #getBoundedRectangle
	 */
	RealRectangle bounds;

    /**
     * Linear velocity of the {@code Drawable} in the direction of the X axis.
     */
	double vx;

	/**
	 * Linear velocity of the {@code Drawable} in the direction of the Y axis.
	 */
	double vy;

	/**
	 * Angular velocity of the {@code Drawable}.
	 */
	double phi;

	/**
	 * Size of {@code updateEvents}.
	 */
	int ueSize;

    /**
     * Returns the {@code GraphFX} with which the {@code Drawable} is associated.
     */
	public GraphFX getParent()
	{
		return gfx;
	}

	/**
	 * Returns a rectangle with two non - adjacent end points specified by the parameters.
	 *
	 * @param x1 X cordinate of the first point.
	 * @param y1 Y cordinate of the first point.
	 * @param x2 X cordinate of the second point.
	 * @param y2 Y cordinate of the second point.
	 */
	public RealRectangle getBoundedRectangle(double x1, double y1, double x2, double y2)
	{
		if(bounds == null)
			bounds = new RealRectangle(0, 0, 0, 0);
		double xl, yl, w, h;
		xl = Math.min(x1, x2);
		yl = Math.min(y1, y2);

		w = Math.abs(x2 - x1);
		h = Math.abs(y2 - y1);

		bounds.x = xl;
		bounds.y = yl;
		bounds.width = w;
		bounds.height = h;
		bounds.x1 = xl + w;
		bounds.y1 = yl + h;

		return bounds;
	}

	/**
	 * Returns the rectangular bounds of the {@code Drawable}.
	 */
	public abstract RealRectangle getBounds();

    /**
     * Change the {@code GraphFX} associated with the {@code Drawable}.
     * Note that this function DOES NOT remove it from the plot list
     * of the previous {@code GraphFX}. This must be done explicitly using
     * the {@code removeDrawable} function of {@code GraphFX}.
     *
     * @param gfx the new GraphFX with which the {@code Drawable} is associated.
     * @see GraphFX#removeDrawable
     */
	public void changeGraphFX(GraphFX gfx)
	{
		this.gfx = gfx;
	}

	/**
	 * Adds an {@code UpdateEvent} to the {@code UpdateEvent} list
	 * of the {@code Drawable}.
	 *
	 * @param e the {@code UpdateEvent} to add
	 * @see UpdateEvent
	 */
	public void addUpdateEvent(UpdateEvent e)
	{
		updateEvents.add(e);
		ueSize++;
	}

	/**
	 * Sets the {@code BufferedImage} on which to draw to.
	 *
	 * @param bim the {@code BufferedImage} to set.
	 */
	public void setBImage(BufferedImage bim)
	{
		this.bim = bim;
	}

    /**
     * The function which draws the {@code Drawable} to the {@code BufferedImage}.
     * The {@code BufferedImage} is drawn to the {@code GraphFX} using the
     * {@code paintComponent} method.
     *
     * @see GraphFX#timer
     */
	public abstract void plot();

	public void update(double err)
	{
		if(updateEvents != null)
			for(int i = 0; i < ueSize; i++)
			{
				updateEvents.elementAt(i).update(this);
			}
	}

	public  void setLinearVel(double vx, double vy)
	{
		this.vx = vx;
		this.vy = vy;
	}

	public  void setLinearXVel(double vx)
	{
		this.vx =vx;
	}

	public void setLinearYVel(double vy)
	{
		this.vy = vy;
	}

	public void setAngularVel(double phi)
	{
		this.phi = phi;
	}

	public double getLinearXVel()
	{
		return vx;
	}

	public double getLinearYVel()
	{
		return vy;
	}

	public double getAngularVel()
	{
		return phi;
	}

	public void xShift(double x)
	{

	}

	public void yShift(double y)
	{

	}

	public  void setName(String name)
	{
		this.name = name;
	}

	public String getName()
	{
		return name;
	}

	public Object clone()
	{
		return null;
	}

}
