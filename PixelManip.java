import java.util.*;
import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
import java.awt.image.*;

public class PixelManip
{
    Hashtable<SPoint, Color> htb;
    public boolean putPixel(int x, int y, BufferedImage bim, boolean replot)
    {
        try{
            int rgb = bim.getGraphics().getColor().getRGB();
            //Graphics2D g2d = (Graphics2D)bim.getGraphics();
            if(!replot)
            {
                if(!pushed(x, y))
                    pushPoint(x, y, bim);
                bim.setRGB(x, y, rgb);

            }
            else
            {
                unplotPixel(x, y, bim);
            }
        }
        catch(ArrayIndexOutOfBoundsException e1)
        {
            //e1.printStackTrace(System.err);
            return false;
        }
        return true;
    }

    protected void unplotPixel(int x, int y, BufferedImage bim)
    {
        Color c;
        c = htb.get(new SPoint(x, y));
        //System.out.println("Unplotting " + x + "," + y);
        //System.out.println("White - " + Color.WHITE.getRGB() + " ACTUAL " + c.getRGB());
        try
        {
            if(c == null)
                c = new Color(bim.getRGB(x, y));
            bim.setRGB(x, y, c.getRGB());
            htb.remove(new SPoint(x, y));
        }
        catch(ArrayIndexOutOfBoundsException e1)
        {
            //e1.printStackTrace(System.err);
        }
    } 

    public boolean pushed(int x, int y)
    {
        return htb.get(new SPoint(x, y)) == null ? false : true;
    }

    public void pushPoint(int x, int y, BufferedImage bim)
    {
        try
        {
            //System.out.println("Pushed " + x + "," + y + " with rgb = " + (bim.getRGB(x, y) == Color.BLACK.getRGB()));
            htb.put(new SPoint(x, y), new Color(bim.getRGB(x, y)));
        }
        catch(ArrayIndexOutOfBoundsException e1)
        {
            //e1.printStackTrace(System.err);
        }
    }

    public SPoint rotatePoint(int x, int y, int x0, int y0, double theta)
    {
        double xN = (x - x0) * (Math.cos(theta) - Math.sin(theta)) + x0;
        double yN =  - (y - y0) * (Math.sin(theta) + Math.cos(theta)) - y0;

        return new SPoint((int)xN, -(int)yN);
    }

    public SPoint rotatePoint(int x, int y, double theta, GraphFX gfx)
    {
        return rotatePoint(x, y, 0, 0, theta);
    }

    

    public RPoint rotatePoint(double x, double y, double x0, double y0, double theta, GraphFX gfx)
    {
        double xN = (x - x0) * (Math.cos(theta) - Math.sin(theta)) + x0;
        double yN =  - (y - y0) * (Math.sin(theta) + Math.cos(theta)) - y0;

        return new RPoint(xN, -yN);
    }

    public RPoint rotatePoint(double x, double y, double theta, GraphFX gfx)
    {
        return rotatePoint(x, y, 0.0, 0.0, theta, gfx);
    }

}