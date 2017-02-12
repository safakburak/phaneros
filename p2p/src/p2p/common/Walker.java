package p2p.common;

public abstract class Walker {

	@SuppressWarnings("rawtypes")
	protected AbstractAgent agent;
	protected int worldWidth;
	protected int worldHeight;

	protected int dX = 0;
	protected int dY = 0;

	public Walker(AbstractAgent<?> agent, int worldWidth, int worldHeight) {

		this.agent = agent;
		this.worldWidth = worldWidth;
		this.worldHeight = worldHeight;
	}

	public abstract boolean updateDirection();

	public void walk(float time) {

		float t = time / 1000; // to seconds

		if (updateDirection()) {

			agent.setPosition(agent.getX() + dX * t, agent.getY() + dY * t);
			agent.onPositionChange();

		} else {

			agent.onCacheMiss(agent.getX() + dX * t, agent.getY() + dY * t);
		}
	}

	protected boolean isInBounds(float x, float y) {

		return x >= 0 && y >= 0 && x < worldWidth && y < worldHeight;
	}
}
