package p2p.common;

import java.awt.Point;
import java.util.Collection;
import java.util.HashMap;

import actionsim.core.Node;
import p2p.map.Region;
import p2p.map.Tile;
import p2p.renderer.IRenderable;
import p2p.timer.Timer;
import p2p.visibility.Visibility;
import p2p.visibility.VisibilityCell;

public abstract class AbstractAgent<T> implements IRenderable {

	protected int x;
	protected int y;

	protected Cache cache;

	protected Node node;

	protected Timer timer;

	protected Visibility visibility;

	protected HashMap<T, Point> knownAgents = new HashMap<T, Point>();

	protected VisibilityCell currentCell;

	public AbstractAgent(Node node, Visibility visibility, int cacheSize, MapServer server) {

		this.node = node;
		this.visibility = visibility;

		if (server != null) {

			this.cache = new NeverMissCache(server, cacheSize, visibility.getCellSize());

		} else {

			this.cache = new LruCache(cacheSize, visibility.getCellSize());
		}

		timer = new Timer(node);
	}

	public void fillCache(MapServer server) {

		for (VisibilityCell cell : getPvs()) {

			Region region = cell.getRegion();

			cache.addTile(server.getTile(region.getX(), region.getY()));
		}
	}

	public Collection<VisibilityCell> getPvs() {

		return visibility.getCellForPos(x, y).getPvs();
	}

	public int getX() {

		return x;
	}

	public int getY() {

		return y;
	}

	public String getId() {

		return node.getId();
	}

	@Override
	public Collection<Tile> getAvailableTiles() {

		return cache.getTiles();
	}

	public Collection<Point> getAgents() {

		return knownAgents.values();
	}

	@SuppressWarnings("rawtypes")
	@Override
	public boolean isKnown(AbstractAgent agent) {

		return knownAgents.containsKey(agent);
	}

	public Cache getCache() {

		return cache;
	}

	public void setPosition(int x, int y) {

		this.x = x;
		this.y = y;
	}

	public abstract void start();

	public abstract void onUrgentTileNeed(int x, int y);

	public abstract void onPositionChange();
}
