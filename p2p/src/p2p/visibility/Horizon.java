package p2p.visibility;

import java.util.ArrayList;

public class Horizon 
{
	ArrayList<Section> mSections = new ArrayList<Section>();
	
	public Horizon() 
	{
		mSections.add(new Section(0, 360, 0));
	}
	
	public boolean update(double start, double end, double elev)
	{
		if(start > end)
		{
			boolean result1;
			boolean result2;
			
			result1 = update(start, 360, elev);
			result2 = update(0, end, elev);
			
			return result1 || result2;
		}
		else
		{
			ArrayList<Section> newSections = new ArrayList<Section>();

			boolean result = false;
			
			for (Section section : mSections) 
			{
				if(section.test(elev))
				{
					result = true;
					
					Section[] subsections = section.update(start, end, elev);
					
					for (Section subsection : subsections) 
					{
						newSections.add(subsection);
					}
				}
				else
				{
					newSections.add(section);
				}
			}
			
			if(newSections.size() > 1)
			{
				for(int sectionIndex = 0; sectionIndex < (newSections.size() - 1); sectionIndex++)
				{
					Section section1 = newSections.get(sectionIndex); 
					Section section2 = newSections.get(sectionIndex + 1); 
					
					if(section1.getElev().equals(section2.getElev()))
					{
						Section mergedSection = new Section(section1.getStart().getValue(), 
								section2.getEnd().getValue(), 
								section1.getElev().getValue());
						
						newSections.set(sectionIndex, mergedSection);
						newSections.remove(sectionIndex + 1);
						sectionIndex--;
					}
				}
			}
			
			mSections = newSections;
			
			return result;
		}
	}
	
	public boolean isClosed()
	{
		return mSections.size() == 1 && mSections.get(0).getElev().getValue() > 0;
	}
	
	public static void main(String[] args) 
	{
		boolean isVisible;
		
		Horizon horizon = new Horizon();
		
		isVisible = horizon.update(100, 200, 1);
		isVisible = horizon.update(310, 50, 1);
		
		System.out.println(horizon);
	}
}
