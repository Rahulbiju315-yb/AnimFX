import java.util.*;
import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
import java.awt.image.*;

public class GroupFX extends Drawable
{
	BufferedImage bim;
	Vector<Drawable> drs;
	String name, ID;

	int XR, YR;
	int n ;
	static int count;

	public GroupFX(GraphFX gfx, Drawable... drs)
	{
		this.gfx = gfx;

		n = drs.length;
		XR = gfx.getViewWidth();
		YR = gfx.getViewHeight();

		count++;
		ID = "GroupFX_" + count;
		name = new String(ID);

		this.drs = new Vector<Drawable>();
		updateEvents = new Vector<UpdateEvent>();
		for(Drawable dr : drs)
		{
			this.drs.add(dr);
		}
	}

	@Override
	public void xShift(double x)
	{
		for(int i = 0; i < n; i++)
		{
			drs.elementAt(i).xShift(x);
		}
	}

	@Override
	public void yShift(double y)
	{
		
		for(int i = 0; i < n; i++)
		{
			drs.elementAt(i).yShift(y);
		}
	}	

	@Override 
	public void setLinearXVel(double vx)
	{
		this.vx = vx;
		for(int i = 0; i < n; i++)
		{
			drs.elementAt(i).setLinearXVel(vx);
		}
	}

	@Override 
	public void setLinearYVel(double vy)
	{
		this.vy = vy;
		for(int i = 0; i < n; i++)
		{
			drs.elementAt(i).setLinearYVel(vy);
		}
	}

	@Override 
	public void setLinearVel(double vx, double vy)
	{
		this.vx = vx;
		this.vy = vy;
		for(int i = 0; i < n; i++)
		{
			drs.elementAt(i).setLinearVel(vx, vy);
		}
	}

	@Override
	public void setAngularVel(double phi)
	{
		this.phi = phi;
		for(int i = 0; i < n; i++)
		{
			drs.elementAt(i).setAngularVel(phi);
		}
	}
	public void setBImage(BufferedImage bim)
	{
		this.bim = bim;
		for(int i = 0; i < n; i++)
		{
			drs.elementAt(i).setBImage(bim);
		}
	}

	@Override 
	public Rectangle getBounds()
	{
		int xl = 0, yl = 0, xr = 0, yr = 0, w, h;

		for(int i = 0; i < n; i++)
		{
			Rectangle rct = drs.elementAt(i).getBounds();
			if(rct == null)
				return null;
			else
			{
				xl = (int)Math.min(xl, rct.getX());
				yl = (int)Math.min(yl, rct.getY());

				xr = (int)Math.max(xr, (rct.getX() + rct.getWidth()));
				yr = (int)Math.max(yr, (rct.getY() + rct.getHeight()));
			}
		}

		return new Rectangle(xl, yl, xr - xl, yr - yl);
	}

	public void addDrawable(Drawable dr)
	{
		drs.add(dr);
	}

	public void setID(String ID)
	{
		this.ID = ID;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public String getID()
	{
		return ID;
	}

	public String toString()
	{
		return name;
	}

	public void plot()
	{
		for(int i = 0; i < n; i++)
		{
			drs.elementAt(i).plot();
		}
	}

	public void unplot()
	{
		for(int i = n - 1; i >= 0; i--)
		{
			drs.elementAt(i).unplot();
		}
	}

	@Override
	public void update(double err)
	{
		super.update(err);
		for(int i = 0; i < n; i++)
		{
			drs.elementAt(i).update(err);
		}
	}
}