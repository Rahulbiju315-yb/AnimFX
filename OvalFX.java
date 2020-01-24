import java.util.*;
import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
import java.awt.image.*;
import java.awt.geom.*;

public class OvalFX extends Drawable
{
    double x1, y1;
    double w, h, ow, oh;

    boolean replot = false;

	String name;

    static int count = 0;
	double theta = 0;

    public OvalFX(int x1, int y1, int w, int h, GraphFX gfx)
    {
        this.x1 = x1;
        this.y1 = y1;
        this.w = w;
        this.h = h;
        this.ow = w;
        this.oh = h;
        this.gfx = gfx;

        count++;
        name = "OvalFX_" + count;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public OvalFX(double x1, double y1, double w, double h, GraphFX gfx)
    {
        this(gfx.rtsX(x1), gfx.rtsY(y1), Math.abs(gfx.rtsX(w) - gfx.rtsX(0)) , Math.abs(gfx.rtsY(h) - gfx.rtsY(0)), gfx);
    }

    public void setBImage(BufferedImage bim)
    {
        this.bim = bim;
    }

    public void plot()
    {
        Graphics2D g2d = ((Graphics2D)(bim.getGraphics()));
		g2d.rotate(theta, (int)(x1 + 0.5), (int)(y1 + 0.5));
		g2d.drawOval((int)(x1 - w / 2.0 + 0.5), (int)(y1 - h / 2.0 + 0.5), (int)w, (int)h);
		g2d.rotate(-theta, (int)(x1 + 0.5), (int)(y1 + 0.5));
		g2d.fillOval((int)(x1 + 0.5), (int)(y1 + 0.5), 2, 2);
    }

    public boolean isWithinBounds()
    {
        return true;
    }

    public void update(double err)
    {
		super.update(err);

		double vx_ = vx * (1 + err);
		double vy_ = vy * (1 + err);
		double phi_ = phi * (1 + err);

		x1 += vx;
		y1 += vy;

		theta += phi_;
    }

	public void xShiftR(double x)
	{
		double dx = gfx.rtsXD(x);
		x1 += dx;
	}

	public void yShiftR(double y)
	{
		double dy = gfx.rtsYD(y);
		y1 += dy;
	}

	public void xShiftS(double x)
	{
		double dx = x;
		x1 += dx;
	}

	public void yShiftS(double y)
	{
		double dy = y;
		y1 += dy;
	}

    public String toString()
    {
        return name;
    }

    @Override
    public RealRectangle getBounds()
    {
        return new RealRectangle((x1 - w), (y1 - h), (2 * w), (2 * h));
    }

    @Override
    public Object clone()
    {
        OvalFX ovalClone = new OvalFX((int)x1, (int)y1, (int)w, (int)h, gfx);
        ovalClone.setLinearVel(vx, vy);
        ovalClone.setAngularVel(phi);

        if(updateEvents != null)
            for(int i = 0; i < ueSize; i++)
            {
                ovalClone.addUpdateEvent(updateEvents.elementAt(i));
            }

        ovalClone.setBImage(bim);

        return ovalClone;
    }
}
