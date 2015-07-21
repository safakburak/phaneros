package p2p.visibility;

public class MyDouble 
{
	private int mValue;
	
	public MyDouble()
	{
		
	}
	
	public MyDouble(double value) 
	{
		mValue = (int) (value * 1000);
	}
	
	public boolean eq(MyDouble value)
	{
		return value.mValue == mValue;
	}
	
	public boolean lt(MyDouble value)
	{
		return mValue < value.mValue;
	}
	
	public boolean gt(MyDouble value)
	{
		return mValue > value.mValue;
	}
	
	public boolean lte(MyDouble value)
	{
		return mValue <= value.mValue;
	}
	
	public boolean gte(MyDouble value)
	{
		return mValue >= value.mValue;
	}
	
	public double getValue()
	{
		return mValue / 1000.0;
	}
	
	public MyDouble add(MyDouble value)
	{
		MyDouble result = new MyDouble();
		
		result.mValue = mValue + value.mValue;
		
		return result;
	}
	
	public MyDouble sub(MyDouble value)
	{
		MyDouble result = new MyDouble();
		
		result.mValue = mValue - value.mValue;
		
		return result;
	}
}
