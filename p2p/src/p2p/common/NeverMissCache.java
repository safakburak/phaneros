package p2p.common;

import p2p.map.Atlas;
import p2p.map.Region;
import p2p.map.Tile;

public class NeverMissCache extends LimitedCache {

	private static Tile[][] patches;

	public NeverMissCache(Atlas atlas, int capacity, int cellSize) {

		super(capacity, cellSize);

		if (patches == null) {

			int colNum = atlas.getWidth() / cellSize;
			int rowNum = atlas.getHeight() / cellSize;

			patches = new Tile[colNum][rowNum];

			for (int col = 0; col < colNum; col++) {
				for (int row = 0; row < rowNum; row++) {

					patches[col][row] = atlas.getTile(col * cellSize, row * cellSize, cellSize);
				}
			}
		}
	}

	public Tile getTile(Region cell) {

		Tile patch = super.getTile(cell);

		if (patch == null) {

			patch = patches[cell.getX() / getCellSize()][cell.getY() / getCellSize()];

			addTile(patch);
		}

		return patch;
	}
}
