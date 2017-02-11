package p2p.common;

import java.util.Random;

public class RandomWalker extends Walker {

	private Random random = new Random();

	public RandomWalker(AbstractAgent<?> agent, int worldWidth, int worldHeight) {

		super(agent, worldWidth, worldHeight);
	}

	@Override
	public void updateDirection() {

		int x = agent.getX();
		int y = agent.getY();
		int nX;
		int nY;

		do {

			dX = random.nextInt(3) - 1;
			dY = random.nextInt(3) - 1;

			nX = x + dX;
			nY = y + dY;

		} while ((dX == 0 && dY == 0) || isInBounds(nX, nY) == false);
	}
}
