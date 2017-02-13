package p2p.common;

import java.util.Random;

import actionsim.Point;
import p2p.map.Tile;

public class RandomWalker extends Walker {

	private Random random = new Random();

	private float dX;
	private float dY;

	public RandomWalker(AbstractAgent<?> agent, int worldWidth, int worldHeight) {

		super(agent, worldWidth, worldHeight);
	}

	@Override
	public boolean updateDirection(Point move) {

		float x = agent.getX();
		float y = agent.getY();
		float nX;
		float nY;

		while (true) {

			nX = x + dX;
			nY = y + dY;

			if ((dX != 0 || dY != 0) && isInBounds(nX, nY)) {

				Tile tile = agent.cache.getTile(nX, nY);

				move.x = dX;
				move.y = dY;

				if (tile == null) {

					return false;

				} else if (tile.getAbsolute(nX, nY) == 0) {

					return true;
				}
			}

			dX = random.nextInt(3) - 1;
			dY = random.nextInt(3) - 1;
		}
	}
}
