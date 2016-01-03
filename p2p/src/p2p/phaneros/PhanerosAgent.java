package p2p.phaneros;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import actionsim.AbstractNodeListener;
import actionsim.chord.ChordId;
import actionsim.chord.ChordNode;
import actionsim.core.Message;
import actionsim.core.Node;
import actionsim.scribe.ScribeListener;
import actionsim.scribe.ScribeNode;
import p2p.common.AbstractAgent;
import p2p.common.RandomWalker;
import p2p.common.messages.Update;
import p2p.map.Map;
import p2p.phaneros.messages.CellEnter;
import p2p.phaneros.messages.CellExit;
import p2p.phaneros.messages.PatchRequest;
import p2p.timer.TimedAction;
import p2p.visibility.Visibility;
import p2p.visibility.VisibilityCell;

public class PhanerosAgent extends AbstractAgent {

	private ScribeNode scribeNode;
	private ChordNode chordNode;
	private Node mapServer;
	private RandomWalker walker;
	
	private Multimap<VisibilityCell, Node> connections = HashMultimap.create();
	
	private List<VisibilityCell> subscriptions = Collections.synchronizedList(new ArrayList<VisibilityCell>());
	
	public PhanerosAgent(Node node, Visibility visibility, int cacheSize, Node mapServer, int worldWidth, int worldHeight) {

		super(node, visibility, cacheSize);
		
		this.scribeNode = new ScribeNode(node);
		this.chordNode = scribeNode.getChordNode();
		this.mapServer = mapServer;
		
		walker = new RandomWalker(this, worldWidth, worldHeight);
	}
	
	public void start() {

		timer.repeat(new TimedAction() {
			
			@Override
			public void act(float time) {
				
				walker.walk();
			}
		}, 500);
		
		node.addNodeListener(new AbstractNodeListener() {
			
			@Override
			public void onMessage(Message message) {
				
				Object payload = message.getPayload();
				
				if(payload instanceof Map) {
					
					cache.addPatch((Map) payload);
					
				} else if(payload instanceof Update) {
					
					Update update = (Update) payload;

					if(isKeepOthers && update.getNode() != node) {
						
						agents.put(update.getNode().getId(), new Point(update.getX(), update.getY()));
					}
					
				}
			}
		});
		
		scribeNode.addScribeListener(new ScribeListener() {
			
			@Override
			public void onScribeMessage(ChordId topic, Object message) {

				if(message instanceof CellEnter) {
					
					CellEnter cellEnter = (CellEnter) message;
					
					if(currentCell.getPvs().contains(cellEnter.getCell())) {
						
						connections.put(cellEnter.getCell(), cellEnter.getNode());
						agents.put(cellEnter.getNode().getId(), new Point(cellEnter.getX(), cellEnter.getY()));
						cellEnter.getNode().send(new Message(node, cellEnter.getNode(), new CellEnter(currentCell, node, x, y)));
					}
					
				} else if(message instanceof CellExit) {
					
					CellExit cellExit = (CellExit) message;
					
					connections.remove(cellExit.getCell(), cellExit.getNode());
					agents.remove(cellExit.getNode().getId());
				}
			}
		});
		
		onPositionChange();
	}
	
	public ChordNode getChordNode() {
		return chordNode;
	}

	@Override
	public void onCacheMissAt(int x, int y) {
		
		mapServer.send(new Message(node, mapServer, new PatchRequest(x, y)));
	}

	@Override
	public void onPositionChange() {

		for(Node node : connections.values().toArray(new Node[]{})) {
			
			node.send(new Message(this.node, node, new Update(this.node, x, y)));
		}
		
		VisibilityCell newCell = visibility.getCellForPos(x, y);
		
		if(currentCell == null || currentCell != newCell) {

			Set<VisibilityCell> newPvs = newCell.getPvs();
			
			synchronized (subscriptions) {
				
				Iterator<VisibilityCell> itr = subscriptions.iterator();
				
				while(itr.hasNext()) {
				
					VisibilityCell cell = itr.next();
					
					if(newPvs.contains(cell) == false) {
						
						scribeNode.unsubscribe(new ChordId(cell.toString()));
						
						for(Node node : connections.removeAll(cell)) {

							agents.remove(node.getId());
						}

						itr.remove();
					}
				}
			}

			for(VisibilityCell cell : newPvs) {

				if(subscriptions.contains(cell) == false) {
					
					scribeNode.subscribe(new ChordId(cell.getRegion().toString()));
					subscriptions.add(cell);
				}
			}
			
			if(currentCell != null) {
				
				scribeNode.publish(new ChordId(currentCell.getRegion().toString()), new CellExit(currentCell, node));
			}
			
			currentCell = newCell;
			
			scribeNode.publish(new ChordId(currentCell.getRegion().toString()), new CellEnter(currentCell, node, x, y));
		}
	}
}
