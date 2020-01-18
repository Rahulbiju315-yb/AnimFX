import java.util.*;
import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
import java.awt.image.*;


public class LineFX extends Drawable
{
	BufferedImage bim;
	double x1, y1, x2, y2;

	Graphics2D g2d;
	boolean replot = false;

	double cx, cy;

	double theta;
	double l;

	String ID;
	String name;

	static int count;
	boolean withinBounds = true;

	public LineFX(int x1, int y1, int x2, int y2, GraphFX gfx)
	{
		this.bim = bim;
		this.gfx = gfx;
		htb = new Hashtable<SPoint, Color>();
		updateEvents = new Vector<UpdateEvent>();

		this.x1 = x1;
		this.y1 = y1;

		this.x2 = x2;
		this.y2 = y2;

		cx = (x1 + x2)/2;
		cy = (y1 + y2)/2;

		if(bim != null)
			g2d = (Graphics2D)bim.getGraphics();
		l = Math.pow(Math.pow((x2 - x1), 2) + Math.pow((y2 - y1), 2), 1/2.0);

		theta = (x2 == x1) ? Math.PI / 2 : Math.atan((y2 - y1) / (x2 - x1));

		count++;
		ID = "LineFX_" + count;
		name = new String(ID);
	}

	public LineFX(double x1, double y1, double x2, double y2, GraphFX gfx)
	{
		this(gfx.rtsX(x1), gfx.rtsY(y1), gfx.rtsX(x2), gfx.rtsY(y2), gfx);
	}

	public void setID(String ID)
    {
        this.ID = new String(ID);
    }

    public void setName(String name)
    {
    	this.name = name;
    }

	public void setBImage(BufferedImage bim)
	{
		this.bim = bim;
	}

	@Override
	public void xShift(double x)
	{
		x1 += x;
		x2 += x;
	}

	@Override 
	public void yShift(double y)
	{
		y1 += y;
		y2 += y;
	}

	public void plot()
	{
		boolean fl = true;

		if(bim == null)
			return;
		g2d = (Graphics2D)bim.getGraphics();
		double dy = y2 - y1;
		double dx = x2 - x1;
		int sign = dy*dx > 0 ? 1 : (dx == 0 ? 0 : -1);

		if(sign == -1 && Math.abs(dx) >= Math.abs(dy))
		{
			int x1, y1;
			int x2, y2;
			double err = 0;
			double m = dy / dx;
			if(this.x1 < this.x2)
			{
				x1 = (int)this.x1;
				y1 = (int)this.y1;
				x2 = (int)this.x2;
				y2 = (int)this.y2;
			}
			else
			{
				x2 = (int)this.x1;
				y2 = (int)this.y1;
				x1 = (int)this.x2;
				y1 = (int)this.y2;
			}

			for(int i = x1; i < x2; i++)
			{
				err += m;
				if(err < -0.5)
				{
					err += 1;
					y1 -= 1;
				}
				putPixel(i, y1, bim, replot);
			}
		}

		if(sign == -1 && Math.abs(dx) < Math.abs(dy))
		{
			int x1, y1;
			int x2, y2;
			double err = 0;
			double m = dx / dy;
			if(this.y1 < this.y2)
			{
				x1 = (int)this.x1;
				y1 = (int)this.y1;
				x2 = (int)this.x2;
				y2 = (int)this.y2;
			}
			else
			{
				x2 = (int)this.x1;
				y2 = (int)this.y1;
				x1 = (int)this.x2;
				y1 = (int)this.y2;
			}

			for(int i = y1; i < y2; i++)
			{
				err += m;
				if(err < -0.5)
				{
					err += 1;
					x1 -= 1;
				}
				putPixel(x1, i, bim, replot);
			}
		}

		if(sign == 1 && Math.abs(dx) >= Math.abs(dy))
		{
			int x1, y1;
			int x2, y2;
			double err = 0;
			double m = dy / dx;
			if(this.x1 < this.x2)
			{
				x1 = (int)this.x1;
				y1 = (int)this.y1;
				x2 = (int)this.x2;
				y2 = (int)this.y2;
			}
			else
			{
				x2 = (int)this.x1;
				y2 = (int)this.y1;
				x1 = (int)this.x2;
				y1 = (int)this.y2;
			}

			for(int i = x1; i < x2; i++)
			{
				err += m;
				if(err > 0.5)
				{
					err -= 1;
					y1 += 1;
				}
				putPixel(i, (int)y1, bim, replot);
			}
		}

		if(sign == 1 && Math.abs(dx) < Math.abs(dy))
		{
			int x1, y1;
			int x2, y2;
			double err = 0;
			double m = dx / dy;
			if(this.y1 < this.y2)
			{
				x1 = (int)this.x1;
				y1 = (int)this.y1;
				x2 = (int)this.x2;
				y2 = (int)this.y2;
			}
			else
			{
				x2 = (int)this.x1;
				y2 = (int)this.y1;
				x1 = (int)this.x2;
				y1 = (int)this.y2;
			}

			for(int i = y1; i < y2; i++)
			{
				err += m;
				if(err > 0.5)
				{
					err -= 1;
					x1 += 1;
				}
				putPixel((int)x1, i, bim, replot);
			}
		}

		if(sign == 0)
		{
			int y1;
			int y2;
			if(this.y1 < this.y2)
			{
				y1 = (int)this.y1;
				y2 = (int)this.y2;
			}
			else
			{
				y2 = (int)this.y1;
				y1 = (int)this.y2;
			}
			for(int i = y1; i < y2; i++)
			{
				putPixel((int)this.x1, i, bim, replot);
			}
		}
	}

	public void setColor(Color c)
	{
		g2d.setColor(c);
	}

	public void unplot()
	{
		replot = true;
		plot();
		replot = false;
	}

	public void changeCords(int x1, int y1, int x2, int y2)
	{
		this.x1 = x1;
		this.y1 = y1;
		this.x2 = x2;
		this.y2 = y2;
	}

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

			//putPixel(x1, y1, bim, false);
			theta += phi_;
		}
		//omega += omega;
	}

	public String toString()
    {
        return name;
    }

    public String getID()
    {
    	return ID;
    }

    @Override
    public Rectangle getBounds()
    {
    	return getBoundedRectangle((int)x1, (int)y1, (int)x2, (int)y2);
    }

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


