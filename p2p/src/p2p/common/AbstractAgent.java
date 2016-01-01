package p2p.common;

import java.awt.Point;
import java.util.Collection;
import java.util.HashMap;

import actionsim.core.Node;
import p2p.map.Map;
import p2p.renderer.IRenderable;
import p2p.timer.Timer;
import p2p.visibility.Visibility;
import p2p.visibility.VisibilityCell;

public abstract class AbstractAgent implements IRenderable {
	
	protected int x;
	protected int y;
	
	protected Cache cache;
	
	protected Node node;
	
	protected Timer timer;
	
	protected Visibility visibility;
	
	protected HashMap<String, Point> agents = new HashMap<String, Point>();
	
	protected VisibilityCell currentCell; 
	
	public AbstractAgent(Node node, Visibility visibility, int cacheSize) {
		
		this.node = node;
		this.visibility = visibility;
		this.cache = new Cache(cacheSize, visibility.getCellSize());
		
		timer = new Timer(node);
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
	public Collection<Map> getPatches() {
		
		return cache.getPatches();
	}
	
	public Collection<Point> getAgents() {
		
		return agents.values();
	}
	
	public Cache getCache() {
		
		return cache;
	}
	
	public void setPosition(int x, int y) {
		
		this.x = x;
		this.y = y;
	}
	
	public abstract void start();
	
	public abstract void onCacheMissAt(int x, int y);
	
	public abstract void onPositionChange();
}
