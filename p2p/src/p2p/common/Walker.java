package p2p.common;

import java.util.Random;

import actionsim.Point;

public abstract class Walker {

	private static Random random = new Random();
	private static int[] speeds = new int[] { 1, 2, 4 };

	@SuppressWarnings("rawtypes")
	protected AbstractAgent agent;
	protected int worldWidth;
	protected int worldHeight;

	private Point lastMove;

	private float speed = 2; // speeds[random.nextInt(speeds.length)]; // normal
								// hız 2

	public Walker(AbstractAgent<?> agent, int worldWidth, int worldHeight) {

		this.agent = agent;
		this.worldWidth = worldWidth;
		this.worldHeight = worldHeight;
	}

	public abstract boolean updateDirection(Point move);

	public void walk(float time) {

		float t = time / 1000; // to seconds

		if (Math.abs(((int) agent.getX()) - agent.getX()) > 0.01
				|| Math.abs(((int) agent.getY()) - agent.getY()) > 0.01) {

			agent.setPosition(agent.getX() + lastMove.x * t * speed, agent.getY() + lastMove.y * t * speed);
			agent.onPositionChange();

		} else {

			Point move = new Point();

			if (updateDirection(move)) {

				float dX = move.x * t * speed;
				float dY = move.y * t * speed;

				while (isInBounds(agent.getX() + dX, 0) == false) {

					if (dX > 0.01) {

						dX -= 1;

					} else if (dX < -0.01) {

						dX += 1;
					}
				}

				while (isInBounds(0, agent.getY() + dY) == false) {

					if (dY > 0.01) {

						dY -= 1;

					} else if (dY < -0.01) {

						dY += 1;
					}
				}

				agent.setPosition(agent.getX() + dX, agent.getY() + dY);
				agent.onPositionChange();

				lastMove = move;

			} else {

				agent.onCacheMiss(agent.getX() + move.x * t, agent.getY() + move.y * t);
			}
		}
	}

	protected boolean isInBounds(float x, float y) {

		return x >= 0 && y >= 0 && x < worldWidth && y < worldHeight;
	}
}
