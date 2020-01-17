public class SPoint
{
	public int x, y; 
	public SPoint(int x, int y)
	{
		this.x = x;
		this.y = y;
	}

	@Override
	public int hashCode()
	{
		return y;
	}

	@Override
	public boolean equals(Object p)
	{
		if(p instanceof SPoint)
			return (((SPoint)p).x == x && ((SPoint)p).y == y);
		System.out.print("Hurrah!!!");
		return false;
	}
}
