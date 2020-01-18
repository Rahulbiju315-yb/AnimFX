import java.awt.*;
import java.awt.image.*;
import java.util.*;

public abstract class Drawable extends PixelManip implements Cloneable
{
	GraphFX gfx;
	Vector<UpdateEvent> updateEvents;

	double vx, vy, phi;
	int ueSize;

	public GraphFX getParent()
	{
		return gfx;
	}

	public Drawable getDrawable(String name)
	{
		return gfx.getDrawable(name);
	}

	public Rectangle getBoundedRectangle(int x1, int y1, int x2, int y2)
	{
		int xl, yl, w, h;
		xl = (int)Math.min(x1, x2);
		yl = (int)Math.min(y1, y2);

		w = Math.abs(x2 - x1);
		h = Math.abs(y2 - y1);
		Rectangle bounds = new Rectangle(xl, yl, w, h);

		return bounds;
	}

	public Rectangle getBounds()
	{
		return null;
	}

	public void changeDrawable(GraphFX gfx)
	{
		this.gfx = gfx;
	}
	
	public void addUpdateEvent(UpdateEvent e)
	{
		updateEvents.add(e);
		ueSize++;
	}

	public abstract void setBImage(BufferedImage bim);

	public abstract void plot();
	public abstract void unplot();
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

	public abstract void setID(String id);
	public abstract void setName(String name);

	public abstract String getID();

	public Object clone()
	{
		return null;
	}
}