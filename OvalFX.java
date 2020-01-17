import java.util.*;
import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
import java.awt.image.*;

public class OvalFX extends Drawable
{
    BufferedImage bim;

    double x1, y1;
    double w, h, ow, oh;
    int csw = 1, csh = 1;

    boolean replot = false;

    String ID;
    String name;

    static int count = 0;
    public OvalFX(int x1, int y1, int w, int h, GraphFX gfx)
    {
        this.x1 = x1;
        this.y1 = y1;
        this.w = w;
        this.h = h;
        this.ow = w;
        this.oh = h;
        this.gfx = gfx;
        //System.out.println("" + this.x1 + " " + this.y1 + " " + this.w + " " + this.h);
        htb = new Hashtable<SPoint, Color>();
        count++;
        ID = "OvalFX_" + count;
        name = "OvalFX_" + count;
    }

    public void setID(String ID)
    {
        this.ID = ID;
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
        int x, y;
        x = 0;
        y = (int)h;

        double u, v, err = 0;
        double h2 = h * h;
        double w2 = w * w;
        u = h2;
        v = w2 * (2 * h - 1);

        plotEllipsePoint(x, y);

        while(h2 * x < w2 * y)
        {
            if(Math.abs(err + u - v) < Math.abs(err + u))
            {
                err += u - v;
                x++;
                y--;
                u += 2 * h2;
                v -= 2 * w2;
            }
            else
            {
                err += u;
                x++;
                u += 2 * h2;
            }
            plotEllipsePoint(x, y);
        }

        double temp = h;
        h = w;
        w = temp;

        x = 0;
        y = (int)h;

        double temp2 = h2;
        h2 = w2;
        w2 = temp2;

        u = h2;
        v = w2 * (2 * h - 1);
        plotEllipsePoint(y, x);

        while(h2 * x < w2 * y)
        {
            if(Math.abs(err + u - v) < Math.abs(err + u))
            {
                err += u - v;
                x++;
                y--;
                u += 2 * h2;
                v -= 2 * w2;
            }
            else
            {
                err += u;
                x++;
                u += 2 * h2;
            }
            plotEllipsePoint(y, x);
        }

        temp = h;
        h = w;
        w = temp;
    } 

    public void plotEllipsePoint(int x, int y)
    {
        putPixel((int)x1 + x, (int)y1 + y, bim, replot);
        putPixel((int)x1 - x, (int)y1 + y, bim, replot);
        putPixel((int)x1 + x, (int)y1 - y, bim, replot);
        putPixel((int)x1 - x, (int)y1 - y, bim, replot);
    }

    public boolean isWithinBounds()
    {
        return true;
    }
    
    public void unplot()
    {
        replot = true;
        plot();
        replot = false;
    }


    public void update()
    {
    

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
        return new Rectangle((int)(x1 - w), (int)(y1 - h), (int)(2 * w), (int)(2 * h));
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





