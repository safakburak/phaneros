package p2p.common;

import java.util.Collection;

import p2p.map.Tile;
import p2p.map.Region;

public interface Cache {
	
	public Tile getTile(Region cell);

	public Collection<Tile> getTiles();

	public Tile getTile(int x, int y);
		
	public void addTile(Tile patch);

}
