package p2p.renderer;

import java.awt.Point;
import java.util.Collection;

import p2p.map.Map;
import p2p.visibility.VisibilityCell;

public interface IRenderable {

	public Collection<Map> getPatches();
	
	public Collection<VisibilityCell> getPvs();
	
	public Collection<Point> getAgents();
	
	public String getId();
	
	public int getX();
	
	public int getY();
}
