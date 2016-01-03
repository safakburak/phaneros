package p2p.common;

import java.util.ArrayList;
import java.util.Collection;

import p2p.map.Map;
import p2p.map.Region;

public class FullCache implements Cache {

	private ArrayList<Map> patchList = new ArrayList<Map>();
	private Map[][] patches;
	private int cellSize;

	public FullCache(Map map, int cellSize) {

		this.cellSize = cellSize;

		int colNum = map.getWidth() / cellSize;
		int rowNum = map.getHeight() / cellSize;

		patches = new Map[colNum][rowNum];

		for (int col = 0; col < colNum; col++) {
			for (int row = 0; row < rowNum; row++) {

				Map patch = map.getMapPart(col * cellSize, row * cellSize, cellSize, cellSize);

				patches[col][row] = patch;

				patchList.add(patch);
			}
		}
	}

	public Map getPatch(Region cell) {

		return patches[cell.getX() / cellSize][cell.getY() / cellSize];
	}

	public Collection<Map> getPatches() {

		return patchList;
	}

	public Map getPatch(int x, int y) {

		return patches[x / cellSize][y / cellSize];
	}

	public void addPatch(Map patch) {

	}
}
