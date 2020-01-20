import java.util.*;
import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
import java.awt.image.*;



/**
 * An extended form of the {@code Graph} class which contains animation
 * functionalities built-in.
 *
 * <p>
 * The animation functionalitites are implemented using classes which
 * class {@code Drawable}. Each {@code Drawable} is stored in a vector and
 * plotted at rates determined by {@code fTime}.
 *
 * <p>
 * The entire clear-update-plot cycle occurs within a {@code Thread} defined
 * in the constructor. The {@code Thread.sleep()} is called passing the
 * difference between the fTime and time taken for update and plot as the parameter. This ensures
 * that frames are plotted at a constant rate. If this happens to be negative (meaning a lot of time
 * has been spend in the plot-update part of the cycle) the {@code Thread.sleep} funciton is not
 * called but rather the next update is called with some non - zero error parameter depending on the
 * delay.
 *
 * <p>
 * Unlike a {@code Graph}, {@code GraphFX} has {@code showAxes}, {@code showCrosshair}, {@code showCord}
 * disabled by default, though they can still be toggled on/off using the keyboard.
 *
 * @see Drawable
 * @see Graph
 */

public class GraphFX extends Graph
{

	/**
	 * The {@code Vector} to store all the Drawables to be plotted.
	 * They are plotted sequentially, in the increasing order of their
	 * indices as stored in the {@code Vector}.
	 *
	 * @see Drawable
	 */
	volatile Vector < Drawable > aObjs;

	/**
	 * The {@code Hashtable} to store (name, {@code Drawable}) pairs.
	 * Each {@code Drawable} must have a unique name to be added to
	 * the {@code Vector} aObj.
	 *
	 * @see Drawable
	 * @see Drawable#setName
	 * @see Drawable#getName
	 */
	Hashtable <String, Drawable> hashName;

	/**
	 * The time of persistence of each frame in the units of milliseconds.
	 *
	 * @see timer
	 */
	volatile int fTime;

	/**
	 * The size of {@code Vector} {@code aObjs}.
	 *
	 * @see #aObjs
	 */
	volatile int n;

	/**
	 * Flag variable to check if the animation has been paused.
	 */
	volatile boolean pause;

	/**
	 * The {@code Thread}  which initiates and maintains the plot-update cycle.
	 * The {@code Thread} is initialised in the constructor but not started then.
	 * The {@code Thread} must be started by calling {@code start} method in
	 * the {@code GraphFX} object after adding all the {@code Drawables} to it.
	 *
	 * @see #start
	 */
	Thread timer;

    /**
     * The {@code Plot} for the {@code GraphFX}.A plot specifies the starting
     * configuration of the animation.
     *
     * @see Plot
     */
	Plot plot;

	/**
	 * Constructs a {@code GraphFX} with the specified width and height,
	 * with
	 * <pre>
	 *     left  X - axis bound as -W,
	 *     right X - axis bound as W,
     *     lower Y - axis bound as -H,
     *	   upper Y - axis bound as H
	 * </pre>
	 *
	 * @param W defines the left and right bounds for X - axis.
	 * @param H defines the upper and lower bounds for Y - axis.
	 *
	 */
	public GraphFX(double W, double H, int width, int height)
	{
		this(-W, W, -H, H, width, height);
	}

	/**
	 * Constructs a {@code GraphFX} with the specified axes bounds and specified
	 * width and height.
	 *
	 * <p>
	 * Note that {@code showAxes}, {@code showCord}, {@code showCrosshair} inherited
	 * from the {@code Graph} class are set to {@code false} by default.
	 * The default value of {@code fRate} is set to 20.
	 *
	 * @param Wl left X - axis bound
	 * @param Wr right X - axis bound
	 * @param Hl lower Y - axis bound
	 * @param Hr upper Y - axis bound
	 *
	 */
	public GraphFX(double Wl, double Wr, double Hl, double Hr, int width, int height)
	{
		super(Wl, Wr, Hl, Hr, width, height);
		showAxes = false;
		showCrosshair = false;
		showCord = false;

		fTime = 20;
		started = false;
		plot = null;

		aObjs = new Vector< Drawable >();
		hashName = new Hashtable<String, Drawable>();

		timer = new Thread(

				new Runnable()
				{
					@Override
					public void run()
					{
						double err = 0;
						while(true)
						{

							if(pause)
							{
								continue;
							}

							// Clearing the plot
							Graphics2D g2 = ((Graphics2D)graph.getGraphics());
							g2.setColor(bkg);
							g2.fillRect(0, 0, width, height);
							g2.setColor(plClr);

							long beforeTime = System.currentTimeMillis();

							// Update - plot loop. Iterates through each Drawable and calls its
							// update and plot functions.
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
							repaint();


							try
							{
								// Pausing the thread for the required time.
								// long sleepTime calculates the difference between the required
								// frame time and the elapsed time. The difference, if positive, becomes
								// the parameter for the Thread.sleep function.
								// If negative, the relative error is stored in err, and scales the update
								// expressions of the next iteration by err.
								long sleepTime = fTime - System.currentTimeMillis() + beforeTime;
								System.out.println(sleepTime);
								if(sleepTime >= 0)
									Thread.sleep(sleepTime);
								else
									err = -(double)sleepTime / fTime;

							}catch(InterruptedException ex)
							{
								ex.printStackTrace(System.err);
							}

						}

					}

			});
		//timer.start();
	}

	/**
	 * Returns a copy of all the {@code Drawable} in the form of a {@code Vector}.
	 * The order of {@code Drawables} in the new Vector reflects the order in which
	 * it is stored by {@code GraphFX}
	 */
	public Vector cloneInstance()
	{
		Vector<Drawable> cloneVec = new Vector<Drawable>();
		for(int i = 0; i < n; i++)
		{
			cloneVec.add((Drawable)aObjs.elementAt(i).clone());
		}

		return cloneVec;
	}

	/**
	 * Start the timer {@code Thread}
	 */
	public void start()
	{
		timer.start();
	}

	/**
	 * Adds a {@code Drawable} to the plot list.
	 *
	 * <p>
	 * The {@code Drawbale} is registered in a {@code Hashtable} and
	 * is stored with its {@code name} as key.
	 *
	 * @param drw the drawable to add to the plot list.
	 *
	 * @see hashName
	 * @see aObjs
	 */
	public void addDrawable(Drawable drw)
	{
		if(hashName.get(drw.toString()) == null)
		{
			drw.setBImage(graph);
			hashName.put(drw.toString(), drw);

			aObjs.add(drw);
			n++;
		}

		else
		{
			System.err.println("Cannot add Drawable as Drawable " + drw.toString() + " already exists.");
		}
	}

	/**
	 * Remove a {@code Drawable} with the given name from the plot
	 * list. It is also removed from the {@code Hashtable} hashName.
	 *
	 * @param name the name of the {@code Drawable} to remove.
	 *
	 * @see Drawable#toString
	 * @see hashName
	 * @see aObjs
	 */
	public void removeDrawable(String name)
	{
		if(hashName.get(name) != null)
		{
			aObjs.remove(n);
			hashName.remove(name);
		}
	}

	/**
	 * Removes all {@code Drawable} from the plot list.
	 */
	public void removeAll()
	{
		aObjs = new Vector< Drawable >();
		hashName = new Hashtable<String, Drawable>();
	}

	/**
	 * Sets the frame persistence time.
	 *
	 * @see fTime
	 */
	public void setfTime(int fTime)
	{
		this.fTime = fTime;
	}

	/**
	 * Replot the entrire animation, starting with the initial
	 * configuration as defined by the {@code Plot}.
	 *
	 * @see Plot
	 * @see #setPlot
	 */
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

	/**
	 * Sets the {@code Plot} of the {@code GraphFX}.
	 *
	 * @param plot the {@code Plot} to set.
	 *
	 * @see Plot
	 * @see #replot
	 */
	public void setPlot(Plot plot)
	{
		this.plot = plot;
		plot.plot(this);
	}

	/**
	 * Overrides the {@code mouseWheelMoved} function of the
	 * super class {@code Graph} to change the {@code fTime}
	 * with the mouse wheel.
	 */
	@Override
	public void mouseWheelMoved(MouseWheelEvent e)
	{
		fTime += 1 * e.getWheelRotation();
		if(fTime < 10)
			fTime = 10;
	}

	/**
	 * Overrides the {@code keyPressed} function of the super
	 * class {@code Graph} to implement a pause feature.
	 */
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
		paint(g);
	}

	/**
	 * Returns a {@code Drawable} with the specified name.
	 *
	 * @param name the {@code name} of the Drawable to retrieve.
	 *
	 * @see Drawable#toString
	 */
	public Drawable getDrawable(String name)
	{
		Drawable r = hashName.get(name);
		return r;
	}

}
