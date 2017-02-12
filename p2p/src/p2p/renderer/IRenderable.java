package p2p.renderer;

import java.util.Collection;

import actionsim.Point;
import p2p.common.AbstractAgent;
import p2p.map.Tile;
import p2p.visibility.VisibilityCell;

public interface IRenderable {

	public Collection<Tile> getAvailableTiles();

	public Collection<VisibilityCell> getPvs();

	public Collection<Point> getAgents();

	@SuppressWarnings("rawtypes")
	public boolean isKnown(AbstractAgent agent);

	public String getId();

	public int getX();

	public int getY();
}
