package p2p.common;

import actionsim.Point;

public abstract class Walker {

	@SuppressWarnings("rawtypes")
	protected AbstractAgent agent;
	protected int worldWidth;
	protected int worldHeight;

	public Walker(AbstractAgent<?> agent, int worldWidth, int worldHeight) {

		this.agent = agent;
		this.worldWidth = worldWidth;
		this.worldHeight = worldHeight;
	}

	public abstract boolean updateDirection(Point move);

	public void walk(float time) {

		float t = time / 1000; // to seconds

		t = 1;

		Point move = new Point();

		if (updateDirection(move)) {

			agent.setPosition(agent.getX() + move.x * t, agent.getY() + move.y * t);
			agent.onPositionChange();

		} else {

			agent.onCacheMiss(agent.getX() + move.x * t, agent.getY() + move.y * t);
		}
	}

	protected boolean isInBounds(float x, float y) {

		return x >= 0 && y >= 0 && x < worldWidth && y < worldHeight;
	}
}
