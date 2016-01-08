package p2p.renderer;

import java.awt.Point;
import java.util.Collection;

import p2p.map.Tile;
import p2p.visibility.VisibilityCell;

public interface IRenderable {

	public Collection<Tile> getAvailableTiles();

	public Collection<VisibilityCell> getPvs();

	public Collection<Point> getAgents();

	public String getId();

	public int getX();

	public int getY();
}
