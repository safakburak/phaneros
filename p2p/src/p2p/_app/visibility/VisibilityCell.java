package p2p._app.visibility;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import p2p._app.map.Region;

@SuppressWarnings("serial")
public class VisibilityCell implements Serializable {

	private Region region;
	
	private HashSet<VisibilityCell> pvs = new HashSet<VisibilityCell>();
	

	public VisibilityCell(int x, int y, int cellSize) {
		
		this.region = new Region(x, y, cellSize);
	}

	public Region getRegion() {
		
		return region;
	}
	
	public Set<VisibilityCell> getPvs() {
		
		return pvs;
	}
	
	public void addToPvs(VisibilityCell cell) {
		
		pvs.add(cell);
	}
}
