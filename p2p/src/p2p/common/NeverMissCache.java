package p2p.common;

import p2p.map.Region;
import p2p.map.Tile;

public class NeverMissCache extends LruCache {

	private MapServer server;

	public NeverMissCache(MapServer server, int capacity, int cellSize) {

		super(capacity, cellSize);

		this.server = server;
	}

	public Tile getTile(Region region) {

		Tile tile = super.getTile(region);

		if (tile == null) {

			tile = server.getTile(region.getX(), region.getY());

			addTile(tile);
		}

		return tile;
	}
}
