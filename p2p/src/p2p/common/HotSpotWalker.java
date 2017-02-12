package p2p.common;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import actionsim.Point;
import actionsim.core.Simulation;
import p2p.map.Tile;
import p2p.timer.TimedAction;
import p2p.timer.Timer;

public class HotSpotWalker extends Walker {

	private static Point[] dirCW = new Point[] { new Point(0, -1), new Point(1, -1), new Point(1, 0), new Point(1, 1),
			new Point(0, 1), new Point(-1, 1), new Point(-1, 0), new Point(-1, -1) };

	private static int validHotSpotVersion = 0;

	private static List<Point> hotSpots = new ArrayList<Point>();

	private static Timer timer = new Timer();

	static {

		timer.repeat(new TimedAction() {
			@Override
			public void act(float time) {

				Random random = new Random();

				hotSpots.clear();

				List<Point> spots = new ArrayList<Point>();

				for (int i = 0; i < 4; i++) {

					Point p = new Point(random.nextInt(1024), random.nextInt(1024));

					hotSpots.add(p);
					spots.add(p);
				}

				validHotSpotVersion++;

				Simulation.instance.setHotSpots(hotSpots);
			}
		}, 300000, 0);
	}

	private int activeHotSpotVersion = 0;

	private Point target;

	private Random random = new Random();

	private int searchDir = 1;

	private float prevX;

	private float prevY;

	public HotSpotWalker(AbstractAgent<?> agent, int worldWidth, int worldHeight) {

		super(agent, worldWidth, worldHeight);

		timer.repeat(new TimedAction() {
			@Override
			public void act(float time) {

				searchDir *= -1;
			}
		}, 2000 * (random.nextInt(8) + 5));
	}

	private int getNextDirection(int x, int y, int start) {

		for (int i = 0; i < dirCW.length; i++) {

			int index = (start + (i * searchDir)) % dirCW.length;

			while (index < 0) {

				index += dirCW.length;
			}

			Point ref = dirCW[index];

			if (ref.x == x && ref.y == y) {

				index = (index + searchDir) % dirCW.length;

				while (index < 0) {

					index += dirCW.length;
				}

				return index;
			}
		}

		return -1;
	}

	@Override
	public boolean updateDirection() {

		float x = agent.getX();
		float y = agent.getY();

		if (activeHotSpotVersion != validHotSpotVersion || target == null) {

			activeHotSpotVersion = validHotSpotVersion;
			target = hotSpots.get(random.nextInt(hotSpots.size()));

			target.x += random.nextInt(40) - 20;
			target.y += random.nextInt(40) - 20;

			prevX = 0;
			prevY = 0;
		}

		if (target.x > x) {

			dX = 1;

		} else if (target.x < x) {

			dX = -1;

		} else {

			dX = 0;
		}

		if (target.y > y) {

			dY = 1;

		} else if (target.y < y) {

			dY = -1;

		} else {

			dY = 0;
		}

		float nX;
		float nY;
		int searchStart = 0;

		while (true) {

			nX = x + dX;
			nY = y + dY;

			if (dX == 0 && dY == 0) {

				// geldik balam
				return true;

			} else if (isInBounds(nX, nY)) {

				Tile tile = agent.cache.getTile(nX, nY);

				if (tile == null) {

					return false;

				} else if (tile.getAbsolute(nX, nY) == 0) {

					if (nX == prevX && nY == prevY) {

						searchDir *= -1;

					} else {

						prevX = x;
						prevY = y;

						return true;
					}
				}
			}

			searchStart = getNextDirection(dX, dY, searchStart);

			dX = (int) dirCW[searchStart].x;
			dY = (int) dirCW[searchStart].y;
		}
	}
}
