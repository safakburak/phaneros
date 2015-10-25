package actionsim.chord;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;

import actionsim.chord.internal.PredecessorNotification;
import actionsim.chord.internal.PredecessorQuery;
import actionsim.chord.internal.PredecessorResponse;
import actionsim.chord.internal.SuccessorQuery;
import actionsim.chord.internal.SuccessorResponse;
import actionsim.core.AbstractHelperNode;
import actionsim.logger.Logger;

public class ChordNode extends AbstractHelperNode {

	private static ConcurrentHashMap<ChordId, ChordNode> chordNodes = new ConcurrentHashMap<ChordId, ChordNode>();

	public static ChordNode node(ChordId id) {
		
		return chordNodes.get(id);
	}
	
	
	private ChordId me;
	private ChordId predecessor;
	private ChordId successor;
	
	private ChordId[] fingerKeys = new ChordId[ChordConfiguration.chordIdBits];
	private ChordId[] fingers = new ChordId[ChordConfiguration.chordIdBits];
	
	private float stabilizeTime = 0;
	private float fixFingersTime = 0;
	private int fixFingerIndex = 0;
	
//	private HashMap<ChordId, Object> entries = new HashMap<ChordId, Object>();

	private ArrayList<MapEntry> waitingQueries = new ArrayList<MapEntry>();
	
	public ChordNode(long serial) {
		
		super(serial);
		
		me = new ChordId("Node" + serial);
		
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
	protected void onMessage(Object envelopeMessage) {
	
		ChordEnvelope envelope = (ChordEnvelope) envelopeMessage;
		
		ChordMessage message = envelope.getPayload();
		
		if(message.getTarget().equals(me) == false) {
			
			//message is not for me, forward it 
			sendChordMessage(message);
		}
		else if(message instanceof SuccessorQuery) {

			SuccessorQuery query = (SuccessorQuery) message;

			if (successor == null) {
				
			}
			else if(query.getKey().isIn(me, successor, true) || me.equals(successor)) {
				
				sendChordMessage(query.getOrigin(), new SuccessorResponse(me, query.getOrigin(), query.getKey(), successor));
			}
			else {
				
				waitingQueries.add(new MapEntry(query.getKey(), query.getOrigin()));
				
				query.setOrigin(me);
				query.setTarget(findCpf(query.getKey()));
				sendChordMessage(query);
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
					
					sendChordMessage(entry.value, new SuccessorResponse(me, entry.value, entry.key, response.getSuccessor()));
					itr.remove();
				}
			}
		}
		else if(message instanceof PredecessorQuery) {
			
			PredecessorQuery query = (PredecessorQuery) message;
			
			if(predecessor != null) {
				
				sendChordMessage(query.getOrigin(), new PredecessorResponse(me, query.getOrigin(), predecessor));
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
			
			if (predecessor == null || notification.getOrigin().isIn(predecessor, me)) {
				
				setPredecessor(notification.getOrigin());
				
				// only handles the join of second node
				if(successor != null && successor.equals(me)) {
					
					setSuccessor(predecessor);
				}
			}
		}
		else {
			
			// not an internal message. 
			// should be handled by application.
			onChordMessage(message);
		}
	}

	private void setSuccessor(ChordId successor) {
		
		if(successor.equals(this.successor) == false) {
			
			this.successor = successor;
			fingers[0] = successor;
			
			if(me.equals(successor) == false) {
				
				sendChordMessage(new PredecessorNotification(me, successor));
			}
		}
	}
	
	private void setPredecessor(ChordId predecessor) {
		
		this.predecessor = predecessor;
	}
	
	@Override
	protected void update(float deltaTime) {
		
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
			
			sendChordMessage(new PredecessorQuery(me, successor));
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
			
			sendChordMessage(new SuccessorQuery(me, successor, fingerKeys[fixFingerIndex]));
		}
	}
	
	
	// public interface
	
	public ChordId getChordId() {
		
		return me;
	}
	
	public void sendChordMessage(ChordId to, ChordMessage message) {
		
		message.hop(me);
		
		ChordEnvelope envelope = new ChordEnvelope(me, to, message);
		
		send(envelope);
	}
	
	public void sendChordMessage(ChordMessage message) {
		
		message.hop(me);
		
		ChordEnvelope envelope = new ChordEnvelope(me, findCpf(message.getTarget()), message);
		
		send(envelope);
	}
	
	public static long totalHops = 0;
	
	public void onChordMessage(ChordMessage message) {

		totalHops += message.getHopCount();
	}
	
	public void setEntry(ChordId key, Object value) {
		
	}
	
	public void getEntry(ChordId key) {
		
	}
	
	public void createNetwork() {
		
		predecessor = null;
		successor = me;
	}
	
	public void joinNetwork(ChordId seed) {
		
		predecessor = null;
		sendChordMessage(seed, new SuccessorQuery(me, seed, me));
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
