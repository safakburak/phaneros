package actionsim.chord;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;

import actionsim.chord.internal.InternalMessage;
import actionsim.chord.internal.PredecessorNotification;
import actionsim.chord.internal.PredecessorQuery;
import actionsim.chord.internal.PredecessorResponse;
import actionsim.chord.internal.SuccessorQuery;
import actionsim.chord.internal.SuccessorResponse;
import actionsim.core.Action;
import actionsim.core.DefaultApplication;
import actionsim.core.Message;
import actionsim.core.Node;
import p2p.log.Logger;

public class ChordNode extends DefaultApplication {

	private static ConcurrentHashMap<ChordId, ChordNode> chordNodes = new ConcurrentHashMap<ChordId, ChordNode>();

	public static ChordNode node(ChordId id) {
		
		return chordNodes.get(id);
	}
	
	private Node node;
	
	private ChordApplication application;
	
	private ChordId me;
	private ChordId predecessor;
	private ChordId successor;
	
	private ChordId[] fingerKeys = new ChordId[ChordConfiguration.chordIdBits];
	private ChordId[] fingers = new ChordId[ChordConfiguration.chordIdBits];
	
	private float stabilizeTime = 0;
	private float fixFingersTime = 0;
	private int fixFingerIndex = 0;
	
	private HashMap<ChordId, Object> entries = new HashMap<ChordId, Object>();

	private ArrayList<MapEntry> waitingQueries = new ArrayList<MapEntry>();
	
	public ChordNode(Node node) {

		this.node = node;
		this.node.setApplication(this);
		
		me = new ChordId(node.getId());
		
		application = new DefaultChordApplication(me);
		
		chordNodes.put(me, this);
		
		for(int i = 0; i < fingerKeys.length; i++) {
			
			fingerKeys[i] = me.addPowerOfTwo(i);
		}
	}
	
	private ChordId findCpf(ChordId id) {
		
		for(int i = fingers.length - 1; i >= 0; i--) {
			
			if(fingers[i] != null && fingers[i].isIn(me, id, true)) {
				
				return fingers[i];
			}
		}
		
		return me;
	}
	
	@Override
	public void onMessage(Message message) {

		if(message.getPayload() instanceof ChordMessage) {
			
			ChordMessage chordMessage = (ChordMessage) message.getPayload();
			
			if(chordMessage.getTo().equals(me) == false) {
				
				handleForward(chordMessage);
			}
			else {
				
				if(chordMessage instanceof InternalMessage) {

					handleInternalMessage(chordMessage);
				}
				else {
					
					// not an internal chordMessage. 
					// should be handled by upper application.
					application.onChordMessage(chordMessage);
				}
			}
		}
	}
	
	private void handleForward(ChordMessage chordMessage) {
		
		if(chordMessage.getTo().isIn(predecessor, me, true)) {
			
			application.onChordMessage(chordMessage);
		}
		else if(chordMessage.getTo().isIn(me, successor, true)) {
			
			if(application.onForward(chordMessage, successor)) {
				
				send(successor, chordMessage);
			}
		}
		else {

			ChordId nextNode = findCpf(chordMessage.getTo());
			
			if(application.onForward(chordMessage, nextNode)) {
				
				send(nextNode, chordMessage);
			}
		}
	}
	
	private void handleInternalMessage(ChordMessage chordMessage) {
		
		if(chordMessage instanceof SuccessorQuery) {

			SuccessorQuery query = (SuccessorQuery) chordMessage;

			if (successor == null) {
				
			}
			else if(query.getKey().isIn(me, successor, true) || me.equals(successor)) {
				
				send(query.getFrom(), new SuccessorResponse(me, query.getFrom(), query.getKey(), successor));
			}
			else {
				
				waitingQueries.add(new MapEntry(query.getKey(), query.getFrom()));
				
				query.setFrom(me);
				query.setTo(findCpf(query.getKey()));
				send(query);
			}
		}
		else if(chordMessage instanceof SuccessorResponse) {
		
			SuccessorResponse response = (SuccessorResponse) chordMessage;
			
			// if successor of my id
			if (response.getKey().equals(me)) {
				
				setSuccessor(response.getSuccessor());
			}
			
			// if successor of any of the fingers
			for(int i = 0; i < fingerKeys.length; i++) {
				
				if(fingerKeys[i].equals(response.getKey())) {
					
					fingers[i] = response.getSuccessor();
				}
			}
			
			// return to waiting queries 
			Iterator<MapEntry> itr = waitingQueries.iterator();
			
			while(itr.hasNext()) {
				
				MapEntry entry = itr.next();
				
				if(entry.key.equals(response.getKey())) {
					
					send(entry.value, new SuccessorResponse(me, entry.value, entry.key, response.getSuccessor()));
					itr.remove();
				}
			}
		}
		else if(chordMessage instanceof PredecessorQuery) {
			
			PredecessorQuery query = (PredecessorQuery) chordMessage;
			
			if(predecessor != null) {
				
				send(query.getFrom(), new PredecessorResponse(me, query.getFrom(), predecessor));
			}
		}
		else if(chordMessage instanceof PredecessorResponse) {
			
			PredecessorResponse response = (PredecessorResponse) chordMessage; 
			
			if(response.getPredecessor().isIn(me, successor)) {

				setSuccessor(response.getPredecessor());
			}
		}
		else if(chordMessage instanceof PredecessorNotification) {
			
			PredecessorNotification notification = (PredecessorNotification) chordMessage;
			
			if (predecessor == null || notification.getFrom().isIn(predecessor, me)) {
				
				setPredecessor(notification.getFrom());
				
				// only handles the join of second node
				if(successor != null && successor.equals(me)) {
					
					setSuccessor(predecessor);
				}
			}
		}
	}

	private void setSuccessor(ChordId successor) {
		
		if(successor.equals(this.successor) == false) {
			
			this.successor = successor;
			fingers[0] = successor;
			
			if(me.equals(successor) == false) {
				
				send(new PredecessorNotification(me, successor));
			}
		}
	}
	
	private void setPredecessor(ChordId predecessor) {
		
		this.predecessor = predecessor;
	}
	
	@Override
	public void onStep(Action[] completedActions, float deltaTime) {
		
		stabilizeTime += deltaTime;
		
		if(stabilizeTime >= ChordConfiguration.stabilizePeriod) {
			
			stabilizeTime -= ChordConfiguration.stabilizePeriod;
			
			stabilize();
		}
		
		fixFingersTime += deltaTime;
		
		if(fixFingersTime >= ChordConfiguration.fixFingersPeriod) {
			
			fixFingersTime -= ChordConfiguration.fixFingersPeriod;
			
			fixFingers();
		}
		
	}
	
	
	private void stabilize() {
		
		if(successor != null && me.equals(successor) == false) {
			
			send(new PredecessorQuery(me, successor));
		}
	}
	
	private void fixFingers() {
		
		fixFingerIndex = (fixFingerIndex + 1) % ChordConfiguration.chordIdBits;
		
		//pass successor entry
		if(fixFingerIndex == 0) {
			
			fixFingerIndex++;
		}
		
		if(successor == null) {
			
		}
		else if(fingerKeys[fixFingerIndex].isIn(me, successor, true)) {
			
			fingers[fixFingerIndex] = successor;
		}
		else if(me.equals(successor) == false) {
			
			send(new SuccessorQuery(me, successor, fingerKeys[fixFingerIndex]));
		}
	}
	
	
	// public interface
	
	public ChordId getId() {
		
		return me;
	}
	
	public void setApplication(ChordApplication application) {
		
		this.application = application;
	}
	
	public void send(ChordId to, ChordMessage chordMessage) {
		
		chordMessage.hop(me);
		
		node.send(new Message(node, node(to).node, chordMessage));
	}
	
	public void send(ChordMessage chordMessage) {
		
		
		if (chordMessage.getTo().isIn(me, successor, true)) {
		
			send(successor, chordMessage);
		}
		else {
			
			ChordId nextNode = findCpf(chordMessage.getTo());
			send(nextNode, chordMessage);
		}
	}
	
	public void createNetwork() {
		
		predecessor = null;
		successor = me;
	}
	
	public void joinNetwork(ChordId seed) {
		
		predecessor = null;
		send(seed, new SuccessorQuery(me, seed, me));
	}
	
	public void leaveNetwork() {
		
	}
	
	@Override
	public String toString() {
		
		String result = "";
		result += "Node: " + me + "\n";
		result += "Succ: " + successor + "\n";
		result += "Pred: " + predecessor + "\n";
		
		return result;
	}
	
	
	
	// for debug
	public void report(int n) {

		if (n == 0) {
			
			return;
		}
		
		Logger.log(toString());
		
		if(successor != null) {
			
			node(successor).report(n - 1);
		}
	}
	
	public ArrayList<ChordNode> gather() {
		
		ArrayList<ChordNode> result = new ArrayList<ChordNode>();
		
		gather(result);
		
		return result;
	}
	
	private void gather(ArrayList<ChordNode> list) {
		
		if(successor == null) {
			
			Logger.log("Dead end!");
			Logger.log(this);
		}
		
		if(list.contains(this)) {
			
			return;
		}
		
		list.add(this);
		
		node(successor).gather(list);
	}
}
