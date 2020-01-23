import java.util.*;
import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
import java.awt.image.*;

public class OvalFX extends Drawable
{
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
        (bim.getGraphics()).drawOval((int)(x1 - w / 2.0), (int)(y1 - h / 2.0), (int)(w), (int)(h));
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





