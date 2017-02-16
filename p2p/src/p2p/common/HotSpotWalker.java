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

				List<Point> spots = new ArrayList<Point>();

				for (int i = 0; i < 4; i++) {

					spots.add(new Point(random.nextInt(1024), random.nextInt(1024)));
				}

				hotSpots = spots;

				validHotSpotVersion++;

				Simulation.instance.setHotSpots(hotSpots);
			}
		}, 60000, 0);
	}

	private int activeHotSpotVersion = 0;

	private Point target;

	private Random random = new Random();

	public HotSpotWalker(AbstractAgent<?> agent, int worldWidth, int worldHeight) {

		super(agent, worldWidth, worldHeight);
	}

	private Point getNextDirection(Point dir) {

		for (int i = 0; i < dirCW.length; i++) {

			if (dirCW[i].x == dir.x && dirCW[i].y == dir.y) {

				return dirCW[(i + 1) % dirCW.length];
			}
		}

		return null;
	}

	private void updateTarget() {

		if (activeHotSpotVersion != validHotSpotVersion || target == null) {

			activeHotSpotVersion = validHotSpotVersion;
			target = new Point(hotSpots.get(random.nextInt(hotSpots.size())));

			target.x += random.nextInt(40) - 20;
			target.y += random.nextInt(40) - 20;
		}
	}

	private Point getMoveTowardTarget() {

		Point result = new Point(0, 0);

		float x = agent.getX();
		float y = agent.getY();

		if (target.x > x) {

			result.x = 1;

		} else if (target.x < x) {

			result.x = -1;

		} else {

			result.x = 0;
		}

		if (target.y > y) {

			result.y = 1;

		} else if (target.y < y) {

			result.y = -1;

		} else {

			result.y = 0;
		}

		return result;
	}

	@Override
	public boolean updateDirection(Point move) {

		updateTarget();

		Point toTarget = getMoveTowardTarget();

		Boolean isMoveOk = checkMove(toTarget);

		if (isMoveOk == null) { // bilgi yok harita iste

			move.x = toTarget.x;
			move.y = toTarget.y;

			return false;

		} else if (isMoveOk == true) {

			move.x = toTarget.x;
			move.y = toTarget.y;

			return true;

		} else {

			Point dir = getNextDirection(toTarget);

			while (true) {

				isMoveOk = checkMove(dir);

				if (isMoveOk == null) {

					move.x = dir.x;
					move.y = dir.y;

					return false;

				} else if (isMoveOk == true) {

					move.x = dir.x;
					move.y = dir.y;

					return true;

				} else {

					dir = getNextDirection(dir);
				}
			}
		}
	}

	private Boolean checkMove(Point move) {

		float nX = agent.getX() + move.x;
		float nY = agent.getY() + move.y;

		if (move.x == 0 && move.y == 0) {

			// geldik balam
			return true;

		} else if (isInBounds(nX, nY)) {

			Tile tile = agent.cache.getTile(nX, nY);

			if (tile == null) {

				return null;

			} else if (tile.getAbsolute(nX, nY) == 0) {

				return true;

			} else {

				return false;
			}

		} else {

			return false;
		}
	}
}
