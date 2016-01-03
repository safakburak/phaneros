package p2p.common;

import java.util.Collection;

import p2p.map.Map;
import p2p.map.Region;

public interface Cache {
	
	public Map getPatch(Region cell);

	public Collection<Map> getPatches();

	public Map getPatch(int x, int y);
		
	public void addPatch(Map patch);

}
