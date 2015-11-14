package actionsim.chord;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;

import actionsim.chord.internal.AbstractMessage;
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

		if(message.getPayload() instanceof AbstractMessage) {
			
			AbstractMessage abstractMessage = (AbstractMessage) message.getPayload();
			
			Logger.log(me, Logger.TRACE);
			Logger.log(abstractMessage, Logger.TRACE);
			
			if(abstractMessage instanceof InternalMessage) {

				handleInternalMessage(abstractMessage);
			}
			else if(abstractMessage instanceof ChordMessage) {

				handleChordMessage((ChordMessage) abstractMessage);
			}
			else {
				
				throw new AssertionError("Unsupported message type!");
			}
		}
		else {
			
			throw new AssertionError("Unsupported message type!");
		}
	}
	
	private void handleChordMessage(ChordMessage chordMessage) {
		
		if(chordMessage.getTo().isIn(predecessor, me, true)) {
			
			application.onChordMessage(chordMessage);
		}
		else if(chordMessage.getTo().isIn(me, successor, true)) {
			
			if(application.beforeForward(chordMessage, successor)) {
				
				send(successor, chordMessage);
			}
		}
		else {

			ChordId nextNode = findCpf(chordMessage.getTo());
			
			if(application.beforeForward(chordMessage, nextNode)) {
				
				send(nextNode, chordMessage);
			}
		}
	}
	
	private void handleInternalMessage(AbstractMessage abstractMessage) {
		
		if(abstractMessage instanceof SuccessorQuery) {

			SuccessorQuery query = (SuccessorQuery) abstractMessage;

			if(me.equals(successor) || query.getKey().isIn(me, successor, true)) {
				
				send(query.getLastHop(), new SuccessorResponse(query.getKey(), successor));
			}
			else {
				
				waitingQueries.add(new MapEntry(query.getKey(), query.getLastHop()));
				
				ChordId nextNode = findCpf(query.getKey());
				send(nextNode, query);
			}
		}
		else if(abstractMessage instanceof SuccessorResponse) {
		
			SuccessorResponse response = (SuccessorResponse) abstractMessage;
			
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
					
					send(entry.value, new SuccessorResponse(response.getKey(), response.getSuccessor()));
					itr.remove();
				}
			}
		}
		else if(abstractMessage instanceof PredecessorQuery) {
			
			PredecessorQuery query = (PredecessorQuery) abstractMessage;
			
			if(predecessor == null) {
			
				predecessor = query.getLastHop();
			}
			
			send(query.getLastHop(), new PredecessorResponse(predecessor));
		}
		else if(abstractMessage instanceof PredecessorResponse) {
			
			PredecessorResponse response = (PredecessorResponse) abstractMessage; 
			
			if(response.getPredecessor().isIn(me, successor)) {

				setSuccessor(response.getPredecessor());
			}
		}
		else if(abstractMessage instanceof PredecessorNotification) {
			
			PredecessorNotification notification = (PredecessorNotification) abstractMessage;
			
			if (predecessor == null || notification.getLastHop().isIn(predecessor, me)) {
				
				setPredecessor(notification.getLastHop());
				
				// only handles the join of second node
				if(successor.equals(me)) {
					
					setSuccessor(predecessor);
				}
			}
		}
	}

	private void setSuccessor(ChordId successor) {
		
		this.successor = successor;
		fingers[0] = successor;
		
		if(me.equals(successor) == false) {
			
			send(successor, new PredecessorNotification());
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
			
			send(successor, new PredecessorQuery());
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
			
			send(successor, new SuccessorQuery(fingerKeys[fixFingerIndex]));
		}
	}
	
	// public interface
	
	public ChordId getId() {
		
		return me;
	}
	
	public void setApplication(ChordApplication application) {
		
		this.application = application;
	}
	
	public void send(ChordMessage chordMessage) {
		
		handleChordMessage(chordMessage);
	}
	
	public void send(ChordId to, AbstractMessage chordMessage) {
		
		chordMessage.addHop(me);
		
		node.send(new Message(node, node(to).node, chordMessage));
	}
	
	public void createNetwork() {
		
		predecessor = null;
		successor = me;
	}
	
	public void joinNetwork(ChordId seed) {
		
		predecessor = null;
		send(seed, new SuccessorQuery(me));
	}
	
	public void leaveNetwork() {
		
	}
	
	@Override
	public String toString() {
		
		return predecessor + " <- " + me + " -> " + successor;
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
	
	public ChordNode getSuccessor() {
		
		return node(successor);
	}
}
