package p2p.visibility.nuv.horizon;

public class Angle 
{
	public static Angle MIN = new Angle(0);
	public static Angle MAX = new Angle(360);
	
	private int value;
	
	public Angle(double value) 
	{
		this.value = (int) (value * 100);
	}
	
	@Override
	public boolean equals(Object obj) {
		
		return obj instanceof Angle && this.value == ((Angle)obj).value;
	}
	
	public boolean lt(Angle angle)
	{
		return value < angle.value;
	}
	
	public boolean gt(Angle angle)
	{
		return value > angle.value;
	}
	
	public boolean lte(Angle angle)
	{
		return value <= angle.value;
	}
	
	public boolean gte(Angle angle)
	{
		return value >= angle.value;
	}
	
	public double getValue()
	{
		return value / 100.0;
	}
	
	public Angle add(Angle angle)
	{
		return new Angle(this.value + angle.value);
	}
	
	public Angle sub(Angle angle)
	{
		return new Angle(this.value - angle.value);
	}
}
