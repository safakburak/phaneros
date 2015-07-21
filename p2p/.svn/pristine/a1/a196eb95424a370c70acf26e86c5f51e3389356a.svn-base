package p2p.visibility;


public class Section 
{
	private MyDouble mStart = new MyDouble();
	private MyDouble mEnd = new MyDouble();
	private MyDouble mElev = new MyDouble();

	public Section(double start, double end, double elev) 
	{
		mStart = new MyDouble(start);
		mEnd = new MyDouble(end);
		mElev = new MyDouble(elev);
	}
	
	private boolean contains(MyDouble value)
	{
		return value.gte(mStart) && value.lt(mEnd);
	}
	
	public Section[] update(double start, double end, double elev)
	{
		Section[] result;

		MyDouble newStart = new MyDouble(start);
		MyDouble newEnd = new MyDouble(end);
		MyDouble newElev = new MyDouble(elev);
		
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
		MyDouble myElev = new MyDouble(elev);
		
		return myElev.gt(mElev);
	}
	
	public MyDouble getStart()
	{
		return mStart;
	}
	
	public MyDouble getEnd()
	{
		return mEnd;
	}
	
	public MyDouble getElev()
	{
		return mElev;
	}
}
