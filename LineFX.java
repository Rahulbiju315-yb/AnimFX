import java.util.*;
import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
import java.awt.image.*;

/**
 * A class implementing {@code Drawable} to draw basic lines and allow their animation.
 * on a {@code GraphFX}
 *
 * <p>
 * A {@code LineFX} can be created by either specifying its end points in real cordinates
 * or in screen cordinates and also passing the {@code GraphFX} to which the drawing needs
 * to be done. Note that this does not mean that the LineFX will be rendered on the screen.
 * To do this the {@code addDrawable} method of the {@code GraphFX} must be called.
 *
 * <p>
 * Though the cordinates are stored as scren - cordinates in {@code LineFX}, they are of type
 * double. This is to ensure a continuous change in position on each update.
 */
public class LineFX extends Drawable
{
	/**
	 * The end points' cordinates of the {@code LineFX} to be drawn.
	 */
	double x1, y1, x2, y2;

	Graphics2D g2d;

	/**
	 * Cordinates of the centre of the line. These are defined for calculting endpoints
	 * following rotation updates.
	 *
	 * @see #update
	 */
	double cx, cy;

	/**
	 * The angle of inclination of the line.
	 */
	double theta;

	/**
	 * The length of the line.
	 */
	double l;

	/**
	 * A static variable which counts the number of {@code LineFX} objects that
	 * have been created, to give a unique name to each of the {@code LineFX}
	 * objects.
	 *
	 * @see #LineFX
	 */
	static int count;

	/**
	 * Creates a {@code LineFX} with the given screen cordinates and associated with
	 * the given {@code GraphFX}.
	 *
	 * @param x1 the screen x cordinate of the first endpoint.
	 * @param y1 the screen y cordinate of the first endpoint.
	 * @param x2 the screen x cordinate of the second endpoint.
	 * @param y2 the screen y cordinate of the second endpoint.
	 * @param gfx the {@code GraphFX} instance.
	 */
	public LineFX(int x1, int y1, int x2, int y2, GraphFX gfx)
	{
		this.gfx = gfx;

		updateEvents = new Vector<UpdateEvent>();

		this.x1 = x1;
		this.y1 = y1;

		this.x2 = x2;
		this.y2 = y2;

		cx = (x1 + x2)/2;
		cy = (y1 + y2)/2;

		l = Math.pow(Math.pow((x2 - x1), 2) + Math.pow((y2 - y1), 2), 1/2.0);
		theta = (x2 == x1) ? Math.PI / 2 : Math.atan((y2 - y1) / (x2 - x1));

		count++;
		name = "LineFX_" + count;
	}

	/**
	 * Creates a {@code LineFX} with the given real cordinates.
	 *
	 * @param x1 the real x cordinate of the first endpoint.
	 * @param y1 the real y cordinate of the first endpoint.
	 * @param x2 the real x cordinate of the second endpoint.
	 * @param y2 the real y cordinate of the second endpoint.
	 */
	public LineFX(double x1, double y1, double x2, double y2, GraphFX gfx)
	{
		this(gfx.rtsX(x1), gfx.rtsY(y1), gfx.rtsX(x2), gfx.rtsY(y2), gfx);
	}

	/**
	 * Shifts the line in the x direction by the specified real cordinates.
	 *
	 * @param x the amount to shift in x direction.
	 */
	@Override
	public void xShiftR(double x)
	{
		double dx = gfx.rtsXD(x);
		x1 += dx;
		x2 += dx;
		cx += dx;
	}

	/**
	 * Shifts the line in the x direction by the specified real cordinates.
	 *
	 * @param y the amount to shift in y direction.
	 */
	@Override
	public void yShiftR(double y)
	{
		double dy = gfx.rtsYD(y);
		y1 += dy;
		y2 += dy;
		cy += dy;
	}

	/**
	 * Shifts the line in the x direction by the specified screen cordinates.
	 *
	 * @param x the amount to shift in x direction.
	 */
	@Override
	public void xShiftS(double x)
	{
		double dx = x;
		x1 += dx;
		x2 += dx;
		cx += dx;
	}

	/**
	 * Shifts the line in the x direction by the specified screen cordinates.
	 *
	 * @param y the amount to shift in y direction.
	 */
	@Override
	public void yShiftS(double y)
	{
		double dy = y;
		y1 += dy;
		y2 += dy;
		cy += dy;
	}
	/**
	 * Draws a line between ({@code x1}, {@code y1}) and ({@code x2}, {@code y2}).
	 */
	public void plot()
	{
		((Graphics2D)bim.getGraphics()).drawLine((int)(x1 + 0.5), (int)(y1 + 0.5), (int)(x2 + 0.5), (int)(y2 + 0.5));
	}

	/**
	 * Sets the color of the line.
	 *
	 * @param c the color of the line.
	 */
	public void setColor(Color c)
	{
		((Graphics2D)bim.getGraphics()).setColor(c);
	}

	/**
	 * The update function.
	 *
	 * <p>
	 * The values of endpoints are updated based on the velocity and error factor. The error factor
	 * {@code err} is the factor of the frame time by which the previous plot-update cycle finished
	 * late by. For example is the frame time is 20 ms and it took the previous plot-update cycle
	 * finished in 25 ms the error factor will be (25 - 20) /
	 */
	public void update(double err)
	{
		super.update(err);
		//System.out.print(err);

		double vx_ = vx * (1 + err);
		double vy_ = vy * (1 + err);
		double phi_ = phi * (1 + err);

		x1 += vx_;
		y1 += vy_;
		x2 += vx_;
		y2 += vy_;

		cx += vx_;
		cy += vy_;

		if(phi != 0)
		{
			x1 = cx + (l / 2 * Math.cos(phi_ + theta));
			y1 = cy + (l / 2 * Math.sin(phi_ + theta));
			x2 = cx - (l / 2 * Math.cos(phi_ + theta));
			y2 = cy - (l / 2 * Math.sin(phi_ + theta));

			theta += phi_;
		}
	}


	public String toString()
   {
   	return name;
   }

	/**
	 * Returns the bounding rectangle of the {@code LineFX}.
	 *
	 * @see Drawable#getBounds
	 */
	@Override
	public RealRectangle getBounds()
	{
   	return getBoundedRectangle(x1, y1, x2, y2);
   }

	/**
	 * Returns a clone of the current {@code LineFX} instance.
	 */
	@Override
	public Object clone()
   {
   	LineFX lineClone = new LineFX((int)x1, (int)y1, (int)x2, (int)y2, gfx);
    	lineClone.setLinearVel(vx, vy);
    	lineClone.setAngularVel(phi);

    	if(updateEvents != null)
			for(int i = 0; i < ueSize; i++)
			{
				lineClone.addUpdateEvent(updateEvents.elementAt(i));
			}

		lineClone.setBImage(bim);

		return lineClone;
    }
}
