public class RPoint
{
	public double x, y;
	public RPoint(double x, double y)
	{
		this.x = x;
		this.y = y;
	}

	@Override
	public int hashCode()
	{
		return (int)y;
	}

	@Override
	public boolean equals(Object p)
	{
		if(p instanceof SPoint)
			return (((RPoint)p).x == x && ((RPoint)p).y == y);
		return false;
	}
}