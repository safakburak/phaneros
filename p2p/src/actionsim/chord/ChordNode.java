package actionsim.chord;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;

import actionsim.chord.internal.ChordEnvelope;
import actionsim.chord.internal.EntryQuery;
import actionsim.chord.internal.EntryResponse;
import actionsim.chord.internal.EntryUpdate;
import actionsim.chord.internal.PredecessorNotification;
import actionsim.chord.internal.PredecessorQuery;
import actionsim.chord.internal.PredecessorResponse;
import actionsim.chord.internal.SuccessorQuery;
import actionsim.chord.internal.SuccessorResponse;
import actionsim.core.Action;
import actionsim.core.DefaultApplication;
import actionsim.core.Node;
import actionsim.logger.Logger;

public class ChordNode extends DefaultApplication {

	private static ConcurrentHashMap<ChordId, ChordNode> chordNodes = new ConcurrentHashMap<ChordId, ChordNode>();

	public static ChordNode node(ChordId id) {
		
		return chordNodes.get(id);
	}
	
	private Node node;
	
	private ChordApplication application = new DefaultChordApplication();
	
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
		
		me = new ChordId("Node" + node.getSerial());
		
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
	public void onMessage(Object envelopeMessage) {
	
		ChordEnvelope envelope = (ChordEnvelope) envelopeMessage;
		
		ChordMessage message = envelope.getPayload();
		
		if(message.getTo().equals(me) == false) {
			
			ChordId nextHop = findCpf(message.getTo());
			
			if(application.onForward(message, nextHop)) {
				
				//message is not for me, forward it 
				sendMessage(nextHop, message);
			}
		}
		else if(message instanceof SuccessorQuery) {

			SuccessorQuery query = (SuccessorQuery) message;

			if (successor == null) {
				
			}
			else if(query.getKey().isIn(me, successor, true) || me.equals(successor)) {
				
				sendMessage(query.getFrom(), new SuccessorResponse(me, query.getFrom(), query.getKey(), successor));
			}
			else {
				
				waitingQueries.add(new MapEntry(query.getKey(), query.getFrom()));
				
				query.setOrigin(me);
				query.setTarget(findCpf(query.getKey()));
				sendMessage(query);
			}
		}
		else if(message instanceof SuccessorResponse) {
		
			SuccessorResponse response = (SuccessorResponse) message;
			
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
					
					sendMessage(entry.value, new SuccessorResponse(me, entry.value, entry.key, response.getSuccessor()));
					itr.remove();
				}
			}
		}
		else if(message instanceof PredecessorQuery) {
			
			PredecessorQuery query = (PredecessorQuery) message;
			
			if(predecessor != null) {
				
				sendMessage(query.getFrom(), new PredecessorResponse(me, query.getFrom(), predecessor));
			}
		}
		else if(message instanceof PredecessorResponse) {
			
			PredecessorResponse response = (PredecessorResponse) message; 
			
			if(response.getPredecessor().isIn(me, successor)) {

				setSuccessor(response.getPredecessor());
			}
		}
		else if(message instanceof PredecessorNotification) {
			
			PredecessorNotification notification = (PredecessorNotification) message;
			
			if (predecessor == null || notification.getFrom().isIn(predecessor, me)) {
				
				setPredecessor(notification.getFrom());
				
				// only handles the join of second node
				if(successor != null && successor.equals(me)) {
					
					setSuccessor(predecessor);
				}
			}
		}
		else if(message instanceof EntryUpdate) {
			
			EntryUpdate update = (EntryUpdate) message;
			
			ChordId cpf = findCpf(update.getKey());
			
			if(cpf.equals(me)) {
				
				entries.put(update.getKey(), update.getValue());
			}
			else {
				
				update.setTarget(cpf);
				sendMessage(cpf, update);
			}
		}
		else if(message instanceof EntryQuery) {
			
			EntryQuery query = (EntryQuery) message;
			
			ChordId cpf = findCpf(query.getKey());
			
			if(cpf.equals(me)) {
				
				sendMessage(new EntryResponse(me, query.getFrom(), query.getKey(), entries.get(query.getKey())));
			}
			else {
			
				query.setTarget(cpf);
				sendMessage(cpf, query);
			}
			
		}
		else if(message instanceof EntryResponse) {
			
			EntryResponse response = (EntryResponse) message;
			
			application.onEntryValue(response.getKey(), response.getValue());
		}
		else {
			
			// not an internal message. 
			// should be handled by upper application.
			application.onMessage(message);
		}
	}

	private void setSuccessor(ChordId successor) {
		
		if(successor.equals(this.successor) == false) {
			
			this.successor = successor;
			fingers[0] = successor;
			
			if(me.equals(successor) == false) {
				
				sendMessage(new PredecessorNotification(me, successor));
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
			
			sendMessage(new PredecessorQuery(me, successor));
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
			
			sendMessage(new SuccessorQuery(me, successor, fingerKeys[fixFingerIndex]));
		}
	}
	
	
	// public interface
	
	public ChordId getId() {
		
		return me;
	}
	
	public void sendMessage(ChordId to, ChordMessage message) {
		
		if(to.equals(me)) {
			
			application.onMessage(message);
		}
		else {
			
			message.hop(me);
			
			ChordEnvelope envelope = new ChordEnvelope(node, node(to).node, message);
			
			node.send(envelope);
		}
		
	}
	
	public void sendMessage(ChordMessage message) {
		
		sendMessage(findCpf(message.getTo()), message);
	}
	
	public void setEntry(ChordId key, Object value) {
		
		ChordId cpf = findCpf(key);
		
		if(cpf.equals(me)) {

			entries.put(key, value);
		}
		else {
			
			sendMessage(new EntryUpdate(me, cpf, key, value));
		}
	}
	
	public void requestEntry(ChordId key) {
		
		ChordId cpf = findCpf(key);
		
		if(cpf.equals(me)) {

			Object value = entries.get(key);
			application.onEntryValue(key, value);
		}
		else {
			
			sendMessage(new EntryQuery(me, cpf, key));
		}
	}
	
	public void createNetwork() {
		
		predecessor = null;
		successor = me;
	}
	
	public void joinNetwork(ChordId seed) {
		
		predecessor = null;
		sendMessage(seed, new SuccessorQuery(me, seed, me));
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
