package p2p.common;

import java.util.ArrayDeque;
import java.util.Collection;
import java.util.HashMap;

import p2p.map.Region;
import p2p.map.Tile;

public class LruCache implements Cache {

	private ArrayDeque<Tile> disposeQueue;
	private HashMap<Region, Tile> tileMap = new HashMap<Region, Tile>();

	private int cellSize;
	private int capacity;

	public LruCache(int capacity, int cellSize) {

		this.capacity = capacity;
		this.cellSize = cellSize;

		disposeQueue = new ArrayDeque<Tile>();
	}

	@Override
	public boolean contains(Region region) {

		return tileMap.containsKey(region);
	}

	public Tile getTile(Region region) {

		Tile tile = tileMap.get(region);

		if (tile != null) {

			disposeQueue.remove(tile);
			disposeQueue.addLast(tile);
		}

		return tile;
	}

	public Collection<Tile> getTiles() {

		return tileMap.values();
	}

	public Tile getTile(float x, float y) {

		return getTile(new Region(((int) (x / cellSize)) * cellSize, ((int) (y / cellSize)) * cellSize, cellSize));
	}

	public void addTile(Tile tile) {

		if (disposeQueue.contains(tile)) {

			disposeQueue.remove(tile);
			disposeQueue.addLast(tile);

		} else {

			disposeQueue.addLast(tile);
			tileMap.put(tile.getRegion(), tile);

			while (tileMap.size() > capacity) {

				tileMap.remove(disposeQueue.poll().getRegion());
			}
		}
	}

	public int getCellSize() {

		return cellSize;
	}
}
