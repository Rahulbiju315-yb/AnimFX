import java.util.*;
import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
import java.awt.image.*;



/**
* An Equation Plotter.
* @author  Rahul B.
*/
public class EquationPlot
{
	static volatile double K = -2.7;
	static volatile double dx;
	static volatile double epsilon = Math.pow(10, -7.5);
	static volatile double di = 1;
	static volatile double dj = 1;
	static lambda l;
	//static Thread tr;
	
	static volatile boolean start = true;
	static volatile boolean stop = false;
	static volatile boolean redraw = false;
	
	public static void main(String args[])
	{
		Graph grp = new Graph(10, 10, 1500, 900);
		l = (double x, double y) -> //(Math.exp(x) - y);
		((x*Math.cos(2*x)*Math.cos(x*y)) + y*Math.tan(x)*Math.cos(x*y)+Math.cos(x)*Math.cos(y)*x*y
		                            *(x*Math.cos(3*x)*Math.cos(x) - y*Math.tan(x)*Math.cos(x*y)+Math.cos(x)*Math.cos(y)*x*y)
				             *(x*Math.cos(4*x)*Math.cos(x*y) + y*Math.tan(x)*Math.cos(x*y)- Math.cos(x/y)*Math.cos(y)*x*y)*(x*Math.cos(5*x)*Math.cos(x*y) - y*Math.tan(x)*Math.cos(x*y)- Math.cos(x/y)*Math.cos(y)*x*y));
		 dx = Math.pow(10, K + 0.5*(Math.log10((grp.getHr() - grp.getHl()) * (grp.getWr() - grp.getWl())) / 4.0 - 1));
		
		grp.setGraphListener(new GraphListener()
		{
			public void graphEvent(int m, InputEvent ie, Graph g)
			{
				if(m == Graph.CTRL_KEY_PRESSED)
				{
					int c = ((KeyEvent)ie).getKeyCode();

					if(c == (int)('X') || c == (int)('x'))
					{
						stop = true;
						grp.clearGraph();
						start = false;
					} 
					else if(c == (int)('P') || c == (int)('p'))
					{
						start = !start;
					}
					else if(c == (int)('S') || c == (int)('s'))
					{
						stop = true;
						start = true;
						grp.clearGraph();
						redraw = true;
					}
					else if(c == (int)('E') || c == (int)('e'))
					{
						di -= 0.1;
						dj -= 0.1;
					}
					else if(c == (int)('D') || c == (int)('d'))
					{
						di += 0.1;
						dj += 0.1;
					}
				}
				else if(m == Graph.MOUSE_WHEEL_MOVED)
				{
					di -= 0.1 * ((MouseWheelEvent)ie).getWheelRotation();
					dj -= 0.1 * ((MouseWheelEvent)ie).getWheelRotation();
				}
			}
		});
		
		JFrame frame = new JFrame("Hello World");
		frame.setSize(1500,1500);
		frame.add(grp.getDesk());
		frame.setVisible(true);
		frame.addKeyListener(grp);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		grp.repaint();
		
		grp.setKx(1);
		grp.setKy(1);
		
		LinkedList<RPoint> lnk = new LinkedList<RPoint>();
		lnk.addLast(new RPoint(1, 1));
		lnk.addLast(new RPoint(2, 1));
		lnk.addLast(new RPoint(1, 3));
		lnk.addLast(new RPoint(4, 1));
		lnk.addLast(new RPoint(1, 5));
		lnk.addLast(new RPoint(6, 1));
		lnk.addLast(new RPoint(7, 1));
		
		LinkedList<RPoint> lnk1 = new LinkedList<RPoint>();
		lnk1.addLast(new RPoint(3, 1));
		lnk1.addLast(new RPoint(2, 5));
		lnk1.addLast(new RPoint(1, 3));
		lnk1.addLast(new RPoint(4, 7));
		lnk1.addLast(new RPoint(8, 2));
		lnk1.addLast(new RPoint(6, 5));
		lnk1.addLast(new RPoint(7, 2));

		grp.setBackgroundColor(Color.white);
		grp.setAxesColor(Color.BLACK);
		grp.setCrosshairColor(new Color(0, 0, 0, 95));
		grp.setPlotColor(Color.BLACK);
		grp.setGridColor(new Color(0, 0, 0, 50));
		
		grp.setPlotStroke(new BasicStroke(2f));
		
		// grp.splotList(lnk1);
		// grp.splotList(lnk);
		 grp.setPlotRadius(4);
		draw(grp, l);

		//grp.drawVector(5, 5);
	}
	
	/**
	* Plots a given function defined by the interface <code>lambda</code> on 
	* the given graph object.
	* - dy2
	* @param grp the <code>graph</code> object to plot the given equation.
	* @param lm the equation to be plotted.
	*/
	public static void draw(Graph grp, lambda lm)
	{
		for(double i = (grp.getWl()/dx); i < (grp.getWr()/dx); i += di)
		{
			for(double j = (grp.getHl()/dx); j < (grp.getHr()/dx); j += dj)
			{
				while(!start);
				double ll = lm.eval(i * dx, j * dx);
				double ul = lm.eval(i * dx, (j + 1) * dx);
				double lr = lm.eval((i + 1) * dx, j * dx);
				double ur = lm.eval((i + 1) * dx, (j + 1) * dx);
				
				if(ll * ur <= epsilon || lr * ul <= epsilon)
				{
					grp.plotPoint((i*dx), (j*dx));
					//System.out.print("Yea");
					
				}
				if(stop)
					break;
			}
			if(stop)
			{
				stop = false;
			    start = true;
				break;
			}
			grp.repaint();
		}
		
		while(!redraw);
		redraw = false;
		stop = false;
		draw(grp, lm);
		
	}
	
	public static void drawPolar(Graph grp, lambda lm, double R, double THETA)
	{  
		double dx = Math.pow(10, K);
		double epsilon = Math.pow(10, -3);
		for(double i = 0; i < (THETA/dx); i += di)
		{
			for(double j = -(int)(R/dx); j < (R/dx); j += dj)
			{
				while(!start);
				double ll = lm.eval(j * dx, i * dx);
				double ul = lm.eval(j * dx, (i + 1) * dx);
				double lr = lm.eval((j + 1) * dx, i * dx);
				double ur = lm.eval((j + 1) * dx, (i + 1) * dx);
				
				
				if(ll * ur <= epsilon || lr * ul <= epsilon)
				{
					grp.plotPoint((j*dx*Math.cos(i*dx)), (j*dx*Math.sin(i*dx)));
				}
				if(stop)
					break;
			}
			
			if(stop)
				{
					stop = false;
					start = true;
					break;
				}
			grp.repaint();
		}
		while(!redraw);
		redraw = false;
		drawPolar(grp, lm, R, THETA);
	}
	
}
