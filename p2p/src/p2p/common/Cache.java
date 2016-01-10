package p2p.common;

import java.util.Collection;

import p2p.map.Region;
import p2p.map.Tile;

public interface Cache {

	public boolean contains(Region region);

	public Tile getTile(Region region);

	public Collection<Tile> getTiles();

	public Tile getTile(int x, int y);

	public void addTile(Tile tile);
}
