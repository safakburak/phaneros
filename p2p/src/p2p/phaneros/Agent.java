package p2p.phaneros;

import java.awt.Point;
import java.util.Collection;
import java.util.HashMap;
import java.util.Random;
import java.util.Set;

import actionsim.AbstractChordApplication;
import actionsim.AbstractNodeListener;
import actionsim.chord.ChordId;
import actionsim.chord.ChordNode;
import actionsim.chord.internal.AbstractMessage;
import actionsim.core.Message;
import actionsim.core.Node;
import actionsim.scribe.ScribeListener;
import actionsim.scribe.ScribeNode;
import p2p.map.Map;
import p2p.renderer.IRenderable;
import p2p.timer.TimedAction;
import p2p.timer.Timer;
import p2p.visibility.Visibility;
import p2p.visibility.VisibilityCell;

public class Agent implements IRenderable {

	private int x;
	private int y;
	private int dX;
	private int dY;

	private Cache cache;
	private Visibility visibility;
	private ScribeNode scribeNode;
	private ChordNode chordNode;
	private Node node;
	private Node server;
	
	private Random random;
	private Timer timer;
	private VisibilityCell currentCell; 
	
	private boolean isKeepOthers = false;
	private HashMap<String, Point> agents = new HashMap<String, Point>();
	
	public Agent(Node node, Visibility visibility, int cacheSize, Node server) {

		this.node = node;
		this.visibility = visibility;
		this.cache = new Cache(cacheSize, visibility.getCellSize());
		this.scribeNode = new ScribeNode(node);
		this.chordNode = scribeNode.getChordNode();
		this.server = server;
		
		random = new Random();
		timer = new Timer(node);
	}
	
	public void start() {

		node.addNodeListener(new AbstractNodeListener() {
			
			@Override
			public void onMessage(Message message) {
				
				Object payload = message.getPayload();
				
				if(payload instanceof Map) {
					
					cache.addPatch((Map) payload);
				}
			}
		});
		
		chordNode.addApplication(new AbstractChordApplication() {
			
			@Override
			public void onChordMessage(AbstractMessage message) {
				
				
			}
		});
		
		scribeNode.addScribeListener(new ScribeListener() {
			
			@Override
			public void onScribeMessage(ChordId topic, Object message) {

				if(message instanceof Update) {
					
					Update update = (Update) message;

					if(isKeepOthers && update.getId().equals(getId()) == false) {
						
						agents.put(update.getId(), new Point(update.getX(), update.getY()));
					}
				}
			}
		});
		
		timer.repeat(new TimedAction() {
			
			@Override
			public void act(float time) {
				
				step();
				doSubscriptions();
			}
		}, 500);
	}
	
	private void doSubscriptions() {

		VisibilityCell newCell = visibility.getCellForPos(x, y);
		
		if(currentCell == null || currentCell != newCell) {

			if(currentCell != null) {
				
				Set<VisibilityCell> pvs = currentCell.getPvs();
				
				for(Object topic : scribeNode.getSubscriptions().toArray()) {
					
					if(pvs.contains(topic) == false) {
						
						scribeNode.unsubscribe((ChordId) topic);
					}
				}
			}

			for(VisibilityCell cell : newCell.getPvs()) {

				scribeNode.subscribe(new ChordId(cell.getRegion().toString()));
			}
			
			currentCell = newCell;
		}
	}
	
	private boolean isValid(int x, int y) {
		
		return x > 0 && y > 0 && x < 1024 && y < 1024;
	}
	
	private void step() {

		int nX = x + dX;
		int nY = y + dY;
		
		Map map = cache.getPatch(nX, nY);
		
		if(isValid(nX, nY) && map == null) {
			
			server.send(new Message(node, server, new PatchRequest(nX, nY)));
			
		} else {
			
			if((dX != 0 || dY != 0) && isValid(nX, nY) && map.getHeightAtAbs(nX, nY) == 0) {
				
				x = nX;
				y = nY;
				
				scribeNode.publish(new ChordId(currentCell.getRegion().toString()), new Update(getId(), x, y));
				
			} else {
				
				do {
					
					dX = random.nextInt(3) - 1;
					dY = random.nextInt(3) - 1;
					
					nX = x + dX;
					nY = y + dY;
					
				} while((dX == 0 && dY == 0) || isValid(nX, nY) == false);
			}
		}
	}
	
	public Cache getCache() {
		
		return cache;
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
	
	public ChordNode getChordNode() {
		return chordNode;
	}
	
	public Collection<Point> getAgents() {
		
		return agents.values();
	}
	
	public void setPosition(int x, int y) {
		
		this.x = x;
		this.y = y;
		
		dX = 0;
		dY = 0;
	}
	
	public void setKeepOthers(boolean isKeepOthers) {
		
		this.isKeepOthers = isKeepOthers;
	}
	
	@Override
	public Collection<Map> getPatches() {
		
		return cache.getPatches();
	}
}
