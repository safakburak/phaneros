package p2p.common;

import p2p.map.Tile;

public abstract class Walker {

	protected static class Point {

		Point(double x, double y) {

			this.x = x;
			this.y = y;
		}

		double x;
		double y;
	}

	@SuppressWarnings("rawtypes")
	protected AbstractAgent agent;
	private int worldWidth;
	private int worldHeight;

	protected int dX = 0;
	protected int dY = 0;

	public Walker(AbstractAgent<?> agent, int worldWidth, int worldHeight) {

		this.agent = agent;
		this.worldWidth = worldWidth;
		this.worldHeight = worldHeight;
	}

	public abstract void updateDirection();

	public void walk() {

		int nX = agent.getX() + dX;
		int nY = agent.getY() + dY;

		if ((dX != 0 || dY != 0) && isInBounds(nX, nY)) {

			Tile map = agent.getCache().getTile(nX, nY);

			if (map == null) {

				agent.onCacheMiss(nX, nY);

			} else if (map.getAbsolute(nX, nY) == 0) {

				agent.setPosition(nX, nY);
				agent.onPositionChange();

			} else {

				updateDirection();
			}

		} else {

			updateDirection();
		}
	}

	protected boolean isInBounds(int x, int y) {

		return x > 0 && y > 0 && x < worldWidth && y < worldHeight;
	}
}
