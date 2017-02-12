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

	public void walk() {

		if (updateDirection()) {

			agent.setPosition(agent.getX() + dX, agent.getY() + dY);
			agent.onPositionChange();

		} else {

			agent.onCacheMiss(agent.getX() + dX, agent.getY() + dY);
		}
	}

	protected boolean isInBounds(int x, int y) {

		return x >= 0 && y >= 0 && x < worldWidth && y < worldHeight;
	}
}
