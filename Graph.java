import java.util.*;
import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
import java.awt.image.*;


/**
*  A component for plotting graphs.
*
*  <code>Graph</code> inherits all the properties from <code>JPanel</code>
*  and overrides the <code>paint(Graphics)</code> function of the JPanel
*  to draw the graph.
*  The graph is drawn on a <code>BufferedImage</code> object and then
*  that is drawn to the <code>JPanel</code>.
*  The graph also contains axes, a crosshair, grids and also displays
*  the real mouse cordinates next to the mouse pointer. These features can
*  be turned on / off / toggled using functions <code>showAxes</code>, <code>showCrosshair</code>,
*  <code>showGrid</code>, <code>showCords</code>,<code>hideAxes</code>, <code>hideCrosshair</code>,
*  <code>hideGrid</code>, <code>hideCords</code>, <code>toggleAxes</code>, <code>toggleCrosshair</code>,
*  <code>toggleGrid</code>, <code>toggleCords</code>. The current states of the flags can be
*  accessed by their respective getter functions.
*  
*  The <code>Graph</code> object also maintains two lists of plotted points,
*  one for points plotted by the <code>plotPoint</code> and the <code>joinPlot</code>
*  functions.
*  
*  The <code>Graph</code> object operates on two separate cordinate systems,
*  			the real cordinate and,
*			the screen cordinate systems. 
*  The screen cordinates correspond to the cordinate system of the <code>JPanel</code>
*  on which the graph is drawn.
*
*  The real cordinate system corresponds to a system of cordinates determined by the 
*  arguments passed to the constructor( Wl, Wr, Hl, Hr in case of the first constructor and W and H in case of 
*  the second constructor). These parameters determine the upper and lower bounds of the graph as plotted
*  on the screen. For example, Wl = -3 and Wr = 3 means that when plotted the X - axis of the graph covers real 
*  X - cordinates between -3 and 3 as it stretches across the width of the graph. Similarly Hl and Hr defines
*  the visible bounds of the real Y - axis.										
*  
*  The <code>Graph</code> class also implements a MouseMotionListener, a MouseWheelListener and a KeyListener.
*  The KeyListener listens to keyPressed events. These events manage toggling of the axes, grids, crosshair
*  and the display of mouse cordinates as well switching between various plots.
*  The MouseMotionListener listens to both mouseDragged and mouseMoved events. These events updates the current
*  mouse location.
*   
*  These implemented events can be extended by adding a <code>GrapListener</code> object to the <code>Graph</code>
*  object. The <code>graphEvent</code> function is called in each of the implemented events and a unique key is 
*  passed to identify the calling event.
*
*  @author : Rahul B.
*/
public class  Graph extends JPanel implements MouseMotionListener, MouseWheelListener, KeyListener
{
	volatile BufferedImage graph;
	BufferedImage axes;
	protected Graphics2D g2d;
	
	protected double Wr;
	protected double Hr;
	protected double Wl;
	protected double Hl;
	protected double xf;
	protected double yf;
	
	protected int mX, mY;
	protected int width; 
	protected int height;
	protected double lX;
	protected double lY;
	protected double sX;
	protected double sY;
	protected double kx;
	protected double ky;
	
	protected Color bkg;
	protected Color aClr;
	protected Color crClr;
	protected Color plClr;
	protected Color gClr;
	
	protected Stroke aStr;
	protected Stroke crStr;
	protected Stroke plStr;
	protected Stroke gStr;
	
	protected boolean started = false;
	protected boolean maintainList = true;
	protected boolean showAxes = true;
	protected boolean axesRedraw = true;
	protected boolean showGrid = true;
	protected boolean showCrosshair =  true;
	protected boolean showCord = true;
	
	protected LinkedList< LinkedList< RPoint > > scatterList;
	protected LinkedList< LinkedList< RPoint > > lineList;
	
	protected GraphListener grListener;
	protected JPanel desk;
	
	public static int CTRL_KEY_PRESSED = 0;
	public static int MOUSE_WHEEL_MOVED = 1;
	public static int MOUSE_MOVED = 2;
	public static int MOUSE_DRAGGED = 3;
	
	public int rad = 6;

	/**
	* Constructor for <code>Graph</code>
	* Initialises a graph object with the specified component bounds 
	* and axes bounds.
	* 
	* @param Wl the lower X - axis bounds.
	* @param Wr the upper X - axis bounds.
	* @param Hl the lower Y - axis bounds.
	* @param Hr the upper Y - axis bounds.
	* @param width the width of the <code>Graph</code> component.
	* @param height the height of the <code>Graph</code> component.
	*/
	public Graph(double Wl, double Wr, double Hl, double Hr, int width, int height)
	{
		super();
		aClr  = Color.WHITE;
		crClr = new Color(255, 255, 255, 95);
		plClr = Color.WHITE;
		gClr  = new Color(255, 255, 255, 50);
		bkg   = Color.BLACK;
		
		setPreferredSize(new Dimension(width, height));
		
		graph = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		g2d   = (Graphics2D)graph.getGraphics();
		
		aStr = new BasicStroke(3.0f);
		crStr = g2d.getStroke();
		plStr = g2d.getStroke();
		gStr  = g2d.getStroke();
		lineList = new LinkedList< LinkedList< RPoint > >();
		scatterList = new LinkedList< LinkedList< RPoint > >(); 

		this.Wr = Wr;
		this.Hr = Hr;
		this.Wl = Wl;
		this.Hl = Hl;
		this.width = width;
		this.height = height;
		yf = Math.abs( Hr / ( Hr - Hl) ); 
		xf = Math.abs( Wl / ( Wr - Wl) );

		setKxKy(1, 1);

		addMouseMotionListener(this);
		addMouseWheelListener(this);
		
		JPanel padL = new JPanel();
		padL.setPreferredSize(new Dimension(10, height + 20));
		JPanel padR = new JPanel();
		padR.setPreferredSize(new Dimension(10, height + 20));
		JPanel padU = new JPanel();
		padU.setPreferredSize(new Dimension(width + 20, 10));
		JPanel padB = new JPanel();
		padB.setPreferredSize(new Dimension(width + 20, 10));
		
		desk = new JPanel();
		desk.setLayout(new BorderLayout());
		desk.add(padL, BorderLayout.WEST);
		desk.add(padR, BorderLayout.EAST);
		desk.add(padU, BorderLayout.NORTH);
		desk.add(padB, BorderLayout.SOUTH);
		desk.add(this, BorderLayout.CENTER);
	}
	
	/**
	*  Constructor for the <code>Graph</code>
	*  Initialises the <code>Graph</code> with specified 
	*  component bounds and axes bounds.
	*
	*  @param W defines the upper and lower X - axis bounds. The bounds
	*                   will vary from -W to W.
	*  @param H defines the upper and lower Y - axis bounds. The bounds
	*                   will vary from -H to H.
    *  @param width the width of the <code>Graph</code> component.
	*  @param height the height of the <code>Graph</code> component.
	*/
	public Graph(double W, double H, int width, int height)
	{
		this(-W, W, -H, H, width, height);
	}
	
	/**
	* Converts real Y cordinate to screen Y cordinate
	*/
	int rtsY(double y)
	{
		return (int)(height * yf - y * sY);
	}
	
	/**
	* Converts real X cordinate to screen X cordinate
	*/
	int rtsX(double x)
	{
		return (int)(width * xf  + x * sX);
	}
	
	/**
	* Converts screen Y cordinate to real Y cordinate 
	*/
	double strY(int y)
	{
		return (height * yf - y) / sY;
	}
	
	/**
	* Converts screen X cordinate to real X cordinate 
	*/
	double strX(int x)
	{
		return (x - width * xf) / sX;
	}
	
	/**
	* Returns the X - axis bounds of the <code>Graph</code> object.
	*/
	public double getWl()
	{
		return Wl;
	}
	
	/**
	* Returns the Y - axis bounds of the <code>Graph</code> object.
	*/
	public double getHl()
	{
		return Hl;
	}
	
	/**
	* Returns the X - axis bounds of the <code>Graph</code> object.
	*/
	public double getWr()
	{
		return Wr;
	}
	
	/**
	* Returns the Y - axis bounds of the <code>Graph</code> object.
	*/
	public double getHr()
	{
		return Hr;
	}
	

	public int getViewWidth()
	{
		return width;
	}

	public int getViewHeight()
	{
		return height;
	}
	/**
	* Plots a real point (x, y) on the screen after converting to
    * screen cordinates.	
	*/
	public void plotPoint(double x, double y)
	{
		g2d.setColor(plClr);
		g2d.setStroke(plStr);
		g2d.fillOval(rtsX(x) - rad/2, rtsY(y) - rad/2, rad, rad);

		if(!started)
		{
		    started = true;
			if(maintainList)
			{
				LinkedList<RPoint> lst = new LinkedList<RPoint>();
				lst.addLast(new RPoint(x, y));
				scatterList.addLast(lst);
			}
		}
		else
		{
			if(maintainList)
				scatterList.getLast().addLast(new RPoint(x, y));
		}
	}
	
	public void drawVector(double x, double y, double x0, double y0, double L, double THETA)
	{
		g2d.setColor(plClr);
		g2d.setStroke(plStr);
		g2d.drawLine(rtsX(x), rtsY(y), rtsX(x0), rtsY(y0));
		double tanT = (rtsY(y) - rtsY(y0)) * 1.0 / (rtsX(x) - rtsX(x0));
		double angle = Math.atan(tanT);
	    double dT = THETA * Math.PI / 180;
	    double a1 = angle + dT;
	    double a2 = angle - dT;

	    int dx1 = (int)(Math.cos(a1) * L );
	    int dx2 = (int)(Math.cos(a2) * L );
	    int dy1 = (int)(Math.sin(a1) * L );
	    int dy2 = (int)(Math.sin(a2) * L );

	    g2d.drawLine(rtsX(x), rtsY(y), rtsX(x) - dx1, rtsY(y) - dy1);
	    g2d.drawLine(rtsX(x), rtsY(y), rtsX(x) - dx2, rtsY(y) - dy2);
	}

	public void drawVector(double x, double y)
	{
		drawVector(x, y, 0, 0, (Math.sqrt( Math.abs((Wr - Wl) * (Hr - Hl))) ), 30);
	}

	public void drawVector(double x, double y, double L, double THETA)
	{
		drawVector(x, y, 0, 0, L, THETA);
	}

	public void setPlotRadius(int rad)
	{
		this.rad = rad;
	}

	/**
	* Plots a real point (x, y) on the screen and joins it 
	* to the previous point plotted with this function.
	* The continuiuty of the plotting can be interrupted
	* by calling <code>endPlot</code> which will set 
	* <code>started</code> to false.
	*/
	public void joinPlot(double x, double y)
	{
		g2d.setColor(plClr);
		g2d.setStroke(plStr);
		
		if(!started)
		{
			g2d.drawLine(rtsX(x), rtsY(y), rtsX(x), rtsY(y));
			lX = x;
			lY = y;
		    started = true;
			if(maintainList)
			{
				LinkedList<RPoint> lst = new LinkedList<RPoint>();
				lst.addLast(new RPoint(x, y));
				lineList.addLast(lst);
			}
		}
		else
		{
			g2d.drawLine(rtsX(x), rtsY(y), rtsX(lX), rtsY(lY));
			lX = x;
			lY = y;
			if(maintainList)
				lineList.getLast().addLast(new RPoint(x, y));
		}

	}
	
	
	/**
	* Sets <code>started</code> to false
	*/
	public void endJoin()
	{
		started = false;
	}
	
	/**
	* Plots the axes of the graph.
	*/
	protected void plotAxes(Graphics2D aG2d)
	{
		aG2d.setColor(aClr);
		aG2d.setStroke(aStr);
		
		aG2d.drawLine((int)(width * xf), 0, (int)(width * xf), height);
		aG2d.drawLine(0, (int)(height * yf), width, (int)(height * yf));
		
		this.kx = kx;
		this.ky = ky;
		
		for(double i = Wl; i <= Wr; i += kx)
		{
			if(i == 0)
				continue;
			aG2d.drawString("" + (float)i, rtsX(i), (int)(height * yf + 16));
		}
		for(double i = Hl; i <= Hr; i += ky)
		{
			if(i == 0)
				continue;
			aG2d.drawString("" + (float)i, (int)(width * xf + 8), rtsY(i) + 4);
		}
		
	}
	
	protected void drawGrid(Graphics2D g)
	{
		g.setColor(gClr);
		g.setStroke(gStr);
		
		for(double i = Wl; i < Wr; i++)
		{
			g.drawLine(rtsX(i), rtsY(Hr), rtsX(i), rtsY(Hl));
		}
		for(double i = Hl; i < Hr; i++)
		{
			g.drawLine(rtsX(Wl), rtsY(i), rtsX(Wr), rtsY(i));
		}
	}
	
	public void drawCrosshair(Graphics2D g)
	{
		g.setColor(crClr);
		g.setStroke(crStr);
		
		g.drawLine(mX, height, mX, 0);
		g.drawLine(0, mY, width, mY);
	}

	/**
	* Returns a <code>BufferedImage</code> containing the sub-area of the given graph
	* as specified by the parameters in real cordinates, scaled to a specifed width and height.
	* @param x1 the abscissa of upper left corner of the sub-area in real cordinates.
	* @param y1 the ordinate of the upper left corner of the sub-area in real cordinates.
	* @param x2 the abscissa of the lower right corner of the sub-area in real cordinates.
	* @param y2 the ordinate of thr lower right corner of the sub-area in real coordinates.
	* @param w the width of the destination image.
	* @param h the height of the destination image.
	*/
	public BufferedImage zoomR(double x1, double y1, double x2, double y2, int w, int h)
	{
		BufferedImage zoom = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
		((Graphics2D)zoom.getGraphics()).drawImage(graph, 0, 0, w, h, rtsX(x1), rtsY(y1), rtsX(x2), rtsY(y2), null);
		return zoom;
	}
	
	/**
	* Returns a <code>BufferedImage</code> containing the sub-area of the given graph
	* as specified by the parameters in screen cordinates, scaled to a specifed width and height.
	* @param x1 the abscissa of upper left corner of the sub-area in screen cordinates.
	* @param y1 the ordinate of the upper left corner of the sub-area in screen cordinates.
	* @param x2 the abscissa of the lower right corner of the sub-area in screen cordinates.
	* @param y2 the ordinate of thr lower right corner of the sub-area in screen coordinates.
	* @param w the width of the destination image.
	* @param h the height of the destination image.
	*/
	public BufferedImage zoomS(int x1, int y1, int x2, int y2, int w, int h)
	{
		BufferedImage zoom = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
		((Graphics2D)(zoom.getGraphics())).drawImage(graph, 0, 0, w, h, x1, y1, x2, y2, null);
		return zoom;
	}
	
	/**
	* Clears the graph, including the axes.
	*/
	public void clearGraph()
	{
			g2d.setColor(bkg);
			g2d.fillRect(0, 0, width, height);
	}
	
	/**
	* Clears all plotted lines and points without clearing the axes. The maintained 
	* list of all points are cleared.
	*/
	public void clearPlot()
	{
		g2d.setColor(bkg);
		g2d.fillRect(0,0, width, height);
		if(maintainList)
		{
			lineList.clear();
			scatterList.clear();
		}
	}
	
	/**
	* Draws the given image on the graph.
	* @param img the image to be drawn.
	* @param showAxes determine whether or not to plot axes.
	*/
	public void drawImage(BufferedImage img, boolean showAxes)
	{
		clearGraph();
		g2d.drawImage(img, 0, 0, null);
	}
	
	/**
	* Set the background color.
	*/
	public void setBackgroundColor(Color c)
	{
		bkg = c;
		clearGraph();
	}
	
	/**
	* Set the foreground color.
	*/
	public void setAxesColor(Color c)
	{
		aClr = c;
	}
	
	public void setGridColor(Color c)
	{
		gClr = c;
	}
	
	public void setCrosshairColor(Color c)
	{
		crClr = c;
	}
	
	public void setPlotColor(Color c)
	{
		plClr = c;
	}
	
	
	public void setAxesStroke(Stroke c)
	{
		aStr = c;
	}
	
	public void setGridStroke(Stroke c)
	{
		gStr = c;
	}
	
	public void setCrosshairStroke(Stroke c)
	{
		crStr = c;
	}
	
	public void setPlotStroke(Stroke c)
	{
		plStr = c;
	}
	
	public void setGraphListener(GraphListener grListener)
	{
		this.grListener = grListener;
	}

	/**
	* Change the X - axis unit interval.
	*/
	public void setKx(double kx)
	{
		this.kx = kx;
		sX = (width / (Wr - Wl));
	}
	
	/**
	* Change the Y - axis unit interval.
	*/
	public void setKy(double ky)
	{
		this.ky = ky;
		sY = (height / (Hr - Hl));
	}
	
	public void setKxKy(double kx, double ky)
	{
		setKx(kx);
		setKy(ky);
	}
	
	/**
	* Plots the given list of points.
	*/
	public void splotList(LinkedList<RPoint> points)
	{
		for(RPoint point : points)
		{
			plotPoint(point.x, point.y);
		}
	}

	public void splotLists(LinkedList < LinkedList<RPoint> > pointLists)
	{
		for(LinkedList<RPoint> points : pointLists)
		{
			for(RPoint point : points)
			{
				plotPoint(point.x, point.y);
			}
		}
	}
	
	
	/**
	* Does a joinPlot of the given list of plot list
	*/
	public void jplotLists(LinkedList< LinkedList<RPoint> > lines)
	{
		for(LinkedList<RPoint> line : lines)
		{
			endJoin();
			for(RPoint point : line)
			{
				joinPlot(point.x, point.y);
			}
		}
	}
	
	public void jplotList(LinkedList< RPoint > line)
	{
		for(RPoint point : line)
		{
			joinPlot(point.x, point.y);
		}
		endJoin();
	}
	
	public void addToJList(LinkedList < RPoint > line)
	{
		lineList.addLast(line);
	}

	public void addToSList(LinkedList < RPoint > points)
	{
		scatterList.addLast(points);
	}

	public void hideCords()
	{
		showCord = false;
	}
	
	public void showCords()
	{
		showCord = true;
	}
	
	public void toggleCords()
	{
		showCord = !showCord;
	}
	
	public boolean areCordsVisible()
	{
		return showCord;
	}
	public void hideAxes()
	{
		showAxes = false;
	}
	
	public void showAxes()
	{
		showAxes = true;
	}
	
	public void toggleAxes()
	{
		showAxes = !showAxes;
	}
	
	public boolean areAxesVisible()
	{
		return showAxes;
	}
	
	public void hideCrosshair()
	{
		showCrosshair = false;
	}
	
	public void showCrosshair()
	{
		showCrosshair = true;
	}
	
	public void toggleCrosshair()
	{
		showCrosshair = !showCrosshair;
	}
	
	public boolean areCrosshairVisible()
	{
		return showCrosshair;
	}
	
	public void hideGrid()
	{
		showGrid = false;
	}
	
	public void showGrid()
	{
		showGrid = true;
	}
	
	public void toggleGrid()
	{
		showGrid = !showGrid;
	}
	
	public boolean areGridVisible()
	{
		return showGrid;
	}
	
	public RPoint getMouseLocationR()
	{
		return new RPoint(strX(mX), strY(mY));
	}
	
	public SPoint getMouseLocationS()
	{
		return new SPoint(mX, mY);
	}
	
	public JPanel getDesk()
	{
		return desk;
	}

	@Override
	public void paintComponent(Graphics g)
	{
		//super.paintComponent(g);
		if(graph != null)
		{
			g.drawImage(graph, 0, 0, null);
			
			if(showAxes)
				plotAxes((Graphics2D)g);
			
			if(showGrid)
				drawGrid((Graphics2D)g);
			
			if(showCrosshair)
				drawCrosshair((Graphics2D)g);
			if(showCord)
			{
				RPoint mp = getMouseLocationR();
				((Graphics2D)g).setStroke(plStr);
				g.drawString((float)mp.x + ", " + (float)mp.y, mX, mY);
			}
		}
	}
	
	public void mouseDragged(MouseEvent e)
	{
		int oX = mX;
		int oY = mY;

		if(grListener != null)
			grListener.graphEvent(MOUSE_DRAGGED, e, this);
		mX = e.getX();
		mY = e.getY();

	}
	
	public void mouseMoved(MouseEvent e)
	{
		int oX = mX;
		int oY = mY;

		if(grListener != null)
			grListener.graphEvent(MOUSE_MOVED, e, this);
		mX = e.getX();
		mY = e.getY();

		repaint();
	}
	
	public void keyPressed(KeyEvent e)
	{
		if(e.getKeyCode() == (int)'G' || e.getKeyCode() == (int)'g')
			toggleGrid();
		
		else if(e.getKeyCode() == (int)'C' || e.getKeyCode() == (int)'c')
			toggleCrosshair();
		
		else if(e.getKeyCode() == (int)'A' || e.getKeyCode() == (int)'a')
			toggleAxes();
		
		else if(e.getKeyCode() == (int)'T' || e.getKeyCode() == (int)'t')
			toggleCords();
		
		else if(e.getKeyCode() >= (int)'1' && e.getKeyCode() <= (int)'9')
		{
			if(lineList != null && lineList.size() > (int)(e.getKeyCode() - '1') )
			{
				clearGraph();
				maintainList = false;
				LinkedList < RPoint > lst = lineList.get((int)(e.getKeyCode() - '1'));
				if(lst != null)
				{
					jplotList(lst);
				}
				else
				{
					return;
				}
				maintainList = true;
			}
			else
			{
				return;
			}
			
		}
		
		else if(e.getModifiers() == 1 && e.getKeyCode() >= (int)'1' && e.getKeyCode() <= (int)'9')
		{
			System.out.println(e.getModifiers());
			if(scatterList != null)
			{
				clearGraph();
				maintainList = false;
				LinkedList < RPoint > lst = scatterList.get((int)(e.getKeyCode() - '1'));
				if(lst != null)
				{
					splotList(lst);
				}
				else
				{
					return;
				}
				maintainList = true;
			}
			else
			{
				return;
			}
		}

		else if(e.getKeyCode() == '0')
		{
			maintainList = false;
			if(lineList != null)
			{
				jplotLists(lineList);
			}
			else
			{
				return;
			}
			maintainList = true;
		}
		
		else if(e.getModifiers() == 2 && grListener != null) // CTRL key pressed
		{
			if(e.getModifiers() == 2)
			{
				grListener.graphEvent(CTRL_KEY_PRESSED, e, this);
			}
			else
			{
				return;
			}
		}

		else
		{
			return;
		}
		repaint();
	}
	
	public void mouseWheelMoved(MouseWheelEvent mwe)
	{
		if(grListener != null)
			grListener.graphEvent(MOUSE_WHEEL_MOVED, mwe, this);
	}
	
	public void keyReleased(KeyEvent e)
	{
		
	}
	
	public void keyTyped(KeyEvent e)
	{
		
	}
}

