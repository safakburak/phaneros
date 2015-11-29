package p2p.visibility.nuv.horizon;

import java.util.LinkedList;

public class Horizon {

	private LinkedList<Sector> sectors = new LinkedList<Sector>();
	
	public Horizon() {
		
		sectors.add(new Sector(Angle.MIN, Angle.MAX, new Angle(0)));
	}
	
	public void update(Sector sector) {

		split(sector.getStart());
		split(sector.getEnd());
		
		updateElevs(sector.getStart(), sector.getEnd(), sector.getElev());
		
		merge();
	}
	
	private void split(Angle angle) {
		
		for (int i = 0; i < sectors.size(); i++) {
			
			Sector sector = sectors.get(i);
			
			if(sector.contains(angle, false)) {

				Sector newSector = new Sector(sector.getStart(), angle, sector.getElev());
				sector.setStart(angle);
				sectors.add(i, newSector);
				break;
			}
		}		
	}
	
	private void updateElevs(Angle start, Angle end, Angle elev) {
		
		for(Sector sector : sectors) {
			
			if(sector.getElev().lt(elev) && sector.getStart().gte(start) && sector.getEnd().lte(end)) {
				
				sector.setElev(elev);
			}
		} 
	}
	
	private void merge() {
		
		Sector prev = sectors.get(0); 
		
		for(int i = 1; i < sectors.size(); i++) {
			
			Sector sector = sectors.get(i);
			
			if(prev.getElev().equals(sector.getElev())) {
				
				prev.setEnd(sector.getEnd());
				sectors.remove(i);
				i--;
				
			} else {
				
				prev = sector;
			}
		}
	}
	
	@Override
	public String toString() {
		
		String result = "";
		
		for(Sector sector : sectors) {
			
			result += sector.toString() + "\n";
		}
		
		return result;
	}
}
