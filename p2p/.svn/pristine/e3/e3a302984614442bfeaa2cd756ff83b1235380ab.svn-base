package p2p.data;

import java.io.Serializable;

@SuppressWarnings("serial")
public class IntPair implements Serializable
{
	private int x;
	private int y;
	
	public IntPair(int x, int y) 
	{
		this.x = x;
		this.y = y;
	}

	public int getX() 
	{
		return x;
	}

	public int getY() 
	{
		return y;
	}
	
	@Override
	public boolean equals(Object obj) 
	{
		if(obj instanceof IntPair)
		{
			IntPair pos = (IntPair) obj;
			
			return pos == this || (x == pos.x && y == pos.y);
		}
		else
		{
			return false;
		}
	}
	
	@Override
	public int hashCode() 
	{
		return (x + "," + y).hashCode();
	}
}
