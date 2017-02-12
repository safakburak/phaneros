package p2p.common;

import java.util.Collection;
import java.util.HashMap;

import actionsim.Point;
import actionsim.core.Node;
import p2p.map.Region;
import p2p.map.Tile;
import p2p.renderer.IRenderable;
import p2p.timer.Timer;
import p2p.visibility.Visibility;
import p2p.visibility.VisibilityCell;

public abstract class AbstractAgent<T> implements IRenderable {

	protected float x;
	protected float y;

	protected Cache cache;

	protected Node node;

	protected Timer timer;

	protected Visibility visibility;

	protected HashMap<T, Point> knownAgents = new HashMap<T, Point>();

	protected VisibilityCell currentCell;

	public AbstractAgent(Node node, Visibility visibility, int cacheSize, MapServer server) {

		this.node = node;
		this.visibility = visibility;

		if (server == null) {

			this.cache = new LruCache(cacheSize, visibility.getCellSize());

		} else {

			this.cache = new NeverMissCache(server, cacheSize, visibility.getCellSize());
		}

		timer = new Timer();
	}

	public void fillCache(MapServer server) {

		for (VisibilityCell cell : getPvs()) {

			Region region = cell.getRegion();

			cache.addTile(server.getTile(region.getX(), region.getY()));
		}
	}

	public Collection<VisibilityCell> getPvs() {

		return visibility.getCellForPos((int) x, (int) y).getPvs();
	}

	public float getX() {

		return x;
	}

	public float getY() {

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

	public void setPosition(float x, float y) {

		this.x = x;
		this.y = y;
	}

	public abstract void start();

	public abstract void onCacheMiss(float x, float y);

	public abstract void onPositionChange();

	public abstract void requestPvs(VisibilityCell forCell, boolean b);
}
