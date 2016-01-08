package p2p.common;

import java.util.ArrayDeque;
import java.util.Collection;
import java.util.HashMap;

import p2p.map.Tile;
import p2p.map.Region;

public class LimitedCache implements Cache {

	private ArrayDeque<Tile> disposeQueue;
	private HashMap<Region, Tile> tileMap = new HashMap<Region, Tile>();
	private int cellSize;
	private int capacity;

	public LimitedCache(int capacity, int cellSize) {

		this.capacity = capacity;
		this.cellSize = cellSize;

		disposeQueue = new ArrayDeque<Tile>(cellSize);
	}

	public Tile getTile(Region region) {

		Tile tile = tileMap.get(region);
		
		if (tile != null) {

			disposeQueue.remove(tile);
			disposeQueue.add(tile);
		}
		
		return tile;
	}

	public Collection<Tile> getTiles() {

		return disposeQueue;
	}

	public Tile getTile(int x, int y) {

		return getTile(new Region(x / cellSize * cellSize, y / cellSize * cellSize, cellSize));
	}

	public void addTile(Tile tile) {

		Region key = new Region((tile.getX() / cellSize) * cellSize, (tile.getY() / cellSize) * cellSize, cellSize);

		tileMap.putIfAbsent(key, tile);

		if (disposeQueue.contains(tile)) {

			disposeQueue.remove(tile);
			disposeQueue.add(tile);

		} else {

			disposeQueue.add(tile);

			if (disposeQueue.size() == capacity) {

				Tile removeTile = disposeQueue.poll();
				Region removeKey = new Region((removeTile.getX() / cellSize) * cellSize,
						(removeTile.getY() / cellSize) * cellSize, cellSize);

				tileMap.remove(removeKey);
			}
		}
	}
	
	public int getCellSize() {
		
		return cellSize;
	}
}
