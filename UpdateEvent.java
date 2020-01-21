import java.util.*;
import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
import java.awt.image.*;


/**
 * Definition for an update event.
 *
 * <p>
 * An update event performs a set of parameter updates on the given
 * {@code Drawable}
 */
public abstract class UpdateEvent
{
	/**
	 * The method which is called by the update method of the Drawable
	 * class.
	 *
	 * @param d the {@code Drawable} which is calling the update function.
	 * @param err the error parameter with which the update method of
	          the {@code Drawable} had been called.
	 */
	public abstract void update(Drawable d, double err);
}
