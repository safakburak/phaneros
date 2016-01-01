package p2p.phaneros;

import java.awt.Point;
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
import p2p.timer.TimedAction;
import p2p.timer.Timer;
import p2p.visibility.Visibility;
import p2p.visibility.VisibilityCell;

public class Agent extends AbstractAgent {

	private ScribeNode scribeNode;
	private ChordNode chordNode;
	private Node server;
	
	private boolean isKeepOthers = false;
	
	private RandomWalker walker;
	
	public Agent(Node node, Visibility visibility, int cacheSize, Node server, int worldWidth, int worldHeight) {

		super(node, visibility, cacheSize);
		
		this.node = node;
		this.visibility = visibility;
		this.cache = new Cache(cacheSize, visibility.getCellSize());
		this.scribeNode = new ScribeNode(node);
		this.chordNode = scribeNode.getChordNode();
		this.server = server;
		
		walker = new RandomWalker(this, worldWidth, worldHeight);
		
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
				
				walker.walk();
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
	
	public ChordNode getChordNode() {
		return chordNode;
	}
	
	public void setKeepOthers(boolean isKeepOthers) {
		
		this.isKeepOthers = isKeepOthers;
	}
	
	@Override
	public void onCacheMissAt(int x, int y) {
		
		server.send(new Message(node, server, new PatchRequest(x, y)));
	}

	@Override
	public void onPositionChange() {

		scribeNode.publish(new ChordId(currentCell.getRegion().toString()), new Update(getId(), x, y));
	}
}
