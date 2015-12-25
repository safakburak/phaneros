package p2p._app.agent;

import java.util.Collection;
import java.util.Random;
import java.util.Set;

import actionsim.AbstractChordApplication;
import actionsim.AbstractNodeListener;
import actionsim.chord.ChordId;
import actionsim.chord.ChordNode;
import actionsim.chord.internal.AbstractMessage;
import actionsim.core.Action;
import actionsim.core.Message;
import actionsim.core.Node;
import actionsim.scribe.ScribeListener;
import actionsim.scribe.ScribeNode;
import p2p._app.map.Map;
import p2p._app.visibility.Visibility;
import p2p._app.visibility.VisibilityCell;

public class Agent {

	private int x;
	private int y;
	private int nX;
	private int nY;

	private Cache cache;
	private Visibility visibility;
	private ScribeNode scribeNode;
	private ChordNode chordNode;
	private Node node;
	private Node server;
	
	private Random random;
	
	public Agent(Node node, Visibility visibility, int cacheSize, Node server) {

		this.node = node;
		this.visibility = visibility;
		this.cache = new Cache(cacheSize, visibility.getCellSize());
		this.scribeNode = new ScribeNode(node);
		this.chordNode = scribeNode.getChordNode();
		this.server = server;
		
		random = new Random();
		
		node.addNodeListener(new AbstractNodeListener() {
			
			float time = 0;
			
			@Override
			public void onStep(Action[] completedActions, float deltaTime) {
				
				time += deltaTime;
				
				if(time >= 1000) {
				
					step();
					time -= 1000;
				}
			}
			
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

				
			}
		});
	}
	
	private void step() {

		Map map = cache.getPatch(nX, nY);
		
		if(map == null) {
			
			server.send(new Message(node, server, new PatchRequest(nX, nY)));
			
		} else {
			
			if(map.getHeightAtAbs(nX, nY) == 0) {
				
				x = nX;
				y = nY;
			} 
			
			do {
				
				nX = x + (random.nextInt(3) - 1);
				nY = y + (random.nextInt(3) - 1);
				
			} while(nX < 0 || nY < 0);
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
}
