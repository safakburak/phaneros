package p2p._app.agent;

import java.util.ArrayDeque;
import java.util.Collection;
import java.util.HashMap;

import p2p._app.map.Map;
import p2p._app.map.Region;

public class Cache {

	private ArrayDeque<Map> disposeQueue;
	private HashMap<Region, Map> patchMap = new HashMap<Region, Map>();
	private int cellSize;
	private int capacity;

	public Cache(int capacity, int cellSize) {

		this.capacity = capacity;
		this.cellSize = cellSize;
		
		disposeQueue = new ArrayDeque<Map>(cellSize);
	}

	public Map getPatch(Region cell) {

		return patchMap.get(cell);
	}

	public Collection<Map> getPatches() {

		return disposeQueue;
	}
	
	public Map getPatch(int x, int y) {
		
		Map patch = patchMap.get(new Region(x / cellSize * cellSize, y / cellSize * cellSize, cellSize)); 
		
		if(patch != null) {
			
			disposeQueue.remove(patch);
			disposeQueue.add(patch);
		}
		
		return patch;
	}
	
	public void addPatch(Map patch) {

		Region key = new Region((patch.getX() / cellSize) * cellSize, (patch.getY() / cellSize) * cellSize, cellSize);
		
		patchMap.putIfAbsent(key, patch);
		
		if(disposeQueue.contains(patch)) {
			
			disposeQueue.remove(patch);
			disposeQueue.add(patch);
			
		} else {

			disposeQueue.add(patch);

			if(disposeQueue.size() == capacity) {
				
				Map removePatch = disposeQueue.poll();
				Region removeKey = new Region((removePatch.getX() / cellSize) * cellSize, (removePatch.getY() / cellSize) * cellSize, cellSize);
				
				patchMap.remove(removeKey);
			}
		}
	}
}
