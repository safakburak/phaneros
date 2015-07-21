package p2p.visibility;

public class Span 
{
	private static int MIN = 0;
	private static int MAX = 360000;
	
	private int mStart;
	private int mEnd;
	
	public void Span(int start, int end)
	{
		mStart = start;
		mEnd = end;
	}
	
	public void setStart(int start)
	{
		mStart = start;
	}
	
	public void setEnd(int end)
	{
		mEnd = end;
	}
	
	public int getStart()
	{
		return mStart;
	}
	
	public int getEnd()
	{
		return mStart;
	}
	
	public boolean test(int value)
	{
		if(mEnd > mStart)
		{
			return value < mEnd && value >= mStart;
		}
		else
		{
			return (value >= 0 && value < mEnd) || (value >= mStart && value < MAX);
		}
	}
}
