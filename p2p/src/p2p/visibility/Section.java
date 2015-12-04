package p2p.visibility;

import p2p.visibility.nuv.visibility.horizon.Angle;

public class Section 
{
	private Angle mStart = new Angle(0);
	private Angle mEnd = new Angle(0);
	private Angle mElev = new Angle(0);

	public Section(double start, double end, double elev) 
	{
		mStart = new Angle(start);
		mEnd = new Angle(end);
		mElev = new Angle(elev);
	}
	
	private boolean contains(Angle value)
	{
		return value.gte(mStart) && value.lt(mEnd);
	}
	
	public Section[] update(double start, double end, double elev)
	{
		Section[] result;

		Angle newStart = new Angle(start);
		Angle newEnd = new Angle(end);
		Angle newElev = new Angle(elev);
		
		if(newElev.gt(mElev))
		{
			if(mStart.equals(newStart) && mEnd.equals(newEnd))
			{
				mElev = newElev;
				result = new Section[1];
				result[0] = this;
			}
			else if(mStart.equals(newStart))
			{
				if(newEnd.gt(mEnd))
				{
					mElev = newElev;
					result = new Section[1];
					result[0] = this;
				}
				else
				{
					result = new Section[2];
					result[0] = new Section(mStart.getValue(), newEnd.getValue(), newElev.getValue());
					result[1] = new Section(newEnd.getValue(), mEnd.getValue(), mElev.getValue());
				}
			}
			else if(mEnd.equals(newEnd))
			{
				if(newStart.lt(mStart))
				{
					mElev = newElev;
					result = new Section[1];
					result[0] = this;
				}
				else
				{
					result = new Section[2];
					result[0] = new Section(mStart.getValue(), newStart.getValue(), mElev.getValue());
					result[1] = new Section(newStart.getValue(), mEnd.getValue(), newElev.getValue());
				}
			}
			else if(contains(newStart) && contains(newEnd))
			{
				result = new Section[3];
				result[0] = new Section(mStart.getValue(), newStart.getValue(), mElev.getValue());
				result[1] = new Section(newStart.getValue(), newEnd.getValue(), newElev.getValue());
				result[2] = new Section(newEnd.getValue(), mEnd.getValue(), mElev.getValue());
			}
			else if(contains(newStart))
			{
				result = new Section[2];
				result[0] = new Section(mStart.getValue(), newStart.getValue(), mElev.getValue());
				result[1] = new Section(newStart.getValue(), mEnd.getValue(), newElev.getValue());
			}
			else if(contains(newEnd))
			{
				result = new Section[2];
				result[0] = new Section(mStart.getValue(), newEnd.getValue(), newElev.getValue());
				result[1] = new Section(newEnd.getValue(), mEnd.getValue(), mElev.getValue());
			}
			else if(newStart.lt(mStart) && newEnd.gt(newEnd))
			{
				mElev = newElev;
				result = new Section[1];
				result[0] = this;
			}
			else
			{
				result = new Section[1];
				result[0] = this;
			}
		}
		else
		{
			result = new Section[1];
			result[0] = this;
		}
		
		return result;
	}
	
	public boolean test(double elev)
	{
		Angle myElev = new Angle(elev);
		
		return myElev.gt(mElev);
	}
	
	public Angle getStart()
	{
		return mStart;
	}
	
	public Angle getEnd()
	{
		return mEnd;
	}
	
	public Angle getElev()
	{
		return mElev;
	}
}
