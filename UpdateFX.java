import java.util.*;
import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
import java.awt.image.*;


public class UpdateFX
{
	public static final UpdateEvent ReverseLVelocityOnCollision = new UpdateEvent()
	{
		public void update(Drawable d)
		{
			if(d == null)
			{
				return;
			}

			Rectangle r = d.getBounds();

			if(r == null)
			{
				return;
			}

			if(r.getX() < 0)
			{
				d.xShift(2 * -r.getX());
				d.setLinearXVel(Math.abs(d.getLinearXVel()));
			}
			if(r.getX() + r.getWidth() > d.getParent().getViewWidth())
			{
				d.xShift(- 2 * (r.getX() + r.getWidth() - d.getParent().getViewWidth()));
				d.setLinearXVel(-Math.abs(d.getLinearXVel()));
			}
			if(r.getY() < 0) 
			{
				d.yShift(2 * -r.getY());
				d.setLinearYVel(Math.abs(d.getLinearYVel()));
			}
			if(r.getY() + r.getHeight() > d.getParent().getViewHeight())
			{
				d.yShift(- 2 * (r.getY() + r.getHeight() - d.getParent().getViewHeight()));
				d.setLinearYVel(-Math.abs(d.getLinearYVel()));
			}
		}
	};

	public static final UpdateEvent BasicCollision = new UpdateEvent()
	{
		public void update(Drawable d)
		{
			if(d == null)
			{
				return;
			}

			Rectangle r = d.getBounds();

			if(r == null)
			{
				return;
			}

			if(r.getX() < 0)
			{
				leftCollision(d, 2 * -r.getX());
			}
			if(r.getX() + r.getWidth() > d.getParent().getViewWidth())
			{
				rightCollision(d, - 2 * (r.getX() + r.getWidth() - d.getParent().getViewWidth()));
			}
			if(r.getY() < 0) 
			{
				upperCollision(d, 2 * -r.getY());
			}
			if(r.getY() + r.getHeight() > d.getParent().getViewHeight())
			{
				lowerCollision(d, - 2 * (r.getY() + r.getHeight() - d.getParent().getViewHeight()));
			}
		}

		public void upperCollision(Drawable d, double shiftParam)
		{

		}

		public void lowerCollision(Drawable d, double shiftParam)
		{
			
		}

		public void leftCollision(Drawable d, double shiftParam)
		{
			
		}

		public void rightCollision(Drawable d, double shiftParam)
		{
			
		}
	};
}