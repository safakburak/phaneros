package p2p.data;

public class Region 
{
	private int mX;
	private int mY;
	private int mW;
	private int mH;
	
	public Region(int x, int y, int w, int h) 
	{
		mX = x;
		mY = y;
		mW = w;
		mH = h;
	}
	
	public boolean contains(int aX, int aY)
	{
		return aX >= mX && aX < (mX + mW) && aY >= mY && aY < (mY + mH);
	}
}
