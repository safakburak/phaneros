package actionsim.chord;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.concurrent.ConcurrentHashMap;

import actionsim.chord.internal.PredecessorNotification;
import actionsim.chord.internal.PredecessorQuery;
import actionsim.chord.internal.PredecessorResponse;
import actionsim.chord.internal.SuccessorQuery;
import actionsim.chord.internal.SuccessorResponse;
import actionsim.core.AbstractHelperNode;
import actionsim.core.Message;

public class ChordNode extends AbstractHelperNode {

	private static ConcurrentHashMap<ChordId, ChordNode> chordNodes = new ConcurrentHashMap<ChordId, ChordNode>();

	public static ChordNode node(ChordId id) {
		
		return chordNodes.get(id);
	}
	
	
	private ChordId me;
	
	private ChordId predecessor;
	
	private ChordId successor;
	
	private ChordId[] fingers = new ChordId[ChordConfiguration.chordIdBits];
	private ChordId[] fingerSuccessors = new ChordId[ChordConfiguration.chordIdBits];
	
	private LinkedList<SuccessorQuery> forwardedQueries = new LinkedList<SuccessorQuery>();

	
	private float stabilizeTime = 0;

	private float fixFingersTime = 0;
	
	
	private int fixFingerIndex = 0;
	
	
//	private HashMap<ChordId, Object> entries = new HashMap<ChordId, Object>();
	
	
	public ChordNode(long serial) {
		
		super(serial);
		
		me = new ChordId("Node" + serial);
		
		for(int i = 0; i < fingerSuccessors.length; i++) {
			
			fingers[i] = me.addPowerOfTwo(i);
		}
		
		chordNodes.put(me, this);
	}
	
	private ChordId closestPrecedingFinger(ChordId id) {
		
		ChordId right = me;
		
		for(int i = fingerSuccessors.length - 1; i >= 0; i--) {
			
			ChordId left = fingerSuccessors[i];
			
			if(left != null && right != null && id.isBetweenCw(left, right)) {
				
				return left;
			}
			else {
				
				right = left;
			}
		}
		
		return me;
	}
	
	private void join(ChordId seed) {
		
		if(seed == null) {

			setPredecessor(me);
			setSuccessor(me);
			
			for(int i = 0; i < fingerSuccessors.length; i++) {
				
				fingerSuccessors[i] = me;
			}
		}
		else {
			
			send(new SuccessorQuery(me, seed, me));
		}
	}
	
	@Override
	protected void onMessage(Object message) {
	
		if(message instanceof SuccessorQuery) {

			SuccessorQuery query = (SuccessorQuery) message;
			
			if (successor == null || successor.equals(me)) {
				
				send(new SuccessorResponse(me, ((SuccessorQuery) message).getSource(), query.getKey(), me));
			}
			else if(query.getKey().isBetweenCw(me, successor)) {
				
				send(new SuccessorResponse(me, ((SuccessorQuery) message).getSource(), query.getKey(), successor));
			}
			else {
				
				forwardedQueries.add(query);
				
				send(new SuccessorQuery(me, closestPrecedingFinger(query.getKey()), query.getKey()));
			}
		}
		else if(message instanceof SuccessorResponse) {
		
			SuccessorResponse response = (SuccessorResponse) message;
			
			// if successor of my id
			if (response.getKey().equals(me)) {
				
				setSuccessor(response.getSuccessor());
			}
			
			// if successor of any forwarded queries
			Iterator<SuccessorQuery> itr = forwardedQueries.iterator();
			
			while(itr.hasNext()) {
				
				SuccessorQuery query = itr.next();
				
				if (query.getKey().equals(response.getKey())) {
					
					send(new SuccessorResponse(me, query.getSource(), query.getKey(), response.getSuccessor()));
					
					itr.remove();
				} 
			}
			
			// if successor of any of the fingers
			for(int i = 0; i < fingerSuccessors.length; i++) {
				
				if(fingers[i].equals(response.getKey())) {
					
					fingerSuccessors[i] = response.getSuccessor();
				}
			}
		}
		else if(message instanceof PredecessorQuery) {
			
			PredecessorQuery query = (PredecessorQuery) message;
			
			if(predecessor != null) {
				
				send(new PredecessorResponse(me, query.getSource(), predecessor));
			}
		}
		else if(message instanceof PredecessorResponse) {
			
			PredecessorResponse response = (PredecessorResponse) message; 
			
			if(response.getPredecessor().isBetweenCw(me, successor)) {

				setSuccessor(response.getPredecessor());
			}
		}
		else if(message instanceof PredecessorNotification) {
			
			PredecessorNotification notification = (PredecessorNotification) message;
			
			if (predecessor == null || predecessor.equals(me) || notification.getSource().isBetweenCw(predecessor, me)) {
				
				setPredecessor(notification.getSource());
				
				// only handles the join of second node
				if(successor.equals(me)) {
					
					setSuccessor(predecessor);
				}
			}
		}
		else if(message instanceof ChordMessage) {
			
			ChordMessage chordMessage = (ChordMessage) message;
			
			if(chordMessage.getDestination().equals(me)) {
				
				onChordMessage(chordMessage);
			}
			else {

				send(new ChordMessage(me, closestPrecedingFinger(chordMessage.getDestination()), chordMessage.getPayload()));
			}
		}
	}

	private void setSuccessor(ChordId successor) {
		
		if(successor.equals(this.successor) == false) {
			
			this.successor = successor;
			fingerSuccessors[0] = successor;
			
			send(new PredecessorQuery(me, successor));
			send(new PredecessorNotification(me, successor));
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
		
		if(successor != null) {
			
			send(new PredecessorQuery(me, successor));
		}
	}
	
	private void fixFingers() {
		
		fixFingerIndex = (fixFingerIndex + 1) % ChordConfiguration.chordIdBits;
		
		//pass successor entry
		if(fixFingerIndex == 0) {
			
			fixFingerIndex++;
		}
		
		if (successor == null) {
			
			fingerSuccessors[fixFingerIndex] = me;
		}
		else if(fingers[fixFingerIndex].isBetweenCw(me, successor)) {
			
			fingerSuccessors[fixFingerIndex] = successor;
		}
		else {
			
			send(new SuccessorQuery(me, successor, fingers[fixFingerIndex]));
		}
	}
	
	
	// public interface
	
	public ChordId getChordId() {
		
		return me;
	}
	
	public void onChordMessage(ChordMessage message) {
		
		System.out.println("Chord message arrived!");
	}
	
	public void setEntry(ChordId key, Object value) {
		
	}
	
	public void getEntry(ChordId key) {
		
	}
	
	public void joinNetwork() {
		
		if(chordNodes.size() == 1) {
			
			join(null);
		}
		else {
			
			ChordId seed;

			do {
				
				int seedIndex = (int) (Math.random() * (chordNodes.size() - 1) + 0.5);
				seed = (ChordId) chordNodes.keySet().toArray()[seedIndex];
				
			} while(seed.equals(me));
			
			
			join(seed);
		}
	}
	
	public void leaveNetwork() {
		
	}
	
	@Override
	public String toString() {
		
		return me + "(" + getSerial() + ")";
	}
}
