package p2p.visibility;


public class Section 
{
	private FpDouble mStart = new FpDouble(0);
	private FpDouble mEnd = new FpDouble(0);
	private FpDouble mElev = new FpDouble(0);

	public Section(double start, double end, double elev) 
	{
		mStart = new FpDouble(start);
		mEnd = new FpDouble(end);
		mElev = new FpDouble(elev);
	}
	
	private boolean contains(FpDouble value)
	{
		return value.gte(mStart) && value.lt(mEnd);
	}
	
	public Section[] update(double start, double end, double elev)
	{
		Section[] result;

		FpDouble newStart = new FpDouble(start);
		FpDouble newEnd = new FpDouble(end);
		FpDouble newElev = new FpDouble(elev);
		
		if(newElev.gt(mElev))
		{
			if(mStart.eq(newStart) && mEnd.eq(newEnd))
			{
				mElev = newElev;
				result = new Section[1];
				result[0] = this;
			}
			else if(mStart.eq(newStart))
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
			else if(mEnd.eq(newEnd))
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
		FpDouble myElev = new FpDouble(elev);
		
		return myElev.gt(mElev);
	}
	
	public FpDouble getStart()
	{
		return mStart;
	}
	
	public FpDouble getEnd()
	{
		return mEnd;
	}
	
	public FpDouble getElev()
	{
		return mElev;
	}
}
