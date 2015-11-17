package p2p.visibility;

public class FpDouble 
{
	private int mValue;
	
	public FpDouble(double value) 
	{
		mValue = (int) (value * 1000);
	}
	
	public boolean eq(FpDouble value)
	{
		return value.mValue == mValue;
	}
	
	public boolean lt(FpDouble value)
	{
		return mValue < value.mValue;
	}
	
	public boolean gt(FpDouble value)
	{
		return mValue > value.mValue;
	}
	
	public boolean lte(FpDouble value)
	{
		return mValue <= value.mValue;
	}
	
	public boolean gte(FpDouble value)
	{
		return mValue >= value.mValue;
	}
	
	public double getValue()
	{
		return mValue / 1000.0;
	}
	
	public FpDouble add(FpDouble value)
	{
		return new FpDouble(mValue + value.mValue);
	}
	
	public FpDouble sub(FpDouble value)
	{
		return new FpDouble(mValue - value.mValue);
	}
}
