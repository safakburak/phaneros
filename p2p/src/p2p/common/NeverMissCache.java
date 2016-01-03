package p2p.common;

import p2p.map.Map;
import p2p.map.Region;

public class NeverMissCache extends LimitedCache {

	private static Map[][] patches;
	
	public NeverMissCache(Map world, int capacity, int cellSize) {

		super(capacity, cellSize);
		
		if(patches == null) {
			
			int colNum = world.getWidth() / cellSize;
			int rowNum = world.getHeight() / cellSize;
			
			patches = new Map[colNum][rowNum];
			
			for(int col = 0; col < colNum; col++) {
				for(int row = 0; row < rowNum; row++) {
					
					patches[col][row] = world.getMapPart(col * cellSize, row * cellSize, cellSize, cellSize);
				}	
			}
		}
	}

	public Map getPatch(Region cell) {

		Map patch = super.getPatch(cell);
		
		if (patch == null) {

			patch = patches[cell.getX() / getCellSize()][cell.getY() / getCellSize()];
			
			addPatch(patch);
		}
		
		return patch;
	}
}
