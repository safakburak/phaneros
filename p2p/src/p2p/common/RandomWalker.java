package p2p.common;

import java.util.Random;

import p2p.map.Map;

public class RandomWalker {

	private AbstractAgent agent; 
	private int worldWidth;
	private int worldHeight;
	private Random random;
	
	private int dX = 0;
	private int dY = 0;
	
	public RandomWalker(AbstractAgent agent, int worldWidth, int worldHeight) {
		
		this.agent = agent;
		this.random = new Random();
		this.worldWidth = worldWidth;
		this.worldHeight = worldHeight;
	}
	
	public void walk() {
		
		int x = agent.getX();
		int y = agent.getY();
		int nX = x + dX;
		int nY = y + dY;
		
		Map map = agent.getCache().getPatch(nX, nY);
		
		if(isValid(nX, nY) && map == null) {
			
			agent.onCacheMissAt(nX, nY);
			
		} else {
			
			if((dX != 0 || dY != 0) && isValid(nX, nY) && map.getHeightAtAbs(nX, nY) == 0) {
				
				agent.setPosition(nX, nY);
				agent.onPositionChange();
				
			} else {
				
				do {
					
					dX = random.nextInt(3) - 1;
					dY = random.nextInt(3) - 1;
					
					nX = x + dX;
					nY = y + dY;
					
				} while((dX == 0 && dY == 0) || isValid(nX, nY) == false);
			}
		}
	}
	
	private boolean isValid(int x, int y) {
		
		return x > 0 && y > 0 && x < worldWidth && y < worldHeight;
	}
}
