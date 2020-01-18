import java.util.*;
import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
import java.awt.image.*;

public class GraphFX extends Graph
{
	volatile Vector < Drawable > aObjs;
	Hashtable <String, Drawable> hashID;
	Hashtable <String, String> hashName;

	volatile int fRate;
	volatile int n;
	volatile boolean started;
	static int count;
	volatile boolean pause;

	Graphics g;
	
	Thread timer;
	Plot plot;

	static Random r = new Random();
	public GraphFX(double W, double H, int width, int height)
	{	
		this(-W, W, -H, H, width, height);
	}

	public GraphFX(double Wl, double Wr, double Hl, double Hr, int width, int height)
	{
		super(Wl, Wr, Hl, Hr, width, height);
		showAxes = false;
		showCrosshair = false;
		showCord = false;

		fRate = 20;
		count = 0;
		started = false;
		plot = null;

		aObjs = new Vector< Drawable >();
		hashID = new Hashtable<String, Drawable>();
		hashName = new Hashtable<String, String>();

		timer = new Thread(
			
				new Runnable()
				{
					@Override
					public void run()
					{
						double err = 0;
						while(true)
						{
							long beforeTime = System.currentTimeMillis();
							if(pause)
							{
								continue;
							}
							for(int i = n - 1; i >= 0; i--)
							{
								if(started)
									aObjs.elementAt(i).unplot();
							}

							for(int i = 0; i < n; i++)
							{	
								if(started)
								{
									aObjs.elementAt(i).update(err);
								}
								else
								{
									started = true;
								}

								aObjs.elementAt(i).plot();
							}
							err = 0;
							//paintComponent(getGraphics());
							repaint();
							try
							{
								long sleepTime = fRate - System.currentTimeMillis() + beforeTime;
								//System.out.println(sleepTime);
								if(sleepTime >= 0)
									Thread.sleep(sleepTime);
								else
									err = -(double)sleepTime / fRate;

							}catch(InterruptedException ex)
							{
								ex.printStackTrace(System.err);
								//System.out.println("Yup");
							}

						}
						
					}
				
			});
		//timer.start();
	}

	public Vector cloneInstance()
	{
		Vector<Drawable> cloneVec = new Vector<Drawable>();
		for(int i = 0; i < n; i++)
		{
			cloneVec.add((Drawable)aObjs.elementAt(i).clone());
		}

		return cloneVec;
	}

	public void start()
	{
		timer.start();
	}

	public Graphics getGraphics()
	{
		return super.getGraphics();
	}

	public void addDrawable(Drawable drw)
	{
		if(hashID.get(drw.getID()) == null)
		{
			drw.setBImage(graph);
			drw.setID("" + count);
			count++;

			hashID.put(""+count, drw);
			hashName.put(drw.toString(), drw.getID());

			aObjs.add(drw);
			n++;
			//System.out.println("Added " + drw.toString());
		}

		else
		{
			System.out.println("Cannot add Drawable as Drawable " + drw.toString() + " already exists.");
		}
	}

	public void removeDrawable(String name)
	{
		if(hashName.get(name) != null)
		{
			String ID =	hashName.get(name);
			int n = Integer.parseInt(ID);
			aObjs.elementAt(n).unplot();
			aObjs.remove(n); 
			hashName.remove(name);
		}
	}

	public void removeAll()
	{
		for(int i = n - 1; i >= 0; i--)
		{	
			if(started)
				aObjs.elementAt(i).unplot();
		}

		aObjs = new Vector< Drawable >();
		hashID = new Hashtable<String, Drawable>();
		hashName = new Hashtable<String, String>();

	}

	public void setFRate(int fRate)
	{
		this.fRate = fRate;
	}

	public void replot()
	{
		removeAll();

		if(plot == null)
		{
			System.err.println("Error !! No plot found.");
		}
		else
		{
			plot.plot(this);
		}
	}

	public void setPlot(Plot plot)
	{
		this.plot = plot;
		plot.plot(this);
	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent e)
	{
		fRate += 1 * e.getWheelRotation();
		if(fRate < 10)
			fRate = 10;
	}

	@Override
	public void keyPressed(KeyEvent e)
	{
		super.keyPressed(e);
		if(e.getKeyCode() == (int)'P' || e.getKeyCode() == (int)'p')
		{
			pause = !pause;
		}
	}

	@Override
	public void paintComponent(Graphics g)
	{
		super.paintComponent(g);
	}

	@Override
	public void update(Graphics g)
	{
		paintComponent(g);
	}

	public Drawable getDrawable(String name)
	{
		String ID = hashName.get(name);

		if(ID != null)
		{
			Drawable dr = aObjs.elementAt(Integer.parseInt(ID));
			return dr;
		}
		else
		{
			return null;
		}
	}

	public static void main(String args[])
	{
		EventQueue.invokeLater(() -> {
            JFrame jf = new JFrame("FX Panel");
			GraphFX gfx = new GraphFX(60, 40, 1500, 1000);
			gfx.setKxKy(5, 5);

			Plot plot = new Plot()
			{	
				public void plot(GraphFX gfx)
				{
					int L = 100;
					for(int i = 0; i < L; i++)
					{
						double r1 = i / (double)L * 20;
						double r2 = i / (double)L * 20;
						LineFX l1 = new LineFX(-60d + i , -37.5d + i, -55d + i, -37.5d + i, gfx);
						LineFX l2 = new LineFX(-57.5d + i , -40d + i, -57.5d + i, -35d + i, gfx);
						//GroupFX grp = new GroupFX(gfx, l1, l2);

						l1.addUpdateEvent(UpdateFX.ReverseLVelocityOnCollision);
						l2.addUpdateEvent(UpdateFX.ReverseLVelocityOnCollision);

						l1.setLinearVel(1.6, 1.6);
						l2.setLinearVel(1.6, 1.6);
						//grp.setAngularVel(0.05);

						gfx.addDrawable(l1);
						gfx.addDrawable(l2);
					}
				}
			};

			gfx.setPlot(plot);
			gfx.start();
			jf.add(gfx);
			jf.addKeyListener(gfx);
			jf.setSize(1500, 1000);
			jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			jf.setVisible(true);
        });
		
	}

}









